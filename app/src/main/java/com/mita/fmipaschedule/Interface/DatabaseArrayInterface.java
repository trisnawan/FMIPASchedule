package com.mita.fmipaschedule.Interface;

import com.google.firebase.firestore.QuerySnapshot;

public interface DatabaseArrayInterface {
    void onSuccess(boolean isSuccess, String message, QuerySnapshot data);
}
