package com.flyingeffects.com.ui.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.constans.BaseConstans;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * description ：隐私政策
 * date: ：2019/11/12 14:38
 * author: 张同举 @邮箱 jutongzhang@sina.com
 */
public class PrivacyPolicyActivity extends Activity {

    private static final int RESULT_CODE=3;

    @BindView(R.id.tv_policy)
    TextView tv_policy;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);
        initView();
        initAction();
    }

    protected void initView() {
        ButterKnife.bind(this);
        String tips = "1.我们会遵循隐私政策收集、使用信息，但不会仅因同意本隐私政策而采取强制捆绑的方式收集信息。\n" +
                "2.在仅浏览时，为保障服务所必须，我们会搜集设备信息和日志信息用于图片浏览、推荐。\n" +
                "3.地理位置、摄像头、相册、通讯录权限均不会默认开启，只有经过明示授权才会为实现功能或服务时使用，您有权拒绝或撤回授权。\n" +
                "您可以查看完整版隐私政策和用户协议。";
        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(tips);
        ClickableSpan clickableSpanOne = new ClickableSpan() {
            @Override
            public void onClick(@NotNull View view) {
                Intent intent = new Intent(PrivacyPolicyActivity.this, webViewActivity.class);
                intent.putExtra("webUrl", BaseConstans.PRIVACYPOLICY);
                startActivity(intent);
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
                Intent intent = new Intent(PrivacyPolicyActivity.this, webViewActivity.class);
                intent.putExtra("webUrl", BaseConstans.PROTOCOL);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint paint) {
                paint.setColor(Color.parseColor("#0092FE"));
                // 设置下划线 true显示、false不显示
                paint.setUnderlineText(false);
            }
        };

        spannableBuilder.setSpan(clickableSpanTwo, 163, 167, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableBuilder.setSpan(clickableSpanOne, 158, 162, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_policy.setMovementMethod(LinkMovementMethod.getInstance());
        tv_policy.setText(spannableBuilder);
    }

    protected void initAction() {


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @OnClick({R.id.tv_refuse, R.id.tv_agree})
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_refuse:
                callback(false);
                break;


            case R.id.tv_agree:
                callback(true);
                break;


        }
    }



    private void callback(boolean isAgree){
        Intent i = new Intent();
        i.putExtra("agree", isAgree);
        setResult(RESULT_CODE, i);
        finish();


    }


    @Override
    public final boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            callback(false);
        }
        return true;
    }
}
