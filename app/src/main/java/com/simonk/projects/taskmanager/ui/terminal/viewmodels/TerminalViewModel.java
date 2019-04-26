package com.simonk.projects.taskmanager.ui.terminal.viewmodels;

import android.app.Application;
import android.os.HandlerThread;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.simonk.projects.taskmanager.entity.TerminalCall;
import com.simonk.projects.taskmanager.entity.TerminalSnapshot;
import com.simonk.projects.taskmanager.terminal.StringTerminalListener;
import com.simonk.projects.taskmanager.terminal.Terminal;
import com.simonk.projects.taskmanager.terminal.TerminalService;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TerminalViewModel extends AndroidViewModel {

    private MutableLiveData<List<TerminalSnapshot>> mTerminalSnapshotsArray;

    private TerminalService mTerminalService;

    public TerminalViewModel(@NonNull Application application) {
        super(application);

        mTerminalService = new TerminalService();

        mTerminalSnapshotsArray = new MutableLiveData<>();
        mTerminalSnapshotsArray.setValue(new ArrayList<>());
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
    }

    public LiveData<List<TerminalSnapshot>> getTerminalSnapshots() {
        return mTerminalSnapshotsArray;
    }

    public void addTerminalSnapshot(TerminalSnapshot snapshot) {
        mTerminalSnapshotsArray.getValue().add(snapshot);
    }
}
