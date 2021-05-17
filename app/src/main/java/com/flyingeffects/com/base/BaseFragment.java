package com.flyingeffects.com.base;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.flyingeffects.com.enity.CommonNewsBean;
import com.flyingeffects.com.enity.HomeChoosePageListener;
import com.flyingeffects.com.enity.SecondChoosePageListener;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.screenUtil;
import com.nineton.ntadsdk.bean.FeedAdConfigBean;
import com.nineton.ntadsdk.itr.FeedAdCallBack;
import com.nineton.ntadsdk.manager.FeedAdManager;
import com.nineton.ntadsdk.utils.DeviceUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.subjects.PublishSubject;

import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.TYPE_GDT_FEED_EXPRESS_AD;
import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.TYPE_TT_FEED_EXPRESS_AD;

/**
 * Created by 张sir
 * on 2017/8/22.
 */

public abstract class BaseFragment extends Fragment implements IActivity {
    protected static final String[] PERMISSION_STORAGE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    protected static final String[] PERMISSION_READ_STORAGE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    protected static final int CODE_PERMISSION_STORAGE = 1;
    protected static final int CODE_PERMISSION_READ_STORAGE = 4;
    public final PublishSubject<ActivityLifeCycleEvent> mLifecycleSubject = PublishSubject.create();
    protected static final String[] PERMISSION_READ_PHONE_STATE = new String[]{Manifest.permission.READ_PHONE_STATE};
    protected static final int CODE_PERMISSION_READ_PHONE_STATE = 2;
    protected static final String[] PERMISSION_LOCATION = new String[]{Manifest.permission.ACCESS_FINE_LOCATION
            , Manifest.permission.ACCESS_COARSE_LOCATION};
    protected static final int CODE_PERMISSION_LOCATION = 3;

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
            if (getContentLayout() != 0) {
                contentView = inflater.inflate(getContentLayout(), null);
            } else {
                contentView = getBindingView(inflater, container);
            }
            unbinder = ButterKnife.bind(this, contentView);
            initView();
            EventBus.getDefault().register(this);
        } else {
            ViewGroup vp = (ViewGroup) contentView.getParent();
            if (null != vp) {
                vp.removeView(contentView);
            }
        }
        initAction();
        initData();

        return contentView;
    }

    protected View getBindingView(LayoutInflater inflater, ViewGroup container) {
        return null;
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
//        EventBus.getDefault().unregister(this);
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


    /**
     * description ：请求Feed 广告
     * creation date: 2021/3/12
     * user : zhangtongju
     */
    public void requestFeedAd(FeedAdManager mAdManager, RequestFeedBack callback) {
        if (getActivity() != null && mAdManager != null) {
            float needScreenWidth = DeviceUtil.getScreenWidthInPX(getActivity()) / (float) 2 - screenUtil.dip2px(getActivity(), 10);
            LogUtil.d("OOM2", "needScreenWidth=" + needScreenWidth);
            mAdManager.setViewWidth((int) needScreenWidth);
            mAdManager.getFeedAd(getActivity(), AdConfigs.AD_FEED, new FeedAdCallBack() {
                @Override
                public void onFeedAdShow(int typeId, FeedAdConfigBean.FeedAdResultBean feedAdResultBean) {
                    CommonNewsBean bean = new CommonNewsBean();
                    bean.setTitle(feedAdResultBean.getTitle());
                    bean.setHide(false);
                    bean.setImageUrl(feedAdResultBean.getImageUrl());
                    bean.setEventType(feedAdResultBean.getEventType());
                    bean.setChannel(feedAdResultBean.getChannel());
                    bean.setReadCounts(feedAdResultBean.getAdReadCount());
                    bean.setShowCloseButton(feedAdResultBean.isShowCloseButton());
//                    bean.setAdapterPosition(tag);
                    bean.setFeedResultBean(feedAdResultBean.getFeedResultBean());
                    //根据类型设置对应的属性
                    switch (typeId) {
                        case TYPE_TT_FEED_EXPRESS_AD:
                        case TYPE_GDT_FEED_EXPRESS_AD:
                            bean.setAdView(feedAdResultBean.getAdView());
                            break;
                    }
//                    LogUtil.d("OOM2","onFeedAdShow");
//                    CommonNewsBean bean =new CommonNewsBean();
//                    bean.setTitle(feedAdResultBean.getTitle());
//                    bean.setHide(false);
//                    bean.setImageUrl(feedAdResultBean.getImageUrl());
//                    bean.setEventType(feedAdResultBean.getEventType());
//                    bean.setChannel(feedAdResultBean.getChannel());
//                    bean.setReadCounts(feedAdResultBean.getAdReadCount());
//                    bean.setShowCloseButton(feedAdResultBean.isShowCloseButton());
//                    //根据类型设置对应的属性
//                    switch (typeId) {
//                        case BAIDU_FEED_AD_EVENT:
//                        case GDT_FEED_AD_EVENT:
//                        case TT_FEED_AD_EVENT:
//                        case TYPE_TT_FEED_EXPRESS_AD:
//                            bean.setFeedResultBean(feedAdResultBean.getFeedResultBean());
//                            break;
//                        case TYPE_GDT_FEED_EXPRESS_AD:
//                            bean.setAdView(feedAdResultBean.getAdView());
//                            break;
//                    }

                    callback.getAdCallback(feedAdResultBean);
                }

                @Override
                public void onFeedAdError(String error) {
                    LogUtil.d("OOM2", "onFeedAdError=" + error);
                }

                @Override
                public void onFeedAdClose(int type, int adIndex) {
                    LogUtil.d("OOM2", "onFeedAdClose=");
                    callback.choseAdBack(type, adIndex);
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
        } else {
            LogUtil.d("OOM2", "adManage为null");
        }

    }


    public interface RequestFeedBack {
        void getAdCallback(FeedAdConfigBean.FeedAdResultBean bean);

        void choseAdBack(int type, int adIndex);
    }


    //----------------------------------------仿懒加载，判断当前显示的页面是那个-----------------------------------------------

    /**
     * 当前主页选择的位数
     */
    public static int NowHomePageChooseNum = 1;

    /**
     * 当前第二页选择位数 ,每次切换后都需要用之前的那个
     */
    public static int NowSecondChooseNum = 0;


    private int cacheBjSecondChooseNum;
    private int cacheTmSecondChooseNum;
    private int cacheDressUpSecondChooseNum;

    @Subscribe
    public void onEventMainThread(HomeChoosePageListener listener) {
        if (getActivity() != null) {
            NowHomePageChooseNum = listener.getPager();
            if (NowHomePageChooseNum == 0) {
                NowSecondChooseNum = cacheBjSecondChooseNum;
            } else if (NowHomePageChooseNum == 1) {
                NowSecondChooseNum = cacheTmSecondChooseNum;
            } else {
                NowSecondChooseNum = cacheDressUpSecondChooseNum;
            }
            LogUtil.d("page2Change", "NowHomePageChooseNum=" + NowHomePageChooseNum);
            if (callback != null) {
                callback.isChange();
            }
            if (callback2 != null) {
                callback2.isChange();
            }
        }
    }


    @Subscribe
    public void onEventMainThread(SecondChoosePageListener listener) {
        if (getActivity() != null) {
            int secondChooseNum = listener.getPager();
            if (NowHomePageChooseNum == 0) {
                cacheBjSecondChooseNum = secondChooseNum;
            } else if (NowHomePageChooseNum == 1) {
                cacheTmSecondChooseNum = secondChooseNum;
            } else {
                cacheDressUpSecondChooseNum = secondChooseNum;
            }
            NowSecondChooseNum = secondChooseNum;
            LogUtil.d("page2Change", "NowSecondChooseNum=" + NowSecondChooseNum);
            if (callback != null) {
                callback.isChange();
            }

            if (callback2 != null) {
                callback2.isChange();
            }
        }
    }


    private PageChangeCallback callback;

    public void ChoosePageChange(PageChangeCallback callback) {
        this.callback = callback;
    }

    private PageChangeCallback2 callback2;

    public void ChoosePageChange2(PageChangeCallback2 callback2) {
        this.callback2 = callback2;
    }


    public interface PageChangeCallback {
        void isChange();
    }

    public interface PageChangeCallback2 {
        void isChange();
    }


}
