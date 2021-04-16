package com.flyingeffects.com.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.bytedance.sdk.open.douyin.DouYinOpenApiFactory;
import com.bytedance.sdk.open.douyin.DouYinOpenConfig;
import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.faceunity.FURenderer;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.anchortask.AnchorTaskCreator;
import com.flyingeffects.com.base.anchortask.TaskNameConstants;
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
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shixing.sxvideoengine.License;
import com.shixing.sxvideoengine.SXLog;
import com.umeng.commonsdk.UMConfigure;
import com.xj.anchortask.library.AnchorProject;
import com.xj.anchortask.library.OnProjectExecuteListener;
import com.xj.anchortask.library.log.LogUtils;
import com.xj.anchortask.library.monitor.OnGetMonitorRecordCallback;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import cn.nt.lib.analytics.NTAnalytics;
import de.greenrobot.event.EventBus;
import rx.subjects.PublishSubject;

/**
 * Created by 张同举
 * on 2017/8/14.
 */

public class BaseApplication extends MultiDexApplication {
    private static final String TAG = "BaseApplication";
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
        Log.d(TAG, "Application start");
        baseApp = this;
        @NotNull AnchorProject project = new AnchorProject.Builder().setContext(this)
                .setLogLevel(LogUtils.LogLevel.DEBUG)
                .setAnchorTaskCreator(new AnchorTaskCreator())
                .addTask(TaskNameConstants.INIT_YOU_MENG)
                .addTask(TaskNameConstants.MULTI_DEX)
                .addTask(TaskNameConstants.INIT_LAN_SONG)
                .addTask(TaskNameConstants.INIT_HAWK)
                .addTask(TaskNameConstants.INIT_VE)
                .addTask(TaskNameConstants.INIT_JPUSH)
                .addTask(TaskNameConstants.INIT_ZT)
                .addTask(TaskNameConstants.INIT_SHAN_YAN)
                .addTask(TaskNameConstants.INIT_BYTE_DANCE_SHARE)
                .addTask(TaskNameConstants.INIT_AD_SDK)
                .addTask(TaskNameConstants.INIT_TTAD)
                .addTask(TaskNameConstants.INIT_EMOJI)
                .addTask(TaskNameConstants.INIT_ALBUM)
                .addTask(TaskNameConstants.INIT_FU)
                .build();

        project.start().await(3000);

        project.addListener(new OnProjectExecuteListener() {
            @Override
            public void onProjectStart() {
                Log.d(TAG, "Application project start");
            }

            @Override
            public void onTaskFinish(@NotNull String s) {
                Log.d(TAG, "Application project task finish : " + s);
            }

            @Override
            public void onProjectFinish() {
                Log.d(TAG, "Application project finish");
            }
        });

        project.setOnGetMonitorRecordCallback(new OnGetMonitorRecordCallback() {
            @Override
            public void onGetTaskExecuteRecord(@Nullable Map<String, Long> map) {

            }

            @Override
            public void onGetProjectExecuteTime(long l) {

            }
        });

        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
        Log.d(TAG, "Application onCreate end");
//        setSystemFont();
    }


    /**
     * description ：设置系统字体不跟随用户的改变而改变
     * creation date: 2021/4/2
     * user : zhangtongju
     */
    private void setSystemFont() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
    }


    /***
     * 保存错误日志
     */
    public void keepCrash() {
        if ("test".equals(ChannelUtil.getChannel(BaseApplication.getInstance()))) {
            CrashHandler.getInstance().init(this);
        }
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
            return new ClassicsHeader(context).setPrimaryColorId(R.color.black);
        });
    }


    public static BaseApplication getInstance() {
        return baseApp;
    }


}


