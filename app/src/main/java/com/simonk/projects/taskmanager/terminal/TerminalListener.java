package com.simonk.projects.taskmanager.terminal;

public interface TerminalListener {

    void onInput(byte[] input);

    void onInputStarted();

    void onInputFinished(Exception exception);

    void onError(byte[] error);

    void onErrorFinished();

    byte[] onOutput();

    void onFinished();
}
