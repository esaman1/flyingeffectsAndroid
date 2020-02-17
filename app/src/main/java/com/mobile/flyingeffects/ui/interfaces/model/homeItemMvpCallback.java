package com.mobile.flyingeffects.ui.interfaces.model;


import android.widget.ImageView;

import java.util.ArrayList;

public interface homeItemMvpCallback {

    void setViewPageShowItem(int pageNumber);
    void isOnRefresh();
    void isOnLoadMore();
    void  setViewPagerAdapter(ArrayList<ImageView> list);
}
