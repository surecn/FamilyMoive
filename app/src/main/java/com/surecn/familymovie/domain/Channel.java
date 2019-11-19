package com.surecn.familymovie.domain;

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
public class Channel implements Serializable {

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

    private ArrayList<ChannelProgram> programs;

    private int index;

    private String lastDate;

    private String currentProgram;

    public Channel() {
        this.section = 0;
    }

    public Channel(String title, int section, int index) {
        this.title = title;
        this.section = section;
        this.index = index;
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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
}
