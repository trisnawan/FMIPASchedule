package com.mita.fmipaschedule.database;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mita.fmipaschedule.Interface.DatabaseInterface;
import com.mita.fmipaschedule.model.ProgramModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudyProgram {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface Study {
        void onSuccess(List<ProgramModel> list);
        void onFailed(String message);
    }

    public void getProgram(String bidang, Study study){
        db.collection("program").whereEqualTo("bidang", bidang).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshots) {
                List<ProgramModel> li = new ArrayList<>();
                for (DocumentSnapshot doc : snapshots){
                    ProgramModel model = doc.toObject(ProgramModel.class);
                    if (model!=null){
                        model.setId(doc.getId());
                        li.add(model);
                    }
                }
                study.onSuccess(li);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                study.onFailed(e.getLocalizedMessage());
            }
        });
    }

    public void getBidang(Study study){
        db.collection("bidang").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshots) {
                List<ProgramModel> li = new ArrayList<>();
                for (DocumentSnapshot doc : snapshots){
                    ProgramModel model = doc.toObject(ProgramModel.class);
                    if (model!=null){
                        model.setId(doc.getId());
                        li.add(model);
                    }
                }
                study.onSuccess(li);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                study.onFailed(e.getLocalizedMessage());
            }
        });
    }

    public void saveProgram(ProgramModel programModel, DatabaseInterface databaseInterface){
        Map<String, Object> us = new HashMap<>();
        us.put("program", programModel);
        db.collection("users").document(new Users().getId()).update(us).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                databaseInterface.onSuccess(true, null, null);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                databaseInterface.onSuccess(false, e.getLocalizedMessage(), null);
            }
        });
    }

}
