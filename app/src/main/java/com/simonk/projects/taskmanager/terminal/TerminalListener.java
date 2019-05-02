package com.simonk.projects.taskmanager.terminal;

/**
 * Listener for terminal requests. Use to get response and supply additional output to terminal
 */
public interface TerminalListener {

    /**
     * New part of input data received
     * @param input
     */
    void onInput(byte[] input);

    /**
     * Start reading input stream from response
     */
    void onInputStarted();


    /**
     * Finish reading input stream from response
     * @param exception
     */
    void onInputFinished(Exception exception);


    /**
     * New part of error data received
     * @param error
     */
    void onError(byte[] error);

    /**
     * Start reading error stream from response
     */
    void onErrorStarted();


    /**
     * Finish reading error stream form response
     * @param exception
     */
    void onErrorFinished(Exception exception);

    /**
     * Called when terminal wait some data to send in command's process
     * @return
     */
    byte[] onOutput();

    /**
     * Request finished
     * @param exception
     */
    void onFinished(Exception exception);
}
