package com.flyingeffects.com.ui.view.activity;

import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.ui.interfaces.view.LocalMusicTailorMvpView;
import com.flyingeffects.com.ui.presenter.LocalMusicTailorPresenter;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.histogram.MyBarChartView;
import com.flyingeffects.com.view.histogram.MyBarChartView.BarData;

import java.util.ArrayList;

import butterknife.BindView;


/**
 * description ：本地音乐裁剪
 * creation date: 2020/8/25
 * user : zhangtongju
 */
public class LocalMusicTailorActivity extends BaseActivity implements LocalMusicTailorMvpView {

    @BindView(R.id.animation_view_2)
    LottieAnimationView animation_view;
    private LocalMusicTailorPresenter Presenter;
    private String videoPath;
    /**
     * 原視頻需要裁剪的大小
     */
    private long needDuration;

    /**
     * 媒体信息
     */
    private VideoInfo videoInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.act_local_music_tailor;
    }

    @Override
    protected void initView() {
        ((TextView) findViewById(R.id.tv_top_title)).setText("裁剪音乐");
        findViewById(R.id.iv_top_back).setOnClickListener(this);
        Presenter = new LocalMusicTailorPresenter(this, this);
        videoPath = getIntent().getStringExtra("videoPath");
        videoInfo = getVideoInfo.getInstance().getRingDuring(videoPath);
        needDuration = getIntent().getLongExtra("needDuration", 10000);
    }


    @Override
    protected void initAction() {
        Presenter.DownPath(videoPath);
        animStart();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Presenter.OnDestroy();
    }

    private void animStart() {
        animation_view.setProgress(0f);
        animation_view.playAnimation();
    }


    @Override
    public void onStop() {
        super.onStop();
        animation_view.cancelAnimation();
    }


    /**
     * description ：显示波形图
     * creation date: 2020/8/26
     * user : zhangtongju
     */
    @Override
    public void showCharView(int[] date, int numFrame) {
        ArrayList<BarData> innerData = new ArrayList<>();
        for (int num : date
        ) {
            innerData.add(new BarData(num, ""));
        }
        MyBarChartView myBarCharView = findViewById(R.id.mybarCharView);
        long nowMaterial = videoInfo.getDuration();
        float percent = nowMaterial / (float) needDuration;
        LogUtil.d("OOM2","numFrame="+numFrame+",percent="+percent);
        myBarCharView.setBaseData(numFrame, percent);
        myBarCharView.setBarChartData(innerData);
    }
}
