package com.mita.fmipaschedule.app;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private final Context context;
    private final SharedPreferences sharedpreferences;

    public SessionManager(Context context) {
        this.context = context;
        this.sharedpreferences = context.getSharedPreferences("mita-schedule-session", Context.MODE_PRIVATE);
    }

    public void setUserType(String type){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("user_type", type);
        editor.apply();
    }

    public String getUserType(){
        return sharedpreferences.getString("user_type", "customer");
    }

}
