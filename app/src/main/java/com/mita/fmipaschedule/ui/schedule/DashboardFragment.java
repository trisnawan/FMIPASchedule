package com.mita.fmipaschedule.ui.schedule;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mita.fmipaschedule.Interface.ListInterface;
import com.mita.fmipaschedule.R;
import com.mita.fmipaschedule.ScheduleActivity;
import com.mita.fmipaschedule.adapter.DaysAdapter;
import com.mita.fmipaschedule.database.DaysData;
import com.mita.fmipaschedule.database.Users;
import com.mita.fmipaschedule.helper.DialogHelper;
import com.mita.fmipaschedule.model.DaysModel;
import com.mita.fmipaschedule.model.InterfaceModel;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private Button btnNewSchedule;
    private final List<DaysModel> list = new ArrayList<>();
    private DaysAdapter adapter;

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

        adapter = new DaysAdapter(requireContext(), list);
        adapter.setListInterface(new ListInterface() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(requireContext(), ScheduleActivity.class);
                intent.putExtra("day", list.get(position).getId());
                intent.putExtra("title", list.get(position).getName());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);

        btnNewSchedule.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.navigation_schedule_add);
        });

        getData();
    }

    private void getData(){
        refreshLayout.setRefreshing(true);
        DaysData data = new DaysData();
        data.gets(new DaysData.DaysListInterface() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void response(InterfaceModel<List<DaysModel>> response) {
                list.clear();
                if (response.isSuccess()){
                    list.addAll(response.getData());
                    adapter.notifyDataSetChanged();
                }else{
                    DialogHelper.toastShort(requireContext(), response.getMessage());
                }
                refreshLayout.setRefreshing(false);
            }
        });

    }
}