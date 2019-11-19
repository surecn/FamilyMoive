package com.surecn.familymovie.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-08
 * Time: 19:56
 */
public class ChannelSource  implements Serializable {

    @Expose
    @SerializedName("tid")
    private int tid;

    @Expose
    @SerializedName("url")
    private String url;

    @Expose
    @SerializedName("order")
    private int order;

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
