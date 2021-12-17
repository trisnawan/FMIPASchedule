package com.mita.fmipaschedule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.mita.fmipaschedule.Interface.AuthInterface;
import com.mita.fmipaschedule.database.Users;
import com.mita.fmipaschedule.helper.DialogHelper;
import com.mita.fmipaschedule.model.UserModel;
import com.mita.fmipaschedule.ui.auth.AuthLoginFragment;
import com.mita.fmipaschedule.ui.auth.AuthRegisterFragment;

public class AuthActivity extends AppCompatActivity implements AuthInterface {
    private FragmentManager fragmentManager;
    private Users users;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        fragmentManager = getSupportFragmentManager();

        users = new Users();
        progressDialog = new ProgressDialog(AuthActivity.this);
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.please_wait));

        users.setDatabaseInterface((isSuccess, message, data) -> {
            progressDialog.dismiss();
            if (isSuccess){
                startActivity(new Intent(AuthActivity.this, SplashScreenActivity.class));
                finish();
            }else{
                DialogHelper.alert(AuthActivity.this, getString(R.string.app_name), message);
            }
        });
        showLogin();
    }

    @Override
    public void showLogin() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        AuthLoginFragment loginFragment = new AuthLoginFragment();
        loginFragment.setAuthInterface(this);
        transaction.replace(R.id.fragment_layout, loginFragment);
        transaction.commit();
    }

    @Override
    public void showRegister() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        AuthRegisterFragment registerFragment = new AuthRegisterFragment();
        registerFragment.setAuthInterface(this);
        transaction.replace(R.id.fragment_layout, registerFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void showMessage(String message) {
        DialogHelper.alert(this, getString(R.string.app_name), message);
    }

    @Override
    public void onLogin(String email, String password) {
        progressDialog.show();
        users.login(getApplicationContext(), email, password);
    }

    @Override
    public void onRegister(int type, String name, String reg, String email, long birthdate, String password) {
        progressDialog.show();
        UserModel userModel = new UserModel();
        userModel.setType(type);
        userModel.setName(name);
        userModel.setReg(reg);
        userModel.setEmail(email);
        userModel.setBirthdate(birthdate);
        users.register(getApplicationContext(), userModel, password);
    }
}