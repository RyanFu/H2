package com.prettygirl.app.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

public class ThreadHandler extends HandlerThread {

    private static final String NAME = "MHandlerThread";

    private static final String MESSAGE_TYPE = "MESSAGE_TYPE";

    private static final int CORE_POOL_SIZE = 1;

    private static final int MAXIMUM_POOL_SIZE = 1;

    private static final int KEEP_ALIVE = 1;

    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(8);

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new MThreadPoolExecutor(CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE, KEEP_ALIVE, sPoolWorkQueue);

    public Handler mHandler;

    private Handler mCallback;

    public static final int REQUEST_MESSAGE = 0xffff01;

    public static final int RESPONSE_MESSAGE = 0xffff02;

    public ThreadHandler() {
        super(NAME);
        setDaemon(true);
        start();
        mHandler = new MHandler(getLooper());
    }

    public String getMessageName(Message message) {
        if (mHandler == null) {
            return null;
        }
        //        if(mHandler instanceof MThreadHandler){
        //            return ((MThreadHandler)mHandler).getMessageName(message);
        //        }
        return null;
        //        return mHandler.getMessageName(message);
    }

    public final Message obtainMessage() {
        if (mHandler == null) {
            return null;
        }
        Message msg = Message.obtain(this.mHandler);
        msg.setTarget(mHandler);
        return msg;
    }

    public void setHandlerCallback(Handler mCallback) {
        this.mCallback = mCallback;
    }

    public final Message obtainMessage(int what, int type) {
        if (mHandler == null) {
            return null;
        }
        Message message = Message.obtain(this.mHandler, what);
        message.setTarget(mHandler);
        Bundle data = new Bundle();
        data.putInt(MESSAGE_TYPE, type);
        message.setData(data);
        return message;
    }

    public final Message obtainMessage(int what, int type, Object obj) {
        if (mHandler == null) {
            return null;
        }
        Message message = Message.obtain(this.mHandler, what, obj);
        message.setTarget(mHandler);
        Bundle data = new Bundle();
        data.putInt(MESSAGE_TYPE, type);
        message.setData(data);
        return message;
    }

    public final Message obtainMessage(int what, int type, int arg1, int arg2) {
        if (mHandler == null) {
            return null;
        }
        Message message = Message.obtain(this.mHandler, what, arg1, arg2);
        message.setTarget(mHandler);
        Bundle data = new Bundle();
        data.putInt(MESSAGE_TYPE, type);
        message.setData(data);
        return message;
    }

    public final Message obtainMessage(int what, int type, int arg1, int arg2, Object obj) {
        if (mHandler == null) {
            return null;
        }
        Message message = Message.obtain(this.mHandler, what, arg1, arg2, obj);
        message.setTarget(mHandler);
        Bundle data = new Bundle();
        data.putInt(MESSAGE_TYPE, type);
        message.setData(data);
        return message;
    }

    public final void post(Runnable r) {
        try {
            THREAD_POOL_EXECUTOR.execute(r);
        } catch (Exception e) {
            sPoolWorkQueue.poll();
            try {
                THREAD_POOL_EXECUTOR.execute(r);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public final boolean postAtTime(Runnable r, long uptimeMillis) {
        if (mHandler == null) {
            return false;
        }
        return mHandler.postAtTime(r, uptimeMillis);
    }

    public final boolean postAtTime(Runnable r, Object token, long uptimeMillis) {
        if (mHandler == null) {
            return false;
        }
        return mHandler.postAtTime(r, token, uptimeMillis);
    }

    public final boolean postDelayed(Runnable r, long delayMillis) {
        if (mHandler == null) {
            return false;
        }
        return mHandler.postDelayed(r, delayMillis);
    }

    public final boolean postAtFrontOfQueue(Runnable r) {
        if (mHandler == null) {
            return false;
        }
        return mHandler.postAtFrontOfQueue(r);
    }

    public final void removeCallbacks(Runnable r) {
        if (mHandler == null) {
            return;
        }
        mHandler.removeCallbacks(r);
        THREAD_POOL_EXECUTOR.remove(r);
    }

    public final void removeCallbacks(Runnable r, Object token) {
        if (mHandler == null) {
            return;
        }
        mHandler.removeCallbacks(r, token);
        THREAD_POOL_EXECUTOR.remove(r);
    }

    public final boolean sendMessage(Message msg) {
        return sendMessageDelayed(msg, 0);
    }

    public final boolean sendEmptyMessage(int what) {
        return sendEmptyMessageDelayed(what, 0);
    }

    public final boolean sendEmptyMessageDelayed(int what, long delayMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        return sendMessageDelayed(msg, delayMillis);
    }

    public final boolean sendEmptyMessageAtTime(int what, long uptimeMillis) {
        Message msg = Message.obtain();
        msg.what = what;
        return sendMessageAtTime(msg, uptimeMillis);
    }

    public final boolean sendMessageDelayed(Message msg, long delayMillis) {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        return sendMessageAtTime(msg, SystemClock.uptimeMillis() + delayMillis);
    }

    public final boolean sendMessageAtFrontOfQueue(Message msg) {
        if (mHandler == null) {
            return false;
        }
        return mHandler.sendMessageAtFrontOfQueue(msg);
    }

    public final void removeMessages(int what) {
        if (mHandler == null) {
            return;
        }
        mHandler.removeMessages(what);
    }

    public final void removeMessages(int what, Object object) {
        if (mHandler == null) {
            return;
        }
        mHandler.removeMessages(what, object);
    }

    public final void removeCallbacksAndMessages(Object token) {
        if (mHandler == null) {
            return;
        }
        mHandler.removeCallbacksAndMessages(token);
    }

    public final boolean hasCallbacks(Runnable r) {
        //        if (mHandler == null) {
        //            return false;
        //        }
        return THREAD_POOL_EXECUTOR.getQueue().contains(r);
        //        return mHandler.hasCallbacks(r);
    }

    public final boolean hasMessages(int what) {
        if (mHandler == null) {
            return false;
        }
        return mHandler.hasMessages(what);
    }

    public final boolean hasMessages(int what, Object object) {
        if (mHandler == null) {
            return false;
        }
        return mHandler.hasMessages(what, object);
    }

    public final boolean sendMessageAtTime(Message message, long delay) {
        if (mHandler == null) {
            return false;
        }
        return mHandler.sendMessageAtTime(message, delay);
    }

    public void handleThreadMessage(Message msg) {
    }

    private class MHandler extends Handler {

        public MHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void dispatchMessage(Message msg) {
            if (msg.getCallback() == null) {
                Bundle data = msg.peekData();
                if (data == null) {
                    super.dispatchMessage(msg);
                } else {
                    int type = data.getInt(MESSAGE_TYPE, REQUEST_MESSAGE);
                    if (type == REQUEST_MESSAGE) {
                        super.dispatchMessage(msg);
                    } else {
                        if (mCallback != null) {
                            if (msg.getTarget() == mCallback) {
                                msg.sendToTarget();
                            } else {
                                mCallback.sendMessage(Message.obtain(msg));
                                removeMessages(msg.what);
                            }
                        }
                    }
                }
            } else {
                THREAD_POOL_EXECUTOR.execute(msg.getCallback());
            }
        }

        @Override
        public void handleMessage(Message msg) {
            handleThreadMessage(msg);
        }

    }

}
