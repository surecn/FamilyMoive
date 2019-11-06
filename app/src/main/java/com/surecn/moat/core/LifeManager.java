package com.surecn.moat.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-11-29
 * Time: 16:02
 */
public class LifeManager implements Application.ActivityLifecycleCallbacks {

    private static LifeManager sLifeManager;

    public static LifeManager getLifeManager() {
        if (sLifeManager == null) {
            sLifeManager = new LifeManager();
        }
        return sLifeManager;
    }

    private HashMap<Context, List<TaskSchedule>> mTaskList = new HashMap<>();

    private boolean mIsInit = false;

    public void init(Application application) {
        application.registerActivityLifecycleCallbacks(this);
        mIsInit = true;
    }

    public void watch(TaskSchedule taskPool) {
        if (!mIsInit) {
            throw new RuntimeException("LifeManager need init");
        }
        Context context = taskPool.getContext();
        if (context == null) {
            return;
        }
        if (context instanceof Activity) {
            List<TaskSchedule> taskPools = mTaskList.get(context);
            if (taskPools == null) {
                taskPools = new ArrayList<>();
                mTaskList.put(context, taskPools);
            }
            taskPools.add(taskPool);
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        List<TaskSchedule> list = mTaskList.get(activity);
        if (list == null || list.size() <= 0) {
            return;
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            TaskSchedule taskSchedule = list.get(i);
            taskSchedule.cancel();
            list.remove(i);
        }
    }
}
