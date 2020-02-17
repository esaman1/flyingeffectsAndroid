package com.mobile.flyingeffects.ui.interfaces.view;

import android.widget.ImageView;

import java.util.ArrayList;

public interface HomeItemMvpView {

  void isOnRefresh();
  void isOnLoadMore();
  void  setViewPagerAdapter(ArrayList<ImageView> list);
  void onclickBinnerIndex(int position);
  void setViewPageShowItem(int pageNumber);

}
