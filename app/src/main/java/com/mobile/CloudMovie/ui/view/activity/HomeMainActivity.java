package com.mobile.CloudMovie.ui.view.activity;

import android.app.ActionBar;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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

import com.githang.statusbar.StatusBarCompat;
import com.mobile.CloudMovie.R;
import com.mobile.CloudMovie.ui.view.fragment.frag0;
import com.mobile.CloudMovie.ui.view.fragment.frag1;
import com.mobile.CloudMovie.ui.view.fragment.frag2;
import com.mobile.CloudMovie.ui.view.fragment.frag3;
import com.mobile.CloudMovie.ui.view.fragment.frag_user_center;
import com.mobile.CloudMovie.utils.JsonReaderUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


/****
 * 修改主界面
 * @author zhang
 */
public class HomeMainActivity extends FragmentActivity {
    private ImageView[] iV_menu = new ImageView[5];
    private TextView[] tv_main = new TextView[5];
    private LinearLayout[] lin_menu = new LinearLayout[5];
    private int[] lin_Id = {R.id.ll_menu_0, R.id.ll_menu_1, R.id.ll_menu_2, R.id.ll_menu_3, R.id.ll_menu_4};
    private int[] img_Id = {R.id.iV_menu_0, R.id.iV_menu_1, R.id.iV_menu_2, R.id.iV_menu_3, R.id.iV_menu_4};
    public HomeMainActivity ThisMain;
    private int[] tv_main_button = {R.id.tv_main_0, R.id.tv_main_1, R.id.tv_main_2, R.id.tv_main_3, R.id.tv_main_4};
    private int[] selectIconArr = {R.mipmap.long_movie, R.mipmap.sort_movie, R.mipmap.found, R.mipmap.collect, R.mipmap.member};
    private int[] unSelectIconArr = {R.mipmap.long_movie_ed, R.mipmap.sort_movie_ed, R.mipmap.found_ed, R.mipmap.collect_ed, R.mipmap.member_ed};
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        //禁止休眠
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.act_home_main);
        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#FFFFFF"));
        ThisMain = this;
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        fragmentManager = getSupportFragmentManager();
        initView();



        //测试
        try {
            Object test=  JsonReaderUtils.getInstance().mapForKey("buildcfg").get("version_name");
            String aa=  test+"";
            Toast.makeText(this,aa,Toast.LENGTH_LONG).show();

        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }




//        checkUpdate(false);
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
        Intent intent = new Intent(this, updateApkActivity.class);
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
        whichMenuSelect(0);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

//    /**
//     * user :TongJu  ;描述：有盟要求的
//     * 时间：2018/7/6
//     **/
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
//    }


    private OnClickListener listener = v -> {
        switch (v.getId()) {
            case R.id.ll_menu_0:
                whichMenuSelect(0);
                break;
            case R.id.ll_menu_1:
                whichMenuSelect(1);
                break;
            case R.id.ll_menu_2:
                whichMenuSelect(2);
                break;
            case R.id.ll_menu_3:
                whichMenuSelect(3);
                break;
            case R.id.ll_menu_4:
                whichMenuSelect(4);
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
        tv_main[LastWhichMenu].setTextColor(getResources().getColor(R.color.colorRedBg));
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
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    private frag0 menu0F = null;
    private frag1 menu1F = null;
    private frag2 menu2F = null;
    private frag3 menu3F = null;
    private frag_user_center menu4F = null;

    private void openMenu(int which) {
        setStatusBar();
        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (which) {
                case 0: {
                    if (menu0F == null) {
                        menu0F = new frag0();
                    }
                    if (!menu0F.isAdded() && !menu0F.isVisible() && !menu0F.isRemoving()) {
                        fragmentTransaction.replace(R.id.rL_show, menu0F, menu0F.getClass().getName()).commitAllowingStateLoss();
                    }
                    break;
                }
                case 1: {
                    if (menu1F == null) {
                        menu1F = new frag1();

                    }
                    fragmentTransaction.replace(R.id.rL_show, menu1F, menu1F.getClass().getName()).commitAllowingStateLoss();
                    break;
                }
                case 2: {
                    if (menu2F == null) {
                        menu2F = new frag2();

                    }
                    fragmentTransaction.replace(R.id.rL_show, menu2F, menu2F.getClass().getName()).commitAllowingStateLoss();
                    break;
                }
                case 3: {
                    if (menu3F == null) {
                        menu3F = new frag3();

                    }
                    fragmentTransaction.replace(R.id.rL_show, menu3F, menu3F.getClass().getName()).commitAllowingStateLoss();
                    break;
                }
                case 4: {
                    if (menu4F == null) {
                        menu4F = new frag_user_center();

                    }
                    fragmentTransaction.replace(R.id.rL_show, menu4F, menu4F.getClass().getName()).commitAllowingStateLoss();
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
            Toast.makeText(ThisMain, getResources().getString(R.string.app_name), Toast.LENGTH_SHORT).show();
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
//        Intent intent = new Intent(this, updateApkActivity.class);
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
