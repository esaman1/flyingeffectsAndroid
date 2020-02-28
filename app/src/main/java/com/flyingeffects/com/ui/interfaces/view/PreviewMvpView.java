package com.flyingeffects.com.ui.interfaces.view;

import java.util.List;

public interface PreviewMvpView {
    void  getCompressImgList(List<String>imgList);

    void showDownProgress(int progress);

    void getTemplateFileSuccess(String TemplateFilePath);
}
