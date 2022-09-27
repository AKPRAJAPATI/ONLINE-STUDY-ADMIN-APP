package com.university.mrmindedadmin.Models;

public interface pdfInterface {
    void updateClickLister(int position, String uniqueKey);
    void deleteClickLister(int position, String uniqueKey);
    void showpdf(int position ,String videoUrl ,  String uniqueKey);
}
