package com.mita.fmipaschedule.ui.schedule;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.mita.fmipaschedule.Interface.ProgramInterface;
import com.mita.fmipaschedule.R;
import com.mita.fmipaschedule.SchedulePageActivity;
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
import com.mita.fmipaschedule.ui.dialog.DialogMataChooseFragment;
import com.mita.fmipaschedule.ui.dialog.DialogMataEditorFragment;
import com.mita.fmipaschedule.ui.dialog.DialogProgramFragment;

import java.util.List;

public class ScheduleEditorFragment extends Fragment implements MataKuliah.MataInterface {
    private View mainView;
    private ProgressDialog progressDialog;
    private EditText editProgram, editMatkul, editSemester, editSks;
    private RadioGroup editWp;
    private Button btnSave;
    private FakultasModel fakultasModel;
    private ProgramModel programModel;
    private MatkulModel matkulModel;
    private DaysModel daysModel;
    private String stringWp = "W";

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

        fakultasModel = new FakultasModel();
        programModel = new ProgramModel();
        matkulModel = new MatkulModel();
        daysModel = new DaysData().days().get(0);
        progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

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

        getFakultas();
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
                    scheduler.addDosen(dosenModel, new Scheduler.DosenInterface() {
                        @Override
                        public void onSuccess(List<DosenModel> listDosen) {
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
                            DialogHelper.alert(requireActivity(), getString(R.string.app_name), message);
                            Navigation.findNavController(mainView).popBackStack();
                            progressDialog.dismiss();
                            Intent i = new Intent(requireContext(), SchedulePageActivity.class);
                            i.putExtra("id", dosenModel.getScheduleId());
                            i.putExtra("title", list.get(0).getMatkul().getName());
                            startActivity(i);
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