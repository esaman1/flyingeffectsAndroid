package com.flyingeffects.com.ui.interfaces.model;

import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.flyingeffects.com.enity.new_fag_template_item;

import java.util.List;

public interface PreviewUpAndDownMvpCallback {

    void collectionResult(boolean hasCollect);

    void ZanResult();

    void hasLogin(boolean hasLogin);

    void onclickCollect();

    void GetDressUpPathResult(List<String>paths);


    void downVideoSuccess(String path,String imagePath);

    void getTemplateFileSuccess(String filePath);

    void showDownProgress(int progress);


    void showNewData(List<new_fag_template_item> allData,boolean isRefresh);

    void resultAd(List<TTNativeExpressAd> ads);

    void getTemplateLInfo(new_fag_template_item data);

    void getSpliteMusic(String path);
    void returnSpliteMusic(String path,String videoPath);

}
