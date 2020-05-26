package com.flyingeffects.com.view.animations.CustomMove;

import android.view.animation.LinearInterpolator;

import com.flyingeffects.com.utils.LogUtil;

import java.util.Timer;
import java.util.TimerTask;




/**
 * description ：匀速插值器，返回当前时间的点
 * creation date: 2020/5/21
 * user : zhangtongju
 */
public class AnimationLinearInterpolator {

    /**
     * 总时长
     */
    private int totalDuration;
    private boolean isPlaying = false;
    private Timer timer;
    private TimerTask task;
    private GetProgressCallback callback;
    private  int nowDuration;



    /**
     * description ：初始化
     * creation date: 2020/5/21
     * param : totalDuration 插值器时长 nowDuration 当前时长  GetProgressCallback 返回阶段值
     * user : zhangtongju
     */
    public AnimationLinearInterpolator(int totalDuration ,GetProgressCallback callback) {
        this.totalDuration = totalDuration;
        this.callback=callback;
    }


    /**
     * description ：停止动画
     * creation date: 2020/5/21
     * user : zhangtongju
     */
    public void StopAnimation(){
        endTimer();
    }

    public void PlayAnimation(int delay){
        endTimer();
        startTimer(delay);
    }

    public void PlayAnimation(){
        endTimer();
        startTimer(0);
    }


    public float getNowInterpolatorProgress(float progress) {
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        return linearInterpolator.getInterpolation(progress);
    }


    /**
     * 关闭timer 和task
     */
    public void endTimer() {
        isPlaying=false;
        destroyTimer();
    }


    /**
     * user :TongJu  ; email:jutongzhang@sina.com
     * time：2018/10/15
     * describe:严防内存泄露
     **/
    private void destroyTimer() {
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




    /**
     * description ：20帧
     * creation date: 2020/5/21
     * user : zhangtongju
     */
    private boolean  isDone;
    private void startTimer(int delay) {
        isDone=false;
        if(!isPlaying){
            isPlaying=true;
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
                    if(nowDuration>=totalDuration){
                        nowDuration=0;
                    }
                    nowDuration+=10;
                    LogUtil.d("xxx2","nowDuration="+nowDuration);
                    float nowFloatTime=nowDuration/(float)totalDuration;
                    LogUtil.d("xxx2","nowFloatTime="+nowFloatTime);
                    float progress=getNowInterpolatorProgress(nowFloatTime);
                    callback.progress(progress,isDone);

                }
            };
            timer.schedule(task, delay, 10);
        }
    }




    /**
     * description ：获得当前进度
     * creation date: 2020/5/21
     * user : zhangtongju
     */
    public interface  GetProgressCallback{
        void progress(float progress,boolean isDown);
    }




}
