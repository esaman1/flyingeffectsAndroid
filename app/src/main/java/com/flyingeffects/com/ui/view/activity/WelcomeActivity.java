package com.flyingeffects.com.ui.view.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.WindowManager;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.Config;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.utils.PermissionUtil;
import com.flyingeffects.com.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;

public class WelcomeActivity extends BaseActivity {

    private final int BUILD_VERSION = 23;
//    private boolean hasPermission = false;
    private final int PERMISSION_REQUEST_CODE = 1024;
    private static  final int RESULT_CODE=3;
    @Override
    protected int getLayoutId() {
        return R.layout.act_welcome;
    }

    @Override
    protected void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, //去掉状态栏
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        gotoPrivacyPolicyActivity();

    }

    @Override
    protected void initAction() {
        requestConfig();
    }



    private void showSplashAd(){
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                    goActivity(HomeMainActivity.class);
                this.finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
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
//        if (!(checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED)) {
//            lackedPermission.add(Manifest.permission.SYSTEM_ALERT_WINDOW);
//        }

        // 权限都已经有了，那么直接调用SDK
        if (lackedPermission.size() == 0) {
//            hasPermission = true;
                showSplashAd();
        } else {
//            hasPermission = false;
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
//            hasPermission = true;
            showSplashAd();
        } else {
//            hasPermission = false;
            // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
            ToastUtil.showToast("您已拒绝权限，需要开启权限才能使用");
            new Handler().postDelayed(() -> {
                PermissionUtil.gotoPermission(WelcomeActivity.this);
                finish();
            },3000);

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


    private void getPermission(){
            if (Build.VERSION.SDK_INT >= BUILD_VERSION) {
                checkPermission();
            } else {
//                hasPermission = true;
                showSplashAd();
            }
    }




    private void requestConfig() {
        HashMap<String, String> params = new HashMap<>();
        params.put("config_name", "wechat_name");
        // 启动时间
        Observable ob = Api.getDefault().configList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Config>(WelcomeActivity.this) {
            @Override
            protected void _onError(String message) {
//                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(Config data) {
                BaseConstans.service_wxi=data.getValue();
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
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
            if(agree){
                BaseConstans.setFirstClickUseApp();
                getPermission();
            }else{
                this.finish();
            }
        }
    }

}
