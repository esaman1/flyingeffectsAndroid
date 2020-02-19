package com.mobile.flyingeffects.ui.interfaces.model;



import com.mobile.flyingeffects.enity.new_fag_template_item;

import java.util.ArrayList;

public interface homeItemMvpCallback {

    void isOnRefresh();
    void isOnLoadMore();
    void showData(  ArrayList<new_fag_template_item>list);
    void showNoData(boolean isShowData);
}
