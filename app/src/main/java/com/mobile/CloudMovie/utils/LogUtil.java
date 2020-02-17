package com.mobile.CloudMovie.utils;

import android.util.Log;

import com.mobile.CloudMovie.constans.BaseConstans;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LogUtil {

    private static LogUtil instance;


    public static LogUtil getInstance() {
        if (instance == null) {
            instance = new LogUtil();
        }
        return instance;
    }

    public  enum LogLevel {
        DEBUG_LEVEL, // 调试级别 日志
        RELEASE_LEVEL; // 发布级别 日志
    }

    public static void d(String TAG, String msg) {
        if (logLevel == LogLevel.DEBUG_LEVEL) {
            Log.d(TAG, msg);
        }
    }

    public static void d(String TAG, String msg, Throwable tr) {

        if (logLevel == LogLevel.DEBUG_LEVEL) {
            Log.d(TAG, msg, tr);
        }
    }

    public static void e(String TAG, String msg, Throwable tr) {

        if (logLevel == LogLevel.DEBUG_LEVEL) {
            Log.e(TAG, msg, tr);
        }
    }

    public static void e(String TAG, String msg) {

        if (logLevel == LogLevel.DEBUG_LEVEL) {
            Log.e(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (logLevel == LogLevel.DEBUG_LEVEL) {
            Log.e("error", msg);
        }
    }

    public static void i(String TAG, String msg) {
        if (logLevel == LogLevel.DEBUG_LEVEL) {
            Log.i(TAG, msg);
        }

    }

    public static void i(String TAG, String msg, Throwable tr) {
        if (logLevel == LogLevel.DEBUG_LEVEL) {
            Log.i(TAG, msg, tr);
        }
    }

    public static void setLogLevel(LogLevel logLevel) {
        LogUtil.logLevel = logLevel;
    }

    public static void w(String TAG, String msg) {
        if (logLevel == LogLevel.DEBUG_LEVEL) {
            Log.w(TAG, msg);
        }
    }

    public static void w(String TAG, String msg, Throwable tr) {

        if (logLevel == LogLevel.DEBUG_LEVEL) {
            Log.w(TAG, msg, tr);
        }
    }


    public static void dLong(String str, String content) {
        if (logLevel == LogLevel.DEBUG_LEVEL) {
            int LOG_MAXLENGTH = 2000;
            //规定每段显示的长度
            int strLength = content.length();
            int start = 0;
            int end = LOG_MAXLENGTH;
            for (int i = 0; i < 100; i++) {
                //剩下的文本还是大于规定长度则继续重复截取并输出
                if (strLength > end) {
                    Log.d(str + i, content.substring(start, end));
                    start = end;
                    end = end + LOG_MAXLENGTH;
                } else {
                    Log.d(str, content.substring(start, strLength));
                    break;
                }
            }
        }
    }


    // 日志级别
    private static LogLevel logLevel = BaseConstans.PRODUCTION?LogLevel.RELEASE_LEVEL:LogLevel.DEBUG_LEVEL;




     static final String LINE_SEPARATOR = System.getProperty("line.separator");

     static void printLine(String tag, boolean isTop) {
        if (isTop) {
            Log.d(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════");
        } else {
            Log.d(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
        }
    }
    public static void printJson(String tag, String msg, String headString) {
        String message;
        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(4);//最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(4);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }

        printLine(tag, true);
        message = headString + LINE_SEPARATOR + message;
        String[] lines = message.split(LINE_SEPARATOR);
        for (String line : lines) {
            Log.d(tag, "║ " + line);
        }
        printLine(tag, false);
    }

}
