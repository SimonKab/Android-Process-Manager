package com.simonk.projects.taskmanager.ui.process;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.simonk.projects.taskmanager.R;
import com.simonk.projects.taskmanager.entity.ProcessInfo;
import com.simonk.projects.taskmanager.ui.MainActivity;

public class ChangeDetailsDialog extends DialogFragment {

    public static final String PROCESS_INFO_ARG = "PROCESS_INFO_ARG";

    public interface OnChanged {
        void onChanged(ProcessInfo info, int priority);
    }

    private OnChanged mOnChanged;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChanged) {
            mOnChanged = (OnChanged) context;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle onSaveInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View root = layoutInflater.inflate(R.layout.change_details_dialog, null);

        ProcessInfo processInfo =
                (ProcessInfo)getArguments().getSerializable(PROCESS_INFO_ARG);

        EditText editText = root.findViewById(R.id.details_priority);
        editText.setText(""+processInfo.getPriority());

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(root)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mOnChanged != null) {
                            String p = editText.getText().toString();
                            int ip = Integer.parseInt(p);
                            mOnChanged.onChanged(processInfo, ip);
                        }
                    }
                })
                .create();
        return dialog;
    }


}
