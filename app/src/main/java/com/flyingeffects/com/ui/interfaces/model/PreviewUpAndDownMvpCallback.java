package com.flyingeffects.com.ui.interfaces.model;

import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.flyingeffects.com.entity.NewFragmentTemplateItem;

import java.util.List;

public interface PreviewUpAndDownMvpCallback {

    void collectionResult(boolean hasCollect);

    void zanResult();

    void hasLogin(boolean hasLogin);

    void onclickCollect();

    void getDressUpPathResult(List<String>paths);


    void downVideoSuccess(String path,String imagePath);

    void getTemplateFileSuccess(String filePath);

    void showDownProgress(int progress);


    void showNewData(List<NewFragmentTemplateItem> allData, boolean isRefresh);

    void resultAd(List<TTNativeExpressAd> ads);

    void getTemplateLInfo(NewFragmentTemplateItem data);

    void getSpliteMusic(String path);
    void returnSpliteMusic(String path,String videoPath);

    void shareSaveToAlbum();
}
