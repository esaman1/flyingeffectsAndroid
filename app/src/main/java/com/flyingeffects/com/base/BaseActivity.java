package com.flyingeffects.com.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.flyingeffects.com.R;
import com.flyingeffects.com.ui.interfaces.PermissionListener;
import com.githang.statusbar.StatusBarCompat;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import rx.subjects.PublishSubject;


@SuppressLint("InflateParams")
public abstract class BaseActivity extends AppCompatActivity implements OnClickListener, IActivity {
    protected static final String[] PERMISSION_STORAGE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    protected static final String[] PERMISSION_READ_STORAGE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    protected static final int CODE_PERMISSION_STORAGE = 1;
    protected static final int CODE_PERMISSION_READ_STORAGE = 4;
    public final PublishSubject<ActivityLifeCycleEvent> mLifecycleSubject = PublishSubject.create();
    protected static final String[] PERMISSION_READ_PHONE_STATE = new String[]{Manifest.permission.READ_PHONE_STATE};
    protected static final int CODE_PERMISSION_READ_PHONE_STATE = 2;
//    protected static final String[] PERMISSION_LOCATION = new String[]{Manifest.permission.ACCESS_FINE_LOCATION
//            , Manifest.permission.ACCESS_COARSE_LOCATION};
    protected static final int CODE_PERMISSION_LOCATION = 3;

    private PermissionListener mlistener;

    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    /**
     * actionbar 默认只需调用 super.setPageTitle("title") 设置标题, 如不需要actionbar, 调用 mActionbar.hide();
     */
    protected ActionBar mActionBar;

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
        //由于 启动时设置了 R.style.launcher 的windowBackground属性
        //在进入主页后,把窗口背景清理掉
        setTheme(R.style.AppTheme);
        lifecycleSubject.onNext(ActivityLifeCycleEvent.CREATE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }
        overridePendingTransition(0, 0);
        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#181818"));
//        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#181818"), true);
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
        MobclickAgent.onPause(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        MobclickAgent.onResume(this);
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
