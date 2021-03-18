package com.flyingeffects.com.ui.view.activity;


import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.enity.CutSuccess;
import com.flyingeffects.com.enity.FragmentHasSlide;
import com.flyingeffects.com.ui.view.fragment.ExtractAudioChooseMusicFragment;
import com.flyingeffects.com.ui.view.fragment.RecentUpdateMusicFragment;
import com.flyingeffects.com.utils.LogUtil;

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
    public static String IS_FROM = "isFrom";

    public static int IS_FROM_SHOOT = 0;
    public static int IS_FROM_MB_SHOOT = 1;
    public static int IS_FROM_OTHERS = 2;

    @BindView(R.id.viewpager)
    ViewPager viewpager;

    @BindView(R.id.tl_tabs)
    SlidingTabLayout tabLayout;

    FragmentManager manager;

    @BindView(R.id.relative_top)
    RelativeLayout relative_top;

    /**
     * 如果时长为0，就表示无限裁剪，这里新添加一个功能，可以拖动裁剪
     */
    private long needDuration;


    private boolean isFromShoot = false;

    private int isFrom = IS_FROM_SHOOT;


    @Override
    protected int getLayoutId() {
        return R.layout.act_choose_music;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        needDuration = getIntent().getLongExtra("needDuration", 10000);
        isFromShoot = getIntent().getBooleanExtra("isFromShoot", false);
        isFrom = getIntent().getIntExtra(ChooseMusicActivity.IS_FROM, 0);
        LogUtil.d("OOM2", "当前需要的音乐时长为" + needDuration);
        ArrayList<Fragment> list = new ArrayList<>();
        String[] titles = {"最近更新", "本地音频", "视频提取", "收藏音乐"};

        RecentUpdateMusicFragment fragment = new RecentUpdateMusicFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("id", 0);
        bundle.putBoolean("isFromShoot", isFromShoot);
        bundle.putInt(ChooseMusicActivity.IS_FROM, isFrom);
        bundle.putSerializable("needDuration", needDuration);
        fragment.setArguments(bundle);
        list.add(fragment);

        RecentUpdateMusicFragment fragment1 = new RecentUpdateMusicFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("id", 1);
        bundle.putBoolean("isFromShoot", isFromShoot);
        bundle.putInt(ChooseMusicActivity.IS_FROM, isFrom);
        bundle1.putSerializable("needDuration", needDuration);
        fragment1.setArguments(bundle1);
        list.add(fragment1);

        ExtractAudioChooseMusicFragment fragmentLocalMusic = new ExtractAudioChooseMusicFragment();
        Bundle bundleLocalMusic = new Bundle();
        bundleLocalMusic.putSerializable("needDuration", needDuration);
        fragmentLocalMusic.setArguments(bundleLocalMusic);
        bundle.putInt(ChooseMusicActivity.IS_FROM, isFrom);
        bundle.putBoolean("isFromShoot", isFromShoot);
        list.add(fragmentLocalMusic);

        RecentUpdateMusicFragment fragment2 = new RecentUpdateMusicFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putSerializable("id", 2);
        bundle2.putSerializable("needDuration", needDuration);
        bundle.putInt(ChooseMusicActivity.IS_FROM, isFrom);
        bundle.putBoolean("isFromShoot", isFromShoot);
        fragment2.setArguments(bundle2);
        list.add(fragment2);

        relative_top.setOnClickListener(view -> {
            Intent intent = new Intent(ChooseMusicActivity.this, SearchMusicActivity.class);
            intent.putExtra("needDuration", needDuration);
            intent.putExtra(ChooseMusicActivity.IS_FROM, isFrom);
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
                EventBus.getDefault().post(new FragmentHasSlide());
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
        viewpager.setOffscreenPageLimit(4);
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
    public void onEventMainThread(CutSuccess cutSuccess) {
        this.finish();
    }

}
