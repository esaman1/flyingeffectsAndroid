package com.flyingeffects.com.ui.interfaces.view;

import com.flyingeffects.com.entity.SubtitleEntity;

import java.util.List;

/**
 * @author ZhouGang
 * @date 2021/5/25
 * 玉体字制作
 */
public interface JadeFontMakeMvpView {

    void identifySubtitle(List<SubtitleEntity> subtitles,boolean isVideoInAudio,String audioPath);

    void getBgmPath(String bgmPath);

    void clearCheckBox();

    void chooseCheckBox(int i);

    void showLoadingDialog();

    void setDialogProgress(String title, int dialogProgress, String content);

    void dismissLoadingDialog();
}
