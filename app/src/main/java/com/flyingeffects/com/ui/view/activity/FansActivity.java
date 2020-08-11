package com.flyingeffects.com.ui.view.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.Fans_adapter;
import com.flyingeffects.com.adapter.Like_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.fansEnity;
import com.flyingeffects.com.enity.messageCount;
import com.flyingeffects.com.enity.systemessagelist;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
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

    @Override
    protected int getLayoutId() {
        return R.layout.act_fans;
    }

    @Override
    protected void initView() {
        ((TextView) findViewById(R.id.tv_top_title)).setText("粉丝");
        findViewById(R.id.iv_top_back).setOnClickListener(this);
        to_user_id=getIntent().getStringExtra("to_user_id");
    }


    @Override
    protected void onResume() {
        super.onResume();
        requestMessageCount();

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
        Observable ob = Api.getDefault().followerList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<fansEnity>>(FansActivity.this) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(List<fansEnity> data) {
                fansList = data;
                ShowData();
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
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


        recyclerView.setAdapter(adapter);
    }


}
