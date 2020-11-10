package com.flyingeffects.com.ui.interfaces.model;

import com.flyingeffects.com.ui.model.AnimStickerModel;
import com.flyingeffects.com.view.StickerView;

public interface CreationTemplateMvpCallback {

    void ItemClickForStickView(AnimStickerModel stickView);

    void hasPlayingComplete();

    void deleteFirstSticker();

    void hideKeyBord();

    void stickerOnclickCallback(String title);

    void animIsComplate();

    void getVideoDuration(long duration);

    void needPauseVideo();

    void getVideoCover(String path,String originalPath);

    void getBgmPath(String path);

    void showRenderVideoTime(int duration);

    void changFirstVideoSticker(String path);

    void isFirstAddSuccess();

    void showCreateTemplateAnim(boolean isShow);

    void showMusicBtn(boolean isShow);

    void showTextDialog(String inputText);

    void hineTextDialog();

    void addStickerTimeLine(String id,boolean isText,String text,StickerView stickerView);

    void updateTimeLineSickerText(String text,String id);

    void deleteTimeLineSicker(String id);

    void showTimeLineSickerArrow(String id);

    void modifyTimeLineSickerPath(String id,String path);

}
