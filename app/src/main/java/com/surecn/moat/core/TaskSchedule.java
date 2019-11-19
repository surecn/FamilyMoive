package com.surecn.moat.core;

import android.content.Context;

import com.surecn.moat.core.task.Task;

import java.util.LinkedList;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-03
 * Time: 09:31
 */
public class TaskSchedule<R> {

    public enum State{
        next,
        error,
        cancel,
        repeat
    }

    private LinkedList<TaskRecord> mTaskList;

    private State mState = State.next;

    private Context mContext;

    public TaskSchedule(Context context) {
        mContext = context;
        mTaskList = new LinkedList<>();
    }

    public Context getContext() {
        return mContext;
    }

    /*package*/synchronized State getCurrentState() {
        return this.mState;
    }

    /*package*/synchronized State setCurrentState(State state) {
        return this.mState = state;
    }


    /**
     * 设置该任务执行成功结果
     * @param result
     */
    public synchronized void sendNext(R result) {
        if (mState == State.repeat) {
            mTaskList.get(0).mObject = result;
        } if (mTaskList.size() > 1) {
            mTaskList.get(1).mObject = result;
        }
    }

    public void repeat() {
        mTaskList.get(0).setNextState(State.repeat);
    }

    public void cancel() {
        mTaskList.get(0).setNextState(State.cancel);
    }

    protected TaskSchedule append(Task task, long delayMillis) {
        mTaskList.add(new TaskRecord(task, delayMillis));
        return this;
    }

    protected TaskRecord current() {
        return mTaskList.peekFirst();
    }

    protected boolean hasNext() {
        return mTaskList.size() > 0;
    }

    protected TaskRecord next() {
        switch (getCurrentState()) {
            case next:
                mTaskList.pollFirst();
                return current();
            case repeat: {
                return current();
            }
            case cancel: {
                onCancel();
                return null;
            }
        }
        mTaskList.pollFirst();
        return current();
    }

    private void onCancel() {
        mTaskList.clear();
    }

    public void start() {

    }
}
