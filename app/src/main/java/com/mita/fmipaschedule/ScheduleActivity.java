package com.mita.fmipaschedule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mita.fmipaschedule.Interface.ListInterface;
import com.mita.fmipaschedule.adapter.ScheduleAdapter;
import com.mita.fmipaschedule.database.Scheduler;
import com.mita.fmipaschedule.database.Users;
import com.mita.fmipaschedule.helper.DialogHelper;
import com.mita.fmipaschedule.model.ScheduleModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {
    private SwipeRefreshLayout refreshLayout;
    private ScheduleAdapter scheduleAdapter;
    private int day = 0;
    private final List<ScheduleModel> schedules = new ArrayList<>();

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) actionBar.setDisplayHomeAsUpEnabled(true);

        refreshLayout = findViewById(R.id.refresh_layout);
        RecyclerView recyclerSchedule = findViewById(R.id.recycler_view);

        Intent intent = getIntent();
        day = intent.getIntExtra("day", 0);
        String title = intent.getStringExtra("title");

        if (actionBar!=null && title!=null) actionBar.setTitle(title);

        scheduleAdapter = new ScheduleAdapter(getApplicationContext(), schedules);
        scheduleAdapter.setListInterface(new ListInterface() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerSchedule.setLayoutManager(layoutManager);
        recyclerSchedule.setAdapter(scheduleAdapter);

        refreshLayout.setOnRefreshListener(this::getSchedules);
        getSchedules();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getSchedules(){
        refreshLayout.setRefreshing(true);
        Scheduler scheduler = new Scheduler(getApplicationContext());
        scheduler.gets(day, new Scheduler.SchedulerInterface() {
            @Override
            public void onSuccess(List<ScheduleModel> list) {
                schedules.clear();
                schedules.addAll(list);
                scheduleAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int code, String message) {
                DialogHelper.toastShort(getApplicationContext(), message);
                refreshLayout.setRefreshing(false);
            }
        });
    }
}