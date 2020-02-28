package com.flyingeffects.com.ui.model;

import android.content.Context;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.ui.interfaces.model.homeItemMvpCallback;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;


public class home_fag_itemMvpModel {
    private homeItemMvpCallback callback;
    private Context context;
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private SmartRefreshLayout smartRefreshLayout;
    private boolean isRefresh = true;
    private ArrayList<new_fag_template_item> listData = new ArrayList<>();
    private int selectPage = 1;
    private String templateId;
    private int perPageCount=10;

    public home_fag_itemMvpModel(Context context, homeItemMvpCallback callback) {
        this.context = context;
        this.callback = callback;
    }



    public void requestData(String templateId, int num) {
        this.templateId = templateId;
//        if (num == 0) {
//            List<new_fag_template_item> data = Hawk.get("FagData"); //得到banner缓存数据
//            if (data != null && data.size() > 0) {
//                listData.addAll(data);
//                callback.showData(listData);
//            }
            requestFagData(false, true); //首页杂数据
//        } else {
//            requestFagData(false, true); //首页杂数据
//        }
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

    private void requestFagData(boolean isCanRefresh, boolean isSave) {
        HashMap<String, String> params = new HashMap<>();
        LogUtil.d("templateId", "templateId=" + templateId);
        params.put("category_id", templateId);
        params.put("page", selectPage + "");
        params.put("pageSize", perPageCount + "");
        Observable ob = Api.getDefault().getTemplate(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<new_fag_template_item>>(context) {
            @Override
            protected void _onError(String message) {
                finishData();
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(List<new_fag_template_item> data) {
//                String str= StringUtil.beanToJSONString(data);
//                LogUtil.d("OOM","_onNext="+str);
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
                }
                if (data.size() < perPageCount) {
                    smartRefreshLayout.setEnableLoadMore(false);
                }
                listData.addAll(data);
                callback.showData(listData);
            }
        }, "FagData", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, isSave, true, isCanRefresh);
    }


    private void finishData() {
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }




}

