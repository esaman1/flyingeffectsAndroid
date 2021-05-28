package com.flyingeffects.com.adapter;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by 张sir
 * on 2017/8/22.
 */

public class home_vp_frg_adapter extends FragmentPagerAdapter {
    private final List<Fragment> list;

    public home_vp_frg_adapter(FragmentManager fm, List<Fragment> list) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.list = list;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
