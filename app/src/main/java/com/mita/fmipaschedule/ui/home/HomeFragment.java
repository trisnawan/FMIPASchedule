package com.mita.fmipaschedule.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mita.fmipaschedule.R;
import com.mita.fmipaschedule.database.Users;

public class HomeFragment extends Fragment {
    private TextView textName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textName = view.findViewById(R.id.user_name);

        Users users = new Users();
        textName.setText(users.getName());
    }
}