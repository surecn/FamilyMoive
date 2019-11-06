package com.surecn.moat.tools.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;

import com.surecn.moat.tools.utils.SDCardUtils;

import java.io.File;

/**
 * Created by surecn on 15/8/5.
 */
public class SDcardStatus extends BroadcastReceiver {

    private boolean isExistSdcard;

    private Context mContext;

    private static SDcardStatus sSDcardStatus;

    private String mAppFolder;

    public static SDcardStatus getInstance() {
        if (sSDcardStatus == null) {
            sSDcardStatus = new SDcardStatus();
        }
        return sSDcardStatus;
    }

    private SDcardStatus() {
    }

    public void init(Context context, String appFolder) {
        mContext = context;
        mAppFolder = appFolder;
        isExistSdcard = SDCardUtils.isExistSdCard();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        intentFilter.addDataScheme("file");
        context.registerReceiver(this, intentFilter);
    }

    public void onDestory() {
        mContext.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        isExistSdcard = SDCardUtils.isExistSdCard();
    }

    public File getAppFolder() {
        if (isExistSdcard) {
            return new File(Environment.getExternalStorageDirectory().toString(), mAppFolder);
        }
        return null;
    }
}
