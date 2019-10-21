package com.surecn.moat.rest;

import android.content.Context;

import com.surecn.moat.core.Moat;
import com.surecn.moat.core.task.Task;
import com.surecn.moat.core.TaskManager;
import com.surecn.moat.core.TaskPool;

/**
 * Created by surecn on 15/8/5.
 */
public class TaskSupport {

    /*package*/static TaskManager createWork(RestAdapter adapter, final MethodInfo methodInfo) {
        return Moat.with(null).next(createTask(adapter, methodInfo));
    }

    /*package*/static Task createTask(RestAdapter adapter, final MethodInfo methodInfo) {
        return new HttpTask(adapter, methodInfo) {
            @Override
            public void run(Context context, TaskPool work, Object result) {
                Object data = HttpBehavior.behavior(mAdapter, mMethodInfo);
                work.doNext(data);
            }
        };
    }

}
