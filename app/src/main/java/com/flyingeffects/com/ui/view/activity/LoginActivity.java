package com.flyingeffects.com.ui.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.databinding.ActLoginBinding;
import com.flyingeffects.com.entity.BackgroundTemplateCollectionEvent;
import com.flyingeffects.com.entity.HttpResult;
import com.flyingeffects.com.entity.LoginToAttentionUserEvent;
import com.flyingeffects.com.entity.UserInfo;
import com.flyingeffects.com.entity.WxLogin;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.utils.AbScreenUtils;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.NetworkUtils;
import com.flyingeffects.com.utils.ShanyanConfigUtils;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.VideoUtils;
import com.flyingeffects.com.view.MyVideoView;
import com.orhanobut.hawk.Hawk;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.nt.lib.analytics.NTAnalytics;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;

/**
 * Created by zhangtongju
 * on 2017/8/9.
 */

public class LoginActivity extends BaseActivity {
    private static final int TOTAL_TIME = 60;

    private Context mContext;

    private boolean isCanSendMsg = true;

    MyVideoView videoView;

    boolean isOnDestroy = false;
    /**
     * 0 ，发送验证码，1 登录
     */
    private int nowProgressType;


    //当前页面类型 0是老板ui ,1 是新版ui
    private int nowPageType = 1;
    private ActLoginBinding mBinding;

    private CountDownTimer mTimer;
    private int mTime = TOTAL_TIME;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {
        mContext = LoginActivity.this;
        isOnDestroy = false;
        mBinding = ActLoginBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        setContentView(rootView);
        EventBus.getDefault().register(this);
        clearUmData();
        WaitingDialog.openPragressDialog(this);
        OneKeyLoginManager.getInstance().setLoadingVisibility(false);
        OneKeyLoginManager.getInstance().setAuthThemeConfig(ShanyanConfigUtils.getCJSConfig(getApplicationContext()), ShanyanConfigUtils.getCJSConfig(getApplicationContext()));
        openLoginActivity();
        SpannableStringBuilder strBuilder = initTipsBuilder();
        mBinding.tvXy.setMovementMethod(LinkMovementMethod.getInstance());
        mBinding.tvXy.setText(strBuilder);
        setOnclickListener();
    }

    private void setOnclickListener() {
        mBinding.tvLogin.setOnClickListener(this::onViewClick);
        mBinding.ivClose.setOnClickListener(this::onViewClick);
        mBinding.llWeixin.setOnClickListener(this::onViewClick);
    }

