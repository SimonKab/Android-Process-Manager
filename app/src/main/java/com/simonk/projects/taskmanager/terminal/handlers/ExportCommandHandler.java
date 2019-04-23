package com.simonk.projects.taskmanager.terminal.handlers;

import com.simonk.projects.taskmanager.entity.TerminalCall;
import com.simonk.projects.taskmanager.terminal.Terminal;

public class ExportCommandHandler extends RequestHandler {

    private static final String COMMAND = "export";

    @Override
    public boolean isHandleRequest(TerminalCall call) {
        return requestMatch(call.getRequest(), COMMAND);
    }

    @Override
    public void handleRequest(TerminalCall call, Terminal terminal) {
        String request = call.getRequest();
        String vars = request.substring(COMMAND.length() + 1);
        String[] subVars = vars.split(" ");

        for (String var : subVars) {
            String[] varAssignment = var.split("=");
            if (varAssignment.length != 2) {
                continue;
            }

            String name = varAssignment[0];
            String value = varAssignment[1];
            terminal.addEnvironmentVar(name, value);
        }
    }

}
