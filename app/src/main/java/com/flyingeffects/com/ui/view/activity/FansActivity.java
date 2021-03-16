package com.flyingeffects.com.ui.view.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.Fans_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.AttentionChange;
import com.flyingeffects.com.enity.fansEnity;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import de.greenrobot.event.Subscribe;
import rx.Observable;

/**
 * description ：粉丝
 * creation date: 2020/7/29
 * user : zhangtongju
 */
public class FansActivity extends BaseActivity {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private Fans_adapter adapter;
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
        ((TextView) findViewById(R.id.tv_top_title)).setText("粉丝");
        findViewById(R.id.iv_top_back).setOnClickListener(this);
        to_user_id = getIntent().getStringExtra("to_user_id");
        ShowData();
        initSmartRefreshLayout();
        requestMessageCount();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(BaseConstans.hasLogin()){
            selectPage=1;
            isRefresh=true;
            requestMessageCount();
        }
    }


    /**
     * description ：请求粉丝
     * creation date: 2020/7/29
     * user : zhangtongju
     */
    private void requestMessageCount() {
        HashMap<String, String> params = new HashMap<>();
        params.put("to_user_id", to_user_id);
        params.put("type", "1");
        params.put("page", selectPage + "");
        params.put("pageSize", "10");
        Observable ob = Api.getDefault().followerList(BaseConstans.getRequestHead(params));
        LogUtil.d("OOM2", StringUtil.beanToJSONString(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<fansEnity>>(FansActivity.this) {
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
    }


    private void ShowData() {
        adapter = new Fans_adapter(R.layout.list_fans_item, fansList, this);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.tv_follow:
                        requestFocus(fansList.get(position).getId(),fansList.get(position).getIs_has_follow(),position);
                        break;

                    default:
                        break;
                }
            }
        });

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(FansActivity.this, UserHomepageActivity.class);
                intent.putExtra("toUserId", fansList.get(position).getId());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }


    /**
     * description ：请求用户信息
     * creation date: 2020/7/30
     * user : zhangtongju
     */
    private int hasFollow;
    private void requestFocus(String to_user_id,int follow,int position) {
        hasFollow=follow;
        HashMap<String, String> params = new HashMap<>();
        params.put("to_user_id", to_user_id);

        // 启动时间
        Observable ob = Api.getDefault().followUser(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(this) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(Object data) {
                LogUtil.d("OOM", StringUtil.beanToJSONString(data));
                if(hasFollow==0){
                    hasFollow=1;
                }else{
                    hasFollow=0;
                }

                fansEnity fansE=fansList.get(position);
                fansE.setIs_has_follow(hasFollow);
                fansList.set(position,fansE);
                adapter.notifyItemChanged(position);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }


    @Subscribe
    public void onEventMainThread(AttentionChange event) {
//        if (from == 1) {
//            isRefresh = true;
//            selectPage = 1;
//            requestMessageCount();
//        }

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


}