    private SpannableStringBuilder initTipsBuilder() {
        String tips = "登录表示你同意《服务条款》和《隐私政策》";
        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(tips);
        ClickableSpan clickableSpanOne = new ClickableSpan() {
            @Override
            public void onClick(@NotNull View view) {
                if (NetworkUtils.isNetworkAvailable(LoginActivity.this)) {
                    Intent intent = new Intent(LoginActivity.this, webViewActivity.class);
                    intent.putExtra("webUrl", BaseConstans.PROTOCOL);
                    startActivity(intent);
                } else {
                    ToastUtil.showToast("网络连接失败！");
                }
            }

            @Override
            public void updateDrawState(TextPaint paint) {
                paint.setColor(Color.parseColor("#0092FE"));
                // 设置下划线 true显示、false不显示
                paint.setUnderlineText(false);
            }
        };

        ClickableSpan clickableSpanTwo = new ClickableSpan() {
            @Override
            public void onClick(@NotNull View view) {
                if (NetworkUtils.isNetworkAvailable(LoginActivity.this)) {
                    Intent intent = new Intent(LoginActivity.this, webViewActivity.class);
                    intent.putExtra("webUrl", BaseConstans.PRIVACYPOLICY);
                    startActivity(intent);
                } else {
                    ToastUtil.showToast("网络连接失败！");
                }
            }

            @Override
            public void updateDrawState(TextPaint paint) {
                paint.setColor(Color.parseColor("#0092FE"));
                // 设置下划线 true显示、false不显示
                paint.setUnderlineText(false);
            }
        };
        spannableBuilder.setSpan(clickableSpanOne, 8, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableBuilder.setSpan(clickableSpanTwo, 15, 19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableBuilder;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        isOnDestroy = true;

        mBinding.shanyanLoginRelative.removeAllViews();

        endTimer();
        if (null != videoView) {
            videoView.setOnCompletionListener(null);
            videoView.setOnPreparedListener(null);
            videoView.setOnErrorListener(null);
            videoView = null;
        }
        EventBus.getDefault().unregister(this);
    }


    private void disMissShanYanUi() {
        OneKeyLoginManager.getInstance().finishAuthActivity();
        OneKeyLoginManager.getInstance().removeAllListener();
    }

    private void openLoginActivity() {
        //拉取授权页方法
        OneKeyLoginManager.getInstance().openLoginAuth(false, (code, result) -> {
            WaitingDialog.closeProgressDialog();
            if (1000 == code) {
                //拉起授权页成功
                Log.e("VVV", "拉起授权页成功： _code==" + code + "   _result==" + result);
                videoView = new MyVideoView(getApplicationContext());
                RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                mBinding.shanyanLoginRelative.addView(videoView, 0, mLayoutParams);
                VideoUtils.startBgVideo(videoView, getApplicationContext(), "android.resource://" + LoginActivity.this.getPackageName() + "/" + R.raw.login_video);
            } else {
                nowPageType = 0;
                //拉起授权页失败
                Log.e("VVV", "拉起授权页失败： _code==" + code + "   _result==" + result);
                mBinding.relativeNormal.setVisibility(View.VISIBLE);
                disMissShanYanUi();
            }
        }, (code, result) -> {
            if (1011 == code) {
                Log.e("OOM", "用户点击授权页返回： _code==" + code + "   _result==" + result);
                closeThisAct();
            } else if (1000 == code) {
                Log.e("VVV", "用户点击登录获取token成功： _code==" + code + "   _result==" + result);
                try {
                    JSONObject ob = new JSONObject(result);
                    requestLoginForSdk("4", ob.getString("token"), "", "", "", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("VVV", "用户点击登录获取token失败： _code==" + code + "   _result==" + result);
                closeThisAct();
            }

        });
    }

    @Override
    protected void initAction() {
        mBinding.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String strPassword = mBinding.password.getText().toString().trim();
                if (!"".equals(strPassword)) {
                    nextStep(true);
                    mBinding.tvLogin.setEnabled(true);
                    endTimer();
                } else {
                    nextStep(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                nextStep(true);
                mBinding.tvLogin.setEnabled(true);
                endTimer();
            }
        });

        mBinding.username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                endTimer();
                nextStep(!TextUtils.isEmpty(mBinding.password.getText().toString()));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mBinding.tvLogin.setEnabled(true);
            }
        });
    }


