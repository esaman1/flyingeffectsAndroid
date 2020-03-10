package com.flyingeffects.com.ui.view.fragment;

import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.ui.interfaces.view.FagBjMvpView;
import com.flyingeffects.com.ui.presenter.FagBjMvpPresenter;

import butterknife.BindView;


/**
 * user :TongJu  ;描述：背景页面
 * 时间：2018/4/24
 **/

public class frag_Bj extends BaseFragment implements FagBjMvpView {

    @BindView(R.id.viewpager)
    ViewPager viewpager;


    @BindView(R.id.ll_add_child)
    LinearLayout ll_add_child;

   private  FagBjMvpPresenter presenter;


    @Override
    protected int getContentLayout() {
        return R.layout.fag_bj;
    }


    @Override
    protected void initView() {
        presenter=new FagBjMvpPresenter(this,this)
    }


    @Override
    protected void initAction() {
        presenter.requestData();

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





}


