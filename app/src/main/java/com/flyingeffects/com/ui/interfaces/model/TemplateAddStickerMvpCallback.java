package com.flyingeffects.com.ui.interfaces.model;

public interface TemplateAddStickerMvpCallback {
    void animIsComplate();

    void needPauseVideo();

    void getVideoDuration(int duration,int thumbCount );

    void setgsyVideoProgress(int progress);

    void showTextDialog(String inputText);

    void hideTextDialog();

    void showAdCallback();

    void stickerOnclickCallback(String title);

    void hideKeyBord();
}
