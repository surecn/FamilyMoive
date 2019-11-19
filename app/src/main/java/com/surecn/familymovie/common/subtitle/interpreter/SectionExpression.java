package com.surecn.familymovie.common.subtitle.interpreter;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-11
 * Time: 11:13
 */
public abstract class SectionExpression implements Expression {
    private int mSection;

    public int getSection() {
        return mSection;
    }

    public void setSection(int section) {
        this.mSection = section;
    }

    @Override
    public abstract boolean interpret(String info);
}
