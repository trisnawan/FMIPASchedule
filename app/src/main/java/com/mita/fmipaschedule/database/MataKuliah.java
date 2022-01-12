package com.mita.fmipaschedule.database;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mita.fmipaschedule.model.InterfaceModel;
import com.mita.fmipaschedule.model.MatkulModel;

import java.util.List;

public class MataKuliah {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String table = "matkul";

    public interface MataInterface{
        void response(InterfaceModel<MatkulModel> response);
    }

    public interface MataListInterface{
        void response(InterfaceModel<List<MatkulModel>> response);
    }

    public void save(MatkulModel model, MataInterface mataInterface){
        db.collection(table).document(model.getId()).set(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                mataInterface.response(new InterfaceModel<>(true, "Success!", model));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mataInterface.response(new InterfaceModel<>(false, e.getLocalizedMessage(), null));
            }
        });
    }

    public void gets(MataListInterface listInterface){
        db.collection(table).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshots) {
                listInterface.response(new InterfaceModel<>(true, "Success!", snapshots.toObjects(MatkulModel.class)));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listInterface.response(new InterfaceModel<>(false, e.getLocalizedMessage(), null));
            }
        });
    }

    public void get(String id, MataInterface mataInterface){
        db.collection(table).document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                mataInterface.response(new InterfaceModel<>(true, "Success!", documentSnapshot.toObject(MatkulModel.class)));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mataInterface.response(new InterfaceModel<>(false, e.getLocalizedMessage(), null));
            }
        });
    }
}
