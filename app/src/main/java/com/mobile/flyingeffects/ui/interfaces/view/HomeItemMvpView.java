package com.mobile.flyingeffects.ui.interfaces.view;

import android.widget.ImageView;

import com.mobile.flyingeffects.enity.new_fag_template_item;

import java.util.ArrayList;

public interface HomeItemMvpView {

  void isOnRefresh();
  void isOnLoadMore();
  void isShowData(ArrayList<new_fag_template_item>list);

}
