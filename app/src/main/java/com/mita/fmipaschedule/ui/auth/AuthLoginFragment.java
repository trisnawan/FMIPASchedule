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

import com.mita.fmipaschedule.Interface.AuthInterface;
import com.mita.fmipaschedule.R;

public class AuthLoginFragment extends Fragment {
    private EditText userEmail, userPass;
    private Button btnRegister, btnLogin;
    private AuthInterface authInterface;

    public AuthLoginFragment() {
        // Required empty public constructor
    }

    public void setAuthInterface(AuthInterface authInterface) {
        this.authInterface = authInterface;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auth_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userEmail = view.findViewById(R.id.email);
        userPass = view.findViewById(R.id.password);
        btnLogin = view.findViewById(R.id.btn_login);
        btnRegister = view.findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> {
            authInterface.showRegister();
        });
        btnLogin.setOnClickListener(v -> {
            if (userEmail.getText().length()>0 && userPass.getText().length()>0){
                authInterface.onLogin(userEmail.getText().toString(), userPass.getText().toString());
            }else{
                authInterface.showMessage(getString(R.string.error_form_not_complete));
            }
        });
    }
}