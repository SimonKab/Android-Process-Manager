package com.simonk.projects.taskmanager.terminal.handlers;

import com.simonk.projects.taskmanager.entity.TerminalCall;
import com.simonk.projects.taskmanager.terminal.Terminal;

import java.io.ByteArrayInputStream;


public class PwdCommandHandler extends RequestHandler {

    private static final String COMMAND = "pwd";

    @Override
    public boolean isHandleRequest(TerminalCall call) {
        return requestMatch(call.getRequest(), COMMAND);
    }

    @Override
    public void handleRequest(TerminalCall call, Terminal terminal) {
        call.setResponseInputStream(new ByteArrayInputStream(terminal.getCurrentDirectory().getAbsolutePath().getBytes()));
    }

}
