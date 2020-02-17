package com.mobile.CloudMovie.ui.model;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mobile.CloudMovie.R;
import com.mobile.CloudMovie.ui.interfaces.model.homeItemMvpCallback;
import com.mobile.CloudMovie.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class home_fag_itemMvpModel {
    private homeItemMvpCallback callback;
    private Context context;
    private int bannerCount = 3; //banner的个数
    private ImageView[] img_dian;
    private Timer timer;
    private boolean isExecuteViewPager = true; //是否可以执行viewpager


    public home_fag_itemMvpModel(Context context, homeItemMvpCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    /**
     * user :TongJu  ;描述：设置选中
     * 时间：2018/5/7
     **/
    public void ChoosePoint(int ChoosePosition) {
        for (int i = 0; i < bannerCount; i++) {
            img_dian[i].setImageResource(R.mipmap.point_write_lucency);
        }
        img_dian[ChoosePosition].setImageResource(R.mipmap.point_write_lucency);
    }


    public void requestData() {

        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {

            list.add("");
        }
        setViewpager(list);
    }


    private void setViewpager(List<String> data) {
        ArrayList<ImageView> list = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            ImageView iv = new ImageView(context);
            iv.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
//            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            Glide.with(context).load(data.get(i).getSlide_pic().getOrigin()).apply(new RequestOptions().placeholder(R.mipmap.loading_bj)).into(iv);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            list.add(iv);
        }
        callback.setViewPagerAdapter(list);
    }


    public void initPoint(LinearLayout ll_addpoint, int count) {
        bannerCount = count;
        //清空之前的点
        ll_addpoint.removeAllViews();
        //获得新数据后清空之前的image
        img_dian = new ImageView[count];
        for (int i = 0; i < count; i++) {
            img_dian[i] = (ImageView) LayoutInflater.from(context).inflate(R.layout.imageview, ll_addpoint, false);
            ll_addpoint.addView(img_dian[i]);
        }
    }


    /**
     * user :TongJu  ;描述：开启轮播
     * 时间：2018/5/11
     **/
    private TimerTask task;
    private int ShowPageCount;
    private int interval;
    private int allPageCount;

    public void startCarousel(int interval, final int allPageCount) {
        LogUtil.d("startCarousel", "startCarousel");
        this.interval = interval;
        this.allPageCount = allPageCount;
        if (isExecuteViewPager) {
            //启动新的timer之前都要确认是否关闭之前的
            closetimer();
            timer = new Timer();
            task = new TimerTask() {
                public void run() {
                    ShowPageCount++;
                    if (ShowPageCount == allPageCount) {
                        ShowPageCount = 0;
                    }
                    LogUtil.d("startCarousel", "=" + ShowPageCount);
                    callback.setViewPageShowItem(ShowPageCount);
                }
            };
            timer.schedule(task, 2000, interval);
        }
    }

    public void setonPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            isExecuteViewPager = true;
            if (timer == null && task == null) {
                startCarousel(interval, allPageCount);
            }
        } else if (state == ViewPager.SCROLL_STATE_DRAGGING) {
            //手动滑动的时候
            closetimer();
            isExecuteViewPager = false;
        }
    }


    private void closetimer() {
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

}

