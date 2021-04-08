package com.flyingeffects.com.base.anchortask;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.TTAdManagerHolder;
import com.flyingeffects.com.utils.ChannelUtil;
import com.flyingeffects.com.utils.SystemUtil;
import com.nineton.ntadsdk.NTAdConfig;
import com.nineton.ntadsdk.NTAdSDK;
import com.xj.anchortask.library.AnchorTask;

public class TtAdAnchor extends AnchorTask {

    public TtAdAnchor() {
        super(TaskNameConstants.INIT_TTAD);
    }

    @Override
    public boolean isRunOnMainThread() {
        return true;
    }

    @Override
    public void run() {
        TTAdManagerHolder.init(BaseApplication.getInstance());
    }
}
