package com.surecn.familymovie.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-09
 * Time: 13:14
 */
public class LiveRoot implements Serializable {

    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("title")
    private String title;

    @Expose
    @SerializedName("subs")
    private List<Channel> subs;

    private int channelPosition;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Channel> getSubs() {
        return subs;
    }

    public void setSubs(List<Channel> subs) {
        this.subs = subs;
    }

    public int getChannelPosition() {
        return channelPosition;
    }

    public void setChannelPosition(int channelPosition) {
        this.channelPosition = channelPosition;
    }
}
