package com.flyingeffects.com.base;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.flyingeffects.com.enity.CommonNewsBean;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.ui.interfaces.PermissionListener;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.screenUtil;
import com.nineton.ntadsdk.bean.FeedAdConfigBean;
import com.nineton.ntadsdk.itr.FeedAdCallBack;
import com.nineton.ntadsdk.manager.FeedAdManager;
import com.nineton.ntadsdk.utils.DeviceUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.subjects.PublishSubject;

import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.BAIDU_FEED_AD_EVENT;
import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.GDT_FEED_AD_EVENT;
import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.TT_FEED_AD_EVENT;
import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.TYPE_GDT_FEED_EXPRESS_AD;
import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.TYPE_TT_FEED_EXPRESS_AD;

/**
 * Created by 张sir
 * on 2017/8/22.
 */

public abstract class BaseFragment extends Fragment implements IActivity {
    protected View contentView = null;
    protected Unbinder unbinder;
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();

    protected abstract int getContentLayout();

    protected abstract void initView();

    protected abstract void initAction();

    protected abstract void initData();

//    public      FeedAdManager mAdManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(getContentLayout(), null);
            unbinder = ButterKnife.bind(this, contentView);
//            test();
            initView();

        } else {
            ViewGroup vp = (ViewGroup) contentView.getParent();
            if (null != vp) {
                vp.removeView(contentView);
//                vp.removeAllViews();
            }
        }
        initAction();
        initData();

        return contentView;
    }



    /***
     *user: 张sir ,@time: 2017/8/22
     *description:当Fragment所在的Activity被启动完成后回调该方法。
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    /**
     * 查找控件
     */
    public View findViewById(int id) {
        View v = null;
        if (contentView != null) {
            v = contentView.findViewById(id);
        }
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        lifecycleSubject.onNext(ActivityLifeCycleEvent.PAUSE);
        super.onPause();
    }

    @Override
    public void onStop() {
        lifecycleSubject.onNext(ActivityLifeCycleEvent.STOP);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        lifecycleSubject.onNext(ActivityLifeCycleEvent.DESTROY);
//        unbinder.unbind();
        super.onDestroy();
    }


    /***
     *user: 张sir ,@time: 2017/8/14
     *description:判断事件出发时间间隔是否超过预定值,防重复点击
     */
    private long lastClickTime;

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
        startActivity(new Intent(getActivity(), clazz));
    }


    @Override
    public void goActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(getActivity(), cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }


    //---------------------权限申请-----------------------
    /**
     * 权限申请
     *
     * @param permissions 待申请的权限集合
     * @param listener  申请结果监听事件
     * describe:android  6.0及+ 需要手动申请权限，而6.0以前只需要在清单文件里面申请，
     * 用户安装后就默认申请通过了权限，所以这里需要适配权限，主动让用户申请权限，
     * 否则会崩溃
     */
    PermissionListener mlistener;

    protected void requestRunTimePermission(String[] permissions, PermissionListener listener) {
        this.mlistener = listener;

        //用于存放为授权的权限
        List<String> permissionList = new ArrayList<>();
        //遍历传递过来的权限集合
        for (String permission : permissions) {
            //判断是否已经授权
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                //未授权，则加入待授权的权限集合中
                permissionList.add(permission);
            }
        }

        //判断集合
        if (!permissionList.isEmpty()) {  //如果集合不为空，则需要去授权
            BaseFragment.this.requestPermissions(permissions, 1);
        } else {  //为空，则已经全部授权
            listener.onGranted();
        }
    }


    /**
     * 权限申请结果
     *
     * @param requestCode  请求码
     * @param permissions  所有的权限集合
     * @param grantResults 授权结果集合
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    //被用户拒绝的权限集合
                    List<String> deniedPermissions = new ArrayList<>();
                    //用户通过的权限集合
                    List<String> grantedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        //获取授权结果，这是一个int类型的值
                        int grantResult = grantResults[i];

                        if (grantResult != PackageManager.PERMISSION_GRANTED) { //用户拒绝授权的权限
                            String permission = permissions[i];
                            deniedPermissions.add(permission);
                        } else {  //用户同意的权限
                            String permission = permissions[i];
                            grantedPermissions.add(permission);
                        }
                    }

                    if (deniedPermissions.isEmpty()) {  //用户拒绝权限为空
                        mlistener.onGranted();
                    } else {  //不为空
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


    /**
     * description ：请求Feed 广告
     * creation date: 2021/3/12
     * user : zhangtongju
     */
    public void requestFeedAd(FeedAdManager mAdManager,RequestFeedBack callback){
        LogUtil.d("OOM2","requestAd");
        if(getActivity()!=null&&mAdManager!=null){
            float needScreenWidth= DeviceUtil.getScreenWidthInPX(getActivity())/(float)2- screenUtil.dip2px(getActivity(),10);
            LogUtil.d("OOM2","needScreenWidth="+needScreenWidth);
            mAdManager.setViewWidth((int) needScreenWidth);
            mAdManager.getFeedAd(getActivity(), AdConfigs.AD_FEED, new FeedAdCallBack() {
                @Override
                public void onFeedAdShow(int typeId, FeedAdConfigBean.FeedAdResultBean feedAdResultBean) {
                    LogUtil.d("OOM2","onFeedAdShow");
                    CommonNewsBean bean =new CommonNewsBean();
                    bean.setTitle(feedAdResultBean.getTitle());
                    bean.setHide(false);
                    bean.setImageUrl(feedAdResultBean.getImageUrl());
                    bean.setEventType(feedAdResultBean.getEventType());
                    bean.setChannel(feedAdResultBean.getChannel());
                    bean.setReadCounts(feedAdResultBean.getAdReadCount());
                    bean.setShowCloseButton(feedAdResultBean.isShowCloseButton());
                    //根据类型设置对应的属性
                    switch (typeId) {
                        case BAIDU_FEED_AD_EVENT:
                        case GDT_FEED_AD_EVENT:
                        case TT_FEED_AD_EVENT:
                        case TYPE_TT_FEED_EXPRESS_AD:
                            bean.setFeedResultBean(feedAdResultBean.getFeedResultBean());
                            break;
                        case TYPE_GDT_FEED_EXPRESS_AD:
                            bean.setAdView(feedAdResultBean.getAdView());
                            break;
                    }
                    callback.GetAdCallback(feedAdResultBean);
                }

                @Override
                public void onFeedAdError(String error) {
                    LogUtil.d("OOM2","onFeedAdError="+error);
                }

                @Override
                public void onFeedAdClose(int type, int adIndex) {
                    LogUtil.d("OOM2","onFeedAdClose=");
                    callback.ChoseAdBack(type,adIndex);
                }

                @Override
                public void onFeedAdExposed() {
                    LogUtil.e("onFeedAdExposed");
                }

                @Override
                public boolean onFeedAdClicked(String title, String url, boolean isNtAd, boolean openURLInSystemBrowser, int adapterPosition) {
                    LogUtil.e("onFeedAdClicked" + adapterPosition);
                    return false;
                }
            });
        }else{
            LogUtil.d("OOM2","adManage为null");
        }

    }


    public interface RequestFeedBack{
        void GetAdCallback(FeedAdConfigBean.FeedAdResultBean bean);
        void ChoseAdBack(int type, int adIndex);
    }


}
