package com.flyingeffects.com.ui.interfaces.view;

public interface PreviewUpAndDownMvpView {
    void collectionResult();

    void hasLogin(boolean hasLogin);

    void downVideoSuccess(String path,String imagePath);

    void showDownProgress(int progress);

    void getTemplateFileSuccess(String filePath);
}
