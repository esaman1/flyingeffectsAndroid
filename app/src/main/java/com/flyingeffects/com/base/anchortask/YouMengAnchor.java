package com.flyingeffects.com.base.anchortask;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.utils.ChannelUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.umeng.commonsdk.UMConfigure;
import com.xj.anchortask.library.AnchorTask;

public class YouMengAnchor extends AnchorTask {
    private static final String TAG = "YouMengAnchor";

    public YouMengAnchor() {
        super(TaskNameConstants.INIT_YOU_MENG);
    }

    @Override
    public void run() {
        UMConfigure.preInit(BaseApplication.getInstance(),
                BaseConstans.UMENGAPPID, ChannelUtil.getChannel(BaseApplication.getInstance()));
        LogUtil.d(TAG, "YouMengAnchor finish " + System.currentTimeMillis());
    }

}
