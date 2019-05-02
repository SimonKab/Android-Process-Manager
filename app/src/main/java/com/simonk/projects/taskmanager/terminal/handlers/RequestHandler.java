package com.simonk.projects.taskmanager.terminal.handlers;

import com.simonk.projects.taskmanager.entity.TerminalCall;
import com.simonk.projects.taskmanager.terminal.Terminal;

/**
 * Interface lets you to handle a terminal request by yourself.
 * Don't forget to register it in Terminal
 */
public abstract class RequestHandler {

    /**
     * Checks if call should be handled by this handler
     * @param call
     * @return
     */
    public abstract boolean isHandleRequest(TerminalCall call);

    /**
     * Handle request. Will be called only if isHandleRequest returned true
     * @param call
     * @param terminal
     */
    public abstract void handleRequest(TerminalCall call, Terminal terminal);

    /**
     * Util method. Checks is there is command in request
     * @param requestString
     * @param command
     * @return
     */
    boolean requestMatch(String requestString, String command) {
        String[] words = requestString.split(" ");
        return words.length > 0 && words[0].equals(command);
    }
}
