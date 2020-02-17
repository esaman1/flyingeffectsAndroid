package com.mobile.CloudMovie.ui.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.flyco.tablayout.SlidingTabLayout;
import com.mobile.CloudMovie.R;
import com.mobile.CloudMovie.adapter.home_vp_frg_adapter;
import com.mobile.CloudMovie.base.BaseFragment;
import com.mobile.CloudMovie.enity.TemplateType;
import com.mobile.CloudMovie.ui.interfaces.view.home_fagMvpView;
import com.mobile.CloudMovie.ui.presenter.home_fagMvpPresenter;
import com.mobile.CloudMovie.ui.view.activity.searchActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * user :TongJu  ;描述：首页
 * 时间：2018/4/24
 **/

public class frag0 extends BaseFragment implements home_fagMvpView {


    @BindView(R.id.tl_tabs)
    SlidingTabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewpager;

    @BindView(R.id.ll_into_search)
    LinearLayout ll_into_search;


    @BindView(R.id.column_more)
    ImageView column_more;

    @Override
    protected int getContentLayout() {
        return R.layout.fag_0;
    }


    @Override
    protected void initView() {
        home_fagMvpPresenter Presenter = new home_fagMvpPresenter(getActivity(), this);
        Presenter.getFragmentList();
        ll_into_search.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), searchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void setFragmentList(List<TemplateType> data) {
        if (data != null && data.size() > 0) {
            ArrayList<Fragment> list = new ArrayList<>();
            FragmentManager manager = getFragmentManager();
            String[] titles = new String[data.size()];
            for (int i = 0; i < data.size(); i++) {
                Bundle bundle = new Bundle();
//                bundle.putSerializable("id", data.get(i).getId());
                bundle.putSerializable("num", i);
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
//                        statisticsEventAffair.getInstance().setFlag(getActivity(), "1_tab", titles[i]);
//                        EventBus.getDefault().post(new viewPagerSelected(i));  //消息通知
                    }
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });
            tabLayout.setViewPager(viewpager, titles);
        }
    }

    @OnClick({R.id.tv_filtrate,R.id.column_more})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_filtrate:

                break;

            case R.id.column_more:

                break;


            default:
                break;

        }


    }
}


