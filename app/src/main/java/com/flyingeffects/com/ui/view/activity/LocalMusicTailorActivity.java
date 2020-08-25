package com.flyingeffects.com.ui.view.activity;

import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.view.histogram.MyBarChartView;

import java.util.ArrayList;
import   com.flyingeffects.com.view.histogram.MyBarChartView.BarData;

import butterknife.BindView;


/**
 * description ：本地音乐裁剪
 * creation date: 2020/8/25
 * user : zhangtongju
 */
public class LocalMusicTailorActivity extends BaseActivity {


    @BindView(R.id.animation_view_2)
    LottieAnimationView animation_view;

    @Override
    protected int getLayoutId() {
        return R.layout.act_local_music_tailor;
    }

    @Override
    protected void initView() {
        ((TextView)findViewById(R.id.tv_top_title)).setText("裁剪音乐");
        findViewById(R.id.iv_top_back).setOnClickListener(this);
    }

    @Override
    protected void initAction() {
        animStart();
        test();
    }




    private void test(){
        ArrayList<BarData> innerData = new ArrayList<>();
        for(int i=0;i<100;i++){
            innerData.add(new BarData(14, "1月"));
            innerData.add(new BarData(14, "2月"));
            innerData.add(new BarData(43, "3月"));
            innerData.add(new BarData(35, "4月"));
            innerData.add(new BarData(56, "5月"));
            innerData.add(new BarData(12, "6月"));
            innerData.add(new BarData(102, "9月"));
            innerData.add(new BarData(142, "7月"));
            innerData.add(new BarData(121, "8月"));
            innerData.add(new BarData(238, "10月"));
            innerData.add(new BarData(18, "11月"));
            innerData.add(new BarData(348, "12月"));
            innerData.add(new BarData(82, "13月"));
            innerData.add(new BarData(238, "14月"));
            innerData.add(new BarData(18, "15月"));
            innerData.add(new BarData(348, "16月"));
            innerData.add(new BarData(82, "17月"));
        }


        MyBarChartView mybarCharView =findViewById(R.id.mybarCharView);
        mybarCharView.setBarChartData(innerData);
    }



    private void animStart(){
        animation_view.setProgress(0f);
        animation_view.playAnimation();
    }


    @Override
    public void onStop() {
        super.onStop();
        animation_view.cancelAnimation();
    }



}
