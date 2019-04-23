package com.simonk.projects.taskmanager.terminal.handlers;

import com.simonk.projects.taskmanager.entity.TerminalCall;
import com.simonk.projects.taskmanager.terminal.Terminal;

public class PwdCommandHandler extends RequestHandler {

    private static final String COMMAND = "pwd";

    @Override
    public boolean isHandleRequest(TerminalCall call) {
        return requestMatch(call.getRequest(), COMMAND);
    }

    @Override
    public void handleRequest(TerminalCall call, Terminal terminal) {
        call.setResponseContent(terminal.getCurrentDirectory().getAbsolutePath());
    }

}
