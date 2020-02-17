/*
 * File name: OnClickUtil.java
 * Copyright: Copyright (c) 2006-2013 hoperun Inc, All rights reserved
 * Description: <描述>
 * Author: shen_feng
 * Last modified date: 2013-10-15
 * Version: <版本编号>
 * Edit history: <修改内容><修改人>
 */
package com.mobile.CloudMovie.utils;

/***
 *user: 张sir ,@time: 2017/8/11
 *description:防止重复点击
 */
public class OnClickUtil {

    /**
     * 判断是否短时间多次操作
     * return true 是 没有超出规定操作间隔时间不可以操作 返回 false否 可以操作
     * see [类、类#方法、类#成员]
     */

    public static boolean isMostPost() {
        long time = System.currentTimeMillis();
        // 如果距离上次操作时间小于1秒就不做操作
        if (time - postTime < 0 || time - postTime > 400L) {
            postTime = time;
            return false;
        } else {
            return true;
        }

    }

    private static long postTime;
}
