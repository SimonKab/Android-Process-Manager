package com.simonk.projects.taskmanager.terminal.handlers;

import com.simonk.projects.taskmanager.entity.TerminalCall;
import com.simonk.projects.taskmanager.terminal.Terminal;

import java.io.File;

public class CdCommandHandler extends RequestHandler {

    private static final String COMMAND = "cd";

    @Override
    public boolean isHandleRequest(TerminalCall call) {
        return requestMatch(call.getRequest(), COMMAND);
    }

    @Override
    public void handleRequest(TerminalCall call, Terminal terminal) {
        String request = call.getRequest();
        String newPath = request.substring(3); // remove 'cd ' part from request

        if (newPath.isEmpty()) {
            return;
        }

        if (newPath.equals("/")) {
            terminal.setCurrentDirectory(new File("/"));
            return;
        }

        String[] subPaths = newPath.split("/");

        File currentDirectory;
        if (subPaths[0].isEmpty()) {
            currentDirectory = new File("/");
        } else {
            currentDirectory = terminal.getCurrentDirectory();
        }
        for (String subPath : subPaths) {
            currentDirectory = handleSubpath(currentDirectory, subPath);
        }
        terminal.setCurrentDirectory(currentDirectory);
    }

    private File handleSubpath(File currentDirectory, String subPath) {
        if (subPath.equals(".")) {
            return currentDirectory;
        }

        if (subPath.equals("..")) {
            File parent = currentDirectory.getParentFile();
            if (parent == null) {
                return currentDirectory;
            }
            return parent;
        }

        return new File(currentDirectory, subPath);
    }
}
