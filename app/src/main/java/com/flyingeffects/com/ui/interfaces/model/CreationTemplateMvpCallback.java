package com.flyingeffects.com.ui.interfaces.model;

import com.flyingeffects.com.ui.model.AnimStickerModel;
import com.flyingeffects.com.view.StickerView;

public interface CreationTemplateMvpCallback {

    void ItemClickForStickView(AnimStickerModel stickView);

    void hasPlayingComplete();

    void setgsyVideoProgress(int progress);

    void animIsComplate();

    void getVideoDuration(int duration,int thumbCount );

    void needPauseVideo();

    void getVideoCover(String path,String originalPath);

    void getBgmPath(String path);

    void showRenderVideoTime(int duration);

    void changFirstVideoSticker(String path);

    void isFirstAddSuccess();

    void showCreateTemplateAnim(boolean isShow);


}
