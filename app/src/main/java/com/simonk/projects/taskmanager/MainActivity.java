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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
         {

    private ProcessAdapter processAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.activity_main_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        processAdapter = new ProcessAdapter();
        recyclerView.setAdapter(processAdapter);
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
                processInfo.ppackage = info.processName;
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }
            processInfoList.add(processInfo);
        }

        processAdapter.resolveActionChange(() -> {
            processAdapter.setItemsList(processInfoList);
        });
    }

    private static class ProcessAdapter extends RecyclerView.Adapter<ProcessAdapter.ProcessAdapterViewHolder> {

        @NonNull
        @Override
        public ProcessAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View v = inflater.inflate(R.layout.process_list_item, viewGroup, false);

            return new ProcessAdapterViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ProcessAdapterViewHolder processAdapterViewHolder, int i) {
            processAdapterViewHolder.bind(getItem(i));
        }

        public static class ProcessAdapterViewHolder extends RecyclerView.ViewHolder {

            private TextView mName;
            private TextView mPackage;

            private ProcessInfo mItem;

            public ProcessAdapterViewHolder(@NonNull View itemView) {
                super(itemView);
                mName = itemView.findViewById(R.id.process_list_item_text);
                mPackage = itemView.findViewById(R.id.process_list_item_package);

            }

            public void bind(ProcessInfo info) {
                mItem = info;
                mName.setText(info.text);
                mPackage.setText(info.ppackage);
            }
        }

    }

    public static class ProcessInfo implements Serializable {
        public String text;
        public String ppackage;
    }
}
