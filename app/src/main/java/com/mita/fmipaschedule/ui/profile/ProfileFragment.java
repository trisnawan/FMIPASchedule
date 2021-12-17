package com.mita.fmipaschedule.ui.profile;

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
import com.mita.fmipaschedule.R;
import com.mita.fmipaschedule.app.NumberHelper;
import com.mita.fmipaschedule.database.Users;

public class ProfileFragment extends Fragment {
    private SwipeRefreshLayout refreshLayout;
    private ImageView userAvatar;
    private TextView userName, userEmail, userBirthDate, userBidang, userProgram, userStatus;
    private RelativeLayout btnBidang, btnProgram;
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
        userBidang = view.findViewById(R.id.bidang);
        userProgram = view.findViewById(R.id.program);
        userStatus = view.findViewById(R.id.status);
        btnBidang = view.findViewById(R.id.btn_bidang);
        btnProgram = view.findViewById(R.id.btn_program);

        users = new Users();

        refreshLayout.setOnRefreshListener(this::loadProfile);
        btnBidang.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.navigation_profile_program);
        });
        btnProgram.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.navigation_profile_program);
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
        userBidang.setText("-");
        userProgram.setText(users.getProgram(requireContext()).getName());
        refreshLayout.setRefreshing(false);
    }
}