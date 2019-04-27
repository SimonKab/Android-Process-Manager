package com.simonk.projects.taskmanager.util;

public class ProcessCompat {

    public static boolean isAlive(Process process) {
        try {
            process.exitValue();
            return false;
        } catch(IllegalThreadStateException e) {
            return true;
        }
    }

}
