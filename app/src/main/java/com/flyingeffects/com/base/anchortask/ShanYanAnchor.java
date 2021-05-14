package com.flyingeffects.com.base.anchortask;

import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.utils.ChannelUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.xj.anchortask.library.AnchorTask;

import cn.nt.lib.analytics.NTAnalytics;

public class ShanYanAnchor extends AnchorTask {

    public ShanYanAnchor() {
        super(TaskNameConstants.INIT_SHAN_YAN);
    }

    @Override
    public void run() {
        OneKeyLoginManager.getInstance().init(BaseApplication.getInstance(), "SSjHAvIf", (code, result) -> {
            //闪验SDK初始化结果回调
            LogUtil.d("OOM", "初始化： code==" + code + "   result==" + result);
        });
    }
}
