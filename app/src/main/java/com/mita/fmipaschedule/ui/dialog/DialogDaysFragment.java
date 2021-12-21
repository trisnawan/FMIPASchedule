package com.mita.fmipaschedule.ui.dialog;

import android.annotation.SuppressLint;
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
import android.widget.ProgressBar;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mita.fmipaschedule.Interface.ListInterface;
import com.mita.fmipaschedule.R;
import com.mita.fmipaschedule.adapter.DaysAdapter;
import com.mita.fmipaschedule.database.DaysData;
import com.mita.fmipaschedule.helper.DialogHelper;
import com.mita.fmipaschedule.model.DaysModel;
import com.mita.fmipaschedule.model.InterfaceModel;

import java.util.ArrayList;
import java.util.List;

public class DialogDaysFragment extends BottomSheetDialogFragment {
    private ProgressBar progressBar;
    private final List<DaysModel> list = new ArrayList<>();
    private DaysAdapter adapter;
    private DaysData.DaysInterface daysInterface;

    public DialogDaysFragment() {
        // Required empty public constructor
    }

    public void setDaysInterface(DaysData.DaysInterface daysInterface) {
        this.daysInterface = daysInterface;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_days, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.progress_bar);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        adapter = new DaysAdapter(requireContext(), list);
        adapter.setListInterface(new ListInterface() {
            @Override
            public void onClick(View view, int position) {
                daysInterface.response(new InterfaceModel<>(true, null, list.get(position)));
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

        progressBar.setVisibility(View.VISIBLE);
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
                    dismiss();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}