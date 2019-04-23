package com.simonk.projects.taskmanager.terminal;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.simonk.projects.taskmanager.entity.TerminalCall;
import com.simonk.projects.taskmanager.terminal.handlers.BlankCommandHandler;
import com.simonk.projects.taskmanager.terminal.handlers.CdCommandHandler;
import com.simonk.projects.taskmanager.terminal.handlers.EchoCommandHandler;
import com.simonk.projects.taskmanager.terminal.handlers.ExportCommandHandler;
import com.simonk.projects.taskmanager.terminal.handlers.PwdCommandHandler;
import com.simonk.projects.taskmanager.terminal.handlers.RequestHandler;

import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Terminal {

    private File mCurrentDirectory;

    private List<RequestHandler> mRequestHandlers;

    private Map<String, String> mEnvironmentVars;

    public Terminal() {
        mEnvironmentVars = new HashMap<>(System.getenv());
        mCurrentDirectory = new File("/");

        mRequestHandlers = new ArrayList<>();
        registerRequestHandlers();
    }

    private void registerRequestHandlers() {
        mRequestHandlers.add(new BlankCommandHandler());
        mRequestHandlers.add(new CdCommandHandler());
        mRequestHandlers.add(new PwdCommandHandler());
        mRequestHandlers.add(new EchoCommandHandler());
        mRequestHandlers.add(new ExportCommandHandler());
    }

    public TerminalCall makeNewRequest(TerminalCall terminalRequest) {
        String request = terminalRequest.getRequest().trim();

        for (RequestHandler requestHandler : mRequestHandlers) {
            if (requestHandler.isHandleRequest(terminalRequest)) {
                requestHandler.handleRequest(terminalRequest, this);
                return  terminalRequest;
            }
        }

        try {
            Process process = Runtime.getRuntime().exec(
                    request,
                    convertEnvironmentVarsToString(mEnvironmentVars),
                    mCurrentDirectory
            );
            terminalRequest.setProcess(process);
        } catch (Exception e) {
            terminalRequest.setException(e);
        }
        return terminalRequest;
    }

    public void setCurrentDirectory(File currentDirectory) {
        mCurrentDirectory = currentDirectory;
    }

    public File getCurrentDirectory() {
        return mCurrentDirectory;
    }

    public void addEnvironmentVar(String name, String data) {
        mEnvironmentVars.put(name, data);
    }

    public String getEnvironmentVar(String name) {
        return mEnvironmentVars.get(name);
    }

    private String[] convertEnvironmentVarsToString(Map<String, String> environmentVarsMap) {
        if (environmentVarsMap.size() == 0) {
            return null;
        }

        List<String> output = new ArrayList<>();
        for (String key : environmentVarsMap.keySet()) {
            output.add(String.format("%s=%s", key, environmentVarsMap.get(key)));
        }
        return output.toArray(new String[0]);
    }
}
