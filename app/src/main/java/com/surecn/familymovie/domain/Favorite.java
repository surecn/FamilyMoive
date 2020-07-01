package com.surecn.familymovie.domain;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-10
 * Time: 11:08
 */
public class Favorite {

    private int type;

    private String value;

    private long time;

    private String extra;

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