    private void nextStep(boolean isLogin) {
        if (isLogin) {
            mBinding.tvLogin.setText("登录");
            mBinding.tvLogin.setBackground(ContextCompat.getDrawable(mContext, R.drawable.login_button));
            nowProgressType = 1;
        } else {
            mBinding.tvLogin.setText("获得验证码");
            nowProgressType = 0;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (nowPageType == 1) {
            VideoUtils.startBgVideo(videoView, getApplicationContext(), "android.resource://" + this.getPackageName() + "/" + R.raw.login_video);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nowPageType == 1) {
            AbScreenUtils.hideBottomUIMenu(this);
        }
    }


    private void clearUmData() {
        UMShareAPI.get(mContext).deleteOauth(this, SHARE_MEDIA.WEIXIN, authListener);
        UMShareAPI.get(mContext).deleteOauth(this, SHARE_MEDIA.QQ, authListener);
    }


    private void requestLogin() {

        if ("".equals(mBinding.username.getText().toString())) {
            ToastUtil.showToast("请输入手机号");
            return;
        }

        if ("".equals(mBinding.password.getText().toString())) {
            ToastUtil.showToast("请输入验证码");
            return;
        }
        requestLogin(mBinding.username.getText().toString().trim(), mBinding.password.getText().toString().trim());
    }


    public void onViewClick(View view) {
        if (view == mBinding.tvLogin) {
            if (nowProgressType == 0) {
                if (isCanSendMsg) {
                    toRequestSms();
                }
            } else {
                mBinding.tvLogin.setEnabled(true);
                mBinding.tvLogin.setBackground(ContextCompat.getDrawable(mContext, R.drawable.login_button));
                requestLogin();
            }
        } else if (view == mBinding.ivClose) {
            finish();
        } else if (view == mBinding.llWeixin) {
            if (!isWeiXinAvailable(this)) {
                ToastUtil.showToast("您还未安装微信");
            } else {
                if (!DoubleClick.getInstance().isFastZDYDoubleClick(2000)) {
                    wxLogin();
                }
            }
        }
    }


    private void toRequestSms() {
        if (TextUtils.isEmpty(mBinding.username.getText().toString())) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!StringUtil.isPhone(mBinding.username.getText().toString())) {
            Toast.makeText(this, "请输入正确手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        requestSms(mBinding.username.getText().toString().trim());
    }


    private void requestSms(String strEditTextUsername) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", strEditTextUsername);
        // 启动时间
        Observable<HttpResult<Object>> ob = Api.getDefault().toSms(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(LoginActivity.this) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(Object data) {
                startTimer();
                ToastUtil.showToast("发送成功");
                String str = StringUtil.beanToJSONString(data);
                changeFocus();
                LogUtil.d("login", "str=" + str);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }

    private void changeFocus() {
        mBinding.password.requestFocus();
        mBinding.password.setFocusable(true);
        mBinding.password.setFocusableInTouchMode(true);
    }

    private void requestLogin(String editTextUsername, String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", editTextUsername);
        params.put("code", password);

        params.put("center_imei", NTAnalytics.getIMEI());
        // 启动时间
        LogUtil.d("OOM", StringUtil.beanToJSONString(params));
        Observable<HttpResult<UserInfo>> ob = Api.getDefault().toLogin(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<UserInfo>(LoginActivity.this) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(UserInfo data) {
                Hawk.put(UserInfo.USER_INFO_KEY, data);
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("OOM", "requestLogin=" + str);
                BaseConstans.SetUserToken(data.getToken());
                BaseConstans.SetUserId(data.getId(), data.getNickname(), data.getPhotourl());
                EventBus.getDefault().post(new LoginToAttentionUserEvent());
                EventBus.getDefault().post(new BackgroundTemplateCollectionEvent());
                closeThisAct();
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }


    private void closeThisAct(){
        new Handler().postDelayed(LoginActivity.this::finish,500);
    }



    /**
     * description ：除了短信登录外的登录 ,之所以加了弹出关闭，因为米面弹出错误日志
     * creation date: 2020/4/8
     * param :type|1=微信2=qq3=苹果4=闪验
     * user : zhangtongju
     */
    private void requestLoginForSdk(String type, String flash_token, String nickname, String photourl, String openid, String unionid) {
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            HashMap<String, String> params = new HashMap<>();
            params.put("type", type);
            params.put("flash_token", flash_token);
            params.put("nickname", nickname);
            params.put("photourl", photourl);
            params.put("openid", openid);
            params.put("center_imei", NTAnalytics.getIMEI());
            params.put("unionid", unionid);
            // 启动时间
            Observable<HttpResult<UserInfo>> ob = Api.getDefault().toLoginSms(BaseConstans.getRequestHead(params));
            HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<UserInfo>(LoginActivity.this) {
                @Override
                protected void onSubError(String message) {
                    if (!isOnDestroy) {
                        WaitingDialog.closeProgressDialog();
                        ToastUtil.showToast(message);
                    }
                }

                @Override
                protected void onSubNext(UserInfo data) {
                    if (!isOnDestroy) {
                        Hawk.put(UserInfo.USER_INFO_KEY, data);
                        //String str = StringUtil.beanToJSONString(data);
                        LogUtil.d("OOM", "setToken=" + data.getToken());
                        BaseConstans.SetUserToken(data.getToken());
                        BaseConstans.SetUserId(data.getId(), data.getNickname(), data.getPhotourl());
                        disMissShanYanUi();
                        WaitingDialog.closeProgressDialog();
                        EventBus.getDefault().post(new LoginToAttentionUserEvent());
                        EventBus.getDefault().post(new BackgroundTemplateCollectionEvent());
                        closeThisAct();
                    }
                }
            }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
        }

    }


    /***
     * 倒计时60s
     */
    private void startTimer() {
        isCanSendMsg = false;
        mBinding.tvLogin.setEnabled(false);
        mBinding.tvLogin.setBackground(ContextCompat.getDrawable(mContext, R.drawable.login_button_forbidden));

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mTimer = new CountDownTimer(TOTAL_TIME * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isFinishing()) {
                    return;
                }
                mTime = mTime - 1;
                mBinding.tvLogin.setText((String.format(getResources().getString(R.string.remainTime), mTime)));
                if (mTime == 0) {
                    mTime = TOTAL_TIME;
                    endTimer();
                }
            }

            @Override
            public void onFinish() {
                endTimer();
            }
        };
        //调用 CountDownTimer 对象的 start() 方法开始倒计时，也不涉及到线程处理
        mTimer.start();
    }

    /**
     * 关闭timer 和task
     */
    private void endTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (nowProgressType == 0) {
            mBinding.tvLogin.setText("获取短信验证码");
        }

        isCanSendMsg = true;
        mBinding.tvLogin.setEnabled(true);
        mBinding.tvLogin.setBackground(ContextCompat.getDrawable(mContext, R.drawable.login_button));
    }


    public void wxLogin() {
        UMShareAPI.get(LoginActivity.this).getPlatformInfo(LoginActivity.this, SHARE_MEDIA.WEIXIN, authListener);
    }

    UMAuthListener authListener = new UMAuthListener() {
        /**
         * 授权开始的回调
         * @param platform 平台名称
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
            LogUtil.d("OOM", "onstart");
        }

        /**
         *  授权成功的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param data 用户资料返回
         */
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            if (data != null && !isOnDestroy) {
                Bundle bundle = new Bundle();
                String name = data.get("name");
                String iconUrl = data.get("iconurl");
                bundle.putSerializable("name", name);
                bundle.putSerializable("iconUrl", iconUrl);

                WaitingDialog.openPragressDialog(LoginActivity.this);
                requestLoginForSdk("1", "", name, iconUrl, data.get("openid"), data.get("unionid"));
            }
        }


        /**
         * 授权失败的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            LogUtil.d("OOM", "onError" + t.getMessage());
            ToastUtil.showToast(getString(R.string.login_fail));
            clearUmData();
        }

        /**
         * 授权取消的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         */
        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(mContext, getString(R.string.cancel_login), Toast.LENGTH_LONG).show();
        }
    };


    @Subscribe
    public void onEventMainThread(WxLogin event) {
        if ("wxLogin".equals(event.getTag())) {
            if (!isWeiXinAvailable(this)) {
                ToastUtil.showToast("您还未安装微信");
            } else {
                if (!DoubleClick.getInstance().isFastZDYDoubleClick(2000)) {
                    wxLogin();
                }
            }
        }
    }

    /**
     * description ： 是否安装了微信
     * creation date: 2020/4/9
     * param :
     * user : zhangtongju
     */
    public boolean isWeiXinAvailable(Context context) {
        // 获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if ("com.tencent.mm".equals(pn)) {
                    return true;
                }
            }
        }
        return false;
    }


}




