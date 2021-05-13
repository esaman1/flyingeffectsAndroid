package com.flyingeffects.com.ui.presenter;

import android.content.Context;
import android.view.View;

import com.flyingeffects.com.enity.StickerAnim;
import com.flyingeffects.com.enity.StickerTypeEntity;
import com.flyingeffects.com.ui.interfaces.contract.ICreationTemplateMvpContract;
import com.flyingeffects.com.ui.model.AnimStickerModel;
import com.flyingeffects.com.ui.model.CreationTemplateMvpModel;
import com.flyingeffects.com.view.StickerView;
import com.lansosdk.box.ViewLayerRelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * 自由创作页面presenter
 *
 * @author Coca Cola
 */
public class CreationTemplateMvpPresenter extends ICreationTemplateMvpContract.CreationTemplateMvpPresenter implements LifecycleObserver {

    private final ICreationTemplateMvpContract.ICreationTemplateMvpView creationTemplatemvpView;
    private final CreationTemplateMvpModel creationTemplatemodel;

    public CreationTemplateMvpPresenter(Context context, ICreationTemplateMvpContract.ICreationTemplateMvpView mvp_view, String mVideoPath, ViewLayerRelativeLayout viewLayerRelativeLayout, String originalPath, int from) {
        this.creationTemplatemvpView = mvp_view;
        creationTemplatemodel = new CreationTemplateMvpModel(context, this, mVideoPath, viewLayerRelativeLayout, originalPath, from);
    }

    @Override
    public void setVideoPath(String path) {
        creationTemplatemodel.setVideoPath(path);
    }


    public void changeTextStyle(String path, int type, String title) {
        creationTemplatemodel.changeTextStyle(path, type, title);
    }

    public void getNowPlayingTime(long nowProgressTime, long totalTime) {
        creationTemplatemodel.getNowPlayingTime(nowProgressTime, totalTime);
    }

    public void changeTextLabe(String str) {
        creationTemplatemodel.changeTextLabe(str);
    }


    public void changeTextColor(String color0, String color1, String title) {
        creationTemplatemodel.changeTextColor(color0, color1, title);
    }


    public void changeTextFrame(String textBjPath, String textFramePath, String frameTitle) {
        creationTemplatemodel.changeTextFrame(textBjPath, textFramePath, frameTitle);
    }


    public void changeTextFrame(String color0, String color1, String textFramePath, String frameTitle) {
        creationTemplatemodel.changeTextFrame(color0, color1, textFramePath, frameTitle);
    }


    public void addTextSticker() {
        creationTemplatemodel.addSticker("", false, false, false,
                "", false, null, false,
                StickerView.CODE_STICKER_TYPE_TEXT, null);
    }

    public void checkedChanged(boolean isChecked) {
        creationTemplatemodel.checkedChanged(isChecked);
    }

    public void setAddChooseBjPath(String path) {
        creationTemplatemodel.setAddChooseBjPath(path);
    }

    public void setNowChooseMusicId(int id) {
        creationTemplatemodel.setNowChooseMusicId(id);
    }

    public void addFragmentList(Fragment fragment) {
        creationTemplatemodel.addFragmentList(fragment);
    }

    public void addListForBottom(View view) {
        creationTemplatemodel.addListForBottom(view);
    }


    public void intoOnPause() {
        creationTemplatemodel.intoOnPause();
    }

    public void getVideoCover(String path) {
        creationTemplatemodel.getVideoCover(path);
    }

    public void addNewSticker(String path, String originalPath) {
        creationTemplatemodel.addNewSticker(path, originalPath);
    }

    public void setAllStickerCenter() {
        creationTemplatemodel.setAllStickerCenter();
    }

    public void showGifAnim(boolean isShow) {
        creationTemplatemodel.showGifAnim(isShow);
    }

    public void showAllAnim(boolean isShow) {
        creationTemplatemodel.showAllAnim(isShow);
    }


    public void initStickerView(String path, String originalPath) {
        creationTemplatemodel.initStickerView(path, originalPath);
    }

    public void initVideoProgressView() {
        creationTemplatemodel.initVideoProgressView();
    }

    public void onDestroy() {
        creationTemplatemodel.onDestroy();
    }


    public void statisticsDuration(String path, Context context) {
        creationTemplatemodel.statisticsDuration(path, context);
    }

    public void toSaveVideo(String imageBjPath, boolean nowUiIsLandscape, float percentageH, int templateId, long musicStartTime, long musicEndTime,
                            long cutStartTime, long cutEndTime, String title) {
        creationTemplatemodel.toSaveVideo(imageBjPath, nowUiIsLandscape, percentageH, templateId, musicStartTime, musicEndTime, cutStartTime, cutEndTime, title);
    }


