package com.surecn.familymovie.common.subtitle.interpreter.ass;

import com.surecn.familymovie.common.subtitle.interpreter.SectionExpression;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-11
 * Time: 11:20
 */
public class AssSectionExpression extends SectionExpression {

    private final static int SECTION_SCRIPT= 1;
    private final static int SECTION_STYLE= 2;
    private final static int SECTION_EVENT= 3;

    @Override
    public boolean interpret(String info) {
        if ("[Script Info]".equals(info)) {
            setSection(SECTION_SCRIPT);
        } else if ("[V4+ Styles]".equals(info)) {
            setSection(SECTION_STYLE);
        } else if ("[Events]".equals(info)) {
            setSection(SECTION_EVENT);
        }
        return true;
    }
}
