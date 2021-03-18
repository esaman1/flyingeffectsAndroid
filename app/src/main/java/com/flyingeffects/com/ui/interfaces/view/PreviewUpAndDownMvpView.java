package com.flyingeffects.com.ui.interfaces.view;

import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.flyingeffects.com.enity.new_fag_template_item;

import java.util.List;

public interface PreviewUpAndDownMvpView {
    void collectionResult(boolean collectionResult);

    void zanResult();

    void hasLogin(boolean hasLogin);

    void downVideoSuccess(String path,String imagePath);

    void showDownProgress(int progress);

    void getTemplateFileSuccess(String filePath);

    void showNewData(List<new_fag_template_item> allData,boolean isRefresh);

    void resultAd(List<TTNativeExpressAd> ads);

    void getTemplateLInfo(new_fag_template_item data);

    void  returnSpliteMusic(String path,String videoPath);

    void onclickCollect();

    void getDressUpPathResult(List<String>paths);

    void shareSaveToAlbum();
}
