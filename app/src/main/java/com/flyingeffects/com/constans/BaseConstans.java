package com.flyingeffects.com.constans;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.flyingeffects.com.BuildConfig;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.entity.ConfigForTemplateList;
import com.flyingeffects.com.http.Url;
import com.flyingeffects.com.http.abc;
import com.flyingeffects.com.manager.SPHelper;
import com.flyingeffects.com.utils.ChannelUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.OsUtils;
import com.flyingeffects.com.utils.StringUtil;

import java.io.File;
import java.util.HashMap;

/**
 * Created by 张sir
 * on 2017/8/14.
 */


public class BaseConstans {
    public static final String UMENGAPPID = "5e5c68a2570df3d6930002b4";
    /**
     * 抖音分享ClientKey
     */
    public static final String DOUYINSHARE_CLIENTKEY = "awikd2g333hd0ien";
    public static final int MAP_DEFAULT_INITIAL_CAPACITY = 16;


    /**
     * 是否开启华为渠道适配
     */
    public static boolean IsOpenChannelAdaptive = true;

    /**
     * 当前抠图是用sdk 还是用服务器
     */
    public static final boolean UserFaceSdk = true;
    //    public static final boolean isTitokChannel=true;
    public static final int THREADCOUNT = 4;
    /**
     * 信息流广告插入的位置
     */
    public static final int NOWADSHOWPOSITION = 5;
    public static boolean hasCreatingSegJni = true;
    public static String titok;
    public static String kuaishou;
    /**
     * 是否是正式环境
     */
    public static final boolean PRODUCTION = false;
    public static final boolean DEBUG = BuildConfig.DEBUG;
    private static String channel = "";
    private static String versionCode = "";
    private static String uuid = "";
    public static final String PRIVACYPOLICY = Url.BASE_URL + "/fly/FS-PrivacyPolicy.html";
    public static String service_wxi;
    public static boolean TemplateHasWatchingAd = false;
    public static ConfigForTemplateList configList;

    /**
     * 是否有广告，0表示没得，1表示有，全局控制
     */
    private static int hasAdvertising = 0;

    /**
     * 退出后台后多少秒后会重新显示插屏
     */
    public static int showAgainKaipingAd = 60;

    /**
     * 只是用前几次的新用户
     */
    private static boolean isNewUserForAdvertising = false;

    public static final String PROTOCOL = Url.BASE_URL + "/fly/FS-Agreement.html";

    public static final String FILE_PATH;
    public static final String FRAME_TEMP_PATH;

    public static HashMap<String, String> getRequestHead(HashMap<String, String> map) {
        String nowTimestamp = getTimestamp() + "";
        map.put("app_id", "10000");
        map.put("platform", "android");
        //getChannel()  test
        map.put("channel", getChannel());
        map.put("version", getVersionCode());
        //getTimestamp()+""
        map.put("timestamp", nowTimestamp);
        map.put("imei", getUuid());
        map.put("uuid", getUserUuid());
        map.put("token", getUserToken());
        map.put("sign", getSine(nowTimestamp, map));

        return map;
    }


    private static String getSine(String nowTimestamp, HashMap<String, String> map) {
        map.put("app_id", "10000");
        map.put("platform", "android");
        map.put("channel", getChannel());
        map.put("version", getVersionCode());
        //getTimestamp()+""
        map.put("timestamp", nowTimestamp);
        map.put("imei", getUuid());
        map.put("uuid", getUserUuid());
        map.put("token", getUserToken());
        return abc.sign(map);
    }


