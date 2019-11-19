package com.surecn.familymovie.common.subtitle.interpreter.ass;

import com.surecn.familymovie.common.subtitle.interpreter.Expression;

import java.util.HashMap;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-11
 * Time: 11:41
 */
public class AssScriptExpression implements Expression {

    private ScriptInfo mScriptInfo;

    private HashMap<String, String> mEvent;

    public AssScriptExpression(ScriptInfo scriptInfo) {
        this.mScriptInfo = scriptInfo;
    }

    public HashMap<String, String> getEvent() {
        return mEvent;
    }

    @Override
    public boolean interpret(String info) {
        String [] array = info.split(":");
        if (array.length <= 0) {
            return false;
        }
        String value = "";
        if (array.length > 1) {
            value = array[1];
        }
        if ("ScriptType".equals(array[0])) {
            mScriptInfo.setScriptType(value);
        } else if ("Collisions".equals(array[0])) {
            mScriptInfo.setCollisions(value);
        } else if ("PlayResX".equals(array[0])) {
            mScriptInfo.setPlayResX(value);
        } else if ("PlayResY".equals(array[0])) {
            mScriptInfo.setPlayResY(value);
        } else if ("Timer".equals(array[0])) {
            mScriptInfo.setTimer(value);
        } else if ("Synch Point".equals(array[0])) {
            mScriptInfo.setSynchPoint(value);
        } else if ("WrapStyle".equals(array[0])) {
            mScriptInfo.setWrapStyle(value);
        } else if ("ScaledBorderAndShadow".equals(array[0])) {
            mScriptInfo.setScaledBorderAndShadow(value);
        } else if ("Video Zoom".equals(array[0])) {
            mScriptInfo.setVideoZoom(value);
        } else if ("Scroll Position".equals(array[0])) {
            mScriptInfo.setScrollPosition(value);
        } else if ("Active Line".equals(array[0])) {
            mScriptInfo.setActiveLine(value);
        } else if ("Video Zoom Percent".equals(array[0])) {
            mScriptInfo.setVideoZoomPercent(value);
        }
        return true;
    }


}
