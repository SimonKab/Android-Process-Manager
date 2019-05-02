package com.simonk.projects.taskmanager.repository;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.simonk.projects.taskmanager.entity.ProcessInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for process management. Works mainly with system's API
 *
 */
public class ProcessRepository {

    /**
     * @param context
     * @return all currently running processes
     */
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

    /**
     * @param context
     * @param packageName
     * @return return process info for corresponding package
     * or null if there is not application or application is not running
     */
    public ProcessInfo getRunningProcessInfoForPackage(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos
                = activityManager.getRunningAppProcesses();

        PackageManager packageManager = context.getPackageManager();

        for (ActivityManager.RunningAppProcessInfo info : runningAppProcessInfos) {
            if (info.processName.equals(packageName)) {
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
                    return null;
                }
                return processInfo;
            }
        }

        return null;
    }

    /**
     * Kill process.
     *
     * Important: there is not guarantee that process will be destroyed actually.
     * It only sends kill signals
     *
     * Since there is not root permissions on device foreground process will never be killed
     *
     * @param context
     * @param processInfo
     */
    public void killProcess(Context context, ProcessInfo processInfo) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        android.os.Process.killProcess(processInfo.getPid());
        android.os.Process.sendSignal(processInfo.getPid(), android.os.Process.SIGNAL_KILL);
        activityManager.killBackgroundProcesses(processInfo.getPpackage());
    }

    /**
     * Tries to change process priority
     *
     * Important: there is not guarantee that process will accept new priority.
     *
     * @param context
     * @param processInfo
     * @param priority
     */
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

    /**
     * Kill processes as much as possible
     *
     * Important: there is not guarantee that process will be destroyed actually.
     * It only sends kill signals
     *
     * Since there is not root permissions on device foreground process will never be killed
     *
     * @param context
     */
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
