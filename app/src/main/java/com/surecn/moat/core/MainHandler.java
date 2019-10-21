package com.surecn.moat.core;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-11-29
 * Time: 13:43
 */
class MainHandler extends Handler {

    /*package*/final static int ACTION_PRE = 1000;

    /*package*/final static int ACTION_EXECUTE = 1001;

    /*package*/final static int OBSERVER_COMPLETE = 1002;

    /*package*/final static int OBSERVER_ERROR = 1003;

    /*package*/final static int OBSERVER_FINALLY = 1004;

    /*package*/final static int SYNC_ACTION = 1008;

    /*package*/final static int ASYNC_ACTION = 1009;

    private ErrorInterceptor mErrorInterceptor;

    private TaskExecutor mTaskExecutor;


    /*package*/ MainHandler(TaskExecutor taskExecutor) {
        super(Looper.getMainLooper());
        this.mTaskExecutor = taskExecutor;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case ACTION_PRE: {
                mTaskExecutor.doPre((TaskPool) msg.obj);
                break;
            }
            case ACTION_EXECUTE: {
                mTaskExecutor.executeRunable((TaskPool) msg.obj);
                break;
            }
            case OBSERVER_COMPLETE: {
                TaskQueue actionQueue = (TaskQueue) msg.obj;
                mTaskExecutor.executeComplete(actionQueue);
                restart(actionQueue);
                break;
            }
            case OBSERVER_ERROR: {
                TaskQueue actionQueue = (TaskQueue) msg.obj;
                mTaskExecutor.executeError(actionQueue);
                restart(actionQueue);
                break;
            }
            case OBSERVER_FINALLY: {
                TaskQueue actionQueue = (TaskQueue) msg.obj;
                final ProcessObserver observer = actionQueue.getProcessObserver();
                if (observer != null) {
                    observer.onFinally();
                }
                break;
            }
            case ASYNC_ACTION: {
                mTaskExecutor.executeAsyncAction((TaskRecord) msg.obj);
                break;
            }
            case SYNC_ACTION: {
                mTaskExecutor.executeSyncAction((TaskRecord) msg.obj);
                break;
            }
            default:
                break;
        }
    }

    private void restart(TaskPool taskPool) {
        if (taskPool.repeat()) {
            mTaskExecutor.reStart(taskPool, 0);
        } else {
            mTaskExecutor.executeFinally(taskPool);
        }
    }


    public void sendPreTaskMessage(TaskPool pool, int delay) {
        Message msg = obtainMessage(ACTION_PRE);
        msg.obj = pool;
        msg.setTarget(this);
        this.sendMessageDelayed(msg, delay);
    }

    public void sendExecuteMessage(TaskPool pool) {
        sendExecuteMessage(pool, 0);
    }

    public void sendErrorMessage(TaskPool pool) {
        Message errorMsg = obtainMessage(OBSERVER_ERROR);
        errorMsg.obj = pool;
        errorMsg.sendToTarget();
    }

    public void sendExecuteMessage(TaskPool pool, int delay) {
        Message msg = obtainMessage(ACTION_EXECUTE);
        msg.obj = pool;
        if (delay > 0) {
            msg.setTarget(this);
            sendMessageDelayed(msg, delay);
        } else {
            msg.sendToTarget();
        }
    }

    public void sendAsyncMainAction(TaskRecord record) {
        Message errorMsg = obtainMessage(ASYNC_ACTION);
        errorMsg.obj = record;
        errorMsg.sendToTarget();
    }

    public void sendSyncMainAction(TaskRecord record) {
        Message errorMsg = obtainMessage(SYNC_ACTION);
        errorMsg.obj = record;
        errorMsg.sendToTarget();
    }
};
