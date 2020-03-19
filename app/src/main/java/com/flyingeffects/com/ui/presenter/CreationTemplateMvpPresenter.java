package com.flyingeffects.com.ui.presenter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.ui.interfaces.model.CreationTemplateMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.CreationTemplateMvpView;
import com.flyingeffects.com.ui.model.AnimStickerModel;
import com.flyingeffects.com.ui.model.CreationTemplateMvpModel;
import com.flyingeffects.com.view.VideoFrameRecycler;
import com.flyingeffects.com.view.lansongCommendView.StickerView;
import com.lansosdk.box.ViewLayerRelativeLayout;
import com.lansosdk.videoeditor.DrawPadView;

public class CreationTemplateMvpPresenter extends BasePresenter implements CreationTemplateMvpCallback {
    private CreationTemplateMvpView creationTemplatemvpView;
    private CreationTemplateMvpModel creationTemplatemodel;

    public CreationTemplateMvpPresenter(Context context, CreationTemplateMvpView mvp_view,String mVideoPath, ViewLayerRelativeLayout viewLayerRelativeLayout) {
        this.creationTemplatemvpView = mvp_view;
        creationTemplatemodel = new CreationTemplateMvpModel(context, this,mVideoPath,viewLayerRelativeLayout);
    }


    public void showGifAnim(boolean isShow){
        creationTemplatemodel.showGifAnim(isShow);
    }


    public void initBottomLayout(ViewPager viewPager){
        creationTemplatemodel.initBottomLayout(viewPager);
    }

    public void initStickerView(String path){
        creationTemplatemodel.initStickerView(path);
    }

    public void initVideoProgressView(RecyclerView mTimeLineView){
        creationTemplatemodel.initVideoProgressView(mTimeLineView);
    }



    public void requestStickersList(){
        creationTemplatemodel.requestStickersList();
    }

    public void onDestroy(){
        creationTemplatemodel.onDestroy();
    }


    @Override
    public void ItemClickForStickView(AnimStickerModel stickView) {
        creationTemplatemvpView.ItemClickForStickView(stickView);
    }

    @Override
    public void hasPlayingComplete() {
        creationTemplatemvpView.hasPlayingComplete();
    }

    @Override
    public void setgsyVideoProgress(int progress) {
        creationTemplatemvpView.setgsyVideoProgress(progress);
    }



    public void toSaveVideo(){
        creationTemplatemodel.toSaveVideo();
    }
}
