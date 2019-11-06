package com.surecn.familymoive;

import android.app.Application;
import android.content.Intent;

import com.surecn.familymoive.common.StreamService;
import com.surecn.familymoive.data.HttpAdapter;
import com.surecn.moat.core.Schedule;
import com.surecn.moat.sqliteadmin.SQLiteAdmin;
import com.surecn.moat.tools.log;
import com.surecn.moat.tools.setting.SettingManager;
import com.umeng.commonsdk.UMConfigure;

import static com.surecn.familymoive.BuildConfig.DEBUG;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-13
 * Time: 19:35
 */
public class FMApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        log.init(this, DEBUG);

        SettingManager.getInstance(this).registerSetting(Setting.class);

        Schedule.init(this);

        if (DEBUG) {
            SQLiteAdmin.with(this).init(8000);
        }

        /*http请求接口初始化*/
        HttpAdapter.init(this);

        /*smb转http服务*/
        startService(new Intent(this, StreamService.class));

        UMConfigure.init(this, "5dc2580f570df3adbb000688", BuildConfig.CHANNEL, UMConfigure.DEVICE_TYPE_BOX, null);
    }
}
