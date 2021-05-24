package com.flyingeffects.com.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.anchortask.AnchorTaskCreator;
import com.flyingeffects.com.base.anchortask.TaskNameConstants;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.entity.AplicationInitRetroposition;
import com.flyingeffects.com.entity.isIntoBackground;
import com.flyingeffects.com.ui.view.activity.WelcomeActivity;
import com.flyingeffects.com.utils.ChannelUtil;
import com.flyingeffects.com.utils.CrashHandler;
import com.flyingeffects.com.utils.DateUtils;
import com.flyingeffects.com.utils.LogUtil;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.umeng.commonsdk.UMConfigure;
import com.xj.anchortask.library.AnchorProject;
import com.xj.anchortask.library.OnProjectExecuteListener;
import com.xj.anchortask.library.log.LogUtils;
import com.xj.anchortask.library.monitor.OnGetMonitorRecordCallback;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
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
        baseApp = this;
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
        //准备初始化，不进行任何网络请求
        UMConfigure.preInit(BaseApplication.getInstance(),
                BaseConstans.UMENGAPPID, ChannelUtil.getChannel(BaseApplication.getInstance()));
        EventBus.getDefault().register(this);
    }



    @Subscribe
    public void onEventMainThread(AplicationInitRetroposition cutSuccess) {
        initAllSdk();
    }


    /**
     * description ：工信部要求，在接受我们协议前，不能进行任何初始化的情况，所以初始化需要后移
     * creation date: 2021/5/19
     * user : zhangtongju
     */
    private void initAllSdk(){
        @NotNull AnchorProject project = new AnchorProject.Builder().setContext(this)
                .setLogLevel(LogUtils.LogLevel.DEBUG)
                .setAnchorTaskCreator(new AnchorTaskCreator())
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
                .addTask(TaskNameConstants.INIT_KUAI_SHOU_MONITOR)
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
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) ->
                new ClassicsHeader(context).setPrimaryColorId(R.color.black));
    }


    public static BaseApplication getInstance() {
        return baseApp;
    }









}


