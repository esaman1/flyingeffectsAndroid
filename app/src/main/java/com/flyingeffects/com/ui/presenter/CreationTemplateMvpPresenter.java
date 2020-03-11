package com.flyingeffects.com.ui.presenter;

import android.content.Context;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.ui.interfaces.model.CreationTemplateMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.CreationTemplateMvpView;
import com.flyingeffects.com.ui.model.CreationTemplateMvpModel;

public class CreationTemplateMvpPresenter extends BasePresenter implements CreationTemplateMvpCallback {
    private CreationTemplateMvpView CreationTemplatemvpView;
    private CreationTemplateMvpModel CreationTemplatemodel;

    public CreationTemplateMvpPresenter(Context context, CreationTemplateMvpView mvp_view) {
        this.CreationTemplatemvpView = mvp_view;
        CreationTemplatemodel = new CreationTemplateMvpModel(context, this);
    }



}
