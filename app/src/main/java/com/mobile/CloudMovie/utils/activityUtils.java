package com.mobile.CloudMovie.utils;

import android.app.ActivityManager;
import android.content.Context;

/**
 * 作者:Administrator on 2019/3/22 16:39
 * 邮箱:jutongzhang@sina.com
 */
public class activityUtils {



    /**
     * 判断某activity是否处于栈顶
     * true在栈顶 false不在栈顶
     */
    public  static  boolean isActivityTop(Class cls, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(cls.getName());
    }

}
