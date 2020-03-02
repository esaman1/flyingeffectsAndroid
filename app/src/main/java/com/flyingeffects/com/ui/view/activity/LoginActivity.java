package com.flyingeffects.com.ui.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

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


    @BindView(R.id.tv_login)
    TextView tv_login;


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
              String  strPassword= editTextPassword.getText().toString().trim();
                if (!strPassword.equals("")) {
                    nextStep(true);
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
            }
        });

        editTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(TextUtils.isEmpty(editTextPassword.getText().toString())){
                    nextStep(false);
                }else{
                    nextStep(true);
                }

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }



    private void nextStep(boolean isLogin){
        if(isLogin){
            tv_login.setText("登录");
            nowProgressType = 1;
        }else{
            tv_login.setText("获得验证码");
            nowProgressType = 0;
        }
    }


    @Override
    protected void initView() {
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
                    toRequestSms();
                } else {
                    requestLogin();
                }

                break;

        }

    }


    private void toRequestSms() {
        if (TextUtils.isEmpty(editTextUsername.getText().toString())) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
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
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("login", "str=" + str);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(str);
                    int code = jsonObject.getInt("jsonObject");
                    if (code == 1) {
                        ToastUtil.showToast("发送成功");
                    }
                    nextStep(true);
                    changeFocus();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }



    private void changeFocus(){
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
                LoginActivity.this.finish();
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }





}




