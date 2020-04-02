package com.flyingeffects.com.ui.view.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.manager.DataCleanManager;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.view.fragment.frag_Bj;
import com.flyingeffects.com.utils.faceUtil.ConUtil;
import com.githang.statusbar.StatusBarCompat;
import com.flyingeffects.com.R;
import com.flyingeffects.com.ui.view.fragment.FragForTemplate;
import com.flyingeffects.com.ui.view.fragment.frag_search;
import com.flyingeffects.com.ui.view.fragment.frag_user_center;
import com.flyingeffects.com.utils.AssetsUtils;
import com.glidebitmappool.GlideBitmapPool;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.lansosdk.videoeditor.LanSongUtil;
import com.megvii.segjni.SegJni;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


/****
 * 修改主界面
 * @author zhang
 */
public class HomeMainActivity extends FragmentActivity {
    private ImageView[] iV_menu = new ImageView[4];
    private TextView[] tv_main = new TextView[4];
    private LinearLayout[] lin_menu = new LinearLayout[4];
    private int[] lin_Id = {R.id.ll_menu_0, R.id.ll_menu_1, R.id.ll_menu_2,R.id.ll_menu_3};
    private int[] img_Id = {R.id.iV_menu_0, R.id.iV_menu_1, R.id.iV_menu_2, R.id.iV_menu_3};
    public HomeMainActivity ThisMain;
    private int[] tv_main_button = {R.id.tv_main_0, R.id.tv_main_1, R.id.tv_main_2,R.id.tv_main_3};
    private int[] selectIconArr = {R.mipmap.home_bj,R.mipmap.moban, R.mipmap.chazhao, R.mipmap.wode};
    private int[] unSelectIconArr = { R.mipmap.home_bj_unselect,R.mipmap.moban_unslect, R.mipmap.chazhao_unselect, R.mipmap.wode_unselect};
    private FragmentManager fragmentManager;

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
        SegJni.nativeCreateSegHandler(this, ConUtil.getFileContent(this, R.raw.megviisegment_model));
        GlideBitmapPool.initialize(10 * 1024 * 1024); // 10mb max memory size
    }


    public void copyFile(String name) {
        FileManager manager=new FileManager();
        String dir = manager.getFileCachePath(this,"");
        File file = new File(dir, name);
        if (!file.exists()) {
            AssetsUtils.copyFileFromAssets(this, name, file.getPath());
        }
    }


//    /**
//     * description ：检查更新
//     * date: ：2019/6/13 10:44
//     * author: 张同举 @邮箱 jutongzhang@sina.com
//     */
//    public void checkUpdate(boolean isShowAlert) {
//        HashMap<String, String> params = new HashMap<>();
//        Observable ob = Api.getDefault().checkUpdate(BaseConstans.getRequestHead(params));
//        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<checkVersion>(this) {
//            @Override
//            protected void _onError(String message) {
//                LogUtil.d("checkUpdate", message);
//                ToastUtil.showToast(message);
//            }
//
//            @Override
//            protected void _onNext(checkVersion data) {
//                LogUtil.d("checkUpdate", StringUtil.beanToJSONString(data));
//                try {
//                    if (data != null) {
//                        String uploadVersion = data.getVersion();
//                        String content = data.getContent();
//                        int uVersion = Integer.parseInt(uploadVersion);
//                        int NowVersion = Integer.parseInt(BaseConstans.getVersionCode());
//                        if (uVersion > NowVersion) { //todo  1231
//                            intoCheckUpdateAct(data.getPath(), data.getIs_renew(), content);
//                        } else {
//                            if (isShowAlert) {
//                                ToastUtil.showToast(getResources().getString(R.string.already_newest_version));
//                            }
//                        }
//                    } else {
//                        if (isShowAlert) {
//                            ToastUtil.showToast(getResources().getString(R.string.already_newest_version));
//                        }
//                    }
//                } catch (NumberFormatException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, "checkUpdate", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
//    }


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

        }

    }



    private OnClickListener listener = v -> {
        switch (v.getId()) {
            case R.id.ll_menu_0:
                whichMenuSelect(0);
                statisticsEventAffair.getInstance().setFlag(HomeMainActivity.this,"5_bj");
                break;
            case R.id.ll_menu_1:
                whichMenuSelect(1);
                break;
            case R.id.ll_menu_2:
                whichMenuSelect(2);
                break;
            case R.id.ll_menu_3:
                statisticsEventAffair.getInstance().setFlag(HomeMainActivity.this,"3_mine");
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

//    /**
//     * description ：检查更新
//     * date: ：2019/6/13 10:44
//     * author: 张同举 @邮箱 jutongzhang@sina.com
//     */
//    public void checkUpdate(boolean isShowAlert) {
//        HashMap<String, String> params = new HashMap<>();
//        Observable ob = Api.getDefault().checkUpdate(BaseConstans.getRequestHead(params));
//        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<checkVersion>(this) {
//            @Override
//            protected void _onError(String message) {
//                LogUtil.d("checkUpdate", message);
//                ToastUtil.showToast(message);
//            }
//
//            @Override
//            protected void _onNext(checkVersion data) {
//                LogUtil.d("checkUpdate", StringUtil.beanToJSONString(data));
//                try {
//                    if (data != null) {
//                        String uploadVersion = data.getVersion();
//                        String content = data.getContent();
//                        int uVersion = Integer.parseInt(uploadVersion);
//                        int NowVersion = Integer.parseInt(BaseConstans.getVersionCode());
//                        if (uVersion > NowVersion) { //todo  1231
//                            intoCheckUpdateAct(data.getPath(), data.getIs_renew(), content);
//                        } else {
//                            if (isShowAlert) {
//                                ToastUtil.showToast(getResources().getString(R.string.already_newest_version));
//                            }
//                        }
//                    } else {
//                        if (isShowAlert) {
//                            ToastUtil.showToast(getResources().getString(R.string.already_newest_version));
//                        }
//                    }
//                } catch (NumberFormatException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, "checkUpdate", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
//    }

//    /**
//     * user :TongJu  ;描述：跳转到更新界面
//     * 时间：2018/5/29
//     **/
//    private void intoCheckUpdateAct(String url, String is_must_update, String content) {
//        Intent intent = new Intent(this, UpdateApkActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("url", url);
//        intent.putExtra("content", content);
//        intent.putExtra("policy", "0");
//        intent.putExtra("is_must_update", is_must_update);
//        startActivity(intent);
//    }


//    /**
//     * description ：更新用户信息
//     * date: ：2019/10/18 14:52
//     * author: 张同举 @邮箱 jutongzhang@sina.com
//     */
//    private void requestUserInfo() {
//        HashMap<String, String> params = new HashMap<>();
//        Observable ob = Api.getDefault().updateUserInfo(BaseConstans.getRequestHead(params));
//        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<loginResult>(this) {
//            @Override
//            protected void _onError(String message) {
//                ToastUtil.showToast(message);
//            }
//
//            @Override
//            protected void _onNext(loginResult data) {
//                LogUtil.d("requestUserInfo", StringUtil.beanToJSONString(data));
//                if (data.getIs_vip() == 1) { //vip
//                    BaseConstans.setUseIsVip(true);
//                    BaseConstans.setIsNewUser(true);
//                    BaseConstans.getVip_end_time = data.getVip_end_time();
//                    statisticsEventAffair.getInstance().setFlag(HomeMainActivity.this, "vip_start", "vip_start");  //统计视频数量
//
//                } else {
//                    showScreenAdvertising();
//                    BaseConstans.setUseIsVip(false);
//
//                }
//            }
//        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
//    }







}
