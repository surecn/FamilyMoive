package com.surecn.moat.core;

/**
 * User: surecn(surecn@163.com)
 * Date: 2016-07-29
 * Time: 15:32
 */
public abstract class PriorityRunnable implements Runnable, Comparable<PriorityRunnable> {

    private int mPriority;

    private TaskPool mTaskPool;

    public PriorityRunnable(TaskPool taskPool, int priority) {
        mTaskPool = taskPool;
        mPriority = priority;
    }

    public void setActionPool(TaskPool taskPool) {
        this.mTaskPool = taskPool;
    }

    public TaskPool getActionPool() {
        return mTaskPool;
    }

    @Override
    public int compareTo(PriorityRunnable another) {
        if (this.mPriority < another.mPriority) {
            return 1;
        }
        if (this.mPriority > another.mPriority) {
            return -1;
        }
        return 0;
    }

    public int getPriority() {
        return mPriority;
    }

    public void setPriority(int priority) {
        this.mPriority = mPriority;
    }
}
