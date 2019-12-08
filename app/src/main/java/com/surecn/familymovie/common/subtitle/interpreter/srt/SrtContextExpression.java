package com.surecn.familymovie.common.subtitle.interpreter.srt;

import android.text.TextUtils;

import com.surecn.familymovie.common.subtitle.interpreter.ContextExpression;
import com.surecn.familymovie.common.subtitle.interpreter.TimedText;
import com.surecn.moat.tools.log;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-11
 * Time: 11:10
 */
public class SrtContextExpression extends ContextExpression {

    private ArtTextExpression mArtTextExpression;

    private ArtTimedExpression mArtTimedExpression;

    private int mLastLine = 0;

    private int mNumber;

    private TimedText mTimedText;

    public SrtContextExpression() {
        mArtTimedExpression = new ArtTimedExpression();
        mArtTextExpression = new ArtTextExpression();
    }

    @Override
    public boolean interpret(String info) {
        if (TextUtils.isEmpty(info)) {
            mLastLine = 0;
            if (mTimedText != null) {
                mOnSubTitleListener.onNewTimeText(mTimedText);
                mArtTextExpression.setTimedText(null);
                mArtTextExpression.setTimedText(null);
            }
            return true;
        } else if (mLastLine == 0) {
            mNumber = Integer.parseInt(info.trim());
            mLastLine = 1;
            mTimedText = new TimedText();
            mArtTimedExpression.setTimedText(mTimedText);
            mArtTextExpression.setTimedText(mTimedText);
            return true;
        } else if (mLastLine == 1) {
            mArtTimedExpression.interpret(info);
            mLastLine = 2;
        } else if (mLastLine == 2) {
            mArtTextExpression.interpret(info);
            if (mTimedText == null) {
                return false;
            }
        }
        return true;
    }

}
