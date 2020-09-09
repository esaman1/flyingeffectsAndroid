package com.flyingeffects.com.ui.view.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.CutSuccess;
import com.flyingeffects.com.enity.ReplayMessageEvent;
import com.flyingeffects.com.ui.view.fragment.frag_choose_music_local_music;
import com.flyingeffects.com.ui.view.fragment.frag_choose_music_recent_updates;

import java.util.ArrayList;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;


/**
 * description ：选择音乐界面
 * creation date: 2020/8/26
 * user : zhangtongju
 */
public class ChooseMusicActivity extends BaseActivity {


    @BindView(R.id.viewpager)
    ViewPager viewpager;

    @BindView(R.id.tl_tabs)
    SlidingTabLayout tabLayout;

    FragmentManager manager;

    @BindView(R.id.relative_top)
    RelativeLayout relative_top;

    private long needDuration;

    @Override
    protected int getLayoutId() {
        return R.layout.act_choose_music;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        needDuration=getIntent().getLongExtra("needDuration",10000);
        ArrayList<Fragment> list = new ArrayList<>();
        String[] titles = {"最近更新","本地音频","视频提取","收藏音乐"};

        frag_choose_music_recent_updates fragment = new frag_choose_music_recent_updates();
        Bundle bundle = new Bundle();
        bundle.putSerializable("id",0);
        bundle.putSerializable("needDuration",needDuration);
        fragment.setArguments(bundle);
        list.add(fragment);

        frag_choose_music_recent_updates fragment_1 = new frag_choose_music_recent_updates();
        Bundle bundle_1 = new Bundle();
        bundle_1.putSerializable("id",1);
        bundle_1.putSerializable("needDuration",needDuration);
        fragment_1.setArguments(bundle_1);
        list.add(fragment_1);

        frag_choose_music_local_music fragment_local_music = new frag_choose_music_local_music();
        Bundle bundle_local_music = new Bundle();
        bundle_local_music.putSerializable("needDuration",needDuration);
        fragment_local_music.setArguments(bundle_local_music);
        list.add(fragment_local_music);


        frag_choose_music_recent_updates fragment_2 = new frag_choose_music_recent_updates();
        Bundle bundle_2 = new Bundle();
        bundle_2.putSerializable("id",2);
        bundle_2.putSerializable("needDuration",needDuration);
        fragment_2.setArguments(bundle_2);
        list.add(fragment_2);


        relative_top.setOnClickListener(view -> {
            Intent intent=new Intent(ChooseMusicActivity.this,searchMusicActivity.class);
            intent.putExtra("needDuration",needDuration);
            startActivity(intent);
        });
        manager = getSupportFragmentManager();
        home_vp_frg_adapter adapter = new home_vp_frg_adapter(manager, list);
        viewpager.setAdapter(adapter);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        tabLayout.setViewPager(viewpager, titles);
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {

            }

            @Override
            public void onTabReselect(int position) {

            }
        });

        findViewById(R.id.iv_top_back).setOnClickListener(view -> finish());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initAction() {

    }




    @Subscribe
    public void onEventMainThread( CutSuccess cutSuccess) {
            this.finish();
    }



}
