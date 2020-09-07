package com.flyingeffects.com.ui.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.main_recycler_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.CommonNewsBean;
import com.flyingeffects.com.enity.DownVideoPath;
import com.flyingeffects.com.enity.ListForUpAndDown;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.enity.templateDataCollectRefresh;
import com.flyingeffects.com.enity.templateDataZanRefresh;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AdConfigs;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.view.activity.PreviewUpAndDownActivity;
import com.flyingeffects.com.utils.BackgroundExecutor;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.google.android.exoplayer2.C;
import com.nineton.ntadsdk.bean.FeedAdConfigBean;
import com.nineton.ntadsdk.itr.FeedAdCallBack;
import com.nineton.ntadsdk.manager.FeedAdManager;
import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;

import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.BAIDU_FEED_AD_EVENT;
import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.GDT_FEED_AD_EVENT;
import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.TT_FEED_AD_EVENT;


/**
 * description ：背景页面，背景栏目下面模板列表，
 * creation date: 2020/4/20
 * param :
 * user : zhangtongju
 */
public class fragBjItem extends BaseFragment {

    private int perPageCount = 10;
    @BindView(R.id.RecyclerView)
    RecyclerView recyclerView;
    private main_recycler_adapter adapter;
    private List<new_fag_template_item> allData = new ArrayList<>();
    private String templateId = "";
    @BindView(R.id.smart_refresh_layout_bj)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.lin_show_nodata_bj)
    LinearLayout lin_show_nodata;
    private boolean isRefresh = true;
    private ArrayList<new_fag_template_item> listData = new ArrayList<>();
    private int selectPage = 1;

    /**
     * 0 表示来做模板，1表示来自背景 3表示创作下载页面
     */
    private int fromType;

    /**
     * 封面图，来自一键模板切换背景的时候，这个封面图代表默认的背景选择
     */
    private String cover;

    private FeedAdManager mAdManager;


    @Override
    protected int getContentLayout() {
        return R.layout.fag_bj_item;
    }

    @Override
    protected void initView() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            templateId = bundle.getString("id");
            fromType = bundle.getInt("from");
            cover = bundle.getString("cover");
        }
        EventBus.getDefault().register(this);
        initRecycler();
        initSmartRefreshLayout();
        LogUtil.d("OOM", "fromType=" + fromType);
    }

    @Override
    protected void initAction() {
        mAdManager = new FeedAdManager();
        requestFagData(true, true);
    }

    @Override
    protected void initData() {

    }


    private void initRecycler() {
        adapter = new main_recycler_adapter(R.layout.list_main_item, allData, getActivity(), fromType);
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.
                        VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (!DoubleClick.getInstance().isFastDoubleClick()) {
                if (!TextUtils.isEmpty(cover) && position == 0) {
                    EventBus.getDefault().post(new DownVideoPath(""));
                } else {
//                    statisticsEventAffair.getInstance().setFlag(getActivity(), "1_mb_click", allData.get(position).getTitle());
////                    Intent intent =new Intent(getActivity(), PreviewActivity.class);
////                    if(fromType==0){
////                        intent.putExtra("fromTo", FromToTemplate.ISFROMTEMPLATE);
////                    }else if(fromType==3){
////                        intent.putExtra("fromTo", FromToTemplate.ISFROMEDOWNVIDEO);
////                    }else{
////                        intent.putExtra("fromTo", FromToTemplate.ISFROMBJ);
////                    }
////                    intent.putExtra("person",allData.get(position));//直接存入被序列化的对象实例
////                    startActivity(intent);

                    //test
                    Intent intent = new Intent(getActivity(), PreviewUpAndDownActivity.class);
                    ListForUpAndDown listForUpAndDown = new ListForUpAndDown(allData);
                    intent.putExtra("person", listForUpAndDown);//直接存入被序列化的对象实例
                    intent.putExtra("position", position);
                    if (fromType == 0) {
                        intent.putExtra("fromTo", FromToTemplate.ISTEMPLATE);
                    } else if (fromType == 3) {//来自选择背景tab
                        intent.putExtra("fromTo", FromToTemplate.ISCHOOSEBJ);
                    } else {
                        intent.putExtra("fromTo", FromToTemplate.ISBJ);
                    }
                    intent.putExtra("nowSelectPage", selectPage);
                    intent.putExtra("category_id", templateId);

                    startActivity(intent);
                }
            }
        });
    }


    public void initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            isOnRefresh();
            isRefresh = true;
            refreshLayout.setEnableLoadMore(true);
            selectPage = 1;
            requestFagData(false, true);
        });
        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
            isOnLoadMore();
            isRefresh = false;
            selectPage++;
            requestFagData(false, false);
        });
    }


    //得到banner缓存数据
    public void requestData() {
        List<new_fag_template_item> data = Hawk.get("fagBjItem", new ArrayList<>());
        if (data != null) {
            listData.clear();
            listData.addAll(data);
            isShowData(listData);
            requestFagData(false, true); //首页杂数据
        } else {
            requestFagData(true, true); //首页杂数据
        }
    }

    /**
     * description ：
     * creation date: 2020/3/11
     * param : template_type  1是模板 2是背景
     * user : zhangtongju
     */
    private void requestFagData(boolean isCanRefresh, boolean isSave) {
        HashMap<String, String> params = new HashMap<>();
        LogUtil.d("templateId", "templateId=" + templateId);
        params.put("category_id", templateId);
        params.put("template_type", "2");
        params.put("page", selectPage + "");
        params.put("pageSize", perPageCount + "");

        Observable ob = Api.getDefault().getTemplate(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<new_fag_template_item>>(getActivity()) {
            @Override
            protected void _onError(String message) {
                finishData();
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(List<new_fag_template_item> data) {
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("OOM", "str=" + str);
                LogUtil.d("OOM", "请求广告");
                finishData();
                if (isRefresh) {
                    listData.clear();
                    if (!TextUtils.isEmpty(cover)) {
                        new_fag_template_item item = new new_fag_template_item();
                        item.setImage(cover);
                        item.setTitle("默认背景");
                        listData.add(item);
                    }
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
                int allPosition = listData.size();
                if (data.size() >= 5) {
                    int needPosition = allPosition - 5;
                    new_fag_template_item item = new new_fag_template_item();
                    item.setHasShowAd(true);
                    listData.add(needPosition, item);
                    requestAd(needPosition);
                }

                isShowData(listData);
            }
        }, "fagBjItem", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, isSave, true, isCanRefresh);
    }


    ArrayList<CommonNewsBean> listCommentBean = new ArrayList<>();

    private void requestAd(int position) {
        LogUtil.d("OOM", "请求广告");
        if (getActivity() != null) {
            mAdManager.getFeedAd(getActivity(), AdConfigs.AD_FEED, new FeedAdCallBack() {
                @Override
                public void onFeedAdShow(int typeId, FeedAdConfigBean.FeedAdResultBean feedAdResultBean) {
                    CommonNewsBean bean = new CommonNewsBean();
                    bean.setTitle(feedAdResultBean.getTitle());
                    bean.setImageUrl(feedAdResultBean.getImageUrl());
                    bean.setEventType(feedAdResultBean.getEventType());
                    Log.d("OOM", "EventType=" + feedAdResultBean.getEventType());
                    bean.setChannel(feedAdResultBean.getChannel());
                    bean.setReadCounts(feedAdResultBean.getAdReadCount());
                    //根据类型设置对应的属性
                    switch (typeId) {
                        case BAIDU_FEED_AD_EVENT:
                            bean.setNativeResponse(feedAdResultBean.getNativeResponse());
                            break;
                        case GDT_FEED_AD_EVENT:
                            bean.setGdtAdData(feedAdResultBean.getGdtAdData());
                            break;
                        case TT_FEED_AD_EVENT:
                            bean.setTtFeedAd(feedAdResultBean.getTtFeedAd());
                            break;
                    }
                    listCommentBean.add(bean);
                    adapter.setAdList(listCommentBean);
                    adapter.notifyItemChanged(position);
                }

                @Override
                public void onFeedAdError(String error) {
                    LogUtil.d("OOM", "onFeedAdError=" + error);
                }

                @Override
                public void onFeedAdClose() {

                }

                @Override
                public boolean onFeedAdClicked(String title, String url, boolean isNtAd, boolean openURLInSystemBrowser) {
                    return false;
                }
            });
        }
    }


    private void finishData() {
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }

    public void isOnLoadMore() {

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

    public void isShowData(ArrayList<new_fag_template_item> listData) {
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

    /**
     * description ：这里的数据是用来刷新点赞功能的
     * creation date: 2020/8/11
     * user : zhangtongju
     */
    @Subscribe
    public void onEventMainThread(templateDataZanRefresh event) {
        if (event.getTemplateId() != 0) {
            if (allData != null && allData.size() > 0) {
                int changeId = event.getTemplateId();
                boolean isPraise = event.isSeleted();
                for (int i = 0; i < allData.size(); i++) {
                    int needId = allData.get(i).getTemplate_id();
                    if (needId == 0) {
                        needId = allData.get(i).getId();
                    }
                    LogUtil.d("OOM", "needID=" + needId);
                    if (needId == changeId) {
                        new_fag_template_item item = allData.get(i);
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


    @Subscribe
    public void onEventMainThread(templateDataCollectRefresh event) {
        if (event.getFrom() == 4) {
            int position = event.getPosition();
            if (allData != null && allData.size() > position) {
                new_fag_template_item item = allData.get(position);
                item.setIs_collection(event.isSeleted() ? 1 : 0);
                allData.set(position, item);
                adapter.notifyItemChanged(position);
            }
        }
    }

}
