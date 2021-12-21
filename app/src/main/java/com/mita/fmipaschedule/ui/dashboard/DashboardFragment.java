package com.mita.fmipaschedule.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mita.fmipaschedule.R;
import com.mita.fmipaschedule.database.Users;

public class DashboardFragment extends Fragment {
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private Button btnNewSchedule;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout = view.findViewById(R.id.refresh_layout);
        recyclerView = view.findViewById(R.id.recycler_view);
        btnNewSchedule = view.findViewById(R.id.btn_new);

        Users users = new Users();
        if (users.isDosen(requireContext())){
            btnNewSchedule.setVisibility(View.VISIBLE);
        }else{
            btnNewSchedule.setVisibility(View.GONE);
        }

        btnNewSchedule.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.navigation_schedule_add);
        });
    }
}