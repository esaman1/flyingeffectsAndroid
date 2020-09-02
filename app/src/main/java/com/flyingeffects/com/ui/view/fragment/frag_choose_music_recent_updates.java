package com.flyingeffects.com.ui.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.music_recent_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.commonlyModel.DoubleClick;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.BlogFile.Music;
import com.flyingeffects.com.enity.ChooseMusic;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.ui.view.activity.LocalMusicTailorActivity;
import com.flyingeffects.com.utils.BlogFileResource.FileManager;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


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

    private int nowClickPosition;


    @Override
    protected int getContentLayout() {
        return R.layout.fag_recent_updates;
    }


    @Override
    protected void initView() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            id = bundle.getInt("id", 0);
            LogUtil.d("oom2", "id=" + id + "bundle != null");
            needDuration = bundle.getLong("needDuration");
        }
        initSmartRefreshLayout();
        initRecycler();
    }

    @Override
    protected void initAction() {
        if (id == 1) {
            LogUtil.d("OOM2", "当前选择的是本地音频");
            //本地音频
            Observable.create((Observable.OnSubscribe<List<ChooseMusic>>) subscriber -> {
                FileManager mInstance = FileManager.getInstance();
                for (Music music : mInstance.getMusics()
                ) {
                    ChooseMusic chooseMusic = new ChooseMusic();
                    chooseMusic.setAudio_url(music.getPath());
                    chooseMusic.setImage(music.getAlbum());
                    chooseMusic.setNickname(music.getName());
                    listData.add(chooseMusic);
                }
                subscriber.onNext(listData);
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<ChooseMusic>>() {
                @Override
                public void call(List<ChooseMusic> chooseMusics) {
                    adapter.notifyDataSetChanged();
                }
            });

        } else {
            LogUtil.d("OOM2", "当前选择的是请求");
            requestFagData();

        }

    }

    @Override
    protected void initData() {

    }


    private void requestFagData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", selectPage + "");
        params.put("pageSize", perPageCount + "");
        Observable ob = null;
        if (id == 0) {


            ob = Api.getDefault().musicList(BaseConstans.getRequestHead(params));
        } else {
            //2 我的收藏
            ob = Api.getDefault().musicCollectionList(BaseConstans.getRequestHead(params));
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (!DoubleClick.getInstance().isFastDoubleClick()) {
                nowClickPosition = position;
                switch (view.getId()) {
                    case R.id.tv_make:
                        Intent intent = new Intent(getActivity(), LocalMusicTailorActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("videoPath", listData.get(position).getAudio_url());
                        intent.putExtra("needDuration", needDuration);
                        intent.putExtra("isAudio", true);
                        startActivity(intent);
                        break;


                    case R.id.iv_collect:
                        //收藏
                        clickCollect(listData.get(position).getId(), listData.get(position).getIs_collection());

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


    public void clickCollect(String music_id, int isCollect) {
        HashMap<String, String> params = new HashMap<>();
        params.put("music_id", music_id);
        // 启动时间
        Observable ob = Api.getDefault().collectMusic(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(getActivity()) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(Object data) {
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("OOM", "收藏音乐返回的值为" + str);
                updateCollect(isCollect);

            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    private void updateCollect(int oldIsCollect) {
        if (id == 2) {
            //移除收藏item
            listData.remove(nowClickPosition);
            adapter.notifyDataSetChanged();
        } else {
            if (oldIsCollect == 0) {
                oldIsCollect = 1;
            } else {
                oldIsCollect = 0;
            }
            ChooseMusic chooseMusic = listData.get(nowClickPosition);
            chooseMusic.setIs_collection(oldIsCollect);
            listData.set(nowClickPosition, chooseMusic);
            adapter.notifyItemChanged(nowClickPosition);
        }
    }

}
