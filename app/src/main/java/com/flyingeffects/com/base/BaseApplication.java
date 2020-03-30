package com.flyingeffects.com.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.flyingeffects.com.R;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.manager.MediaLoader;
import com.flyingeffects.com.utils.ChannelUtil;
import com.flyingeffects.com.utils.CrashHandler;
import com.flyingeffects.com.utils.LogUtil;
import com.lansosdk.videoeditor.LanSoEditor;
import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shixing.sxvideoengine.License;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;

import java.util.Locale;

import cn.jpush.android.api.JPushInterface;
import cn.nt.lib.analytics.NTAnalytics;
import rx.subjects.PublishSubject;

/**
 * Created by 张同举
 * on 2017/8/14.
 */

public class BaseApplication extends MultiDexApplication {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private static BaseApplication myzxApp;

    @Override
    public void onCreate() {
        super.onCreate();
        myzxApp = this;
        MultiDex.install(this); //分包支持
        initLansong();
        Hawk.init(this).build();
        //registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
        initAlbum();
        initLicense();
        initYouMeng();
        initJPush();
        initZt();
        keepCrash();


    }

    private void initLansong() {
        LanSoEditor.initSDK(getApplicationContext(), "jiu_LanSongSDK_android5.key");
    }


    /***
     * 保存错误日志
     */
    public void keepCrash() {
        CrashHandler.getInstance().init(this);
    }


    /**
     * 中台
     */
    private void initZt() {
        NTAnalytics.setDebug(true);
        NTAnalytics.init(this, "87", "vQlTNPzHOzBYHzkg", ChannelUtil.getChannel(this));

    }


    public void initJPush() {
        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);
    }


    private void initYouMeng() {
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_MANUAL);  //页面采集模式
        MobclickAgent.setCatchUncaughtExceptions(true);
        UMConfigure.setProcessEvent(true); // 支持在子进程中统计自定义事件
        UMConfigure.setLogEnabled(false);
        UMConfigure.init(this, BaseConstans.UMENGAPPID, ChannelUtil.getChannel(this), UMConfigure.DEVICE_TYPE_PHONE, "");
//        PlatformConfig.setWeixin("wx48a4ba91f880abcc", "68932433247e0f33ec8c93c89e9bd374");
//        PlatformConfig.setQQZone("1109289339", "hdOiuQsp2iudqu3v");

    }

    /**
     * description ：註冊VE
     * date: ：2019/5/8 16:51
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public void initLicense() {
//        String licenseID="UJ03ctDfZ1ZTWzTF2uC2dmWnOeyD0dk/UhyEu+npLrXEgeMlo2PBMaoHwffFV7bS6O48q0I/8qI4epo2acEbZyiXD1Im4oUNERrPhVtu2nPtxeIz1yRO1BKPQGh5Jult1SlspS6g9pD/6zyP3KsdfoW5wUkc19dzSZrq9kmuYULB2j/o7g6Rh71HYMIYoq4avDT8DDeO1P+GmeUz793hELjUMUBbZwUiYC+xDMNM2LOnWK1DEAzAWWwyh3/mJdcwCyc4MY2LttOa0ksn6iWPtFxrBxw97cIFLdhLkEoMibeCPCLtmfjofB3VnEvo9AWC85vbpSQbzw5mqD8mALQbJJe8/vZWgBFSPqv3PwDZL3OI2APv5CBxgKTmWE5lDRHpUN4f8LDFqi/yyhoRj4Nc/aJdqdte38RjJPLX1tt6J78=";
        String licenseID = "nnogIjD3C8du/T2PWYvFbMkJxM2Vw8FpkCs9RqiwjjrEgeMlo2PBMaoHwffFV7bS6O48q0I/8qI4epo2acEbZyiXD1Im4oUNERrPhVtu2nNSnXyjUGr9dLmrYazM4YmNE/A9T6ir5gt3XEs7IjfWfsFAdP+uvPvoKEzu8/pZLRQacEoaYzl1w04Wkn0t0aXWu3l92WacTnKG2JFyCSzPwUgZiqh2Z8xbQdpRYL22HYqMAkhHeNO5Vix3sYRWtKfm59U3wgWtXoU+1gmAICjM1WDRlgyg80Os1BRSzkp9TG7sb7QJUzFdLvo2cpfhnFyBfRBvoykvllQZaPmbC73J+FB8X4zyN1ZESuYOdfoKvYZ3i0S68Rk0izoqbarUpUnkTUUNViGopPKKUXSaufSd+ZxWOxnqjIdyx4a2OhE4vbY=";
        License l = License.init(licenseID);
        boolean isValid = l.isValid();
        LogUtil.d("OOM", "isValid=" + isValid);
    }


    private long onStopTime;
    private int activityAount = 0;
    ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            activityAount++;
        }

        @Override
        public void onActivityResumed(Activity activity) {


        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
            activityAount--;
            if (activityAount == 0) {
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    };


    static {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
//            layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
            return new MaterialHeader(context).setColorSchemeResources(R.color.theme_toast, R.color.blue_0b84d3);
        });
    }


    public static BaseApplication getInstance() {
        return myzxApp;
    }

    private void initAlbum() {
        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(new MediaLoader())
                .setLocale(Locale.getDefault())
                .build()
        );
    }


}


