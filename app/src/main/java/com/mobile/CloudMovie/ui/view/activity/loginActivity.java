package com.mobile.CloudMovie.ui.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobile.CloudMovie.R;
import com.mobile.CloudMovie.base.ActivityLifeCycleEvent;
import com.mobile.CloudMovie.base.BaseActivity;
import com.mobile.CloudMovie.constans.BaseConstans;
import com.mobile.CloudMovie.http.Api;
import com.mobile.CloudMovie.http.HttpUtil;
import com.mobile.CloudMovie.http.ProgressSubscriber;
import com.mobile.CloudMovie.utils.LogUtil;
import com.mobile.CloudMovie.utils.StringUtil;
import com.mobile.CloudMovie.utils.ToastUtil;

import java.util.HashMap;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import rx.Observable;

/**
 * Created by zhangtongju
 * on 2017/8/9.
 */

public class loginActivity extends BaseActivity implements View.OnClickListener {
//
//    @BindView(R.id.tv_top_title)
//    TextView tv_top_title;

//
//    @BindView(R.id.rela_login)
//    RelativeLayout rela_login;


    @BindView(R.id.password)
    EditText password;
//
    @BindView(R.id.username)
    EditText username;
//
//    @BindView(R.id.iv_top_back)
//    ImageView iv_top_back;
//
//    @BindView(R.id.tv_xy)
//    TextView tv_xy;

    private String actTag;

    private boolean isCanSendMsg = true;

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
        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.iv_top_back).setOnClickListener(this);

        TextView forget = findViewById(R.id.forget);
        forget.setOnClickListener(this);

        TextView register = findViewById(R.id.register);
        register.setOnClickListener(this);
//        rela_login.setOnClickListener(this);
//        iv_top_back.setOnClickListener(this);
//        tv_xy.setOnClickListener(this);
//        tv_top_title.setText("登录");
        actTag=getIntent().getStringExtra("actTag");
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rela_login:
                requestLogin();
                break;

            case R.id.register:
                Intent intent=new Intent(this,regiestActivity.class);
                startActivity(intent);
                break;

            case R.id.tv_xy:
//                intoWebView();
                break;


            case R.id.forget:
                Intent intentForget=new Intent(this,forgetActivity.class);
                startActivity(intentForget);
                break;
            case R.id.login:
                requestLogin();
                break;


            default:
                super.onClick(v);
                break;
        }
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



//    /**
//    * user :TongJu  ;描述：发送通知
//    * 时间：2018/5/25
//    **/
//    private void closeAct() {
//        EventBus.getDefault().post(new mineUpdateUserInfo(actTag));
//        loginActivity.this.finish();
//    }




    private void requestLogin(String username, String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        // 启动时间
        Observable ob = Api.getDefault().toLogin(params);
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(loginActivity.this) {
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




