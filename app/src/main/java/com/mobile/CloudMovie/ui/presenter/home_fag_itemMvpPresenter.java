package com.mobile.CloudMovie.ui.presenter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mobile.CloudMovie.base.mvpBase.BasePresenter;
import com.mobile.CloudMovie.ui.interfaces.model.homeItemMvpCallback;
import com.mobile.CloudMovie.ui.interfaces.view.HomeItemMvpView;
import com.mobile.CloudMovie.ui.model.home_fag_itemMvpModel;

import java.util.ArrayList;

public class home_fag_itemMvpPresenter extends BasePresenter implements homeItemMvpCallback {
    private HomeItemMvpView mvp_view;
    private home_fag_itemMvpModel homeItemModel;

    public home_fag_itemMvpPresenter(Context context, HomeItemMvpView mvp_view) {
        this.mvp_view = mvp_view;
        homeItemModel = new home_fag_itemMvpModel(context, this);
    }

    public void initPoint(LinearLayout initPoint, int count) {

        homeItemModel.initPoint(initPoint,count);
    }

    public void startCarousel(int interval, int allPageCount) {
        homeItemModel.startCarousel(interval, allPageCount);
    }


    public void setonPageScrollStateChanged(int state){
        homeItemModel.setonPageScrollStateChanged(state);
    }


    public void CoosePoint(int ChoosePosition) {
        homeItemModel.ChoosePoint(ChoosePosition);
    }

    public void requestData(){
        homeItemModel.requestData();
    }


    @Override
    public void setViewPageShowItem(int pageNumber) {
        mvp_view.setViewPageShowItem(pageNumber);
    }

    @Override
    public void isOnRefresh() {

    }

    @Override
    public void isOnLoadMore() {

    }

    @Override
    public void setViewPagerAdapter(ArrayList<ImageView> list) {
        mvp_view.setViewPagerAdapter(list);
    }
}
