package com.mita.fmipaschedule.ui.dialog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mita.fmipaschedule.R;
import com.mita.fmipaschedule.database.MataKuliah;
import com.mita.fmipaschedule.helper.DialogHelper;
import com.mita.fmipaschedule.model.InterfaceModel;
import com.mita.fmipaschedule.model.MatkulModel;

public class DialogMataEditorFragment extends BottomSheetDialogFragment {
    private EditText editCode, editName;
    private Button btnSave;
    private MataKuliah.MataInterface mataInterface;

    public void setMataInterface(MataKuliah.MataInterface mataInterface) {
        this.mataInterface = mataInterface;
    }

    public DialogMataEditorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_mata_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editCode = view.findViewById(R.id.code);
        editName = view.findViewById(R.id.name);
        btnSave = view.findViewById(R.id.btn_save);

        btnSave.setOnClickListener(v -> {
            if (btnSave.isEnabled()){
                saveData();
            }
        });
    }

    private void saveData(){
        if (editCode.getText().length()>0 && editName.getText().length()>0) {
            btnSave.setEnabled(false);
            btnSave.setText(getString(R.string.loading));
            MataKuliah mataKuliah = new MataKuliah();
            mataKuliah.save(new MatkulModel(editCode.getText().toString(), editName.getText().toString(), "active"), new MataKuliah.MataInterface() {
                @Override
                public void response(InterfaceModel<MatkulModel> response) {
                    mataInterface.response(response);
                    dismiss();
                }
            });
        }else{
            DialogHelper.toastShort(requireContext(), getString(R.string.error_form_not_complete));
            btnSave.setEnabled(true);
            btnSave.setText(getString(R.string.save));
        }
    }
}