package com.flyingeffects.com.ui.presenter;

import android.content.Context;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.enity.TemplateType;
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


    public void CompressImg(List<String> paths){
        PreviewModel.CompressImg(paths);
    }


    public void downZip(String url,long createTime){
        PreviewModel.downZip(url,createTime);
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
}
