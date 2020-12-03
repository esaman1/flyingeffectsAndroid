package com.flyingeffects.com.ui.view.fragment;

import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.main_recycler_adapter;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * description ：换装
 * creation date: 2020/12/1
 * user : zhangtongju
 */
public class DressUpFragment extends BaseFragment {

    private List<new_fag_template_item> allData = new ArrayList<>();

    @BindView(R.id.smart_refresh_layout_bj)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.lin_show_nodata_bj)
    LinearLayout lin_show_nodata;

    private ArrayList<new_fag_template_item> listData = new ArrayList<>();

    private boolean isRefresh = true;

    private int selectPage = 1;

    private int perPageCount = 10;

    private main_recycler_adapter adapter;

    @BindView(R.id.RecyclerView)
    RecyclerView recyclerView;

    @Override
    protected int getContentLayout() {
        return 0;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initAction() {
    }

    @Override
    protected void initData() {

    }







}
