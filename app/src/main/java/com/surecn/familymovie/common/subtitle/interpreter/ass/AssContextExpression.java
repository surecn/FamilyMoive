package com.surecn.familymovie.common.subtitle.interpreter.ass;

import android.text.TextUtils;

import com.surecn.familymovie.common.subtitle.interpreter.ContextExpression;
import com.surecn.familymovie.common.subtitle.interpreter.EventExpression;
import com.surecn.familymovie.common.subtitle.interpreter.SectionExpression;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-11
 * Time: 11:10
 */
public class AssContextExpression extends ContextExpression {

    private SectionExpression mSectionExpression;

    private AssScriptExpression mScriptExpression;

    private EventExpression mEventExpression;

    private int mSection;

    private boolean mLastEmpty = true;

    private ScriptInfo mScriptInfo;

    public ScriptInfo getScriptInfo() {
        return mScriptInfo;
    }

    public AssContextExpression() {
        mScriptInfo = new ScriptInfo();
        mSectionExpression = new AssSectionExpression();
        mScriptExpression = new AssScriptExpression(mScriptInfo);
        mEventExpression = new AssEventExpression();
    }

    @Override
    public boolean interpret(String info) {
        if (TextUtils.isEmpty(info)) {
            mLastEmpty = true;
            return true;
        }
        if (mLastEmpty) {
            if (info.charAt(0) == (char) '[') {
                mSectionExpression.interpret(info);
                mSection = mSectionExpression.getSection();
                return true;
            }
        }
        mLastEmpty = false;
        switch (mSection) {
            case 1:
                mScriptExpression.interpret(info);
                break;
            case 2:
                break;
            case 3:
                mEventExpression.interpret(info);
                if (mEventExpression.getmTimedText() != null) {
                    mOnSubTitleListener.onNewTimeText(mEventExpression.getmTimedText());
                    mEventExpression.setTimedText(null);
                }
                break;
        }
        return true;
    }

}
