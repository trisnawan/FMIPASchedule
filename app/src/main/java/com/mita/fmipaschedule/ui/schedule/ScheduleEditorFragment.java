package com.mita.fmipaschedule.ui.schedule;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.firebase.firestore.auth.User;
import com.mita.fmipaschedule.Interface.ListInterface;
import com.mita.fmipaschedule.Interface.ProgramInterface;
import com.mita.fmipaschedule.R;
import com.mita.fmipaschedule.SchedulePageActivity;
import com.mita.fmipaschedule.adapter.DosenAdapter;
import com.mita.fmipaschedule.app.AppHelper;
import com.mita.fmipaschedule.database.DaysData;
import com.mita.fmipaschedule.database.Fakultas;
import com.mita.fmipaschedule.database.MataKuliah;
import com.mita.fmipaschedule.database.Scheduler;
import com.mita.fmipaschedule.database.Users;
import com.mita.fmipaschedule.helper.DialogHelper;
import com.mita.fmipaschedule.model.DaysModel;
import com.mita.fmipaschedule.model.DosenModel;
import com.mita.fmipaschedule.model.FakultasModel;
import com.mita.fmipaschedule.model.InterfaceModel;
import com.mita.fmipaschedule.model.MatkulModel;
import com.mita.fmipaschedule.model.ProgramModel;
import com.mita.fmipaschedule.model.ScheduleModel;
import com.mita.fmipaschedule.ui.dialog.DialogDaysFragment;
import com.mita.fmipaschedule.ui.dialog.DialogDosenEditorFragment;
import com.mita.fmipaschedule.ui.dialog.DialogMataChooseFragment;
import com.mita.fmipaschedule.ui.dialog.DialogMataEditorFragment;
import com.mita.fmipaschedule.ui.dialog.DialogProgramFragment;

import java.util.ArrayList;
import java.util.List;

public class ScheduleEditorFragment extends Fragment implements MataKuliah.MataInterface {
    private View mainView;
    private ProgressDialog progressDialog;
    private EditText editProgram, editMatkul, editSemester, editSks;
    private RadioGroup editWp;
    private Button btnSave, btnDosen;
    private FakultasModel fakultasModel;
    private ProgramModel programModel;
    private MatkulModel matkulModel;
    private DaysModel daysModel;
    private String stringWp = "W";
    private RecyclerView recyclerView;
    private List<DosenModel> listDosen = new ArrayList<>();
    private DosenAdapter dosenAdapter;

    public ScheduleEditorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainView = view;
        editProgram = view.findViewById(R.id.program);
        editMatkul = view.findViewById(R.id.matkul);
        editSemester = view.findViewById(R.id.semester);
        editSks = view.findViewById(R.id.sks);
        editWp = view.findViewById(R.id.wp);
        btnSave = view.findViewById(R.id.btn_save);
        recyclerView = view.findViewById(R.id.recycler_view);
        btnDosen = view.findViewById(R.id.btn_dosen);

        Users users = new Users();

        // menambahkan dirinya sebagai dosen
        DosenModel dosenMy = new DosenModel();
        dosenMy.setNip(users.getReg(requireContext()));
        dosenMy.setName(users.getName());
        listDosen.add(dosenMy);

        dosenAdapter = new DosenAdapter(requireActivity(), listDosen);
        dosenAdapter.setListInterface(new ListInterface() {
            @Override
            public void onClick(View view, int position) {
                setDialog(position);
            }

            @Override
            public void onLongClick(View view, int position) {
                setDialog(position);
            }
        });
        fakultasModel = new FakultasModel();
        programModel = new ProgramModel();
        matkulModel = new MatkulModel();
        daysModel = new DaysData().days().get(0);
        progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(dosenAdapter);

