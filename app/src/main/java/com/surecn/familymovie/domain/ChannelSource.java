package com.surecn.familymovie.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-08
 * Time: 19:56
 */
public class ChannelSource implements Parcelable {

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.tid);
        dest.writeString(this.url);
        dest.writeInt(this.order);
    }

    public ChannelSource() {
    }

    protected ChannelSource(Parcel in) {
        this.tid = in.readInt();
        this.url = in.readString();
        this.order = in.readInt();
    }

    public static final Parcelable.Creator<ChannelSource> CREATOR = new Parcelable.Creator<ChannelSource>() {
        @Override
        public ChannelSource createFromParcel(Parcel source) {
            return new ChannelSource(source);
        }

        @Override
        public ChannelSource[] newArray(int size) {
            return new ChannelSource[size];
        }
    };
}
