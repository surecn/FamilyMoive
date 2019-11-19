package com.surecn.familymovie.utils;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-05
 * Time: 09:56
 */
public class SubtitleUtils {

    public static String formatText(String text) {
        while (true) {
            int index = text.indexOf("{\\");
            if (index < 0) {
                return text;
            }
            int end = text.indexOf("}", index + 1);
            if (end < 0) {
                return text;
            }
            text = text.substring(0, index) + text.substring(end + 1, text.length());
        }
    }


}