        editProgram.setFocusable(false);
        editProgram.setOnClickListener(v -> getProgram());
        editMatkul.setFocusable(false);
        editMatkul.setOnClickListener(v -> getMata());
        editWp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.p) {
                    stringWp = "P";
                } else {
                    stringWp = "W";
                }
            }
        });
        btnSave.setOnClickListener(v -> {
            if (btnSave.isEnabled()){
                saveSchedule();
            }
        });
        btnDosen.setOnClickListener(v -> {
            DialogDosenEditorFragment editorFragment = new DialogDosenEditorFragment();
            editorFragment.setDialog(new DialogDosenEditorFragment.Dialog() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFinish(DosenModel dosenModel) {
                    listDosen.add(dosenModel);
                    dosenAdapter.notifyDataSetChanged();
                }
            });
            editorFragment.show(getParentFragmentManager(), null);
        });

        getFakultas();
    }

    private void setDialog(int position){
        final CharSequence[] dialogItem = {"Hapus Dosen", "Kembali"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
        dialog.setItems(dialogItem, new DialogInterface.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0:
                        listDosen.remove(position);
                        dosenAdapter.notifyDataSetChanged();
                        break;
                    default:
                        dialogInterface.dismiss();
                        break;
                }
            }
        });
        dialog.show();
    }

    private void getFakultas(){
        progressDialog.show();
        Fakultas fakultas = new Fakultas();
        fakultas.get(new Fakultas.FakultasInterface() {
            @Override
            public void response(InterfaceModel<FakultasModel> response) {
                progressDialog.dismiss();
                if (response.isSuccess()){
                    fakultasModel = response.getData();
                }else{
                    DialogHelper.toastShort(requireContext(), response.getMessage());
                    Navigation.findNavController(mainView).popBackStack();
                }
            }
        });
    }

    private void getProgram(){
        DialogProgramFragment programFragment = new DialogProgramFragment();
        programFragment.setProgramInterface(model -> {
            programModel = model;
            editProgram.setText(programModel.getName());
        });
        programFragment.show(getParentFragmentManager(), null);
    }

    private void getMata(){
        DialogMataChooseFragment chooseFragment = new DialogMataChooseFragment();
        chooseFragment.setMataInterface(this);
        chooseFragment.show(getParentFragmentManager(), null);
    }

    private void newMata(){
        DialogMataEditorFragment editorFragment = new DialogMataEditorFragment();
        editorFragment.setMataInterface(this);
        editorFragment.show(getParentFragmentManager(), null);
    }

    private void saveSchedule(){
        progressDialog.show();
        btnSave.setEnabled(false);
        if (programModel!=null && matkulModel!=null && daysModel!=null && editSemester.getText().length()>0 && editSks.getText().length()>0){
            ScheduleModel scheduleModel = new ScheduleModel();
            scheduleModel.setSemester(editSemester.getText().toString());
            scheduleModel.setWp(stringWp);
            scheduleModel.setMatkul(matkulModel);
            scheduleModel.setProgram(programModel);
            scheduleModel.setSks(Integer.parseInt(editSks.getText().toString()));
            scheduleModel.setDay(daysModel.getId());
            // PRIMARY
            scheduleModel.setFakultas(fakultasModel.getId());
            scheduleModel.setCode(matkulModel.getId());
            scheduleModel.setTimeLong(scheduleModel.getSks() * AppHelper.timePerSks());
            Scheduler scheduler = new Scheduler(requireContext(), fakultasModel);
            scheduler.set(scheduleModel).create(new Scheduler.SchedulerInterface() {
                @Override
                public void onSuccess(List<ScheduleModel> list) {
                    DosenModel dosenModel = new DosenModel();
                    dosenModel.setName(new Users().getName());
                    dosenModel.setNip(new Users().getReg(requireContext()));
                    dosenModel.setScheduleId(list.get(0).getId());
                    scheduler.addDosen(listDosen, list.get(0).getId(), new Scheduler.DosenInterface() {
                        @Override
                        public void onSuccess(List<DosenModel> dosens) {
                            DialogHelper.toastShort(requireContext(), "Berhasil!");
                            Navigation.findNavController(mainView).popBackStack();
                            progressDialog.dismiss();
                            Intent i = new Intent(requireContext(), SchedulePageActivity.class);
                            i.putExtra("id", dosenModel.getScheduleId());
                            i.putExtra("title", list.get(0).getMatkul().getName());
                            startActivity(i);
                        }

                        @Override
                        public void onFailure(String message) {

                        }
                    });
                }

                @Override
                public void onFailure(int code, String message) {
                    DaysData dd = new DaysData();
                    if (code==1 && dd.days().get(daysModel.getId()+1)!=null){
                        daysModel = dd.days().get(daysModel.getId()+1);
                        saveSchedule();
                    } else {
                        DialogHelper.alert(requireActivity(), getString(R.string.app_name), message);
                        progressDialog.dismiss();
                        btnSave.setEnabled(true);
                    }
                }
            });
        }else{
            DialogHelper.toastShort(requireContext(), getString(R.string.error_form_not_complete));
        }
    }

    @Override
    public void response(InterfaceModel<MatkulModel> response) {
        if (response.isSuccess()){
            matkulModel = response.getData();
            editMatkul.setText(matkulModel.getName());
        }else{
            if (response.getMessage().equals("NEW")){
                newMata();
            }else{
                DialogHelper.toastShort(requireContext(), response.getMessage());
            }
        }
    }
}