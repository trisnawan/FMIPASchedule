package com.mita.fmipaschedule.Interface;

public interface AuthInterface {
    void showLogin();
    void showRegister();
    void showMessage(String message);
    void onLogin(String email, String password);
    void onRegister(int type, String name, String reg, String email, long birthdate, String password);
}
