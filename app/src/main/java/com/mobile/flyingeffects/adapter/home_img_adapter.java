package com.mobile.flyingeffects.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.flyingeffects.ui.interfaces.view.HomeItemMvpView;

import java.util.List;

/**
 * Created by 张sir
 * on 2017/8/22.
 */

public class home_img_adapter extends PagerAdapter {
    private final List<ImageView> data;
    private HomeItemMvpView mvpView;

    public home_img_adapter(List<ImageView> data, HomeItemMvpView mvpview) {
        super();
        this.data = data;
        this.mvpView=mvpview;
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
        ImageView imageView = data.get(position);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mvpView.onclickBinnerIndex(position);
            }
        });

        return data.get(position);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }
}