    public void deleteAllTextSticker() {
        creationTemplatemodel.deleteAllTextSticker();
    }

    public void bringStickerFront(String id) {
        creationTemplatemodel.bringStickerFront(id);
    }

    public List<View> getListForInitBottom() {
        return creationTemplatemodel.getListForInitBottom();
    }

    public List<Fragment> getFragmentList() {
        return creationTemplatemodel.getFragmentList();
    }


    @Override
    public void itemClickForStickView(AnimStickerModel stickView) {
        creationTemplatemvpView.itemClickForStickView(stickView);
    }

    @Override
    public void hasPlayingComplete() {
        creationTemplatemvpView.hasPlayingComplete();
    }

    @Override
    public void chooseMusicIndex(int index) {
        creationTemplatemvpView.chooseMusicIndex(index);
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
    public void getVideoCover(String path, String originalPath) {
        creationTemplatemvpView.getVideoCover(path, originalPath);
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
        creationTemplatemvpView.addStickerTimeLine(id, isText, text, stickerView);
    }

    @Override
    public void updateTimeLineSickerText(String text, String id) {
        creationTemplatemvpView.updateTimeLineSickerText(text, id);
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
    public void dismissLoadingDialog() {
        creationTemplatemvpView.dismissLoadingDialog();
    }

    @Override
    public void setDialogProgress(String title, int dialogProgress, String content) {
        creationTemplatemvpView.setDialogProgress(title, dialogProgress, content);
    }

    @Override
    public void chooseFrame(String path) {
        creationTemplatemvpView.chooseFrame(path);
    }

    @Override
    public void dismissStickerFrame() {
        creationTemplatemvpView.dismissStickerFrame();
    }

    @Override
    public void dismissTextStickerFrame() {
        creationTemplatemvpView.dismissTextStickerFrame();
    }

    @Override
    public void chooseCheckBox(int i) {
        creationTemplatemvpView.chooseCheckBox(i);
    }

    @Override
    public long getDuration() {
        return creationTemplatemodel.getDuration();
    }

    @Override
    public void returnStickerTypeList(ArrayList<StickerTypeEntity> list) {
        creationTemplatemvpView.returnStickerTypeList(list);
    }

    @Override
    public void getStickerTypeList() {
        creationTemplatemodel.getStickerTypeList();
    }

    @Override
    public void chooseInitMusic() {
        creationTemplatemodel.chooseInitMusic();
    }

    @Override
    public void startPlayAnim(int position, boolean isClearAllAnim, StickerView targetStickerView, boolean isFromPreview) {
        creationTemplatemodel.startPlayAnim(position, isClearAllAnim, targetStickerView, isFromPreview);
    }

    @Override
    public void modificationSingleAnimItemIsChecked(int i) {
        creationTemplatemodel.modificationSingleAnimItemIsChecked(i);
    }

    @Override
    public List<StickerAnim> getListAllAnim() {
        return creationTemplatemodel.getListAllAnim();
    }

    @Override
    public void chooseNowStickerMaterialMusic() {
        creationTemplatemodel.chooseNowStickerMaterialMusic();
    }

    @Override
    public void chooseTemplateMusic(boolean b) {
        creationTemplatemodel.chooseTemplateMusic(b);
    }

    @Override
    public void chooseAddChooseBjPath() {
        creationTemplatemodel.chooseAddChooseBjPath();
    }

    @Override
    public void closeAllAnim() {
        creationTemplatemvpView.closeAllAnim();
    }

    @Override
    public void stopAllAnim() {
        creationTemplatemodel.stopAllAnim();
    }

    @Override
    public void deleteAllSticker() {
        creationTemplatemodel.deleteAllSticker();
    }

    @Override
    public void addSticker(String stickerPath, String title) {
        creationTemplatemodel.addSticker(stickerPath, title);
    }

    @Override
    public void copyGif(String fileName, String copyName, String title) {
        creationTemplatemodel.copyGif(fileName, copyName, title);
    }

    @Override
    public void showLoading() {
        creationTemplatemvpView.showLoading();
    }

    @Override
    public void modifyTimeLineSickerPath(String id, String path, StickerView stickerView) {
        creationTemplatemvpView.modifyTimeLineSickerPath(id, path, stickerView);
    }

    public void clearCheckBox() {
        creationTemplatemvpView.clearCheckBox();
    }

//    @Override
//    public BaseModel createModel() {
//        return new CreationTemplateMvpModel(context, this, mVideoPath, viewLayerRelativeLayout, originalPath, from);;
//    }

}
