package com.mobile.flyingeffects.ui.view.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.GridLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mobile.flyingeffects.R;
import com.mobile.flyingeffects.adapter.main_recycler_adapter;
import com.mobile.flyingeffects.adapter.user_collect_adapter;
import com.mobile.flyingeffects.base.BaseFragment;
import com.mobile.flyingeffects.enity.new_fag_template_item;
import com.mobile.flyingeffects.enity.user_collect;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class frag_user_collect extends BaseFragment {
    private int selectPage = 1;
    private boolean isRefresh = true;

    private BaseQuickAdapter adapter;

    @BindView(R.id.smart_refresh_layout)
    SmartRefreshLayout smartRefreshLayout;


    @BindView(R.id.RecyclerView)
    RecyclerView recyclerView;



    @Override
    protected int getContentLayout() {
        return R.layout.frg_user_collect;
    }


    @Override
    protected void initView() {
        initSmartRefreshLayout();
    }

    @Override
    protected void initAction() {

        initRecycler();
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

    public void initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            isRefresh = true;
            refreshLayout.setEnableLoadMore(true);
            selectPage = 1;
        });


        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
            isRefresh = false;
            selectPage++;
        });
    }


    private void initRecycler() {
        List<new_fag_template_item> list=new ArrayList<>();
        for (int i=0;i<10;i++){
            new_fag_template_item item=new new_fag_template_item();
            item.setTitle("123");
            list.add(item);
        }
        adapter = new main_recycler_adapter(R.layout.list_main_item, list, getActivity(), null, 0);
        StaggeredGridLayoutManager      layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }


}


