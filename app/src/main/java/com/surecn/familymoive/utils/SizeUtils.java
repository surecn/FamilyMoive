package com.surecn.familymoive.utils;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-26
 * Time: 16:08
 */
public class SizeUtils {

    public final static long GB = 1024 * 1024 * 1024;
    public final static long MB = 1024 * 1024;
    public final static long KB = 1024;

    public static String toSimple(long size) {
        int gb = 0,mb = 0,kb = 0, b = 0;
        gb = (int) (size / GB);
        size = size % GB;
        mb = (int) (size / MB);
        size = size % MB;
        kb = (int) (size / KB);
        b = (int) (size % KB);
        if (size > GB) {
            float res = gb * 1000 + mb;
            if (mb > 0) {
                return String.valueOf(res / 1000) + "GB";
            } else {
                return gb + "GB";
            }
        } else if (size > MB) {
            float res = mb * 1000 + kb;
            if (kb > 0) {
                return String.valueOf(res / 1000) + "MB";
            } else {
                return mb + "MB";
            }
        } else if (size > KB) {
            float res = kb * 1000 + b;
            if (b > 0) {
                return String.valueOf(res / 1000) + "KB";
            } else {
                return kb + "MB";
            }
        } else {
            return b + "B";
        }
    }

}
