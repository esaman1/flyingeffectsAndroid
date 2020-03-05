package com.flyingeffects.com.ui.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.UserInfo;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.NetworkUtils;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;

/**
 * Created by zhangtongju
 * on 2017/8/9.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.password)
    EditText editTextPassword;
    @BindView(R.id.username)
    EditText editTextUsername;
    private boolean isCanSendMsg = true;
    @BindView(R.id.tv_login)
    TextView tv_login;
    @BindView(R.id.tv_xy)
    TextView tv_xy;


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


        SpannableString ss = new SpannableString("请输入手机号");//定义hint的值
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(16, true);//设置字体大小 true表示单位是sp
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editTextUsername.setHint(new SpannedString(ss));
        SpannableString ss2 = new SpannableString("请输入验证码");//定义hint的值
        ss2.setSpan(ass, 0, ss2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editTextPassword.setHint(new SpannedString(ss2));


    }


    private void requestLogin() {

        if (editTextUsername.getText().toString().equals("")) {
            ToastUtil.showToast("请输入手机号");
            return;
        }

        if (editTextPassword.getText().toString().equals("")) {
            ToastUtil.showToast("请输入密码");
            return;
        }
        requestLogin(editTextUsername.getText().toString(), editTextPassword.getText().toString());

    }


    @OnClick({R.id.tv_login})
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


    private void requestSms(String streditTextUsername) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", streditTextUsername);
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
                LogUtil.d("login", "str=" + str);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }


    private void changeFocus() {
        editTextPassword.requestFocus();
        editTextPassword.setFocusable(true);
        editTextPassword.setFocusableInTouchMode(true);
        InputMethodManager imm = (InputMethodManager) editTextPassword.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
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


}




