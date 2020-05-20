package com.flyingeffects.com.ui.view.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bytedance.applog.AppLog;
import com.bytedance.applog.GameReportHelper;
import com.bytedance.applog.InitConfig;
import com.bytedance.applog.util.UriConfig;
import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.chuanglan.shanyan_sdk.listener.GetPhoneInfoListener;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.ConfigForTemplateList;
import com.flyingeffects.com.enity.checkVersion;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.AdManager;
import com.flyingeffects.com.manager.DataCleanManager;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.SPHelper;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.view.fragment.FragForTemplate;
import com.flyingeffects.com.ui.view.fragment.frag_Bj;
import com.flyingeffects.com.ui.view.fragment.frag_search;
import com.flyingeffects.com.ui.view.fragment.frag_user_center;
import com.flyingeffects.com.utils.AssetsUtils;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.faceUtil.ConUtil;
import com.githang.statusbar.StatusBarCompat;
import com.glidebitmappool.GlideBitmapPool;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.megvii.segjni.SegJni;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.subjects.PublishSubject;

import static com.flyingeffects.com.constans.BaseConstans.getChannel;


/****
 * 修改主界面
 * @author zhang
 */
public class HomeMainActivity extends FragmentActivity {
    private ImageView[] iV_menu = new ImageView[4];
    private TextView[] tv_main = new TextView[4];
    private LinearLayout[] lin_menu = new LinearLayout[4];
    private int[] lin_Id = {R.id.ll_menu_0, R.id.ll_menu_1, R.id.ll_menu_2, R.id.ll_menu_3};
    private int[] img_Id = {R.id.iV_menu_0, R.id.iV_menu_1, R.id.iV_menu_2, R.id.iV_menu_3};
    public HomeMainActivity ThisMain;
    private int[] tv_main_button = {R.id.tv_main_0, R.id.tv_main_1, R.id.tv_main_2, R.id.tv_main_3};
    private int[] selectIconArr = {R.mipmap.home_bj, R.mipmap.moban, R.mipmap.chazhao, R.mipmap.wode};
    private int[] unSelectIconArr = {R.mipmap.home_bj_unselect, R.mipmap.moban_unslect, R.mipmap.chazhao_unselect, R.mipmap.wode_unselect};
    private FragmentManager fragmentManager;
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private Timer timer;
    private TimerTask task;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        //禁止休眠
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.act_home_main);
        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#181818"));
        ThisMain = this;
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        fragmentManager = getSupportFragmentManager();
        clearAllData();
        initView();
        copyFile("default_bj.png");
        SegJni.nativeCreateSegHandler(this, ConUtil.getFileContent(this, R.raw.megviisegment_model), BaseConstans.THREADCOUNT);
        GlideBitmapPool.initialize(10 * 1024 * 1024); // 10mb max memory size
        checkUpdate();
        checkConfig();
        getUserPhoneInfo();
        getPushPermission();

        initTiktok();
        if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
            requestCPad();
        }

    }



    /**
     * description ：请求插屏广告
     * creation date: 2020/4/24
     * user : zhangtongju
     */
    private void   requestCPad(){
        int second=BaseConstans.getInterstitial();
        startTimer(second);
    }

    private void startTimer(int second) {
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                AdManager.getInstance().showCpAd(HomeMainActivity.this,AdConfigs.AD_SCREEN);
                destroyTimer();
            }
        };
        timer.schedule(task, second*1000, second*1000);
    }

    /**
     * user :TongJu  ; email:jutongzhang@sina.com
     * time：2018/10/15
     * describe:严防内存泄露
     **/
    private void destroyTimer() {
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }






    private void  initTiktok(){

        /* 初始化开始 */
        // appid和渠道，appid须保证与广告后台申请记录一致，渠道可自定义，如有多个马甲包建议设置渠道号唯一标识一个马甲包。
        final InitConfig config = new InitConfig("181569", getChannel());
         /*
         域名默认国内: DEFAULT, 新加坡:SINGAPORE, 美东:AMERICA
         注意：国内外不同vendor服务注册的did不一样。由DEFAULT切换到SINGAPORE或者AMERICA，会发生变化，
         切回来也会发生变化。因此vendor的切换一定要慎重，随意切换导致用户新增和统计的问题，需要自行评估。
         */

        config.setUriConfig(UriConfig.DEFAULT);
//        //配置心跳，游戏模式
//        config.setEnablePlay(true);
        // 是否在控制台输出日志，可用于观察用户行为日志上报情况，建议仅在调试时使用，release版本请设置为false ！
        AppLog.setEnableLog(false);
        AppLog.init(this, config);
//        /* 初始化结束 */
//        GameReportHelper.onEventRegister("wechat",true);
//        GameReportHelper.onEventPurchase("gift","flower", "008",1,
//                "wechat","¥", true, 1);
    }


    public void getUserPhoneInfo() {
        OneKeyLoginManager.getInstance().getPhoneInfo(new GetPhoneInfoListener() {
            @Override
            public void getPhoneInfoStatus(int code, String result) {
                //预取号回调
                Log.e("VVV", "预取号： code==" + code + "   result==" + result);
            }
        });
    }


    public void copyFile(String name) {
        FileManager manager = new FileManager();
        String dir = manager.getFileCachePath(this, "");
        File file = new File(dir, name);
        if (!file.exists()) {
            AssetsUtils.copyFileFromAssets(this, name, file.getPath());
        }
    }


    /**
     * description ：检查更新
     * date: ：2019/6/13 10:44
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public void checkUpdate() {
        HashMap<String, String> params = new HashMap<>();
        params.put("config_name", "android_version_ad");
        params.put("channel", getChannel());
        Observable ob = Api.getDefault().checkUpdate(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<checkVersion>(this) {
            @Override
            protected void _onError(String message) {
                LogUtil.d("checkUpdate", message);
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(checkVersion data) {
                LogUtil.d("checkUpdate", StringUtil.beanToJSONString(data));
                try {
                    if (data != null) {
                        String uploadVersion = data.getNewversion();
                        String content = data.getContent();
                        int uVersion = Integer.parseInt(uploadVersion);
                        int NowVersion = Integer.parseInt(BaseConstans.getVersionCode());
                        if (uVersion > NowVersion) {
                            intoCheckUpdateAct(data.getDownloadfile(), data.getIs_forceupdate(), content);
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }, "checkUpdate", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    private void getPushPermission() {
        SPHelper spUtil = new SPHelper(this, "fileName");
        boolean isFirst = spUtil.getBoolean("isFirst", true);
        if (isFirst) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.permission_alert)
                    .setMessage(R.string.permission_content)
                    .setPositiveButton(getString(R.string.toGetPermission), (dialog, which) -> goToSetting())
                    .show();
            spUtil.putBoolean("isFirst", false);
        }
    }


    private void goToSetting() {
        try {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= 26) {// android 8.0引导
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
            } else if (Build.VERSION.SDK_INT >= 21) { // android 5.0-7.0
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("app_package", getPackageName());
                intent.putExtra("app_uid", getApplicationInfo().uid);
            } else {//其它
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", getPackageName(), null));
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }catch (Exception e){
            LogUtil.d("OOM","锤子手机这里会闪退");
        }

    }


    /**
     * user :TongJu  ;描述：跳转到更新界面
     * 时间：2018/5/29
     **/
    private void intoCheckUpdateAct(String url, String is_must_update, String content) {
        Intent intent = new Intent(this, UpdateApkActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("url", url);
        intent.putExtra("content", content);
        intent.putExtra("policy", "0");
        intent.putExtra("is_must_update", is_must_update);
        startActivity(intent);
    }


    public void initView() {
        for (int i = 0; i < iV_menu.length; i++) {
            iV_menu[i] = findViewById(img_Id[i]);
            tv_main[i] = findViewById(tv_main_button[i]);
            lin_menu[i] = findViewById(lin_Id[i]);
            lin_menu[i].setOnClickListener(listener);
        }
        whichMenuSelect(1);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    private void clearAllData() {
        //清除外部cache下的内容
        DataCleanManager.cleanExternalCache();
        //清理内部cache
        DataCleanManager.cleanInternalCache(BaseApplication.getInstance());
        //清理内部sdk
        DataCleanManager.cleanFiles(BaseApplication.getInstance());

        LanSongFileUtil.deleteDefaultDir();
        //清理外部sdk
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            DataCleanManager.deleteFilesByDirectory(getExternalFilesDir("dynamic"));
            DataCleanManager.deleteFilesByDirectory(getExternalFilesDir("runCatch"));
            DataCleanManager.deleteFilesByDirectory(getExternalFilesDir("def"));
            DataCleanManager.deleteFilesByDirectory(getExternalFilesDir("imageCopy"));
            DataCleanManager.deleteFilesByDirectory(getExternalFilesDir("faceFolder"));
            DataCleanManager.deleteFilesByDirectory(getExternalFilesDir("faceMattingFolder"));
            DataCleanManager.deleteFilesByDirectory(getExternalFilesDir("soundFolder"));
            DataCleanManager.deleteFilesByDirectory(getExternalFilesDir("cacheMattingFolder"));
            DataCleanManager.deleteFilesByDirectory(getExternalFilesDir("ExtractFrame"));
            DataCleanManager.deleteFilesByDirectory(getExternalFilesDir("DownVideo"));
            DataCleanManager.deleteFilesByDirectory(getExternalFilesDir("toHawei"));
        }

    }


    private OnClickListener listener = v -> {
        switch (v.getId()) {
            case R.id.ll_menu_0:
                whichMenuSelect(0);
                statisticsEventAffair.getInstance().setFlag(HomeMainActivity.this, "5_bj");
                break;
            case R.id.ll_menu_1:
                whichMenuSelect(1);
                break;
            case R.id.ll_menu_2:
                whichMenuSelect(2);
                break;
            case R.id.ll_menu_3:
                statisticsEventAffair.getInstance().setFlag(HomeMainActivity.this, "3_mine");
                whichMenuSelect(3);
                break;

            default:
                break;

        }
    };

    private void setStatusBar() {
        changeBottomTab();
    }


    /**
     * user :TongJu  ;描述：底部栏改变
     * 时间：2018/6/6
     **/
    private void changeBottomTab() {
        for (int i = 0; i < lin_Id.length; i++) {
            iV_menu[i].setImageResource(unSelectIconArr[i]);
            tv_main[i].setTextColor(getResources().getColor(R.color.home_navigation_dark_gray));
        }
        iV_menu[LastWhichMenu].setImageResource(selectIconArr[LastWhichMenu]);
        tv_main[LastWhichMenu].setTextColor(getResources().getColor(R.color.home_base_blue_color));
    }

    /**
     * 记录当前页面id
     */
    private int LastWhichMenu = -1;

    public void whichMenuSelect(int whichMenu) {
        this.LastWhichMenu = whichMenu;
        openMenu(whichMenu);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private frag_Bj menu0F = null;
    private FragForTemplate menu1F = null;
    private frag_search menu2F = null;
    private frag_user_center menu3F = null;


    private void openMenu(int which) {
        setStatusBar();
        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (which) {

                case 0: {
                    if (menu0F == null) {
                        menu0F = new frag_Bj();
                    }
                    if (!menu0F.isAdded() && !menu0F.isVisible() && !menu0F.isRemoving()) {
                        fragmentTransaction.replace(R.id.rL_show, menu0F, menu0F.getClass().getName()).commitAllowingStateLoss();
                    }
                    break;
                }

                case 1: {
                    if (menu1F == null) {
                        menu1F = new FragForTemplate();
                    }
                    if (!menu1F.isAdded() && !menu1F.isVisible() && !menu1F.isRemoving()) {
                        fragmentTransaction.replace(R.id.rL_show, menu1F, menu1F.getClass().getName()).commitAllowingStateLoss();
                    }
                    break;
                }
                case 2: {
                    if (menu2F == null) {
                        menu2F = new frag_search();
                    }
                    fragmentTransaction.replace(R.id.rL_show, menu2F, menu2F.getClass().getName()).commitAllowingStateLoss();
                    break;
                }
                case 3: {
                    if (menu3F == null) {
                        menu3F = new frag_user_center();
                    }
                    fragmentTransaction.replace(R.id.rL_show, menu3F, menu3F.getClass().getName()).commitAllowingStateLoss();
                    break;
                }
                default:
                    break;
            }
        }


    }

    @Override
    public final boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitPressAgain();
        }
        return true;
    }

    private long exitTime = 0;


    private void exitPressAgain() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(ThisMain, "再点一次退出程序" +
                    "" +
                    "" +
                    "", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void intoCreationActivity() {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0) {
                //被用户拒绝的权限集合
                List<String> deniedPermissions = new ArrayList<>();
                //用户通过的权限集合
                List<String> grantedPermissions = new ArrayList<>();
                for (int i = 0; i < grantResults.length; i++) {
                    //获取授权结果，这是一个int类型的值
                    int grantResult = grantResults[i];
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        //用户拒绝授权的权限
                        String permission = permissions[i];
                        deniedPermissions.add(permission);
                    } else {  //用户同意的权限
                        String permission = permissions[i];
                        grantedPermissions.add(permission);
                    }
                }
                if (deniedPermissions.isEmpty()) {
                    //用户拒绝权限为空
                    intoCreationActivity();
                } else {  //不为空
//                    ToastUtil.showToast(getResources().getString(R.string.permission_denied));
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



    /**
     * 检查推广配置是否ok
     */
    private void checkConfig(){
        if(BaseConstans.configList==null) {
            BaseConstans.configList = new ConfigForTemplateList();
            BaseConstans.configList.setContent("已为您复制微信号");
            BaseConstans.configList.setType(1);
            BaseConstans.configList.setCopydata("wordcq520");
            BaseConstans.configList.setDescription("加微信领取100套精美背景素材");
            BaseConstans.configList.setSecondline("领取100套精美素材");
            requestConfigForTemplateList();
        }
    }


    private void requestConfigForTemplateList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("config_name", "wechat_name");
        // 启动时间
        Observable ob = Api.getDefault().configListForTemplateList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<ConfigForTemplateList>(HomeMainActivity.this) {
            @Override
            protected void _onError(String message) {
            }

            @Override
            protected void _onNext(ConfigForTemplateList data) {
                if (data != null) {
                    BaseConstans.configList = data;
                }
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }

}
