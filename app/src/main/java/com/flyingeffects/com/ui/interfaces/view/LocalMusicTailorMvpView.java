package com.flyingeffects.com.ui.interfaces.view;



public interface LocalMusicTailorMvpView {
    void showCharView(int[] date,int numFrame);
    void onPlayerCompletion();
    void isAudioCutDone(String audioPath);
    void initComplate();
}
