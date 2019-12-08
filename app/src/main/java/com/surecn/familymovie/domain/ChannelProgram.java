package com.surecn.familymovie.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class ChannelProgram implements Parcelable {

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.cid);
        dest.writeString(this.date);
        dest.writeLong(this.startTime != null ? this.startTime.getTime() : -1);
        dest.writeLong(this.endTime != null ? this.endTime.getTime() : -1);
        dest.writeString(this.title);
    }

    public ChannelProgram() {
    }

    protected ChannelProgram(Parcel in) {
        this.cid = in.readInt();
        this.date = in.readString();
        long tmpStartTime = in.readLong();
        this.startTime = tmpStartTime == -1 ? null : new Date(tmpStartTime);
        long tmpEndTime = in.readLong();
        this.endTime = tmpEndTime == -1 ? null : new Date(tmpEndTime);
        this.title = in.readString();
    }

    public static final Parcelable.Creator<ChannelProgram> CREATOR = new Parcelable.Creator<ChannelProgram>() {
        @Override
        public ChannelProgram createFromParcel(Parcel source) {
            return new ChannelProgram(source);
        }

        @Override
        public ChannelProgram[] newArray(int size) {
            return new ChannelProgram[size];
        }
    };
}
