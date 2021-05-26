package com.flyingeffects.com.ui.interfaces.model;

import com.flyingeffects.com.enity.SubtitleEntity;

import java.util.List;

/**
 * @author ZhouGang
 * @date 2021/5/25
 * 玉体字制作
 */
public interface JadeFontMakeMvpCallback {

    void identifySubtitle(List<SubtitleEntity> subtitles,boolean isVideoInAudio,String audioPath);

    void getBgmPath(String bgmPath);

    void chooseVideoInAudio();

    void chooseNowStickerMaterialMusic();

    void extractedAudio();

    void chooseCheckBox(int i);

    void clearCheckBox();

}
