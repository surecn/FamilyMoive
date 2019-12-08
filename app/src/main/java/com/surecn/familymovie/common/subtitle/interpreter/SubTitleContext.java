package com.surecn.familymovie.common.subtitle.interpreter;

import com.surecn.familymovie.common.subtitle.interpreter.ass.AssContextExpression;
import com.surecn.familymovie.common.subtitle.interpreter.srt.SrtContextExpression;
import com.surecn.moat.tools.log;

import java.util.HashMap;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-11
 * Time: 11:01
 */
public class SubTitleContext {

    private String mExtension;

    private ContextExpression mContextExpression;

    private static final HashMap<String, Class<? extends ContextExpression>> sSupportSubtitle = new HashMap<>();

    static {
        sSupportSubtitle.put("ass", AssContextExpression.class);
        sSupportSubtitle.put("ssa", AssContextExpression.class);
        sSupportSubtitle.put("srt", SrtContextExpression.class);
    }

    public SubTitleContext(String extension) {
        log.d("extension:" + extension);
        this.mExtension = extension;
        try {
            mContextExpression = sSupportSubtitle.get(extension).newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void setOnSubTitleListener(ContextExpression.OnSubTitleListener subTitleListener) {
        if (mContextExpression != null)
            mContextExpression.setOnSubTitleListener(subTitleListener);
    }


    public void putLine(String line) {
        try {
            mContextExpression.interpret(line);
        } catch (Exception e) {
            log.e(e);
        }
    }



}
