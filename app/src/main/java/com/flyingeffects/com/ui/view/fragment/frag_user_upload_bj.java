package com.flyingeffects.com.ui.view.fragment;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.Upload_bj_list_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.view.activity.PreviewActivity;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Observable;


/***
 * 我的收藏
 */

public class frag_user_upload_bj extends BaseFragment {
    private boolean isRefresh = true;

    private BaseQuickAdapter adapter;

    private List<new_fag_template_item> allData = new ArrayList<>();
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

    ArrayList<new_fag_template_item> listData = new ArrayList<>();

    private int selectPage = 1;

    private StaggeredGridLayoutManager layoutManager;

    @Override
    protected int getContentLayout() {
        return R.layout.frg_user_upload;
    }


    @Override
    protected void initView() {
        initSmartRefreshLayout();
    }


    @Override
    protected void initAction() {
        initRecycler();
    }


    private void requestUploadBjList(boolean isShowDialog) {
        tv_hint.setVisibility(View.GONE);
        HashMap<String, String> params = new HashMap<>();
        params.put("token", BaseConstans.GetUserToken());
        params.put("page", selectPage + "");
        params.put("pageSize", perPageCount + "");
        Observable ob = Api.getDefault().uploadList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<Object>>(getActivity()) {
            @Override
            protected void _onError(String message) {
                finishData();
            }

            @Override
            protected void _onNext(List<Object> data) {
                LogUtil.d("OOM",StringUtil.beanToJSONString(data));
//                finishData();
//                if (isRefresh) {
//                    listData.clear();
//                }
//                if (data.size() == 0&&isRefresh) {
//                    tv_hint.setVisibility(View.VISIBLE);
//                    tv_hint.setText("暂无收藏模板");
//                }
//                if (!isRefresh && data.size() < perPageCount) {  //因为可能默认只请求8条数据
//                    ToastUtil.showToast(getResources().getString(R.string.no_more_data));
//                }
//                if (data.size() < perPageCount) {
//                    smartRefreshLayout.setEnableLoadMore(false);
//                }
//                listData.addAll(data);
//                showData(listData);

                new_fag_template_item item=new new_fag_template_item();
                item.setTitle("test");
                listData.add(item);
                showData(listData);

            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, isShowDialog);
    }





    private void showData(ArrayList<new_fag_template_item> listData) {
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
            isRefresh = true;
            refreshLayout.setEnableLoadMore(true);
            selectPage = 1;
            requestUploadBjList(false);
        });
        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
            isRefresh = false;
            selectPage++;
            requestUploadBjList(false);
        });
    }


    @Override
    protected void initData() {

    }

    @Override
    public void onResume() {
        if(!TextUtils.isEmpty(template_type)&&template_type.equals("3")) {

            if (BaseConstans.hasLogin()) {
                isRefresh = true;
                selectPage = 1;
                smartRefreshLayout.setEnableLoadMore(true);
                requestUploadBjList(false);
            }

        }

        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    private void initRecycler() {
        adapter = new Upload_bj_list_adapter(R.layout.list_upload_bj_item, allData, getActivity());
        layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (!DoubleClick.getInstance().isFastDoubleClick()) {
                Intent intent = new Intent(getActivity(), PreviewActivity.class);
                intent.putExtra("person", allData.get(position));//直接存入被序列化的对象实例
                if(template_type!=null&&template_type.equals("1")){
                    intent.putExtra("fromTo", FromToTemplate.ISFROMTEMPLATE);
                }else{
                    intent.putExtra("fromTo", FromToTemplate.ISFROMBJ);
                }
                intent.putExtra("fromToMineCollect", true);
                intent.putExtra("person", allData.get(position));//直接存入被序列化的对象实例
                startActivity(intent);
            }
        });
    }


}


