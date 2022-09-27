package com.university.mrmindedadmin.Models;

public class pdfModel {
    private String pdfUrl;
    private String pdfName;
    private String pdfUniqueKey;

    public pdfModel() {
    }

    public pdfModel(String pdfUrl, String pdfName) {
        this.pdfUrl = pdfUrl;
        this.pdfName = pdfName;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getPdfName() {
        return pdfName;
    }

    public void setPdfName(String pdfName) {
        this.pdfName = pdfName;
    }

    public String getPdfUniqueKey() {
        return pdfUniqueKey;
    }

    public void setPdfUniqueKey(String pdfUniqueKey) {
        this.pdfUniqueKey = pdfUniqueKey;
    }
}
