package com.surecn.familymoive;

import android.app.Application;

import com.surecn.moat.core.Moat;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-13
 * Time: 19:35
 */
public class FMApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Moat.init(this);
    }
}
