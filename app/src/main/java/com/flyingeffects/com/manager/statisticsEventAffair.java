package com.flyingeffects.com.manager;

import android.content.Context;

import com.flyingeffects.com.utils.LogUtil;
import com.umeng.analytics.MobclickAgent;


public class statisticsEventAffair {

    static statisticsEventAffair instance;

    public static statisticsEventAffair getInstance() {
        if (instance == null) {
            instance = new statisticsEventAffair();
        }
        return instance;
    }


    public void setFlag(Context context, String tag, String title) {
        LogUtil.d("setFlag","title="+title);
        MobclickAgent.onEvent(context, tag, title);
//        StatService.onEvent(context, tag, title);
    }

    public void setFlag(Context context, String tag) {
        MobclickAgent.onEvent(context, tag);
    }



}
