package com.flyingeffects.com.ui.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.music_recent_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.commonlyModel.DoubleClick;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.ChooseMusic;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.ui.view.activity.LocalMusicTailorActivity;
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
 * description ：最近更新
 * creation date: 2020/8/26
 * user : zhangtongju
 */
public class frag_choose_music_recent_updates extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.smart_refresh_layout_bj)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.lin_show_nodata_bj)
    LinearLayout lin_show_nodata_bj;

    private boolean isRefresh = true;
    private int selectPage = 1;
    private int perPageCount = 10;

    //0 表示最近更新 1 表示本地音频 2表示收藏音乐
    private int id;

    private music_recent_adapter adapter;

    private List<ChooseMusic> listData = new ArrayList<>();

    private long needDuration;


    @Override
    protected int getContentLayout() {
        return R.layout.fag_recent_updates;
    }


    @Override
    protected void initView() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            id = bundle.getInt("id", 0);
            needDuration=bundle.getLong("needDuration");
        }
        initSmartRefreshLayout();
        initRecycler();
    }

    @Override
    protected void initAction() {
        requestFagData();
    }

    @Override
    protected void initData() {

    }


    private void requestFagData() {
        HashMap<String, String> params = new HashMap<>();
//        params.put("keyword", "");
        params.put("page", selectPage + "");
        params.put("pageSize", perPageCount + "");
        Observable ob = null;
        if (id == 0) {
            ob = Api.getDefault().musicList(BaseConstans.getRequestHead(params));
        }
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<ChooseMusic>>(getActivity()) {
            @Override
            protected void _onError(String message) {
                finishData();
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(List<ChooseMusic> data) {
                finishData();
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("OOM2", str);
                if (isRefresh) {
                    listData.clear();
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
                adapter.notifyDataSetChanged();

            }
        }, "fagBjItem", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
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
            requestFagData();
        });
        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
            isRefresh = false;
            selectPage++;
            requestFagData();
        });
    }


    private void initRecycler() {
        adapter = new music_recent_adapter(R.layout.list_music_recent_item, listData, getActivity(), id);
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.
                        VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (!DoubleClick.getInstance().isFastDoubleClick()) {
                switch (view.getId()) {
                    case R.id.tv_make:
                        Intent intent = new Intent(getActivity(), LocalMusicTailorActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("videoPath",listData.get(position).getAudio_url());
                        intent.putExtra("needDuration",needDuration);
                        intent.putExtra("isAudio",true);
                        startActivity(intent);
                        break;

                    default:
                        break;
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }


    public void showNoData(boolean isShowNoData) {
        if (isShowNoData) {
            lin_show_nodata_bj.setVisibility(View.VISIBLE);
        } else {
            lin_show_nodata_bj.setVisibility(View.GONE);
        }
    }


}
