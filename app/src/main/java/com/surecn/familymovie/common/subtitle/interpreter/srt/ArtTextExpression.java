package com.surecn.familymovie.common.subtitle.interpreter.srt;

import com.surecn.familymovie.common.subtitle.interpreter.Expression;
import com.surecn.familymovie.common.subtitle.interpreter.TimedText;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-11
 * Time: 18:41
 */
public class ArtTextExpression implements Expression {

    private long HOUR = 60 * 60 * 1000;
    private long MINUTE = 60 * 1000;

    private TimedText mTimedText;

    public void setTimedText(TimedText timedText) {
        this.mTimedText = timedText;
    }

    @Override
    public boolean interpret(String info) {
        mTimedText.appendLine(info);
        return false;
    }
}
