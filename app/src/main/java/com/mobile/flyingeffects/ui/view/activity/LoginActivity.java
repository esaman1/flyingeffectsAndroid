package com.mobile.flyingeffects.ui.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.mobile.flyingeffects.R;
import com.mobile.flyingeffects.base.ActivityLifeCycleEvent;
import com.mobile.flyingeffects.base.BaseActivity;
import com.mobile.flyingeffects.http.Api;
import com.mobile.flyingeffects.http.HttpUtil;
import com.mobile.flyingeffects.http.ProgressSubscriber;
import com.mobile.flyingeffects.utils.LogUtil;
import com.mobile.flyingeffects.utils.StringUtil;
import com.mobile.flyingeffects.utils.ToastUtil;

import java.util.HashMap;

import butterknife.BindView;
import rx.Observable;

/**
 * Created by zhangtongju
 * on 2017/8/9.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.password)
    EditText password;
//
    @BindView(R.id.username)
    EditText username;


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
    }


    @Override
    protected void initView() {
    }







    private void requestLogin() {

        if (username.getText().toString().equals("")) {
            ToastUtil.showToast("请输入手机号");
            return;
        }

        if (password.getText().toString().equals("")) {
            ToastUtil.showToast("请输入密码");
            return;
        }
        requestLogin(username.getText().toString(),password.getText().toString());

    }







    private void requestLogin(String username, String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        // 启动时间
        Observable ob = Api.getDefault().toLogin(params);
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(LoginActivity.this) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(Object data) {
                String str=StringUtil.beanToJSONString(data);
                LogUtil.d("login","str="+str);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }






}




