package com.simonk.projects.taskmanager.ui.blacklist.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.simonk.projects.taskmanager.entity.AppInfo;
import com.simonk.projects.taskmanager.entity.ProcessInfo;
import com.simonk.projects.taskmanager.repository.BlacklistRepository;

import java.util.ArrayList;
import java.util.List;

public class BlacklistViewModel extends AndroidViewModel {

    private BlacklistRepository mRepository;

    private MutableLiveData<List<AppInfo>> mAllAppsInfo;
    private LiveData<List<AppInfo>> mBlacklistInfo;

    private MediatorLiveData<AppsInfoList> mAppsInfo;

    public BlacklistViewModel(@NonNull Application application) {
        super(application);

        mRepository = new BlacklistRepository();

        mAllAppsInfo = new MutableLiveData<>();
        mBlacklistInfo = new MutableLiveData<>();

        mAllAppsInfo.setValue(mRepository.getAllInstalledApplicationsInfo(application, true));
        mBlacklistInfo = mRepository.getAllBlacklistApplicationInfo(application);

        // important: appsInfo initialization must be after allAppsInfo and blacklistInfo initialization
        mAppsInfo = new MediatorLiveData<>();
        mAppsInfo.addSource(mAllAppsInfo, value -> {
            AppsInfoList appsInfoList = mAppsInfo.getValue();
            if (appsInfoList == null) {
                appsInfoList = new AppsInfoList();
            }
            appsInfoList.allApps = value;
            mAppsInfo.setValue(appsInfoList);
        });
        mAppsInfo.addSource(mBlacklistInfo, value -> {
            AppsInfoList appsInfoList = mAppsInfo.getValue();
            if (appsInfoList == null) {
                appsInfoList = new AppsInfoList();
            }
            appsInfoList.blacklistApps = value;
            if (appsInfoList.blacklistApps != null && appsInfoList.allApps != null) {
                for (AppInfo info : value) {
                    for (int i = 0; i < appsInfoList.allApps.size(); i++) {
                        if (appsInfoList.allApps.get(i).getPpackage().equals(info.getPpackage())) {
                            appsInfoList.allApps.remove(i);
                            i--;
                        }
                    }
                }
            }
            mAppsInfo.setValue(appsInfoList);
        });
    }

    public LiveData<List<AppInfo>> getAllAppsInfo() {
        return mAllAppsInfo;
    }

    public LiveData<List<AppInfo>> getBlacklistInfo() {
        return mBlacklistInfo;
    }

    public LiveData<AppsInfoList> getAppsInfo() {
        return mAppsInfo;
    }

    public void insertAppInBlacklist(AppInfo appInfo) {
        mRepository.insertAppInBlacklist(getApplication(), appInfo);
    }

    public void deleteAppFromBlackList(AppInfo appInfo) {
        mRepository.deleteAppFromBlackList(getApplication(), appInfo);
    }

    public static class AppsInfoList {
        public List<AppInfo> allApps;
        public List<AppInfo> blacklistApps;
    }
}
