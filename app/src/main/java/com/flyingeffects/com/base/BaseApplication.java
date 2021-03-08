package com.flyingeffects.com.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.bytedance.sdk.open.douyin.DouYinOpenApiFactory;
import com.bytedance.sdk.open.douyin.DouYinOpenConfig;
import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.faceunity.FURenderer;
import com.flyingeffects.com.R;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.isIntoBackground;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.MediaLoader;
import com.flyingeffects.com.manager.TTAdManagerHolder;
import com.flyingeffects.com.ui.view.activity.WelcomeActivity;
import com.flyingeffects.com.utils.ChannelUtil;
import com.flyingeffects.com.utils.CrashHandler;
import com.flyingeffects.com.utils.DateUtils;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.SystemUtil;
import com.green.hand.library.EmojiManager;
import com.lansosdk.box.OnLanSongLogOutListener;
import com.lansosdk.videoeditor.LanSoEditor;
import com.nineton.ntadsdk.NTAdConfig;
import com.nineton.ntadsdk.NTAdSDK;
import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shixing.sxvideoengine.License;
import com.shixing.sxvideoengine.SXLog;
import com.umeng.commonsdk.UMConfigure;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;

import java.util.Locale;

import cn.jpush.android.api.JPushInterface;
import cn.nt.lib.analytics.NTAnalytics;
import de.greenrobot.event.EventBus;
import rx.subjects.PublishSubject;

/**
 * Created by 张同举
 * on 2017/8/14.
 */

public class BaseApplication extends MultiDexApplication {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private static BaseApplication baseApp;
    private boolean isActive = true;
    /**
     * 默认从APP退到后台值为true
     */
    public boolean isBackHome = true;

    @Override
    public void onCreate() {
        super.onCreate();
        baseApp = this;
        //分包支持
        MultiDex.install(this);
        initLansong();
        Hawk.init(this).build();
        initLicense();
        initYouMeng();
        initJPush();
        initZt();
        //闪验SDK初始化（建议放在Application的onCreate方法中执行）
        initShanyanSDK(this);
        initByteDanceShare();
//        keepCrash();
        initNTAdSDK();
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
        TTAdManagerHolder.init(this);
        EmojiManager.init(this);
        initAlbum();
        FURenderer.initFURenderer(this);
    }


    private void initAlbum() {
        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(new MediaLoader())
                .setLocale(Locale.getDefault())
                .build()
        );
    }


    private void initNTAdSDK() {
        NTAdSDK.init(this
                , new NTAdConfig.Builder()
                        .appName("飞闪")
                        .appVersion(SystemUtil.getVersionName(this))
                        .appId("61074cddf23c0a8dd2b7e00996057e78")
                        .appChannel(ChannelUtil.getChannel(this))
                        .TTAppKey(AdConfigs.APP_ID_CSJ)
                        .KSAppKey("517200002")
                        .KaiJiaAppKey("68662a49")
                        .GDTAppKey(AdConfigs.APP_ID_GDT)
                        .isDebug(false)
                        .build());
    }


    /**
     * description ：闪验
     * creation date: 2020/4/7
     * user : zhangtongju
     */
    private void initShanyanSDK(Context context) {
        OneKeyLoginManager.getInstance().init(context, "SSjHAvIf", (code, result) -> {
            //闪验SDK初始化结果回调
            LogUtil.d("OOM", "初始化： code==" + code + "   result==" + result);
        });
    }

    private void initLansong() {
        LanSoEditor.initSDK(getApplicationContext(), "jiu_LanSongSDK_android5.key");
        LanSoEditor.setSDKLogOutListener(new OnLanSongLogOutListener() {
            @Override
            public void onLogOut(int i, String s) {
                LogUtil.d("lansong", "蓝松具体错误信息为" + s);
            }
        });
    }


    /***
     * 保存错误日志
     */
    public void keepCrash() {
        if ("test".equals(ChannelUtil.getChannel(BaseApplication.getInstance()))) {
            CrashHandler.getInstance().init(this);
        }
    }

    /**
     * 中台
     */
    private void initZt() {
//        NTAnalytics.setDebug(true);
        NTAnalytics.init(this, "87", "vQlTNPzHOzBYHzkg", ChannelUtil.getChannel(this));
    }


    public void initJPush() {
        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);
    }

    private void initYouMeng() {

        UMConfigure.preInit(this, BaseConstans.UMENGAPPID, ChannelUtil.getChannel(this));

    }

    /**
     * 初始化抖音分享
     */
    private void initByteDanceShare() {
        //抖音分享
        DouYinOpenApiFactory.init(new DouYinOpenConfig(BaseConstans.DOUYINSHARE_CLIENTKEY));
    }

    /**
     * description ：註冊VE
     * date: ：2019/5/8 16:51
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public void initLicense() {
        String licenseID = "nnogIjD3C8du/T2PWYvFbMkJxM2Vw8FpkCs9RqiwjjrEgeMlo2PBMaoHwffFV7bS6O48q0I/8qI4epo2acEbZyiXD1Im4oUNERrPhVtu2nNSnXyjUGr9dLmrYazM4YmNE/A9T6ir5gt3XEs7IjfWfsFAdP+uvPvoKEzu8/pZLRQacEoaYzl1w04Wkn0t0aXWu3l92WacTnKG2JFyCSzPwUgZiqh2Z8xbQdpRYL22HYqMAkhHeNO5Vix3sYRWtKfm59U3wgWtXoU+1gmAICjM1WDRlgyg80Os1BRSzkp9TG7sb7QJUzFdLvo2cpfhnFyBfRBvoykvllQZaPmbC73J+FB8X4zyN1ZESuYOdfoKvYZ3i0S68Rk0izoqbarUpUnkTUUNViGopPKKUXSaufSd+ZxWOxnqjIdyx4a2OhE4vbY=";
        License l = License.init(licenseID);

        boolean isValid = l.isValid();
        SXLog.showInLogcat();
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

            if (!isActive && BaseConstans.getHasAdvertising() == 1) {
                isActive = true;
                LogUtil.d("BASEACTIVITY2", "进入了前台");
                isBackHome = false;
                intoKaiPing(System.currentTimeMillis() - onStopTime);
                //  EventBus.getDefault().post(new isIntoBackground(false));  //消息通知
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
            activityAount--;
            if (activityAount == 0) {
                EventBus.getDefault().post(new isIntoBackground(true));  //消息通知
                onStopTime = System.currentTimeMillis();
                isActive = false;
                isBackHome = true;
                LogUtil.d("BASEACTIVITY2", "进入了后台");
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };


    /**
     * description ：进入开屏广告
     * date: ：2019/11/5 15:21
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    private void intoKaiPing(long time) {
        String times = DateUtils.getCurrentTime_m(time);
        int timeI = Integer.parseInt(times);
        LogUtil.d("BASEACTIVITY", "timeI=" + timeI);
        if (timeI > BaseConstans.showAgainKaipingAd) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("fromBackstage", true);
            startActivity(intent);
        }
    }

    static {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
//            layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
            return new MaterialHeader(context).setColorSchemeResources(R.color.theme_toast, R.color.blue_0b84d3);
        });
    }


    public static BaseApplication getInstance() {
        return baseApp;
    }


}


