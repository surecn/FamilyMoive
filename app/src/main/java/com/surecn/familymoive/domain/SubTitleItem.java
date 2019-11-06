package com.surecn.familymoive.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-04
 * Time: 13:45
 */
public class SubTitleItem {

    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("subtype")
    private String subtype;

    @Expose
    @SerializedName("native_name")
    private String native_name;

    @Expose
    @SerializedName("videoname")
    private String videoname;

    @Expose
    @SerializedName("vote_score")
    private int vote_score;

    @Expose
    @SerializedName("revision")
    private int revision;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubtype() {
        return subtype;
    }

    public String getNative_name() {
        return native_name;
    }

    public String getVideoname() {
        return videoname;
    }

    public int getVote_score() {
        return vote_score;
    }

    public int getRevision() {
        return revision;
    }
}
