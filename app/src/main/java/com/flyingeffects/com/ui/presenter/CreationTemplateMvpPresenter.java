package com.flyingeffects.com.ui.presenter;

import android.content.Context;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.ui.interfaces.model.CreationTemplateMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.CreationTemplateMvpView;
import com.flyingeffects.com.ui.model.CreationTemplateMvpModel;

public class CreationTemplateMvpPresenter extends BasePresenter implements CreationTemplateMvpCallback {
    private CreationTemplateMvpView creationTemplatemvpView;
    private CreationTemplateMvpModel creationTemplatemodel;

    public CreationTemplateMvpPresenter(Context context, CreationTemplateMvpView mvp_view) {
        this.creationTemplatemvpView = mvp_view;
        creationTemplatemodel = new CreationTemplateMvpModel(context, this);
    }


    public void initBottomLayout(){
        creationTemplatemodel.initBottomLayout();
    }


}
