package com.surecn.moat.core;

import android.os.Message;

/**
 * User: surecn(surecn@163.com)
 * Date: 2015-08-30
 * Time: 16:02
 */

/*package*/class ConcurrentRunnable implements Runnable, Comparable<ConcurrentRunnable> {

    private TaskParallel mActionPool;

    private int mPriority;

    private MainHandler mHandler;

    /*package*/ConcurrentRunnable(MainHandler handler, TaskParallel actionPool, int priority) {
        mHandler = handler;
        mActionPool = actionPool;
        mPriority = priority;
    }

    /*package*/int getPriority() {
        return mPriority;
    }

    @Override
    public void run() {
        TaskQueue.State state = mActionPool.getCurrentState();
        switch (state) {
            case error:{
                Message msg = mHandler.obtainMessage(MainHandler.OBSERVER_ERROR);
                msg.obj = mActionPool;
                msg.sendToTarget();
                break;}
            case next:{
                Message msg = mHandler.obtainMessage(MainHandler.OBSERVER_COMPLETE);
                msg.obj = mActionPool;
                msg.sendToTarget();
                break;}
            default:{
                Message msg = mHandler.obtainMessage(MainHandler.OBSERVER_FINALLY);
                msg.obj = mActionPool;
                msg.sendToTarget();
                break;}
        }
    }

    @Override
    public int compareTo(ConcurrentRunnable another) {
        if (this.getPriority() < another.getPriority()) {
            return 1;
        }
        if (this.getPriority() > another.getPriority()) {
            return -1;
        }
        return 0;
    }
};