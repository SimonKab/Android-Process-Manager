package com.simonk.projects.taskmanager.terminal.handlers;

import com.simonk.projects.taskmanager.entity.TerminalCall;
import com.simonk.projects.taskmanager.terminal.Terminal;

public class BlankCommandHandler extends RequestHandler {

    @Override
    public boolean isHandleRequest(TerminalCall call) {
        return call.getRequest().isEmpty();
    }

    @Override
    public void handleRequest(TerminalCall call, Terminal terminal) {

    }

}
