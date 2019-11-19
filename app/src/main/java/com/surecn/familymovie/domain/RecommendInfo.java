package com.surecn.familymovie.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-11-05
 * Time: 15:41
 */
public class RecommendInfo {

    @Expose
    @SerializedName("images")
    private ArrayList<String> images;

    @Expose
    @SerializedName("text")
    private String text;

    @Expose
    @SerializedName("qrcode")
    private String qrcode;


    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }
}
