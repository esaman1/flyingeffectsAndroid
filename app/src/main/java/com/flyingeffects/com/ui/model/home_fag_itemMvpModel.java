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
    private String templateId, tc_id;
    private int perPageCount = 10;
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
            perPageCount = 10;
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
                }
                if (data.size() < perPageCount) {
                    smartRefreshLayout.setEnableLoadMore(false);
                }
                listData.addAll(data);
                callback.showData(listData);
            }
        }, "FagData", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, isSave, true, false);
    }


    private void finishData() {
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }


}

