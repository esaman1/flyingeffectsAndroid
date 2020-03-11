package com.flyingeffects.com.ui.presenter;

import android.content.Context;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.enity.TemplateType;
import com.flyingeffects.com.ui.interfaces.model.FagBjMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.FagBjMvpView;
import com.flyingeffects.com.ui.model.FagBjMvpModel;

import java.util.List;

public class FagBjMvpPresenter extends BasePresenter implements FagBjMvpCallback {
    private FagBjMvpView FagBjmvpView;
    private FagBjMvpModel FagBjModel;

    public FagBjMvpPresenter(Context context, FagBjMvpView mvp_view) {
        this.FagBjmvpView = mvp_view;
        FagBjModel = new FagBjMvpModel(context, this);
    }

    public void requestData(){
        FagBjModel.requestData();
    }


    @Override
    public void setFragmentList(List<TemplateType> data) {
        FagBjmvpView.setFragmentList(data);
    }
}
