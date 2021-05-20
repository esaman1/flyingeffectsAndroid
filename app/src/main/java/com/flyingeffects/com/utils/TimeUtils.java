package com.flyingeffects.com.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {
    /**
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

    static String dayNames[] = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    public static String getNewChatTime(long timesamp) {
        timesamp = timesamp * 1000;
        String result = "";
        Calendar todayCalendar = Calendar.getInstance();
        Calendar otherCalendar = Calendar.getInstance();
        otherCalendar.setTimeInMillis(timesamp);

        String timeFormat = "M月d日 HH:mm";
        String yearTimeFormat = "yyyy年M月d日 HH:mm";
        String am_pm = "";
        int hour = otherCalendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 0 && hour < 6) {
            am_pm = "凌晨";
        } else if (hour >= 6 && hour < 12) {
            am_pm = "早上";
        } else if (hour == 12) {
            am_pm = "中午";
        } else if (hour > 12 && hour < 18) {
            am_pm = "下午";
        } else if (hour >= 18) {
            am_pm = "晚上";
        }
        timeFormat = "M月d日 " + am_pm + "HH:mm";
        yearTimeFormat = "yyyy年M月d日 " + am_pm + "HH:mm";

        boolean yearTemp = todayCalendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR);
        if (yearTemp) {
            int todayMonth = todayCalendar.get(Calendar.MONTH);
            int otherMonth = otherCalendar.get(Calendar.MONTH);
            //表示是同一个月
            if (todayMonth == otherMonth) {
                int temp = todayCalendar.get(Calendar.DATE) - otherCalendar.get(Calendar.DATE);
                switch (temp) {
                    case 0:
                        result = getHourAndMin(timesamp);
                        break;
                    case 1:
                        result = "昨天 " + getHourAndMin(timesamp);
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        int dayOfMonth = otherCalendar.get(Calendar.WEEK_OF_MONTH);
                        int todayOfMonth = todayCalendar.get(Calendar.WEEK_OF_MONTH);
                        //表示是同一周
                        if (dayOfMonth == todayOfMonth) {
                            int dayOfWeek = otherCalendar.get(Calendar.DAY_OF_WEEK);
                            //判断当前是不是星期日     如想显示为：周日 12:09 可去掉此判断
                            if (dayOfWeek != 1) {
                                result = dayNames[otherCalendar.get(Calendar.DAY_OF_WEEK) - 1] + getHourAndMin(timesamp);
                            } else {
                                result = getTime(timesamp, timeFormat);
                            }
                        } else {
                            result = getTime(timesamp, timeFormat);
                        }
                        break;
                    default:
                        result = getTime(timesamp, timeFormat);
                        break;
                }
            } else {
                result = getTime(timesamp, timeFormat);
            }
        } else {
            result = getYearTime(timesamp, yearTimeFormat);
        }
        return result;
    }

    /**
     * 当天的显示时间格式
     *
     * @param time
     * @return
     */
    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }

    /**
     * 不同一周的显示时间格式
     *
     * @param time
     * @param timeFormat
     * @return
     */
    public static String getTime(long time, String timeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        return format.format(new Date(time));
    }

    /**
     * 不同年的显示时间格式
     *
     * @param time
     * @param yearTimeFormat
     * @return
     */
    public static String getYearTime(long time, String yearTimeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(yearTimeFormat);
        return format.format(new Date(time));

    }

    public static long millis2Days(long millis, TimeZone timeZone) {
        return (((long) timeZone.getOffset(millis)) + millis) / 86400000;
    }

    public static String formatTheDate(String dateStr) {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

//        Date date1 = new Date();
//        LogUtils.d("date", "date1 = " + date1.getTime());
//        String dateReturn = simpleDateFormat.format(date);
//        LogUtils.d("date", "dateReturn = " + dateReturn);
//        return dateReturn;

        long dateLong = Long.parseLong(dateStr);
        dateLong = dateLong * 1000;
        return getDateTime(dateLong);
    }

    public static String formatTheDate(long date) {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

//        Date date1 = new Date();
//        LogUtils.d("date", "date1 = " + date1.getTime());
//        String dateReturn = simpleDateFormat.format(date);
//        LogUtils.d("date", "dateReturn = " + dateReturn);
//        return dateReturn;

        long dateLong = date * 1000;
        return getDateTime(dateLong);
    }

    public static String formatTheDateHour(String dateStr) {
        long dateLong = Long.parseLong(dateStr);
        dateLong = dateLong * 1000;
        return getDateTimeHour(dateLong);
    }

    public static String formatTheDateHour(long date) {
        long dateLong = date * 1000;
        return getDateTimeHour(dateLong);
    }


    /**
     * long转换成字符串日期
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    private static String getDateTime(long longTime) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateToString(new Date(longTime), dateFormat);
    }

    /**
     * long转换成字符串日期
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    private static String getDateTimeHour(long longTime) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return dateToString(new Date(longTime), dateFormat);
    }

    /**
     * 日期类型转换成字符串类型
     *
     * @param date       日期
     * @param dateFormat 日期格式
     * @return 日期字符串
     */
    private static String dateToString(Date date, DateFormat dateFormat) {
        return dateFormat.format(date);
    }

    /**
     * 判断两个时间戳是否为同一天
     *
     * @param millis1
     * @param millis2
     * @param timeZone
     * @return
     */
    public static boolean isSameDay(long millis1, long millis2, TimeZone timeZone) {
        long interval = millis1 - millis2;
        return interval < 86400000 && interval > -86400000 && millis2Days(millis1, timeZone) == millis2Days(millis2, timeZone);
    }

}
