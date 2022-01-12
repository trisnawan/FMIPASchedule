package com.mita.fmipaschedule.ui.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mita.fmipaschedule.Interface.DatabaseInterface;
import com.mita.fmipaschedule.MainActivity;
import com.mita.fmipaschedule.R;
import com.mita.fmipaschedule.SplashScreenActivity;
import com.mita.fmipaschedule.app.NumberHelper;
import com.mita.fmipaschedule.database.MataKuliah;
import com.mita.fmipaschedule.database.Users;
import com.mita.fmipaschedule.helper.DialogHelper;
import com.mita.fmipaschedule.model.InterfaceModel;
import com.mita.fmipaschedule.model.MatkulModel;
import com.mita.fmipaschedule.ui.dialog.DialogMataChooseFragment;
import com.mita.fmipaschedule.ui.dialog.DialogMataEditorFragment;

public class ProfileFragment extends Fragment implements MataKuliah.MataInterface {
    private SwipeRefreshLayout refreshLayout;
    private ImageView userAvatar;
    private TextView userName, userEmail, userBirthDate, userMatkul, userProgram, userStatus, userReg, userRegTitle;
    private RelativeLayout btnMatkul, btnProgram, btnLogout;
    private Users users;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout = view.findViewById(R.id.refresh_layout);
        userAvatar = view.findViewById(R.id.avatar);
        userName = view.findViewById(R.id.name);
        userEmail = view.findViewById(R.id.email);
        userBirthDate = view.findViewById(R.id.birthdate);
        userMatkul = view.findViewById(R.id.matkul);
        userProgram = view.findViewById(R.id.program);
        userStatus = view.findViewById(R.id.status);
        userReg = view.findViewById(R.id.reg);
        userRegTitle = view.findViewById(R.id.reg_title);
        btnMatkul = view.findViewById(R.id.btn_matkul);
        btnProgram = view.findViewById(R.id.btn_program);
        btnLogout = view.findViewById(R.id.btn_logout);

        users = new Users();

        refreshLayout.setOnRefreshListener(this::loadProfile);
        btnMatkul.setOnClickListener(v -> {
            getMata();
        });
        btnProgram.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.navigation_profile_program);
        });
        btnLogout.setOnClickListener(v -> {
            users.logout(requireContext());
            startActivity(new Intent(requireContext(), SplashScreenActivity.class));
            requireActivity().finish();
        });
        loadProfile();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfile();
    }

    private void loadProfile(){
        refreshLayout.setRefreshing(true);
        userStatus.setText(users.getTypeString(requireContext()));
        Glide.with(requireContext()).load(users.getAvatar()).into(userAvatar);
        userName.setText(users.getName());
        userEmail.setText(users.getEmail());
        userBirthDate.setText(NumberHelper.dateFormat(users.getBirthDate(requireContext())));
        userMatkul.setText("...");
        userProgram.setText(users.getProgram(requireContext()).getName());
        userReg.setText(users.getReg(requireContext()));
        if (users.isDosen(requireContext())){
            userRegTitle.setText(requireContext().getString(R.string.user_nip));
            btnMatkul.setVisibility(View.VISIBLE);
        }else{
            userRegTitle.setText(requireContext().getString(R.string.user_nim));
            btnMatkul.setVisibility(View.GONE);
        }
        if (users.getMatkulId(requireContext())!=null) {
            MataKuliah mataKuliah = new MataKuliah();
            mataKuliah.get(users.getMatkulId(requireContext()), new MataKuliah.MataInterface() {
                @Override
                public void response(InterfaceModel<MatkulModel> response) {
                    if (response.isSuccess()) {
                        userMatkul.setText(response.getData().getName());
                    } else {
                        DialogHelper.toastShort(requireContext(), response.getMessage());
                    }
                    refreshLayout.setRefreshing(false);
                }
            });
        }else{
            userMatkul.setText("-");
            refreshLayout.setRefreshing(false);
        }
    }

    private void getMata(){
        DialogMataChooseFragment chooseFragment = new DialogMataChooseFragment();
        chooseFragment.setMataInterface(this);
        chooseFragment.show(getParentFragmentManager(), null);
    }

    private void newMata(){
        DialogMataEditorFragment editorFragment = new DialogMataEditorFragment();
        editorFragment.setMataInterface(this);
        editorFragment.show(getParentFragmentManager(), null);
    }

    @Override
    public void response(InterfaceModel<MatkulModel> response) {
        refreshLayout.setRefreshing(true);
        if (response.isSuccess()){
            Users users = new Users();
            users.setDosenMatkul(requireContext(), response.getData(), new DatabaseInterface() {
                @Override
                public void onSuccess(boolean isSuccess, String message, DocumentSnapshot data) {
                    if (!isSuccess){
                        DialogHelper.toastShort(requireContext(), message);
                    } else {
                        loadProfile();
                    }
                    refreshLayout.setRefreshing(false);
                }
            });
        }else{
            refreshLayout.setRefreshing(false);
            if (response.getMessage().equals("NEW")){
                newMata();
            }else{
                DialogHelper.toastShort(requireContext(), response.getMessage());
            }
        }
    }
}