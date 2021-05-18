package com.flyingeffects.com.ui.view.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.bigkoo.convenientbanner.utils.ScreenUtil;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.databinding.ActWelcomeBinding;
import com.flyingeffects.com.enity.ConfigForTemplateList;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.TimeUtils;
import com.flyingeffects.com.utils.ToastUtil;
import com.kwai.monitor.log.TurboAgent;
import com.nineton.ntadsdk.NTAdSDK;
import com.nineton.ntadsdk.itr.SplashAdCallBack;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import rx.Observable;

public class WelcomeActivity extends BaseActivity {
    private static final String TAG = "WelcomeActivity";
    private final int BUILD_VERSION = 23;
    private final int PERMISSION_REQUEST_CODE = 1024;
    private static final int RESULT_CODE = 3;

    private boolean isShow = false;

    /**
     * 是否来自后台进入
     */
    boolean fromBackstage = false;

    public boolean canJump = false;

    private boolean hasPermission = false;
    private ActWelcomeBinding mBinding;


    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {
        mBinding = ActWelcomeBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        setContentView(rootView);
        //快手集成sdk 应用活跃事件
        TurboAgent.onAppActive();
        checkNextDayStay();
        //解决广告bug ,点击图标后广告爆款广告不弹出来
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, //去掉状态栏
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        fromBackstage = getIntent().getBooleanExtra("fromBackstage", false);
        if (!isTaskRoot() && !fromBackstage) {
            finish();
            return;
        }
        if (BaseConstans.isFirstOpenApp()) {
            BaseConstans.setFirstOpenApp(System.currentTimeMillis()); //记录第一次打开app的时间
            BaseConstans.setOpenAppNum(1); //打开app的次数为1
        } else {
            int openAppNum = BaseConstans.getOpenAppNum();
            openAppNum++;
            BaseConstans.setOpenAppNum(openAppNum); //打开app的次数为1
        }
        gotoPrivacyPolicyActivity();
        StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "test_ad_into_coopen");
    }

    @Override
    protected void initAction() {
    }

    /**
     * 快手 记录次日留存和7日留存
     */
    private void checkNextDayStay() {
        Date date = new Date();
        if (BaseConstans.getFirstUseAppTime() != 0) {
            long t1 = TimeUtils.millis2Days(date.getTime(), TimeZone.getDefault());
            long t2 = TimeUtils.millis2Days(BaseConstans.getFirstUseAppTime(), TimeZone.getDefault());

            if (t1 - t2 == 1) {
                TurboAgent.onNextDayStay();
            }
//            else if (t1 - t2 == 6) {
//                TurboAgent.onWeekStay();
//            }
        }
    }

    /**
     * 权限检测
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        List<String> lackedPermission = new ArrayList<>();
        if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        // 权限都已经有了，那么直接调用SDK
        if (lackedPermission.size() == 0) {
            if (!fromBackstage || BaseConstans.getNextIsNewUser()) {
//                requestConfig();
                requestConfigForTemplateList();
            }
            hasPermission = true;
            if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getNextIsNewUser()) {
                StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "test_ad_into_checkPermiss_6_requestAd");
                showSplashAd();
            } else {
                noQueryAdReason();
                StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "test_ad_into_checkPermiss_6_no_requestAd");
            }
        } else {
            hasPermission = false;
            // 请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            requestPermissions(requestPermissions, PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            hasPermission = true;
            gotoNext();
        } else {
            hasPermission = false;
            // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
            ToastUtil.showToast(this.getString(R.string.Permissions_repulse));
            new Handler().postDelayed(() -> {
                gotoNext();
            }, 0);
        }
    }

    /**
     * description ：显示开屏广告还是进入主页 ,没有权限也能请求
     * creation date: 2021/3/10
     * user : zhangtongju
     */
    private void gotoNext() {
        if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
            StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "test_ad_into_requestPermiss_6_requestAd");
            showSplashAd();
        } else {
            noQueryAdReason();
            StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "test_ad_into_requestPermiss_6_no_requestAd");
            intoMain();
        }
    }


    /**
     * description ：没请求广告的原因
     * creation date: 2021/5/11
     * user : zhangtongju
     */
    private void noQueryAdReason() {
        if (BaseConstans.getHasAdvertising() != 1) {
            StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "start_not_request_ad", "后台没配置广告");
        }
        if (BaseConstans.getIsNewUser()) {
            StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "start_not_request_ad", "新用户");
        }
    }


    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }


    private void getPermission() {
        mBinding.rlAdContainer.post(() -> {
            if (Build.VERSION.SDK_INT >= BUILD_VERSION) {

                checkPermission();
                StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "test_ad_into_checkPermiss_6");

            } else {

                hasPermission = true;
                StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "test_ad_into_checkPermiss_less_6");

                if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getNextIsNewUser()) {
                    StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "test_ad_into_checkPermiss_less_6_requestAd");
                    showSplashAd();
                } else {
                    noQueryAdReason();
                    StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "test_ad_into_checkPermiss_less_6_no_requestAd");
                }

            }
        });

        mBinding.rlAdContainer.postDelayed(() -> {
            if (!isShow && hasPermission) {
                intoMain();
                finish();
                overridePendingTransition(R.anim.nt_ad_fade_in, R.anim.nt_ad_fade_out);
            }
        }, BaseConstans.getKaiPingADTimeOut());
    }


    /**
     * description ：调整到隐私政策页面
     * date: ：2019/11/12 15:06
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    private void gotoPrivacyPolicyActivity() {
        if (BaseConstans.isFirstClickUseApp()) {
            StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "test_ad_into_privacy_policy");
            Intent intent = new Intent(this, PrivacyPolicyActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, 1);
        } else {
            getPermission();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_CODE) {
            boolean agree = data.getBooleanExtra("agree", true);
            if (agree) {
                BaseConstans.setFirstClickUseApp();
                getPermission();
                Date date = new Date();
                BaseConstans.setFirstUseAppTime(date.getTime());
            } else {
                this.finish();
            }
        }
    }


    /**
     * 展示开屏广告
     */
    private void showSplashAd() {
        Log.d(TAG, "Application start finished");
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "start_ad_request");
            NTAdSDK.getInstance().showSplashAd(this, mBinding.rlAdContainer, mBinding.tvSkip, ScreenUtil.dip2px(this, 0), AdConfigs.AD_SPLASH, new SplashAdCallBack() {

                @Override
                public void onAdSuccess() {
                    isShow = true;
                    StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "start_ad_request_success");
                }

                @Override
                public void onAdError(String errorMsg) {
                    isShow = false;
                    intoMain();
                    finish();
                }

                @Override
                public boolean onAdClicked(String title, String url, boolean isNtAd, boolean openURLInSystemBrowser) {
                    return false;
                }

                @Override
                public void onAdTick(long millisUntilFinished) {
                    mBinding.tvSkip.setText(String.format("跳过 %d", Math.round(millisUntilFinished / 1000f)));
                }

                @Override
                public void onAdDismissed() {
                    next();
                }
            });

        }
    }

    /**
     * 设置一个变量来控制当前开屏页面是否可以跳转，当开屏广告为普链类广告时，点击会打开一个广告落地页，此时开发者还不能打开自己的App主页。当从广告落地页返回以后，
     * 才可以跳转到开发者自己的App主页；当开屏广告是App类广告时只会下载App。
     */
    private void next() {
        if (canJump) {
            intoMain();
            this.finish();
            this.overridePendingTransition(R.anim.nt_ad_fade_in, R.anim.nt_ad_fade_out);
        } else {
            canJump = true;
        }
    }

    boolean isIntoMain = false;

    private void intoMain() {
        if (fromBackstage) {
            this.finish();
        } else {
            if (!isIntoMain) {
                Intent intent = new Intent(this, HomeMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                isIntoMain = true;
                this.finish();
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        canJump = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJump) {
            next();
        }
        canJump = true;
    }

    /**
     * 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * description ：这个请求是用来请求模板里面的数据，复制快手或者抖音
     * creation date: 2020/4/8
     * user : zhangtongju
     */
    private void requestConfigForTemplateList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("config_name", "wechat_name");
        // 启动时间
        Observable ob = Api.getDefault().configListForTemplateList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<ConfigForTemplateList>(WelcomeActivity.this) {

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


}
