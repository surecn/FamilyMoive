package com.surecn.familymovie;


import com.surecn.familymovie.FMApplication;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * Created by surecn on 15/7/10.
 */
public class UserTrack {

    public final static boolean ENABLED_USERTRACK = true;

    public static void mark(String tag) {
        if (ENABLED_USERTRACK) {
            MobclickAgent.onEvent(FMApplication.getApplication(), tag);
        }
    }

    public static void mark(String tag, String type) {
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("type",type);
        map.put("quantity","1");
        MobclickAgent.onEvent(FMApplication.getApplication(), tag, map);
    }
}