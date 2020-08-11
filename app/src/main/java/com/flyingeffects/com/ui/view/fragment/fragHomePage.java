package com.flyingeffects.com.ui.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.frag_home_page_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.MyProduction;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.utils.BackgroundExecutor;
import com.flyingeffects.com.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Observable;


/**
 * description ：用户的喜欢和收藏，
 * creation date: 2020/4/20
 * param :
 * user : zhangtongju
 */
public class fragHomePage extends BaseFragment {

    private int perPageCount = 10;
    @BindView(R.id.RecyclerView)
    RecyclerView recyclerView;
    private frag_home_page_adapter adapter;
    private List<MyProduction> allData = new ArrayList<>();
    private String toUserId = "";
    //0 shi 1 是喜欢
    private int isFrom;
    @BindView(R.id.smart_refresh_layout_bj)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.lin_show_nodata_bj)
    LinearLayout lin_show_nodata;
    private boolean isRefresh = true;
    private ArrayList<MyProduction> listData = new ArrayList<>();
    private int selectPage = 1;



    @Override
    protected int getContentLayout() {
        return R.layout.fag_home_page_item;
    }

    @Override
    protected void initView() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            toUserId = bundle.getString("toUserId");
            isFrom=bundle.getInt("isFrom");
        }
        initRecycler();
        initSmartRefreshLayout();
    }

    @Override
    protected void initAction() {
        requestFagData(true, true);
    }

    @Override
    protected void initData() {

    }


    private void initRecycler() {
        adapter = new frag_home_page_adapter(R.layout.list_home_page_item, allData, getActivity());
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {

        });
    }


    public void initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            isOnRefresh();
            isRefresh = true;
            refreshLayout.setEnableLoadMore(true);
            selectPage = 1;
            requestFagData(false, true);
        });
        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
            isOnLoadMore();
            isRefresh = false;
            selectPage++;
            requestFagData(false, false);
        });
    }


    //得到banner缓存数据
    public void requestData() {
            requestFagData(false, false); //首页杂数据
    }

    /**
     * description ：
     * creation date: 2020/3/11
     * param : template_type  1是模板 2是背景
     * user : zhangtongju
     */
    private void requestFagData(boolean isCanRefresh, boolean isSave) {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", selectPage + "");
        params.put("to_user_id",toUserId);
        params.put("type",isFrom+"");
        params.put("pageSize", perPageCount + "");
        Observable ob = Api.getDefault().getMyProduction(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<MyProduction>>(getActivity()) {
            @Override
            protected void _onError(String message) {
                finishData();
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(List<MyProduction> data) {
                finishData();
                if (isRefresh) {
                    listData.clear();

                }
                if (isRefresh && data.size() == 0) {
                    showNoData(true);
                } else {
                    showNoData(false);
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
        }, "fagBjItem", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, isSave, true, isCanRefresh);
    }


    private void finishData() {
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }

    public void isOnLoadMore() {

    }

    @Override
    public void onResume() {
        super.onResume();
        requestData();
    }

    public void isOnRefresh() {
    }

    public void showNoData(boolean isShowNoData) {
        if (isShowNoData) {
            lin_show_nodata.setVisibility(View.VISIBLE);
        } else {
            lin_show_nodata.setVisibility(View.GONE);
        }
    }

    public void isShowData(ArrayList<MyProduction> listData) {
        if (getActivity() != null) {
            allData.clear();
            allData.addAll(listData);
            adapter.notifyDataSetChanged();
            if (isFirstData) {
                BackgroundExecutor.execute(() -> {
                    isFirstData = false;
                });
            }
        }
    }

    private boolean isFirstData = true;

}
