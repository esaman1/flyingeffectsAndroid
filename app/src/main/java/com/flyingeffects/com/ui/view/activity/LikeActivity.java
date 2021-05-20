package com.flyingeffects.com.ui.view.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.Like_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.databinding.ActLikeBinding;
import com.flyingeffects.com.entity.ListForUpAndDown;
import com.flyingeffects.com.entity.MineCommentEnity;
import com.flyingeffects.com.entity.NewFragmentTemplateItem;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;

/**
 * description ：点赞页面和评论页面，公用的
 * creation date: 2020/7/29
 * user : zhangtongju
 */
public class LikeActivity extends BaseActivity {
    private static final String TAG = "LikeActivity";
    private int perPageCount = 10;

    private Like_adapter adapter;

    private List<MineCommentEnity> listData = new ArrayList<>();

    private boolean isRefresh = true;
    private int selectPage = 1;


    //1 来自消息评论页面
    int from;

    private List<NewFragmentTemplateItem> allData = new ArrayList<>();
    private ActLikeBinding mBinding;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {
        mBinding = ActLikeBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        setContentView(rootView);

        mBinding.tvTopTitle.setText("评论");
        mBinding.ivTopBack.setOnClickListener(this);
        from = getIntent().getIntExtra("from", 0);
        initSmartRefreshLayout();
        adapter = new Like_adapter(R.layout.list_like_item, listData, this);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                requestTemplateDetail(listData.get(position).getTemplate_id(), position);
            }
        });

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_icon:
                        Intent intent = new Intent(LikeActivity.this, UserHomepageActivity.class);
                        intent.putExtra("toUserId", listData.get(position).getUser_id());
                        intent.putExtra("templateType", listData.get(position).getType());

                        startActivity(intent);
                        break;

                    default:
                        break;
                }
            }
        });


        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setAdapter(adapter);
    }


    public void requestTemplateDetail(String templateId, int position) {
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
                    Intent intent = new Intent(LikeActivity.this, PreviewUpAndDownActivity.class);
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
        if (from == 1 && !BaseConstans.hasLogin()) {
            goActivity(LoginActivity.class);
        }
        if (BaseConstans.hasLogin()) {
            requestCommentList(true);
        } else {
            ToastUtil.showToast(getResources().getString(R.string.need_login));
        }
    }


    @Override
    protected void onResume() {
        super.onResume();


    }


    /**
     * description ：请求我的评论列表
     * creation date: 2020/8/6
     * user : zhangtongju
     */
    private void requestCommentList(boolean isShowDialog) {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", selectPage + "");
        params.put("pageSize", perPageCount + "");
        Observable ob = Api.getDefault().commentList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<MineCommentEnity>>(LikeActivity.this) {
            @Override
            protected void onSubError(String message) {
                finishData();
                Log.e(TAG, "_onError: " + message);
            }

            @Override
            protected void onSubNext(List<MineCommentEnity> data) {
                LogUtil.d("OOM", StringUtil.beanToJSONString(data));

                finishData();
                if (isRefresh) {
                    listData.clear();
                }
                if (!isRefresh && data.size() < perPageCount) {  //因为可能默认只请求8条数据
                    ToastUtil.showToast(getResources().getString(R.string.no_more_data));
                }
                if (data.size() < perPageCount) {
                    mBinding.smartRefreshLayoutBj.setEnableLoadMore(false);
                }
                listData.addAll(data);
                adapter.notifyDataSetChanged();
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, isShowDialog);
    }


    public void initSmartRefreshLayout() {
        mBinding.smartRefreshLayoutBj.setOnRefreshListener(refreshLayout -> {
            isRefresh = true;
            refreshLayout.setEnableLoadMore(true);
            selectPage = 1;
            requestCommentList(false);
        });
        mBinding.smartRefreshLayoutBj.setOnLoadMoreListener(refresh -> {
            isRefresh = false;
            selectPage++;
            requestCommentList(false);
        });
    }

    private void finishData() {
        mBinding.smartRefreshLayoutBj.finishRefresh();
        mBinding.smartRefreshLayoutBj.finishLoadMore();
    }


}
