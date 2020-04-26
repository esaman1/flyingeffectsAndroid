package com.flyingeffects.com.ui.presenter;

import android.content.Context;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.ui.interfaces.model.VideoCropMVPCallback;
import com.flyingeffects.com.ui.interfaces.view.VideoCropMVPView;
import com.flyingeffects.com.ui.model.VideoCropMVPModel;
import com.flyingeffects.com.view.RangeSeekBarView;
import com.flyingeffects.com.view.RoundImageView;
import com.flyingeffects.com.view.VideoFrameRecycler;
import com.lansosdk.videoeditor.DrawPadView2;

public class VideoCropMVPPresenter extends BasePresenter implements VideoCropMVPCallback {
    private VideoCropMVPView mvp_view;
    private VideoCropMVPModel model;
    public VideoCropMVPPresenter(Context context, VideoCropMVPView mvp_view) {
        this.mvp_view = mvp_view;
        model = new VideoCropMVPModel(context, this);
    }
    public void initDrawpad(DrawPadView2 drawPadView2, String path){
        model.initDrawpad(drawPadView2,path);
    }

    public void changeVideoZoom(int progress){
        model.changeVideoZoom(progress);
    }

    @Override
    public void initTrimmer() {
        mvp_view.initTrimmer();
    }

    @Override
    public void showCropTotalTime(long durationMs, long startTimeMs, long endTimeMs) {
        mvp_view.showTimeMs(durationMs,startTimeMs,endTimeMs);
    }

    @Override
    public void updateCursor(float currentX) {
        mvp_view.updateCursor(currentX);
    }

    @Override
    public void hideCursor() {
        mvp_view.hideCursor();
    }

    @Override
    public void showCursor() {
        mvp_view.showCursor();
    }

    @Override
    public void finishCrop(String path) {
        mvp_view.finishCrop(path);
    }

    @Override
    public void getRealCutTime(float RealCutTime) {
        mvp_view.getRealCutTime(RealCutTime);
    }

    public void setUpTrimmer(RangeSeekBarView mRangeSeekBarView, VideoFrameRecycler mTimeLineView, RoundImageView progressCursor) {
        model.initTrimmer(mRangeSeekBarView,mTimeLineView,progressCursor);
    }

    public void onDestroy() {
        model.onDestroy();
    }

    public void onPause() {
        model.onPause();
    }

    public void onResume() {
        model.onResume();
    }

    public void saveVideo() {
        model.saveVideo();
    }
}
