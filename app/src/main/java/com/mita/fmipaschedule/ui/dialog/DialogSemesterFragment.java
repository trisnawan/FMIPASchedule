package com.mita.fmipaschedule.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mita.fmipaschedule.Interface.ListInterface;
import com.mita.fmipaschedule.R;
import com.mita.fmipaschedule.adapter.SemesterAdapter;
import com.mita.fmipaschedule.model.SemesterModel;

import java.util.ArrayList;
import java.util.List;

public class DialogSemesterFragment extends BottomSheetDialogFragment {
    private RecyclerView recyclerView;
    private List<SemesterModel> list = new ArrayList<>();
    private SemesterAdapter adapter;
    private Dialog dialog;

    public interface Dialog{
        void onSuccess(String semester);
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public DialogSemesterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_semester, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view);

        list.add(new SemesterModel("1", "Semester 1"));
        list.add(new SemesterModel("2", "Semester 2"));
        list.add(new SemesterModel("3", "Semester 3"));
        list.add(new SemesterModel("4", "Semester 4"));
        list.add(new SemesterModel("5", "Semester 5"));
        list.add(new SemesterModel("6", "Semester 6"));
        list.add(new SemesterModel("7", "Semester 7"));
        list.add(new SemesterModel("8", "Semester 8"));

        adapter = new SemesterAdapter(requireContext(), list);
        adapter.setListInterface(new ListInterface() {
            @Override
            public void onClick(View view, int position) {
                dialog.onSuccess(list.get(position).getId());
                dismiss();
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
    }
}