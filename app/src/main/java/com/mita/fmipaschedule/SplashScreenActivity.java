package com.mita.fmipaschedule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mita.fmipaschedule.Interface.DatabaseInterface;
import com.mita.fmipaschedule.database.Users;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // Hide ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        // timer
        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser!=null){
                Users users = new Users();
                users.setDatabaseInterface(new DatabaseInterface() {
                    @Override
                    public void onSuccess(boolean isSuccess, String message, DocumentSnapshot data) {
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                        finish();
                    }
                });
                users.syncUser(getApplicationContext());
            }else{
                startActivity(new Intent(SplashScreenActivity.this, AuthActivity.class));
                finish();
            }
        }, 1000);
    }
}