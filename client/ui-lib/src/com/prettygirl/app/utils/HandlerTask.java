package com.prettygirl.app.utils;

import android.os.Handler;
import android.os.Message;

public abstract class HandlerTask implements Runnable {

    private Handler mHandler;

    private static final int BEGINE = 1;

    private static final int UPDATE_PROGRESS = 2;

    private static final int OVER = 4;

    private boolean isRunning = false;

    public HandlerTask(boolean background) {
        super();
        if (!background) {
            mHandler = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case BEGINE:
                            onStartExecute();
                            break;
                        case UPDATE_PROGRESS:
                            onProgressChanged(msg.arg1);
                            break;
                        case OVER:
                            onStopExecute();
                            break;
                        default:
                            super.handleMessage(msg);
                    }
                }

            };
        }
    }

    @Override
    public final void run() {

    }

    protected final void updateProgress(int progress) {
        if (mHandler == null) {
            return;
        }
        Message msg = mHandler.obtainMessage(UPDATE_PROGRESS);
        msg.arg1 = progress;
        msg.sendToTarget();
    }

    protected void onStartExecute() {

    }

    protected void onProgressChanged(int progress) {

    }

    protected void onStopExecute() {

    }

    public boolean isRunning() {
        return isRunning;
    }

    void beforeExecute() {
        mHandler.sendEmptyMessage(BEGINE);
    }

    void afterExecute() {
        mHandler.sendEmptyMessage(OVER);
    }

}
