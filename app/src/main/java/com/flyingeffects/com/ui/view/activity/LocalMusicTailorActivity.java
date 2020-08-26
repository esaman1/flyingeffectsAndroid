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
        for(int i=0;i<1000;i++){
            innerData.add(new BarData(Test(), "1月"));
        }


        MyBarChartView mybarCharView =findViewById(R.id.mybarCharView);
        mybarCharView.setBarChartData(innerData);
    }


    public int  Test() {
        int pre = -1;
        while (true) {
            int random = (int) (Math.random()*300);
            if (pre>-1){
                int nowRandom = Math.abs(random - pre);
                if (nowRandom<5) {
                    pre = random;
                 return  pre;
                }
            } else {
                pre = random;
                return  pre;
            }
        }

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
