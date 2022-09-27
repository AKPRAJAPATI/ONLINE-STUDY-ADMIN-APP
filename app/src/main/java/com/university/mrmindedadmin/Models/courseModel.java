package com.university.mrmindedadmin.Models;

import java.io.Serializable;

public class courseModel implements Serializable {
    private String course_name;
    private String thumbnail;
    private int price;

    private String  uniquekey;

    private String userProfile;

    public courseModel() {
    }

    public courseModel(String course_name, String thumbnail, int price, String userProfile) {
        this.course_name = course_name;
        this.thumbnail = thumbnail;
        this.price = price;
        this.userProfile = userProfile;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getUniquekey() {
        return uniquekey;
    }

    public void setUniquekey(String uniquekey) {
        this.uniquekey = uniquekey;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }
}
