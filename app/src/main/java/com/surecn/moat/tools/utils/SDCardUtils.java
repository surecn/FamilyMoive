package com.surecn.moat.tools.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by surecn on 15/8/5.
 */
public class SDCardUtils {

    public static String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }

    public static boolean isExistSdCard() {
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        return sdCardExist;
    }
}
