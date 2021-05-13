package com.flyingeffects.com.ui.view.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bytedance.applog.AppLog;
import com.bytedance.applog.IOaidObserver;
import com.bytedance.applog.InitConfig;
import com.bytedance.applog.util.UriConfig;
import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.Config;
import com.flyingeffects.com.enity.ConfigForTemplateList;
import com.flyingeffects.com.enity.HomeChoosePageListener;
import com.flyingeffects.com.enity.RequestMessage;
import com.flyingeffects.com.enity.UserInfo;
import com.flyingeffects.com.enity.checkVersion;
import com.flyingeffects.com.enity.messageCount;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.AdManager;
import com.flyingeffects.com.manager.DataCleanManager;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.SPHelper;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.model.ShowPraiseModel;
import com.flyingeffects.com.ui.model.initFaceSdkModel;
import com.flyingeffects.com.ui.view.dialog.CommonMessageDialog;
import com.flyingeffects.com.ui.view.fragment.BackgroundFragment;
import com.flyingeffects.com.ui.view.fragment.DressUpFragment;
import com.flyingeffects.com.ui.view.fragment.FragForTemplate;
import com.flyingeffects.com.ui.view.fragment.frag_user_center;
import com.flyingeffects.com.utils.AssetsUtils;
import com.flyingeffects.com.utils.ChannelUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.NoDoubleClickListener;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.SystemUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.view.NoSlidingViewPager;
import com.githang.statusbar.StatusBarCompat;
import com.glidebitmappool.GlideBitmapPool;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.orhanobut.hawk.Hawk;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.xj.anchortask.library.log.LogUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.nt.lib.analytics.NTAnalytics;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;
import rx.subjects.PublishSubject;

import static com.flyingeffects.com.constans.BaseConstans.getChannel;


/****
 * 修改主界面
 * @author zhang
 */
public class HomeMainActivity extends FragmentActivity {
    private static final String TAG = "HomeMainActivity";

    private static final String[] CATCH_DIRECTORY = {"dynamic", "runCatch", "def", "imageCopy", "faceFolder", "faceMattingFolder",
            "soundFolder", "cacheMattingFolder", "ExtractFrame", "DownVideo", "TextFolder", "toHawei", "downVideoForMusic",
            "downSoundForMusic", "downCutSoundForMusic", "fontStyle", "DressUpFolder","facePP"};

    private final ImageView[] mIvMenuBack = new ImageView[4];
    private final TextView[] tv_main = new TextView[4];
    private final int[] mImBackId = {R.id.iv_back_menu_0, R.id.iv_back_menu_1, R.id.iv_back_menu_2, R.id.iv_back_menu_3};
    public HomeMainActivity ThisMain;
    private final int[] tv_main_button = {R.id.tv_main_0, R.id.tv_main_1, R.id.tv_main_2, R.id.tv_main_3};
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private Timer timer;
    private TimerTask task;
    private TextView message_count;
    private NoSlidingViewPager viewpager_home;

