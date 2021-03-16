package com.flyingeffects.com.ui.interfaces.view;


public interface LocalMusicTailorMvpView {
    void showCharView(int[] date, int numFrame);

    void onPlayerCompletion();

    void isAudioCutDone(String audioPath, String originalPath);

    void initComplate();

    void onStopSeekThumbs(float startPercent, float endPercent);

    void setLoadProgress(int progress);
}
