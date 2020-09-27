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
 * description ：点赞页面
 * creation date: 2020/7/29
 * user : zhangtongju
 */
public class FansActivity extends BaseActivity {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private Fans_adapter adapter;
    private String to_user_id;
    private List<fansEnity> fansList = new ArrayList<>();
    //0 表示我的页面 1 表示消息
    private int from;

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
        from=getIntent().getIntExtra("from",0);
        ShowData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(BaseConstans.hasLogin()){
            requestMessageCount();
        }else{

            ToastUtil.showToast(getResources().getString(R.string.need_login));
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
        Observable ob ;
        if(from==0){
            ob = Api.getDefault().followerList(BaseConstans.getRequestHead(params));
        }else{
            ob = Api.getDefault().getFollowList(BaseConstans.getRequestHead(params));
        }
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<fansEnity>>(FansActivity.this) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(List<fansEnity> data) {

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
        if(from==1&&!BaseConstans.hasLogin()){
            goActivity(LoginActivity.class);
        }
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

                        if(from==0){
                            //我的页面
                            requestFocus(fansList.get(position).getId());
                        }else{
                            //消息的粉丝
                            requestFocus(fansList.get(position).getUser_id());
                        }


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
                if(from==0){
                    intent.putExtra("toUserId", fansList.get(position).getId());
                }else{
                    intent.putExtra("toUserId", fansList.get(position).getUser_id());
                }
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
    private void requestFocus(String to_user_id) {
        HashMap<String, String> params = new HashMap<>();
        params.put("to_user_id", to_user_id);

        // 启动时间
        Observable ob = Api.getDefault().followUser(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(this) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(Object data) {
                LogUtil.d("OOM", StringUtil.beanToJSONString(data));
                requestMessageCount();
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }


    @Subscribe
    public void onEventMainThread(AttentionChange event) {
        if(from==1){
            isRefresh=true;
            selectPage=1;
            requestMessageCount();
        }

    }


}
