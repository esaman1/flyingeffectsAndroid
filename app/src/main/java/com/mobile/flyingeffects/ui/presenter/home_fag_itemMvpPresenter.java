package com.mobile.flyingeffects.ui.presenter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mobile.flyingeffects.base.mvpBase.BasePresenter;
import com.mobile.flyingeffects.enity.new_fag_template_item;
import com.mobile.flyingeffects.ui.interfaces.model.homeItemMvpCallback;
import com.mobile.flyingeffects.ui.interfaces.view.HomeItemMvpView;
import com.mobile.flyingeffects.ui.model.home_fag_itemMvpModel;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;

public class home_fag_itemMvpPresenter extends BasePresenter implements homeItemMvpCallback {
    private HomeItemMvpView mvp_view;
    private home_fag_itemMvpModel homeItemModel;

    public home_fag_itemMvpPresenter(Context context, HomeItemMvpView mvp_view) {
        this.mvp_view = mvp_view;
        homeItemModel = new home_fag_itemMvpModel(context, this);
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
    }

}
