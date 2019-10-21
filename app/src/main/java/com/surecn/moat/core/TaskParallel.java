package com.surecn.moat.core;

import android.content.Context;

import com.surecn.moat.core.task.Task;

/**
 * User: surecn(surecn@163.com)
 * Date: 2015-08-31
 * Time: 13:11
 */
public class TaskParallel<R> extends TaskQueue<R> {

    private final static int CONCURRENT_COUNT = 3;

    TaskParallel(Context context, Task task, int priority, long delayMillis) {
        super(context, task, priority, delayMillis);
    }

}
