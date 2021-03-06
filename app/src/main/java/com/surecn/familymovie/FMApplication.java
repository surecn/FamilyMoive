package com.surecn.familymovie;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.dangbei.euthenia.manager.DangbeiAdManager;
import com.surecn.familymovie.common.StreamService;
import com.surecn.familymovie.data.HttpAdapter;
import com.surecn.moat.core.LinearSchedule;
import com.surecn.moat.core.Schedule;
import com.surecn.moat.core.TaskSchedule;
import com.surecn.moat.core.task.UITask;
import com.surecn.moat.sqliteadmin.SQLiteAdmin;
import com.surecn.moat.tools.log;
import com.surecn.moat.tools.setting.SettingManager;
import com.umeng.commonsdk.UMConfigure;

import java.util.Properties;

import jcifs.CIFSException;
import jcifs.context.SingletonContext;

import static com.surecn.familymovie.BuildConfig.DEBUG;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-13
 * Time: 19:35
 */
public class FMApplication extends Application {

    private static FMApplication sFMApplication;

    public static FMApplication getApplication() {
        return sFMApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sFMApplication = this;
        log.init(this, DEBUG);

        if (!getCurProcessName(this).equals(getPackageName())) {
            return;
        }
        SettingManager.getInstance(this).registerSetting(Setting.class);

        Schedule.init(this);

        if (DEBUG) {
            SQLiteAdmin.with(this).init(8000);
        }

//        Schedule.linear(new UITask() {
//            @Override
//            public void run(TaskSchedule taskSchedule, Object result) {
//                Properties prop = new Properties();
//                prop.put( "jcifs.smb.client.enableSMB1", "true");
//                prop.put( "jcifs.smb.client.disableSMB2", "false");
//                prop.put( "jcifs.smb.client.ipcSigningEnforced", "false");
//                prop.put( "jcifs.traceResources", "true" );
//                try {
//                    SingletonContext.init(prop);
//                } catch (CIFSException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();

        /*http请求接口初始化*/
        HttpAdapter.init(this);

        /*smb转http服务*/
        startService(new Intent(this, StreamService.class));

        UMConfigure.init(this, "5dc2580f570df3adbb000688", BuildConfig.CHANNEL, UMConfigure.DEVICE_TYPE_BOX, null);

//        DangbeiAdManager.init(this, "E2JO2Ti5FAvMnZ88bEG8GMm0APEs9MlwyHuMpQpV22CVWOMP", "7Q7d3799KVlgG05t", BuildConfig.CHANNEL);
    }

    private String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
