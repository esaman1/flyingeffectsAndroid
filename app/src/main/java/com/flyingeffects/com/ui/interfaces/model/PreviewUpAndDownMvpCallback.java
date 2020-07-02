package com.flyingeffects.com.ui.interfaces.model;

public interface PreviewUpAndDownMvpCallback {

    void collectionResult();

    void hasLogin(boolean hasLogin);


    void downVideoSuccess(String path,String imagePath);

    void getTemplateFileSuccess(String filePath);

    void showDownProgress(int progress);

}
