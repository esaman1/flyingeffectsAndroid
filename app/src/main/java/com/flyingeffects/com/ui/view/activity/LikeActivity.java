package com.flyingeffects.com.ui.view.activity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.Like_adapter;
import com.flyingeffects.com.adapter.main_recycler_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.MineCommentEnity;
import com.flyingeffects.com.enity.MyProduction;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.enity.systemessagelist;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.utils.BackgroundExecutor;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Observable;

/**
 * description ：点赞页面和评论页面，公用的
 * creation date: 2020/7/29
 * user : zhangtongju
 */
public class LikeActivity extends BaseActivity {

    private int perPageCount = 10;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private Like_adapter adapter;

    private List<MineCommentEnity> listData=new ArrayList<>();

    private boolean isRefresh = true;
    private int selectPage = 1;

    @BindView(R.id.smart_refresh_layout_bj)
    SmartRefreshLayout smartRefreshLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.act_like;
    }

    @Override
    protected void initView() {
        ((TextView) findViewById(R.id.tv_top_title)).setText("赞");
        findViewById(R.id.iv_top_back).setOnClickListener(this);
        initSmartRefreshLayout();
        adapter = new Like_adapter(R.layout.list_like_item, listData, this);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initAction() {

    }


    @Override
    protected void onResume() {
        super.onResume();
        requestCommentList(true);
    }


    /**
     * description ：请求我的评论列表
     * creation date: 2020/8/6
     * user : zhangtongju
     */
    private void requestCommentList(boolean isShowDialog) {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", selectPage + "");
        params.put("pageSize", perPageCount + "");
        Observable ob = Api.getDefault().commentList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<MineCommentEnity>>(LikeActivity.this) {
            @Override
            protected void _onError(String message) {
                finishData();
                Log.e(TAG, "_onError: " + message);
            }

            @Override
            protected void _onNext(List<MineCommentEnity> data) {
                LogUtil.d("OOM", StringUtil.beanToJSONString(data));

                finishData();
                if (isRefresh) {
                    listData.clear();
                }
                if (!isRefresh && data.size() < perPageCount) {  //因为可能默认只请求8条数据
                    ToastUtil.showToast(getResources().getString(R.string.no_more_data));
                }
                if (data.size() < perPageCount) {
                    smartRefreshLayout.setEnableLoadMore(false);
                }
                listData.addAll(data);
                isShowData(listData);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, isShowDialog);
    }



    public void initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            isRefresh = true;
            refreshLayout.setEnableLoadMore(true);
            selectPage = 1;
            requestCommentList(false );
        });
        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
            isRefresh = false;
            selectPage++;
            requestCommentList(false);
        });
    }
    private boolean isFirstData = true;
    public void isShowData(List<MineCommentEnity> data) {
            adapter.notifyDataSetChanged();
            if (isFirstData) {
                BackgroundExecutor.execute(() -> {
                    isFirstData = false;
                });
            }
    }

    private void finishData() {
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }



}
