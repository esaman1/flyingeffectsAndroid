package com.flyingeffects.com.manager;

import android.app.Activity;
import android.content.Context;

import com.flyingeffects.com.utils.LogUtil;
import com.nineton.ntadsdk.itr.ScreenAdCallBack;
import com.nineton.ntadsdk.manager.ScreenAdManager;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class AdManager {

    private static AdManager thisModel;

    public static AdManager getInstance() {

        if (thisModel == null) {
            thisModel = new AdManager();
        }
        return thisModel;

    }


    public void showCpAd(Context context,String  id){
        Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
            ScreenAdManager screenAdManager = new ScreenAdManager();
            screenAdManager.showScreenAd((Activity) context,id , new ScreenAdCallBack() {
                @Override
                public void onScreenAdShow() {
                    LogUtil.d("OOM","onScreenAdShow");
                }

                @Override
                public void onScreenAdError(String errorMsg) {
                    LogUtil.d("OOM","onScreenAdError="+errorMsg);
                }

                @Override
                public void onScreenAdClose() {
                    LogUtil.d("OOM","onScreenAdClose=");
                }

                @Override
                public boolean onScreenAdClicked(String title, String url, boolean isNtAd, boolean openURLInSystemBrowser) {
                    return false;
                }
            });
        });
    }



    public void showCpAd(Context context,String  id,Callback callback){
        Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
            ScreenAdManager screenAdManager = new ScreenAdManager();
            screenAdManager.showScreenAd((Activity) context,id , new ScreenAdCallBack() {
                @Override
                public void onScreenAdShow() {
                    LogUtil.d("OOM","onScreenAdShow");
                }

                @Override
                public void onScreenAdError(String errorMsg) {
                    LogUtil.d("OOM","onScreenAdError="+errorMsg);
                }

                @Override
                public void onScreenAdClose() {
                    callback.adClose();
                    LogUtil.d("OOM","onScreenAdClose=");
                }

                @Override
                public boolean onScreenAdClicked(String title, String url, boolean isNtAd, boolean openURLInSystemBrowser) {
                    return false;
                }
            });
        });
    }


    public interface  Callback{
        void adClose();

    }


}
