package com.flyingeffects.com.base.anchortask;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.utils.LogUtil;
import com.shixing.sxvideoengine.License;
import com.shixing.sxvideoengine.SXLog;
import com.xj.anchortask.library.AnchorTask;

import cn.jpush.android.api.JPushInterface;

public class JPushAnchor extends AnchorTask {

    public JPushAnchor() {
        super(TaskNameConstants.INIT_JPUSH);
    }

    @Override
    public void run() {
        JPushInterface.setDebugMode(BaseConstans.PRODUCTION);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(BaseApplication.getInstance());
    }
}