    public static String getUserToken() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getString("token", "");
    }


    public static void setUserToken(String token) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putString("token", token);
    }

    public static String getUserId() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getString("userId", "");
    }


    public static void setUserId(String id, String userName, String headUrl) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putString("userId", id);
        LogUtil.d("oom3", "设置的userId=" + id);
        spUtil.putString("userName", userName);
        spUtil.putString("headUrl", headUrl);
    }

    public static String nickName() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getString("userName", "");
    }

    public static String headUrl() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getString("headUrl", "");
    }


    public static boolean hasLogin() {
        return getUserToken() != null && !"".equals(getUserToken());
    }


    static String getUserUuid() {

        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getString("uuid", "");
    }


    public static String getVersionCode() {
        if ("".equals(versionCode)) {
            versionCode = StringUtil.getVersion(BaseApplication.getInstance());
            return versionCode;
        } else {
            return versionCode;
        }
    }


    public static String getUuid() {
        if ("".equals(uuid)) {
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


    public static boolean isFirstIntoMainAct() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getBoolean("isFirstUseApp", true);
    }

    public static void setFirstIntoMainAct() {
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
        if (channel != null && !"".equals(channel)) {
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
            if (titok != null && !"".equals(titok)) {
                return titok;
            } else {
                return "http://v.douyin.com/B62HrT/";
            }
        } else {
            if (kuaishou != null && !"".equals(kuaishou)) {
                return kuaishou;
            } else {
                return "看了这么多快手，还是「飞闪哥哥」最好玩了！ http://m.gifshow.com/s/xEna7Voi 复制此链接，打开【快手】直接观看！";
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
     * description ：设置入口
     * creation date: 2021/3/12
     * user : zhangtongju
     */
    public static void setHasAdEntrance(String json) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putString("AdEntrance", json);
    }


    /**
     * gif 教程配置说明图片地址
     */
    public static void setGifCourse(String json) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putString("GIFCOURSE", json);
    }


    public static String getGifCourse() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getString("GIFCOURSE", "");
    }


    public static void setAdShowErrorCanSave(String json) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putString("AdShowErrorCanSave", json);
    }


    public static void setCreateVideoShowAdUserNum(String json) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putString("createVideoShowAdUserNum", json);
    }

    public static String getCreateVideoShowAdUserNum() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getString("createVideoShowAdUserNum", "");
    }


    /**
     * description ：激励视频出错的情况下能够得到奖励 1 是可以的，0是不可以
     * creation date: 2021/4/16
     * user : zhangtongju
     */
    public static String getAdShowErrorCanSave() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getString("AdShowErrorCanSave", "1");
    }


    public static String getHasAdEntrance() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getString("AdEntrance", "");
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
        if (!isNewUserForAdvertising) {
            SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
            isNewUserForAdvertising = spUtil.getBoolean("isNewUserForAdvertising", false);
            return isNewUserForAdvertising;
        }
        return true;

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
        return spUtil.getString("minapp_share_title", "我也会了！用飞闪就能制作：");
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


    public static void setFirstUseDownAndUpAct() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putBoolean("isFirstUseDownAndUp", false);
    }


    public static void setFirstOpenApp(long time) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putBoolean("isFirstOpen", false);
        spUtil.putLong("lastShowAdvertisingTime", time);
    }

    /**
     * 设置打开app的次数
     */
    public static void setOpenAppNum(int num) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putInt("OpenAppNum", num);
    }


    /**
     * 得到打开app的次数
     */
    public static int getOpenAppNum() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getInt("OpenAppNum", 0);
    }

    /**
     * 设置间隔多少次加载广告
     */
    public static void setIntervalNumShowAD(int num) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putInt("IntervalNumShowAD", num);
    }

    /**
     * 得到间隔多少次加载广告  默认间隔10次显示一次广告
     */
    public static int getIntervalNumShowAD() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getInt("IntervalNumShowAD", 10);
    }

    /**
     * 设置打开选择图片或视频的activity的次数
     */
    public static void setOpenPhotoAlbumNum(int num) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putInt("OpenPhotoAlbumNum", num);
    }

    /**
     * 得到打开选择图片或视频的activity的次数
     */
    public static int getOpenPhotoAlbumNum() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getInt("OpenPhotoAlbumNum", -1);
    }

    /**
     * 启动APP多少秒后显示插屏广告
     */
    public static void setInterstitial(int num) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putInt("interstitial", num);
    }


    /**
     * 启动APP多少秒后显示插屏广告
     */
    public static int getInterstitial() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getInt("interstitial", 5);
    }


    /**
     * 关闭浮动弹窗的时间
     */
    public static void setAdCloseTime(long time) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putLong("closeAdTime", time);
    }

    /**
     * 得到浮动弹窗的时间
     */
    public static long getAdCloseTime() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getLong("closeAdTime", 0);
    }



    /**
     * 得到上次请求权限的时间
     */
    public static long getLastRequestPerTime() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getLong("lastPerTime", 0);
    }

    public static void setLastRequestPerTime(long time) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putLong("lastPerTime", time);
    }


