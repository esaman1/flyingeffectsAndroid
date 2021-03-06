package com.flyingeffects.com.view.animations.CustomMove;

import android.view.animation.CycleInterpolator;
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
    private int nowDuration;

    /**
     * 差值类型  0表示匀速， 1 表示正弦函数
     */
    private int interpolatorType = 0;


    /**
     * description ：初始化
     * creation date: 2020/5/21
     * param : totalDuration 插值器时长 nowDuration 当前时长  GetProgressCallback 返回阶段值
     * user : zhangtongju
     */
    public AnimationLinearInterpolator(int totalDuration, GetProgressCallback callback) {
        this.totalDuration = totalDuration;
        this.callback = callback;
    }

    public void setInterpolatorType(int interpolatorType) {
        this.interpolatorType = interpolatorType;
    }


    /**
     * description ：停止动画
     * creation date: 2020/5/21
     * user : zhangtongju
     */
    public void StopAnimation() {
        LogUtil.d("OOM5", "StopAnimation");
        endTimer();
    }

    public void PlayAnimation(int delay) {
        LogUtil.d("OOM5", "PlayAnimationDelay");
        endTimer();
        startTimer(delay);
    }

    public void PlayAnimation() {
        LogUtil.d("OOM5", "PlayAnimation");
        endTimer();
        startTimer(0);
    }


    public void setNowDuration(int duration) {
        this.nowDuration = duration;
    }


    private boolean isCirculation = true;


    /**
     * description ：是否循环
     * creation date: 2020/12/22
     * user : zhangtongju
     */
    public void SetCirculation(boolean isCirculation) {
        this.isCirculation = isCirculation;
    }

    public void PlayAnimationNoTimer(float percentage) {
        LogUtil.d("OOM5", "percentage="+percentage);
        if (interpolatorType == 0) {
            callback.progress(getNowInterpolatorProgress(percentage), isDone);
        } else {
            callback.progress(getNowCycleInterpolatorProgress(percentage), isDone);
        }
    }


    /**
     * 匀速运动
     */
    public float getNowInterpolatorProgress(float progress) {
     LogUtil.d("OOM5", "当前动画没消失匀速运动");
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        return linearInterpolator.getInterpolation(progress);
    }


    /**
     * description ：正弦函数
     * creation date: 2020/5/28
     * user : zhangtongju
     */

    public float getNowCycleInterpolatorProgress(float progress) {
        LogUtil.d("OOM5", "当前动画没消失正玄函数");
        CycleInterpolator cycleInterpolator = new CycleInterpolator(1);
        return cycleInterpolator.getInterpolation(progress);
    }


    /**
     * 关闭timer 和task
     */
    public void endTimer() {
        isPlaying = false;
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
            LogUtil.d("OOM5","timer != null置空");
        }
        if (task != null) {
            task.cancel();
            task = null;
            LogUtil.d("OOM5","task != null置空");
        }
    }


    /**
     * description ：20帧
     * creation date: 2020/5/21
     * user : zhangtongju
     */
    private boolean isDone;

    private void startTimer(int delay) {
        isDone = false;
        if (!isPlaying) {
            isPlaying = true;
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
                    LogUtil.d("OOM5", "计时器还在跑");
                    if (nowDuration >= totalDuration) {
                        if (!isCirculation) {
                            StopAnimation();
                            callback.progress(1, isDone);
                        } else {
                            nowDuration = 0;
                        }
                    }
                    nowDuration += 5;
                    float nowFloatTime = nowDuration / (float) totalDuration;
                    float progress;
                    if (interpolatorType == 0) {
                        progress = getNowInterpolatorProgress(nowFloatTime);
                    } else {
                        progress = getNowCycleInterpolatorProgress(nowFloatTime);
                    }
                    callback.progress(progress, isDone);

                }
            };
            timer.schedule(task, delay, 5);
        }
    }


    /**
     * description ：获得当前进度
     * creation date: 2020/5/21
     * user : zhangtongju
     */
    public interface GetProgressCallback {
        void progress(float progress, boolean isDown);
    }


}
