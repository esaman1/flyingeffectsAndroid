package com.flyingeffects.com.ui.model;

import android.app.Activity;
import android.content.Context;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.CommonNewsBean;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.ui.interfaces.model.homeItemMvpCallback;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.screenUtil;
import com.nineton.ntadsdk.bean.FeedAdConfigBean;
import com.nineton.ntadsdk.itr.FeedAdCallBack;
import com.nineton.ntadsdk.manager.FeedAdManager;
import com.nineton.ntadsdk.utils.DeviceUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.BAIDU_FEED_AD_EVENT;
import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.GDT_FEED_AD_EVENT;
import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.TT_FEED_AD_EVENT;
import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.TYPE_GDT_FEED_EXPRESS_AD;
import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.TYPE_TT_FEED_EXPRESS_AD;


public class home_fag_itemMvpModel {
    private homeItemMvpCallback callback;
    private Context context;
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private SmartRefreshLayout smartRefreshLayout;
    private boolean isRefresh = true;
    private ArrayList<new_fag_template_item> listData = new ArrayList<>();
    private int selectPage = 1;
    private String templateId, tc_id;
    private int perPageCount = 9;
    /**
     * 1是模板 2是背景
     */
    private int template_type;
    private int fromType;

    public home_fag_itemMvpModel(Context context, homeItemMvpCallback callback, int fromType) {
        this.context = context;
        this.callback = callback;
        this.fromType = fromType;
        template_type = template_type == 0 ? 1 : 2;
        mAdManager = new FeedAdManager();
    }


    public int getselectPage() {
        return selectPage;
    }


    public void requestData(String templateId, String tc_id, int num) {
        this.templateId = templateId;
        this.tc_id = tc_id;
        //首页杂数据
        requestFagData(true, true);
    }


    public void initSmartRefreshLayout(SmartRefreshLayout smartRefreshLayout) {
        this.smartRefreshLayout = smartRefreshLayout;
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            callback.isOnRefresh();
            isRefresh = true;
            refreshLayout.setEnableLoadMore(true);
            selectPage = 1;
            requestFagData(false, true);
        });
        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
            callback.isOnLoadMore();
            isRefresh = false;
            selectPage++;
            requestFagData(false, false);
        });
    }


    /**
     * description ：
     * creation date: 2020/3/11
     * param : template_type  1是模板 2是背景
     * user : zhangtongju
     */
    private void requestFagData(boolean needRefresh, boolean isSave) {
        HashMap<String, String> params = new HashMap<>();
        LogUtil.d("templateId", "templateId=" + templateId);
        params.put("category_id", templateId);
        if (needRefresh) {
            selectPage = 1;
            perPageCount = 9;
        }
        if (fromType == 4) {
            params.put("template_type", "3");
        } else {
            params.put("template_type", "1");
        }
        if (Integer.parseInt(tc_id) >= 0) {
            params.put("tc_id", tc_id);
        }
        params.put("search", "");
        params.put("page", selectPage + "");
        params.put("pageSize", perPageCount + "");
        Observable ob;
        if(fromType == 4){
            ob = Api.getDefault().getMeargeTemplate(BaseConstans.getRequestHead(params));
        }else{
            ob = Api.getDefault().getTemplate(BaseConstans.getRequestHead(params));
        }
        LogUtil.d("OOM", StringUtil.beanToJSONString(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<new_fag_template_item>>(context) {
            @Override
            protected void onSubError(String message) {
                finishData();
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(List<new_fag_template_item> data) {
                String str = StringUtil.beanToJSONString(data);
                LogUtil.dLong("OOM", "_onNext=" + str);
                finishData();
                if (isRefresh) {
                    listData.clear();
                }

                if (isRefresh && data.size() == 0) {
                    callback.showNoData(true);
                } else {
                    callback.showNoData(false);
                }

                if (!isRefresh && data.size() < perPageCount) {  //因为可能默认只请求8条数据
                    ToastUtil.showToast(context.getResources().getString(R.string.no_more_data));
                    smartRefreshLayout.setEnableLoadMore(false);
                }

                if(BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()&&data.size()>BaseConstans.NOWADSHOWPOSITION){
                    new_fag_template_item item=new new_fag_template_item();
                    item.setHasShowAd(true);
                    data.add(BaseConstans.NOWADSHOWPOSITION,item);

                }
                listData.addAll(data);
                callback.showData(listData);

                if(BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()){
                    requestAd();
                }
            }
        }, "FagData", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, isSave, true, false);
    }




    /**
     * description ：请求信息流广告
     * creation date: 2021/3/10
     * user : zhangtongju
     */

    private FeedAdManager mAdManager;
    public void requestAd(){
        LogUtil.d("OOM2","requestAd");
        float needScreenWidth=DeviceUtil.getScreenWidthInPX(context)/(float)2- screenUtil.dip2px(context,10);
        mAdManager.setViewWidth((int) needScreenWidth);
        mAdManager.getFeedAd((Activity) context, AdConfigs.AD_FEED, new FeedAdCallBack() {
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
//                com.nineton.ntadsdk.utils.LogUtil.e("close == " + adIndex);
//                if (type != TYPE_GDT_FEED_EXPRESS_AD) {
//                    mAdAdapter.remove(adIndex);
//                }
            }

            @Override
            public void onFeedAdExposed() {
                com.nineton.ntadsdk.utils.LogUtil.e("onFeedAdExposed");
            }

            @Override
            public boolean onFeedAdClicked(String title, String url, boolean isNtAd, boolean openURLInSystemBrowser, int adapterPosition) {
                com.nineton.ntadsdk.utils.LogUtil.e("onFeedAdClicked" + adapterPosition);
                return false;
            }
        });

    }











    private void finishData() {
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }


}

