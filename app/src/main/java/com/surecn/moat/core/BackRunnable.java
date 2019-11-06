package com.surecn.moat.core;

import android.os.Handler;
import android.os.Message;

import com.surecn.moat.core.task.SyncUITask;
import com.surecn.moat.core.task.UITask;
import com.surecn.moat.core.task.Task;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * User: surecn(surecn@163.com)
 * Date: 2015-08-30
 * Time: 16:02
 */
public class BackRunnable implements Runnable {

    private TaskExecutor mTaskExecutor;

    private TaskSchedule mTaskSchedule;

    public BackRunnable(TaskExecutor taskExecutor, TaskSchedule taskSchedule) {
        mTaskExecutor = taskExecutor;
        mTaskSchedule = taskSchedule;
    }

    @Override
    public void run() {
        do {
            TaskRecord record = mTaskSchedule.current();
            if (record == null) {
                return;
            }
            try {
                long delay = record.getDelayMillis();
                if ((record.getTask() instanceof UITask) || (delay > 0)) {
                    mTaskExecutor.execute(mTaskSchedule);
                    return;
                } else {
                    record.getTask().run(mTaskSchedule, record.mObject);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mTaskExecutor.onError(mTaskSchedule, e);
            }
        } while (mTaskSchedule.next() != null);
    }

}