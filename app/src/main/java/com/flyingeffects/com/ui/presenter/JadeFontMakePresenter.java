package com.flyingeffects.com.ui.presenter;

import android.app.Activity;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.entity.SubtitleEntity;
import com.flyingeffects.com.ui.interfaces.model.JadeFontMakeMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.JadeFontMakeMvpView;
import com.flyingeffects.com.ui.model.JadeFontMakeModel;
import com.imaginstudio.imagetools.pixellab.textContainer;

import java.util.List;

import androidx.lifecycle.LifecycleObserver;

/**
 * @author ZhouGang
 * @date 2021/5/25
 */
public class JadeFontMakePresenter extends BasePresenter implements JadeFontMakeMvpCallback, LifecycleObserver {

    JadeFontMakeMvpView mMvpView;
    JadeFontMakeModel mMakeModel;

    public JadeFontMakePresenter(Activity context, JadeFontMakeMvpView mMvpView, String videoPath,String imagePath) {
        this.mMvpView = mMvpView;
        mMakeModel = new JadeFontMakeModel(context, videoPath,imagePath, this);
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

    @Override
    public void showLoadingDialog() {
        mMvpView.showLoadingDialog();
    }

    @Override
    public void setDialogProgress(String title, int dialogProgress, String content) {
        mMvpView.setDialogProgress(title,dialogProgress,content);
    }

    @Override
    public void dismissLoadingDialog() {
        mMvpView.dismissLoadingDialog();
    }

    public void startIdentify(boolean isVideoInAudio, String videoPath, String audioPath) {
        mMakeModel.startIdentify(isVideoInAudio, videoPath, audioPath);
    }

    public void setExtractedAudioBjMusicPath(String bjMusicPath){
        mMakeModel.setExtractedAudioBjMusicPath(bjMusicPath);
    }

    public void getNowPlayingTimeViewShow(textContainer textContain, long progressBarProgress, long endTime) {
        mMakeModel.getNowPlayingTimeViewShow(textContain, progressBarProgress, endTime);
    }

    public void saveVideo(long cutStartTime, long cutEndTime, boolean nowUiIsLandscape, float percentageH,textContainer textContain) {
        mMakeModel.saveVideo(cutStartTime,cutEndTime,nowUiIsLandscape,percentageH,textContain);
    }

}
