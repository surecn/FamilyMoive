package com.surecn.familymovie.common.subtitle.interpreter;

import android.text.SpannableStringBuilder;

import java.util.HashMap;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-11
 * Time: 11:04
 */
public abstract class ContextExpression implements Expression {

    public static interface OnSubTitleListener {
        void onScriptInfo(HashMap<String, String> map);
        void onNewTimeText(TimedText timedText);
    }
    protected OnSubTitleListener mOnSubTitleListener;

    public void setOnSubTitleListener(OnSubTitleListener mOnSubTitleListener) {
        this.mOnSubTitleListener = mOnSubTitleListener;
    }

    protected SpannableStringBuilder mSpannable;

    public ContextExpression() {
        mSpannable = new SpannableStringBuilder();
    }

    public abstract boolean interpret(String info);
}