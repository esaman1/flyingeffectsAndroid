package com.flyingeffects.com.ui.presenter;

import android.content.Context;

import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.enity.NewFragmentTemplateItem;
import com.flyingeffects.com.ui.interfaces.model.homeItemMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.HomeItemMvpView;
import com.flyingeffects.com.ui.model.home_fag_itemMvpModel;
import com.nineton.ntadsdk.manager.FeedAdManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;

public class home_fag_itemMvpPresenter extends BasePresenter implements homeItemMvpCallback {
    private HomeItemMvpView mvp_view;
    private home_fag_itemMvpModel homeItemModel;

    public home_fag_itemMvpPresenter(Context context, HomeItemMvpView mvp_view, int fromType, FeedAdManager mAdManager) {
        this.mvp_view = mvp_view;
        homeItemModel = new home_fag_itemMvpModel(context, this, fromType,mAdManager);
    }


    public int getselectPage() {
        return homeItemModel.getselectPage();
    }


    public void requestData(String templateId, String tc_id, int num) {
        homeItemModel.requestData(templateId, tc_id, num);
    }

    public void initSmartRefreshLayout(SmartRefreshLayout smartRefreshLayout) {
        homeItemModel.initSmartRefreshLayout(smartRefreshLayout);
    }


//    public void requestAd() {
//        homeItemModel.requestAd();
//    }


    @Override
    public void isOnRefresh() {

    }

    @Override
    public void isOnLoadMore() {

    }

    @Override
    public void showData(ArrayList<NewFragmentTemplateItem> list) {
        mvp_view.isShowData(list);
    }

    @Override
    public void showNoData(boolean isShowData) {
        mvp_view.showNoData(isShowData);
    }

//    @Override
//    public void GetAdCallback(FeedAdConfigBean.FeedAdResultBean feedAdResultBean) {
//        mvp_view.GetAdCallback(feedAdResultBean);
//    }

    @Override
    public void needRequestFeedAd() {
        mvp_view.needRequestFeedAd();
    }

}
