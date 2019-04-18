package com.simonk.projects.taskmanager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simonk.projects.taskmanager.databinding.ActivityMainBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BindingActivity
        implements CleanedDialog.OnCleaned, ChangeDetailsDialog.OnChanged {

    private ProcessAdapter processAdapter;

    @Override
    public ActivityMainBinding getBinding() {
        return (ActivityMainBinding) super.getBinding();
    }

    @Override
    public ViewDataBinding initBinding() {
        return DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_main, null, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.activity_main_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        processAdapter = new ProcessAdapter();
        recyclerView.setAdapter(processAdapter);

        findViewById(R.id.clear_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CleanedDialog cleanedDialog = new CleanedDialog();
                cleanedDialog.show(getSupportFragmentManager(), "CleanedDialog");
            }
        });

        updateUi();
    }

    private void updateUi() {
        ActivityManager actManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        double bToG = 1024 * 1024 * 1024;
        float allMemory = ((int) (memInfo.totalMem / bToG * 100)) / 100.0f;
        float availMemory = ((int) (memInfo.availMem / bToG * 100)) / 100.0f;
        ((TextView)findViewById(R.id.all_memory)).setText("" + allMemory + "G");
        ((TextView)findViewById(R.id.free_memory)).setText("" + availMemory + "G");
        ((TextView)findViewById(R.id.percent_memory)).setText("" + (int)(availMemory * 100 / allMemory) + "%");

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos
                = activityManager.getRunningAppProcesses();

        PackageManager packageManager = getPackageManager();

        List<ProcessInfo> processInfoList = new ArrayList<>();
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcessInfos) {
            ProcessInfo processInfo = new ProcessInfo();
            try {
                ApplicationInfo applicationInfo =
                        packageManager.getApplicationInfo(info.processName, 0);
                processInfo.text = packageManager.getApplicationLabel(applicationInfo).toString();
                processInfo.image = packageManager.getApplicationIcon(applicationInfo);
                processInfo.ppackage = info.processName;
                processInfo.priority = info.importance;
                processInfo.status = applicationInfo.enabled;
                processInfo.minSdk = applicationInfo.targetSdkVersion;
                processInfo.uid = applicationInfo.uid;
                processInfo.description = applicationInfo.descriptionRes != 0
                        ? getResources().getString(applicationInfo.descriptionRes)
                        : "";
                processInfo.pid = info.pid;
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }
            processInfoList.add(processInfo);
        }

        processAdapter.resolveActionChange(() -> {
            processAdapter.setItemsList(processInfoList);
        });

        processAdapter.setItemClickListener(new ProcessAdapter.ProcessAdapterViewHolder.OnClickListener() {
            @Override
            public void onClick(View v, ProcessInfo info) {
                ViewGroup detailsView = (ViewGroup)
                        LayoutInflater.from(MainActivity.this).inflate(R.layout.process_list_item_details, null);
                if (((FrameLayout) v.findViewById(R.id.process_list_item_details)).getChildCount() == 0) {
                    ((TextView) detailsView.findViewById(R.id.priority)).setText("Priority: " + info.priority);
                    ((TextView) detailsView.findViewById(R.id.status)).setText("Enabled: " + info.status);
                    ((TextView) detailsView.findViewById(R.id.uid)).setText("Uid: " + info.uid);
                    ((TextView) detailsView.findViewById(R.id.min_sdk)).setText("Target sdk: " + info.minSdk);
                    ((TextView) detailsView.findViewById(R.id.description)).setText("Description: " + info.description);
                    ((FrameLayout) v.findViewById(R.id.process_list_item_details)).addView(detailsView);
                    ((Button) detailsView.findViewById(R.id.kill)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            android.os.Process.killProcess(info.pid);
                            android.os.Process.sendSignal(info.pid, android.os.Process.SIGNAL_KILL);
                            activityManager.killBackgroundProcesses(info.ppackage);
                            updateUi();
                        }
                    });
                } else {
                    ((FrameLayout) v.findViewById(R.id.process_list_item_details)).removeAllViews();
                }
            }

            @Override
            public boolean onLongClick(ProcessInfo info) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(ChangeDetailsDialog.PROCESS_INFO_ARG, info);
                ChangeDetailsDialog changeDetailsDialog = new ChangeDetailsDialog();
                changeDetailsDialog.setArguments(bundle);
                changeDetailsDialog.show(getSupportFragmentManager(), "ChangeDetailsDialog");
                return false;
            }
        });
    }

    @Override
    public void onCleaned() {
        updateUi();
    }

    @Override
    public void onChanged(ProcessInfo info, int priority) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos
                = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfos) {
            if (runningAppProcessInfo.processName.equals(info.ppackage)) {
                runningAppProcessInfo.importance = priority;
            }
        }

        updateUi();
    }

    private static class ProcessAdapter extends ObjectListAdapter<ProcessInfo, ProcessAdapter.ProcessAdapterViewHolder> {

        private ProcessAdapterViewHolder.OnClickListener mItemClickListener;

        @NonNull
        @Override
        public ProcessAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View v = inflater.inflate(R.layout.process_list_item, viewGroup, false);

            return new ProcessAdapterViewHolder(v, mItemClickListener);
        }

        @Override
        public void onBindViewHolder(@NonNull ProcessAdapterViewHolder processAdapterViewHolder, int i) {
            processAdapterViewHolder.bind(getItem(i));
        }

        public void setItemClickListener(ProcessAdapterViewHolder.OnClickListener onClickListener) {
            mItemClickListener = onClickListener;
        }

        public static class ProcessAdapterViewHolder extends RecyclerView.ViewHolder {

            private TextView mName;
            private ImageView mImage;
            private TextView mPackage;

            private ProcessInfo mItem;

            public interface OnClickListener {
                void onClick(View v, ProcessInfo ppackage);
                boolean onLongClick(ProcessInfo info);
            }

            public ProcessAdapterViewHolder(@NonNull View itemView, OnClickListener onClickListener) {
                super(itemView);
                mName = itemView.findViewById(R.id.process_list_item_text);
                mImage = itemView.findViewById(R.id.process_list_item_logo);
                mPackage = itemView.findViewById(R.id.process_list_item_package);
                itemView.setOnClickListener(v -> {
                    if (onClickListener != null) {
                        onClickListener.onClick(v, mItem);
                    }
                });
                itemView.setOnLongClickListener(v -> {
                    if (onClickListener != null) {
                        return onClickListener.onLongClick(mItem);
                    }
                    return false;
                });
            }

            public void bind(ProcessInfo info) {
                mItem = info;
                mName.setText(info.text);
                mImage.setImageDrawable(info.image);
                mPackage.setText(info.ppackage);
            }
        }

    }

    public static class ProcessInfo implements Serializable {
        public String text;
        public Drawable image;
        public String ppackage;
        public int priority;
        public boolean status;
        public int minSdk;
        public int uid;
        public String description;
        public int pid;
    }
}
