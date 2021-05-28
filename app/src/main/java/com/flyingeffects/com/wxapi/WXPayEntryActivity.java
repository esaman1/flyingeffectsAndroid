package com.flyingeffects.com.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.flyingeffects.com.R;
import com.flyingeffects.com.utils.LogUtil;
import com.sweet.paylib.wechat.WechatPay;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_pay_result);

        api = WXAPIFactory.createWXAPI(this, "appid");
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
        WechatPay.getInstance().onResp(resp.errCode);
        LogUtil.e("onPayFinish, errCode = " + resp.errCode);
        LogUtil.e("onPayFinish, transaction = " + resp.transaction);
        LogUtil.e("onPayFinish, errStr = " + resp.errStr);
        LogUtil.e("onPayFinish, openId = " + resp.openId);
        finish();
    }
}
