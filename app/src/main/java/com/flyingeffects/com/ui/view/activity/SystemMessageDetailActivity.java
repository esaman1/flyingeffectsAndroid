package com.flyingeffects.com.ui.view.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.SystemMessageDetailAdapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.entity.ListForUpAndDown;
import com.flyingeffects.com.entity.NewFragmentTemplateItem;
import com.flyingeffects.com.entity.SystemMessageDetailAllEnity;
import com.flyingeffects.com.entity.SystemMessageDetailEnity;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Observable;


/**
 * description ：系统消息
 * creation date: 2020/10/9
 * user : zhangtongju
 */


public class SystemMessageDetailActivity extends BaseActivity {

    private int perPageCount = 10;

    private boolean isRefresh = true;
    private int selectPage = 1;

    @BindView(R.id.smart_refresh_layout)
    SmartRefreshLayout smartRefreshLayout;


    @BindView(R.id.RecyclerView)
    RecyclerView recyclerView;

    private List<SystemMessageDetailEnity> dataList=new ArrayList<>();

    private SystemMessageDetailAdapter adapter;
    private String needId;


    @Override
    protected int getLayoutId() {
        return R.layout.act_system_message_detail;
    }

    @Override
    protected void initView() {
        findViewById(R.id.iv_top_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_top_title)).setText("系统消息");
        initSmartRefreshLayout();
        needId =getIntent().getStringExtra("needId");
        adapter = new SystemMessageDetailAdapter(R.layout.item_system_message_detail, dataList,this);
        adapter.setOnItemClickListener((adapter, view, position) -> {
        });


        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()){
                case R.id.tv_make:
                    StatisticsEventAffair.getInstance().setFlag(this, "12_system_click",dataList.get(position).getContent());
                    requestTemplateDetail(dataList.get(position).getTemplate_id());
                    requestMessageStatistics("3",dataList.get(position).getId(),dataList.get(position).getTemplate_id());
                    break;

                default:
                    break;
            }

        });
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        requestSystemDetail(true);
        requestMessageStatistics("2","","");
    }


    private List<NewFragmentTemplateItem> allData = new ArrayList<>();
    public void requestTemplateDetail(String templateId) {
        if (!TextUtils.isEmpty(templateId)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("template_id", templateId);

            // 启动时间
            Observable ob = Api.getDefault().templateLInfo(BaseConstans.getRequestHead(params));
            HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<NewFragmentTemplateItem>(this) {
                @Override
                protected void onSubError(String message) {
                    LogUtil.d("OOM", "requestTemplateDetail-error=" + message);
                }

                @Override
                protected void onSubNext(NewFragmentTemplateItem data) {
                    allData.clear();
                    Intent intent =new Intent(SystemMessageDetailActivity.this,PreviewUpAndDownActivity.class);
                    String type = data.getTemplate_type();
                    allData.add(data);
                    ListForUpAndDown listForUpAndDown = new ListForUpAndDown(allData);
                    intent.putExtra("person", listForUpAndDown);//直接存入被序列化的对象实例
                    intent.putExtra("position", 0);
                    intent.putExtra("fromToMineCollect", false);
                    intent.putExtra("nowSelectPage", 1);
                    intent.putExtra("templateId", data.getTemplate_id());
                    intent.putExtra("isCanLoadMore", false);
                    if (!TextUtils.isEmpty(type) && "2".equals(type)) {
                        intent.putExtra("fromTo", FromToTemplate.ISBJ);
                    } else {
                        intent.putExtra("fromTo", FromToTemplate.ISTEMPLATE);
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
        }
    }

    @Override
    protected void initAction() {
        StatisticsEventAffair.getInstance().setFlag(this, "12_system_screen");

    }


    public void initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            isRefresh = true;
            refreshLayout.setEnableLoadMore(true);
            selectPage = 1;
            requestSystemDetail(false);
        });
        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
            isRefresh = false;
            selectPage++;
            requestSystemDetail(false);
        });
    }


    /**
     * description ：请求系统消息
     * creation date: 2020/8/6
     * user : zhangtongju
     */
    private void requestSystemDetail(boolean isShowDialog) {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", selectPage + "");
        params.put("pageSize", perPageCount + "");
        params.put("user_id", BaseConstans.getUserId());
        LogUtil.d("OOM",needId);
        params.put("id", needId);
        Observable ob = Api.getDefault().systemessageinfo(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<SystemMessageDetailAllEnity>(SystemMessageDetailActivity.this) {
            @Override
            protected void onSubError(String message) {
                finishData();
                Log.e("OOM", "_onError: " + message);
            }

            @Override
            protected void onSubNext(SystemMessageDetailAllEnity AllData) {
                List<SystemMessageDetailEnity> data=AllData.getList();
                finishData();
                if (isRefresh) {
                    dataList.clear();
                }
                if (!isRefresh && data.size() < perPageCount) {  //因为可能默认只请求8条数据
                    ToastUtil.showToast(getResources().getString(R.string.no_more_data));
                }
                if (data.size() < perPageCount) {
                    smartRefreshLayout.setEnableLoadMore(false);
                }
                dataList.addAll(data);
                adapter.notifyDataSetChanged();
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, isShowDialog);
    }





    /**
     * description ：消息页面后台统计
     * type 1=模板制作次数,2=消息已读次数3=消息点击次数,
     * creation date: 2020/8/6
     * user : zhangtongju
     */
    private void requestMessageStatistics(String type,String message_id,String template_id) {
        HashMap<String, String> params = new HashMap<>();
        params.put("template_id", template_id);
        params.put("type", type);
        params.put("message_id", message_id );
        Observable ob = Api.getDefault().addTimes(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<SystemMessageDetailAllEnity>(SystemMessageDetailActivity.this) {
            @Override
            protected void onSubError(String message) {

            }

            @Override
            protected void onSubNext(SystemMessageDetailAllEnity AllData) {

            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }

    private void finishData() {
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }



}
