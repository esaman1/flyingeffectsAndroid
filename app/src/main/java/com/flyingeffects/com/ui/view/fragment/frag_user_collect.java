package com.flyingeffects.com.ui.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.MainRecyclerAdapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.ListForUpAndDown;
import com.flyingeffects.com.enity.NewFragmentTemplateItem;
import com.flyingeffects.com.enity.templateDataZanRefresh;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.view.activity.PreviewUpAndDownActivity;
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


/***
 * 我的收藏
 */

public class frag_user_collect extends BaseFragment {
    private boolean isRefresh = true;

    private BaseQuickAdapter adapter;

    private List<NewFragmentTemplateItem> allData = new ArrayList<>();
    @BindView(R.id.smart_refresh_layout_collect)
    SmartRefreshLayout smartRefreshLayout;
    private int perPageCount = 10;

    @BindView(R.id.recyclerView_collect)
    RecyclerView recyclerView;

    /**
     * 1 是模板，2是背景 3是我上传的背景
     */
    private String template_type;

    @BindView(R.id.tv_hint_collect)
    TextView tv_hint;

    ArrayList<NewFragmentTemplateItem> listData = new ArrayList<>();

    private int selectPage = 1;

    private StaggeredGridLayoutManager layoutManager;
    //3为模板页面选择背景
    private int from;

    @Override
    protected int getContentLayout() {
        return R.layout.frg_user_collect;
    }


    @Override
    protected void initView() {
        initSmartRefreshLayout();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            template_type = bundle.getString("template_type");
            from = bundle.getInt("from", 0);
        }
    }


    @Override
    protected void initAction() {
        initRecycler();
    }


    /**
     * description ：请求收藏列表
     * creation date: 2020/5/12
     * user : zhangtongju
     */
    private void requestCollectionList(boolean isShowDialog) {
        tv_hint.setVisibility(View.GONE);
        HashMap<String, String> params = new HashMap<>();
        params.put("token", BaseConstans.GetUserToken());
        params.put("page", selectPage + "");
        params.put("template_type", template_type + "");
        params.put("pageSize", perPageCount + "");

        String str = StringUtil.beanToJSONString(params);
        LogUtil.d("OOM", "请求的参数为------" + str);

        Observable ob = Api.getDefault().collectionList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<NewFragmentTemplateItem>>(getActivity()) {
            @Override
            protected void onSubError(String message) {
                finishData();
            }

            @Override
            protected void onSubNext(List<NewFragmentTemplateItem> data) {
                finishData();
                if (isRefresh) {
                    listData.clear();
                }
                if (data.size() == 0 && isRefresh) {
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


    private void showData(ArrayList<NewFragmentTemplateItem> listData) {
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
            if (BaseConstans.hasLogin()) {
                isRefresh = true;
                refreshLayout.setEnableLoadMore(true);
                selectPage = 1;
                requestCollectionList(false);
            } else {
                finishData();
                ToastUtil.showToast("请先登录");
            }
        });
        smartRefreshLayout.setOnLoadMoreListener(refresh -> {


            if (BaseConstans.hasLogin()) {
                isRefresh = false;
                selectPage++;
                requestCollectionList(false);
            } else {
                finishData();
                ToastUtil.showToast("请先登录");
            }


        });
    }


    @Override
    protected void initData() {

    }

    @Override
    public void onResume() {
        if (BaseConstans.hasLogin()) {
            isRefresh = true;
            selectPage = 1;
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
        int fromType;
        //0 模板  1 背景 2 搜索/我的收藏 3 表示背景模板下载
        if (!TextUtils.isEmpty(template_type) && "2".equals(template_type)) {
            fromType = 1;
        } else {
            fromType = 2;
        }
        adapter = new MainRecyclerAdapter(allData, fromType,false,null);
        layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (!DoubleClick.getInstance().isFastDoubleClick()) {
//                Intent intent = new Intent(getActivity(), PreviewActivity.class);
//                intent.putExtra("person", allData.get(position));//直接存入被序列化的对象实例
//
//                if(from==3){
//                    intent.putExtra("fromTo", FromToTemplate.ISFROMEDOWNVIDEO);
//                }else{
//                    if (template_type != null && template_type.equals("1")) {
//                        intent.putExtra("fromTo", FromToTemplate.ISFROMTEMPLATE);
//                    } else if(template_type != null && template_type.equals("2")) {
//                        intent.putExtra("fromTo", FromToTemplate.ISFROMBJ);
//                    }else{
//                        intent.putExtra("fromTo", FromToTemplate.ISFROMUPDATEBJ);
//                    }
//                }
//                intent.putExtra("fromToMineCollect", true);
//                intent.putExtra("person", allData.get(position));//直接存入被序列化的对象实例
//                startActivity(intent);


                Intent intent = new Intent(getActivity(), PreviewUpAndDownActivity.class);
                ListForUpAndDown listForUpAndDown = new ListForUpAndDown(allData);
                intent.putExtra("person", listForUpAndDown);//直接存入被序列化的对象实例
                intent.putExtra("position", position);
                if (from == 3) {
                    intent.putExtra("fromTo", FromToTemplate.ISCHOOSEBJ);
                } else {
                    if (template_type != null && "1".equals(template_type)) {
//                        intent.putExtra("fromTo", FromToTemplate.ISTEMPLATE);
                        intent.putExtra("fromTo", FromToTemplate.ISHOMEMYTEMPLATECOLLECT);
                    } else if (template_type != null && "2".equals(template_type)) {
                        intent.putExtra("fromTo", FromToTemplate.ISBJCOLLECT);
                    } else {
                        intent.putExtra("fromTo", FromToTemplate.ISHOMEFROMBJ);
                    }
                }
                intent.putExtra("fromToMineCollect", true);
                intent.putExtra("nowSelectPage", selectPage);
                intent.putExtra("category_id", "");
                startActivity(intent);

            }
        });
    }

    /**
     * description ：这里的数据是用来刷新点赞功能的
     * creation date: 2020/8/11
     * user : zhangtongju
     */
    @Subscribe
    public void onEventMainThread(templateDataZanRefresh event) {
        if(event.getTemplateId()!=0){
            if(allData != null && allData.size() > 0){
                int changeId=event.getTemplateId();
                boolean isPraise = event.isSeleted();
                for (int i=0;i<allData.size();i++){

                    int needId = allData.get(i).getTemplate_id();
                    if (needId == 0) {
                        needId = allData.get(i).getId();
                    }
                    if(needId==changeId){
                        NewFragmentTemplateItem item = allData.get(i);
                        item.setPraise(event.getZanCount() + "");
                        if (isPraise) {
                            item.setIs_praise(1);
                        } else {
                            item.setIs_praise(0);
                        }
                        allData.set(i, item);
                        adapter.notifyItemChanged(i);
                        return;
                    }
                }
            }

        }
    }
}


