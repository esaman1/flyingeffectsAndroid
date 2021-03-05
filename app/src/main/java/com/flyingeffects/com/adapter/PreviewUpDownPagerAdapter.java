package com.flyingeffects.com.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 预览模板页面适配器
 *
 * @author vidya
 */
public class PreviewUpDownPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList;

    public PreviewUpDownPagerAdapter(@NonNull FragmentManager fm, int behavior, List<Fragment> list) {
        super(fm, behavior);
        mFragmentList = list;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList == null ? 0 : mFragmentList.size();
    }

}
