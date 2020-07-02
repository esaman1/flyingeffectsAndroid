package com.flyingeffects.com.ui.presenter;

import android.content.Context;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.ui.interfaces.model.PreviewUpAndDownMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.PreviewUpAndDownMvpView;
import com.flyingeffects.com.ui.model.PreviewUpAndDownMvpModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

public class PreviewUpAndDownMvpPresenter extends BasePresenter implements PreviewUpAndDownMvpCallback {
    private PreviewUpAndDownMvpView previewUpAndDownMvpView;
    private PreviewUpAndDownMvpModel previewUpAndDownMvpModel;

    public PreviewUpAndDownMvpPresenter(Context context, PreviewUpAndDownMvpView mvp_view) {
        this.previewUpAndDownMvpView = mvp_view;
        previewUpAndDownMvpModel = new PreviewUpAndDownMvpModel(context, this);
    }


    public void downZip(String url,String zipPid){
        previewUpAndDownMvpModel.prepareDownZip(url,zipPid);
    }





    public void initSmartRefreshLayout(SmartRefreshLayout smartRefreshLayout){
        previewUpAndDownMvpModel.initSmartRefreshLayout(smartRefreshLayout);
    }


    public void collectTemplate(String templateId,String title,String template_type){
        previewUpAndDownMvpModel.collectTemplate(templateId, title,template_type);
    }

    public void DownVideo(String path,String imagePath,String id){
        previewUpAndDownMvpModel.DownVideo(path,imagePath,id,false);
    }


    @Override
    public void collectionResult() {
        previewUpAndDownMvpView.collectionResult();
    }

    @Override
    public void hasLogin(boolean hasLogin) {
        previewUpAndDownMvpView.hasLogin(hasLogin);
    }

    @Override
    public void downVideoSuccess(String path, String imagePath) {
        previewUpAndDownMvpView.downVideoSuccess(path,imagePath);
    }

    @Override
    public void getTemplateFileSuccess(String filePath) {
        previewUpAndDownMvpView.getTemplateFileSuccess(filePath);
    }

    @Override
    public void showDownProgress(int progress) {
        previewUpAndDownMvpView.showDownProgress(progress);
    }

    public void requestUserInfo(){
        previewUpAndDownMvpModel.requestUserInfo();
    }


}