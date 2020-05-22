package com.flyingeffects.com.view.animations.CustomMove;

import android.view.animation.DecelerateInterpolator;

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
    /**
     * 当前时长
     */
    private int nowDuration=0;




    /**
     * description ：初始化
     * creation date: 2020/5/21
     * param : totalDuration 插值器时长 nowDuration 当前时长  GetProgressCallback 返回阶段值
     * user : zhangtongju
     */
    public AnimationLinearInterpolator(int totalDuration,int nowDuration ,GetProgressCallback callback) {
        this.totalDuration = totalDuration;
        this.callback=callback;
        this.nowDuration=nowDuration;
    }


    /**
     * description ：停止动画
     * creation date: 2020/5/21
     * user : zhangtongju
     */
    public void StopAnimation(){
        endTimer();
    }

    public void PlayAnimation(){
        startTimer();
    }


    public float getNowInterpolatorProgress(float progress) {
        DecelerateInterpolator linearInterpolator = new DecelerateInterpolator();
        return linearInterpolator.getInterpolation(progress);
    }


    /**
     * 关闭timer 和task
     */
    private void endTimer() {
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
    private void startTimer() {
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
                        //绘制结束
                        endTimer();
                    }
                    nowDuration+=50;
                    float nowFloatTime=nowDuration/(float)totalDuration;
                    float progress=getNowInterpolatorProgress(nowFloatTime);
                    callback.progress(progress);

                }
            };
            timer.schedule(task, 0, 50);
        }
    }




    /**
     * description ：获得当前进度
     * creation date: 2020/5/21
     * user : zhangtongju
     */
    public interface  GetProgressCallback{
        void progress(float progress);
    }




}
