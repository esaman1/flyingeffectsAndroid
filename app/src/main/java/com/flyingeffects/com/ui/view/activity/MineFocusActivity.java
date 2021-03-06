package com.flyingeffects.com.ui.view.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.MineFocusAdapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.entity.AttentionChange;
import com.flyingeffects.com.entity.fansEnity;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
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
 * description ：我的关注
 * creation date: 2020/7/29
 * user : zhangtongju
 */
public class MineFocusActivity extends BaseActivity {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private MineFocusAdapter adapter;

    private String to_user_id;

    private List<fansEnity> fansList = new ArrayList<>();

    @BindView(R.id.smart_refresh_layout_bj)
    SmartRefreshLayout smartRefreshLayout;

    private boolean isRefresh = true;

    private int selectPage = 1;


    @Override
    protected int getLayoutId() {
        return R.layout.act_fans;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        ((TextView) findViewById(R.id.tv_top_title)).setText("关注");
        findViewById(R.id.iv_top_back).setOnClickListener(this);
        to_user_id = getIntent().getStringExtra("to_user_id");
        ShowData();
        if (BaseConstans.hasLogin()) {
            requestMessageCount();
        } else {
            ToastUtil.showToast(getResources().getString(R.string.need_login));
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * description ：我的关注
     * creation date: 2020/7/29
     * user : zhangtongju
     */
    private void requestMessageCount() {
        HashMap<String, String> params = new HashMap<>();
        params.put("to_user_id", to_user_id);
        params.put("type", "2");
        params.put("page", selectPage + "");
        params.put("pageSize", "10");

        Observable ob = Api.getDefault().followerList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<fansEnity>>(MineFocusActivity.this) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(List<fansEnity> data) {
                finishData();
                if (isRefresh) {
                    fansList.clear();
                }
                if (data.size() < 10) {
                    smartRefreshLayout.setEnableLoadMore(false);
                }
                fansList.addAll(data);
                adapter.notifyDataSetChanged();
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    private void finishData() {
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }


    @Override
    protected void initAction() {
        initSmartRefreshLayout();
    }


    private void ShowData() {
        adapter = new MineFocusAdapter(R.layout.list_mine_foucs_item, fansList, this);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(MineFocusActivity.this, UserHomepageActivity.class);
                intent.putExtra("toUserId", fansList.get(position).getId());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }


    public void initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            isRefresh = true;
            refreshLayout.setEnableLoadMore(true);
            selectPage = 1;
            requestMessageCount();
        });
        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
            isRefresh = false;
            selectPage++;
            requestMessageCount();
        });
    }


    @Subscribe
    public void onEventMainThread(AttentionChange event) {
        isRefresh=true;
        selectPage=1;
        requestMessageCount();
    }

}
