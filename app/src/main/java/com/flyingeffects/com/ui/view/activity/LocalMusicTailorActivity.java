package com.flyingeffects.com.ui.view.activity;

import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.enity.CutSuccess;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.ui.interfaces.view.LocalMusicTailorMvpView;
import com.flyingeffects.com.ui.presenter.LocalMusicTailorPresenter;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.timeUtils;
import com.flyingeffects.com.view.histogram.MyBarChartView;
import com.flyingeffects.com.view.histogram.MyBarChartView.BarData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


/**
 * description ：本地音乐裁剪
 * creation date: 2020/8/25
 * user : zhangtongju
 */
public class LocalMusicTailorActivity extends BaseActivity implements LocalMusicTailorMvpView {

    @BindView(R.id.animation_view_2)
    LottieAnimationView animation_view_2;

    @BindView(R.id.animation_view)
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

    @BindView(R.id.tv_time)
    TextView tv_allDuration;


    @BindView(R.id.mybarCharView)
    MyBarChartView mybarCharView;

    @BindView(R.id.tv_start)
    TextView tv_start;

    @BindView(R.id.tv_end)
    TextView tv_end;

    @BindView(R.id.tv_top_submit)
    TextView tv_top_submit;

    private long allDuration;

    private long nowPlayStartTime;

    private long nowPlayEndTime;


    @Override
    protected int getLayoutId() {
        return R.layout.act_local_music_tailor;
    }

    @Override
    protected void initView() {
        tv_top_submit.setVisibility(View.VISIBLE);
        tv_top_submit.setText("保存");
        ((TextView) findViewById(R.id.tv_top_title)).setText("裁剪音乐");
        findViewById(R.id.iv_top_back).setOnClickListener(this);
        Presenter = new LocalMusicTailorPresenter(this, this);
        videoPath = getIntent().getStringExtra("videoPath");
        videoInfo = getVideoInfo.getInstance().getRingDuring(videoPath);
        allDuration = videoInfo.getDuration();
        needDuration = getIntent().getLongExtra("needDuration", 10000);
        Presenter.setNeedDuration((int) needDuration);
        tv_allDuration.setText("模板时长" + timeUtils.timeParse(allDuration));
        mybarCharView.setCallback(new MyBarChartView.ProgressCallback() {
            @Override
            public void progress(float percent) {
                runOnUiThread(() -> {
                    LogUtil.d("OOM3", "percent=" + percent);
                    nowPlayStartTime = (long) (allDuration * percent);
                    tv_start.setText(timeUtils.timeParse(nowPlayStartTime));
                    nowPlayEndTime   = nowPlayStartTime + needDuration;
                    tv_end.setText(timeUtils.timeParse(nowPlayEndTime));
                });
            }


            @Override
            public void isDone() {
                animation_view.playAnimation();
                Presenter.SeekToPositionMusic((int) nowPlayStartTime);
            }
        });
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
        animation_view_2.setProgress(0f);
        animation_view_2.playAnimation();
    }


    @Override
    public void onStop() {
        super.onStop();
        animation_view.cancelAnimation();
        animation_view_2.cancelAnimation();

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
        LogUtil.d("OOM2", "numFrame=" + numFrame + ",percent=" + percent);
        myBarCharView.setBaseData(numFrame, percent, nowMaterial, needDuration);
        myBarCharView.setBarChartData(innerData);
    }


    /**
     * description ：音频播放完成回调
     * creation date: 2020/9/1
     * user : zhangtongju
     */
    @Override
    public void onPlayerCompletion() {
        animation_view.pauseAnimation();
        animation_view_2.pauseAnimation();
    }


    @Override
    public void isAudioCutDone(String audioPath) {
        LogUtil.d("OOM2","裁剪完成后音频的地址为"+audioPath);
        EventBus.getDefault().post(new CutSuccess(audioPath));
        this.finish();
    }


    @OnClick({R.id.tv_top_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_top_submit:
                //裁剪保存
                Presenter.toSaveCutMusic(nowPlayStartTime,nowPlayEndTime);
                break;


            default:
                break;
        }

};





}
