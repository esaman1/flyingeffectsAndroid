package com.flyingeffects.com.utils;

import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.entity.UserInfo;
import com.orhanobut.hawk.Hawk;
import com.xj.anchortask.library.log.LogUtils;

import java.util.Date;
import java.util.TimeZone;

/**
 * 检查是否为vip或是否显示广告的工具类
 *
 * @author vidya
 */
public class CheckVipOrAdUtils {
    public static final int IS_VIP = 1;
    public static final int IS_NOT_VIP = 0;

    public static final int VIP_GRADE_MONTH = 1;
    public static final int VIP_GRADE_YEAR = 2;
    public static final int VIP_GRADE_FOREVER = 3;

    private static final String TAG = "CheckVipOrAdUtils";

    /**
     * 检查是否为vip
     */
    public static boolean checkIsVip() {
        UserInfo userInfo = Hawk.get(UserInfo.USER_INFO_KEY);
        if (BaseConstans.hasLogin() && userInfo != null) {
            return checkVipWithTime(userInfo.getVip_end_time()
                    , userInfo.getIs_vip());
        } else {
            return false;
        }
    }


    public static boolean checkVipWithTime(long vipEndDate, int isVip) {
        if (vipEndDate != 0) {
            LogUtil.d(TAG, "vipdate = " + vipEndDate + " isVip = " + isVip);
            vipEndDate = vipEndDate * 1000;
            LogUtils.d(TAG, "dateLong = " + vipEndDate + " isVip = " + isVip);
            return System.currentTimeMillis() < vipEndDate && IS_VIP == isVip;
        } else {
            return false;
        }
    }

    /**
     * 检查是否显示悬浮窗
     */
    public static boolean checkFloatWindowShow() {
        Date date = new Date();
        boolean isSameDay = TimeUtils.isSameDay(date.getTime(), BaseConstans.getVipFloatWindowShowTime(), TimeZone.getDefault());
        if (!isSameDay && BaseConstans.getVipFloatWindowConfigShowTimes() != 0) {
            BaseConstans.setVipFloatWindowShowTime(date.getTime());
            BaseConstans.setVipFloatWindowShowTimes(0);
            return true;
        } else {
            return BaseConstans.getVipFloatWindowShowTimes() < BaseConstans.getVipFloatWindowConfigShowTimes();
        }
    }

}
