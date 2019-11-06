package com.surecn.moat.core;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-03
 * Time: 10:44
 */
public class BackHandler {

    private ThreadPoolExecutor mPoolExecutor;

    private static BackHandler sBackHandler;

    public static BackHandler getInstance() {
        if (sBackHandler == null) {
            sBackHandler = new BackHandler();
        }
        return sBackHandler;
    }

    private BackHandler() {
        int threadCount = 5;
        mPoolExecutor = new ThreadPoolExecutor(threadCount, threadCount * 3, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    public void execute(TaskExecutor taskExecutor, TaskSchedule taskSchedule) {
        mPoolExecutor.execute(new BackRunnable(taskExecutor, taskSchedule));
    }

}
