package com.flyingeffects.com.ui.view.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
    ViewPager viewPager;


    @BindView(R.id.ll_add_child)
    LinearLayout ll_add_child;

    private FagBjMvpPresenter presenter;

    private ArrayList<TextView> listTv = new ArrayList<>();
    private ArrayList<View> listView = new ArrayList<>();

    @Override
    protected int getContentLayout() {
        return R.layout.fag_bj;
    }


    @Override
    protected void initView() {
        presenter = new FagBjMvpPresenter(getActivity(), this);
        presenter.requestData();
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
    public void setFragmentList(List<TemplateType> data) {
        ll_add_child.removeAllViews();
        listView.clear();
        listTv.clear();
        FragmentManager manager = getFragmentManager();
        String[] titles = new String[data.size()];
        ArrayList<Fragment> list = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_bj_head, null);
            TextView tv = view.findViewById(R.id.tv_name_bj_head);
            View view_line = view.findViewById(R.id.view_line_head);
            tv.setText(data.get(i).getName());
            tv.setId(i);
            tv.setOnClickListener(v -> showWitchBtn(v.getId()));
            listTv.add(tv);
            listView.add(view_line);
            ll_add_child.addView(view);
            titles[i] = data.get(i).getName();
            Bundle bundle = new Bundle();
            bundle.putSerializable("id", data.get(i).getId());
            bundle.putSerializable("from", 1);
            bundle.putSerializable("num", i);
            titles[i] = data.get(i).getName();
            home_item_fag fragment = new home_item_fag();
            fragment.setArguments(bundle);
            list.add(fragment);
        }
        home_vp_frg_adapter adapter = new home_vp_frg_adapter(manager, list);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

        if (data.size() > 0) {
            showWitchBtn(0);
        }
    }


    private void showWitchBtn(int showWitch) {
        for (int i = 0; i < listTv.size(); i++) {
            TextView tv = listTv.get(i);
            View view = listView.get(i);
            if (i == showWitch) {
                tv.setTextSize(21);
                int width = tv.getWidth();
                view.setVisibility(View.VISIBLE);
                setViewWidth(view,width);
            } else {
                tv.setTextSize(17);
                view.setVisibility(View.GONE);
            }
        }
        viewPager.setCurrentItem(showWitch);
    }


    private void setViewWidth(View mView,int width) {
        RelativeLayout.LayoutParams Params =(RelativeLayout.LayoutParams) mView.getLayoutParams();
        Params.width = width;
        mView.setLayoutParams(Params);
    }

}

