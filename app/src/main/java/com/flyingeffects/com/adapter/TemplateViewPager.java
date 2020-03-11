package com.flyingeffects.com.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class TemplateViewPager extends PagerAdapter {


    private  List<View> data;

    public TemplateViewPager(List<View> data) {
        super();
        this.data = data;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(data.get(position));
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        container.addView(data.get(position));
        return data.get(position);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    public void removeItem(int position){
        data.remove(position);
        notifyDataSetChanged();

    }
}
