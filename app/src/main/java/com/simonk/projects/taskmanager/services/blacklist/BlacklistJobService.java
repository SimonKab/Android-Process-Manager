package com.simonk.projects.taskmanager.services.blacklist;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.simonk.projects.taskmanager.R;
import com.simonk.projects.taskmanager.entity.AppInfo;
import com.simonk.projects.taskmanager.entity.ProcessInfo;
import com.simonk.projects.taskmanager.repository.BlacklistRepository;
import com.simonk.projects.taskmanager.repository.ProcessRepository;
import com.simonk.projects.taskmanager.ui.MainActivity;

import java.util.List;

public class BlacklistJobService extends JobService {

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("TEST", "Start job");
        LiveData<List<AppInfo>> appInfoListData =
                new BlacklistRepository().getAllBlacklistApplicationInfo(getApplicationContext());
        appInfoListData.observeForever(new Observer<List<AppInfo>>() {
            @Override
            public void onChanged(List<AppInfo> appInfos) {
                Log.d("TEST", "New data. Length: " + appInfos.size());
                for (AppInfo appInfo : appInfos) {
                    ProcessInfo processInfo =
                            new ProcessRepository().getRunningProcessInfoForPackage(getApplicationContext(), appInfo.getPpackage());
                    if (processInfo != null) {
                        Log.d("TEST", "Send kill process: " + processInfo.getPpackage());
                        new ProcessRepository().killProcess(getApplicationContext(), processInfo);
                        notifyUser(appInfo);
                        new BlacklistRepository().setBlacklistAppOpenDate(getApplicationContext(), appInfo, System.currentTimeMillis());
                    }
                }
                appInfoListData.removeObserver(this);
            }
        });
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private void notifyUser(AppInfo appInfo) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher_background)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentTitle(getApplicationContext().getString(R.string.notify_title))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setContentText(String.format(getApplicationContext().getString(R.string.notify_content), appInfo.getText()))
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1000, builder.build());
    }

}
