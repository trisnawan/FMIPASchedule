package com.mita.fmipaschedule.database;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mita.fmipaschedule.R;
import com.mita.fmipaschedule.model.FakultasModel;
import com.mita.fmipaschedule.model.FakultasOpensModel;
import com.mita.fmipaschedule.model.ScheduleModel;

import java.util.List;

public class Scheduler {
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String tableMain = "schedule";
    private final FakultasModel fakultasModel;

    public Scheduler(Context context, FakultasModel fakultasModel){
        this.context = context;
        this.fakultasModel = fakultasModel;
    }

    public Saving set(ScheduleModel model){
        model.setFakultas(fakultasModel.getId());
        return new Saving(model);
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
            CollectionReference reference = db.collection(tableMain);
            Query query = reference.whereEqualTo("fakultas", fakultasModel.getId()).whereEqualTo("day", model.getDay());
            query.orderBy("timeStart", Query.Direction.ASCENDING);
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot snapshots) {
                    List<ScheduleModel> list = snapshots.toObjects(ScheduleModel.class);
                    ScheduleModel newModel = getSchedule(list, model);
                    if (newModel!=null){
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
        private ScheduleModel getSchedule(List<ScheduleModel> list, ScheduleModel model){
            if (list!=null){
                boolean lock = false;
                long forLast = fakultasModel.getOpens().get(0).getStart();
                for (FakultasOpensModel om : fakultasModel.getOpens()) {
                    if (!lock) {
                        for (ScheduleModel sm : list) {
                            if (!lock) {
                                if (forLast + 5 < sm.getTimeStart()) {
                                    if (sm.getTimeEnd() > forLast + model.getTimeLong()) {
                                        model.setTimeStart(forLast);
                                        model.setTimeEnd(forLast + model.getTimeLong());
                                        lock = true;
                                    } else {
                                        forLast = sm.getTimeEnd();
                                    }
                                } else {
                                    forLast = sm.getTimeEnd();
                                }
                            }
                        }
                        if (!lock && om.getEnd() >= forLast + model.getTimeLong()) {
                            model.setTimeStart(forLast);
                            model.setTimeEnd(forLast + model.getTimeLong());
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
            }
            return model;
        }
    }
}
