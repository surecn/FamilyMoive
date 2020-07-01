package com.surecn.familymovie.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * 网络状态判断辅助类
 * 参考：https://www.jianshu.com/p/83c28dcc6f75
 */
public class NetAssistUtil {

    /**
     * 获取网络连接状态是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkCAvailable(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo.isConnected()){
                    return activeNetworkInfo.isAvailable();
                }else{
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 获取网络是否已经连接
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context){
        if (context!=null){
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager!=null){
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo.isConnected();
            }else {
                return false;
            }
        }
        return false;
    }

    /**
     * 判断已连接的WIFI是否可用
     *
     * @param context
     * @return
     */
    public static boolean isWiFiAvailable(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo wiFiInfo = connectivityManager.
                        getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                boolean connected = wiFiInfo.isConnected();
                if (connected) {
                    return wiFiInfo.isAvailable();
                } else {
                    Toast.makeText(context, "Wifi网络没有连接", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 判断Wifi网络是否已经连接
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo wiFiInfo = connectivityManager.
                        getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                return wiFiInfo.isConnected();
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 判断移动数据是否已经连接
     *
     * @param context
     * @return
     */
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo mobileInfo = connectivityManager.
                        getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                return mobileInfo.isConnected();
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 判断移动数据是否可用
     *
     * @param context
     * @return
     */
    public static boolean isMobileAvailable(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo mobileInfo = connectivityManager.
                        getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                boolean connected = mobileInfo.isConnected();
                if (connected) {
                    return mobileInfo.isAvailable();
                } else {
                    Toast.makeText(context, "移动数据没有连接", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 获取连接的网络类型
     * @param context
     * @return
     */
    public static Integer netType(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                    return activeNetworkInfo.getType();
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        }
        return -1;
    }

    /**
     * 获取当前 详细的连接网络类型
     * @param context
     * @return
     */
    public static int getNetType(Context context) {
        int netType = 0;
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        return networkInfo.getType();
    }

    public static String getIpAddress(Context context) {
        if (context == null) {
            return "";
        }

        ConnectivityManager conManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo info = conManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 3/4g网络
                if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return getHostIp();
                } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    return getLocalIPAddress(context); // 局域网地址
                } else if (info.getType() == ConnectivityManager.TYPE_ETHERNET) {
                    // 以太网有限网络
                    return getHostIp();
                }
            }
        } catch (Exception e) {
            return "";
        }
        return "";

    }

    // 获取有限网IP
    public static String getHostIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                if (!intf.isUp()) { // 判断网口是否在使用
                    continue;
                }
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();

                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {

        }
        return null;
    }

    // wifi下获取本地网络IP地址（局域网地址）
    public static String getLocalIPAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());
            return ipAddress;
        }
        return "";
    }



    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }
}