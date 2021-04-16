package com.flyingeffects.com.base.anchortask;

import androidx.multidex.MultiDex;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.utils.ChannelUtil;
import com.umeng.commonsdk.UMConfigure;
import com.xj.anchortask.library.AnchorTask;

public class MultiAnchor extends AnchorTask {

    public MultiAnchor() {
        super(TaskNameConstants.MULTI_DEX);
    }

    @Override
    public void run() {
        //分包支持
        MultiDex.install(BaseApplication.getInstance());
    }
}
