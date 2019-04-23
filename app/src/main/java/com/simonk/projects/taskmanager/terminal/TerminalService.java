package com.simonk.projects.taskmanager.terminal;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.WorkerThread;

import com.simonk.projects.taskmanager.entity.TerminalCall;
import com.simonk.projects.taskmanager.util.ProcessCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TerminalService {

    private Terminal mTerminal;
    private TerminalThread mTerminalThread;
    private TerminalHandler mTerminalHandler;
    private MainThreadHandler mMainThreadHandler;

    private TerminalListener mTerminalListener;

    private static final int REQUEST_NEW = 1;
    private static final int REQUEST_INPUT = 2;
    private static final int REQUEST_INPUT_FINISH = 3;
    private static final int REQUEST_INPUT_STARTED = 4;
    private static final int REQUEST_FINISH = 5;

    private static byte[] BUFFER = new byte[4096];

    private boolean mTerminated = true;

    public TerminalService() {
        mTerminal = new Terminal();
        mTerminalThread = new TerminalThread();
        mTerminalThread.start();
        mTerminalHandler = new TerminalHandler(mTerminalThread.getLooper(), this);
        mMainThreadHandler = new MainThreadHandler(Looper.getMainLooper(), this);
    }

    @MainThread
    public void makeTerminalRequest(TerminalCall terminalCall, TerminalListener terminalListener) {
        mTerminalListener = terminalListener;
        mTerminalHandler.sendMessage(mTerminalHandler.obtainMessage(REQUEST_NEW, terminalCall));
    }

    @MainThread public void stopTerminalRequest() {
        mTerminated = true;
    }

    @WorkerThread
    private void performTerminalRequest(TerminalCall terminalCall) {
        mTerminated = false;
        TerminalCall response = mTerminal.makeNewRequest(terminalCall);

        Process process = response.getProcess();
        while (ProcessCompat.isAlive(process)) {
            InputStream contentInputStream = response.getResponseInputStream();
            try {
                int length = 0;
                while (length != -1) {
                    if (mTerminated) {
                        process.destroy();
                        stop();
                        return;
                    }
                    length = contentInputStream.read(BUFFER);
                    mMainThreadHandler.sendMessage(mMainThreadHandler.obtainMessage(REQUEST_INPUT, BUFFER));
                }
                mMainThreadHandler.sendEmptyMessage(REQUEST_INPUT_FINISH);
            } catch (IOException exception) {
                mMainThreadHandler.sendMessage(mMainThreadHandler.obtainMessage(REQUEST_INPUT_FINISH, exception));
            }
        }

        stop();
    }

    @WorkerThread
    private void stop() {
        mTerminated = true;
        mMainThreadHandler.sendEmptyMessage(REQUEST_FINISH);
    }

    @MainThread
    private void dispatchInput(byte[] input) {
        if (mTerminalListener != null) {
            mTerminalListener.onInput(input);
        }
    }

    @MainThread
    private void dispatchInputStarted() {
        if (mTerminalListener != null) {
            mTerminalListener.onInputStarted();
        }
    }

    @MainThread
    private void dispatchInputFinished(Exception exception) {
        if (mTerminalListener != null) {
            mTerminalListener.onInputFinished(exception);
        }
    }

    @MainThread
    private void dispatchFinished() {
        if (mTerminalListener != null) {
            mTerminalListener.onFinished();
        }
    }

    private static class TerminalHandler extends Handler {
        private final TerminalService service;

        TerminalHandler(Looper looper, TerminalService dispatcher) {
            super(looper);
            this.service = dispatcher;
        }

        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case REQUEST_NEW:
                    service.performTerminalRequest((TerminalCall) msg.obj);
                    break;
            }
        }
    }

    private static class MainThreadHandler extends Handler {
        private final TerminalService service;

        MainThreadHandler(Looper looper, TerminalService dispatcher) {
            super(looper);
            this.service = dispatcher;
        }

        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case REQUEST_INPUT:
                    service.dispatchInput((byte[]) msg.obj);
                    break;
                case REQUEST_INPUT_FINISH:
                    service.dispatchInputFinished((Exception) msg.obj);
                    break;
                case REQUEST_INPUT_STARTED:
                    service.dispatchInputStarted();
                    break;
                case REQUEST_FINISH:
                    service.dispatchFinished();
                    break;
            }
        }
    }

    private static class TerminalThread extends HandlerThread {
        public TerminalThread() {
            super("TaskManager-Terminal");
        }
    }
}
