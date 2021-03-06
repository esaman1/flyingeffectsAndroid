package com.flyingeffects.com.ui.presenter;

import android.content.Context;
import android.graphics.Bitmap;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.entity.TemplateThumbItem;
import com.flyingeffects.com.ui.interfaces.model.TemplateMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.TemplateMvpView;
import com.flyingeffects.com.ui.model.TemplateMvpModel;
import com.shixing.sxve.ui.AssetDelegate;
import com.shixing.sxve.ui.model.TemplateModel;

import java.util.ArrayList;
import java.util.List;

public class TemplatePresenter extends BasePresenter implements TemplateMvpCallback {
    private TemplateMvpView home_mvpView;
    private TemplateMvpModel home_model;

    public TemplatePresenter(Context context, TemplateMvpView mvp_view, String fromTo, String templateName,String templateId,String templateType) {
        this.home_mvpView = mvp_view;
        home_model = new TemplateMvpModel(context, this, fromTo, templateName,templateId,templateType);
    }

    public void intoMattingVideo(String path, String name) {
        home_model.intoMattingVideo(path, name);
    }

    public void StopBgmMusic() {
        home_model.StopBgmMusic();
    }

    public void getBjMusic(String videoPath) {
        home_model.getBjMusic(videoPath);
    }


    public void getButtomIcon(String path) {
        home_model.getButtomIcon(path);
    }

    public void alertAlbumUpdate(boolean isSuccess) {
//        home_model.alertAlbumUpdate(isSuccess);
    }


    public void onDestroy() {
        home_model.onDestroy();
    }


    public void getReplaceableFilePath() {
        home_model.getReplaceableFilePath();
    }

    public void playBGMMusic(String path, int progress) {
        home_model.playBGMMusic(path, progress);
    }


    public void getMattingVideoCover(String path) {
        home_model.getMattingVideoCover(path);
    }


    public void renderVideo(String mTemplateFolder, String mAudio1Path, boolean isPreview, int nowTemplateIsAnim, List<String> originalPath,boolean nowIsGifTemplate) {
        home_model.renderVideo(mTemplateFolder, mAudio1Path, isPreview, nowTemplateIsAnim, originalPath,nowIsGifTemplate);
    }


    public void SaveSpecialTemplate(int api_type,boolean nowIsGifTemplate,int needAssetsCount, boolean isMatting){
        home_model.SaveSpecialTemplate(api_type,nowIsGifTemplate,needAssetsCount,isMatting);
    }


    public void loadTemplate(String filePath, AssetDelegate delegate, int nowTemplateIsAnim, int nowTemplateIsMattingVideo,boolean isToSing) {
        home_model.loadTemplate(filePath, delegate, nowTemplateIsAnim, nowTemplateIsMattingVideo,isToSing);
    }


    public void changeMaterial(List<String> list, int maxChooseNum, int needAssetsCount) {
        home_model.changeMaterial(list, maxChooseNum, needAssetsCount);
    }


    @Override
    public void completeTemplate(TemplateModel templateModel) {
        home_mvpView.completeTemplate(templateModel);
    }

    @Override
    public void toPreview(String path) {
        home_mvpView.toPreview(path);
    }

    @Override
    public void ChangeMaterialCallback(ArrayList<TemplateThumbItem> listItem, List<String> list_all, List<String> listAssets) {
        home_mvpView.changeMaterialCallback(listItem, list_all, listAssets);
    }

    @Override
    public void returnReplaceableFilePath(String[] paths) {
        home_mvpView.returnReplaceableFilePath(paths);
    }

    @Override
    public void getCartoonPath(String path) {
        home_mvpView.getCartoonPath(path);
    }

    @Override
    public void showMattingVideoCover(Bitmap bitmap, String path) {
        home_mvpView.showMattingVideoCover(bitmap, path);
    }

    @Override
    public void ChangeMaterialCallbackForVideo(String originalPath, String path, boolean needMatting) {
        home_mvpView.changeMaterialCallbackForVideo(originalPath, path, needMatting);
    }

    @Override
    public void showBottomIcon(String path) {
        home_mvpView.showBottomIcon(path);
    }

    @Override
    public void getSpliteMusic(String path) {
        home_mvpView.getSpliteMusic(path);
    }

    @Override
    public void GetChangeDressUpData(List<String> paths) {
        home_mvpView.GetChangeDressUpData(paths);
    }

    @Override
    public void setDialogProgress(int progress) {
        home_mvpView.setDialogProgress(progress);
    }

    @Override
    public void setDialogDismiss() {
        home_mvpView.setDialogDismiss();
    }

    @Override
    public void showProgressDialog() {
        home_mvpView.showProgressDialog();
    }

    public void statisticsToSave(String templateId) {
        home_model.statisticsToSave(templateId);

    }

    public void toDressUp(String path, String templateId) {
        home_model.toDressUp(path, templateId);
    }

}
