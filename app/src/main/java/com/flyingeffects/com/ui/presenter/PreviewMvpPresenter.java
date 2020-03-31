package com.flyingeffects.com.ui.presenter;

import android.content.Context;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.enity.TemplateType;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.ui.interfaces.model.PreviewMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.PreviewMvpView;
import com.flyingeffects.com.ui.model.PreviewMvpModel;

import java.util.List;

public class PreviewMvpPresenter extends BasePresenter implements PreviewMvpCallback {
    private PreviewMvpView PreviewView;
    private PreviewMvpModel PreviewModel;

    public PreviewMvpPresenter(Context context, PreviewMvpView mvp_view) {
        this.PreviewView = mvp_view;
        PreviewModel = new PreviewMvpModel(context, this);
    }

    public void DownVideo(String path,String imagePath,String id){
        PreviewModel.DownVideo(path,imagePath,id);
    }


    public void requestUserInfo(){
        PreviewModel.requestUserInfo();
    }


    public void CompressImg(List<String> paths,String templateId,int isAnime){
        PreviewModel.CompressImgAndCache(paths,templateId,isAnime);
    }

    public void requestTemplateDetail(String templateId){
        PreviewModel.requestTemplateDetail(templateId);
    }

    public void downZip(String url,String zipPid){
        PreviewModel.prepareDownZip(url,zipPid);
    }

    public void collectTemplate(String templateId,String title,String template_type){
        PreviewModel.collectTemplate(templateId, title,template_type);
    }


    public void onDestroy(){
        PreviewModel.onDestroy();
    }

    @Override
    public void getCompressImgList(List<String> imgList) {
        PreviewView.getCompressImgList(imgList);
    }

    @Override
    public void showDownProgress(int progress) {
        PreviewView.showDownProgress(progress);
    }

    @Override
    public void getTemplateFileSuccess(String filePath) {
        PreviewView.getTemplateFileSuccess(filePath);
    }

    @Override
    public void collectionResult() {
        PreviewView.collectionResult();
    }

    @Override
    public void getTemplateLInfo(new_fag_template_item item) {
        PreviewView.getTemplateLInfo(item);

    }

    @Override
    public void hasLogin(boolean hasLogin) {
        PreviewView.hasLogin(hasLogin);
    }

    @Override
    public void downVideoSuccess(String path,String imagePath) {
        PreviewView.downVideoSuccess(path,imagePath);
    }
}
