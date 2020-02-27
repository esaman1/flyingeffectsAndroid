package com.flyingeffects.com.constans;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.http.abc;
import com.flyingeffects.com.manager.SPHelper;
import com.flyingeffects.com.utils.ChannelUtil;
import com.flyingeffects.com.utils.OsUtils;
import com.flyingeffects.com.utils.StringUtil;

import java.util.HashMap;

/**
 * Created by 张sir
 * on 2017/8/14.
 */

public class BaseConstans {

    public static final boolean PRODUCTION = false;
    private static String channel = "";
    private static String versionCode = "";
    private static String uuid = "";


    public static HashMap getRequestHead(HashMap<String, String> map) {
        String nowTimestamp = getTimestamp() + "";
        map.put("app_id", "10000");
        map.put("platform", "android");
        map.put("channel", getChannel()); //getChannel()  test
        map.put("version", getVersionCode());
        map.put("timestamp", nowTimestamp);//getTimestamp()+""
        map.put("imei", getUuid());
        map.put("uuid", GetUserUuid());
        map.put("token", GetUserToken());
        map.put("sign", getSine(nowTimestamp, map));
        return map;
    }


    private static String getSine(String nowTimestamp, HashMap<String, String> map) {
        map.put("app_id", "10000");
        map.put("platform", "android");
        map.put("channel", getChannel());
        map.put("version", getVersionCode());
        map.put("uuid", GetUserUuid());
        map.put("token", GetUserToken());
        map.put("timestamp", nowTimestamp);//getTimestamp()+""
        map.put("imei", getUuid());
        return abc.sign(map);
    }


    public static String GetUserToken() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getString("token", "");
    }


    public static void SetUserToken(String token) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putString("token", token);
    }


    public static String GetUserUuid() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getString("uuid", "");
    }


    public static String getVersionCode() {
        if (versionCode.equals("")) {
            versionCode = StringUtil.getVersion(BaseApplication.getInstance());
            return versionCode;
        } else {
            return versionCode;
        }
    }


    public static String getUuid() {
        if (uuid.equals("")) {
            uuid = OsUtils.getAndroidID(BaseApplication.getInstance());
            return uuid;
        } else {
            return uuid;
        }
    }


    /**
     * description ：获得时间戳
     * date: ：2019/5/27 10:49
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public static long getTimestamp() {
        return System.currentTimeMillis();
    }

    public static String getChannel() {
        if (channel != null && !channel.equals("")) {
            return channel;
        } else {
            channel = ChannelUtil.getChannel(BaseApplication.getInstance());
            return channel;
        }
    }


}
