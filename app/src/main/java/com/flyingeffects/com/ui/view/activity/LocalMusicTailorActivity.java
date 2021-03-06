package com.flyingeffects.com.ui.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.entity.CutSuccess;
import com.flyingeffects.com.entity.VideoInfo;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.view.LocalMusicTailorMvpView;
import com.flyingeffects.com.ui.presenter.LocalMusicTailorPresenter;
import com.flyingeffects.com.ui.view.dialog.LoadingDialog;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.TimeUtils;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.view.RangeSeekBarForMusicView;
import com.flyingeffects.com.view.histogram.MyBarChartView;
import com.flyingeffects.com.view.histogram.MyBarChartView.BarData;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


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

    private Context mContext;

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

    private String title;


    @BindView(R.id.timeLineBar)
    RangeSeekBarForMusicView mRangeSeekBarView;


    /**
     * 当前是不是无限的
     */
    private boolean isInfinite = false;

    /**
     * 是否来自拍摄页面
     */
    private boolean isFromShoot;
    private LoadingDialog mLoadingDialog;


    @Override
    protected int getLayoutId() {
        return R.layout.act_local_music_tailor;
    }

    @Override
    protected void initView() {
        mContext = LocalMusicTailorActivity.this;
        tv_top_submit.setVisibility(View.VISIBLE);
        tv_top_submit.setText("下一步");
        isFromShoot = getIntent().getBooleanExtra("isFromShoot", false);
        title = getIntent().getStringExtra("title");
        ((TextView) findViewById(R.id.tv_top_title)).setText("裁剪音乐");
        findViewById(R.id.iv_top_back).setOnClickListener(view -> finish());
        Presenter = new LocalMusicTailorPresenter(this, this);
        videoPath = getIntent().getStringExtra("videoPath");
        videoInfo = getVideoInfo.getInstance().getRingDuring(videoPath);
        allDuration = videoInfo.getDuration();
        needDuration = getIntent().getLongExtra("needDuration", 10000);
        if (needDuration == 0) {
            isInfinite = false;
            needDuration = allDuration;
            mRangeSeekBarView.setVisibility(View.VISIBLE);
        } else {
            isInfinite = true;
            mRangeSeekBarView.setVisibility(View.GONE);
        }
        Presenter.setNeedDuration((int) needDuration);
        tv_allDuration.setText("模板时长" + TimeUtils.timeParse(needDuration));
        mybarCharView.setCallback(new MyBarChartView.ProgressCallback() {
            @Override
            public void progress(float percent) {
                runOnUiThread(() -> {
                    nowPlayStartTime = (long) (allDuration * percent);
                    tv_start.setText(TimeUtils.timeParse(nowPlayStartTime));
                    nowPlayEndTime = nowPlayStartTime + needDuration;
                    if (allDuration < needDuration) {
                        tv_end.setText(TimeUtils.timeParse(allDuration));
                    } else {
                        tv_end.setText(TimeUtils.timeParse(nowPlayEndTime));
                    }
                });
            }

            @Override
            public void isDone() {
                startTimer();
            }
        });
        mLoadingDialog = buildProgressDialog();

        Presenter.InitRangeSeekBar(mRangeSeekBarView);
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
        endTimer();
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
        LogUtil.d("OOM2", "nowMaterial=" + nowMaterial);
        float percent = nowMaterial / (float) needDuration;
        LogUtil.d("OOM2", "needDuration=" + needDuration + "percent=" + percent);
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
    protected void onPause() {
        super.onPause();
        Presenter.pauseMusic();
        endTimer();
    }

    @Override
    public void isAudioCutDone(String audioPath, String originalPath) {
        LogUtil.d("OOM2", "裁剪完成后音频的地址为" + audioPath);
        EventBus.getDefault().post(new CutSuccess(audioPath, originalPath, title));
        this.finish();
    }

    @Override
    public void initComplate() {
        mLoadingDialog.dismiss();
    }

    @Override
    public void setLoadProgress(int progress) {
        Observable.just(progress).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                mLoadingDialog.setProgress(integer);
            }
        });

    }

    private LoadingDialog buildProgressDialog() {
        LoadingDialog dialog = LoadingDialog.getBuilder(mContext)
                .setHasAd(false)
                .setTitle("加载中...")
                .build();
        dialog.show();
        return dialog;
    }

    /**
     * description ：无限滑动时候的百分比
     * creation date: 2021/2/19
     * user : zhangtongju
     */
    @Override
    public void onStopSeekThumbs(float startPercent, float endPercent) {
        runOnUiThread(() -> {
            nowPlayStartTime = (long) (allDuration * startPercent);
            tv_start.setText(TimeUtils.timeParse(nowPlayStartTime));
            if (endPercent != 0) {
                nowPlayEndTime = (long) (allDuration * endPercent);
            } else {
                nowPlayEndTime = allDuration;
            }
            tv_end.setText(TimeUtils.timeParse(nowPlayEndTime));
        });
        Presenter.setNeedDuration((int) (nowPlayEndTime - nowPlayStartTime));
        tv_allDuration.setText("模板时长" + TimeUtils.timeParse(nowPlayEndTime - nowPlayStartTime));
        startTimer();

    }


    @Override
    @OnClick({R.id.tv_top_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_top_submit:
                if (isFromShoot && needDuration > allDuration) {
                    //不裁剪大于当前音频的
                    EventBus.getDefault().post(new CutSuccess(Presenter.getSoundMusicPath(), Presenter.getSoundMusicPath(), title));
                    this.finish();
                } else {
                    //裁剪保存
                    StatisticsEventAffair.getInstance().setFlag(LocalMusicTailorActivity.this, "16_pick music_apply", title);
                    if (nowPlayEndTime - nowPlayStartTime < 1000) {
                        ToastUtil.showToast("裁剪时间太短啦");
                    } else {
                        Presenter.toSaveCutMusic(nowPlayStartTime, nowPlayEndTime);
                    }
                }
                break;


            default:
                break;
        }

    }


    private Timer timer;
    private TimerTask task;

    /***
     * 倒计时60s
     */
    private void startTimer() {
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                endTimer();
                handler.sendEmptyMessage(1);
            }
        };
        timer.schedule(task, 1000, 1000);
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            animation_view.playAnimation();
            Presenter.SeekToPositionMusic((int) nowPlayStartTime);
        }
    };

    private void endTimer() {
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
