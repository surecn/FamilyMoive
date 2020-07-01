package com.surecn.familymovie;


import com.umeng.analytics.MobclickAgent;
import java.util.HashMap;

/**
 * Created by surecn on 15/7/10.
 */
public class UserTrack {

    public final static boolean ENABLED_USERTRACK = true;

    public final static String MAIN_HISTORY = "Aa";

    public final static String MAIN_LOCAL = "Ab";

    public final static String MAIN_LAN = "Ac";

    public final static String MAIN_LIVE = "Ad";

    public final static String MAIN_FAVORITE= "Af";

    public final static String MAIN_SETTTING = "Ae";

    public final static String VIDEO_PLAY= "B";

    public final static String FILE_FAVORITE = "Ca";

    public final static String FILE_UNFAVORITE = "Cb";

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