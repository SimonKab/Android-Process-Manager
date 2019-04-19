package com.simonk.projects.taskmanager.repository;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.simonk.projects.taskmanager.entity.ProcessInfo;

import java.util.ArrayList;
import java.util.List;

public class ProcessRepository {

    public List<ProcessInfo> getAllProcessInfo(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos
                = activityManager.getRunningAppProcesses();

        PackageManager packageManager = context.getPackageManager();

        List<ProcessInfo> processInfoList = new ArrayList<>();
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcessInfos) {
            ProcessInfo processInfo = new ProcessInfo();
            try {
                ApplicationInfo applicationInfo =
                        packageManager.getApplicationInfo(info.processName, 0);
                processInfo.setText(packageManager.getApplicationLabel(applicationInfo).toString());
                processInfo.setImage(packageManager.getApplicationIcon(applicationInfo));
                processInfo.setPpackage(info.processName);
                processInfo.setPriority(info.importance);
                processInfo.setStatus(applicationInfo.enabled);
                processInfo.setMinSdk(applicationInfo.targetSdkVersion);
                processInfo.setUid(applicationInfo.uid);
                processInfo.setDescription(applicationInfo.descriptionRes != 0
                        ? context.getResources().getString(applicationInfo.descriptionRes)
                        : "");
                processInfo.setPid(info.pid);
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }
            processInfoList.add(processInfo);
        }

        return processInfoList;
    }

    public void killProcess(Context context, ProcessInfo processInfo) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        android.os.Process.killProcess(processInfo.getPid());
        android.os.Process.sendSignal(processInfo.getPid(), android.os.Process.SIGNAL_KILL);
        activityManager.killBackgroundProcesses(processInfo.getPpackage());
    }

    public void changeProcessPriority(Context context, ProcessInfo processInfo, int priority) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos
                = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfos) {
            if (runningAppProcessInfo.processName.equals(processInfo.getPpackage())) {
                runningAppProcessInfo.importance = priority;
            }
        }
    }

    public void killAllProcesses(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos
                = activityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo info : runningAppProcessInfos) {
            if (!info.processName.contains("simonk")) {
                android.os.Process.killProcess(info.pid);
                android.os.Process.sendSignal(info.pid, android.os.Process.SIGNAL_KILL);
                activityManager.killBackgroundProcesses(info.processName);
            }
        }
    }
}
