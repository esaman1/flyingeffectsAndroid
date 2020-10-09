package com.flyingeffects.com.ui.view.activity;

import android.util.Log;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Observable;


/**
 * description ：系统消息
 * creation date: 2020/10/9
 * user : zhangtongju
 */


public class SystemMessageDetailActivity extends BaseActivity {

    private int perPageCount = 10;

    private boolean isRefresh = true;
    private int selectPage = 1;

    @BindView(R.id.smart_refresh_layout)
    SmartRefreshLayout smartRefreshLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.act_system_message_detail;
    }

    @Override
    protected void initView() {
        initSmartRefreshLayout();
    }

    @Override
    protected void initAction() {

    }


    public void initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            isRefresh = true;
            refreshLayout.setEnableLoadMore(true);
            selectPage = 1;
            requestSystemDetail(false);
        });
        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
            isRefresh = false;
            selectPage++;
            requestSystemDetail(false);
        });
    }


    /**
     * description ：请求系统消息
     * creation date: 2020/8/6
     * user : zhangtongju
     */
    private void requestSystemDetail(boolean isShowDialog) {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", selectPage + "");
        params.put("pageSize", perPageCount + "");
        Observable ob = Api.getDefault().systemessageinfo(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(SystemMessageDetailActivity.this) {
            @Override
            protected void _onError(String message) {
                finishData();
                Log.e("OOM", "_onError: " + message);
            }

            @Override
            protected void _onNext(Object data) {
                LogUtil.d("OOM", StringUtil.beanToJSONString(data));
                finishData();
//                if (isRefresh) {
//                    listData.clear();
//                }
//                if (!isRefresh && data.size() < perPageCount) {  //因为可能默认只请求8条数据
//                    ToastUtil.showToast(getResources().getString(R.string.no_more_data));
//                }
//                if (data.size() < perPageCount) {
//                    smartRefreshLayout.setEnableLoadMore(false);
//                }
//                listData.addAll(data);
//                isShowData(listData);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, isShowDialog);
    }



    private void finishData() {
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }



}
