package com.simonk.projects.taskmanager.entity;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class TerminalCall {

    private String request;
    private Exception exception;

    private Process process;

    public String getRequest() {
        return request.trim();
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public Process getProcess() {
        return process;
    }

    public InputStream getResponseInputStream() {
        return process.getInputStream();
    }

    public InputStream getResponseErrorStream() {
        return process.getErrorStream();
    }

    public OutputStream getResponseOutputStream() {
        return process.getOutputStream();
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TerminalCall that = (TerminalCall) o;
        return Objects.equals(getRequest(), that.getRequest()) &&
                Objects.equals(getException(), that.getException()) &&
                Objects.equals(getProcess(), that.getProcess());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRequest(), getException(), getProcess());
    }
}
