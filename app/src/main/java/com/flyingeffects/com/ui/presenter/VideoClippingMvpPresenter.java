package com.flyingeffects.com.ui.presenter;

import android.content.Context;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.ui.interfaces.model.VideoClippingMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.VideoClippingMvpView;
import com.flyingeffects.com.ui.model.VideoClippingMvpModel;

public class VideoClippingMvpPresenter extends BasePresenter implements VideoClippingMvpCallback {
    private VideoClippingMvpView VideoClippingmvpView;
    private VideoClippingMvpModel VideoClippingmodel;

    public VideoClippingMvpPresenter(Context context, VideoClippingMvpView mvp_view) {
        this.VideoClippingmvpView = mvp_view;
        VideoClippingmodel = new VideoClippingMvpModel(context, this);
    }



}
