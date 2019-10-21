package com.surecn.moat.core;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-11-29
 * Time: 13:42
 */
public class TaskExecutor {

    private MainHandler mMainHandler = null;

    private ThreadPoolExecutor mPoolExecutor;

    private ErrorInterceptor mErrorInterceptor;

    private static TaskExecutor sTaskExecutor;

    private final static int THREAD_COUNT = 5;

    public static TaskExecutor getTaskExecutor() {
        if (sTaskExecutor == null) {
            sTaskExecutor = new TaskExecutor();
            sTaskExecutor.init(THREAD_COUNT);
        }
        return sTaskExecutor;
    }

    /*package*/TaskExecutor() {
        mMainHandler = new MainHandler(this);
    }

    /*package*/void init(int threadCount) {
        mPoolExecutor = new ThreadPoolExecutor(threadCount, threadCount * 3, 0, TimeUnit.MILLISECONDS,new PriorityBlockingQueue<Runnable>());
    }

    /*package*/ void setErrorInterceptor(ErrorInterceptor errorInterceptor) {
        mErrorInterceptor = errorInterceptor;
    }

    /*package*/ void doPre(TaskPool pool) {
        if (pool.getCurrentState() == TaskPool.State.cancel) {
            return;
        }
        final ProcessObserver observer = pool.getProcessObserver();
        if (observer != null) {
            observer.onPre();
        }
        mMainHandler.sendExecuteMessage(pool);
    }

    /*package*/ void executeRunable(TaskPool pool) {
        if (pool instanceof TaskQueue) {
            QueueRunnable runnable = new QueueRunnable(mMainHandler, pool, pool.getPriority());
            if (mPoolExecutor == null) {
                throw new RuntimeException("TaskManager not init");
            }
            if (pool.getCurrentState() == TaskPool.State.cancel) {
                return;
            }
            mPoolExecutor.execute(runnable);
        }
    }

    /*package*/ void executeAsyncAction(TaskRecord record) {
        try {
            record.getAction().run(record.getActionPool().getContext(), record.getActionPool(), record.mObject);
        } catch (Exception e) {
            e.printStackTrace();
            mMainHandler.sendErrorMessage(record.getActionPool());
        }
    }

    /*package*/ void executeSyncAction(TaskRecord record) {
        TaskPool pool = record.getActionPool();
        try {
            record.getAction().run(pool.getContext(), record.getActionPool(), record.mObject);
        } catch (Exception e) {
            e.printStackTrace();
            mMainHandler.sendErrorMessage(record.getActionPool());
        } finally {
            if (record.mNeedNotify) {
                record.mNeedNotify = false;
                TaskPool taskPool = record.getActionPool();
                if (taskPool == null) {
                    return;
                }
                try {
                    synchronized (taskPool) {
                        taskPool.notifyAll();
                    }
                } catch (Exception e){}
            }
        }
    }

    /*package*/ void executeComplete(TaskPool taskPool) {
        final ResultObserver observer = taskPool.getTaskObserver();
        if (observer != null) {
            observer.onComplete(taskPool, taskPool.getResult());
        }
    }

    /*package*/ void executeFinally(TaskPool actionQueue) {
        final ProcessObserver observer = actionQueue.getProcessObserver();
        if (observer != null) {
            observer.onFinally();
        }

    }

    /*package*/ void executeError(TaskPool actionQueue) {
        final ResultObserver observer = actionQueue.getTaskObserver();
        if (observer != null) {
            if (actionQueue.isDefaultErrorHandler() &&
                    mErrorInterceptor != null) {
                if (!mErrorInterceptor.interceptor(actionQueue.getThrowable())) {
                    observer.onError(actionQueue, actionQueue.getThrowable());
                }
            } else {
                observer.onError(actionQueue, actionQueue.getThrowable());
            }
        }
    }

    /*package*/void start(TaskPool pool, int delay) {
        mMainHandler.sendPreTaskMessage(pool, delay);
    }

    /*package*/void reStart(TaskPool pool, int delay) {
        mMainHandler.sendExecuteMessage(pool, delay);
    }

}
