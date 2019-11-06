package com.surecn.moat.core;

import android.content.Context;

import com.surecn.moat.core.task.Task;
import com.surecn.moat.core.task.UITask;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-11-29
 * Time: 13:42
 */
public class TaskExecutor {

    private MainHandler mMainHandler;

    private BackHandler mBackHandler;

    private ErrorInterceptor mErrorInterceptor;

    /*package*/TaskExecutor() {
        mMainHandler = MainHandler.getInstance();
        mBackHandler = BackHandler.getInstance();
    }

    public void onError(TaskSchedule taskSchedule, Throwable e) {
        mMainHandler.performErrorTask(this, taskSchedule, e);
    }

    public void executeNext(TaskSchedule taskSchedule) {
        if (taskSchedule.hasNext()) {
            taskSchedule.next();
        }
        execute(taskSchedule);
    }

    public void execute(TaskSchedule taskSchedule) {
        TaskRecord taskRecord = taskSchedule.current();
        if (taskRecord == null) {
            return;
        }
        if (taskRecord.getTask() instanceof UITask) {
            mMainHandler.performDelayExecute(this, taskSchedule, taskRecord.getDelayMillis());
        } else if (taskRecord.getDelayMillis() > 0) {
            mMainHandler.performDelayNotify(this, taskSchedule, taskRecord.getDelayMillis());
        } else {
            mBackHandler.execute(this, taskSchedule);
        }
    }

}
