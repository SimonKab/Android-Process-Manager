package com.simonk.projects.taskmanager.entity;

import java.io.InputStream;
import java.util.Objects;

public class TerminalCall {

    private String request;
    private String responseContent;
    private String responseErrorContent;
    private Exception exception;

    private InputStream responseInputStream;

    public String getRequest() {
        return request.trim();
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }

    public InputStream getResponseInputStream() {
        return responseInputStream;
    }

    public void setResponseInputStream(InputStream inputStream) {
        this.responseInputStream = inputStream;
    }

    public String getResponseErrorContent() {
        return responseErrorContent;
    }

    public void setResponseErrorContent(String responseErrorContent) {
        this.responseErrorContent = responseErrorContent;
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
                Objects.equals(getResponseContent(), that.getResponseContent()) &&
                Objects.equals(getResponseErrorContent(), that.getResponseErrorContent()) &&
                Objects.equals(getException(), that.getException());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRequest(), getResponseContent(), getResponseErrorContent(), getException());
    }
}
