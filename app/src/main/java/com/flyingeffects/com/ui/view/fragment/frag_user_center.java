package com.flyingeffects.com.ui.view.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.ui.view.activity.AboutActivity;
import com.flyingeffects.com.ui.view.activity.LoginActivity;
import com.shixing.sxve.ui.view.WaitingDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * user :TongJu  ;描述：用戶中心
 * 时间：2018/4/24
 **/

public class frag_user_center extends BaseFragment {

    TextView tv_play_video;
    Dialog mDialog;

  private  String[] titles = {"我的收藏"};

    @BindView(R.id.viewpager)
    ViewPager viewpager;


    @BindView(R.id.tl_tabs)
    SlidingTabLayout tabLayout;

    @BindView(R.id.iv_about)
    ImageView iv_about;

    @BindView(R.id.iv_head)
    ImageView iv_head;

    @BindView(R.id.tv_id)
    TextView tv_id;


    @Override
    protected int getContentLayout() {
        return R.layout.fag_user_center;
    }


    @Override
    protected void initView() {
        iv_about.setOnClickListener(view -> {
            Intent intent=new Intent(getActivity(), AboutActivity.class);
            startActivity(intent);
        });
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
        if (getActivity() != null) {
            //未登陆
            if (BaseConstans.hasLogin()) {
                tv_id.setText("我的id号："+BaseConstans.GetUserToken());
            }else{
                tv_id.setText("未登录");
            }
        }

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
//        frag_user_collect fag_1 = new frag_user_collect();
        list.add(fag_0);
//        list.add(fag_1);
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


        @OnClick({R.id.iv_head})
        public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_head:
                if(!BaseConstans.hasLogin()){
                    Intent intent =new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                break;
        }

            }


}


