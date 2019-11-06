package com.surecn.moat.tools.monitor;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by surecn on 15/8/10.
 */
public class MemoryStatus {

    Context mContext;

    private static MemoryStatus sMemoryStatus;

    long mTotalMemory;

    public static MemoryStatus getInstance() {
        if (sMemoryStatus == null) {
            sMemoryStatus = new MemoryStatus();
        }
        return sMemoryStatus;
    }

    private MemoryStatus() {
    }

    public void init(Context context) {
        mContext = context;
        mTotalMemory = loadTotalMemory();
    }

    public long getAvailMemory() {// 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem;
    }

    public long getTotalMemory() {
        if (mTotalMemory == 0) {
            mTotalMemory = loadTotalMemory();
        }
        return mTotalMemory;
    }

    private long loadTotalMemory() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }

            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return initial_memory; // Byte转换为KB或者MB，内存大小规格化
    }
}
