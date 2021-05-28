package com.flyingeffects.com.ui.presenter;

import android.app.Activity;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.enity.SubtitleEntity;
import com.flyingeffects.com.ui.interfaces.model.JadeFontMakeMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.JadeFontMakeMvpView;
import com.flyingeffects.com.ui.model.JadeFontMakeModel;

import java.util.List;

/**
 * @author ZhouGang
 * @date 2021/5/25
 */
public class JadeFontMakePresenter extends BasePresenter implements JadeFontMakeMvpCallback {

    JadeFontMakeMvpView mMvpView;
    JadeFontMakeModel mMakeModel;

    public JadeFontMakePresenter(Activity context, JadeFontMakeMvpView mMvpView, String videoPath) {
        this.mMvpView = mMvpView;
        mMakeModel = new JadeFontMakeModel(context, videoPath, this);
    }

    @Override
    public void identifySubtitle(List<SubtitleEntity> subtitles, boolean isVideoInAudio, String audioPath) {
        mMvpView.identifySubtitle(subtitles, isVideoInAudio, audioPath);
    }

    @Override
    public void getBgmPath(String bgmPath) {
        mMvpView.getBgmPath(bgmPath);
    }

    @Override
    public void chooseVideoInAudio(int index) {
        mMakeModel.chooseVideoInAudio(index);
    }

    @Override
    public void chooseNowStickerMaterialMusic(int index) {
        mMakeModel.chooseNowStickerMaterialMusic(index);
    }

    @Override
    public void extractedAudio(String path,int index) {
        mMakeModel.extractedAudio(path,index);
    }

    @Override
    public void chooseCheckBox(int i) {
        mMvpView.chooseCheckBox(i);
    }

    @Override
    public void clearCheckBox() {
        mMvpView.clearCheckBox();
    }

    public void startIdentify(boolean isVideoInAudio, String videoPath, String audioPath) {
        mMakeModel.startIdentify(isVideoInAudio, videoPath, audioPath);
    }

    public void setExtractedAudioBjMusicPath(String bjMusicPath){
        mMakeModel.setExtractedAudioBjMusicPath(bjMusicPath);
    }

    public void getNowPlayingTimeViewShow(long progressBarProgress, long endTime) {

    }

}
