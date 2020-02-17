package com.mobile.CloudMovie.base;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.githang.statusbar.StatusBarCompat;
import com.mobile.CloudMovie.R;

import butterknife.ButterKnife;
import rx.subjects.PublishSubject;


@SuppressLint("InflateParams")
public abstract class BaseWebActivity extends Activity implements OnClickListener {
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


//    /**
//     * return Layout 资源 IDx
//     */
//    protected abstract int getPullToRefreshLayout();


    /**
     * 初始化UI
     */
    protected abstract void initView();

    /**
     * 初始化事件
     */
    protected abstract void initAction(String url);


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
//        super.requestWindowFeature(Window.FEATURE_NO_TITLE);
        lifecycleSubject.onNext(ActivityLifeCycleEvent.CREATE);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);


//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, //去掉状态栏
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(getLayoutId());
        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#00000000"), true);
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
        if (savedInstanceState != null) {
            String nowNeedUrl = savedInstanceState.getString("nowWebUrl");
            initAction(nowNeedUrl);
        } else {
            initAction(null);
        }


        // AppContext.INSTANCE.setFullScreen(true);
        // AppContext.INSTANCE.getNavigationReLayout().setBackgroundColor(Color.TRANSPARENT);
    }

//
//    private void initPulltoRefsh(){
//        final PullToRefreshLayout p = (PullToRefreshLayout) findViewById(getPullToRefreshLayout());
//        p.setRefreshListener(new BaseRefreshListener() {
//            @Override
//            public void refresh() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        p.finishRefresh();
//                    }
//                },2000);
//            }
//
//            @Override
//            public void loadMore() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        p.finishLoadMore();
//                    }
//                },2000);
//            }
//        });
//        //   p.showView(ViewStatus.ERROR_STATUS); //设置显示的状态
//        ImageView error_refresh= (ImageView) findViewById(R.id.error_refresh);
////        error_refresh.setOnClickListener(new View.OnClickListener() { //TODO 错误显示页，需要先初始化
////            @Override
////            public void onClick(View v) {
////                Toast.makeText(getActivity(),"ces",Toast.LENGTH_SHORT).show();
////            }
////        });
//
//
//    }


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

}
