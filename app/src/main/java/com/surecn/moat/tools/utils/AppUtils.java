package com.surecn.moat.tools.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.TypedValue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by surecn on 15/7/7.
 */
public class AppUtils {

    public static boolean isSystemApp(ApplicationInfo applicationInfo) {
        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public static boolean isSystemUpdateApp(ApplicationInfo applicationInfo) {
        return ((applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
    }

    public static boolean isUserApp(ApplicationInfo pInfo) {
        return (!isSystemApp(pInfo) && !isSystemUpdateApp(pInfo));
    }

    public static int getAppVersionCode(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getAppVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 不好用
     * @param context
     * @return
     */
    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);
        if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public static class ClassUtils {

        public static Class getGenericClass(ParameterizedType parameterizedType, int i) {
            Object genericClass = parameterizedType.getActualTypeArguments()[i];
            if (genericClass instanceof ParameterizedType) { // 处理多级泛型
                return (Class) ((ParameterizedType) genericClass).getRawType();
            } else if (genericClass instanceof GenericArrayType) { // 处理数组泛型
                return (Class) ((GenericArrayType) genericClass).getGenericComponentType();
            } else if (genericClass instanceof TypeVariable) { // 处理泛型擦拭对象
                return (Class) getClass(((TypeVariable) genericClass).getBounds()[0], 0);
            } else {
                return (Class) genericClass;
            }
        }

        public static Class getClass(Type type, int i) {
            if (type instanceof ParameterizedType) { // 处理泛型类型
                return getGenericClass((ParameterizedType) type, i);
            } else if (type instanceof TypeVariable) {
                return (Class) getClass(((TypeVariable) type).getBounds()[0], 0); // 处理泛型擦拭对象
            } else {// class本身也是type，强制转型
                return (Class) type;
            }
        }
    }

    /**
     * Created by surecn on 15/5/18.
     */
    public static class DensityUtils
    {
        private DensityUtils() {
            /* cannot be instantiated */
            throw new UnsupportedOperationException("cannot be instantiated");
        }

        /**
         * dp转px
         *
         * @param context
         * @param val
         * @return
         */
        public static int dp2px(Context context, float dpVal) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    dpVal, context.getResources().getDisplayMetrics());
        }

        /**
         * sp转px
         *
         * @param context
         * @param val
         * @return
         */
        public static int sp2px(Context context, float spVal) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    spVal, context.getResources().getDisplayMetrics());
        }

        /**
         * px转dp
         *
         * @param context
         * @param pxVal
         * @return
         */
        public static float px2dp(Context context, float pxVal) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (pxVal / scale);
        }

        /**
         * px转sp
         *
         * @param fontScale
         * @param pxVal
         * @return
         */
        public static float px2sp(Context context, float pxVal) {
            return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
        }

    }

    /**
     * Created by surecn on 15/7/1.
     */
    public static class IOUtils {

        public static String readString(InputStream is) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int i= -1;
            try {
                while((i = is.read()) != -1){
                    baos.write(i);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return baos.toString();
        }
    }

    public static class NetworkUtils {

        public static boolean isNetworkAvailable(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            return (info != null && info.isConnected());
        }

    }

    public static class StringUtils {

        //将hashmap中得参数用url方式连接
        public static String hashMapToUrlString(HashMap<String, String> parameters) {
            StringBuffer urlbuff = new StringBuffer();
            if (parameters != null && parameters.size() > 0) {
                Set<String> keySet = parameters.keySet();
                for (String key : keySet) {
                    urlbuff.append(key).append("=").append(parameters.get(key)).append("&");
                }
                urlbuff.deleteCharAt(urlbuff.length() - 1);
            }
            return urlbuff.toString();
        }
    }
}
