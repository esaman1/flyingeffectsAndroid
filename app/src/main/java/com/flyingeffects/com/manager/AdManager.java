package com.flyingeffects.com.manager;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.ui.model.ShowPraiseModel;
import com.flyingeffects.com.utils.LogUtil;
import com.nineton.ntadsdk.bean.AdInfoBean;
import com.nineton.ntadsdk.itr.BannerAdCallBack;
import com.nineton.ntadsdk.itr.ImageAdCallBack;
import com.nineton.ntadsdk.itr.ScreenAdCallBack;
import com.nineton.ntadsdk.manager.BannerAdManager;
import com.nineton.ntadsdk.manager.ImageAdManager;
import com.nineton.ntadsdk.manager.ScreenAdManager;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class AdManager {
    private static final String TAG = "AdManager";
    private static AdManager thisModel;
    private BannerAdManager mBannerAdManager;

    public static AdManager getInstance() {
        if (thisModel == null) {
            thisModel = new AdManager();
        }
        return thisModel;
    }


    public void showCpAd(Context context, String id) {
        Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
            ScreenAdManager screenAdManager = new ScreenAdManager();
            screenAdManager.showScreenAd((Activity) context, id, new ScreenAdCallBack() {
                @Override
                public void onScreenAdShow() {
                    LogUtil.d("OOM", "onScreenAdShow");
                }

                @Override
                public void onScreenAdError(String errorMsg) {
                    LogUtil.d("OOM", "onScreenAdError=" + errorMsg);
                }

                @Override
                public void onScreenAdClose() {
                    LogUtil.d("OOM", "onScreenAdClose=");
                }

                @Override
                public boolean onScreenAdClicked(String title, String url, boolean isNtAd, boolean openURLInSystemBrowser) {
                    return false;
                }
            });
        });
    }


    public void showCpAd(Context context, String id, Callback callback) {
        Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
            ScreenAdManager screenAdManager = new ScreenAdManager();
            screenAdManager.showScreenAd((Activity) context, id, new ScreenAdCallBack() {
                @Override
                public void onScreenAdShow() {
                    callback.onScreenAdShow();
                    LogUtil.d("OOM", "onScreenAdShow");
                }

                @Override
                public void onScreenAdError(String errorMsg) {
                    callback.onScreenAdError();
                    LogUtil.d("OOM", "onScreenAdError=" + errorMsg);
                }

                @Override
                public void onScreenAdClose() {
                    callback.adClose();
                    LogUtil.d("OOM", "onScreenAdClose=");
                }

                @Override
                public boolean onScreenAdClicked(String title, String url, boolean isNtAd, boolean openURLInSystemBrowser) {
                    return false;
                }
            });
        });
    }

    ImageAdManager imageAdManager;

    public void showImageAd(Context context, String id, LinearLayout ll_ad_container) {
        if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
            imageAdManager = new ImageAdManager();
            imageAdManager.setNtAdUserId(BaseConstans.getUserId());
            imageAdManager.showImageAd(context, id, ll_ad_container, null, new ImageAdCallBack() {
                @Override
                public void onImageAdShow(View adView, String adId, String adPlaceId, AdInfoBean adInfoBean) {
                    if (adView != null) {
                        ll_ad_container.removeAllViews();
                        ll_ad_container.addView(adView);
                    }
                }

                @Override
                public void onImageAdError(String error) {
                    LogUtil.e("ImageAdError = " + error);
                }

                @Override
                public void onImageAdClose() {
                }

                @Override
                public boolean onImageAdClicked(String title, String url, boolean isNtAd, boolean openURLInSystemBrowser) {
                    return false;
                }
            });
        }
    }


    public void showImageAd(Context context, String id, LinearLayout ll_ad_container, Callback callback) {
        if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
            imageAdManager = new ImageAdManager();
            imageAdManager.showImageAd(context, id, ll_ad_container, null, new ImageAdCallBack() {
                @Override
                public void onImageAdShow(View adView, String adId, String adPlaceId, AdInfoBean adInfoBean) {
                    if (adView != null) {
                        ll_ad_container.removeAllViews();
                        ll_ad_container.addView(adView);
                        callback.adShow();
                    }
                }

                @Override
                public void onImageAdError(String error) {
                    LogUtil.e("ImageAdError = " + error);
                }

                @Override
                public void onImageAdClose() {
                    if(callback!=null){
                        callback.adClose();
                    }
                }

                @Override
                public boolean onImageAdClicked(String title, String url, boolean isNtAd, boolean openURLInSystemBrowser) {
                    return false;
                }
            });
        }
    }


    public static View AdBannerCacheView;


    /**
     * 加载banner广告
     */
    public void showBannerAd(Activity activity, String id, LinearLayout llAdContainer, boolean isNeedCache) {

        if (isNeedCache) {
            if (AdBannerCacheView != null) {
                llAdContainer.removeAllViews();
                if (AdBannerCacheView.getParent() != null) {
                    ViewGroup vp = (ViewGroup) AdBannerCacheView.getParent();
                    vp.removeAllViews();
                }
                llAdContainer.addView(AdBannerCacheView);
            } else {
                llAdContainer.setVisibility(View.GONE);
            }
        } else {
            mBannerAdManager = new BannerAdManager();
            llAdContainer.setVisibility(View.VISIBLE);
            llAdContainer.post(() -> mBannerAdManager.showBannerAd(activity, id, llAdContainer, new BannerAdCallBack() {
                @Override
                public void onBannerAdShow(View adView) {
                    if (adView != null) {
                        AdBannerCacheView = adView;
                        llAdContainer.removeAllViews();
                        llAdContainer.addView(AdBannerCacheView);
                    }
                }

                @Override
                public void onBannerAdError(String error) {
                    LogUtil.e("banner错误：" + error);
                }

                @Override
                public void onBannerAdClose() {
                    llAdContainer.setVisibility(View.GONE);
                }

                @Override
                public boolean onBannerAdClicked(String title, String url, boolean isNtAd, boolean openURLInSystemBrowser) {
                    return false;
                }
            }));
        }

    }

    public void releaseBannerManager() {
        if (mBannerAdManager != null) {
            LogUtil.d(TAG, "releaseBannerManager");
            mBannerAdManager.destory();
            mBannerAdManager = null;
        }
    }

    public void imageAdClose(LinearLayout llAdContainer) {
        llAdContainer.removeAllViews();
        if (imageAdManager != null) {
            imageAdManager.adDestroy();
        }
    }

    public void imageAdResume() {
        if (imageAdManager != null) {
            imageAdManager.adResume();
        }
    }

    public void imageAdPause() {
        if (imageAdManager != null) {
            imageAdManager.adPause();
        }
    }


    public interface Callback {
        void adShow();

        void adClose();
        void onScreenAdShow();
        void onScreenAdError();
    }

}