    private Context mContext;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Log.d(TAG, "Application start finished");
        mContext = HomeMainActivity.this;
        setTheme(R.style.AppTheme);
        //禁止休眠
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.act_home_main);
        EventBus.getDefault().register(this);
        message_count = findViewById(R.id.message_count);
        viewpager_home = findViewById(R.id.viewpager_home);
        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#181818"));
        ThisMain = this;
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        clearAllData();
        initView();
        copyFile("default_bj.png");
        GlideBitmapPool.initialize(10 * 1024 * 1024); // 10mb max memory size
        checkUpdate();
        checkConfig();
        getUserPhoneInfo();
        getPushPermission();
        initTiktok();
        if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
            requestCPad();
        }
        if (BaseConstans.hasLogin()) {
            requestUserInfo();
            requestMessageCount();
        }
        initYouMeng();
        statisticsUpgradeApp();
        initFaceSdkModel.initFaceSdk();
        initZt();
        requestConfig();
        setOaid();
    }

    private void setOaid() {
        AppLog.setOaidObserver(new IOaidObserver() {
            @Override
            public void onOaidLoaded(@NotNull final IOaidObserver.Oaid oaid) {
                LogUtils.d(TAG, "oaid = " + oaid.id);
                BaseConstans.setOaid(oaid.id);
            }
        });
    }


    /**
     * 中台
     */
    private void initZt() {
        NTAnalytics.setDebug(false);
        NTAnalytics.init(this, "87", "vQlTNPzHOzBYHzkg", ChannelUtil.getChannel(this));
    }

    private void initYouMeng() {
        UMConfigure.setProcessEvent(true); // 支持在子进程中统计自定义事件
        UMConfigure.setLogEnabled(!BaseConstans.PRODUCTION);
        UMConfigure.init(this, BaseConstans.UMENGAPPID, ChannelUtil.getChannel(this), UMConfigure.DEVICE_TYPE_PHONE, "");
        PlatformConfig.setWeixin("wx7cb3c7ece8461be7", "6eed0ad743c6026b10b7e036f22aa762");
        PlatformConfig.setWXFileProvider("com.flyingeffects.com.fileprovider");
    }


    private void requestUserInfo() {
        HashMap<String, String> params = new HashMap<>();
        params.put("to_user_id", BaseConstans.GetUserId());
        // 启动时间
        Observable ob = Api.getDefault().getOtherUserinfo(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<UserInfo>(this) {
            @Override
            protected void onSubError(String message) {
            }

            @Override
            protected void onSubNext(UserInfo data) {
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("OOM2", "requestUserInfo=" + str);
                Hawk.put("UserInfo", data);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    /**
     * description ：请求插屏广告
     * creation date: 2020/4/24
     * user : zhangtongju
     */
    private void requestCPad() {
        int second = BaseConstans.getInterstitial();
        startTimer(second);
    }

    private void startTimer(int second) {
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
                StatisticsEventAffair.getInstance().setFlag(HomeMainActivity.this, "go_home_start_request_alert_ad" );
                AdManager.getInstance().showCpAd(HomeMainActivity.this, AdConfigs.AD_SCREEN, new AdManager.Callback() {
                    @Override
                    public void adShow() {

                    }

                    @Override
                    public void adClose() {
                        if (ShowPraiseModel.canShowAlert() && !ShowPraiseModel.getHasComment() && !ShowPraiseModel.getIsNewUser() && !ShowPraiseModel.ToDayHasShowAd()) {
                            checkCommentcheck();
                        }
                    }

                    @Override
                    public void onScreenAdShow() {
                        StatisticsEventAffair.getInstance().setFlag(HomeMainActivity.this, "go_home_start_request_alert_ad_show" );
                    }

                    @Override
                    public void onScreenAdError() {
                        StatisticsEventAffair.getInstance().setFlag(HomeMainActivity.this, "go_home_start_request_alert_ad_error" );

                    }
                });
                destroyTimer();
            }
        };
        timer.schedule(task, second * 1000, second * 1000);
    }


    /**
     * description ：检查是否可以好评弹窗
     * date: ：2019/6/13 10:44
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public void checkCommentcheck() {
        HashMap<String, String> params = new HashMap<>();
        Observable ob = Api.getDefault().commentcheck(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(this) {
            @Override
            protected void onSubError(String message) {
                LogUtil.d("checkUpdate", message);
            }

            @Override
            protected void onSubNext(Object data) {
                String str = StringUtil.beanToJSONString(data);
                try {
                    JSONObject ob = new JSONObject(str);
                    int is_open_comment = ob.getInt("is_open_comment");
                    if (is_open_comment == 1) {
                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(HomeMainActivity.this, PraiseActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }, 3000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, "checkUpdate", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
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


    private void initTiktok() {
        final InitConfig config = new InitConfig("181569", getChannel());
        config.setUriConfig(UriConfig.DEFAULT);
        // 是否在控制台输出日志，可用于观察用户行为日志上报情况，建议仅在调试时使用，release版本请设置为false ！
        AppLog.setEnableLog(false);
        AppLog.init(this, config);
    }


    public void getUserPhoneInfo() {
        OneKeyLoginManager.getInstance().getPhoneInfo((code, result) -> {
            //预取号回调
            Log.e("VVV", "预取号： code==" + code + "   result==" + result);
        });
    }


    public void copyFile(String name) {
        FileManager manager = new FileManager();
        String dir = manager.getFileCachePath(this, "");
        File file = new File(dir, name);
        if (!file.exists()) {
            AssetsUtils.copyFileFromAssets(this, name, file.getPath());
        }
    }


    /**
     * description ：检查更新
     * date: ：2019/6/13 10:44
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public void checkUpdate() {
        HashMap<String, String> params = new HashMap<>();
        params.put("config_name", "android_version_ad");
        params.put("channel", getChannel());
        Observable ob = Api.getDefault().checkUpdate(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<checkVersion>(this) {
            @Override
            protected void onSubError(String message) {
                LogUtil.d("checkUpdate", message);
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(checkVersion data) {
                LogUtil.d("checkUpdate", StringUtil.beanToJSONString(data));
                try {
                    if (data != null) {
                        String uploadVersion = data.getNewversion();
                        String content = data.getContent();
                        int uVersion = Integer.parseInt(uploadVersion);
                        int nowVersion = Integer.parseInt(BaseConstans.getVersionCode());
                        if (uVersion > nowVersion) {
                            intoCheckUpdateAct(data.getDownloadfile(), data.getIs_forceupdate(), content);
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }, "checkUpdate", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    private void getPushPermission() {
        SPHelper spUtil = new SPHelper(this, "fileName");
        boolean isFirst = spUtil.getBoolean("isFirst", true);
        if (isFirst) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.permission_alert)
                    .setMessage(R.string.permission_content)
                    .setPositiveButton(getString(R.string.toGetPermission), (dialog, which) -> goToSetting())
                    .show();
            spUtil.putBoolean("isFirst", false);
        }
    }


    private void goToSetting() {
        try {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= 26) {// android 8.0引导
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
            } else if (Build.VERSION.SDK_INT >= 21) { // android 5.0-7.0
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("app_package", getPackageName());
                intent.putExtra("app_uid", getApplicationInfo().uid);
            } else {//其它
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", getPackageName(), null));
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            LogUtil.d("OOM", "锤子手机这里会闪退");
        }

    }


    /**
     * user :TongJu  ;描述：跳转到更新界面
     * 时间：2018/5/29
     **/
    private void intoCheckUpdateAct(String url, String is_must_update, String content) {
        Intent intent = new Intent(this, UpdateApkActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("url", url);
        intent.putExtra("content", content);
        intent.putExtra("policy", "0");
        intent.putExtra("is_must_update", is_must_update);
        startActivity(intent);
    }


    public void initView() {
        for (int i = 0; i < mIvMenuBack.length; i++) {
            mIvMenuBack[i] = findViewById(mImBackId[i]);
            tv_main[i] = findViewById(tv_main_button[i]);
            mIvMenuBack[i].setOnClickListener(listener);
        }
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new BackgroundFragment());
        fragments.add(new FragForTemplate());
        fragments.add(new DressUpFragment());
        menu3F = new frag_user_center();
        fragments.add(menu3F);
        home_vp_frg_adapter adapter = new home_vp_frg_adapter(getSupportFragmentManager(), fragments);
        viewpager_home.setAdapter(adapter);
        viewpager_home.setOffscreenPageLimit(1);
        whichMenuSelect(1);
        findViewById(R.id.iv_main_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BaseConstans.hasLogin()) {
                    Intent intent = new Intent(HomeMainActivity.this, FUBeautyActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    StatisticsEventAffair.getInstance().setFlag(mContext, "12_Shoot");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(HomeMainActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            }
        });
        StatisticsEventAffair.getInstance().setFlag(HomeMainActivity.this, "14_home_tab_click", "默认页面不纳入统计");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    private void clearAllData() {
        //清除外部cache下的内容
        DataCleanManager.cleanExternalCache();
        //清理内部cache
        DataCleanManager.cleanInternalCache(BaseApplication.getInstance());
        //清理内部sdk
        DataCleanManager.cleanFiles(BaseApplication.getInstance());

        LanSongFileUtil.deleteDefaultDir();
        //清理外部sdk
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            for (String s : CATCH_DIRECTORY) {
                DataCleanManager.deleteFilesByDirectory(getExternalFilesDir(s));
            }
        }
    }


    private final NoDoubleClickListener listener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back_menu_0:
                    whichMenuSelect(0);

                    StatisticsEventAffair.getInstance().setFlag(HomeMainActivity.this, "14_home_tab_click", "1");
                    StatisticsEventAffair.getInstance().setFlag(HomeMainActivity.this, "5_bj");
                    break;
                case R.id.iv_back_menu_1:
                    StatisticsEventAffair.getInstance().setFlag(HomeMainActivity.this, "14_home_tab_click", "2");

                    whichMenuSelect(1);
                    break;
                case R.id.iv_back_menu_2:
                    whichMenuSelect(2);
                    StatisticsEventAffair.getInstance().setFlag(HomeMainActivity.this, "14_home_tab_click", "3");

                    StatisticsEventAffair.getInstance().setFlag(HomeMainActivity.this, "12_news");
                    break;
                case R.id.iv_back_menu_3:
                    StatisticsEventAffair.getInstance().setFlag(HomeMainActivity.this, "14_home_tab_click", "4");

                    StatisticsEventAffair.getInstance().setFlag(HomeMainActivity.this, "3_mine");
                    whichMenuSelect(3);
                    break;
                default:
                    break;
            }
        }
    };

    private void setStatusBar() {
        changeBottomTab();
    }


    /**
     * user :TongJu  ;描述：底部栏改变
     * 时间：2018/6/6
     **/
    private void changeBottomTab() {
        for (int i = 0; i < mIvMenuBack.length; i++) {
            tv_main[i].setTextColor(ContextCompat.getColor(this, R.color.white));
        }
        tv_main[LastWhichMenu].setTextColor(ContextCompat.getColor(this, R.color.new_base_blue));
    }

    /**
     * 记录当前页面id
     */
    private int LastWhichMenu = -1;

    public void whichMenuSelect(int whichMenu) {
        this.LastWhichMenu = whichMenu;
        openMenu(whichMenu);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        if (BaseConstans.hasLogin()) {
            if (!BaseApplication.getInstance().isBackHome) {
                requestMessageCount();
                BaseApplication.getInstance().isBackHome = true;
            }
        } else {
            message_count.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private frag_user_center menu3F = null;

    private void openMenu(int which) {
        setStatusBar();
        viewpager_home.setCurrentItem(which, false);
        EventBus.getDefault().post(new RequestMessage());
        EventBus.getDefault().post(new HomeChoosePageListener(which));
    }


    private long exitTime = 0;


    private void exitPressAgain() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(ThisMain, "再点一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        //exitPressAgain();
        showBackMessage();
    }


    private void showBackMessage() {
        StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "load_quit_app");
        CommonMessageDialog dialog = CommonMessageDialog.getBuilder(mContext)
                .setAdStatus(CommonMessageDialog.AD_STATUS_MIDDLE)
                .setAdId(AdConfigs.AD_IMAGE_EXIT)
                .setPositiveButton("狠心退出")
                .setNegativeButton("关闭")
                .setDialogBtnClickListener(new CommonMessageDialog.DialogBtnClickListener() {
                    @Override
                    public void onPositiveBtnClick(CommonMessageDialog dialog) {
                        dialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onCancelBtnClick(CommonMessageDialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setDialogDismissListener(new CommonMessageDialog.DialogDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                })
                .build();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 获取到Activity下的Fragment
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        // 查找在Fragment中onRequestPermissionsResult方法并调用
        for (Fragment fragment : fragments) {
            if (fragment != null) {
                // 这里就会调用我们Fragment中的onRequestPermissionsResult方法
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }


    /**
     * 检查推广配置是否ok
     */
    private void checkConfig() {
        if (BaseConstans.configList == null) {
            BaseConstans.configList = new ConfigForTemplateList();
            BaseConstans.configList.setContent("已为您复制微信号");
            BaseConstans.configList.setType(1);
            BaseConstans.configList.setCopydata("wordcq520");
            BaseConstans.configList.setDescription("加微信领取100套精美背景素材");
            BaseConstans.configList.setSecondline("领取100套精美素材");
            requestConfigForTemplateList();
        }
    }


    private void requestConfigForTemplateList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("config_name", "wechat_name");
        // 启动时间
        Observable ob = Api.getDefault().configListForTemplateList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<ConfigForTemplateList>(HomeMainActivity.this) {
            @Override
            protected void onSubError(String message) {
            }

            @Override
            protected void onSubNext(ConfigForTemplateList data) {
                if (data != null) {
                    BaseConstans.configList = data;
                }
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    /**
     * description ：请求粉丝数，赞和评论数量
     * creation date: 2020/7/29
     * user : zhangtongju
     */
    private void requestMessageCount() {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", BaseConstans.GetUserId());
        Observable ob = Api.getDefault().getAllMessageNum(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<messageCount>(this) {
                    @Override
                    protected void onSubError(String message) {
                        ToastUtil.showToast(message);
                    }

                    @Override
                    protected void onSubNext(messageCount data) {
                        if (message_count != null) {
                            String allCount = data.getAll_num();
                            int intAllCount = Integer.parseInt(allCount);
                            if (intAllCount == 0) {
                                message_count.setVisibility(View.GONE);
                            } else {
                                message_count.setVisibility(View.VISIBLE);
                                message_count.setText(intAllCount + "");
                            }
                        }
                    }
                }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject,
                false, true, false);
    }


    @Subscribe
    public void onEventMainThread(RequestMessage event) {
        if (BaseConstans.hasLogin()) {
            requestMessageCount();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (menu3F != null) {
            menu3F.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * description ：统计
     * creation date: 2020/12/10
     * user : zhangtongju
     */
    private void statisticsUpgradeApp() {
        String appCode = SystemUtil.getVersionCode(this);
        String lastCode = Hawk.get("lastAppCode");
        if (TextUtils.isEmpty(lastCode) || !lastCode.equals(appCode)) {
            if (!DoubleClick.getInstance().isFastDoubleClick()) {
                statisticsPhoneInfo();
            }
        }
    }

    /**
     * description ：统计手机信息
     * creation date: 2020/12/10
     * user : zhangtongju
     */
    private void statisticsPhoneInfo() {
        HashMap<String, String> params = new HashMap<>();
        params.put("ip", SystemUtil.getIPAddress(this));
        // 启动时间
        Observable ob = Api.getDefault().add_active(BaseConstans.getRequestHead(params));
        LogUtil.d("OOM", "用户ip=" + StringUtil.beanToJSONString(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(HomeMainActivity.this) {

            @Override
            protected void onSubError(String message) {

            }

            @Override
            protected void onSubNext(Object data) {

            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    /**
     * description ：这个配置是请求关于界面的联系我们
     * creation date: 2020/4/8
     * user : zhangtongju
     */
    private void requestConfig() {
        HashMap<String, String> params = new HashMap<>();
        // 启动时间
        Observable ob = Api.getDefault().configList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<Config>>(HomeMainActivity.this) {
            @Override
            protected void onSubError(String message) {
            }

            @Override
            protected void onSubNext(List<Config> data) {

                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < data.size(); i++) {
                    String tet = StringUtil.beanToJSONString(data.get(i));
                    sb.append(tet);
                    LogUtil.d("_onNext", "i-" + i + "Config=" + tet);
                }
                LogUtil.d("_onNext", "str=" + sb.toString());

                if (data.size() > 0) {
                    for (int i = 0; i < data.size(); i++) {
                        Config config = data.get(i);
                        int id = config.getId();
                        if (id == 18) {
                            //弹出微信
                            BaseConstans.service_wxi = config.getValue();
                        } else if (id == 20) {
                            //android 审核数据
                            String AuditModeJson = config.getValue();
                            auditModeConfig(AuditModeJson);
                        } else if (id == 22) {
                            //获得热更新时长
                            String outTime = config.getValue();
                            BaseConstans.showAgainKaipingAd = Integer.parseInt(outTime);
                        } else if (id == 24) {
                            //首次安装前几次无广告
                            int newUserIsVip = Integer.parseInt(config.getValue());
                            LogUtil.d("OOM2", "newUserIsVip=" + newUserIsVip);
                            if (BaseConstans.getOpenAppNum() < newUserIsVip - 1) {
                                BaseConstans.setNextNewUser(true);
                            } else {
                                BaseConstans.setNextNewUser(false);
                            }
                            if (BaseConstans.getOpenAppNum() < newUserIsVip) { //新用户没广告
                                BaseConstans.setIsNewUser(true);
                            } else {
                                BaseConstans.setIsNewUser(false);
                            }
                        } else if (id == 25) {
                            //启动APP多少秒后显示插屏广告
                            int second = Integer.parseInt(config.getValue());
                            BaseConstans.setInterstitial(second);
                        } else if (id == 26) {
                            //开屏广告延迟时间
                            int second = Integer.parseInt(config.getValue());
                            BaseConstans.setKaiPingADTimeOut(second);
                        } else if (id == 27) {
                            //上传的时候
                            int second = Integer.parseInt(config.getValue());
                            BaseConstans.setMaxuploadTime(second);
                        } else if (id == 32) {
                            String second = config.getValue();
                            BaseConstans.setminapp_share_title(second);
                        } else if (id == 33) {
                            //feed 自渲染信息流 上线和下限
                            String second = config.getValue();
                            BaseConstans.setFeedShowPositionNum(second);
                        } else if (id == 53) {
                            //相册加载广告间隔次数
                            String albumADIntervalNumber = config.getValue();
                            BaseConstans.setIntervalNumShowAD(Integer.parseInt(albumADIntervalNumber));
                        } else if (id == 56) {
                            //自定义模板分享到抖音的话题
                            String douyingTopic = config.getValue();
                            BaseConstans.setDouyingTopic(douyingTopic);
                        } else if (id == 61) {
                            //换装制作页面切换模板按钮加载视频广告的间隔次数
                            int dressupIntervalsNumber = Integer.parseInt(config.getValue());
                            BaseConstans.setDressupIntervalsNumber(dressupIntervalsNumber);
                        } else if (id == 72) {
                            String value = config.getValue();
                            BaseConstans.setHasAdEntrance(value);
                        } else if (id == 73) {
                            String value = config.getValue();
                            BaseConstans.setGifCourse(value);
                        } else if (id == 74) {
                            String video_error_can_save = config.getValue();
                            //1 表示能保存 0 表示不能保存
                            LogUtil.d("OOM3", "video_error_can_save=" + video_error_can_save);
                            BaseConstans.setAdShowErrorCanSave(video_error_can_save);
                        }else if(id==75){
                            BaseConstans.setCreateVideoShowAdUserNum(config.getValue());

                        }
                    }
                }
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    private void auditModeConfig(String str) {
        LogUtil.d("AuditModeConfig", "AuditModeConfig=" + str);
        Hawk.put("AuditModeConfig", str);
        int isVideoadvertisingId = 0;
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(str);
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obArray = jsonArray.getJSONObject(i);
                    String channel = obArray.getString("channel");
                    if ("isVideoadvertising".equals(channel)) { //控制了版本号
                        isVideoadvertisingId = obArray.getInt("id");
                    }
                    if (channel.equals(BaseConstans.getChannel())) { //最新版的审核模式
                        boolean auditOn = obArray.getBoolean("audit_on");
                        int nowVersion = Integer.parseInt(BaseConstans.getVersionCode());
                        if (auditOn || nowVersion != isVideoadvertisingId) {
                            BaseConstans.setHasAdvertising(1);
                        } else {
                            BaseConstans.setHasAdvertising(0);
                        }

                        boolean video_ad_open = obArray.getBoolean("video_ad_open");
                        BaseConstans.setIncentiveVideo(video_ad_open);

                        boolean save_video_ad = obArray.getBoolean("save_video_ad");
                        BaseConstans.setSave_video_ad(save_video_ad);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
