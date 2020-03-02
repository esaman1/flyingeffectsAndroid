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
import com.flyingeffects.com.ui.interfaces.view.HomeItemMvpView;
import com.flyingeffects.com.ui.presenter.home_fag_itemMvpPresenter;
import com.flyingeffects.com.ui.view.activity.PreviewActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class home_item_fag extends BaseFragment implements HomeItemMvpView, main_recycler_adapter.showOnitemClick ,View.OnClickListener {

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


    @Override
    protected int getContentLayout() {
        return R.layout.fag_0_item;
    }


    @Override
    protected void initView() {
        Presenter = new home_fag_itemMvpPresenter(getActivity(), this);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            templateId = bundle.getString("id");
            actTag = bundle.getInt("num");
        }
        initRecycler();
        Presenter.initSmartRefreshLayout(smartRefreshLayout);
        Presenter.requestData(templateId, 0);
    }


    private void initRecycler() {
        adapter = new main_recycler_adapter(R.layout.list_main_item, allData, getActivity(), this, 0);
        layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent =new Intent(getActivity(), PreviewActivity.class);
            intent.putExtra("person",allData.get(position));//直接存入被序列化的对象实例
            startActivity(intent);
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

    @Override
    public void isShowData(ArrayList<new_fag_template_item> listData) {
        if (getActivity() != null) {
            allData.clear();
            allData.addAll(listData);
            adapter.notifyDataSetChanged();
        }
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

    @Override
    public void clickItem(int position) {

    }
}


