package com.flyingeffects.com.ui.interfaces.view;

import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.flyingeffects.com.entity.NewFragmentTemplateItem;

import java.util.List;

public interface PreviewUpAndDownMvpView {

    void collectionResult(boolean collectionResult);

    void zanResult();

    void hasLogin(boolean hasLogin);

    void downVideoSuccess(String path,String imagePath);

    void showDownProgress(int progress);

    void getTemplateFileSuccess(String filePath);

    void showNewData(List<NewFragmentTemplateItem> allData, boolean isRefresh);

    void resultAd(List<TTNativeExpressAd> ads);

    void getTemplateInfo(NewFragmentTemplateItem data);

    void  returnSpliteMusic(String path,String videoPath);

    void onclickCollect();

    void getDressUpPathResult(List<String>paths);

    void shareSaveToAlbum();
}
