package com.surecn.moat.core;

import android.content.Context;

import com.surecn.moat.core.task.Task;

import java.util.LinkedList;

/**
 * Created by surecn on 15/8/3.
 */
public abstract class TaskPool<R> {

    private String mName;

    protected LinkedList<TaskRecord> mTaskList;

    private ResultObserver mTaskObserver;

    private ProcessObserver mProcessObserver;

    private Exception mThrowable;

    private Object mResult;

    private State mState = State.next;

    private boolean mDefaultErrorHandler = true;

    private boolean mStart = false;

    private int mPriority;

    private int mDelay = 0;

    private int mRepeatCount = 0;

    private Context mContext;

    /*package*/enum State{
        next,
        error,
        cancel,
        repeat
    }

    /*package*/TaskPool(Context context, int priority) {
        mContext = context;
        mPriority = priority;
    }

    /*package*/Context getContext() {return mContext;}

    /*package*/synchronized State getCurrentState() {
        return mState;
    }

    /*package*/void setThrowable(Exception throwable) {
        mThrowable = throwable;
    }

    /*package*/Throwable getThrowable() {
        return mThrowable;
    }

    /*package*/ResultObserver getTaskObserver() {
        return mTaskObserver;
    }

    /*package*/ProcessObserver getProcessObserver() {
        return mProcessObserver;
    }

    /*package*/Object getResult() {
        return mResult;
    }

    /*package*/void setResult(Object object) {
        mResult = object;
    }

    /*package*/boolean isDefaultErrorHandler() {
        return mDefaultErrorHandler;
    }

    /*package*/void setResultObserver(ResultObserver resultObserver){mTaskObserver = resultObserver;}

    /*package*/void setProcessObserver(ProcessObserver processObserver){mProcessObserver = processObserver;}

    public boolean isStart() {
        return mStart;
    }

    /*package*/ void setStart(boolean start) {
        mStart = start;
    }

    public synchronized void cancel() {
        mState = State.cancel;
    }

    /**
     * 设置该任务执行成功结果
     * @param result
     */
    public synchronized void doNext(R result) {
        if (mState != State.repeat)
            mState = State.next;
        current().mObject = result;
    }

    /**
     * 任务流增加一个任务
     * @param task
     * @return
     */
    public abstract TaskPool append(Task task);

    public abstract TaskPool append(Task task, long delayMillis);

    public abstract void reset();

    public synchronized void setState(State state) {
        mState = state;
    }

    /**
     * 设置该任务执行错误的异常
     * @param throwable
     */
    public synchronized void onError(Throwable throwable) {
        mState = State.error;
    }

    /**
     * 设置该任务流是否调用统一错误处理
     * @param flag
     * @return
     */
    public void setDefaultErrorHandler(boolean flag) {
        mDefaultErrorHandler = flag;
    }

    /**
     * 该任务优先级
     * @return
     */
    public int getPriority() {
        return mPriority;
    }

//    public void runAsyncAction(UITask action) {
//        runAsyncAction(action, 0);
//    }
//
//    public void runAsyncAction(Task action, int delay) {
//        TaskManager.getInstance().sendAsyncMainAction(new TaskRecord(action, delay));
//    }
//
//    public void runSyncAction(Task action) {
//        runAsyncAction(action, 0);
//    }
//
//    public void runAsyncAction(Task action, int delay) {
//        TaskManager.getInstance().sendAsyncMainAction(new TaskRecord(action, delay));
//    }

    public abstract TaskRecord next();

    public abstract TaskRecord current();

    public int getDelay() {
        return mDelay;
    }

    /*package*/ void repeat(int delay) {
        mDelay = delay;
        mState = State.repeat;
    }

    boolean repeat() {
        mRepeatCount++;
        reset();
        return mState == State.repeat;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public TaskPool priority(int priority) {
        mPriority = priority;
        return this;
    }
}
