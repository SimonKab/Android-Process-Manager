package com.simonk.projects.taskmanager.entity;

import java.util.Objects;

public class TerminalSnapshot {

    private String request;

    private String response;

    public TerminalSnapshot(String request, String response) {
        this.request = request;
        this.response = response;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TerminalSnapshot that = (TerminalSnapshot) o;
        return Objects.equals(getRequest(), that.getRequest()) &&
                Objects.equals(getResponse(), that.getResponse());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRequest(), getResponse());
    }
}
