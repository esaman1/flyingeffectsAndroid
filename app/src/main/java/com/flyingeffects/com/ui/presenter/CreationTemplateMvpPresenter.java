package com.flyingeffects.com.ui.presenter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.ui.interfaces.model.CreationTemplateMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.CreationTemplateMvpView;
import com.flyingeffects.com.ui.model.AnimStickerModel;
import com.flyingeffects.com.ui.model.CreationTemplateMvpModel;
import com.flyingeffects.com.view.HorizontalListView;
import com.lansosdk.box.ViewLayerRelativeLayout;
import com.lansosdk.videoeditor.DrawPadView2;

public class CreationTemplateMvpPresenter extends BasePresenter implements CreationTemplateMvpCallback {
    private CreationTemplateMvpView creationTemplatemvpView;
    private CreationTemplateMvpModel creationTemplatemodel;

    public CreationTemplateMvpPresenter(Context context, CreationTemplateMvpView mvp_view, String mVideoPath, ViewLayerRelativeLayout viewLayerRelativeLayout, String originalPath, DrawPadView2 drawPadView2) {
        this.creationTemplatemvpView = mvp_view;
        creationTemplatemodel = new CreationTemplateMvpModel(context, this,mVideoPath,viewLayerRelativeLayout,originalPath,drawPadView2);
    }

    public void setmVideoPath(String path){
        creationTemplatemodel.setmVideoPath(path);
    }

    public void CheckedChanged(boolean isChecked){
        creationTemplatemodel.CheckedChanged(isChecked);
    }


    public void GetVideoCover(String path){
        creationTemplatemodel.GetVideoCover(path);
    }


    public void scrollToPosition(int position){
        creationTemplatemodel.scrollToPosition(position);
    }


    public void addNewSticker(String path,String originalPath){
        creationTemplatemodel.addNewSticker(path,originalPath);
    }


    public void showGifAnim(boolean isShow){
        creationTemplatemodel.showGifAnim(isShow);
    }

    public void showAllAnim(boolean isSHow){
        creationTemplatemodel.showAllAnim(isSHow);
    }

    public void chooseAnim(int pageNum){
        creationTemplatemodel.chooseAnim(pageNum);
    }


    public void initBottomLayout(ViewPager viewPager){
        creationTemplatemodel.initBottomLayout(viewPager);
    }

    public void initStickerView(String path,String originalPath){
        creationTemplatemodel.initStickerView(path,originalPath);
    }

    public void initVideoProgressView(RecyclerView mTimeLineView){
        creationTemplatemodel.initVideoProgressView(mTimeLineView);
    }

    public void initVideoProgressView(HorizontalListView mTimeLineView){
        creationTemplatemodel.initVideoProgressView(mTimeLineView);
    }

    public void requestStickersList(){
        creationTemplatemodel.requestStickersList(true);
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

    @Override
    public void getVideoDuration(int duration,int thumbCount) {
        creationTemplatemvpView.getVideoDuration(duration,thumbCount);
    }

    @Override
    public void needPauseVideo() {
        creationTemplatemvpView.needPauseVideo();
    }

    @Override
    public void getVideoCover(String path,String originalPath) {
        creationTemplatemvpView.getVideoCover(path,originalPath);
    }

    @Override
    public void getBgmPath(String path) {
        creationTemplatemvpView.getBgmPath(path);
    }

    @Override
    public void showRenderVideoTime(int duration) {
//        creationTemplatemvpView.showRenderVideoTime(duration);
    }

    @Override
    public void changFirstVideoSticker(String path) {
        creationTemplatemvpView.changFirstVideoSticker(path);
    }

    @Override
    public void isFirstAddSuccess() {
        creationTemplatemvpView.isFirstAddSuccess();
    }

    @Override
    public void showCreateTemplateAnim(boolean isShow) {
        creationTemplatemvpView.showCreateTemplateAnim(isShow);
    }


    public void toSaveVideo(String imageBjPath){
        creationTemplatemodel.toSaveVideo(imageBjPath);
    }
}
