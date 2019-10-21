package com.surecn.moat.core;

import android.app.Application;
import android.content.Context;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-11-29
 * Time: 09:24
 */
public class Moat {

    private ErrorInterceptor mErrorInterceptor;

    private static Context mContext;

    public static TaskManager with(Context context) {
        if (context == null) {
            context = mContext;
        }
        return new TaskManager(context);
    }

    /**
     * 初始化
     * @param application
     */
    public static void init(Application application) {
        mContext = application.getApplicationContext();
        LifeManager.getLifeManager().init(application);
        TaskExecutor.getTaskExecutor();
    }

    /**
     * 设置错误拦截器
     * @return
     */
    public void setErrorInterceptor(ErrorInterceptor errorInterceptor) {
        TaskExecutor.getTaskExecutor().setErrorInterceptor(errorInterceptor);
    }

}
