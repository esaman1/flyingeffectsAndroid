package com.flyingeffects.com.ui.view.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
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
    EditText password;
    @BindView(R.id.username)
    EditText username;


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
        requestLogin(username.getText().toString(), password.getText().toString());

    }


    @OnClick({R.id.tv_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login:
                if (nowProgressType == 0) {
                    requestSms();
                }

                break;

        }

    }


    private void requestSms() {
        if (TextUtils.isEmpty(username.getText().toString())) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return;
        }


        requestSms(username.getText().toString());
    }





    private void requestSms(String username) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", username);
        // 启动时间
        Observable ob = Api.getDefault().toSms(params);
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(LoginActivity.this) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(Object data) {
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("login", "str=" + str);
                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(str);
                    int code=jsonObject.getInt("jsonObject");
                    if(code==1){
                        ToastUtil.showToast("发送成功");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }




            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
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
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("login", "str=" + str);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }


}




