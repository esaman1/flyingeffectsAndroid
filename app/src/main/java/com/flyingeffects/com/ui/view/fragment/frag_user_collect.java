package com.flyingeffects.com.ui.view.fragment;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.main_recycler_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.UserInfo;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.ui.view.activity.LoginActivity;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
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
        requestCollectionList();
    }


    private void requestCollectionList() {
        HashMap<String, String> params = new HashMap<>();
        params.put("token", BaseConstans.GetUserToken());
        Observable ob = Api.getDefault().collectionList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(getActivity()) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(Object data) {
                String str=StringUtil.beanToJSONString(data);
                LogUtil.d("OOM",str);

            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
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


