package com.flyingeffects.com.ui.presenter;

import android.content.Context;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.ui.interfaces.model.BgItemMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.BjItemMvpView;
import com.flyingeffects.com.ui.model.bgItemMvpModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;

public class bg_itemMvpPresenter extends BasePresenter implements BgItemMvpCallback {
    private BjItemMvpView mvp_view;
    private bgItemMvpModel homeItemModel;

    public bg_itemMvpPresenter(Context context, BjItemMvpView mvp_view, int fromType) {
        this.mvp_view = mvp_view;
        homeItemModel = new bgItemMvpModel(context, this,fromType);
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
