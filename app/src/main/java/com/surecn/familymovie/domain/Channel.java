package com.surecn.familymovie.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-08
 * Time: 15:26
 */
public class Channel implements Parcelable {

    public final static int SECTION_ROOT = 1;
    public final static int SECTION_CHANNEL = 0;

    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("title")
    private String title;

    @Expose
    @SerializedName("order")
    private int order;

    @Expose
    @SerializedName("srcs")
    private ArrayList<ChannelSource> srcs;

    private TreeMap<Long, ChannelProgram> programMaps;

    @Expose
    @SerializedName("programs")
    private ArrayList<ChannelProgram> programs;

    private int selectIndex;

    private String lastDate;

    private String currentProgram;

    private boolean favorite = false;

    public Channel() {
        this.section = 0;
    }

    public Channel(String title, int section, int selectIndex) {
        this.title = title;
        this.section = section;
        this.selectIndex = selectIndex;
    }

    private int section;

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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public ArrayList<ChannelSource> getSrcs() {
        return srcs;
    }

    public void setSrcs(ArrayList<ChannelSource> srcs) {
        this.srcs = srcs;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String mLastDate) {
        this.lastDate = mLastDate;
    }

    public String getCurrentProgram() {
        return currentProgram;
    }

    public void setCurrentProgram(String currentProgram) {
        this.currentProgram = currentProgram;
    }

    public TreeMap<Long, ChannelProgram> getProgramMaps() {
        return programMaps;
    }

    public void setProgramMaps(TreeMap<Long, ChannelProgram> programMaps) {
        this.programMaps = programMaps;
    }

    public ArrayList<ChannelProgram> getPrograms() {
        return programs;
    }

    public void setPrograms(ArrayList<ChannelProgram> programs) {
        this.programs = programs;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeInt(this.order);
        dest.writeList(this.srcs);
        dest.writeList(this.programs);
        dest.writeString(this.lastDate);
        dest.writeString(this.currentProgram);
        dest.writeByte(this.favorite ? (byte) 1 : (byte) 0);
        dest.writeInt(this.section);
    }

    protected Channel(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.order = in.readInt();
        this.srcs = new ArrayList<ChannelSource>();
        in.readList(this.srcs, ChannelSource.class.getClassLoader());
        this.programs = new ArrayList<ChannelProgram>();
        in.readList(this.programs, ChannelProgram.class.getClassLoader());
        this.lastDate = in.readString();
        this.currentProgram = in.readString();
        this.favorite = in.readByte() != 0;
        this.section = in.readInt();
    }

    public static final Parcelable.Creator<Channel> CREATOR = new Parcelable.Creator<Channel>() {
        @Override
        public Channel createFromParcel(Parcel source) {
            return new Channel(source);
        }

        @Override
        public Channel[] newArray(int size) {
            return new Channel[size];
        }
    };
}
