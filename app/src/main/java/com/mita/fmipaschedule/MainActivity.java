package com.mita.fmipaschedule;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mita.fmipaschedule.Interface.DatabaseInterface;
import com.mita.fmipaschedule.app.SessionManager;
import com.mita.fmipaschedule.database.MataKuliah;
import com.mita.fmipaschedule.database.Users;
import com.mita.fmipaschedule.helper.DialogHelper;
import com.mita.fmipaschedule.model.InterfaceModel;
import com.mita.fmipaschedule.model.MatkulModel;
import com.mita.fmipaschedule.ui.dialog.DialogMataChooseFragment;
import com.mita.fmipaschedule.ui.dialog.DialogMataEditorFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity implements MataKuliah.MataInterface {
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(getApplicationContext());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                switch (destination.getId()){
                    case R.id.navigation_home:
                    case R.id.navigation_dashboard:
                    case R.id.navigation_profile:
                        navView.setVisibility(View.VISIBLE);
                        break;
                    default:
                        navView.setVisibility(View.GONE);
                        break;
                }
            }
        });

        if (new Users().getProgram(getApplicationContext()).getId()==null){
            navController.navigate(R.id.navigation_profile_program);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.getUser().getMatkulId()==null){
            getMata();
        }
    }

    private void getMata(){
        DialogMataChooseFragment chooseFragment = new DialogMataChooseFragment();
        chooseFragment.setMataInterface(this);
        chooseFragment.show(getSupportFragmentManager(), null);
    }

    private void newMata(){
        DialogMataEditorFragment editorFragment = new DialogMataEditorFragment();
        editorFragment.setMataInterface(this);
        editorFragment.show(getSupportFragmentManager(), null);
    }

    @Override
    public void response(InterfaceModel<MatkulModel> response) {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        if (response.isSuccess()){
            Users users = new Users();
            users.setDosenMatkul(getApplicationContext(), response.getData(), new DatabaseInterface() {
                @Override
                public void onSuccess(boolean isSuccess, String message, DocumentSnapshot data) {
                    if (!isSuccess){
                        DialogHelper.toastShort(getApplicationContext(), message);
                    }
                    progressDialog.dismiss();
                }
            });
        }else{
            progressDialog.dismiss();
            if (response.getMessage().equals("NEW")){
                newMata();
            }else{
                DialogHelper.toastShort(getApplicationContext(), response.getMessage());
            }
        }
    }
}