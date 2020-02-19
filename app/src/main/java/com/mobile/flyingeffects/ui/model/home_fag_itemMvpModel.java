package com.mobile.flyingeffects.ui.model;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mobile.flyingeffects.R;
import com.mobile.flyingeffects.constans.BaseConstans;
import com.mobile.flyingeffects.enity.new_fag_template_item;
import com.mobile.flyingeffects.http.Api;
import com.mobile.flyingeffects.http.HttpUtil;
import com.mobile.flyingeffects.http.ProgressSubscriber;
import com.mobile.flyingeffects.ui.interfaces.model.homeItemMvpCallback;
import com.mobile.flyingeffects.utils.LogUtil;
import com.mobile.flyingeffects.utils.ToastUtil;
import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;


public class home_fag_itemMvpModel {
    private homeItemMvpCallback callback;
    private Context context;
    private int bannerCount = 3; //banner的个数
    private ImageView[] img_dian;
    private Timer timer;
    private boolean isExecuteViewPager = true; //是否可以执行viewpager
    private SmartRefreshLayout smartRefreshLayout;
    private boolean isRefresh = true;
    private ArrayList<new_fag_template_item> listData = new ArrayList<>();
    private int selectPage = 1;
    private String templateId;
    private int perPageCount; //每一页显示广告的数量

    public home_fag_itemMvpModel(Context context, homeItemMvpCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    /**
     * user :TongJu  ;描述：设置选中
     * 时间：2018/5/7
     **/
    public void ChoosePoint(int ChoosePosition) {
        for (int i = 0; i < bannerCount; i++) {
            img_dian[i].setImageResource(R.mipmap.point_write_lucency);
        }
        img_dian[ChoosePosition].setImageResource(R.mipmap.point_write_lucency);
    }


    public void requestData(String templateId, int num) {
        this.templateId = templateId;
        if (num == 0) {
            List<new_fag_template_item> data = Hawk.get("FagData"); //得到banner缓存数据
            if (data != null && data.size() > 0) {
                listData.addAll(data);
                callback.showData(listData);
            }
            requestFagData(false, true); //首页杂数据
        } else {
            requestFagData(false, true); //首页杂数据
        }
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
        params.put("classification", templateId);
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

                insertionAdvertising(data);
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

