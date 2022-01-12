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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.QuerySnapshot;
import com.mita.fmipaschedule.Interface.DatabaseArrayInterface;
import com.mita.fmipaschedule.adapter.DosenAdapter;
import com.mita.fmipaschedule.app.NumberHelper;
import com.mita.fmipaschedule.database.DaysData;
import com.mita.fmipaschedule.database.Scheduler;
import com.mita.fmipaschedule.database.Users;
import com.mita.fmipaschedule.helper.DialogHelper;
import com.mita.fmipaschedule.model.ScheduleModel;
import com.mita.fmipaschedule.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class SchedulePageActivity extends AppCompatActivity {
    private String id;
    private SwipeRefreshLayout refreshLayout;
    private LinearLayout mainLayout;
    private TextView textCode, textMatkul, textWp, textDay, textTime, textRoom;
    private Button btnDelete;
    private RecyclerView recyclerDosen;
    private ScheduleModel scheduleModel;
    private List<UserModel> userModels = new ArrayList<>();
    private DosenAdapter dosenAdapter;

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
        setContentView(R.layout.activity_schedule_page);
        refreshLayout = findViewById(R.id.refresh_layout);
        mainLayout = findViewById(R.id.main_layout);
        textCode = findViewById(R.id.code);
        textMatkul = findViewById(R.id.matkul);
        textWp = findViewById(R.id.wp);
        textDay = findViewById(R.id.day);
        textTime = findViewById(R.id.time);
        textRoom = findViewById(R.id.room);
        recyclerDosen = findViewById(R.id.dosens);
        btnDelete = findViewById(R.id.btn_delete);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(intent.getStringExtra("title"));
        }

        dosenAdapter = new DosenAdapter(getApplicationContext(), userModels);
        scheduleModel = new ScheduleModel();
        refreshLayout.setOnRefreshListener(this::getSchedule);
        btnDelete.setOnClickListener(v -> deleteSchedule());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerDosen.setLayoutManager(layoutManager);
        recyclerDosen.setAdapter(dosenAdapter);

        getSchedule();
    }

    private void getSchedule(){
        btnDelete.setVisibility(View.GONE);
        mainLayout.setVisibility(View.INVISIBLE);
        refreshLayout.setRefreshing(true);
        Scheduler scheduler = new Scheduler(getApplicationContext());
        scheduler.get(id, new Scheduler.SchedulerInterface() {
            @Override
            public void onSuccess(List<ScheduleModel> list) {
                scheduleModel = list.get(0);
                showSchedule();
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int code, String message) {
                refreshLayout.setRefreshing(false);
                DialogHelper.toastShort(getApplicationContext(), message);
                finish();
            }
        });
    }

    private void showSchedule(){
        if (scheduleModel!=null) {
            textCode.setText(scheduleModel.getCode());
            textRoom.setText(scheduleModel.getKelas());
            textTime.setText(NumberHelper.timeFormat(scheduleModel.getTimeStart(), scheduleModel.getTimeEnd(), " - "));
            textDay.setText(new DaysData().days().get(scheduleModel.getDay()).getName());
            textWp.setText(scheduleModel.getWp());
            textMatkul.setText(scheduleModel.getMatkul().getName());
            mainLayout.setVisibility(View.VISIBLE);
            Users users = new Users();

            if (users.isDosen(getApplicationContext())){
                btnDelete.setVisibility(View.VISIBLE);
            }else{
                btnDelete.setVisibility(View.GONE);
            }

            refreshLayout.setRefreshing(true);
            userModels.clear();
            users.getsDosen(scheduleModel.getMatkul().getId(), new DatabaseArrayInterface() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onSuccess(boolean isSuccess, String message, QuerySnapshot data) {
                    refreshLayout.setRefreshing(false);
                    if (isSuccess){
                        userModels.addAll(data.toObjects(UserModel.class));
                        dosenAdapter.notifyDataSetChanged();
                    } else {
                        DialogHelper.toastShort(getApplicationContext(), message);
                    }
                }
            });
        }else{
            DialogHelper.toastShort(getApplicationContext(), getString(R.string.error_default));
            finish();
        }
    }

    private void deleteSchedule(){
        Scheduler scheduler = new Scheduler(getApplicationContext());
        scheduler.delete(scheduleModel, new Scheduler.SchedulerInterface() {
            @Override
            public void onSuccess(List<ScheduleModel> list) {
                DialogHelper.toastShort(getApplicationContext(), "Berhasil!");
                finish();
            }

            @Override
            public void onFailure(int code, String message) {
                DialogHelper.toastShort(getApplicationContext(), message);
            }
        });
    }
}