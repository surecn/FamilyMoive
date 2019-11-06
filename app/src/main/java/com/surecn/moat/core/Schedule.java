package com.surecn.moat.core;

import android.app.Application;
import android.content.Context;

import com.surecn.moat.core.task.Task;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-11-29
 * Time: 09:24
 */
public class Schedule {

    private ErrorInterceptor mErrorInterceptor;

    private static Context mContext;

    public static LinearSchedule linear(Task task) {
        LinearSchedule linearSchedule = new LinearSchedule(mContext);
        linearSchedule.next(task);
        return linearSchedule;
    }

    public static LinearSchedule linear(Task task, long delay) {
        LinearSchedule linearSchedule = new LinearSchedule(mContext);
        linearSchedule.next(task, delay);
        return linearSchedule;
    }

    /**
     * 初始化
     * @param application
     */
    public static void init(Application application) {
        mContext = application.getApplicationContext();
        LifeManager.getLifeManager().init(application);
    }

    /**
     * 设置错误拦截器
     * @return
     */
    public void setErrorInterceptor(ErrorInterceptor errorInterceptor) {
        MainHandler.getInstance().setErrorInterceptor(errorInterceptor);
    }

}
