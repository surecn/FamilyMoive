package com.surecn.familymovie.common.subtitle.interpreter.srt;

import android.text.TextUtils;

import com.surecn.familymovie.common.subtitle.interpreter.Expression;
import com.surecn.familymovie.common.subtitle.interpreter.TimedText;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-11
 * Time: 18:41
 */
public class ArtTimedExpression implements Expression {

    private long HOUR = 60 * 60 * 1000;
    private long MINUTE = 60 * 1000;

    private TimedText mTimedText;

    public void setTimedText(TimedText timedText) {
        this.mTimedText = timedText;
    }

    @Override
    public boolean interpret(String info) {
        String [] array = info.split("-->");
        if (array.length == 2) {
            mTimedText.setStart(timeToLong(array[0].trim()));
            mTimedText.setEnd(timeToLong(array[0].trim()));
        }
        return false;
    }

    private long timeToLong(String time) {
        if (TextUtils.isEmpty(time)) {
            return 0;
        }
        int firstIndex = time.lastIndexOf(",");
        int secondIndex = time.lastIndexOf(":", firstIndex  - 1);
        int thirdIndex = time.lastIndexOf(":", secondIndex - 1);
        long mils = Long.parseLong(time.substring(firstIndex + 1));
        long second = Long.parseLong(time.substring(secondIndex + 1, firstIndex));
        long minute = Long.parseLong(time.substring(thirdIndex + 1, secondIndex));
        long hour = Long.parseLong(time.substring(0, thirdIndex));
        return hour * HOUR + minute * MINUTE + second * 1000 + mils;
    }
}
