package com.mobile.CloudMovie.ui.view.fragment;

import android.support.v4.view.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;
import com.mobile.CloudMovie.R;
import com.mobile.CloudMovie.base.BaseFragment;

import butterknife.BindView;


/**
 * description ：发现
 * date: ：2019/5/8 15:08
 * author: ztj
 */

public class frag2 extends BaseFragment {

    @BindView(R.id.tl_tabs)
    SlidingTabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewpager;


    String[] titles = {"明星", "长片", "短片"};

    @Override
    protected int getContentLayout() {
        return R.layout.frg_2;
    }


    @Override
    protected void initView() {

    }


    @Override
    protected void initAction() {

    }

    @Override
    protected void initData() {
        initTabData();
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    private void initTabData() {
    }


}


