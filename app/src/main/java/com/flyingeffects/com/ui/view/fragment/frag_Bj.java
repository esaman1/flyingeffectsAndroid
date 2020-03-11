package com.flyingeffects.com.ui.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.enity.TemplateType;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.view.FagBjMvpView;
import com.flyingeffects.com.ui.presenter.FagBjMvpPresenter;

import java.util.ArrayList;
import java.util.List;

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

    private FagBjMvpPresenter presenter;


    @Override
    protected int getContentLayout() {
        return R.layout.fag_bj;
    }


    @Override
    protected void initView() {
        presenter = new FagBjMvpPresenter(getActivity(), this);
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


    @Override
    public void setFragmentList(List<TemplateType> data) {

        for (int i = 0; i < data.size(); i++) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_home_hot_video_section, null);
            TextView tv = view.findViewById(R.id.tv_text);
            tv.setText(videoModel.getTags().get(i));
            if (view.getParent() != null) {
                ViewGroup vp = (ViewGroup) view.getParent();
                vp.removeAllViews();
            }
            ll_add_Section.addView(view);
        }

        if (data != null && data.size() > 0) {
            ArrayList<Fragment> list = new ArrayList<>();
            FragmentManager manager = getFragmentManager();
            String[] titles = new String[data.size()];
            for (int i = 0; i < data.size(); i++) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("id", data.get(i).getId());
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
                        statisticsEventAffair.getInstance().setFlag(getActivity(), "1_tab", titles[i]);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });
        }
    }


}