//    /**
//     * 关闭vip的时间
//     */
//    public static void setVipCloseTime(long time) {
//        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
//        spUtil.putLong("closeVipTime", time);
//    }


//    /**
//     * 得到浮动弹窗的时间
//     */
//    public static long getVipCloseTime() {
//        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
//        return spUtil.getLong("closeVipTime", 0);
//    }




    /**
     * 设置自定义模板分享到抖音的话题
     */
    public static void setDouyingTopic(String topic) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putString("DouyingTopic", topic);
    }

    /**
     * 获取自定义模板分享到抖音的话题
     */
    public static String getDouyingTopic() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getString("DouyingTopic", "飞闪视频");
    }

    public static void setFirstUseAppTime(long firstUseAppTime) {
        SPHelper.getInstance().putLong("firstUseAppTime", firstUseAppTime);
    }

    public static long getFirstUseAppTime() {
        return SPHelper.getInstance().getLong("firstUseAppTime", 0);
    }

    /**
     * 设置换装制作页面切换模板按钮加载视频广告的间隔次数
     */
    public static void setDressupIntervalsNumber(int intervalsNumber) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putInt("IntervalsNumber", intervalsNumber);
    }

    public static void setOaid(String oaid) {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        spUtil.putString("oaid", oaid);
    }

    public static String getOaid() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getString("oaid", "");
    }

    public static void setVipServerShow(int isShow) {
        SPHelper.getInstance().putInt("vip_server", isShow);
    }

    public static int getVipServerShow() {
        return SPHelper.getInstance().getInt("vip_server", 1);
    }

    public static void setVipFloatWindowConfigShowTimes(int times) {
        SPHelper.getInstance().putInt("vip_float_config_show_times", times);
    }

    public static int getVipFloatWindowConfigShowTimes() {
        return SPHelper.getInstance().getInt("vip_float_config_show_times", 1);
    }

    public static void setVipFloatWindowShowTimes(int times) {
        SPHelper.getInstance().putInt("vip_float_show_times", times);
    }

    public static int getVipFloatWindowShowTimes() {
        return SPHelper.getInstance().getInt("vip_float_show_times", 0);
    }

    public static void setVipFloatWindowShowTime(long time) {
        SPHelper.getInstance().putLong("vip_float_show_time", time);
    }

    public static long getVipFloatWindowShowTime() {
        return SPHelper.getInstance().getLong("vip_float_show_time", 0);
    }

    /**
     * 获取换装制作页面切换模板按钮加载视频广告的间隔次数
     */
    public static int getDressupIntervalsNumber() {
        SPHelper spUtil = new SPHelper(BaseApplication.getInstance(), "fileName");
        return spUtil.getInt("IntervalsNumber", 5);
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


    static {
        FILE_PATH = BaseApplication.getInstance().getCacheDir().getAbsolutePath() + File.separator + "FeiShan_Cache";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(FILE_PATH);
        stringBuilder.append("/frameTemp");
        FRAME_TEMP_PATH = stringBuilder.toString();
    }


}
