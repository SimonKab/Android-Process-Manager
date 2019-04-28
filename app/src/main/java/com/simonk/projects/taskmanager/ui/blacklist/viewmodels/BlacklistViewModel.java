package com.simonk.projects.taskmanager.ui.blacklist.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.simonk.projects.taskmanager.entity.AppInfo;
import com.simonk.projects.taskmanager.entity.ProcessInfo;
import com.simonk.projects.taskmanager.repository.BlacklistRepository;

import java.util.ArrayList;
import java.util.List;

public class BlacklistViewModel extends AndroidViewModel {

    private BlacklistRepository mRepository;

    private MutableLiveData<List<AppInfo>> mAllAppsInfo;
    private MutableLiveData<List<AppInfo>> mBlacklistInfo;

    public BlacklistViewModel(@NonNull Application application) {
        super(application);

        mRepository = new BlacklistRepository();

        mAllAppsInfo = new MutableLiveData<>();
        mAllAppsInfo.setValue(new ArrayList<>());
        mBlacklistInfo = new MutableLiveData<>();
        mBlacklistInfo.setValue(new ArrayList<>());

        mAllAppsInfo.setValue(mRepository.getAllInstalledApplicationsInfo(application, true));
    }

    public LiveData<List<AppInfo>> getAllAppsInfo() {
        return mAllAppsInfo;
    }

    public LiveData<List<AppInfo>> getBlacklistInfo() {
        return mBlacklistInfo;
    }
}
