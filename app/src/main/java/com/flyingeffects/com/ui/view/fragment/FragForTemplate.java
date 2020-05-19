package com.flyingeffects.com.ui.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.enity.TemplateType;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.view.home_fagMvpView;
import com.flyingeffects.com.ui.presenter.home_fagMvpPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * user :TongJu  ;描述：模板页面
 * 时间：2018/4/24
 **/

public class FragForTemplate extends BaseFragment implements home_fagMvpView {

    home_fagMvpPresenter Presenter;
    @BindView(R.id.tl_tabs)
    SlidingTabLayout tabLayout;

    @BindView(R.id.viewpager_bj)
    ViewPager viewpager;

    private List<TemplateType> data;


    @Override
    protected int getContentLayout() {
        return R.layout.fag_0;
    }


    @Override
    protected void initView() {
        Presenter = new home_fagMvpPresenter(getActivity(), this);
    }

    @Override
    protected void initAction() {
        findViewById(R.id.iv_back).setVisibility(View.GONE);
    }

    @Override
    protected void initData() {

    }


    @Override
    public void onResume() {
        super.onResume();
        if(data==null||data.size()==0){
            Presenter.getFragmentList();
        }
    }

    @Override
    public void setFragmentList(List<TemplateType> data) {
        if (getActivity() != null) {
            if (data != null && data.size() > 0) {
                this.data=data;
                ArrayList<Fragment> list = new ArrayList<>();
                FragmentManager manager = getFragmentManager();
                String[] titles = new String[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("id", data.get(i).getId());
                    bundle.putSerializable("num", i);
                    bundle.putSerializable("from", 0);
                    titles[i] = data.get(i).getName();
                    home_item_fag fragment = new home_item_fag();
                    fragment.setArguments(bundle);
                    list.add(fragment);
                }
                home_vp_frg_adapter adapter = new home_vp_frg_adapter(manager, list);
                viewpager.setAdapter(adapter);
                viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i1) {

                    }

                    @Override
                    public void onPageSelected(int i) {
                        if (i <= data.size() - 1) {
                            statisticsEventAffair.getInstance().setFlag(getActivity(), "1_tab", titles[i]);
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {

                    }
                });
                tabLayout.setViewPager(viewpager, titles);
            }
        }

    }



}


