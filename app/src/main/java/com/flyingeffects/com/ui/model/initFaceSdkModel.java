package com.flyingeffects.com.ui.model;

import android.content.Context;
import android.widget.Toast;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.faceUtil.ConUtil;
import com.megvii.facepp.multi.sdk.BodySegmentApi;
import com.megvii.facepp.multi.sdk.FaceppApi;
import com.shixing.sxve.ui.view.WaitingDialog;

import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class initFaceSdkModel {

    private static Timer timer;
    private static TimerTask task;
    public static boolean hasLoadSdkOk = false;
    public static isAddSuccessCallback callback;

    public static void initFaceSdk() {
        new Thread(() -> {
            int result = FaceppApi.getInstance().initHandle(ConUtil.readAssetsData(BaseApplication.getInstance(), "megviifacepp_model"));
            long str=System.currentTimeMillis();
            LogUtil.d("OO3", "result=" + result);
            if (result == FaceppApi.MG_RETCODE_OK) {
                BodySegmentApi.getInstance().initBodySegment(2, BodySegmentApi.SEGMENT_MODE_FAST);//初始化人体抠像
                LogUtil.d("OO3", "模型加载完成");
                long str2=System.currentTimeMillis();
                hasLoadSdkOk = true;
                Observable.just((str2-str)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
//                        new AlertDialog.Builder(BaseApplication.getInstance())
//                                .setTitle("测试")
//                                .setMessage("模型加载耗时了"+aLong)
//                                .show();
                        float time=aLong/(float)1000;
                        Toast.makeText(BaseApplication.getInstance(),"模型加载耗时了"+time+"s",Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).start();
    }


    public static void getHasLoadSdkOk(isAddSuccessCallback successCallback, Context context) {
        callback = successCallback;
        if (hasLoadSdkOk) {
            callback.isSuccess();
        } else {
            WaitingDialog.openPragressDialog(context);
            startTimer();
        }
    }


    /***
     * 倒计时60s
     */
    private static void startTimer() {
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
                if (hasLoadSdkOk) {
                    WaitingDialog.closePragressDialog();
                    destroyTimer();
                    if (callback != null) {

                        callback.isSuccess();
                    }
                }

            }
        };
        timer.schedule(task, 0, 1000);
    }


    /**
     * user :TongJu  ; email:jutongzhang@sina.com
     * time：2018/10/15
     * describe:严防内存泄露
     **/
    private static void destroyTimer() {
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


    public interface isAddSuccessCallback {

        void isSuccess();

    }


}
