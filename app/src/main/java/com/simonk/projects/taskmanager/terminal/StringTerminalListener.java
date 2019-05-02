package com.simonk.projects.taskmanager.terminal;

/**
 * Util class handles byte array to string transformations
 */
public abstract class StringTerminalListener implements TerminalListener {

    public abstract void onInput(String input);

    public abstract void onError(String input);

    public abstract String onOutputString();

    @Override
    public void onInput(byte[] input) {
        onInput(new String(input));
    }

    @Override
    public void onError(byte[] error) {
        onError(new String(error));
    }

    @Override
    public byte[] onOutput() {
        return onOutputString().getBytes();
    }
}
