package com.flyingeffects.com.ui.interfaces.model;

import com.flyingeffects.com.enity.new_fag_template_item;

import java.util.List;

public interface PreviewUpAndDownMvpCallback {

    void collectionResult();

    void hasLogin(boolean hasLogin);


    void downVideoSuccess(String path,String imagePath);

    void getTemplateFileSuccess(String filePath);

    void showDownProgress(int progress);


    void showNewData(List<new_fag_template_item> allData);

}
