package com.surecn.familymovie.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-05
 * Time: 15:29
 */
public class UpdateInfo {

    @Expose
    @SerializedName("update")
    public int update;

    @Expose
    @SerializedName("url")
    public String url;

    @Expose
    @SerializedName("msg")
    public String msg;
}
