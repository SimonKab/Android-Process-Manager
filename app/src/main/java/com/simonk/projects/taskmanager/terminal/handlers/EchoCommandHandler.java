package com.simonk.projects.taskmanager.terminal.handlers;

import com.simonk.projects.taskmanager.entity.TerminalCall;
import com.simonk.projects.taskmanager.terminal.Terminal;

public class EchoCommandHandler extends RequestHandler {

    private static final String COMMAND = "echo";

    @Override
    public boolean isHandleRequest(TerminalCall call) {
        return requestMatch(call.getRequest(), COMMAND);
    }

    @Override
    public void handleRequest(TerminalCall call, Terminal terminal) {
        String request = call.getRequest();
        String vars = request.substring(COMMAND.length() + 1);
        String[] subVars = vars.split(" ");

        StringBuilder responseBuilder = new StringBuilder();
        for (String subVar : subVars) {
            int dollarIndex = subVar.indexOf('$');
            if (dollarIndex != -1) {
                subVar = subVar.substring(dollarIndex + 1);
            }
            String value = terminal.getEnvironmentVar(subVar);
            if (value != null) {
                responseBuilder.append(value);
            }
        }

        //call.setResponseContent(responseBuilder.toString());
    }

}
