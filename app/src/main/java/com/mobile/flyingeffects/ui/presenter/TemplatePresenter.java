package com.mobile.flyingeffects.ui.presenter;

import android.content.Context;

import com.mobile.flyingeffects.base.mvpBase.BasePresenter;
import com.mobile.flyingeffects.enity.TemplateType;
import com.mobile.flyingeffects.ui.interfaces.model.TemplateMvpCallback;
import com.mobile.flyingeffects.ui.interfaces.view.TemplateMvpView;
import com.mobile.flyingeffects.ui.interfaces.view.TemplateMvpView;
import com.mobile.flyingeffects.ui.model.TemplateMvpModel;

import java.util.List;

public class TemplatePresenter extends BasePresenter implements TemplateMvpCallback {
    private TemplateMvpView home_mvpView;
    private TemplateMvpModel home_model;

    public TemplatePresenter(Context context, TemplateMvpView mvp_view) {
        this.home_mvpView = mvp_view;
        home_model = new TemplateMvpModel(context, this);
    }




}
