package com.mita.fmipaschedule.Interface;

import com.google.firebase.firestore.DocumentSnapshot;

public interface DatabaseInterface {
    void onSuccess(boolean isSuccess, String message, DocumentSnapshot data);
}
