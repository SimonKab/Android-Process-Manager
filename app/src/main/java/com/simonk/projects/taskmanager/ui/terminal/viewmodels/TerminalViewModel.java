package com.simonk.projects.taskmanager.ui.terminal.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.simonk.projects.taskmanager.database.DatabaseManager;
import com.simonk.projects.taskmanager.database.LocalDatabase;
import com.simonk.projects.taskmanager.database.entity.TerminalSnapshotEntity;
import com.simonk.projects.taskmanager.entity.TerminalCall;
import com.simonk.projects.taskmanager.terminal.StringTerminalListener;
import com.simonk.projects.taskmanager.terminal.TerminalService;

import java.util.ArrayList;
import java.util.List;

public class TerminalViewModel extends AndroidViewModel {

    private MutableLiveData<List<TerminalSnapshotEntity>> mTerminalSnapshotsArray;

    private TerminalService mTerminalService;

    public TerminalViewModel(@NonNull Application application) {
        super(application);

        mTerminalService = new TerminalService();

        mTerminalSnapshotsArray = new MutableLiveData<>();
        mTerminalSnapshotsArray.setValue(new ArrayList<>());

        LiveData<List<TerminalSnapshotEntity>> databaseSnapshots = DatabaseManager.loadSnapshots(application);
        databaseSnapshots.observeForever(new Observer<List<TerminalSnapshotEntity>>() {
            @Override
            public void onChanged(List<TerminalSnapshotEntity> snapshotEntityList) {
                mTerminalSnapshotsArray.setValue(snapshotEntityList);
                databaseSnapshots.removeObserver(this);
            }
        });
    }

    public void makeNewTerminalRequest(String requestString, StringTerminalListener terminalListener) {
        TerminalCall request = new TerminalCall();
        request.setRequest(requestString);
        mTerminalService.makeTerminalRequest(request, terminalListener);
    }

    public void stopTerminalRequest() {
        mTerminalService.stopTerminalRequest();
    }

    public void clearTerminalSnapshots() {
        stopTerminalRequest();
        mTerminalSnapshotsArray.setValue(new ArrayList<>());
        DatabaseManager.deleteAllSnapshots(getApplication());
    }

    public LiveData<List<TerminalSnapshotEntity>> getTerminalSnapshots() {
        return mTerminalSnapshotsArray;
    }

    public void addTerminalSnapshot(TerminalSnapshotEntity snapshot) {
        mTerminalSnapshotsArray.getValue().add(snapshot);
        DatabaseManager.insertSnapshot(getApplication(), snapshot);
    }
}
