package com.simonk.projects.taskmanager.ui.process;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.simonk.projects.taskmanager.R;

import java.util.List;

public class CleanedDialog extends DialogFragment {

    public interface OnCleaned {
        void onCleaned();
    }

    private OnCleaned mOnCleaned;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCleaned) {
            mOnCleaned = (OnCleaned) context;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle onSaveInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View root = layoutInflater.inflate(R.layout.cleaned_dialog, null);

        ActivityManager actManager = (ActivityManager) requireContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();

        actManager.getMemoryInfo(memInfo);
        double bToG = 1024 * 1024 * 1024;
        float beforeAvailMemory = ((int) (memInfo.availMem / bToG * 100)) / 100.0f;
        ((TextView)root.findViewById(R.id.before_memory)).setText("Before cleaning: " + beforeAvailMemory + "G");

        ActivityManager activityManager = (ActivityManager) requireContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos
                = activityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo info : runningAppProcessInfos) {
            if (!info.processName.contains("simonk")) {
                android.os.Process.killProcess(info.pid);
                android.os.Process.sendSignal(info.pid, android.os.Process.SIGNAL_KILL);
                activityManager.killBackgroundProcesses(info.processName);
            }
        }

        actManager.getMemoryInfo(memInfo);
        float afterAvailMemory = ((int) (memInfo.availMem / bToG * 100)) / 100.0f;
        ((TextView)root.findViewById(R.id.after_memory)).setText("After cleaning: " + afterAvailMemory + "G");

        ((TextView)root.findViewById(R.id.percent_memory)).setText((100 - ((int)(beforeAvailMemory * 100 / afterAvailMemory))) + "%");

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(root)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mOnCleaned != null) {
                            mOnCleaned.onCleaned();
                        }
                    }
                })
                .create();
        return dialog;
    }

}
