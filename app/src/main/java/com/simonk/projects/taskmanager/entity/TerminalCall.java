package com.simonk.projects.taskmanager.entity;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * POJO represents terminal call. Contains request to terminal and different streams of corresponding response
 */
public class TerminalCall {

    private String request;
    private Exception exception;

    private Process process;

    private InputStream inputStream;

    /**
     * @return request to terminal. Additionally trim output
     */
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

    /**
     * By default input stream will be retrieved from Process object supplied by setProcess method
     * But you can explicitly set input stream which will have bigger priority than process's one
     * @param inputStream
     */
    public void setResponseInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * @return input stream provided by setResponseInputStream or input stream retrieved from Process
     */
    public InputStream getResponseInputStream() {
        return inputStream == null
                ? (process == null ? null : process.getInputStream())
                : inputStream;
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
