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


    public void initSmartRefreshLayout(SmartRefreshLayout smartRefreshLayout){
        previewUpAndDownMvpModel.initSmartRefreshLayout(smartRefreshLayout);
    }



}