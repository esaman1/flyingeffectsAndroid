package com.flyingeffects.com.adapter;

import com.flyingeffects.com.base.BaseFragment;

import java.util.List;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * @author ZhouGang
 * @date 2020/12/3
 */
public class TwoLevelFragmentPagerAdapter extends FragmentPagerAdapter {

    List<BaseFragment> list;

    public TwoLevelFragmentPagerAdapter(FragmentManager fragmentManager, List<BaseFragment> list) {
        super(fragmentManager,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list != null  ? list.size() : 0;
    }


    @Override
    public BaseFragment getItem(int position) {
        return list.get(position);
    }
} 
