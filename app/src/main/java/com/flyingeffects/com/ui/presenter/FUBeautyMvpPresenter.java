package com.flyingeffects.com.ui.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;

import com.example.horizontalselectedviewlibrary.HorizontalselectedView;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.ui.interfaces.model.FUBeautyMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.FUBeautyMvpView;
import com.flyingeffects.com.ui.model.FUBeautyMvpModel;
import com.flyingeffects.com.ui.view.activity.ChooseMusicActivity;
import com.flyingeffects.com.utils.LogUtil;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class FUBeautyMvpPresenter extends BasePresenter implements FUBeautyMvpCallback {

    private FUBeautyMvpView fUBeautyMvpView;
    private FUBeautyMvpModel fUBeautyMvpmodel;
    private Context context;
    private HorizontalselectedView horizontalselectedView;
    private Timer timer;
    private TimerTask task;

    /**
     * 当前倒计时状态 0 表示动画前倒计时 1 表示 录制倒计时
     */
    private int countDownStatus;
    /**
     * 当前选择位数
     */
    private int nowChooseCutDownNum = 0;


    public FUBeautyMvpPresenter(Context context, FUBeautyMvpView fUBeautyMvpView, HorizontalselectedView horizontalselectedView) {
        this.fUBeautyMvpView = fUBeautyMvpView;
        this.horizontalselectedView = horizontalselectedView;
        this.context = context;
        fUBeautyMvpmodel = new FUBeautyMvpModel(context, this);
        horizontalselectedView.setData(fUBeautyMvpmodel.GetTimeData());
        horizontalselectedView.setSeeSize(4);
    }

    public void SetNowChooseMusic(String musicPath, String originalPath) {
        fUBeautyMvpmodel.SetNowChooseMusic(musicPath, originalPath);
    }

    /**
     * description ：开启倒计时
     * creation date: 2021/1/28
     * user : zhangtongju
     */
    private int nowCountDownNum;


    public void StartCountDown() {
        countDownStatus = 0;
        if (nowChooseCutDownNum == 0) {
            nowCountDownNum = 4;
        } else if (nowChooseCutDownNum == 1) {
            nowCountDownNum = 8;
        } else {
            nowCountDownNum = 11;
        }
        startTimer();
    }

    /**
     * description ：调整到音乐选取页面
     * creation date: 2021/1/28
     * user : zhangtongju
     */
    public void IntoChooseMusic() {
        String text = horizontalselectedView.getSelectedString();
        long duration = fUBeautyMvpmodel.FetChooseDuration(text);
        Intent intent = new Intent(context, ChooseMusicActivity.class);
        LogUtil.d("OOM2", "当前需要的音乐时长为" + duration);
        intent.putExtra("needDuration", duration);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }


    /**
     * description ：点击倒计时功能
     * creation date: 2021/1/29
     * user : zhangtongju
     */
    public void clickCountDown(ImageView iv) {

        nowChooseCutDownNum++;
        if (nowChooseCutDownNum > 2) {
            nowChooseCutDownNum = 0;
        }
        if (nowChooseCutDownNum == 0) {
            iv.setImageResource(R.mipmap.cout_down_3);
        } else if (nowChooseCutDownNum == 1) {
            iv.setImageResource(R.mipmap.cout_down_7);
        } else {
            iv.setImageResource(R.mipmap.cout_down_10);
        }
    }


    /**
     * description ：开始录像，1 如果有音乐，就播放音乐，如果切换了下面时长，就播放原视频音乐  2 开启进度动画
     * creation date: 2021/2/1
     * user : zhangtongju
     */
    private MediaPlayer mediaPlayer;
    private int allNeedDuration;

    public void startRecord() {
        //1
        LogUtil.d("OOM", "startRecord");
        String musicCutPath = fUBeautyMvpmodel.getMusicPath();
        LogUtil.d("OOM", "musicCutPath=" + musicCutPath);
        if (!TextUtils.isEmpty(musicCutPath)) {
            initMediaPlayer(musicCutPath);
            mediaPlayer.start();
        }

        //2  开启进度动画
        String text = horizontalselectedView.getSelectedString();
        long duration = fUBeautyMvpmodel.FetChooseDuration(text);
        if(duration!=0){
            fUBeautyMvpView.nowChooseRecordIsInfinite(false);
            countDownStatus = 1;
            nowCountDownNum = (int) (duration / 1000);
            allNeedDuration = nowCountDownNum;
            startTimer();
        }else{
            fUBeautyMvpView.nowChooseRecordIsInfinite(true);
        }




    }


    public void stopRecord() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
    }


    private void initMediaPlayer(String path) {
        try {
            mediaPlayer = new MediaPlayer();
            File file = new File(path);
            mediaPlayer.setDataSource(file.getPath());//指定音频文件路径
            mediaPlayer.setLooping(true);//设置为循环播放
            mediaPlayer.prepare();//初始化播放器MediaPlayer
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * description ：倒计时功能
     * creation date: 2021/1/28
     * user : zhangtongju
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
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        };
        timer.schedule(task, 0, 1000);
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    nowCountDownNum = nowCountDownNum - 1;
                    LogUtil.d("OOM", "返回值为" + nowCountDownNum);
                    float progress = 0f;
                    if (countDownStatus == 1) {
                        progress = nowCountDownNum / (float) allNeedDuration;
                        progress = 1 - progress;
                    }
                    fUBeautyMvpView.showCountDown(nowCountDownNum, countDownStatus, progress);
                    if (nowCountDownNum == 0) {
                        endTimer();
                    }
                    break;


                default:

                    break;
            }
        }
    };


    /**
     * 关闭timer 和task
     */
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


    private float countDownTotal;
    private void startProgress() {
        countDownTotal = nowCountDownNum;
        while (nowCountDownNum > 0) {
            countDownTotal = countDownTotal - 0.05f;
            float progress = countDownTotal / (float) allNeedDuration;
            progress = 1 - progress;
            Observable.just(progress).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Float>() {
                @Override
                public void call(Float aFloat) {
                    fUBeautyMvpView.showCountDown((int) countDownTotal, 1, aFloat);
                }
            });
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * description ：销毁，清除音乐播放
     * creation date: 2021/2/1
     * user : zhangtongju
     */
    public void OnDestroy() {
        stopRecord();
    }


}
