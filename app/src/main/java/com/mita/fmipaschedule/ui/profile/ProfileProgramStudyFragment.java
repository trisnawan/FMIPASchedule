package com.mita.fmipaschedule.ui.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;
import com.mita.fmipaschedule.Interface.DatabaseInterface;
import com.mita.fmipaschedule.Interface.ListInterface;
import com.mita.fmipaschedule.R;
import com.mita.fmipaschedule.adapter.ProgramAdapter;
import com.mita.fmipaschedule.app.SessionManager;
import com.mita.fmipaschedule.database.StudyProgram;
import com.mita.fmipaschedule.database.Users;
import com.mita.fmipaschedule.helper.DialogHelper;
import com.mita.fmipaschedule.model.ProgramModel;

import java.util.ArrayList;
import java.util.List;

public class ProfileProgramStudyFragment extends Fragment {
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private List<ProgramModel> list = new ArrayList<>();
    private ProgramAdapter programAdapter;
    private String bidangId = null;

    public ProfileProgramStudyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_program_study, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshLayout = view.findViewById(R.id.refresh_layout);
        recyclerView = view.findViewById(R.id.recycler_view);

        programAdapter = new ProgramAdapter(requireContext(), list);
        programAdapter.setListInterface(new ListInterface() {
            @Override
            public void onClick(View view, int position) {
                if (bidangId!=null){
                    saveProgram(list.get(position));
                }else{
                    bidangId = list.get(position).getId();
                    getData();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(programAdapter);

        refreshLayout.setOnRefreshListener(this::getData);
        getData();
    }

    private void saveProgram(ProgramModel model){
        refreshLayout.setRefreshing(true);
        StudyProgram program = new StudyProgram();
        program.saveProgram(model, new DatabaseInterface() {
            @Override
            public void onSuccess(boolean isSuccess, String message, DocumentSnapshot data) {
                if (isSuccess) {
                    new SessionManager(requireContext()).setProgram(model);
                }else{
                    DialogHelper.toastShort(requireContext(), message);
                }
                Navigation.findNavController(recyclerView).popBackStack();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getData(){
        list.clear();
        programAdapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(true);
        StudyProgram program = new StudyProgram();
        if (bidangId!=null) {
            program.getProgram(bidangId, new StudyProgram.Study() {
                @Override
                public void onSuccess(List<ProgramModel> l) {
                    list.addAll(l);
                    programAdapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailed(String message) {
                    DialogHelper.snackBar(refreshLayout, message);
                    refreshLayout.setRefreshing(false);
                }
            });
        }else{
            program.getBidang(new StudyProgram.Study() {
                @Override
                public void onSuccess(List<ProgramModel> l) {
                    list.addAll(l);
                    programAdapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailed(String message) {
                    DialogHelper.snackBar(refreshLayout, message);
                    refreshLayout.setRefreshing(false);
                }
            });
        }
    }
}