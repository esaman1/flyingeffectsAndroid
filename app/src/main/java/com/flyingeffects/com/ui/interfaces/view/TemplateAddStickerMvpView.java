package com.flyingeffects.com.ui.interfaces.view;

public interface TemplateAddStickerMvpView {
    void animIsComplate();
    void needPauseVideo();
    void getVideoDuration(int duration,int thumbCount );
    void setgsyVideoProgress(int progress);
    void showTextDialog(String inputText);
    void hideTextDialog();

    void stickerOnclickCallback(String str);
}
