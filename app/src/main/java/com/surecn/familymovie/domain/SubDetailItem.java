package com.surecn.familymovie.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-05
 * Time: 19:55
 */
public class SubDetailItem {

    @Expose
    @SerializedName("filename")
    private String filename;

    @Expose
    @SerializedName("native_name")
    private String native_name;

    @Expose
    @SerializedName("url")
    private String url;

    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("filelist")
    private ArrayList<SubUrl> filelist;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getNative_name() {
        return native_name;
    }

    public void setNative_name(String native_name) {
        this.native_name = native_name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArrayList<SubUrl> getFilelist() {
        return filelist;
    }

    public void setFilelist(ArrayList<SubUrl> filelist) {
        this.filelist = filelist;
    }

    public static class SubUrl {
        @Expose
        @SerializedName("url")
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
