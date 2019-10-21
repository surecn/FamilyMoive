package com.surecn.moat.core;

import android.os.Handler;
import android.os.Message;

import com.surecn.moat.core.task.SyncUITask;
import com.surecn.moat.core.task.UITask;
import com.surecn.moat.core.task.Task;

/**
 * User: surecn(surecn@163.com)
 * Date: 2015-08-30
 * Time: 16:02
 */
public class QueueRunnable extends PriorityRunnable {

    private MainHandler mMainHandler;

    /*package*/QueueRunnable(MainHandler mainHandler, TaskPool taskPool, int priority) {
        super(taskPool, priority);
        mMainHandler = mainHandler;
    }

    @Override
    public void run() {
        TaskPool taskPool = getActionPool();
        try {
            TaskRecord record = null;
            TaskRecord oldRecord = null;
            while (true) {
                switch (taskPool.getCurrentState()) {
                    case next:
                        record = taskPool.next();
                        break;
                    case repeat: {
                        record = taskPool.current();
                        int delay = taskPool.getDelay();
                        if (delay > 0) {
                            Thread.sleep(taskPool.getDelay());
                        }
                        break;
                    }
                    case cancel:{
                        return;
                    }
                }
                if (record == null) {
                    break;
                }
                long delay = record.getDelayMillis();
                if (delay > 0) {
                    Thread.sleep(delay);
                }
                record.setActionPool(taskPool);
                Task task = record.getAction();
                if (task instanceof SyncUITask) {
                    mMainHandler.sendSyncMainAction(record);
                    record.mNeedNotify = true;
                    synchronized (taskPool) {
                        taskPool.wait();
                    }
                } else if (task instanceof UITask) {
                    mMainHandler.sendAsyncMainAction(record);
                } else if (task instanceof Task) {
                    Object obj = oldRecord != null ? oldRecord.mObject : null;
                    task.run(taskPool.getContext(), taskPool, obj);
                } else {
                    break;
                }
                oldRecord = record;
            }

            TaskQueue.State state = taskPool.getCurrentState();
            switch (state) {
                case error:{
                    sendMessage(mMainHandler, MainHandler.OBSERVER_ERROR, taskPool);
                    break;}
                case repeat:
                case next:{
                    if (oldRecord != null) {
                        taskPool.setResult(oldRecord.mObject);
                    }
                    sendMessage(mMainHandler, MainHandler.OBSERVER_COMPLETE, taskPool);
                    break;}
                default:{
                    sendMessage(mMainHandler, MainHandler.OBSERVER_FINALLY, taskPool);
                    break;}
            }
        } catch (Exception e) {
            e.printStackTrace();
            taskPool.setThrowable(e);
            sendMessage(mMainHandler, MainHandler.OBSERVER_ERROR, taskPool);
        }
    }

    private void sendMessage(Handler handler, int what, Object obj) {
        Message msg = handler.obtainMessage();
        msg.what = what;
        msg.obj = obj;
        msg.sendToTarget();
    }
}