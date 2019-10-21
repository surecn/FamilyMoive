package com.surecn.moat.core;

import android.content.Context;

import com.surecn.moat.core.task.Task;

/**
 * Created by surecn on 15/8/3.
 */
public class TaskManager {

    private TaskPool mTaskPool;

    private TaskExecutor mTaskExecutor;

    /*package*/ TaskManager(Context context) {
        mTaskPool = new TaskQueue(context, 0);
        mTaskExecutor = TaskExecutor.getTaskExecutor();
        LifeManager.getLifeManager().watch(mTaskPool);
    }

    /**
     * 设置监听,设置后回立即执行任务流,一个任务流只能有一个Observer
     * @param observer
     */
    public void observ(Observer observer){
        mTaskPool.setResultObserver(observer);
        mTaskPool.setProcessObserver(observer);
        mTaskPool.setStart(true);
        mTaskExecutor.start(mTaskPool, mTaskPool.getDelay());
    }

    /**
     * 设置监听,设置后回立即执行任务流,一个任务流只能有一个Observer
     * @param taskObserver
     */
    public void observ(ResultObserver taskObserver, ProcessObserver processObserver){
        mTaskPool.setResultObserver(taskObserver);
        mTaskPool.setProcessObserver(processObserver);
        mTaskPool.setStart(true);
        mTaskExecutor.start(mTaskPool, mTaskPool.getDelay());
    }

    /**
     * 执行任务流
     */
    public void start() {
        mTaskPool.setStart(true);
        mTaskExecutor.start(mTaskPool, 0);
    }

    /**
     * 执行任务流
     */
    public void start(int delay) {
        mTaskPool.setStart(true);
        mTaskExecutor.start(mTaskPool, delay);
    }

    public TaskManager next(Task task) {
        next(task, 0);
        return this;
    }

    public TaskManager next(Task task, long delayMillis) {
        mTaskPool.append(task, delayMillis);
        return this;
    }

}
