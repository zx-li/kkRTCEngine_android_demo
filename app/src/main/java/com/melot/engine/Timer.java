package com.melot.engine;

import android.os.Handler;
import android.os.Message;


public class Timer extends Handler {
    private OnTimeListener mTimeListener;
    private int mElapsed;
    private boolean mIsRepeat;
    public Timer(int elapse, boolean isRepeat, OnTimeListener l) {
        mElapsed = elapse;
        mIsRepeat = isRepeat;
        mTimeListener = l;

        removeMessages(What.RUNNING);
        Message msg = obtainMessage(What.RUNNING);
        sendMessageDelayed(msg, mElapsed);
    }

    public void pause() {
        removeMessages(What.RUNNING);
    }

    public void resume() {
        removeMessages(What.RUNNING);
        Message msg = obtainMessage(What.RUNNING);
        sendMessageDelayed(msg, mElapsed);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        switch (msg.what) {
            case What.RUNNING: {
                msg = obtainMessage(What.RUNNING);

                if (mTimeListener != null) {
                    mTimeListener.onTime(this);
                }

                if (mIsRepeat) {
                    sendMessageDelayed(msg, mElapsed);
                }
            }
            break;
            case What.STEPRUNNING: {
                if (mTimeListener != null) {
                    mTimeListener.onTime(this);
                }
                break;
            }

            default:
                break;
        }
    }

    interface OnTimeListener {
        public void onTime(Timer timer);
    }

    public static class What {
        public final static int RUNNING = 1;
        public final static int PAUSE = 11;
        public final static int RESUME = 12;
        public final static int STEPRUNNING = 2;
    }
}