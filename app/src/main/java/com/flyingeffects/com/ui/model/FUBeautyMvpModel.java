package com.flyingeffects.com.ui.model;

import android.content.Context;
import android.text.TextUtils;

import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.ui.interfaces.model.FUBeautyMvpCallback;

import java.util.ArrayList;

import rx.subjects.PublishSubject;


public class FUBeautyMvpModel {


    private ArrayList<String> timeDataList = new ArrayList<>();
    private int[] timeDataInt = {5000, 15000, 60000, 0};

    /***当前音乐选择时长*/
    public int nowChooseDuration;





    public String getOriginalMusicPath() {
        return originalMusicPath;
    }

    public void setOriginalMusicPath(String originalMusicPath) {
        this.originalMusicPath = originalMusicPath;
    }

    /**当前音乐原地址*/
    private String originalMusicPath;


    public String getMusicPath() {
        return musicPath;
    }

    public void setMusicPath(String musicPath,String originalMusicPath) {
        this.musicPath = musicPath;
        this.originalMusicPath=originalMusicPath;
    }


    /**
     * 如果不为""那么设置了背景音乐的
     */
    public String musicPath;


    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();

    private FUBeautyMvpCallback callback;

    public FUBeautyMvpModel(Context context, FUBeautyMvpCallback callback) {
        timeDataList.add("5秒");
        timeDataList.add("15秒");
        timeDataList.add("60秒");
        timeDataList.add("无限");
    }


    /**
     * description ：写死的时长配置
     * creation date: 2021/1/28
     * user : zhangtongju
     */
    public ArrayList<String> GetTimeData() {
        return timeDataList;
    }


    /**
     * description ：得到当前选择时间
     * creation date: 2021/1/28
     * user : zhangtongju
     */
    public int FetChooseDuration(String text) {

        if (TextUtils.isEmpty(text)) {
            return timeDataInt[2];
        } else {
            int nowChooseId = 0;
            for (int i = 0; i < timeDataList.size(); i++) {
                if (timeDataList.get(i).equals(text)) {
                    nowChooseId = i;
                    break;
                }
            }
            return timeDataInt[nowChooseId];
        }
    }


    public void SetNowChooseMusic(String musicPath,String originalPath) {
        if (!TextUtils.isEmpty(musicPath)) {
            setMusicPath(musicPath,originalPath);
        } else {
            setMusicPath("","");
        }
    }
}
