package com.surecn.familymovie.common.subtitle.interpreter;

import android.text.SpannableStringBuilder;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-11
 * Time: 13:48
 */
public class TimedText {

    private long start;

    private long end;

    private SpannableStringBuilder text;

    public TimedText() {
        this.text = new SpannableStringBuilder();
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public CharSequence getText() {
        return text;
    }

    public void setText(CharSequence text) {
        this.text = new SpannableStringBuilder(text);
    }

    public void appendLine(CharSequence text) {
        this.text.append("\n" + text);
    }
}
