package com.surecn.familymovie.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-09
 * Time: 13:14
 */
public class LiveRoot implements Parcelable {

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeList(this.subs);
        dest.writeInt(this.channelPosition);
    }

    public LiveRoot() {
    }

    protected LiveRoot(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.subs = new ArrayList<Channel>();
        in.readList(this.subs, Channel.class.getClassLoader());
        this.channelPosition = in.readInt();
    }

    public static final Parcelable.Creator<LiveRoot> CREATOR = new Parcelable.Creator<LiveRoot>() {
        @Override
        public LiveRoot createFromParcel(Parcel source) {
            return new LiveRoot(source);
        }

        @Override
        public LiveRoot[] newArray(int size) {
            return new LiveRoot[size];
        }
    };
}
