package com.flyingeffects.com.ui.interfaces.view;

import com.flyingeffects.com.enity.new_fag_template_item;

import java.util.List;

public interface PreviewMvpView {
    void  getCompressImgList(List<String>imgList);

    void showDownProgress(int progress);

    void getTemplateFileSuccess(String TemplateFilePath);

    void collectionResult();

    void getTemplateLInfo(new_fag_template_item item);

    void hasLogin(boolean hasLogin);

    void downVideoSuccess(String path,String imagePath);
}
