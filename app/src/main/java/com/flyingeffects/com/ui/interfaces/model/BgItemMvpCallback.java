package com.flyingeffects.com.ui.interfaces.model;


import com.flyingeffects.com.entity.NewFragmentTemplateItem;

import java.util.ArrayList;

public interface BgItemMvpCallback {
    void isOnRefresh();

    void isOnLoadMore();

    void showData(ArrayList<NewFragmentTemplateItem> list);

    void showNoData(boolean isShowData);
}
