package com.surecn.familymovie.common;

import android.content.Context;
import android.net.ConnectivityManager;

import com.surecn.familymovie.utils.NetAssistUtil;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class LanManager {

    // 根据ip 网段去 发送arp 请求
    public static long[] discover(Context context) {
        String sIP = NetAssistUtil.getIpAddress(context);
        if (sIP == null) {
            return null;
        }
        long ip = ipToLong(sIP);
        int length = 8;

        long min = ip & (0xffffffff << length);
        long max = ip | (0xffffffff >>> (32 - length));

        return new long[]{min, max};
    }

    public static boolean pingHost(String ipAddress,int port) {
        boolean isReachable = false;
        Socket connect = new Socket();
        try {
            InetSocketAddress endpointSocketAddr = new InetSocketAddress(ipAddress, port);
            //此处3000是超时时间,单位 毫秒
            connect.connect(endpointSocketAddr,1000);
            isReachable = connect.isConnected();
        } catch (Exception e) {
            System.out.println(e.getMessage() + ", ip = " + ipAddress + ", port = " +port);
        } finally {
            if (connect != null) {
                try {
                    connect.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage() + ", ip = " + ipAddress + ", port = " +port);
                }
            }
        }
        return isReachable;
    }

    //IP转换为Long
    public static long ipToLong(String ip) {
        String[] ipArray = ip.split("\\.");
        List ipNums = new ArrayList();
        for (int i = 0; i < 4; ++i) {
            ipNums.add(Long.valueOf(Long.parseLong(ipArray[i].trim())));
        }
        long ZhongIPNumTotal = ((Long) ipNums.get(0)).longValue() * 256L * 256L * 256L
                + ((Long) ipNums.get(1)).longValue() * 256L * 256L + ((Long) ipNums.get(2)).longValue() * 256L
                + ((Long) ipNums.get(3)).longValue();

        return ZhongIPNumTotal;
    }

    public static String LongToIp(long ip)
    {
        StringBuilder sb = new StringBuilder();
        //直接右移24位
        sb.append(ip >> 24);
        sb.append(".");
        //将高8位置0，然后右移16
        sb.append((ip & 0x00FFFFFF) >> 16);
        sb.append(".");
        //将高16位置0，然后右移8位
        sb.append((ip & 0x0000FFFF) >> 8);
        sb.append(".");
        //将高24位置0
        sb.append((ip & 0x000000FF));
        return sb.toString();
    }



    private static int getIpAddrMaskForInterfaces(String interfaceName) {
        try {
            Enumeration<NetworkInterface> networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();    //获取本机所有的网络接口
            while (networkInterfaceEnumeration.hasMoreElements()) { //判断 Enumeration 对象中是否还有数据
                NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement(); //获取 Enumeration 对象中的下一个数据
                if (!networkInterface.isUp() && !interfaceName.equals(networkInterface.getDisplayName())) { //判断网口是否在使用，判断是否时我们获取的网口
                    continue;
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {    //
                    if (interfaceAddress.getAddress() instanceof Inet4Address) {    //仅仅处理ipv4
                        return interfaceAddress.getNetworkPrefixLength();   //获取掩码位数，通过 calcMaskByPrefixLength 转换为字符串
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static String getIpAddrForInterfaces(String interfaceName){
        try {
            Enumeration<NetworkInterface> enNetworkInterface = NetworkInterface.getNetworkInterfaces(); //获取本机所有的网络接口
            while (enNetworkInterface.hasMoreElements()) {  //判断 Enumeration 对象中是否还有数据
                NetworkInterface networkInterface = enNetworkInterface.nextElement();   //获取 Enumeration 对象中的下一个数据
                if (!networkInterface.isUp()) { // 判断网口是否在使用
                    continue;
                }
                if (!interfaceName.equals(networkInterface.getDisplayName())) { // 网口名称是否和需要的相同
                    continue;
                }

                Enumeration<InetAddress> enInetAddress = networkInterface.getInetAddresses();   //getInetAddresses 方法返回绑定到该网卡的所有的 IP 地址。
                while (enInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enInetAddress.nextElement();

                    if (inetAddress instanceof Inet4Address) {  //判断是否未ipv4
                        return inetAddress.getHostAddress();
                    }
//                    判断未lo时
//                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
//                        return inetAddress.getHostAddress();
//                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    //通过子网掩码的位数计算子网掩码
    public static String calcMaskByPrefixLength(int length) {

        int mask = 0xffffffff << (32 - length);
        int partsNum = 4;
        int bitsOfPart = 8;
        int maskParts[] = new int[partsNum];
        int selector = 0x000000ff;

        for (int i = 0; i < maskParts.length; i++) {
            int pos = maskParts.length - 1 - i;
            maskParts[pos] = (mask >> (i * bitsOfPart)) & selector;
        }

        String result = "";
        result = result + maskParts[0];
        for (int i = 1; i < maskParts.length; i++) {
            result = result + "." + maskParts[i];
        }
        return result;
    }
}
