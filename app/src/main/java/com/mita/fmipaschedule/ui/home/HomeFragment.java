package com.mita.fmipaschedule.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mita.fmipaschedule.Interface.ListInterface;
import com.mita.fmipaschedule.R;
import com.mita.fmipaschedule.adapter.ScheduleAdapter;
import com.mita.fmipaschedule.app.SessionManager;
import com.mita.fmipaschedule.database.Scheduler;
import com.mita.fmipaschedule.database.Users;
import com.mita.fmipaschedule.helper.DialogHelper;
import com.mita.fmipaschedule.model.FakultasModel;
import com.mita.fmipaschedule.model.ScheduleModel;
import com.mita.fmipaschedule.ui.dialog.DialogSemesterFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {
    private SwipeRefreshLayout refreshLayout;
    private TextView textName, textSemester;
    private RecyclerView recyclerSchedule;
    private RelativeLayout layoutSemester;
    private ScheduleAdapter scheduleAdapter;
    private SessionManager sessionManager;
    private List<ScheduleModel> schedules = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout = view.findViewById(R.id.refresh_layout);
        textName = view.findViewById(R.id.user_name);
        textSemester = view.findViewById(R.id.text_semester);
        layoutSemester = view.findViewById(R.id.btn_semester);
        recyclerSchedule = view.findViewById(R.id.schedule);

        sessionManager = new SessionManager(requireContext());
        scheduleAdapter = new ScheduleAdapter(requireContext(), schedules);
        scheduleAdapter.setListInterface(new ListInterface() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerSchedule.setLayoutManager(layoutManager);
        recyclerSchedule.setAdapter(scheduleAdapter);

        refreshLayout.setOnRefreshListener(this::getSchedules);
        layoutSemester.setOnClickListener(v->{
            DialogSemesterFragment dialogSemesterFragment = new DialogSemesterFragment();
            dialogSemesterFragment.setDialog(new DialogSemesterFragment.Dialog() {
                @Override
                public void onSuccess(String semester) {
                    sessionManager.setSemester(semester);
                    textSemester.setText("Semester "+sessionManager.getSemester());
                    getSchedules();
                }
            });
            dialogSemesterFragment.show(getParentFragmentManager(), null);
        });
        textSemester.setText("Semester "+sessionManager.getSemester());

        Users users = new Users();
        textName.setText(users.getName());
        getSchedules();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getSchedules(){
        refreshLayout.setRefreshing(true);
        Calendar calendar = Calendar.getInstance();
        Scheduler scheduler = new Scheduler(requireContext());
        scheduler.gets(calendar.get(Calendar.DAY_OF_WEEK)-1, sessionManager.getSemester(), new Scheduler.SchedulerInterface() {
            @Override
            public void onSuccess(List<ScheduleModel> list) {
                schedules.clear();
                schedules.addAll(list);
                scheduleAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int code, String message) {
                DialogHelper.toastShort(requireContext(), message);
                refreshLayout.setRefreshing(false);
            }
        });
    }
}