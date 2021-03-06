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
import com.flyingeffects.com.entity.Config;
import com.flyingeffects.com.entity.ConfigForTemplateList;
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
import com.nineton.ntadsdk.itr.SplashAdCallBack;
import com.nineton.ntadsdk.manager.SplashAdManager;
import com.orhanobut.hawk.Hawk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public boolean canJump = false;
    private boolean hasPermission = false;
    private ActWelcomeBinding mBinding;
    /**
     * ????????????????????????
     */
    boolean fromBackstage = false;


    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {
        LogUtil.d("OOM","?????????WelcomeActivity");
        mBinding = ActWelcomeBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        setContentView(rootView);
        //????????????sdk ??????????????????
        TurboAgent.onAppActive();
        checkNextDayStay();
        //????????????bug ,?????????????????????????????????????????????
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, //???????????????
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        fromBackstage = getIntent().getBooleanExtra("fromBackstage", false);
        if (!isTaskRoot() && !fromBackstage) {
            finish();
            return;
        }
        if (BaseConstans.isFirstOpenApp()) {
            BaseConstans.setFirstOpenApp(System.currentTimeMillis()); //?????????????????????app?????????
            BaseConstans.setOpenAppNum(1); //??????app????????????1
        } else {
            int openAppNum = BaseConstans.getOpenAppNum();
            openAppNum++;
            BaseConstans.setOpenAppNum(openAppNum); //??????app????????????1
        }
        gotoPrivacyPolicyActivity();
        StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "test_ad_into_coopen");
    }

    @Override
    protected void initAction() {
    }

    /**
     * ?????? ?????????????????????7?????????
     */
    private void checkNextDayStay() {
        Date date = new Date();
        if (BaseConstans.getFirstUseAppTime() != 0) {
            long t1 = TimeUtils.millis2Days(date.getTime(), TimeZone.getDefault());
            long t2 = TimeUtils.millis2Days(BaseConstans.getFirstUseAppTime(), TimeZone.getDefault());
            if (t1 - t2 == 1) {
                TurboAgent.onNextDayStay();
            }
        }
    }

    /**
     * ????????????
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
        // ??????????????????????????????????????????SDK
        if (lackedPermission.size() == 0) {
            if (!fromBackstage || BaseConstans.getNextIsNewUser()) {
                requestConfigForTemplateList();
            }
            hasPermission = true;
            if (BaseConstans.getHasAdvertising() == 1 ) {
                StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "test_ad_into_checkPermiss_6_requestAd");
                showSplashAd();
            } else {
                noQueryAdReason();
                StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "test_ad_into_checkPermiss_6_no_requestAd");
            }
        } else {
            hasPermission = false;
            // ??????????????????????????????onRequestPermissionsResult???????????????????????????????????????????????????????????????SDK?????????????????????SDK???
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
            if (BaseConstans.getHasAdvertising() == 1 ) {
                showSplashAd();
            }
        } else {
            hasPermission = false;
            // ??????????????????????????????????????????????????????????????????????????????????????????
            ToastUtil.showToast(this.getString(R.string.Permissions_repulse));
            new Handler().postDelayed(this::gotoNext, 0);
        }
    }

    /**
     * description ??????????????????????????????????????? ,????????????????????????
     * creation date: 2021/3/10
     * user : zhangtongju
     */
    private void gotoNext() {
        if (BaseConstans.getHasAdvertising() == 1 ) {
            StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "test_ad_into_requestPermiss_6_requestAd");
            showSplashAd();
        } else {
            StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "test_ad_into_requestPermiss_6_no_requestAd");
            noQueryAdReason();
        }
    }


    /**
     * description ???????????????????????????
     * creation date: 2021/5/11
     * user : zhangtongju
     */
    private void noQueryAdReason() {
        if (BaseConstans.getHasAdvertising() != 1) {
            StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "start_not_request_ad", "?????????????????????");
        }else if (BaseConstans.getIsNewUser()) {
            StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "start_not_request_ad", "?????????");
        } else {
            StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "start_not_request_ad", "????????????");
        }
        intoMain();
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
            if(canGetPermissions()){
                BaseConstans.setLastRequestPerTime(System.currentTimeMillis());
                if (Build.VERSION.SDK_INT >= BUILD_VERSION) {
                    checkPermission();
                    StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "test_ad_into_checkPermiss_6");
                } else {
                    hasPermission = true;
                    StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "test_ad_into_checkPermiss_less_6");
                    if (BaseConstans.getHasAdvertising() == 1 ) {
                        StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "test_ad_into_checkPermiss_less_6_requestAd");
                        showSplashAd();
                    } else {
                        noQueryAdReason();
                        StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "test_ad_into_checkPermiss_less_6_no_requestAd");
                    }
                }
            }else{
                hasPermission = true;
                StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "test_ad_into_checkPermiss_less_6");
                if (BaseConstans.getHasAdvertising() == 1 ) {
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
     * description ??????????????????????????????
     * date: ???2019/11/12 15:06
     * author: ????????? @?????? jutongzhang@sina.com
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
                checkConfigInfoState();
                requestConfig();
            } else {
                this.finish();
            }
        }
    }



    /**
     * description ??????????????????????????????5s?????????????????????????????????app??????
     * creation date: 2021/5/26
     * user : zhangtongju
     */
    private  boolean hasGetConfigInfo=false;
    private void checkConfigInfoState(){
        new Handler().postDelayed(() -> {
            if(!hasGetConfigInfo){
                toGetPermission();
            }
        },5000);
    }


    /**
     * ??????????????????
     */
    private void showSplashAd() {
        Log.d("OOM2", "Application start finished");
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "start_ad_request");
            SplashAdManager splashAdManager = new SplashAdManager();
            splashAdManager.showSplashAd(AdConfigs.AD_SPLASH, this, mBinding.rlAdContainer, mBinding.tvSkip, ScreenUtil.dip2px(this, 0), new SplashAdCallBack() {
                @Override
                public void onAdSuccess() {
                    Log.d(TAG, "onAdSuccess");
                    isShow = true;
                    StatisticsEventAffair.getInstance().setFlag(WelcomeActivity.this, "start_ad_request_success");
                }

                @Override
                public void onAdError(String errorMsg) {
                    Log.d(TAG, "errorMsg="+errorMsg);
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
                    mBinding.tvSkip.setText(String.format("?????? %d", Math.round(millisUntilFinished / 1000f)));
                }

                @Override
                public void onAdDismissed() {
                    next();
                }
            });
        }
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????App?????????????????????????????????????????????
     * ????????????????????????????????????App???????????????????????????App????????????????????????App???
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
     * ???????????????????????????????????????????????????????????????????????????????????????????????????App????????????????????????????????????
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * description ??????????????????????????????????????????????????????????????????????????????
     * creation date: 2020/4/8
     * user : zhangtongju
     */
    private void requestConfigForTemplateList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("config_name", "wechat_name");
        // ????????????
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


    /**
     * description ???????????????????????????????????????????????????
     * creation date: 2020/4/8
     * user : zhangtongju
     */
    private void requestConfig() {
        HashMap<String, String> params = new HashMap<>();
        // ????????????
        Observable ob = Api.getDefault().configList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<Config>>(WelcomeActivity.this) {
            @Override
            protected void onSubError(String message) {
                hasGetConfigInfo=true;
                toGetPermission();
            }

            @Override
            protected void onSubNext(List<Config> data) {
                hasGetConfigInfo=true;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < data.size(); i++) {
                    String tet = StringUtil.beanToJSONString(data.get(i));
                    sb.append(tet);
                }
                if (data.size() > 0) {
                    for (int i = 0; i < data.size(); i++) {
                        Config config = data.get(i);
                        int id = config.getId();
                        if (id == 20) {
                            //android ????????????
                            String AuditModeJson = config.getValue();
                            auditModeConfig(AuditModeJson);
                        }
                    }
                }
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }

    private void auditModeConfig(String str) {
        LogUtil.d("oom2","auditModeConfig="+str);
        Hawk.put("AuditModeConfig", str);
        int isVideoadvertisingId = 0;
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(str);
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obArray = jsonArray.getJSONObject(i);
                    String channel = obArray.getString("channel");
                    if ("isVideoadvertising".equals(channel)) { //??????????????????
                        isVideoadvertisingId = obArray.getInt("id");
                    }
                    if (channel.equals(BaseConstans.getChannel())) { //????????????????????????
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
        toGetPermission();
    }


    private void toGetPermission(){
        BaseConstans.setFirstClickUseApp();
        getPermission();
        Date date = new Date();
        BaseConstans.setFirstUseAppTime(date.getTime());
    }




    /**
     * description ??????????????????????????????7??????????????????
     * creation date: 2021/5/21
     * user : zhangtongju
     */
    private boolean canGetPermissions() {
        long nowCurrentTime = System.currentTimeMillis();
        long lastCloseTime = BaseConstans.getLastRequestPerTime();
        long intervalTime = nowCurrentTime - lastCloseTime;
        intervalTime = intervalTime / 1000 / 60 / 60 / 24;
        return intervalTime >= 24*7;
    }




}
