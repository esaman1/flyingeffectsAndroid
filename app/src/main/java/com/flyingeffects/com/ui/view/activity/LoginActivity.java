package com.flyingeffects.com.ui.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.NetworkUtils;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

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
    @BindView(R.id.tv_xy)
    TextView tv_xy;

    private static final String WEIXIN = "wx";
    private static final String QQ = "qq";

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




    @OnClick({R.id.tv_login,R.id.iv_close,R.id.phone_number,R.id.ll_weixin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login:

                break;

            case R.id.iv_close:
                LoginActivity.this.finish();
                break;


            case R.id.phone_number:
                //通过手机号登录

                break;

            case R.id.ll_weixin:
                //微信登录
                UMShareAPI.get(LoginActivity.this).getPlatformInfo(LoginActivity.this, SHARE_MEDIA.WEIXIN, authListener);
                break;


                default:
                    break;
        }

    }

    UMAuthListener authListener = new UMAuthListener() {
        /**
         * 授权开始的回调
         * @param platform 平台名称
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
            LogUtil.d("OOM","onStart");
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
                LogUtil.d("OOM","openid="+data.get("openid")+"NAME="+name);
               requestLogin(plamformType, iconUrl, name);
            }
        }




        /**
         * description ：
         * creation date: 2020/4/7
         * param :  type|1=微信2=qq3=苹果4=闪验
         * user : zhangtongju
         */
        private void requestLogin(String type,  String iconUrl, String nickName) {
            HashMap<String, String> params = new HashMap<>();
            params.put("type", type);
            params.put("photourl", iconUrl);
            params.put("nickname", nickName);
            Observable ob = Api.getDefault().toCommonLogin(BaseConstans.getRequestHead(params));
            HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(LoginActivity.this) {
                @Override
                protected void _onError(String message) {
                    ToastUtil.showToast(message);
                }

                @Override
                protected void _onNext(Object data) {
                    String str = StringUtil.beanToJSONString(data);
                    LogUtil.d("OOM", "requestLogin=" + str);
//                    BaseConstans.SetUserToken(data.getToken());
//                    BaseConstans.SetUserId(data.getId());
//                    LoginActivity.this.finish();
                }
            }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
        }

        /**
         * 授权失败的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            ToastUtil.showToast(getString(R.string.login_fail));
            clearUmData();
        }



        private void clearUmData() {
            UMShareAPI.get(mContext).deleteOauth(LoginActivity.this, SHARE_MEDIA.WEIXIN, authListener);
            UMShareAPI.get(mContext).deleteOauth(LoginActivity.this, SHARE_MEDIA.QQ, authListener);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }
}




