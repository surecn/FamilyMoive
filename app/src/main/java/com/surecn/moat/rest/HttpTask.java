package com.surecn.moat.rest;

import android.content.Context;

import com.surecn.moat.core.task.Task;
import com.surecn.moat.core.TaskPool;

/**
 * Created by surecn on 15/8/4.
 * 在TaskFlow中执行
 */
/*package*/class HttpTask implements Task {

    protected MethodInfo mMethodInfo;

    protected RestAdapter mAdapter;

    public HttpTask(RestAdapter adapter, MethodInfo methodInfo) {
        mMethodInfo = methodInfo;
        mAdapter = adapter;
    }


    @Override
    public void run(Context context, TaskPool work, Object result) {

    }
}
