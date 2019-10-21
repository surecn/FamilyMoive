package com.surecn.moat.core;

import android.content.Context;

import com.surecn.moat.core.task.Task;

/**
 * Created by surecn on 15/8/3.
 */
public class TaskQueue<R> extends TaskPool {

    private TaskRecord mRootRecord;

    private TaskRecord mLastRecord;

    private TaskRecord mCurrentRecord;

    TaskQueue(Context context, int priority) {
        super(context, priority);
    }

    TaskQueue(Context context, Task task, int priority, long delayMillis) {
        super(context, priority);
        mRootRecord = new TaskRecord(task, delayMillis);
        mLastRecord = mRootRecord;
    }

    @Override
    public TaskPool append(Task task) {
        append(task, 0);
        return this;
    }

    @Override
    public TaskPool append(Task task, long delayMillis) {
        if (mRootRecord == null) {
            mRootRecord = new TaskRecord(task, delayMillis);
            mLastRecord = mRootRecord;
        } else {
            mLastRecord.mNextRecord = new TaskRecord(task, delayMillis);
            mLastRecord = mLastRecord.mNextRecord;
        }
        return this;
    }

    @Override
    public TaskRecord next() {
        if (mCurrentRecord == null) {
            mCurrentRecord = mRootRecord;
            return mCurrentRecord;
        }
        mCurrentRecord = mCurrentRecord.mNextRecord;
        return mCurrentRecord;
    }

    public TaskRecord current() {
        if (mCurrentRecord == null) {
            mCurrentRecord = mRootRecord;
        }
        return mCurrentRecord;
    }

    @Override
    public void reset() {
        setState(State.next);
        mCurrentRecord = null;
    }

    @Override
    public synchronized void doNext(Object result) {
        super.doNext(result);
        if (mCurrentRecord.mNextRecord != null) {
            mCurrentRecord.mNextRecord.mObject = result;
        }
    }
}
