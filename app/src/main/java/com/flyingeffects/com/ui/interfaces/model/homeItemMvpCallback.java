package com.flyingeffects.com.ui.interfaces.model;



import com.flyingeffects.com.entity.NewFragmentTemplateItem;

import java.util.ArrayList;

public interface homeItemMvpCallback {

    void isOnRefresh();
    void isOnLoadMore();
    void showData(  ArrayList<NewFragmentTemplateItem>list);
    void showNoData(boolean isShowData);
//    void GetAdCallback(FeedAdConfigBean.FeedAdResultBean feedAdResultBean);
    void needRequestFeedAd();
}
