package com.mobile.flyingeffects.ui.presenter;

import android.content.Context;

import com.mobile.flyingeffects.base.mvpBase.BasePresenter;
import com.mobile.flyingeffects.enity.TemplateType;
import com.mobile.flyingeffects.ui.interfaces.model.home_fagMvpCallback;
import com.mobile.flyingeffects.ui.interfaces.view.home_fagMvpView;
import com.mobile.flyingeffects.ui.model.home_fagMvpModel;

import java.util.List;

public class home_fagMvpPresenter extends BasePresenter implements home_fagMvpCallback {
    private home_fagMvpView home_mvpView;
    private home_fagMvpModel home_model;

    public home_fagMvpPresenter(Context context, home_fagMvpView mvp_view) {
        this.home_mvpView = mvp_view;
        home_model = new home_fagMvpModel(context, this);
    }


    public void getFragmentList(){
        home_model.getFragmentList();
    }


    @Override
    public void setFragmentList(List<TemplateType> data) {
        home_mvpView.setFragmentList(data);
    }
}
