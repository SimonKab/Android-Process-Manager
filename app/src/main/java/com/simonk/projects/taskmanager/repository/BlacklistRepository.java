package com.simonk.projects.taskmanager.repository;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.room.Database;

import com.simonk.projects.taskmanager.database.DatabaseManager;
import com.simonk.projects.taskmanager.database.entity.BlacklistEntity;
import com.simonk.projects.taskmanager.entity.AppInfo;
import com.simonk.projects.taskmanager.entity.ProcessInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for blacklist. Provides base functions for work with blacklist
 * Retrieves information from different sources: system's PackageManager and local database
 */
public class BlacklistRepository {

    /**
     * Returns all installed on device apps. Uses system's PackageManager
     *
     * All returned apps supposed to be not in black list
     *
     * Note: not returns current application in list
     *
     * TODO: perform logic in separated thread with LiveData
     *
     * @param context
     * @param notSystem if true all system applications will be ignored
     * @return list of applications
     */
    public List<AppInfo> getAllInstalledApplicationsInfo(Context context, boolean notSystem) {
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        List<AppInfo> appInfoList = new ArrayList<>();
        for (ApplicationInfo info : installedApplications) {
            AppInfo appInfo = new AppInfo();
            appInfo.setText(packageManager.getApplicationLabel(info).toString());
            appInfo.setImage(packageManager.getApplicationIcon(info));
            appInfo.setPpackage(info.processName);
            appInfo.setEnabled(info.enabled);
            appInfo.setInBlacklist(false);
            boolean isSystem = (info.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
            if (isSystem && notSystem) {
                continue;
            }
            appInfo.setIsSystem(isSystem);

            if (appInfo.getPpackage().contains(context.getPackageName())) {
                continue;
            }

            appInfoList.add(appInfo);
        }

        return appInfoList;
    }

    /**
     * Returns LiveData of blacklist retrieved from database
     * @param context
     * @return
     */
    public LiveData<List<AppInfo>> getAllBlacklistApplicationInfo(Context context) {
        LiveData<List<BlacklistEntity>> blacklistEntitiesData = DatabaseManager.loadBlacklistEntities(context);
        return Transformations.map(blacklistEntitiesData, blacklistEntities -> {
            List<AppInfo> appInfoList = new ArrayList<>();
            PackageManager packageManager = context.getPackageManager();
            for (BlacklistEntity entity : blacklistEntities) {
                ApplicationInfo info = null;
                try {
                    info = packageManager.getApplicationInfo(entity.apppackage, PackageManager.GET_META_DATA);
                } catch (PackageManager.NameNotFoundException exception) {
                    continue;
                }
                AppInfo appInfo = new AppInfo();
                appInfo.setText(packageManager.getApplicationLabel(info).toString());
                appInfo.setImage(packageManager.getApplicationIcon(info));
                appInfo.setPpackage(info.processName);
                appInfo.setEnabled(info.enabled);
                appInfo.setIsSystem((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
                appInfo.setInBlacklist(true);
                appInfo.setLastOpenDate(entity.lastOpenDate);

                appInfoList.add(appInfo);
            }

            return appInfoList;
        });
    }

    /**
     * Insert an application to blacklist
     * @param context
     * @param appInfo
     */
    public void insertAppInBlacklist(Context context, AppInfo appInfo) {
        BlacklistEntity blacklistEntity = new BlacklistEntity();
        blacklistEntity.apppackage = appInfo.getPpackage();
        blacklistEntity.lastOpenDate = appInfo.getLastOpenDate();
        DatabaseManager.insertBlacklistEntity(context, blacklistEntity);
    }

    /**
     * Delete an application from blacklsit
     * @param context
     * @param appInfo
     */
    public void deleteAppFromBlackList(Context context, AppInfo appInfo) {
        BlacklistEntity blacklistEntity = new BlacklistEntity();
        blacklistEntity.apppackage = appInfo.getPpackage();
        blacklistEntity.lastOpenDate = appInfo.getLastOpenDate();
        DatabaseManager.deleteBlacklistEntityByPackage(context, blacklistEntity.apppackage);
    }

    /**
     * Change last open date of an application in blacklsit
     * @param context
     * @param appInfo
     * @param openDate
     */
    public void setBlacklistAppOpenDate(Context context, AppInfo appInfo, long openDate) {
        DatabaseManager.updateBlacklistEntityOpenDate(context, appInfo.getPpackage(), openDate);
    }
}
