package com.flyingeffects.com.ui.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.horizontalselectedviewlibrary.HorizontalselectedView;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.mvpBase.BasePresenter;
import com.flyingeffects.com.ui.interfaces.model.FUBeautyMvpCallback;
import com.flyingeffects.com.ui.interfaces.view.FUBeautyMvpView;
import com.flyingeffects.com.ui.model.FUBeautyMvpModel;
import com.flyingeffects.com.ui.view.activity.ChooseMusicActivity;
import com.flyingeffects.com.utils.LogUtil;

import java.util.Timer;
import java.util.TimerTask;


public class FUBeautyMvpPresenter extends BasePresenter implements FUBeautyMvpCallback {

    private FUBeautyMvpView fUBeautyMvpView;
    private FUBeautyMvpModel fUBeautyMvpmodel;
    private Context context;
    private HorizontalselectedView horizontalselectedView;
    private Timer timer;
    private TimerTask task;
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

    public void SetNowChooseMusic(String musicPath) {
        fUBeautyMvpmodel.SetNowChooseMusic(musicPath);
    }

    /**
     * description ：开启倒计时
     * creation date: 2021/1/28
     * user : zhangtongju
     */
    private int nowCountDownNum;

    public void StartCountDown() {
//        String text = horizontalselectedView.getSelectedString();
//        LogUtil.d("OOM", "text=" + text);
//        nowCountDownNum = fUBeautyMvpmodel.FetChooseDuration(text) / 1000;
//        LogUtil.d("OOM", "nowCountDownNum=" + nowCountDownNum);
        if(nowChooseCutDownNum==0){
            nowCountDownNum=3;
        }else if(nowChooseCutDownNum==1){
            nowCountDownNum=7;
        }else{
            nowCountDownNum=14;
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
        int duration = fUBeautyMvpmodel.FetChooseDuration(text);
        Intent intent = new Intent(context, ChooseMusicActivity.class);
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
                    fUBeautyMvpView.showCountDown(nowCountDownNum);
                    if (nowCountDownNum == 0) {
                        endTimer();
                    }
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


}
