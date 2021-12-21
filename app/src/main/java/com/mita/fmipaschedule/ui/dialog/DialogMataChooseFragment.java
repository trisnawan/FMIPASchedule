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
import com.mita.fmipaschedule.adapter.MataAdapter;
import com.mita.fmipaschedule.database.MataKuliah;
import com.mita.fmipaschedule.helper.DialogHelper;
import com.mita.fmipaschedule.model.InterfaceModel;
import com.mita.fmipaschedule.model.MatkulModel;

import java.util.ArrayList;
import java.util.List;

public class DialogMataChooseFragment extends BottomSheetDialogFragment {
    private ProgressBar progressBar;
    private MataAdapter adapter;
    private final List<MatkulModel> list = new ArrayList<>();
    private MataKuliah.MataInterface mataInterface;

    public void setMataInterface(MataKuliah.MataInterface mataInterface) {
        this.mataInterface = mataInterface;
    }

    public DialogMataChooseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_mata_choose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.progress_bar);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        adapter = new MataAdapter(requireContext(), list);
        adapter.setListInterface(new ListInterface() {
            @Override
            public void onClick(View view, int position) {
                if (list.get(position).getId()!=null){
                    mataInterface.response(new InterfaceModel<>(true, null, list.get(position)));
                }else{
                    mataInterface.response(new InterfaceModel<>(false, "NEW", null));
                }
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
        getData();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getData(){
        progressBar.setVisibility(View.VISIBLE);
        MataKuliah mataKuliah = new MataKuliah();
        mataKuliah.gets(new MataKuliah.MataListInterface() {
            @Override
            public void response(InterfaceModel<List<MatkulModel>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccess()){
                    list.clear();
                    list.addAll(response.getData());
                    list.add(new MatkulModel(null, "-- Mata Kuliah Lain", "active"));
                    adapter.notifyDataSetChanged();
                }else{
                    DialogHelper.toastShort(requireContext(), response.getMessage());
                    dismiss();
                }
            }
        });
    }
}