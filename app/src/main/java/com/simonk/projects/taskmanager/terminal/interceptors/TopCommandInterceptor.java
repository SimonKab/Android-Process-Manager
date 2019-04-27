package com.simonk.projects.taskmanager.terminal.interceptors;

import com.simonk.projects.taskmanager.entity.TerminalCall;
import com.simonk.projects.taskmanager.terminal.Terminal;
import com.simonk.projects.taskmanager.terminal.TerminalService;

public class TopCommandInterceptor implements TerminalRequestInterceptor {

    private static final String COMMAND = "top";

    private String mData = "";

    @Override
    public boolean willIntercept(TerminalCall terminalCall) {
        String[] words = terminalCall.getRequest().split(" ");
        return words.length > 0 && words[0].equals(COMMAND);
    }

    @Override
    public void interceptInput(TerminalService terminalService, TerminalCall terminalCall, byte[] data) {
        String stringRepr = new String(data);
        int deviderIndex = stringRepr.indexOf("\n\n\n");
        if (deviderIndex != -1) {
            mData += stringRepr.substring(0, deviderIndex);
            terminalService.dispatchSendInput(mData.getBytes());
            mData = stringRepr.substring(deviderIndex);
        } else {
            mData += stringRepr;
        }
    }
}
