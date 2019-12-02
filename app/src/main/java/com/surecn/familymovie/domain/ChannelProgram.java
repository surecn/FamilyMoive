package com.surecn.familymovie.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class ChannelProgram implements Serializable {

    @Expose
    @SerializedName("cid")
    private int cid;

    @Expose
    @SerializedName("date")
    private String date;

    @Expose
    @SerializedName("startTime")
    private Date startTime;

    @Expose
    @SerializedName("endTime")
    private Date endTime;

    @Expose
    @SerializedName("title")
    private String title;

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
