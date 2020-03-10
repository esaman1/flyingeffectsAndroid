package com.flyingeffects.com.ui.presenter;

import android.content.Context;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.ui.interfaces.model.FagBjMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.FagBjMvpView;
import com.flyingeffects.com.ui.model.FagBjMvpModel;

public class FagBjMvpPresenter extends BasePresenter implements FagBjMvpCallback {
    private FagBjMvpView FagBjmvpView;
    private FagBjMvpModel FagBjmodel;

    public FagBjMvpPresenter(Context context, FagBjMvpView mvp_view) {
        this.FagBjmvpView = mvp_view;
        FagBjmodel = new FagBjMvpModel(context, this);
    }



}
