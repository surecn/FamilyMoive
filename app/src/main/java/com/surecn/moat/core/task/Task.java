package com.surecn.moat.core.task;

import android.content.Context;

import com.surecn.moat.core.TaskPool;

/**
 * Created by surecn on 15/8/3.
 */
public interface Task<T> {
    public void run(Context context, TaskPool work, T result);
}
