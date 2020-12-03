package com.flyingeffects.com.bdopen;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bytedance.sdk.account.common.api.BDApiEventHandler;
import com.bytedance.sdk.account.common.constants.BDOpenConstants;
import com.bytedance.sdk.account.common.model.BaseReq;
import com.bytedance.sdk.account.common.model.BaseResp;
import com.bytedance.sdk.open.aweme.DYOpenConstants;
import com.bytedance.sdk.open.aweme.api.TiktokOpenApi;
import com.bytedance.sdk.open.aweme.impl.TikTokOpenApiFactory;
import com.bytedance.sdk.open.aweme.share.Share;
import com.flyingeffects.com.ui.view.activity.HomeMainActivity;
import com.flyingeffects.com.utils.ToastUtil;

import androidx.annotation.Nullable;


/**
 * @author ZhouGang
 * @date 2019/8/27
 * 抖音分享授权的接收回调的activity
 */
public class BdEntryActivity extends Activity implements BDApiEventHandler {
    TiktokOpenApi ttOpenApi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ttOpenApi= TikTokOpenApiFactory.create(this);
        ttOpenApi.handleIntent(getIntent(),this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == BDOpenConstants.ModeType.SEND_AUTH_RESPONSE) {
            // 授权成功可以获得authCode
        } else if (resp.getType() == DYOpenConstants.ModeType.SHARE_CONTENT_TO_DY_RESP) {
            Share.Response response = (Share.Response) resp;
            Log.d("DouYing"," code：" + response.errorCode + " 文案：" + response.errorMsg);
            if(response.errorCode==0){
                Intent intent = new Intent(this, HomeMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }

        // 非授权功能不用看这里
        // 这里需要启动一下自己的activity，因为抖音在授权之后，打开这个activity是singleInstance的，在单独的栈里，这个activity
        // finish的话，这个栈为空，系统会自动拉起抖音的栈，所以需要往自己的activity跳一次，跳回自己的栈
        finish();
    }

    @Override
    public void onErrorIntent(@Nullable Intent intent) {
        // 错误数据
        ToastUtil.showToast("检查您是否安装了抖音短视频App");
        Intent intent1 = new Intent(this, HomeMainActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent1);
        // 非授权不用看这里
        // 这里需要启动一下自己的activity，因为抖音在授权之后，打开这个activity是singleInstance的，在单独的栈里，这个activity
        // finish的话，这个栈为空，系统会自动拉起抖音的栈，所以需要往自己的activity跳一次，跳回自己的栈
//        startActivity(new Intent(this, ExportResultActivity.class));
        finish();
    }
}

