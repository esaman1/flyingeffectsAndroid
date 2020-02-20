package com.mobile.flyingeffects.ui.view.fragment;

import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.mobile.flyingeffects.R;
import com.mobile.flyingeffects.adapter.home_vp_frg_adapter;
import com.mobile.flyingeffects.base.BaseFragment;

import java.util.ArrayList;

import butterknife.BindView;


/**
 * user :TongJu  ;描述：用戶中心
 * 时间：2018/4/24
 **/

public class frag_user_center extends BaseFragment {


    TextView tv_play_video;
    Dialog mDialog;

  private  String[] titles = {"我的收藏", "最近编辑",};

    @BindView(R.id.viewpager)
    ViewPager viewpager;



    @BindView(R.id.tl_tabs)
    SlidingTabLayout tabLayout;


    @Override
    protected int getContentLayout() {
        return R.layout.fag_user_center;
    }


    @Override
    protected void initView() {

    }

    @Override
    protected void initAction() {
        initTabData();
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





    private void initTabData() {
        FragmentManager manager = getChildFragmentManager();
        ArrayList<Fragment> list = new ArrayList<>();
        frag_user_collect fag_0 = new frag_user_collect();
        frag_user_collect fag_1 = new frag_user_collect();
        list.add(fag_0);
        list.add(fag_1);
        home_vp_frg_adapter adapter = new home_vp_frg_adapter(manager, list);
        viewpager.setAdapter(adapter);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
//                if (i <= data.size() - 1) {
//                        statisticsEventAffair.getInstance().setFlag(getActivity(), "1_tab", titles[i]);
//                        EventBus.getDefault().post(new viewPagerSelected(i));  //消息通知
//                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        tabLayout.setViewPager(viewpager, titles);
    }






}


