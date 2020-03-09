package com.flyingeffects.com.ui.view.fragment;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.main_recycler_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.ui.view.activity.PreviewActivity;
import com.flyingeffects.com.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Observable;


/***
 * 我的收藏
 */

public class frag_user_collect extends BaseFragment {
    private boolean isRefresh = true;

    private BaseQuickAdapter adapter;

    private List<new_fag_template_item> allData = new ArrayList<>();
    @BindView(R.id.smart_refresh_layout)
    SmartRefreshLayout smartRefreshLayout;
    private int perPageCount = 10;

    @BindView(R.id.RecyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.tv_hint)
    TextView tv_hint;

    ArrayList<new_fag_template_item> listData = new ArrayList<>();

    private int selectPage = 1;

    private StaggeredGridLayoutManager layoutManager;

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


    private void requestCollectionList(boolean isShowDialog) {
        tv_hint.setVisibility(View.GONE);
        HashMap<String, String> params = new HashMap<>();
        params.put("token", BaseConstans.GetUserToken());
        params.put("page", selectPage + "");
        params.put("pageSize", perPageCount + "");
        Observable ob = Api.getDefault().collectionList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<new_fag_template_item>>(getActivity()) {
            @Override
            protected void _onError(String message) {
            }

            @Override
            protected void _onNext(List<new_fag_template_item> data) {
                finishData();
                if (isRefresh) {
                    listData.clear();
                }
                if (data.size() == 0) {
                    tv_hint.setVisibility(View.VISIBLE);
                    tv_hint.setText("暂无收藏模板");
                }
                if (!isRefresh && data.size() < perPageCount) {  //因为可能默认只请求8条数据
                    ToastUtil.showToast(getResources().getString(R.string.no_more_data));
                }
                if (data.size() < perPageCount) {
                    smartRefreshLayout.setEnableLoadMore(false);
                }
                listData.addAll(data);
                showData(listData);

            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, isShowDialog);
    }


    private void showData(ArrayList<new_fag_template_item> listData) {
        if (getActivity() != null) {
            allData.clear();
            allData.addAll(listData);
            adapter.notifyDataSetChanged();
        }
    }

    private void finishData() {
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }


    public void initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            isRefresh = true;
            refreshLayout.setEnableLoadMore(true);
            selectPage = 1;
            requestCollectionList(false);
        });
        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
            isRefresh = false;
            selectPage++;
            requestCollectionList(false);
        });
    }


    @Override
    protected void initData() {

    }

    @Override
    public void onResume() {
        if (BaseConstans.hasLogin()) {
            isRefresh = true;
            selectPage=1;
            smartRefreshLayout.setEnableLoadMore(true);
            requestCollectionList(false);
        } else {
            tv_hint.setVisibility(View.VISIBLE);
            tv_hint.setText("暂无收藏模板");
            allData.clear();
            adapter.notifyDataSetChanged();
        }
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    private void initRecycler() {
        adapter = new main_recycler_adapter(R.layout.list_main_item, allData, getActivity(), null, 0);
        layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (!DoubleClick.getInstance().isFastDoubleClick()) {
                Intent intent = new Intent(getActivity(), PreviewActivity.class);
                intent.putExtra("person", allData.get(position));//直接存入被序列化的对象实例
                startActivity(intent);
            }
        });
    }


}


