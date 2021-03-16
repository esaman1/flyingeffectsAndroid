package com.flyingeffects.com.ui.interfaces.view;

import com.flyingeffects.com.enity.new_fag_template_item;
import com.nineton.ntadsdk.bean.FeedAdConfigBean;

import java.util.ArrayList;

public interface HomeItemMvpView {

    void isOnRefresh();

    void isOnLoadMore();

    void isShowData(ArrayList<new_fag_template_item> list);

    void showNoData(boolean isShowData);

//     void GetAdCallback(FeedAdConfigBean.FeedAdResultBean feedAdResultBean) ;

     void needRequestFeedAd();
}
