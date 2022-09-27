package com.university.mrmindedadmin.Models;

public class slidersModel {
    private String slidersImg;
    private String uniqueKey;

    public slidersModel() {
    }

    public slidersModel(String slidersImg) {
        this.slidersImg = slidersImg;
    }

    public String getSlidersImg() {
        return slidersImg;
    }

    public void setSlidersImg(String slidersImg) {
        this.slidersImg = slidersImg;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
}
