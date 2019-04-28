package com.simonk.projects.taskmanager.repository;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.simonk.projects.taskmanager.entity.AppInfo;
import com.simonk.projects.taskmanager.entity.ProcessInfo;

import java.util.ArrayList;
import java.util.List;

public class BlacklistRepository {

    public List<AppInfo> getAllInstalledApplicationsInfo(Context context, boolean notSystem) {
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        List<AppInfo> appInfoList = new ArrayList<>();
        for (ApplicationInfo info : installedApplications) {
            AppInfo appInfo = new AppInfo();
            try {
                ApplicationInfo applicationInfo =
                        packageManager.getApplicationInfo(info.processName, 0);
                appInfo.setText(packageManager.getApplicationLabel(applicationInfo).toString());
                appInfo.setImage(packageManager.getApplicationIcon(applicationInfo));
                appInfo.setPpackage(info.processName);
                appInfo.setEnabled(applicationInfo.enabled);
                boolean isSystem = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
                if (isSystem && notSystem) {
                    continue;
                }
                appInfo.setIsSystem(isSystem);
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }
            appInfoList.add(appInfo);
        }

        return appInfoList;
    }

}
