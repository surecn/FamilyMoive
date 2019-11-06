package com.surecn.moat.core;

import android.content.Context;

import com.surecn.moat.core.task.Task;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-03
 * Time: 10:00
 */
public class LinearSchedule extends TaskSchedule {

    private TaskExecutor mLinearExecutor;

    public LinearSchedule(Context context) {
        super(context);
    }

    public LinearSchedule next(Task task) {
        return next(task, 0);
    }

    public LinearSchedule next(Task task, long delayMillis) {
        append(task, 0);
        return this;
    }

    @Override
    public void start() {
        mLinearExecutor = new TaskExecutor();
        mLinearExecutor.execute(this);
        super.start();
    }
}
