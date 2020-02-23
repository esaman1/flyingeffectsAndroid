package com.mobile.flyingeffects.ui.presenter;

import android.content.Context;

import com.mobile.flyingeffects.base.mvpBase.BasePresenter;
import com.mobile.flyingeffects.enity.TemplateType;
import com.mobile.flyingeffects.ui.interfaces.model.VideoClippingMvpCallback;
import com.mobile.flyingeffects.ui.interfaces.view.VideoClippingMvpView;
import com.mobile.flyingeffects.ui.model.VideoClippingMvpModel;

import java.util.List;

public class VideoClippingMvpPresenter extends BasePresenter implements VideoClippingMvpCallback {
    private VideoClippingMvpView VideoClippingmvpView;
    private VideoClippingMvpModel VideoClippingmodel;

    public VideoClippingMvpPresenter(Context context, VideoClippingMvpView mvp_view) {
        this.VideoClippingmvpView = mvp_view;
        VideoClippingmodel = new VideoClippingMvpModel(context, this);
    }



}
