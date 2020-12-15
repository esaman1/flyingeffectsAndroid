package com.flyingeffects.com.ui.interfaces.view;

import com.flyingeffects.com.ui.model.AnimStickerModel;
import com.flyingeffects.com.view.StickerView;

public interface CreationTemplateMvpView {

    void ItemClickForStickView(AnimStickerModel stickView);

    void hasPlayingComplete();

    void ChooseMusicIndex(int index);

    void deleteFirstSticker();

    void stickerOnclickCallback(String str);

    void showTextDialog(String inputText);

    void hideTextDialog();

    void getVideoDuration(long duration);

    void needPauseVideo();

    void getVideoCover(String path,String originalPath);

    void getBgmPath(String path);

    void hideKeyBord();

    void changFirstVideoSticker(String path);

    void isFirstAddSuccess();

    void showCreateTemplateAnim(boolean isShow);


    void showMusicBtn(boolean isShow);

    void animIsComplate();

    void addStickerTimeLine(String id,boolean isText,String text,StickerView stickerView);

    void updateTimeLineSickerText(String text,String id);

    void deleteTimeLineSicker(String id);

    void showTimeLineSickerArrow(String id);

    void modifyTimeLineSickerPath(String id,String path);
}
