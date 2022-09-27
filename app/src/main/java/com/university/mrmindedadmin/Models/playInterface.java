package com.university.mrmindedadmin.Models;

public interface playInterface {
    void updateClickLister(int position, String uniqueKey);
    void deleteClickLister(int position, String uniqueKey);
    void playVideo(int position ,String videoUrl ,  String uniqueKey);
}
