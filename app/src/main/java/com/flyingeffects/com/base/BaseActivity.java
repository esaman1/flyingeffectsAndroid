package com.flyingeffects.com.base;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.githang.statusbar.StatusBarCompat;
import com.flyingeffects.com.R;
import com.flyingeffects.com.ui.interfaces.PermissionListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import rx.subjects.PublishSubject;


@SuppressLint("InflateParams")
public abstract class BaseActivity extends AppCompatActivity implements OnClickListener,IActivity {

    private PermissionListener mlistener;

    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    /**
     * actionbar 默认只需调用 super.setPageTitle("title") 设置标题, 如不需要actionbar, 调用 mActionbar.hide();
     */
    protected ActionBar mActionBar;
    /**
     * application context
     */

    protected Context mContext;
    /**
     * TAG = Class.class.getSimpleName();
     */
    protected String TAG;
    /**
     * 页面上部显示的title
     */
    protected TextView pageTitle;


    /**
     * return Layout 资源 IDx
     */
    protected abstract int getLayoutId();





    /**
     * 初始化UI
     */
    protected abstract void initView();

    /**
     * 初始化事件
     */
    protected abstract void initAction();




    /**
     * XXX 所有BaseActivity的子类, 如果override了本方法, 务必在default调用 super.onClick(v);
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
            case R.id.back_img:
            case R.id.iv_top_back:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(ActivityLifeCycleEvent.CREATE);
        setContentView(getLayoutId());
        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#181818"));
//        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#181818"), true);
        mContext = getApplicationContext();
        mActionBar = getActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(false);
            mActionBar.setDisplayShowCustomEnabled(true);
            mActionBar.setDisplayShowHomeEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayUseLogoEnabled(false);
            mActionBar.setHomeButtonEnabled(false);
            View actionbarLayout = LayoutInflater.from(this).inflate(R.layout.base_act_actionbar, null);
            (actionbarLayout.findViewById(R.id.more)).setVisibility(View.INVISIBLE);
            (actionbarLayout.findViewById(R.id.back)).setOnClickListener(this);
            (actionbarLayout.findViewById(R.id.back_img)).setOnClickListener(this);
            pageTitle = actionbarLayout.findViewById(R.id.title);
            mActionBar.setCustomView(actionbarLayout);
//			actionbarLayout.findViewById(R.id.ll_base_reshflsh).setOnClickListener(this);
            mActionBar.show();
        }
        ButterKnife.bind(this);
        Glide.with(this);//Glide.with(this).load("http://pic9/258/a2.jpg").into(iv);
//        EventBus.getDefault().register(this);
        initView();
        initAction();
    }




    /**
     * 处理actionbar 旁边, homebutton的动作
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        lifecycleSubject.onNext(ActivityLifeCycleEvent.PAUSE);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        lifecycleSubject.onNext(ActivityLifeCycleEvent.STOP);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        lifecycleSubject.onNext(ActivityLifeCycleEvent.DESTROY);
//        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    /***
     *user: 张sir ,@time: 2017/8/14
     *description:显示标题
     */
    protected void setPageTitle(String title) {
        pageTitle.setText(title);
    }


    private long lastClickTime;

    /***
     *user: 张sir ,@time: 2017/8/14
     *description:判断事件出发时间间隔是否超过预定值,防重复点击
     */
    public boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 400) {
            return true;
        }
        lastClickTime = time;
        return false;
    }





    //---------------------权限申请-----------------------
    /**
     * 权限申请
     * @param permissions 待申请的权限集合
     * @param listener  申请结果监听事件
     * describe:android  6.0及+ 需要手动申请权限，而6.0以前只需要在清单文件里面申请，
     * 用户安装后就默认申请通过了权限，所以这里需要适配权限，主动让用户申请权限，
     * 否则会崩溃
     */
    protected void requestRunTimePermission(String[] permissions,PermissionListener listener){
        this.mlistener = listener;

        //用于存放为授权的权限
        List<String> permissionList = new ArrayList<>();
        //遍历传递过来的权限集合
        for (String permission : permissions) {
            //判断是否已经授权
            if (ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                //未授权，则加入待授权的权限集合中
                permissionList.add(permission);
            }
        }

        //判断集合
        if (!permissionList.isEmpty()){  //如果集合不为空，则需要去授权
            ActivityCompat.requestPermissions(this,permissionList.toArray(new String[permissionList.size()]),1);
        }else{  //为空，则已经全部授权
            listener.onGranted();
        }
    }

    /**
     * 权限申请结果
     * @param requestCode  请求码
     * @param permissions  所有的权限集合
     * @param grantResults 授权结果集合
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0){
                    //被用户拒绝的权限集合
                    List<String> deniedPermissions = new ArrayList<>();
                    //用户通过的权限集合
                    List<String> grantedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        //获取授权结果，这是一个int类型的值
                        int grantResult = grantResults[i];

                        if (grantResult != PackageManager.PERMISSION_GRANTED){ //用户拒绝授权的权限
                            String permission = permissions[i];
                            deniedPermissions.add(permission);
                        }else{  //用户同意的权限
                            String permission = permissions[i];
                            grantedPermissions.add(permission);
                        }
                    }

                    if (deniedPermissions.isEmpty()){  //用户拒绝权限为空
                        mlistener.onGranted();
                    }else {  //不为空
                        //回调授权成功的接口
                        mlistener.onDenied(deniedPermissions);
                        //回调授权失败的接口
                        mlistener.onGranted(grantedPermissions);
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }



    @Override
    public void goActivity(Intent it) {
        startActivity(it);
    }

    @Override
    public void goActivity(Class<?> clazz) {
        startActivity(new Intent(this, clazz));
    }







    @Override
    public void goActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }




}
