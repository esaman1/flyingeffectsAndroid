package com.flyingeffects.com.base.anchortask;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.utils.ChannelUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.shixing.sxvideoengine.License;
import com.shixing.sxvideoengine.SXLog;
import com.xj.anchortask.library.AnchorTask;

import cn.nt.lib.analytics.NTAnalytics;

public class ZtAnchor extends AnchorTask {

    public ZtAnchor() {
        super(TaskNameConstants.INIT_ZT);
    }

    @Override
    public void run() {
        NTAnalytics.setDebug(false);
        NTAnalytics.init(BaseApplication.getInstance(), "87", "vQlTNPzHOzBYHzkg", ChannelUtil.getChannel(BaseApplication.getInstance()));
    }
}
