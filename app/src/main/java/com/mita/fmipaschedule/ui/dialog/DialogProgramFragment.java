package com.mita.fmipaschedule.ui.dialog;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mita.fmipaschedule.Interface.ListInterface;
import com.mita.fmipaschedule.Interface.ProgramInterface;
import com.mita.fmipaschedule.R;
import com.mita.fmipaschedule.adapter.ProgramAdapter;
import com.mita.fmipaschedule.database.StudyProgram;
import com.mita.fmipaschedule.helper.DialogHelper;
import com.mita.fmipaschedule.model.ProgramModel;

import java.util.ArrayList;
import java.util.List;

public class DialogProgramFragment extends BottomSheetDialogFragment {
    private ProgressBar progressBar;
    private final List<ProgramModel> list = new ArrayList<>();
    private ProgramAdapter programAdapter;
    private String bid = null;
    private ProgramInterface programInterface;

    public DialogProgramFragment() {
        // Required empty public constructor
    }

    public void setProgramInterface(ProgramInterface programInterface) {
        this.programInterface = programInterface;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_program, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.progress_bar);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        programAdapter = new ProgramAdapter(requireContext(), list);
        programAdapter.setListInterface(new ListInterface() {
            @Override
            public void onClick(View view, int position) {
                if (bid!=null){
                    programInterface.response(list.get(position));
                    dismiss();
                }else{
                    bid = list.get(position).getId();
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
        getData();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getData(){
        list.clear();
        programAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.VISIBLE);
        StudyProgram program = new StudyProgram();
        if (bid!=null) {
            program.getProgram(bid, new StudyProgram.Study() {
                @Override
                public void onSuccess(List<ProgramModel> l) {
                    list.addAll(l);
                    programAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailed(String message) {
                    DialogHelper.toastShort(requireContext(), message);
                    progressBar.setVisibility(View.GONE);
                }
            });
        }else{
            program.getBidang(new StudyProgram.Study() {
                @Override
                public void onSuccess(List<ProgramModel> l) {
                    list.addAll(l);
                    programAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailed(String message) {
                    DialogHelper.toastShort(requireContext(), message);
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
}