package com.simonk.projects.taskmanager.terminal;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.WorkerThread;

import com.simonk.projects.taskmanager.entity.TerminalCall;
import com.simonk.projects.taskmanager.terminal.interceptors.TerminalRequestInterceptor;
import com.simonk.projects.taskmanager.terminal.interceptors.TopCommandInterceptor;
import com.simonk.projects.taskmanager.util.ProcessCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TerminalService {

    private static final int BUFFER_SIZE = 4096;

    private Terminal mTerminal;
    private TerminalThread mTerminalThread;
    private TerminalHandler mTerminalHandler;
    private MainThreadHandler mMainThreadHandler;

    private TerminalListener mTerminalListener;

    private static final int REQUEST_NEW = 1;
    private static final int REQUEST_INPUT = 2;
    private static final int REQUEST_INPUT_FINISH = 3;
    private static final int REQUEST_INPUT_STARTED = 4;
    private static final int REQUEST_ERROR = 5;
    private static final int REQUEST_ERROR_FINISH = 6;
    private static final int REQUEST_ERROR_STARTED = 7;
    private static final int REQUEST_FINISH = 8;

    private static byte[] BUFFER = new byte[BUFFER_SIZE];

    private List<TerminalRequestInterceptor> mInterceptors;

    private boolean mTerminated = true;

    public TerminalService() {
        mTerminal = new Terminal();
        mTerminalThread = new TerminalThread();
        mTerminalThread.start();
        mTerminalHandler = new TerminalHandler(mTerminalThread.getLooper(), this);
        mMainThreadHandler = new MainThreadHandler(Looper.getMainLooper(), this);

        mInterceptors = new ArrayList<>();
        mInterceptors.add(new TopCommandInterceptor());
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
        clearBuffer();
        mTerminated = false;

        TerminalCall response = mTerminal.makeNewRequest(terminalCall);

        if (response.getException() != null) {
            dispatchSendFinish(response.getException());
            return;
        }

        TerminalRequestInterceptor requestInterceptor = null;
        for (TerminalRequestInterceptor interceptor : mInterceptors) {
            if (interceptor.willIntercept(terminalCall)) {
                requestInterceptor = interceptor;
                break;
            }
        }

        if (response.getProcess() != null) {
            performProcessWork(response, requestInterceptor);
        } else {
            performInputStreamWork(response, requestInterceptor);
        }

        stop();
    }

    private void performInputStreamWork(TerminalCall response, TerminalRequestInterceptor requestInterceptor) {
        InputStream contentInputStream = response.getResponseInputStream();
        if (contentInputStream == null) {
            return;
        }
        try {
            int length = 0;
            while (length != -1) {
                if (mTerminated) {
                    stop();
                    return;
                }
                length = contentInputStream.read(BUFFER);
                if (requestInterceptor != null) {
                    requestInterceptor.interceptInput(this, response, BUFFER);
                } else {
                    dispatchSendInput(BUFFER);
                }
            }
            dispatchSendFinishInput(null);
        } catch (IOException exception) {
            dispatchSendFinishInput(exception);
        }
    }

    private void performProcessWork(TerminalCall response, TerminalRequestInterceptor requestInterceptor) {
        Process process = response.getProcess();
        while (ProcessCompat.isAlive(process)) {
            performInputStreamWork(response, requestInterceptor);
            if (mTerminated) {
                process.destroy();
                return;
            }

            InputStream errorInputStream = response.getResponseErrorStream();
            try {
                int length = 0;
                while (length != -1) {
                    if (mTerminated) {
                        stop();
                        return;
                    }
                    length = errorInputStream.read(BUFFER);
                    dispatchSendInput(BUFFER);
                }
                dispatchSendFinishInput(null);
            } catch (IOException exception) {
                dispatchSendFinishInput(exception);
            }
        }
    }

    public void dispatchSendInput(byte[] buffer) {
        mMainThreadHandler.sendMessage(mMainThreadHandler.obtainMessage(REQUEST_INPUT, buffer));
    }

    public void dispatchSendFinishInput(Exception exception) {
        mMainThreadHandler.sendMessage(mMainThreadHandler.obtainMessage(REQUEST_INPUT_FINISH, exception));
    }

    private void dispatchSendError(byte[] buffer) {
        mMainThreadHandler.sendMessage(mMainThreadHandler.obtainMessage(REQUEST_ERROR, buffer));
    }

    public void dispatchSendFinishError(Exception exception) {
        mMainThreadHandler.sendMessage(mMainThreadHandler.obtainMessage(REQUEST_ERROR_FINISH, exception));
    }

    public void dispatchSendFinish(Exception exception) {
        mMainThreadHandler.sendMessage(mMainThreadHandler.obtainMessage(REQUEST_FINISH, exception));
    }


    @WorkerThread
    private void stop() {
        mTerminated = true;
        dispatchSendFinish(null);
        clearBuffer();
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
    private void dispatchErrorFinished(Exception exception) {
        if (mTerminalListener != null) {
            mTerminalListener.onErrorFinished(exception);
        }
    }

    @MainThread
    private void dispatchError(byte[] input) {
        if (mTerminalListener != null) {
            mTerminalListener.onError(input);
        }
    }

    @MainThread
    private void dispatchErrorStarted() {
        if (mTerminalListener != null) {
            mTerminalListener.onErrorStarted();
        }
    }

    @MainThread
    private void dispatchInputFinished(Exception exception) {
        if (mTerminalListener != null) {
            mTerminalListener.onInputFinished(exception);
        }
    }

    @MainThread
    private void dispatchFinished(Exception exception) {
        if (mTerminalListener != null) {
            mTerminalListener.onFinished(exception);
        }
    }

    private void clearBuffer() {
        BUFFER = new byte[BUFFER_SIZE];
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
                    service.dispatchFinished((Exception) msg.obj);
                    break;
                case REQUEST_ERROR:
                    service.dispatchError((byte[]) msg.obj);
                    break;
                case REQUEST_ERROR_FINISH:
                    service.dispatchErrorFinished((Exception) msg.obj);
                    break;
                case REQUEST_ERROR_STARTED:
                    service.dispatchErrorStarted();
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
