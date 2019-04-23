package com.simonk.projects.taskmanager.terminal.handlers;

import com.simonk.projects.taskmanager.entity.TerminalCall;
import com.simonk.projects.taskmanager.terminal.Terminal;

public abstract class RequestHandler {

    public abstract boolean isHandleRequest(TerminalCall call);

    public abstract void handleRequest(TerminalCall call, Terminal terminal);

    boolean requestMatch(String requestString, String command) {
        String[] words = requestString.split(" ");
        return words.length > 0 && words[0].equals(command);
    }
}
