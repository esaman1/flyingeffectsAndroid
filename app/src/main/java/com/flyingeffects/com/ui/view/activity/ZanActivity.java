package com.flyingeffects.com.ui.view.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.Mine_zan_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.ListForUpAndDown;
import com.flyingeffects.com.enity.MineZanEnity;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.utils.BackgroundExecutor;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Observable;

/**
 * description ：点赞页面和评论页面，公用的
 * creation date: 2020/7/29
 * user : zhangtongju
 */
public class ZanActivity extends BaseActivity {

    private int perPageCount = 10;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private Mine_zan_adapter adapter;

    private List<MineZanEnity> listData = new ArrayList<>();

    private boolean isRefresh = true;
    private int selectPage = 1;

    @BindView(R.id.smart_refresh_layout_bj)
    SmartRefreshLayout smartRefreshLayout;


    int from;
    private List<new_fag_template_item> allData = new ArrayList<>();


    @Override
    protected int getLayoutId() {
        return R.layout.act_like;
    }

    @Override
    protected void initView() {
        ((TextView) findViewById(R.id.tv_top_title)).setText("赞");
        findViewById(R.id.iv_top_back).setOnClickListener(this);
        initSmartRefreshLayout();
        from = getIntent().getIntExtra("from", 0);
        adapter = new Mine_zan_adapter(R.layout.list_like_item, listData, from, this);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!TextUtils.isEmpty(listData.get(position).getTemplate_id())) {
                    requestTemplateDetail(listData.get(position).getTemplate_id());
                } else {
                    requestTemplateDetail(listData.get(position).getId());
                }
            }
        });


        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_icon:
                    case R.id.tv_title:
                        Intent intent = new Intent(ZanActivity.this, UserHomepageActivity.class);
                        intent.putExtra("toUserId", listData.get(position).getUser_id());
                        intent.putExtra("templateType", listData.get(position).getType());
                        startActivity(intent);
                        break;
                }
            }
        });

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }


    public void requestTemplateDetail(String templateId) {
        if (!TextUtils.isEmpty(templateId)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("template_id", templateId);
            // 启动时间
            Observable ob = Api.getDefault().templateLInfo(BaseConstans.getRequestHead(params));
            HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<new_fag_template_item>(this) {
                @Override
                protected void _onError(String message) {
                    LogUtil.d("OOM", "requestTemplateDetail-error=" + message);
                }

                @Override
                protected void _onNext(new_fag_template_item data) {
                    allData.clear();
                    String str = StringUtil.beanToJSONString(data);
                    LogUtil.d("OOM", str);
                    Intent intent = new Intent(ZanActivity.this, PreviewUpAndDownActivity.class);
                    String type = data.getTemplate_type();
                    allData.add(data);
                    ListForUpAndDown listForUpAndDown = new ListForUpAndDown(allData);
                    intent.putExtra("person", listForUpAndDown);//直接存入被序列化的对象实例
                    intent.putExtra("position", 0);
                    intent.putExtra("fromToMineCollect", false);
                    intent.putExtra("nowSelectPage", 1);
                    intent.putExtra("templateId", data.getTemplate_id());
                    intent.putExtra("isCanLoadMore", false);
                    if (!TextUtils.isEmpty(type) && type.equals("2")) {
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
        if (from == 1 && !BaseConstans.hasLogin()) {
            goActivity(LoginActivity.class);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (BaseConstans.hasLogin()) {
            requestPraiseList(true);
        } else {
            ToastUtil.showToast(getResources().getString(R.string.need_login));
        }

    }


    /**
     * description ：请求我的赞
     * creation date: 2020/8/6
     * user : zhangtongju
     */
    private void requestPraiseList(boolean isShowDialog) {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", selectPage + "");
        params.put("pageSize", perPageCount + "");
        Observable ob = Api.getDefault().praiseList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<MineZanEnity>>(ZanActivity.this) {
            @Override
            protected void _onError(String message) {
                finishData();
                Log.e("OOM", "_onError: " + message);
            }

            @Override
            protected void _onNext(List<MineZanEnity> data) {
                LogUtil.d("OOM", StringUtil.beanToJSONString(data));

                finishData();
                if (isRefresh) {
                    listData.clear();
                }
                if (!isRefresh && data.size() < perPageCount) {  //因为可能默认只请求8条数据
                    ToastUtil.showToast(getResources().getString(R.string.no_more_data));
                }
                if (data.size() < perPageCount) {
                    smartRefreshLayout.setEnableLoadMore(false);
                }
                listData.addAll(data);
                isShowData(listData);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, isShowDialog);
    }


    public void initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            isRefresh = true;
            refreshLayout.setEnableLoadMore(true);
            selectPage = 1;
            requestPraiseList(false);
        });
        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
            isRefresh = false;
            selectPage++;
            requestPraiseList(false);
        });
    }

    private boolean isFirstData = true;

    public void isShowData(List<MineZanEnity> data) {
        adapter.notifyDataSetChanged();
        if (isFirstData) {
            BackgroundExecutor.execute(() -> {
                isFirstData = false;
            });
        }
    }

    private void finishData() {
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }


}
