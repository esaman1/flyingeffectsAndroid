package com.flyingeffects.com.ui.interfaces.view;

import com.flyingeffects.com.enity.new_fag_template_item;

import java.util.List;

public interface PreviewUpAndDownMvpView {
    void collectionResult();

    void hasLogin(boolean hasLogin);

    void downVideoSuccess(String path,String imagePath);

    void showDownProgress(int progress);

    void getTemplateFileSuccess(String filePath);

    void showNewData(List<new_fag_template_item> allData);
}
