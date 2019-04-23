package com.simonk.projects.taskmanager.terminal;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.simonk.projects.taskmanager.entity.TerminalCall;

import java.io.File;
import java.io.InputStreamReader;

public class Terminal {

    public TerminalCall makeNewRequest(TerminalCall terminalRequest) {
        String request = terminalRequest.getRequest().trim();


        try {
            Process process = Runtime.getRuntime().exec(request, null, null);
            terminalRequest.setResponseContent(CharStreams.toString(new InputStreamReader(process.getInputStream(), Charsets.UTF_8)));
            terminalRequest.setResponseErrorContent(CharStreams.toString(new InputStreamReader(process.getErrorStream(), Charsets.UTF_8)));
        } catch (Exception e) {
            terminalRequest.setException(e);
        }
        return terminalRequest;
    }

}
