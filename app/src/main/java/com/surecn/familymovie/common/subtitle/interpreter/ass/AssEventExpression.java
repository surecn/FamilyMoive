package com.surecn.familymovie.common.subtitle.interpreter.ass;

import android.text.TextUtils;

import com.surecn.familymovie.common.subtitle.interpreter.EventExpression;
import com.surecn.familymovie.common.subtitle.interpreter.TimedText;
import com.surecn.moat.tools.log;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-11
 * Time: 11:55
 */
public class AssEventExpression extends EventExpression {

    private final static int LEN_FORMAT = "Dialogue:".length();
    private final static int LEN_DIALOGUE = "Format:".length();

    private String[] keys;

    private int INDEX_START;
    private int INDEX_END;
    private int INDEX_TEXT;

    private long HOUR = 60 * 60 * 1000;
    private long MINUTE = 60 * 1000;

    @Override
    public boolean interpret(String info) {
        if (info.startsWith("Dialogue:")) {
            try {
                String[] array = info.substring(LEN_DIALOGUE).split(",");
                mTimedText = new TimedText();
                mTimedText.setStart(timeToLong(array[INDEX_START]));
                mTimedText.setEnd(timeToLong(array[INDEX_END]));
                mTimedText.setText(array[INDEX_TEXT]);
            } catch (Exception e) {
                log.e(e);
            }
        } else if (info.startsWith("Format:")) {
            keys = info.substring(LEN_FORMAT).split(",");
            for (int i = 0, len = keys.length; i < len; i++) {
                keys[i] = keys[i].trim();
                if (keys[i].equals("Start")) {
                    INDEX_START = i;
                } else if (keys[i].equals("End")) {
                    INDEX_END = i;
                }  else if (keys[i].equals("Text")) {
                    INDEX_TEXT = i;
                }
            }
        }
        return true;
    }

    private long timeToLong(String time) {
        if (TextUtils.isEmpty(time)) {
            return 0;
        }
        int firstIndex = time.lastIndexOf(".");
        int secondIndex = time.lastIndexOf(":", firstIndex  - 1);
        int thirdIndex = time.lastIndexOf(":", secondIndex - 1);
        long mils = Long.parseLong(time.substring(firstIndex + 1));
        long second = Long.parseLong(time.substring(secondIndex + 1, firstIndex));
        long minute = Long.parseLong(time.substring(thirdIndex + 1, secondIndex));
        long hour = Long.parseLong(time.substring(0, thirdIndex));
        return hour * HOUR + minute * MINUTE + second * 1000 + mils;
    }
}
