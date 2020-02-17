package com.mobile.CloudMovie.ui.view.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mobile.CloudMovie.R;
import com.mobile.CloudMovie.adapter.home_img_adapter;
import com.mobile.CloudMovie.base.BaseFragment;
import com.mobile.CloudMovie.enity.HomeItemEnity;
import com.mobile.CloudMovie.ui.interfaces.view.HomeItemMvpView;
import com.mobile.CloudMovie.ui.presenter.home_fag_itemMvpPresenter;
import com.mobile.CloudMovie.view.DecoratorViewPager;
import com.mobile.CloudMovie.view.ViewPagerScroller;

import java.util.ArrayList;

import butterknife.BindView;


public class home_item_fag extends BaseFragment implements HomeItemMvpView, View.OnClickListener {

    private BaseQuickAdapter hotvideo_recyclerlist_adapter;
    private DecoratorViewPager viewPager;
    private home_fag_itemMvpPresenter Presenter;
    private View RecyclerViewHeader;
    private LinearLayout ll_add_point;
//    private LinearLayoutManager linearLayoutManager;
    @BindView(R.id.RecyclerView)
     RecyclerView  recyclerView;

  private   int actTag;



    @Override
    protected int getContentLayout() {
        return R.layout.fag_0_item;
    }


    @Override
    protected void initView() {
        Presenter = new home_fag_itemMvpPresenter(getActivity(), this);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
//            templateId = bundle.getString("id");
            actTag = bundle.getInt("num");
        }
    }


    private void initRecycler() {
    }




    @Override
    protected void initAction() {
    }








    @Override
    protected void initData() {

    }


    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onStop() {
        super.onStop();
    }




    @Override
    public void isOnRefresh() {
    }

    @Override
    public void isOnLoadMore() {

    }

    @Override
    public void setViewPagerAdapter(ArrayList<ImageView> list) {
        if (getActivity() != null) {
            if(actTag==0){
                home_img_adapter adapter = new home_img_adapter(list, this);
                viewPager.setAdapter(adapter);
                ViewPagerScroller scroller = new ViewPagerScroller(getActivity());
                scroller.setScrollDuration(1000);
                scroller.initViewPagerScroll(viewPager);  //这个是设置切换过渡时间为2毫秒
                Presenter.initPoint(ll_add_point, list.size());
                Presenter.startCarousel(4000, list.size()); //有数据后就启动播放功能
                Presenter.CoosePoint(0);
            }
        }
    }

    private int pageNumber;
    @Override
    public void setViewPageShowItem(int pageNumber) {
        this.pageNumber = pageNumber;
        handler.sendEmptyMessage(100);
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            viewPager.setCurrentItem(pageNumber);
            super.handleMessage(msg);
        }
    };


    @Override
    public void onclickBinnerIndex(int position) {

    }


    @Override
    public void onClick(View view) {

    }
}


