package com.flyingeffects.com.ui.interfaces.view;

import com.flyingeffects.com.enity.NewFragmentTemplateItem;

import java.util.ArrayList;

public interface BjItemMvpView {

    void isOnRefresh();

    void isOnLoadMore();

    void isShowData(ArrayList<NewFragmentTemplateItem> list);

    void showNoData(boolean isShowData);
}
