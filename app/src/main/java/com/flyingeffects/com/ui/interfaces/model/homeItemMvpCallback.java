package com.flyingeffects.com.ui.interfaces.model;



import com.flyingeffects.com.enity.new_fag_template_item;
import com.nineton.ntadsdk.bean.FeedAdConfigBean;

import java.util.ArrayList;

public interface homeItemMvpCallback {

    void isOnRefresh();
    void isOnLoadMore();
    void showData(  ArrayList<new_fag_template_item>list);
    void showNoData(boolean isShowData);
    void GetAdCallback(FeedAdConfigBean.FeedAdResultBean feedAdResultBean);
}
