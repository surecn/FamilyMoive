package com.surecn.moat.tools.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 进程工具类
 */
public class ProcessUtils {

    // 5~10ms spent on nexus 4, slow device may spent 20ms
    // this api work on android, depends system_activity_service
    public static String getProcessName(Context context, int pid) {
        String str = null;
        ActivityManager am = null;
        {
            Object obj = context.getSystemService(Context.ACTIVITY_SERVICE);
            if (obj != null) {
                try {
                    am = (ActivityManager) obj;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        if (am == null)
            return null;
        List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
        if (list != null && list.size() > 0) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                RunningAppProcessInfo info = list.get(i);
                if (info != null && info.pid == pid) {
                    str = info.processName;
                    break;
                }
            }
        }
        return str;
    }

    // fastest way to get processName
    //0-2ms spent
    // this api work well on linux
    public static String getProcessName(int pid) {
        String line = "/proc/" + pid + "/cmdline";
        FileInputStream fis = null;
        String processName = null;
        byte[] bytes = new byte[512];
        int read = 0;
        try {
            fis = new FileInputStream(line);
            read = fis.read(bytes);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (fis != null) {
            try {
                fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (read > 0) {
            //find first \0
            int length = 0;
            for (int i = 0; i < read; i++) {
                if (bytes[i] == '\0') {
                    length = i;
                    break;
                }
            }
            processName = new String(bytes, 0, length);
            processName = processName.trim();

        }
        return processName;
    }

    /**
     * this function using system_core to keep always right ! using 0-1 ms ,but it is slower then AstApp.isFront
     *
     * @param pid , process id of  app
     * @return is app front or not, if you want more using getProcessAdj
     */
    public static boolean isAppFront(int pid) {
        int adj = getProcessAdj(pid);
        return (adj > 1) ? false : true;
    }

    /**
     * -16 ,SYSTEM_ADJ   linuix core
     * -12, PERSISTENT_PROC_ADJ , android core system app
     * 0  , FOREGROUND_APP_ADJ  , android front app
     * 1  , VISIBLE_APP_ADJ     , visible app
     * 2  , PERCENTIBLE_APP_ADJ , pausing  activity or foreground service
     * 4  , backup app
     * 5  , service
     * 6  ,  home
     * 7  ,  last activity app
     * 8  ,  not running service
     *
     * @param pid process id of  app
     * @return
     */
    public static int getProcessAdj(int pid) {
        String line = "/proc/" + pid + "/oom_adj";
        FileInputStream fis = null;
        int adj = 100;
        byte[] bytes = new byte[128];
        int read = 0;
        try {
            fis = new FileInputStream(line);
            read = fis.read(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fis != null) {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (read > 0) {
            String processName = new String(bytes, 0, read);
            processName = processName.trim();
            adj = Integer.parseInt(processName);
        }
        return adj;
    }

    public static int[] getProcessThreads(int pid) {
        String line = "/proc/" + pid + "/task";
        int[] threads = null;
        File task = new File(line);
        String[] strs = task.list();
        ArrayList<Integer> list = new ArrayList<Integer>();
        if (strs != null) {
            for (int i = 0; i < strs.length; i++) {
                if (strs[i] != null) {
                    try {
                        int id = Integer.parseInt(strs[i]);
                        list.add(id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        int size = list.size();
        if (size > 0) {
            threads = new int[size];
            for (int i = 0; i < size; i++) {
                threads[i] = list.get(i);
            }
        }
        return threads;
    }

}