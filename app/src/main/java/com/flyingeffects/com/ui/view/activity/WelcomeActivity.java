package com.flyingeffects.com.ui.view.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.Config;
import com.flyingeffects.com.enity.ConfigForTemplateList;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.utils.PermissionUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.nineton.ntadsdk.NTAdSDK;
import com.nineton.ntadsdk.itr.SplashAdCallBack;
import com.nineton.ntadsdk.utils.ScreenUtils;
import com.nineton.ntadsdk.view.NTSkipView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Observable;

public class WelcomeActivity extends BaseActivity {

    private final int BUILD_VERSION = 23;
    private final int PERMISSION_REQUEST_CODE = 1024;
    private static final int RESULT_CODE = 3;

    @BindView(R.id.rl_ad_container)
    RelativeLayout rlAdContainer;

    private NTSkipView tvSkip;

    private boolean isShow = false;

    /**
     * 是否来自后台进入
     */
    boolean fromBackstage = false;

    public boolean canJump = false;

    private boolean hasPermission = false;

    @Override
    protected int getLayoutId() {
        return R.layout.act_welcome;
    }

    @Override
    protected void initView() {
        //解决广告bug ,点击图标后广告爆款广告不弹出来
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, //去掉状态栏
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        fromBackstage = getIntent().getBooleanExtra("fromBackstage", false);
        if (!isTaskRoot() && !fromBackstage) {
            finish();
            return;
        }
        tvSkip = findViewById(R.id.tv_skip);
        rlAdContainer = findViewById(R.id.rl_ad_container);
        gotoPrivacyPolicyActivity();
    }

    @Override
    protected void initAction() {
        requestConfigForTemplateList();
        requestConfig();
    }


    /**
     * 权限检测
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        List<String> lackedPermission = new ArrayList<String>();
        if (!(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }




        // 权限都已经有了，那么直接调用SDK
        if (lackedPermission.size() == 0) {
            if (!fromBackstage) {
                requestConfig();
                requestConfigForTemplateList();
            }
            hasPermission = true;
            if (BaseConstans.getHasAdvertising() == 1) {
                showSplashAd();
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
            if (!fromBackstage) {
                requestConfig();
            }
            if (BaseConstans.getHasAdvertising() == 1) {
                showSplashAd();
            } else {
                intoMain();
            }
        } else {
            hasPermission = false;
            // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
            ToastUtil.showToast(this.getString(R.string.Permissions_repulse));
            new Handler().postDelayed(() -> {
                PermissionUtil.gotoPermission(WelcomeActivity.this);
                finish();
            }, 3000);

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
        rlAdContainer.post(() -> {
            if (Build.VERSION.SDK_INT >= BUILD_VERSION) {
                checkPermission();
            } else {
                hasPermission = true;
                if (!fromBackstage) {
                    requestConfig();
                }
                if (BaseConstans.getHasAdvertising() == 1) {
                    showSplashAd();
                }
            }
        });
        rlAdContainer.postDelayed(() -> {
            if (!isShow && hasPermission) {
                intoMain();
                finish();
                overridePendingTransition(R.anim.nt_ad_fade_in, R.anim.nt_ad_fade_out);
            }
        }, 5000);


    }


    /**
     * description ：调整到隐私政策页面
     * date: ：2019/11/12 15:06
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    private void gotoPrivacyPolicyActivity() {
        if (BaseConstans.isFirstClickUseApp()) {
            Intent intent = new Intent(this, privacyPolicyActivity.class);
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
            } else {
                this.finish();
            }
        }
    }


    /**
     * 展示开屏广告
     */
    private void showSplashAd() {

        int sloganHeight = (ScreenUtils.getScreenWidth(NTAdSDK.getAppContext()) * 250 / 720);
        NTAdSDK.getInstance().showSplashAd(this, rlAdContainer, tvSkip, sloganHeight, AdConfigs.AD_SPLASH, new SplashAdCallBack() {
            @Override
            public void onAdSuccess() {
                isShow = true;
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
                tvSkip.setText(String.format("跳过 %d", Math.round(millisUntilFinished / 1000f)));
            }

            @Override
            public void onAdDismissed() {
                next();
            }
        });


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
     * description ：这个配置是请求关于界面的联系我们
     * creation date: 2020/4/8
     * user : zhangtongju
     */
    private void requestConfig() {
        HashMap<String, String> params = new HashMap<>();
        params.put("config_name", "wechat_name|hot_restart");
        // 启动时间
        Observable ob = Api.getDefault().configList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Config>(WelcomeActivity.this) {
            @Override
            protected void _onError(String message) {
            }

            @Override
            protected void _onNext(Config data) {
                BaseConstans.service_wxi = data.getValue();
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
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
            protected void _onError(String message) {
            }

            @Override
            protected void _onNext(ConfigForTemplateList data) {
                if (data != null) {
                    BaseConstans.configList = data;
                }

            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


}
