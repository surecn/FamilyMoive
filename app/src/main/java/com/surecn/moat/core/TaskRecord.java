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

    public Object mObject;

    public boolean mNeedNotify;

    private TaskSchedule.State mNextState = TaskSchedule.State.next;

    /*package*/TaskRecord(Task task, long delayMillis) {
        mTask = task;
        mDelayMillis = delayMillis;
    }

    public void setDelayMillis(long mDelayMillis) {
        this.mDelayMillis = mDelayMillis;
    }

    /*package*/Task getTask() {
        return mTask;
    }

    /*package*/long getDelayMillis() {
        return mDelayMillis;
    }

    public TaskSchedule.State getNextState() {
        return mNextState;
    }

    public void setNextState(TaskSchedule.State nextState) {
        this.mNextState = nextState;
    }
}