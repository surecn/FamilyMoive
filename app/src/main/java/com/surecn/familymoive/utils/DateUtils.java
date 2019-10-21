package com.surecn.familymoive.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-10-15
 * Time: 12:52
 */
public class DateUtils {

    private final static long HOUR = 60 * 60 * 1000;
    private final static long MINUTE = 60 * 1000;
    private final static SimpleDateFormat SIMPLE_TIME_FORMAT = new SimpleDateFormat("mm:ss");
    private final static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    public static String toTimeLength(long time) {
        long hours = time / HOUR;
        long remain = time % HOUR;
        long minutes = remain / MINUTE;
        remain = remain % MINUTE;
        long seconds = remain / 1000;
        StringBuffer stringBuffer = new StringBuffer();
        if (hours > 0) {
            stringBuffer.append(hours).append(":");
        }
        if (minutes > 0) {
            stringBuffer.append(minutes).append(":");
        }
        if (seconds > 0) {
            stringBuffer.append(seconds);
        }
        return stringBuffer.toString();
    }

    public static String toTimeLength(Date e) {
        return SIMPLE_TIME_FORMAT.format(e);
    }
    public static String toDate(long l) {
        return SIMPLE_DATE_FORMAT.format(new Date(l));
    }

}
