package com.flyingeffects.com.ui.presenter;

import android.content.Context;
import android.graphics.Bitmap;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.enity.TemplateThumbItem;
import com.flyingeffects.com.enity.new_fag_template_item;
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

    public TemplatePresenter(Context context, TemplateMvpView mvp_view, String fromTo, String templateName) {
        this.home_mvpView = mvp_view;
        home_model = new TemplateMvpModel(context, this, fromTo, templateName);
    }


    public void intoMattingVideo(String path, String name) {
        home_model.intoMattingVideo(path, name);
    }

    public void StopBgmMusic() {
        home_model.StopBgmMusic();
    }

    public void chooseBj(new_fag_template_item templateItem) {
        home_model.chooseBj(templateItem);
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


    public void renderVideo(String mTemplateFolder, String mAudio1Path, boolean isPreview, int nowTemplateIsAnim, List<String> originalPath) {
        home_model.renderVideo(mTemplateFolder, mAudio1Path, isPreview, nowTemplateIsAnim, originalPath);
    }


    public void loadTemplate(String filePath, AssetDelegate delegate, int nowTemplateIsAnim, int nowTemplateIsMattingVideo) {
        home_model.loadTemplate(filePath, delegate, nowTemplateIsAnim, nowTemplateIsMattingVideo);
    }


    public void ChangeMaterial(List<String> list, int maxChooseNum, int needAssetsCount) {
        home_model.ChangeMaterial(list, maxChooseNum, needAssetsCount);
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
        home_mvpView.ChangeMaterialCallback(listItem, list_all, listAssets);
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


    public void StatisticsToSave(String templateId) {
        home_model.StatisticsToSave(templateId);

    }


    public void toDressUp(String path, String templateId) {
        home_model.toDressUp(path, templateId);
    }


}
