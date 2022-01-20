package com.mita.fmipaschedule.ui.dialog;

import android.app.Dialog;
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
import com.mita.fmipaschedule.model.DosenModel;

public class DialogDosenEditorFragment extends BottomSheetDialogFragment {
    private EditText editName, editNip;
    private Button btnSave;
    private Dialog dialog;

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public interface Dialog{
        void onFinish(DosenModel dosenModel);
    }

    public DialogDosenEditorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_dosen_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editName = view.findViewById(R.id.name);
        editNip = view.findViewById(R.id.nip);
        btnSave = view.findViewById(R.id.btn_save);

        btnSave.setOnClickListener(v ->{
            DosenModel dosenModel = new DosenModel();
            dosenModel.setNip(editNip.getText().toString());
            dosenModel.setName(editName.getText().toString());
            dialog.onFinish(dosenModel);
            dismiss();
        });
    }
}