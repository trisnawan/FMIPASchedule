package com.mita.fmipaschedule.ui.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.mita.fmipaschedule.Interface.AuthInterface;
import com.mita.fmipaschedule.R;

import java.util.Date;

public class AuthRegisterFragment extends Fragment {
    private TabLayout tabType;
    private TextInputLayout layReg;
    private EditText userName, userReg, userEmail, userBirth, userPass, userPassConf;
    private Button btnRegister, btnLogin;
    private AuthInterface authInterface;
    private int userType = 0;

    public AuthRegisterFragment() {
        // Required empty public constructor
    }

    public void setAuthInterface(AuthInterface authInterface) {
        this.authInterface = authInterface;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auth_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layReg = view.findViewById(R.id.lay_reg);
        tabType = view.findViewById(R.id.tab_status);
        userName = view.findViewById(R.id.name);
        userReg = view.findViewById(R.id.reg);
        userEmail = view.findViewById(R.id.email);
        userBirth = view.findViewById(R.id.birthdate);
        userPass = view.findViewById(R.id.password);
        userPassConf = view.findViewById(R.id.password_conf);
        btnRegister = view.findViewById(R.id.btn_register);
        btnLogin = view.findViewById(R.id.btn_login);

        tabType.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                userType = tab.getPosition();
                if (userType==1){
                    layReg.setHint(getString(R.string.user_nip));
                }else{
                    layReg.setHint(getString(R.string.user_nim));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        btnLogin.setOnClickListener(v -> {
            authInterface.showLogin();
        });
        btnRegister.setOnClickListener(v -> {
            Date date = new Date(userBirth.getText().toString());
            if (userName.getText().length()>0 && userReg.getText().length()>0 && userEmail.getText().length()>0 && userBirth.getText().length()>0 && userPass.getText().length()>0){
                if (userPass.getText().toString().equals(userPassConf.getText().toString())){
                    authInterface.onRegister(userType, userName.getText().toString(), userReg.getText().toString(), userEmail.getText().toString(), date.getTime(), userPass.getText().toString());
                }else{
                    authInterface.showMessage(getString(R.string.error_password_not_match));
                }
            }else{
                authInterface.showMessage(getString(R.string.error_form_not_complete));
            }
        });
    }
}