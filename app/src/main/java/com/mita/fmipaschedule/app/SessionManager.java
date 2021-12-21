package com.mita.fmipaschedule.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.mita.fmipaschedule.model.ProgramModel;
import com.mita.fmipaschedule.model.UserModel;

public class SessionManager {
    private final Context context;
    private final SharedPreferences sharedpreferences;

    public SessionManager(Context context) {
        this.context = context;
        this.sharedpreferences = context.getSharedPreferences("mita-schedule-session", Context.MODE_PRIVATE);
    }

    public void logout(){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void setUser(UserModel user){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("user_id", user.getId());
        editor.putString("user_name", user.getName());
        editor.putString("user_reg", user.getReg());
        editor.putString("user_email", user.getEmail());
        editor.putLong("user_birthdate", user.getBirthdate());
        editor.putInt("user_type", user.getType());
        editor.apply();
        setProgram(user.getProgram());
    }

    public UserModel getUser(){
        UserModel userModel = new UserModel();
        userModel.setId(sharedpreferences.getString("user_id", null));
        userModel.setName(sharedpreferences.getString("user_name", null));
        userModel.setReg(sharedpreferences.getString("user_reg", null));
        userModel.setEmail(sharedpreferences.getString("user_email", null));
        userModel.setBirthdate(sharedpreferences.getLong("user_birthdate", 0));
        userModel.setType(sharedpreferences.getInt("user_type", 0));
        return userModel;
    }

    public int getUserType(){
        return sharedpreferences.getInt("user_type", 0);
    }

    public String getUserTypeString(){
        if (sharedpreferences.getInt("user_type", 0) == 1){
            return "Dosen";
        }else{
            return "Mahasiswa";
        }
    }

    public void setProgram(ProgramModel model){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if (model!=null) {
            editor.putString("program_id", model.getId());
            editor.putString("program_name", model.getName());
        }else{
            editor.putString("program_id", null);
            editor.putString("program_name", null);
        }
        editor.apply();
    }

    public ProgramModel getProgram(){
        ProgramModel model = new ProgramModel();
        model.setId(sharedpreferences.getString("program_id", null));
        model.setName(sharedpreferences.getString("program_name", "-"));
        return model;
    }

}
