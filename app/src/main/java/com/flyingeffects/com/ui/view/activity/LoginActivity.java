package com.flyingeffects.com.ui.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.chuanglan.shanyan_sdk.listener.OneKeyLoginListener;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.UserInfo;
import com.flyingeffects.com.enity.WxLogin;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.NetworkUtils;
import com.flyingeffects.com.utils.ShanyanConfigUtils;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.VideoUtils;
import com.flyingeffects.com.view.MyVideoView;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;

/**
 * Created by zhangtongju
 * on 2017/8/9.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.shanyan_login_relative)
    RelativeLayout shanyan_login_relative;
    @BindView(R.id.password)
    EditText editTextPassword;
    @BindView(R.id.username)
    EditText editTextUsername;
    private boolean isCanSendMsg = true;
    @BindView(R.id.tv_login)
    TextView tv_login;
    @BindView(R.id.tv_xy)
    TextView tv_xy;
    @BindView(R.id.relative_normal)
    RelativeLayout relative_normal;
    private boolean isOpenAuth = false;
    MyVideoView videoView;
    private static final String WEIXIN = "wx";
    private static final String QQ = "qq";


    /**
     * 0 ，发送验证码，1 登录
     */
    private int nowProgressType;

    @Override
    protected int getLayoutId() {
        return R.layout.act_login;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        OneKeyLoginManager.getInstance().setAuthThemeConfig(ShanyanConfigUtils.getCJSConfig(getApplicationContext()), ShanyanConfigUtils.getCJSConfig(getApplicationContext()));
        openLoginActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != shanyan_login_relative) {
            shanyan_login_relative.removeAllViews();
        }
        if (null != videoView) {
            videoView.setOnCompletionListener(null);
            videoView.setOnPreparedListener(null);
            videoView.setOnErrorListener(null);
            videoView = null;
        }
        EventBus.getDefault().unregister(this);
    }

    private void openLoginActivity() {
        //拉取授权页方法
        OneKeyLoginManager.getInstance().openLoginAuth(false, (code, result) -> {
            if (1000 == code) {
                isOpenAuth = true;
                //拉起授权页成功
                Log.e("VVV", "拉起授权页成功： _code==" + code + "   _result==" + result);
                videoView = new MyVideoView(getApplicationContext());
                RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                shanyan_login_relative.addView(videoView, 0, mLayoutParams);
                VideoUtils.startBgVideo(videoView, getApplicationContext(), "android.resource://" + LoginActivity.this.getPackageName() + "/" + R.raw.login_video);
            } else {
                //拉起授权页失败
                Log.e("VVV", "拉起授权页失败： _code==" + code + "   _result==" + result);
                relative_normal.setVisibility(View.VISIBLE);
            }
        }, new OneKeyLoginListener() {
            @Override
            public void getOneKeyLoginStatus(int code, String result) {
                if (1011 == code) {
                    isOpenAuth = false;
                    Log.e("VVV", "用户点击授权页返回： _code==" + code + "   _result==" + result);
                    LoginActivity.this.finish();
                    return;
                } else if (1000 == code) {
                    Log.e("VVV", "用户点击登录获取token成功： _code==" + code + "   _result==" + result);
                    //OneKeyLoginManager.getInstance().setLoadingVisibility(false);
                    //AbScreenUtils.showToast(getApplicationContext(), "用户点击登录获取token成功");
                    try {
                        JSONObject ob=new JSONObject(result);
                        requestLoginForSdk("4",ob.getString("token"),"","");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                } else {
                    Log.e("VVV", "用户点击登录获取token失败： _code==" + code + "   _result==" + result);
                    ToastUtil.showToast("用户点击登录获取token失败： _code==" + code + "   _result==" + result);
//                    relative_normal.setVisibility(View.VISIBLE);
                    LoginActivity.this.finish();
                }
                long startTime = System.currentTimeMillis();
                startResultActivity(code, result, startTime);
            }
        });
    }


    private void startResultActivity(int code, String result, long startTime) {
        LogUtil.d("OOM", "code=" + code + "result=" + result);
//        Intent intent = new Intent(LoginForShanYanActivity.this, ResultActivity.class);
//        intent.putExtra("type", "0");
//        intent.putExtra("startTime", startTime);
//        intent.putExtra("loginResult", result);
//        intent.putExtra("loginCode", code);
//        startActivity(intent);
//        OneKeyLoginManager.getInstance().finishAuthActivity();
//        OneKeyLoginManager.getInstance().removeAllListener();
//        isOpenAuth = false;
    }

    @Override
    protected void initAction() {
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String strPassword = editTextPassword.getText().toString().trim();
                if (!strPassword.equals("")) {
                    nextStep(true);
                    tv_login.setEnabled(true);
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
                tv_login.setEnabled(true);
                endTimer();
            }
        });

        editTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                endTimer();
                if (TextUtils.isEmpty(editTextPassword.getText().toString())) {
                    nextStep(false);
                } else {
                    nextStep(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tv_login.setEnabled(true);
            }
        });


    }


    private void nextStep(boolean isLogin) {
        if (isLogin) {
            tv_login.setText("登录");
            tv_login.setBackground(getResources().getDrawable(R.drawable.login_button));
            nowProgressType = 1;
        } else {
            tv_login.setText("获得验证码");
            nowProgressType = 0;
        }
    }


    @Override
    protected void initView() {
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
        tv_xy.setMovementMethod(LinkMovementMethod.getInstance());
        tv_xy.setText(spannableBuilder);
    }


    private void requestLogin() {

        if (editTextUsername.getText().toString().equals("")) {
            ToastUtil.showToast("请输入手机号");
            return;
        }

        if (editTextPassword.getText().toString().equals("")) {
            ToastUtil.showToast("请输入验证码");
            return;
        }
        requestLogin(editTextUsername.getText().toString(), editTextPassword.getText().toString());

    }


    @OnClick({R.id.tv_login, R.id.iv_close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login:
                if (nowProgressType == 0) {
                    if (isCanSendMsg) {
                        toRequestSms();
                    }
                } else {
                    tv_login.setEnabled(true);
                    tv_login.setBackground(getResources().getDrawable(R.drawable.login_button));
                    requestLogin();
                }
                break;

            case R.id.iv_close:
                LoginActivity.this.finish();
                break;
            default:
                break;
        }

    }


    private void toRequestSms() {
        if (TextUtils.isEmpty(editTextUsername.getText().toString())) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!StringUtil.isPhone(editTextUsername.getText().toString())) {
            Toast.makeText(this, "请输入正确手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        requestSms(editTextUsername.getText().toString());

    }


    private void requestSms(String strEditTextUsername) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", strEditTextUsername);
        // 启动时间
        Observable ob = Api.getDefault().toSms(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(LoginActivity.this) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(Object data) {
                startTimer();
                ToastUtil.showToast("发送成功");
                String str = StringUtil.beanToJSONString(data);
                changeFocus();
                LogUtil.d("login", "str=" + str);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }


    private void changeFocus() {
        editTextPassword.requestFocus();
        editTextPassword.setFocusable(true);
        editTextPassword.setFocusableInTouchMode(true);
    }


    private void requestLogin(String editTextUsername, String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", editTextUsername);
        params.put("code", password);
        // 启动时间
        Observable ob = Api.getDefault().toLogin(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<UserInfo>(LoginActivity.this) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(UserInfo data) {
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("OOM", "requestLogin=" + str);
                BaseConstans.SetUserToken(data.getToken());
                BaseConstans.SetUserId(data.getId());
                LoginActivity.this.finish();
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }





    /**
     * description ：除了短信登录外的登录
     * creation date: 2020/4/8
     * param :type|1=微信2=qq3=苹果4=闪验
     * user : zhangtongju
     */
    private void requestLoginForSdk(String type,String flash_token,String nickname,String photourl) {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", type);
        params.put("flash_token", flash_token);
        params.put("nickname", nickname);
        params.put("photourl", photourl);

        // 启动时间
        Observable ob = Api.getDefault().toLoginSms(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<UserInfo>(LoginActivity.this) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(UserInfo data) {
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("OOM", "requestLogin=" + str);
                BaseConstans.SetUserToken(data.getToken());
                BaseConstans.SetUserId(data.getId());
                LoginActivity.this.finish();
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }




    private Timer timer;
    private TimerTask task;
    private int total_Time = 60;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    total_Time = total_Time - 1;
                    tv_login.setText((String.format(getResources().getString(R.string.remainTime), total_Time)));
                    if (total_Time == 0) {
                        total_Time = 60;
                        endTimer();
                    }
                    break;
            }
        }
    };

    /***
     * 倒计时60s
     */
    private void startTimer() {
        isCanSendMsg = false;
        tv_login.setEnabled(false);
        tv_login.setBackground(getResources().getDrawable(R.drawable.login_button_forbidden));
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
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        };
        timer.schedule(task, 0, 1000);
    }

    /**
     * 关闭timer 和task
     */
    private void endTimer() {
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (nowProgressType == 0) {
            tv_login.setText("获取短信验证码");
        }

        isCanSendMsg = true;
        tv_login.setEnabled(true);
        tv_login.setBackground(getResources().getDrawable(R.drawable.login_button));
    }





    public void wxLogin(){
        UMShareAPI.get(LoginActivity.this).getPlatformInfo(LoginActivity.this, SHARE_MEDIA.WEIXIN, authListener);
    }




    UMAuthListener authListener = new UMAuthListener() {
        /**
         * 授权开始的回调
         * @param platform 平台名称
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
        LogUtil.d("OOM","onstart");
        }

        /**
         *  授权成功的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param data 用户资料返回
         */
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            if (data != null) {
                Bundle bundle = new Bundle();
                String name = data.get("name");
                String iconUrl = data.get("iconurl");
                bundle.putSerializable("name", name);
                bundle.putSerializable("iconUrl", iconUrl);
                String plamformType = platform.toString();
                plamformType = (plamformType.equals("QQ") ? QQ : WEIXIN);
                ToastUtil.showToast(name);
//                requestLogin(plamformType, data.get("openid"), data.get("unionid"), iconUrl, name);
                requestLoginForSdk("1","",name,iconUrl);
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
            LogUtil.d("OOM","onError"+t.getMessage());
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


    private void clearUmData() {
        UMShareAPI.get(mContext).deleteOauth(this, SHARE_MEDIA.WEIXIN, authListener);
        UMShareAPI.get(mContext).deleteOauth(this, SHARE_MEDIA.QQ, authListener);
    }


    @Subscribe
    public void onEventMainThread(WxLogin event) {
        if(event.getTag().equals("wxLogin")){
            wxLogin();
        }

    }


}




