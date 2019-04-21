package com.simonk.projects.taskmanager.ui.process.viewmodels;

import android.app.ActivityManager;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.simonk.projects.taskmanager.entity.ProcessInfo;
import com.simonk.projects.taskmanager.repository.ProcessRepository;
import com.simonk.projects.taskmanager.util.MemoryUtils;

import java.util.List;

public class ProcessViewModel extends AndroidViewModel {

    private ProcessRepository mRepository;

    private MutableLiveData<List<ProcessInfo>> mAllProcessInfo;

    public ProcessViewModel(@NonNull Application application) {
        super(application);

        mRepository = new ProcessRepository();

        mAllProcessInfo = new MutableLiveData<>();
        mAllProcessInfo.setValue(mRepository.getAllProcessInfo(application));
    }

    public ProcessRepository getRepository() {
        return mRepository;
    }

    public float getAvailableMemory() {
        ActivityManager.MemoryInfo memInfo = MemoryUtils.getMemoryInfo(getApplication());
        return MemoryUtils.getAvailableMemory(memInfo);
    }

    public float getTotalMemory() {
        ActivityManager.MemoryInfo memInfo = MemoryUtils.getMemoryInfo(getApplication());
        return MemoryUtils.getTotalMemory(memInfo);
    }

    public void forceUpdateProcessInfo() {
        mAllProcessInfo.setValue(mRepository.getAllProcessInfo(getApplication()));
    }

    public LiveData<List<ProcessInfo>> getAllProcessInfo() {
        return mAllProcessInfo;
    }
}
