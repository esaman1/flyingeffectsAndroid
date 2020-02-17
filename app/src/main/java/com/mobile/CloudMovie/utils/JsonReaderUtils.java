package com.mobile.CloudMovie.utils;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;

import com.lingenliu.util.JsonReader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by Lenovo on 2017/7/26.
 */

public class JsonReaderUtils {
    static JsonReader mJsonReader;

    public static JsonReader getInstance() {
        if (mJsonReader == null) {
            mJsonReader = JsonReader.sharedInstance();
        }
        return mJsonReader;
    }
    /**
     * 获取⼀一个json中的 KEY 的值，指定返回值为string类型
     * */
    public String stringForKey(String key){
        return mJsonReader.stringForKey(key);
    }
    /**
     * 获取⼀一个json中的 KEY 的值，指定返回值为Bool类型
     * */
    public boolean boolForKey(String key){
        return mJsonReader.boolForKey(key);
    }
    /**
     * 获取⼀一个json中的 KEY 的值，指定返回值为int类型
     * */
    public int intForKey(String key){
        return mJsonReader.intForKey(key);
    }
    /**
     * 获取⼀一个json中的 KEY 的值，指定返回值为float类型
     * */
    public float floatForKey(String key){
        return mJsonReader.floatForKey(key);
    }
    /**
     * 获取⼀一个json中的 KEY 的值，指定返回值为long类型
     * */
    public long longForKey(String key){
        return mJsonReader.longForKey(key);
    }
    /**
     * 获取⼀一个json中的 KEY 的值，指定返回值为Map
     * */
    public Map<String,Object> mapForKey(String key){
        return mJsonReader.mapForKey(key);
    }
    /**
     * 获取⼀一个json中的 多级 KEY 的值，指定返回值为String类型
     * */
    public String stringForKeys(String... keys){
        return mJsonReader.stringForKeys(keys);
    }
    /**
     * 获取⼀一个json中的 多级 KEY 的值，指定返回值为Bool类型
     * */
    public boolean boolForKeys(String... keys){
        return mJsonReader.boolForKeys(keys);
    }
    /**
     * 获取⼀一个json中的 多级 KEY 的值，指定返回值为int类型
     * */
    public int intForKeys(String... keys){
        return mJsonReader.intForKeys(keys);
    }
    /**
     * 获取⼀一个json中的 多级 KEY 的值，指定返回值为float类型
     * */
    public float floatForKeys(String... keys){
        return mJsonReader.floatForKeys(keys);
    }
    /**
     * 获取⼀一个json中的 多级 KEY 的值，指定返回值为long类型
     * */
    public long longForKeys(String... keys){
        return mJsonReader.longForKeys(keys);
    }
    /**
     * 获取⼀一个json中的 多级 KEY 的值，指定返回值为map类型
     * */
    public Map<String,Object> mapForKeys(String... keys){
        return mJsonReader.mapForKeys(keys);
    }


    public static boolean checkFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                Class cls = Class.forName("android.content.Context");
                Field declaredField = cls.getDeclaredField("APP_OPS_SERVICE");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(cls);
                if (!(obj instanceof String)) {
                    return false;
                }
                String str2 = (String) obj;
                obj = cls.getMethod("getSystemService", String.class).invoke(context, str2);
                cls = Class.forName("android.app.AppOpsManager");
                Field declaredField2 = cls.getDeclaredField("MODE_ALLOWED");
                declaredField2.setAccessible(true);
                Method checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                int result = (Integer) checkOp.invoke(obj, 24, Binder.getCallingUid(), context.getPackageName());
                return result == declaredField2.getInt(cls);
            } catch (Exception e) {
                return false;
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                if (appOpsMgr == null)
                    return false;
                int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), context
                        .getPackageName());
                return mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED;
            } else {
                return Settings.canDrawOverlays(context);
            }
        }
    }

}
