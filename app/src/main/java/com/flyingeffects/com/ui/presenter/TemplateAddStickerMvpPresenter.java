package com.flyingeffects.com.ui.presenter;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.ViewPager;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.ui.interfaces.model.TemplateAddStickerMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.TemplateAddStickerMvpView;
import com.flyingeffects.com.ui.interfaces.view.TemplateAddStickerMvpView;
import com.flyingeffects.com.ui.model.TemplateAddStickerMvpModel;
import com.flyingeffects.com.ui.model.TemplateAddStickerMvpModel;
import com.flyingeffects.com.ui.view.activity.CreationTemplateActivity;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.HorizontalListView;
import com.flyingeffects.com.view.MyScrollView;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.lansosdk.box.ViewLayerRelativeLayout;

import java.io.File;

public class TemplateAddStickerMvpPresenter extends BasePresenter implements TemplateAddStickerMvpCallback {
    private TemplateAddStickerMvpView TemplateAddStickermvpView;
    private TemplateAddStickerMvpModel TemplateAddStickermodel;
    private LinearLayout ll_space;

    public TemplateAddStickerMvpPresenter(Context context, TemplateAddStickerMvpView mvp_view,LinearLayout ll_space,ViewLayerRelativeLayout viewLayerRelativeLayout,  String mVideoPath) {
        this.TemplateAddStickermvpView = mvp_view;
        this.ll_space=ll_space;
        TemplateAddStickermodel = new TemplateAddStickerMvpModel(context, this,viewLayerRelativeLayout,mVideoPath);
    }


    public void deleteAllTextSticker(){
        TemplateAddStickermodel.deleteAllTextSticker();
    }

    public void ChangeTextStyle(String path, int type){
        TemplateAddStickermodel.ChangeTextStyle(path,type);
    }


    public void ChangeTextColor(String color0, String color1){
        TemplateAddStickermodel.ChangeTextColor(color0,color1);
    }


    public void alertAlbumUpdate(boolean isSuccess){
        TemplateAddStickermodel.alertAlbumUpdate(isSuccess);
    }

    public void ChangeTextLabe(String str){
        TemplateAddStickermodel.ChangeTextLabe(str);
    }


    public void addTextSticker(){
        TemplateAddStickermodel.addTextSticker();
    }


    public void onPause(){
    }

    public void onDestroy(){
        TemplateAddStickermodel.onDestroy();
    }


    public void initVideoProgressView(HorizontalListView mTimeLineView){
        TemplateAddStickermodel.initVideoProgressView(mTimeLineView);
    }

    public void requestStickersList(){
        TemplateAddStickermodel.requestStickersList(true);
    }

    public void initBottomLayout(ViewPager viewPager){
        TemplateAddStickermodel.initBottomLayout(viewPager);
    }


    public void toSaveVideo(float percentageH){
        TemplateAddStickermodel.toSaveVideo(percentageH);
    }

    public void setPlayerViewSize(PlayerView playerView, MyScrollView scrollView, ViewLayerRelativeLayout viewLayerRelativeLayout) {
        LinearLayout.LayoutParams RelativeLayoutParams = (LinearLayout.LayoutParams) playerView.getLayoutParams();
        float oriRatio = 9f / 16f;
        //横屏模式下切换到了竖屏
        scrollView.post(() -> {
            RelativeLayout.LayoutParams RelativeLayoutParams2 = (RelativeLayout.LayoutParams) scrollView.getLayoutParams();
            int height = ll_space.getHeight();
            RelativeLayoutParams2.height = height;
            RelativeLayoutParams2.width = Math.round(1f * height * oriRatio);
            scrollView.setLayoutParams(RelativeLayoutParams2);
            RelativeLayoutParams.width = Math.round(1f * height * oriRatio);
            RelativeLayoutParams.height = height;
            playerView.setLayoutParams(RelativeLayoutParams);
            //设置预览编辑界面
            viewLayerRelativeLayout.setLayoutParams(RelativeLayoutParams2);
        });
    }


    public void showGifAnim(boolean isShow){
        TemplateAddStickermodel.showGifAnim(isShow);
    }

    public void showAllAnim(boolean isSHow){
        TemplateAddStickermodel.showAllAnim(isSHow);
    }


    @Override
    public void animIsComplate() {
        TemplateAddStickermvpView.animIsComplate();
    }

    @Override
    public void needPauseVideo() {
        TemplateAddStickermvpView.needPauseVideo();
    }

    @Override
    public void getVideoDuration(int duration, int thumbCount) {

        TemplateAddStickermvpView.getVideoDuration(duration,thumbCount);
    }

    @Override
    public void setgsyVideoProgress(int progress) {
        TemplateAddStickermvpView.setgsyVideoProgress(progress);
    }

    @Override
    public void showTextDialog(String inputText) {
        TemplateAddStickermvpView.showTextDialog(inputText);
    }

    @Override
    public void hideTextDialog() {
        TemplateAddStickermvpView.hideTextDialog();
    }
}
