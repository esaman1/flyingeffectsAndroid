package com.flyingeffects.com.ui.presenter;

import android.content.Context;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.ui.interfaces.model.homeItemMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.HomeItemMvpView;
import com.flyingeffects.com.ui.model.home_fag_itemMvpModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class home_fag_itemMvpPresenter extends BasePresenter implements homeItemMvpCallback {
    private HomeItemMvpView mvp_view;
    private home_fag_itemMvpModel homeItemModel;

    public home_fag_itemMvpPresenter(Context context, HomeItemMvpView mvp_view, int fromType) {
        this.mvp_view = mvp_view;
        homeItemModel = new home_fag_itemMvpModel(context, this, fromType);
    }


    public int getselectPage() {
        return homeItemModel.getselectPage();
    }


    public void requestData(String templateId, int num) {
        homeItemModel.requestData(templateId, num);
    }

    public void initSmartRefreshLayout(SmartRefreshLayout smartRefreshLayout) {
        homeItemModel.initSmartRefreshLayout(smartRefreshLayout);
    }




    @Override
    public void isOnRefresh() {

    }

    @Override
    public void isOnLoadMore() {

    }

    @Override
    public void showData(ArrayList<new_fag_template_item> list) {
        mvp_view.isShowData(list);
    }

    @Override
    public void showNoData(boolean isShowData) {
        mvp_view.showNoData(isShowData);
    }

}
