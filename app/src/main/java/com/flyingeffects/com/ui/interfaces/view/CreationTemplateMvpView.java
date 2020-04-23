package com.flyingeffects.com.ui.interfaces.view;

import com.flyingeffects.com.ui.model.AnimStickerModel;
import com.flyingeffects.com.view.StickerView;

public interface CreationTemplateMvpView {

    void ItemClickForStickView(AnimStickerModel stickView);

    void hasPlayingComplete();

    void setgsyVideoProgress(int progress);

    void getVideoDuration(int duration,int thumbCount);

    void needPauseVideo();

    void getVideoCover(String path,String originalPath);

    void getBgmPath(String path);
}
