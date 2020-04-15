package com.flyingeffects.com.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
    // 日期格式
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_YYYY_MM = "yyyy-MM";
    public static final String FORMAT_YYYY = "yyyy";
    public static final String FORMAT_HH_MM = "HH:mm";
    public static final String FORMAT_HH_MM_SS = "HH:mm:ss";
    public static final String FORMAT_MM_SS = "mm:ss";
    public static final String FORMAT_MM_DD_HH_MM = "MM-dd HH:mm";
    public static final String FORMAT_MM_DD_HH_MM_SS = "MM-dd HH:mm:ss";
    public static final String FORMAT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_YYYY2MM2DD = "yyyy.MM.dd";
    public static final String FORMAT_YYYY2MM2DD_HH_MM = "yyyy.MM.dd HH:mm";
    public static final String FORMAT_MMCDD_HH_MM = "MM月dd日 HH:mm";
    public static final String FORMAT_MMCDD = "MM月dd日";
    public static final String FORMAT_YYYYCMMCDD = "yyyy年MM月dd日";

    public static final long ONE_DAY = 1000 * 60 * 60 * 24;

    //判断选择的日期是否是本周（分从周日开始和从周一开始两种方式）
    public static boolean isThisWeek(Date time) {
//        //周日开始计算
//      Calendar calendar = Calendar.getInstance();

        //周一开始计算
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);

        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);

        calendar.setTime(time);

        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);

        if (paramWeek == currentWeek) {
            return true;
        }
        return false;
    }

    //判断选择的日期是否是今天
    public static boolean isToday(Date time) {
        return isThisTime(time, "yyyy-MM-dd");
    }

    //判断选择的日期是否是本月
    public static boolean isThisMonth(Date time) {
        return isThisTime(time, "yyyy-MM");
    }

    //判断选择的日期是否是本月
    public static boolean isThisYear(Date time) {
        return isThisTime(time, "yyyy");
    }

    //判断选择的日期是否是昨天
    public static boolean isYesterDay(Date time) {
        Calendar cal = Calendar.getInstance();
        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        if ((ct - lt) == 1) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isThisTime(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        String param = sdf.format(date);//参数时间

        String now = sdf.format(new Date());//当前时间

        if (param.equals(now)) {
            return true;
        }
        return false;
    }


    /**
     * description ：获得秒
     * date: ：2019/11/5 15:26
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public static String getCurrentTime_m(long time) {
        Date d = new Date();
        d.setTime(time);
        SimpleDateFormat format = new SimpleDateFormat("ss", Locale.getDefault());
        return format.format(d);
    }

    /**
     * 获取某年某月有多少天
     */
    public static int getDayOfMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, 0); //输入类型为int类型
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取两个时间相差的天数
     *
     * @return time1 - time2相差的天数
     */
    public static int getDayOffset(long time1, long time2) {
        // 将小的时间置为当天的0点
        long offsetTime;
        if (time1 > time2) {
            offsetTime = time1 - getDayStartTime(getCalendar(time2)).getTimeInMillis();
        } else {
            offsetTime = getDayStartTime(getCalendar(time1)).getTimeInMillis() - time2;
        }
        return (int) (offsetTime / ONE_DAY);
    }

    public static Calendar getCalendar(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar;
    }

    public static Calendar getDayStartTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static String getDurationInString(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss", Locale.CHINA);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String nowTime = dateFormat.format(new Date(time));
        if(nowTime.equals("00:00")){
            nowTime="00:01";
        }
        return  nowTime;






//        String durStr = "";
//        if (time == 0) {
//            return "00:00";
//        }
//        time = time / 1000;
//        long hour = time / (60 * 60);
//        time = time - (60 * 60) * hour;
//        long min = time / 60;
//        time = time - 60 * min;
//        long sec = time;
//
//
//
//
//        if (hour != 0) {
//            if(hour<10){
//                durStr ="0"+ hour + ":" + min + ":" + sec + "";
//            }else{
//                durStr = hour + ":" + min + ":" + sec + "";
//            }
//
//        } else if (min != 0) {
//
//
//            if(min<10){
//                durStr ="0"+ min + ":" + sec + "";
//            }else{
//                durStr = min + ":" + sec + "";
//            }
//
//
//        } else {
//           // durStr = sec + "秒";
//
//            durStr = "00:"+sec + "";
//        }
//        return durStr;
    }

    /**
     * 获取当前时间是星期几
     *
     * @param dt
     * @return
     */
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日","星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();

        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK)-1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }


//    /**
//     * 将日期格式的字符串转换为长整型
//     *
//     * @param date
//     * @param format
//     * @return
//     */
//    public static long convertToLong(String date, String format) {
//        try {
//            if (!StringUtils.isEmpty(date)) {
//                if (TextUtils.isEmpty(format)) {
//                    format = TIME_FORMAT;
//                }
//                SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
//                return formatter.parse(date).getTime();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }


    /**
     * 将长整型数字转换为日期格式的字符串
     *
     * @param time
     * @return
     */
    public static String convertToString(long time) {
        if (time > 0) {
            SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
            Date date = new Date(time);
            return formatter.format(date);
        }
        return "";

    }



    /**
     * description ：当前时间缓存年月日
     * date: ：2019/10/21 11:32
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public static String convertToData(String  time ) {
        long longTime=Long.parseLong(time)*1000L;
        Date date = new Date(longTime);
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd");
        return sDateFormat.format(date);



    }


}