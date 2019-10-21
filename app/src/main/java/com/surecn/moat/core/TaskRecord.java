package com.surecn.moat.core;

import com.surecn.moat.core.task.Task;

/**
 * User: surecn(surecn@163.com)
 * Date: 2015-08-30
 * Time: 12:08
 */
public class TaskRecord {

    private Task mTask;

    private long mDelayMillis;

    private TaskPool mTaskPool;

    public TaskRecord mNextRecord;

    public Object mObject;

    public boolean mNeedNotify;

    /*package*/TaskRecord(Task task, long delayMillis) {
        mTask = task;
        mDelayMillis = delayMillis;
    }

    /*package*/Task getAction() {
        return mTask;
    }

    /*package*/long getDelayMillis() {
        return mDelayMillis;
    }

    public TaskPool getActionPool() {
        return mTaskPool;
    }

    public void setActionPool(TaskPool taskPool) {
        this.mTaskPool = taskPool;
    }
}