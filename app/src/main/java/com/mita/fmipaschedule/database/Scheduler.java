package com.mita.fmipaschedule.database;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mita.fmipaschedule.R;
import com.mita.fmipaschedule.model.FakultasModel;
import com.mita.fmipaschedule.model.FakultasOpensModel;
import com.mita.fmipaschedule.model.KelasModel;
import com.mita.fmipaschedule.model.ScheduleModel;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String tableMain = "schedule";
    private final FakultasModel fakultasModel;

    public Scheduler(Context context){
        this.context = context;
        FakultasModel fm = new FakultasModel();
        fm.setId(new Fakultas().getId());
        this.fakultasModel = fm;
    }

    public Scheduler(Context context, FakultasModel fakultasModel){
        this.context = context;
        this.fakultasModel = fakultasModel;
    }

    public void gets(int day, String semester, SchedulerInterface schedulerInterface){
        Users users = new Users();
        CollectionReference reference = db.collection(tableMain);
        Query query;
        if (users.isDosen(context)){
            query = reference.whereEqualTo("fakultas", fakultasModel.getId()).whereEqualTo("day", day).whereEqualTo("semester", semester);
        }else{
            query = reference.whereEqualTo("fakultas", fakultasModel.getId()).whereEqualTo("day", day).whereEqualTo("program", users.getProgram(context)).whereEqualTo("semester", semester);
        }
        query.orderBy("timeStart", Query.Direction.ASCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshots) {
                List<ScheduleModel> list = new ArrayList<>();
                for (DocumentSnapshot doc : snapshots){
                    ScheduleModel model = doc.toObject(ScheduleModel.class);
                    if (model!=null){
                        model.setId(doc.getId());
                        list.add(model);
                    }
                }
                schedulerInterface.onSuccess(list);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                schedulerInterface.onFailure(0, e.getLocalizedMessage());
            }
        });
    }

    public Saving set(ScheduleModel model){
        model.setFakultas(fakultasModel.getId());
        return new Saving(model);
    }

    public void delete(ScheduleModel model, SchedulerInterface schedulerInterface){
        db.collection(tableMain).document(model.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                schedulerInterface.onSuccess(null);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                schedulerInterface.onFailure(0, e.getLocalizedMessage());
            }
        });
    }

    public void get(String id, SchedulerInterface schedulerInterface){
        db.collection(tableMain).document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<ScheduleModel> list = new ArrayList<>();
                ScheduleModel model = documentSnapshot.toObject(ScheduleModel.class);
                if (model!=null) {
                    model.setId(documentSnapshot.getId());
                    list.add(model);
                }
                schedulerInterface.onSuccess(list);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                schedulerInterface.onFailure(0, e.getLocalizedMessage());
            }
        });
    }

    public interface SchedulerInterface {
        void onSuccess(List<ScheduleModel> list);
        void onFailure(int code, String message);
    }

    public class Saving {
        private final ScheduleModel model;
        public Saving(ScheduleModel model){
            this.model = model;
        }
        public void create(SchedulerInterface schedulerInterface){
            // GET ALL SCHEDULE
            CollectionReference reference = db.collection(tableMain);
            Query query = reference.whereEqualTo("fakultas", fakultasModel.getId()).whereEqualTo("day", model.getDay());
            query.orderBy("timeStart", Query.Direction.ASCENDING);
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot snapshots) {
                    List<ScheduleModel> list = snapshots.toObjects(ScheduleModel.class);
                    // GET ALL KELAS
                    db.collection("kelas").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot snapshots) {
                            List<KelasModel> kelasModelList = snapshots.toObjects(KelasModel.class);
                            ScheduleModel newModel = getSchedule(list, kelasModelList, model);
                            if (newModel!=null && newModel.getDay()!=0 && newModel.getDay()!=6){
                                db.collection(tableMain).add(newModel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        schedulerInterface.onSuccess(null);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        schedulerInterface.onFailure(2, e.getLocalizedMessage());
                                    }
                                });
                            }else{
                                schedulerInterface.onFailure(1, context.getString(R.string.schedule_time_not_found));
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            schedulerInterface.onFailure(0, e.getLocalizedMessage());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    schedulerInterface.onFailure(0, e.getLocalizedMessage());
                }
            });
        }
        private ScheduleModel getSchedule(List<ScheduleModel> list, List<KelasModel> kelas, ScheduleModel model){
            if (list!=null){
                boolean lock = false;
                long forLast = fakultasModel.getOpens().get(0).getStart();
                // FOREACH JAM BUKA KAMPUS
                for (FakultasOpensModel om : fakultasModel.getOpens()) {
                    if (!lock) {
                        // FOREACH JADWAL YANG TERSEDIA
                        for (ScheduleModel sm : list) {
                            if (!lock) {
                                if (forLast + 5 < sm.getTimeStart() && sm.getTimeEnd() > forLast + model.getTimeLong() && !sm.getSemester().equals(model.getSemester())) {
                                    // FOREACH KELAS YANG TERSEDIA
                                    for (KelasModel kl : kelas) {
                                        if (!lock) {
                                            if (!kl.getName().equals(sm.getKelas())) {
                                                model.setTimeStart(forLast);
                                                model.setTimeEnd(forLast + model.getTimeLong());
                                                model.setKelas(kl.getName());
                                                lock = true;
                                            } else {
                                                forLast = sm.getTimeEnd();
                                            }
                                        }
                                    }
                                } else {
                                    // FOREACH KELAS YANG TERSEDIA
                                    for (KelasModel kl : kelas) {
                                        if (!lock) {
                                            if (!kl.getName().equals(sm.getKelas())) {
                                                model.setTimeStart(forLast);
                                                model.setTimeEnd(forLast + model.getTimeLong());
                                                model.setKelas(kl.getName());
                                                lock = true;
                                            } else {
                                                forLast = sm.getTimeEnd();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (!lock && om.getEnd() >= forLast + model.getTimeLong()) {
                            model.setTimeStart(forLast);
                            model.setTimeEnd(forLast + model.getTimeLong());
                            model.setKelas(kelas.get(0).getName());
                            lock = true;
                        }
                    }
                }
                if (!lock){
                    model = null;
                }
            }else{
                model.setTimeStart(fakultasModel.getOpens().get(0).getStart());
                model.setTimeEnd(model.getTimeStart()+model.getTimeLong());
                model.setKelas(kelas.get(0).getName());
            }
            return model;
        }
    }
}
