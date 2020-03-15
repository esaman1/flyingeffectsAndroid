package com.flyingeffects.com.ui.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.main_recycler_adapter;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.view.HomeItemMvpView;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.presenter.home_fag_itemMvpPresenter;
import com.flyingeffects.com.ui.view.activity.PreviewActivity;
import com.flyingeffects.com.utils.BackgroundExecutor;
import com.flyingeffects.com.utils.LogUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class home_item_fag extends BaseFragment implements HomeItemMvpView ,View.OnClickListener {

    private home_fag_itemMvpPresenter Presenter;
    @BindView(R.id.RecyclerView)
    RecyclerView  recyclerView;
    private main_recycler_adapter adapter;
    private List<new_fag_template_item> allData = new ArrayList<>();
    private String templateId = "";
    private StaggeredGridLayoutManager layoutManager;
    private int actTag;
    @BindView(R.id.smart_refresh_layout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.lin_show_nodata)
    LinearLayout lin_show_nodata;
    /**
     * 0 表示来做模板，1表示来自背景
     */
    private int fromType;



    @Override
    protected int getContentLayout() {
        return R.layout.fag_0_item;
    }


    @Override
    protected void initView() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            templateId = bundle.getString("id");
            actTag = bundle.getInt("num");
            fromType=bundle.getInt("from");
        }
        LogUtil.d("OOM","2222fromType="+fromType);
        Presenter = new home_fag_itemMvpPresenter(getActivity(), this,fromType);
        initRecycler();
        Presenter.initSmartRefreshLayout(smartRefreshLayout);
        Presenter.requestData(templateId, actTag);
    }


    private void initRecycler() {
        adapter = new main_recycler_adapter(R.layout.list_main_item, allData, getActivity(),fromType);
        layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if(!DoubleClick.getInstance().isFastDoubleClick()){
                statisticsEventAffair.getInstance().setFlag(getActivity(), "1_mb_click", allData.get(position).getTitle());
                Intent intent =new Intent(getActivity(), PreviewActivity.class);
                if(fromType==0){
                    intent.putExtra("fromTo", FromToTemplate.ISFROMTEMPLATE);
                }else{
                    intent.putExtra("fromTo", FromToTemplate.ISFROMBJ);
                }
                intent.putExtra("person",allData.get(position));//直接存入被序列化的对象实例
                startActivity(intent);
            }
        });
    }






    @Override
    protected void initAction() {
    }




    @Override
    protected void initData() {

    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onStop() {
        super.onStop();
    }




    @Override
    public void isOnRefresh() {
    }

    @Override
    public void isOnLoadMore() {

    }

    private boolean isFirstData = true;
    @Override
    public void isShowData(ArrayList<new_fag_template_item> listData) {
        if (getActivity() != null) {
            allData.clear();
            allData.addAll(listData);
            adapter.notifyDataSetChanged();
            if (isFirstData) {
                BackgroundExecutor.execute(() -> {
                    startStatistics();
                    isFirstData = false;
                });
            }
        }
    }

    private void startStatistics() {
        int[] mFirstVisibleItems = null;
        int[] mLastVisibleItems = null;
        mFirstVisibleItems = layoutManager.findFirstVisibleItemPositions(mFirstVisibleItems);
        mLastVisibleItems = layoutManager.findLastVisibleItemPositions(mLastVisibleItems);
        statisticsCount(mFirstVisibleItems, mLastVisibleItems);
    }
    private ArrayList<Integer> lastData = new ArrayList<>();
    private ArrayList<Integer> nowData = new ArrayList<>();

    private void statisticsCount(int[] data, int[] data2) {
        int end;
        int start = data[0];
        if (data2.length > 0) {
            end = data2[1];
        } else {
            end = data2[0];
        }
        if (start != -1 && end != -1) {
            nowData.clear();
            for (int i = start; i <= end; i++) {
                nowData.add(i);
                if (!hasIncludeNum(i)) {
                    statisticsEventAffair.getInstance().setFlag(getActivity(), "1_mb_screen", allData.get(i).getTitle());
                }
            }
            lastData.clear();
            lastData.addAll(nowData);
        }
    }


    private boolean hasIncludeNum(int num) {
        for (int i = 0; i < lastData.size(); i++) {
            if (lastData.get(i) == num) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void showNoData(boolean isShowNoData) {
        if(isShowNoData){
            lin_show_nodata.setVisibility(View.VISIBLE);
        }else{
            lin_show_nodata.setVisibility(View.GONE);
        }

    }


    @Override
    public void onClick(View view) {

    }

}


