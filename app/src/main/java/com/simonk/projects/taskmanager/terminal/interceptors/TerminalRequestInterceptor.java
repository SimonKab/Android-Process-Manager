package com.simonk.projects.taskmanager.terminal.interceptors;

import com.simonk.projects.taskmanager.entity.TerminalCall;
import com.simonk.projects.taskmanager.terminal.Terminal;
import com.simonk.projects.taskmanager.terminal.TerminalService;

public interface TerminalRequestInterceptor {

    boolean willIntercept(TerminalCall terminalCall);

    void interceptInput(TerminalService terminalService, TerminalCall terminalCall, byte[] data);
}
