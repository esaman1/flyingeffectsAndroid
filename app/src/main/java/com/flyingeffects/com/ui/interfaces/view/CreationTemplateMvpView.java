package com.flyingeffects.com.ui.interfaces.view;

import com.flyingeffects.com.ui.model.AnimStickerModel;
import com.flyingeffects.com.view.StickerView;

public interface CreationTemplateMvpView {

    void ItemClickForStickView(AnimStickerModel stickView);

    void hasPlayingComplete();

    void deleteFirstSticker();

    void stickerOnclickCallback(String str);

    void showTextDialog(String inputText);

    void hideTextDialog();

    void setgsyVideoProgress(int progress);

    void getVideoDuration(int duration,int thumbCount);

    void needPauseVideo();

    void getVideoCover(String path,String originalPath);

    void getBgmPath(String path);

    void changFirstVideoSticker(String path);

    void isFirstAddSuccess();

    void showCreateTemplateAnim(boolean isShow);


    void showMusicBtn(boolean isShow);

    void animIsComplate();

//    void showRenderVideoTime(int duration);
}
