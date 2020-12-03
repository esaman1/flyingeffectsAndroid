package com.flyingeffects.com.ui.presenter;

import android.content.Context;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.enity.FirstLevelTypeEntity;
import com.flyingeffects.com.ui.interfaces.model.DressUpMvpCallback;
import com.flyingeffects.com.ui.interfaces.model.home_fagMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.DressUpMvpView;
import com.flyingeffects.com.ui.interfaces.view.home_fagMvpView;
import com.flyingeffects.com.ui.model.DressUpMvpModel;
import com.flyingeffects.com.ui.model.home_fagMvpModel;

import java.util.List;

public class DressUpMvpPresenter extends BasePresenter implements DressUpMvpCallback {
    private DressUpMvpView home_mvpView;
    private DressUpMvpModel home_model;

    public DressUpMvpPresenter(Context context, DressUpMvpView mvp_view) {
        this.home_mvpView = mvp_view;
        home_model = new DressUpMvpModel(context, this);
    }

    public void getFragmentList(){
        home_model.getFragmentList();
    }

    @Override
    public void setFragmentList(List<FirstLevelTypeEntity> data) {
        home_mvpView.setFragmentList(data);
    }
}
