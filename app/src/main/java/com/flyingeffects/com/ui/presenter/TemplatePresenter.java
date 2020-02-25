package com.flyingeffects.com.ui.presenter;

import android.content.Context;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.ui.interfaces.model.TemplateMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.TemplateMvpView;
import com.flyingeffects.com.ui.model.TemplateMvpModel;
import com.shixing.sxve.ui.AssetDelegate;
import com.shixing.sxve.ui.model.TemplateModel;

public class TemplatePresenter extends BasePresenter implements TemplateMvpCallback {
    private TemplateMvpView home_mvpView;
    private TemplateMvpModel home_model;

    public TemplatePresenter(Context context, TemplateMvpView mvp_view) {
        this.home_mvpView = mvp_view;
        home_model = new TemplateMvpModel(context, this);
    }


    public void renderVideo(String mTemplateFolder,String mAudio1Path,boolean isPreview){
        home_model.renderVideo(mTemplateFolder,mAudio1Path,isPreview);
    }



    public void loadTemplate(String filePath, AssetDelegate delegate){
        home_model.loadTemplate(filePath,delegate);
    }


    @Override
    public void completeTemplate(TemplateModel templateModel) {
        home_mvpView.completeTemplate(templateModel);
    }

    @Override
    public void toPreview(String path) {
        home_mvpView.toPreview(path);
    }
}
