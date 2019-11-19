package com.surecn.familymovie.common.subtitle.interpreter;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-11
 * Time: 11:13
 */
public abstract class EventExpression implements Expression {

    protected TimedText mTimedText;

    public TimedText getmTimedText() {
        return mTimedText;
    }

    public void setTimedText(TimedText mTimedText) {
        this.mTimedText = mTimedText;
    }

    @Override
    public abstract boolean interpret(String info);
}
