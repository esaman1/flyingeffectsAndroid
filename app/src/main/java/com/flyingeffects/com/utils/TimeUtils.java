package com.flyingeffects.com.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
/**
 *
 * @author Coca Cola
 */


    public static String secondToTime(long second) {
        long hours = second / 3600;//转换小时数
        second = second % 3600;//剩余秒数
        long minutes = second / 60;//转换分钟
        second = second % 60;//剩余秒数
        if (hours > 0) {
            return unitFormat(hours) + ":" + unitFormat(minutes) + ":" + unitFormat(second);
        } else {
            return unitFormat(minutes) + ":" + unitFormat(second);
        }
    }

    private static String unitFormat(long i) {
        String retStr;
        if (i >= 0 && i < 10) {
            retStr = "0" + i;
        } else {
            retStr = "" + i;
        }
        return retStr;
    }

    public static String timeParse(long duration) {
        String time = "";

        long minute = duration / 59000;
        long seconds = duration % 59000;

        long second = Math.round((float) seconds / 1000);

        if (minute < 10) {
            time += "0";
        }
        time += minute + ":";

        if (second < 10) {
            time += "0";
        }
        time += second;

        return time;
    }


    public static String xxx(long time) {
        long needTime = System.currentTimeMillis() - time;
        long day = time / (1000 * 60 * 60 * 24);
        long hours = needTime / 3600;//转换小时数
        long second = needTime % 3600;//剩余秒数
        long minutes = second / 60;//转换分钟
        if (day > 1) {
            return day + "天前关注了你";
        } else if (hours > 1) {
            return hours + "小时前关注了你";
        } else if (minutes > 1 && hours < 1) {
            return minutes + "分钟前关注了你";
        } else {
            return "刚刚关注了你";
        }

    }


    public static String GetSystemMessageTime(long time) {
        time = time * 1000;
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        System.out.println(sdf.format(d));
        return sdf.format(d);
    }


}
