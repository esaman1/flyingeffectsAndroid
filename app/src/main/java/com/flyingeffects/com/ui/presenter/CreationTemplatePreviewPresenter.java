package com.flyingeffects.com.ui.presenter;

import android.content.Context;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.ui.interfaces.model.CreationTemplatePreviewMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.CreationTemplatePreviewMvpView;
import com.flyingeffects.com.ui.model.CreationTemplatePreviewModel;
import com.flyingeffects.com.view.RangeSeekBarView;
import com.flyingeffects.com.view.RoundImageView;
import com.flyingeffects.com.view.VideoFrameRecycler;
import com.orhanobut.hawk.Hawk;
;


public class CreationTemplatePreviewPresenter extends BasePresenter implements CreationTemplatePreviewMvpCallback {
    private CreationTemplatePreviewMvpView CreationTemplatePreviewmvpView;
    private CreationTemplatePreviewModel CreationTemplatePreviewModel;

    public CreationTemplatePreviewPresenter(Context context, CreationTemplatePreviewMvpView mvp_view,String videoPath) {
        this.CreationTemplatePreviewmvpView = mvp_view;
        CreationTemplatePreviewModel = new CreationTemplatePreviewModel(context, this,videoPath);
    }

    public void toSaveVideo(boolean hasShowStimulateAd,boolean nowUiIsLandscape){
        CreationTemplatePreviewModel.toSaveVideo(hasShowStimulateAd,nowUiIsLandscape);

    }





    public void destroyTimer(){
        CreationTemplatePreviewModel.destroyTimer();
    };


    public void setUpTrimmer(RangeSeekBarView mRangeSeekBarView, VideoFrameRecycler mTimeLineView, RoundImageView progressCursor,long duration){
        CreationTemplatePreviewModel.initTrimmer(mRangeSeekBarView,mTimeLineView,progressCursor,duration);
    }


    public void initTimer(){
        CreationTemplatePreviewModel.ToInitTimer();
    }

    public void requestData(){

    }

    @Override
    public void initTrimmer() {

    }

    @Override
    public void showCropTotalTime(long durationMs, long startTimeMs, long endTimeMs) {

    }

    @Override
    public void updateCursor(float currentX) {
        CreationTemplatePreviewmvpView.updateCursor(currentX);
    }

    @Override
    public void hideCursor() {

    }

    @Override
    public void showCursor() {

    }

    @Override
    public void finishCrop(String path) {

    }

    @Override
    public void getRealCutTime(float RealCutTime) {

    }

    @Override
    public void seekToPosition(long position,float allDuration) {
        CreationTemplatePreviewmvpView.seekToPosition(position,allDuration);
    }

    @Override
    public void isSaveToAlbum(String path,boolean isAdSuccess) {
        CreationTemplatePreviewmvpView.isSaveToAlbum(path, isAdSuccess);
    }
}
