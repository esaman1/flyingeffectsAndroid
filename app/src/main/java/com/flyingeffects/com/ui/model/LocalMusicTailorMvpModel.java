package com.flyingeffects.com.ui.model;

import android.content.Context;

import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.ui.interfaces.model.LocalMusicTailorCallback;

import rx.subjects.PublishSubject;


public class LocalMusicTailorMvpModel {

    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private LocalMusicTailorCallback callback;
    private Context context;
    /**
     * 音频地址
     */
    private String soundPath;

    /**
     * 数据点
     */
    private int[] frameGains;

    /**
     * 数据量
     */
    private int frameCount;


    public LocalMusicTailorMvpModel(Context context, LocalMusicTailorCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    public void setSoundPath(String soundPath) {
        this.soundPath = soundPath;
    }


    public String   getSoundPath(){
        return soundPath;
    }


    public void setChartData(int[] frameGains, int frameCount) {
        this.frameCount = frameCount;
        this.frameGains = frameGains;
    }

    public int getFrameCount(){
        return frameCount;
    }

    public int[] getFrameGains(){
        return frameGains;
    }






}
