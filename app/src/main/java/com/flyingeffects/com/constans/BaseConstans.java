package com.flyingeffects.com.constans;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
//    public static final boolean isTitokChannel=true;
    public static final int  THREADCOUNT=4;
    public static boolean hasCreatingSegJni = true;
    public static String titok;
    public static String kuaishou;
    public static final boolean PRODUCTION = false;
    private static String channel = "";
    private static String versionCode = "";
    private static String uuid = "";
    public static final String PRIVACYPOLICY = "http://copy-book.oss-cn-hangzhou.aliyuncs.com/link/FeiShan/FS-PrivacyPolicy.html";
    public static String service_wxi;
    public static boolean TemplateHasWatchingAd=false;
    public static ConfigForTemplateList configList;
    private static int hasAdvertising = 0;  //是否有广告，0表示没得，1表示有，全局控制
    public static int showAgainKaipingAd = 60; //退出后台后多少秒后会重新显示插屏
    private static boolean isNewUserForAdvertising = false; //只是用前几次的新用户
    public static final String PROTOCOL = "http://copy-book.oss-cn-hangzhou.aliyuncs.com/link/FeiShan/FS-Agreement.html";

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


    public static void SetUserId(String id, String userName, String headUrl) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putString("userId", id);
        spUtil.putString("userName", userName);
        spUtil.putString("headUrl", headUrl);
    }


    public static String NickName() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getString("userName", "");
    }


    public static String headUrl() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getString("headUrl", "");
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
//        if(isTitokChannel){
//            return 1;
//        }else{
            if (hasAdvertising == 0) {
                SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
                hasAdvertising = spUtil.getInt("AdvertisingNum", 0);
                return hasAdvertising;
            } else {
                return hasAdvertising;
            }
//        }
    }

    public static void setHasAdvertising(int num) {
        hasAdvertising = num;
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putInt("AdvertisingNum", num);
    }


    /**
     * description :保存的时候是否显示激励视频
     * creation date: 2020/6/11
     * user : zhangtongju
     */
    public static void setIncentiveVideo(boolean isShowIncentiveVideo) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putBoolean("IncentiveVideo", isShowIncentiveVideo);
    }

    /**
     * description :得到保存的时候是弹出激励视频还是弹出，true 为激励视频
     * creation date: 2020/6/11
     * user : zhangtongju
     */
    public static boolean getIncentiveVideo() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getBoolean("IncentiveVideo", true);
    }


    /**
     * description :保存的时候是否显示激励视频2,和IncentiveVideo 一起，只針對保存頁面
     * creation date: 2020/6/11
     * user : zhangtongju
     */
    public static void setSave_video_ad(boolean isShowIncentiveVideo) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putBoolean("save_video_ad", isShowIncentiveVideo);
    }

    public static boolean getSave_video_ad() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getBoolean("save_video_ad", true);
    }


    public static boolean getIsNewUser() {
//        if(isTitokChannel){
//            return false;
//        }else{
            if (!isNewUserForAdvertising) {
                SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
                isNewUserForAdvertising = spUtil.getBoolean("isNewUserForAdvertising", false);
                return isNewUserForAdvertising;
            }
            return true;
//        }

    }

    public static void setIsNewUser(boolean newUser) {
        isNewUserForAdvertising = newUser;
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putBoolean("isNewUserForAdvertising", newUser);
    }


    public static boolean getNextIsNewUser() {
            SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
            isNewUserForAdvertising = spUtil.getBoolean("isNewNextUserForAdvertising", false);
            return isNewUserForAdvertising;

    }

    public static void setNextNewUser(boolean newUser) {
        isNewUserForAdvertising = newUser;
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putBoolean("isNewNextUserForAdvertising", newUser);
    }



    public static void setKaiPingADTimeOut(int time) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putInt("KaiPingADTimeOut", time);
    }


    public static void setMaxuploadTime(int time) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putInt("setMaxuploadTime", time);
    }


    public static int getMaxuploadTime() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getInt("setMaxuploadTime", 60);
    }

    public static int getKaiPingADTimeOut() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getInt("KaiPingADTimeOut", 5000);
    }



    public static String getminapp_share_title() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getString("minapp_share_title","我也会了！用飞闪就能制作：");
    }

    public static void setminapp_share_title(String str) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
         spUtil.putString("minapp_share_title", str);
    }




    public static boolean isFirstOpenApp() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getBoolean("isFirstOpen", true);
    }



    public static boolean isFirstUseDownAndUpAct() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getBoolean("isFirstUseDownAndUp", true);
    }


    public static void setOddNum() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        boolean oddNum=spUtil.getBoolean("oddNum",true);
        spUtil.putBoolean("oddNum", !oddNum);
    }

    public static boolean getOddNum() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
       return spUtil.getBoolean("oddNum", true);
    }




    public static void setFirstUseDownAndUpAct() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putBoolean("isFirstUseDownAndUp",false);
    }



    public static void setFirstOpenApp(long time) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putBoolean("isFirstOpen", false);
        spUtil.putLong("lastShowAdvertisingTime", time);
    }

    //设置打开app的次数
    public static void setOpenAppNum(int num) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putInt("OpenAppNum", num);
    }


    //得到打开app的次数
    public static int getOpenAppNum() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getInt("OpenAppNum", 0);
    }

    //启动APP多少秒后显示插屏广告
    public static void setInterstitial(int num) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putInt("interstitial", num);
    }


    //启动APP多少秒后显示插屏广告
    public static int getInterstitial() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getInt("interstitial", 5);
    }


    public static int getAppVersion() {
        try {
            PackageInfo info = BaseApplication.getInstance().getPackageManager().getPackageInfo(BaseApplication.getInstance().getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }


    /**
     * description ：设置抖音列表，上限和下限
     * creation date: 2020/7/6
     * user : zhangtongju
     */
    public static void setFeedShowPositionNum(String str) {
        String[] data = str.split(",");
        String minNum = data[0];
        String MaxNum = data[1];
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putInt("feedMaxNum", Integer.parseInt(MaxNum));
        spUtil.putInt("feedMinNum", Integer.parseInt(minNum));
    }


    /**
     * description ：获得仿抖音页面随机数的最小值和最大值
     * creation date: 2020/7/6
     * user : zhangtongju
     */
    public static int getFeedShowPosition(boolean isMaxNum) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        if (isMaxNum) {
            return spUtil.getInt("feedMaxNum", 15);
        } else {
            return spUtil.getInt("feedMinNum", 5);
        }
    }






}
