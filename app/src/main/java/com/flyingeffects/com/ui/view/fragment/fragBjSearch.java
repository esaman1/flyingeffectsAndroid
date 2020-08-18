package com.flyingeffects.com.ui.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.main_recycler_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.ListForUpAndDown;
import com.flyingeffects.com.enity.SendSearchText;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.view.activity.PreviewUpAndDownActivity;
import com.flyingeffects.com.utils.BackgroundExecutor;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;


/**
 * description ：背景页面，背景栏目下面模板列表，
 * creation date: 2020/4/20
 * param :
 * user : zhangtongju
 */

public class fragBjSearch extends BaseFragment {

    private int perPageCount = 10;
    @BindView(R.id.RecyclerView)
    RecyclerView recyclerView;
    private main_recycler_adapter adapter;
    private List<new_fag_template_item> allData = new ArrayList<>();
    @BindView(R.id.smart_refresh_layout_bj)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.lin_show_nodata_bj)
    LinearLayout lin_show_nodata;
    private boolean isRefresh = true;
    private int selectPage = 1;
    //默认值肯定为""
    private String searchText;
    //0 表示搜索出来模板 1表示搜索内容为背景
    private int isFrom;

    private boolean hasSearch=false;


    @Override
    protected int getContentLayout() {
        return R.layout.fag_bj_item;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            isFrom = bundle.getInt("from");
        }
        initRecycler();
        initSmartRefreshLayout();
    }

    @Override
    protected void initAction() {
        requestFagData(true);
    }

    @Override
    protected void initData() {

    }


    private void initRecycler() {
        adapter = new main_recycler_adapter(R.layout.list_main_item, allData, getActivity(), isFrom);
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            statisticsEventAffair.getInstance().setFlag(getActivity(), "11_yj_searchfor", allData.get(position).getTitle());
            Intent intent = new Intent(getActivity(), PreviewUpAndDownActivity.class);
            ListForUpAndDown listForUpAndDown = new ListForUpAndDown(allData);
            intent.putExtra("person", listForUpAndDown);//直接存入被序列化的对象实例
            intent.putExtra("templateId", "");//直接存入被序列化的对象实例
            intent.putExtra("position", position);
            intent.putExtra("nowSelectPage", selectPage);
            if (isFrom == 0) {
                //模板页面
                intent.putExtra("fromTo", FromToTemplate.ISTEMPLATE);
            } else {
                //背景页面
                intent.putExtra("fromTo", FromToTemplate.ISBJ);
            }
            startActivity(intent);


        });
    }


    /**
     * Fragment当前状态是否可见
     */
    protected boolean isVisible;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isVisible = true;
                    if (allData != null && allData.size() == 0) {
                        if(hasSearch){
                            ToastUtil.showToast("没有查询到输入内容，换个关键词试试");
                        }

                    }
                }
            }, 1000);

        } else {
            isVisible = false;
        }
    }


    public void initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            isOnRefresh();
            isRefresh = true;
            refreshLayout.setEnableLoadMore(true);
            selectPage = 1;
            requestFagData(true);
        });
        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
            isOnLoadMore();
            isRefresh = false;
            selectPage++;
            requestFagData(false);
        });
    }


    //得到banner缓存数据
    public void requestData() {
        requestFagData(true); //首页杂数据
    }


    private void finishData() {
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }

    public void isOnLoadMore() {

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d("OOM", "onDestroy");
        EventBus.getDefault().unregister(this);
    }

    public void isShowData(ArrayList<new_fag_template_item> listData) {
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


    private void requestFagData(boolean isShowDialog) {

        if(!TextUtils.isEmpty(searchText)){
            hasSearch=true;
            HashMap<String, String> params = new HashMap<>();
            params.put("search", searchText);
            params.put("page", selectPage + "");
            params.put("pageSize", perPageCount + "");
            if (isFrom == 0) {
                params.put("template_type", "1");
            } else {
                params.put("template_type", "2");
            }
            Observable ob = Api.getDefault().getTemplate(BaseConstans.getRequestHead(params));
            HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<new_fag_template_item>>(getActivity()) {
                @Override
                protected void _onError(String message) {
                    finishData();
//                ToastUtil.showToast(message);
                }

                @Override
                protected void _onNext(List<new_fag_template_item> data) {

                    finishData();
                    if (isRefresh) {
                        allData.clear();
                    }
                    if (isRefresh && data.size() == 0) {
                        statisticsEventAffair.getInstance().setFlag(getActivity(), "10_Noresults", searchText);

                        if (isVisible) {
                            ToastUtil.showToast("没有查询到输入内容，换个关键词试试");
                        }
                        if (isVisible) {
                            if (isFrom == 0) {
                                statisticsEventAffair.getInstance().setFlag(getActivity(), "4_search_none", searchText);
                            } else {
                                statisticsEventAffair.getInstance().setFlag(getActivity(), "4_search_none_bj", searchText);
                            }
                        }
                    }
                    if (!isRefresh && data.size() < perPageCount) {  //因为可能默认只请求8条数据
                        ToastUtil.showToast(getResources().getString(R.string.no_more_data));
                    }
                    if (data.size() < perPageCount) {
                        smartRefreshLayout.setEnableLoadMore(false);
                    }
                    allData.addAll(data);
                    adapter.notifyDataSetChanged();
                }
            }, "FagData", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, isShowDialog);
        }


    }


    @Subscribe
    public void onEventMainThread(SendSearchText event) {
        if (getActivity() != null) {
            LogUtil.d("OOM", event.getText());
            //搜索了内容
            searchText = event.getText();
            isRefresh = true;
            selectPage = 1;
            requestFagData(true);
        } else {
            ToastUtil.showToast("目标页面已销毁");
        }

    }


}
