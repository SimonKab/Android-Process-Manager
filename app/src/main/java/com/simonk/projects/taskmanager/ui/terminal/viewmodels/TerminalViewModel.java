package com.simonk.projects.taskmanager.ui.terminal.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.simonk.projects.taskmanager.entity.TerminalCall;
import com.simonk.projects.taskmanager.terminal.Terminal;

import java.util.ArrayList;
import java.util.List;

public class TerminalViewModel extends AndroidViewModel {

    private MutableLiveData<List<TerminalCall>> mTerminalRequestArray;
    private MutableLiveData<TerminalCall> mTerminalLastResponse;

    private Terminal mTerminal;

    public TerminalViewModel(@NonNull Application application) {
        super(application);

        mTerminal = new Terminal();

        mTerminalLastResponse = new MutableLiveData<>();
        mTerminalRequestArray = new MutableLiveData<>();
        mTerminalRequestArray.setValue(new ArrayList<>());
    }

    public void makeNewTerminalRequest(String requestString) {
        TerminalCall request = new TerminalCall();
        request.setRequest(requestString);
        TerminalCall response = mTerminal.makeNewRequest(request);
        mTerminalLastResponse.setValue(response);

        mTerminalRequestArray.getValue().add(response);
    }

    public LiveData<List<TerminalCall>> getTerminalRequests() {
        return mTerminalRequestArray;
    }

    public LiveData<TerminalCall> getLastResponse() {
        return mTerminalLastResponse;
    }

}
