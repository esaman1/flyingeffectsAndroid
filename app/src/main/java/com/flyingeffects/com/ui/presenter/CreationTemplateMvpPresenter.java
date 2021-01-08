package com.flyingeffects.com.ui.presenter;

import android.content.Context;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.ui.interfaces.model.CreationTemplateMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.CreationTemplateMvpView;
import com.flyingeffects.com.ui.model.AnimStickerModel;
import com.flyingeffects.com.ui.model.CreationTemplateMvpModel;
import com.flyingeffects.com.view.StickerView;
import com.lansosdk.box.ViewLayerRelativeLayout;
import com.lansosdk.videoeditor.DrawPadView2;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

public class CreationTemplateMvpPresenter extends BasePresenter implements CreationTemplateMvpCallback {
    private CreationTemplateMvpView creationTemplatemvpView;
    private CreationTemplateMvpModel creationTemplatemodel;

    public CreationTemplateMvpPresenter(Context context, CreationTemplateMvpView mvp_view, String mVideoPath, ViewLayerRelativeLayout viewLayerRelativeLayout, String originalPath, DrawPadView2 drawPadView2) {
        this.creationTemplatemvpView = mvp_view;
        creationTemplatemodel = new CreationTemplateMvpModel(context, this,mVideoPath,viewLayerRelativeLayout,originalPath);
    }

    public void setmVideoPath(String path){
        creationTemplatemodel.setmVideoPath(path);
    }


    public void ChangeTextStyle(String path, int type,String title){
        creationTemplatemodel.ChangeTextStyle(path,type,title);
    }

    public void onclickRelativeLayout(){
        creationTemplatemodel.onclickRelativeLayout();
    }



    public void getNowPlayingTime(long nowProgressTime,long totalTime){
        creationTemplatemodel.getNowPlayingTime(nowProgressTime,totalTime);
    }


    public void ChangeTextLabe(String str){
        creationTemplatemodel.ChangeTextLabe(str);
    }


    public void ChangeTextColor(String color0, String color1,String title){
        creationTemplatemodel.ChangeTextColor(color0,color1,title);
    }

    public void isEndTimer(){
        creationTemplatemodel.isEndTimer();
    }

    public void  ChangeTextFrame(String textBjPath, String textFramePath,String frameTitle){
        creationTemplatemodel.ChangeTextFrame(textBjPath,textFramePath,frameTitle);
    }



    public void ChangeTextFrame(String color0, String color1, String textFramePath,String frameTitle) {
        creationTemplatemodel.ChangeTextFrame(color0,color1, textFramePath,frameTitle);
    }




    public void addTextSticker(){
        creationTemplatemodel.addTextSticker();
    }

    public void CheckedChanged(boolean isChecked){
        creationTemplatemodel.CheckedChanged(isChecked);
    }

    public void setAddChooseBjPath(String path){
        creationTemplatemodel.setAddChooseBjPath(path);
    }


    public void intoOnPause(){
        creationTemplatemodel.intoOnPause();
    }


    public void GetVideoCover(String path){
        creationTemplatemodel.GetVideoCover(path);
    }

    public void addNewSticker(String path,String originalPath){
        creationTemplatemodel.addNewSticker(path,originalPath);
    }


    public void setAllStickerCenter(){
        creationTemplatemodel.setAllStickerCenter();
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


    public void initBottomLayout(ViewPager viewPager,FragmentManager fragmentManager){
        creationTemplatemodel.initBottomLayout(viewPager,fragmentManager);
    }

    public void initStickerView(String path,String originalPath){
        creationTemplatemodel.initStickerView(path,originalPath);
    }

    public void initVideoProgressView(){
        creationTemplatemodel.initVideoProgressView();
    }

    public void onDestroy(){
        creationTemplatemodel.onDestroy();
    }


    public void statisticsDuration(String path, Context context){
        creationTemplatemodel.statisticsDuration(path,context);
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
    public void ChooseMusicIndex(int index) {
        creationTemplatemvpView.ChooseMusicIndex(index);
    }

    @Override
    public void deleteFirstSticker() {
        creationTemplatemvpView.deleteFirstSticker();
    }

    @Override
    public void hideKeyBord() {
        creationTemplatemvpView.hideKeyBord();
    }

    @Override
    public void stickerOnclickCallback(String title) {
        creationTemplatemvpView.stickerOnclickCallback(title);
    }

    @Override
    public void animIsComplate() {
        creationTemplatemvpView.animIsComplate();
    }

    @Override
    public void getVideoDuration(long duration) {
        creationTemplatemvpView.getVideoDuration(duration);
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

    @Override
    public void showMusicBtn(boolean isShow) {
        creationTemplatemvpView.showMusicBtn(isShow);
    }

    @Override
    public void showTextDialog(String inputText) {
        creationTemplatemvpView.showTextDialog(inputText);
    }

    @Override
    public void hineTextDialog() {
        creationTemplatemvpView.hideTextDialog();
    }

    @Override
    public void addStickerTimeLine(String id, boolean isText, String text, StickerView stickerView) {
        creationTemplatemvpView.addStickerTimeLine(id, isText, text,stickerView);
    }

    @Override
    public void updateTimeLineSickerText(String text, String id) {
        creationTemplatemvpView.updateTimeLineSickerText(text,id);
    }

    @Override
    public void deleteTimeLineSicker(String id) {
        creationTemplatemvpView.deleteTimeLineSicker(id);
    }

    @Override
    public void showTimeLineSickerArrow(String id) {
        creationTemplatemvpView.showTimeLineSickerArrow(id);
    }

    @Override
    public void stickerFragmentClose() {
        creationTemplatemvpView.stickerFragmentClose();
    }

    @Override
    public void modifyTimeLineSickerPath(String id,String path,StickerView stickerView) {
        creationTemplatemvpView.modifyTimeLineSickerPath(id,path,stickerView);
    }

    public void toSaveVideo(String imageBjPath, boolean nowUiIsLandscape, float percentageH,int templateId,long musicStartTime,long musicEndTime,
                            long cutStartTime,long cutEndTime,String title){
        creationTemplatemodel.toSaveVideo(imageBjPath,nowUiIsLandscape,percentageH,templateId,musicStartTime,musicEndTime,cutStartTime,cutEndTime,title);
    }


    public void deleteAllTextSticker(){
        creationTemplatemodel.deleteAllTextSticker();
    }

    public void bringStickerFront(String id){
        creationTemplatemodel.bringStickerFront(id);
    }
}
