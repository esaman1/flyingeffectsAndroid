package com.flyingeffects.com.ui.model;


import android.util.Log;

import com.flyingeffects.com.utils.LogUtil;
import com.orhanobut.hawk.Hawk;

import java.util.Calendar;


/**
 * description ：显示好评弹窗规则，规则1：如果是新用户（保存数量小于5）,用户已提交过好评，多次关闭好评弹出(>=3次)，当天有过弹窗
 * creation date: 2020/9/2
 * user : zhangtongju
 */
public class ShowPraiseModel {


    /**
     * description ：判断是不是新用户，保存数量小于5次的
     * creation date: 2020/9/2
     * user : zhangtongju
     */
    public static boolean getIsNewUser() {
        int num = 0;
        try {
            num = Hawk.get("keepAlbumNum");
        } catch (Exception e) {
            LogUtil.d("OOM", e.getMessage());
        }
        LogUtil.d("OOM","保存了次数"+num);

        return num < 5;
    }

    public static void keepAlbumCount() {

        int num = 0;
        try {
            num = Hawk.get("keepAlbumNum");
        } catch (Exception e) {
            LogUtil.d("OOM", e.getMessage());
        } finally {
            num++;
            Hawk.put("keepAlbumNum", num);
        }


    }


    /**
     * description ：统计关闭好评弹窗次数
     * creation date: 2020/9/3
     * user : zhangtongju
     */
    public static void statisticsCloseNum() {
        int num = 0;
        try {
            num = Hawk.get("statisticsCloseNum");
        } catch (Exception e) {
            LogUtil.d("OOM", e.getMessage());
        } finally {
            num=num+1;
            Hawk.put("statisticsCloseNum", num);
        }


    }


    /**
     * 关闭三次后就不显示弹窗
     */
    public static boolean canShowAlert() {
        int num = 0;
        try {
            num = Hawk.get("statisticsCloseNum");
        } catch (Exception e) {
            LogUtil.d("OOM", e.getMessage());
        }

        LogUtil.d("OOM","关闭了弹出的数量"+num);
        return num < 3;
    }


    /**
     * 当日已经有了弹窗，那么不显示弹窗
     */
    public static boolean ToDayHasShowAd() {
        try {
            long showAdAlertTime = Hawk.get("showAdAlertTime");
            boolean hasShow = isSameDate(showAdAlertTime);
            LogUtil.d("OOM", "好评弹窗是否已经今天显示过一次了" + hasShow);
            if (hasShow) {
                return true;
            } else {
                Hawk.put("showAdAlertTime", System.currentTimeMillis());
                return false;
            }
        } catch (Exception e) {
            LogUtil.d("OOM", "好评弹窗是否已经今天显示过一次了=否" );
            Hawk.put("showAdAlertTime", System.currentTimeMillis());
            return false;
        }

    }


    /**
     * description ：判断传入时间和当前时间相比
     * 是否还是同一天，true 同一天
     * creation date: 2020/9/3
     * user : zhangtongju
     */
    private static boolean isSameDate(long timestamp) {
        //当前时间
        Calendar currentTime = Calendar.getInstance();
        //要转换的时间
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(timestamp);
        //年相同
        if (currentTime.get(Calendar.YEAR) == time.get(Calendar.YEAR)) {
            //获取一年中的第几天并相减，取差值
            return currentTime.get(Calendar.DAY_OF_YEAR) - time.get(Calendar.DAY_OF_YEAR) == 0;
        } else {
            return false;
        }
    }


    public static void setHasComment() {
        Hawk.put("setHasComment", 1);
    }


    public static boolean getHasComment() {
        int num = 0;
        try {
            num = Hawk.get("setHasComment");
        } catch (Exception e) {
            LogUtil.d("OOM", e.getMessage());
        }

        LogUtil.d("OOM","评论的次数为"+num);

        return num != 0;
    }


}
