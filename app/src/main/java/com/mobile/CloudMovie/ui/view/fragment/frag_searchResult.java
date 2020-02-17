package com.mobile.CloudMovie.ui.view.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.GridLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mobile.CloudMovie.R;
import com.mobile.CloudMovie.adapter.long_movie_adapter;
import com.mobile.CloudMovie.base.BaseFragment;
import com.mobile.CloudMovie.enity.starEnity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class frag_searchResult extends BaseFragment {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;


    private BaseQuickAdapter adapter;


    @Override
    protected int getContentLayout() {
        return R.layout.list_item_search_movie_result;
    }

    @Override
    protected void initView() {


    }

    @Override
    protected void initAction() {
        initRecycler
                ();
    }

    @Override
    protected void initData() {

    }

    private void initRecycler() {
        List<starEnity> list=new ArrayList<>();
        for (int i=0;i<10;i++){
            starEnity entiy=new starEnity();
            entiy.setName("12312");
            list.add(entiy);
        }
        adapter = new long_movie_adapter(R.layout.item_long_movie, list, getActivity());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        //设置RecycleView显示的方向是水平还是垂直 GridLayout.HORIZONTAL水平  GridLayout.VERTICAL默认垂直
        gridLayoutManager.setOrientation(GridLayout.VERTICAL );
        //设置布局管理器， 参数gridLayoutManager对象
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);//解决滑动不流畅
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
    }


}
