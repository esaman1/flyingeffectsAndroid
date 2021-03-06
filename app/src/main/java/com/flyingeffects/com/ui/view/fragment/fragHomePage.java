package com.flyingeffects.com.ui.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.frag_home_page_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.entity.ListForUpAndDown;
import com.flyingeffects.com.entity.NewFragmentTemplateItem;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.view.activity.PreviewUpAndDownActivity;
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
 * description ：用户的喜欢和收藏，
 * creation date: 2020/4/20
 * param :
 * user : zhangtongju
 */
public class fragHomePage extends BaseFragment {

    private int perPageCount = 10;
    @BindView(R.id.RecyclerView)
    RecyclerView recyclerView;
    private frag_home_page_adapter adapter;
    private List<NewFragmentTemplateItem> allData = new ArrayList<>();
    private String toUserId = "";
    //类型:1=作者的作品,2=作者喜欢的作品,3=作者收藏的模板
    private int isFrom;
    private String fromTo;
    @BindView(R.id.smart_refresh_layout_bj)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.lin_show_nodata_bj)
    LinearLayout lin_show_nodata;
    private boolean isRefresh = true;
    private ArrayList<NewFragmentTemplateItem> listData = new ArrayList<>();
    private int selectPage = 1;


    @Override
    protected int getContentLayout() {
        return R.layout.fag_home_page_item;
    }

    @Override
    protected void initView() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            toUserId = bundle.getString("toUserId");
            isFrom = bundle.getInt("isFrom");
            fromTo = bundle.getString("fromTo");
        }
        initRecycler();
        initSmartRefreshLayout();
    }

    @Override
    protected void initAction() {
//        requestFagData(true, true);
    }

    @Override
    protected void initData() {

    }


    private void initRecycler() {
        adapter = new frag_home_page_adapter(R.layout.list_home_page_item, allData, getActivity());
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(getActivity(), PreviewUpAndDownActivity.class);
            ListForUpAndDown listForUpAndDown = new ListForUpAndDown(allData);
            intent.putExtra("person", listForUpAndDown);//直接存入被序列化的对象实例
            intent.putExtra("position", position);
            intent.putExtra("fromToMineCollect", false);
            intent.putExtra("nowSelectPage", selectPage);
            intent.putExtra("templateId", allData.get(position).getTemplate_id());
            intent.putExtra("toUserID", toUserId);
            if("3".equals(allData.get(position).getTemplate_type())){
                intent.putExtra("fromTo", FromToTemplate.DRESSUP);
            }else{
                if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISHOMEMYLIKE)) {
                    intent.putExtra("fromTo", FromToTemplate.ISHOMEMYLIKE);
                } else {
                    if (isFrom == 1) {
                        //我的作品
                        intent.putExtra("fromTo", FromToTemplate.ISMESSAGEMYPRODUCTION);
                        intent.putExtra("isTest", allData.get(position).getTest());

                    } else if (isFrom == 2) {
                        //我的喜欢
                        intent.putExtra("fromTo", FromToTemplate.ISMESSAGEMYLIKE);
                    }
                }
            }
            startActivity(intent);
        });
    }


    public void initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            if (BaseConstans.hasLogin()) {
                isOnRefresh();
                isRefresh = true;
                refreshLayout.setEnableLoadMore(true);
                selectPage = 1;
                requestFagData(false, true);
            } else {
                ToastUtil.showToast("请先登录");
                allData.clear();
                finishData();
            }
        });
        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
            if (BaseConstans.hasLogin()) {
                isOnLoadMore();
                isRefresh = false;
                selectPage++;
                requestFagData(false, false);
            } else {
                ToastUtil.showToast("请先登录");
                allData.clear();
                finishData();
            }
        });
    }


    //得到banner缓存数据
    public void requestData() {
        requestFagData(false, false); //首页杂数据
    }

    /**
     * description ：
     * creation date: 2020/3/11
     * param : template_type  1是模板 2是背景
     * user : zhangtongju
     */
    private void requestFagData(boolean isCanRefresh, boolean isSave) {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", selectPage + "");
        if(!TextUtils.isEmpty(fromTo)&&fromTo.equals(FromToTemplate.ISHOMEMYLIKE)){
            params.put("to_user_id", BaseConstans.getUserId());
        }else{
            params.put("to_user_id", toUserId);
        }
        params.put("type", isFrom + "");//	'类型:1=作者的作品,2=作者喜欢的作品,3=作者收藏的模板
        params.put("pageSize", perPageCount + "");
        LogUtil.d("OOM", "请求喜欢数据参数" + StringUtil.beanToJSONString(params));
        Observable ob = Api.getDefault().getMyProduction(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<NewFragmentTemplateItem>>(getActivity()) {
            @Override
            protected void onSubError(String message) {
                finishData();
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(List<NewFragmentTemplateItem> data) {
                finishData();
                if (isRefresh) {
                    listData.clear();
                    smartRefreshLayout.setEnableLoadMore(true);

                }
                if (isRefresh && data.size() == 0) {
                    showNoData(true);
                } else {
                    showNoData(false);
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
        }, "fagBjItem", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, isSave, true, isCanRefresh);
    }


    private void finishData() {
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }

    public void isOnLoadMore() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (BaseConstans.hasLogin()) {
            selectPage = 1;
            isRefresh = true;
            requestData();
        } else {
            allData.clear();
            adapter.notifyDataSetChanged();
            showNoData(true);
            ToastUtil.showToast(getResources().getString(R.string.need_login));
        }
    }

    public void isOnRefresh() {
    }

    public void showNoData(boolean isShowNoData) {
        if (isShowNoData) {
            lin_show_nodata.setVisibility(View.VISIBLE);
        } else {
            lin_show_nodata.setVisibility(View.GONE);
        }
    }

    public void isShowData(ArrayList<NewFragmentTemplateItem> listData) {
        if (getActivity() != null) {
            allData.clear();
            allData.addAll(listData);
            adapter.notifyDataSetChanged();
            if (isFirstData) {
                BackgroundExecutor.execute(() -> {
                    isFirstData = false;
                });
            }
        }
    }

    private boolean isFirstData = true;

}
