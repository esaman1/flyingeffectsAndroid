package com.flyingeffects.com.ui.interfaces.view;

import com.flyingeffects.com.entity.NewFragmentTemplateItem;

import java.util.ArrayList;

public interface HomeItemMvpView {

    void isOnRefresh();

    void isOnLoadMore();

    void isShowData(ArrayList<NewFragmentTemplateItem> list);

    void showNoData(boolean isShowData);

//     void GetAdCallback(FeedAdConfigBean.FeedAdResultBean feedAdResultBean) ;

     void needRequestFeedAd();
}
