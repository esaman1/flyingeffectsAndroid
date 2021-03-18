package com.flyingeffects.com.ui.view.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.bigkoo.convenientbanner.utils.ScreenUtil;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.databinding.ActWelcomeBinding;
import com.flyingeffects.com.enity.Config;
import com.flyingeffects.com.enity.ConfigForTemplateList;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.nineton.ntadsdk.NTAdSDK;
import com.nineton.ntadsdk.itr.SplashAdCallBack;
import com.orhanobut.hawk.Hawk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;

import rx.Observable;

public class WelcomeActivity extends BaseActivity {

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

        LogUtil.d("OOM", "WelcomeActivity");
        BaseConstans.setOddNum();
        //解决广告bug ,点击图标后广告爆款广告不弹出来
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, //去掉状态栏
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        fromBackstage = getIntent().getBooleanExtra("fromBackstage", false);
        if (!isTaskRoot() && !fromBackstage) {
            finish();
            return;
        }
//        if (!fromBackstage) {
        //记录是不是新用户
        if (BaseConstans.isFirstOpenApp()) {
            BaseConstans.setFirstOpenApp(System.currentTimeMillis()); //记录第一次打开app的时间
            BaseConstans.setOpenAppNum(1); //打开app的次数为1
        } else {
            int openAppNum = BaseConstans.getOpenAppNum();
            openAppNum++;
            BaseConstans.setOpenAppNum(openAppNum); //打开app的次数为1
            LogUtil.d("OOM", "openAppNum=" + openAppNum);
        }
//        }

        gotoPrivacyPolicyActivity();
    }

    @Override
    protected void initAction() {
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
                requestConfig();
                requestConfigForTemplateList();
            }
            hasPermission = true;

            LogUtil.d("oom", "BaseConstans.getNextIsNewUser()=" + BaseConstans.getNextIsNewUser());

            if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getNextIsNewUser()) {
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
            gotoNext();
        } else {
            hasPermission = false;
            // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
            ToastUtil.showToast(this.getString(R.string.Permissions_repulse));
            new Handler().postDelayed(() -> {
                gotoNext();
//                PermissionUtil.gotoPermission(WelcomeActivity.this);
//                finish();
            }, 3000);
        }
    }




    /**
     * description ：显示开屏广告还是进入主页 ,没有权限也能请求
     * creation date: 2021/3/10
     * user : zhangtongju
     */
    private void  gotoNext(){
        if (!fromBackstage) {
            requestConfig();
        }
        if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
            showSplashAd();
        } else {
            intoMain();
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
            } else {
                hasPermission = true;
                if (!fromBackstage || BaseConstans.getNextIsNewUser()) {
                    requestConfig();
                }
                LogUtil.d("oom", "BaseConstans.getHasAdvertising()=" + BaseConstans.getHasAdvertising());
                if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getNextIsNewUser()) {
                    showSplashAd();
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

        LogUtil.d("oom", "开屏广告的时长为" + BaseConstans.getKaiPingADTimeOut());
    }


    /**
     * description ：调整到隐私政策页面
     * date: ：2019/11/12 15:06
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    private void gotoPrivacyPolicyActivity() {
        if (BaseConstans.isFirstClickUseApp()) {
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
            } else {
                this.finish();
            }
        }
    }


    /**
     * 展示开屏广告
     */
    private void showSplashAd() {
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
     * description ：这个配置是请求关于界面的联系我们
     * creation date: 2020/4/8
     * user : zhangtongju
     */
    private void requestConfig() {
        HashMap<String, String> params = new HashMap<>();
        // 启动时间
        Observable ob = Api.getDefault().configList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<Config>>(WelcomeActivity.this) {
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
                        }else if(id == 72){
                            String value = config.getValue();
                            BaseConstans.setHasAdEntrance(value);
                        }
                    }
                }
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


    int isVideoadvertisingId;

    private void auditModeConfig(String str) {
        LogUtil.d("AuditModeConfig", "AuditModeConfig=" + str);
        Hawk.put("AuditModeConfig", str);
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(str);
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obArray = jsonArray.getJSONObject(i);
                    String Channel = obArray.getString("channel");
                    if ("isVideoadvertising".equals(Channel)) { //控制了版本号
                        isVideoadvertisingId = obArray.getInt("id");
                    }
                    if (Channel.equals(BaseConstans.getChannel())) { //最新版的审核模式
                        boolean audit_on = obArray.getBoolean("audit_on");
                        int NowVersion = Integer.parseInt(BaseConstans.getVersionCode());
                        if (audit_on || NowVersion != isVideoadvertisingId) {
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
