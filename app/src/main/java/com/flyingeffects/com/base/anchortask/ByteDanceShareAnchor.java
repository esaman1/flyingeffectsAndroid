package com.flyingeffects.com.base.anchortask;

import com.bytedance.sdk.open.douyin.DouYinOpenApiFactory;
import com.bytedance.sdk.open.douyin.DouYinOpenConfig;
import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.utils.LogUtil;
import com.xj.anchortask.library.AnchorTask;

public class ByteDanceShareAnchor extends AnchorTask {

    public ByteDanceShareAnchor() {
        super(TaskNameConstants.INIT_BYTE_DANCE_SHARE);
    }

    @Override
    public void run() {
        //抖音分享
        DouYinOpenApiFactory.init(new DouYinOpenConfig(BaseConstans.DOUYINSHARE_CLIENTKEY));
    }
}
