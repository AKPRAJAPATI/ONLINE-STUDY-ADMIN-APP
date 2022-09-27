package com.university.mrmindedadmin.Models;

public class videoListModel {
    private String videoUniqueKey;
    private String videoUrl;
    private String videoName;

    public videoListModel() {
    }

    public videoListModel(String videoUniqueKey, String videoUrl) {
        this.videoUniqueKey = videoUniqueKey;
        this.videoUrl = videoUrl;
    }

    public videoListModel(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoUniqueKey() {
        return videoUniqueKey;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public void setVideoUniqueKey(String videoUniqueKey) {
        this.videoUniqueKey = videoUniqueKey;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
