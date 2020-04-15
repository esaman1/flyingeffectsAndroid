package com.flyingeffects.com.constans;

import android.text.TextUtils;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.enity.ConfigForTemplateList;
import com.flyingeffects.com.http.abc;
import com.flyingeffects.com.manager.SPHelper;
import com.flyingeffects.com.utils.ChannelUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.OsUtils;
import com.flyingeffects.com.utils.StringUtil;

import java.util.HashMap;

/**
 * Created by 张sir
 * on 2017/8/14.
 */

public class BaseConstans {
    public static final String UMENGAPPID = "5e5c68a2570df3d6930002b4";
    //当前抠图是用sdk 还是用服务器
    public static final boolean UserFaceSdk=true;
    public static final int  THREADCOUNT=4;
    public static String titok;
    public static String kuaishou;
    public static final boolean PRODUCTION = false;
    private static String channel = "";
    private static String versionCode = "";
    private static String uuid = "";
    public static final String PRIVACYPOLICY = "http://copy-book.oss-cn-hangzhou.aliyuncs.com/link/FeiShan/FS-PrivacyPolicy.html";
    public static String service_wxi;
    public static ConfigForTemplateList configList;
    private static int hasAdvertising = 1;  //是否有广告，0表示没得，1表示有，全局控制

    public static final String PROTOCOL = "http://copy-book.oss-cn-hangzhou.aliyuncs.com/link/FeiShan/FS-Agreement.html";

    public static HashMap getRequestHead(HashMap<String, String> map) {
        String nowTimestamp = getTimestamp() + "";
        map.put("app_id", "10000");
        map.put("platform", "android");
        map.put("channel", getChannel()); //getChannel()  test
        map.put("version", getVersionCode());
        map.put("timestamp", nowTimestamp);//getTimestamp()+""
        LogUtil.d("OOM","请求的token=="+ GetUserToken());
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
        map.put("timestamp", nowTimestamp);//getTimestamp()+""
        map.put("imei", getUuid());
        map.put("uuid", GetUserUuid());
        map.put("token", GetUserToken());
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


    public static String GetUserId() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getString("userId", "");
    }


    public static void SetUserId(String id) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putString("userId", id);
    }



    public static boolean hasLogin() {
        return GetUserToken() != null && !GetUserToken().equals("");
    }



    static String GetUserUuid() {
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

    public static boolean isFirstClickUseApp() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getBoolean("isFirstUseApp", true);
    }

    public static void setFirstClickUseApp() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putBoolean("isFirstUseApp", false);
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



    public static String getService_wxi() {
        if (!TextUtils.isEmpty(service_wxi)) {
            return service_wxi;
        } else {
            return "wordcq520";
        }
    }

    public static String gettitokOrKuaishou(boolean isTiktok) {
        if (isTiktok) {
            if (titok != null && !titok.equals("")) {
                return titok;
            } else {
                return "http://v.douyin.com/B62HrT/";
            }
        } else {
            if (kuaishou != null && !kuaishou.equals("")) {
                return kuaishou;
            } else {
                return "看了这么多快手，还是「卡点哥哥」最好玩了！ http://m.gifshow.com/s/xEna7Voi 复制此链接，打开【快手】直接观看！";
            }
        }
    }



    public static int getHasAdvertising() {
        if (hasAdvertising == 0) {
            SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
            hasAdvertising = spUtil.getInt("AdvertisingNum", 0);
            return hasAdvertising;
        } else {
            return hasAdvertising;
        }

    }

    public static void setHasAdvertising(int num) {
        hasAdvertising = num;
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putInt("AdvertisingNum", num);
    }

}
