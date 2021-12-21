package com.mita.fmipaschedule.database;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mita.fmipaschedule.model.FakultasModel;
import com.mita.fmipaschedule.model.InterfaceModel;

public class Fakultas {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String table = "fakultas";

    public interface FakultasInterface {
        void response(InterfaceModel<FakultasModel> response);
    }

    public String getId(){
        return "uPFIeaQBxkFfMtAmRdOr";
    }

    public void get(FakultasInterface fakultasInterface){
        db.collection(table).document(getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                fakultasInterface.response(new InterfaceModel<>(true, null, documentSnapshot.toObject(FakultasModel.class)));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                fakultasInterface.response(new InterfaceModel<>(false, e.getLocalizedMessage(), null));
            }
        });
    }
}
