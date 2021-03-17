package com.flyingeffects.com.ui.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.View;
import android.widget.LinearLayout;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.MainRecyclerAdapter;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.enity.ListForUpAndDown;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.enity.templateDataCollectRefresh;
import com.flyingeffects.com.enity.templateDataZanRefresh;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.view.HomeItemMvpView;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.presenter.home_fag_itemMvpPresenter;
import com.flyingeffects.com.ui.view.activity.PreviewUpAndDownActivity;
import com.flyingeffects.com.ui.view.activity.webViewActivity;
import com.flyingeffects.com.utils.BackgroundExecutor;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.NetworkUtils;
import com.nineton.market.android.sdk.AppMarketHelper;
import com.nineton.ntadsdk.bean.FeedAdConfigBean;
import com.nineton.ntadsdk.manager.FeedAdManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.TYPE_GDT_FEED_EXPRESS_AD;


/**
 * description ：模板列表页，广告逻辑（先放置null 的广告占位符，一页请求一次广告，更新广告占位符）
 * creation date: 2020/8/18
 * user : zhangtongju
 */
public class HomeTemplateItemFragment extends BaseFragment implements HomeItemMvpView, View.OnClickListener {

    private home_fag_itemMvpPresenter Presenter;
    @BindView(R.id.RecyclerView)
    RecyclerView recyclerView;
    private MainRecyclerAdapter adapter;
    private List<new_fag_template_item> allData = new ArrayList<>();
    private String category_id = "", tc_id = "", tabName = "";
    private StaggeredGridLayoutManager layoutManager;
    private int actTag;
    @BindView(R.id.smart_refresh_layout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.lin_show_nodata)
    LinearLayout lin_show_nodata;
    /**
     * 0 表示来做模板，1表示来自背景 3表示来自背景下载  4表示换脸
     */
    private int fromType;
    private int intoTiktokClickPosition;
    private FeedAdManager mAdManager;

    @Override
    protected int getContentLayout() {
        return R.layout.fag_0_item;
    }


    @Override
    protected void initView() {
        mAdManager = new FeedAdManager();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            category_id = bundle.getString("id");
            tc_id = bundle.getString("tc_id");
            actTag = bundle.getInt("num");
            fromType = bundle.getInt("from");
            tabName = bundle.getString("tabName");
        }
        EventBus.getDefault().register(this);
        LogUtil.d("OOM", "2222fromType=" + fromType);
        Presenter = new home_fag_itemMvpPresenter(getActivity(), this, fromType,mAdManager);
        initRecycler();
        Presenter.initSmartRefreshLayout(smartRefreshLayout);

        if (getActivity() != null) {
            if (NetworkUtils.isNetworkAvailable(getActivity())) {
                Presenter.requestData(category_id, tc_id, actTag);
            }
        }
    }


    private void initRecycler() {
        adapter = new MainRecyclerAdapter(allData, fromType,false,mAdManager);
        adapter.setDressUPTabNameFavorites(tabName);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (!DoubleClick.getInstance().isFastDoubleClick()) {
                if (allData.get(position).getIs_ad_recommend() == 1) {
                    String url = allData.get(position).getRemark();
                    StatisticsEventAffair.getInstance().setFlag(getActivity(), "21_dl_click", allData.get(position).getTitle());
                    LogUtil.d("OOM", url);
                    boolean result = AppMarketHelper.of(getActivity()).skipMarket(url);
                    if (!result) {
                        Intent intent = new Intent(getActivity(), webViewActivity.class);
                        intent.putExtra("webUrl", url);
                        startActivity(intent);
                    }
                } else {
                    if (fromType == 4) {
                        StatisticsEventAffair.getInstance().setFlag(getActivity(), "21_face_click", allData.get(position).getTitle());
                    } else {
                        StatisticsEventAffair.getInstance().setFlag(getActivity(), "1_mb_click", allData.get(position).getTitle());
                    }
                    Intent intent = new Intent(getActivity(), PreviewUpAndDownActivity.class);
                    List<new_fag_template_item> data = getFiltration(allData, position);
                    ListForUpAndDown listForUpAndDown = new ListForUpAndDown(data);
                    intent.putExtra("person", listForUpAndDown);//直接存入被序列化的对象实例
                    intent.putExtra("category_id", category_id);//直接存入被序列化的对象实例
                    intent.putExtra("tc_id", tc_id);
                    intent.putExtra("position", intoTiktokClickPosition);
                    int selectPage = Presenter.getselectPage();
                    intent.putExtra("nowSelectPage", selectPage);
                    if (fromType == 4) {
                        intent.putExtra("fromTo", FromToTemplate.DRESSUP);
                    } else {
                        intent.putExtra("fromTo", FromToTemplate.ISTEMPLATE);
                    }
                    startActivity(intent);
                }
            }
        });
    }






    public List<new_fag_template_item> getFiltration(List<new_fag_template_item> allData, int position) {
        intoTiktokClickPosition = position;
        List<new_fag_template_item> needData = new ArrayList<>();
        for (int i = 0; i < allData.size(); i++) {
            new_fag_template_item item = allData.get(i);
            if (item.getIs_ad_recommend() == 0) {
                needData.add(item);
            } else {
                if (i < position) {
                    intoTiktokClickPosition--;
                }
            }
        }
        return needData;
    }


    @Override
    protected void initAction() {
    }


    @Override
    protected void initData() {

    }


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            mAdManager.adResume();
            if (allData == null || allData.size() == 0 || "11".equals(category_id) || "12".equals(category_id)) {
                LogUtil.d("OOM", "allData==null");
                Presenter.requestData(category_id, tc_id, actTag);
            }

//            else {
//                if(NowFragmentIsVisible&&!HasShowAd){
//                    LogUtil.d("requestAd","onResume之模板请求广告");
//                    needRequestFeedAd();
//                }
//            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void isOnRefresh() {
    }

    @Override
    public void isOnLoadMore() {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(getActivity()!=null){
            mAdManager.adDestroy();
        }
    }

    private boolean isFirstData = true;

    @Override
    public void isShowData(ArrayList<new_fag_template_item> listData) {
        if (getActivity() != null) {
            allData.clear();
            allData.addAll(listData);
            adapter.notifyDataSetChanged();
            if (isFirstData) {
                BackgroundExecutor.execute(() -> {
                    startStatistics();
                    isFirstData = false;
                });
            }
        }
    }

    private void startStatistics() {
        int[] mFirstVisibleItems = null;
        int[] mLastVisibleItems = null;
        mFirstVisibleItems = layoutManager.findFirstVisibleItemPositions(mFirstVisibleItems);
        mLastVisibleItems = layoutManager.findLastVisibleItemPositions(mLastVisibleItems);
        statisticsCount(mFirstVisibleItems, mLastVisibleItems);
    }

    private ArrayList<Integer> lastData = new ArrayList<>();
    private ArrayList<Integer> nowData = new ArrayList<>();

    private void statisticsCount(int[] data, int[] data2) {
        int end;
        int start = data[0];
        if (data2.length > 0) {
            end = data2[1];
        } else {
            end = data2[0];
        }
        if (start != -1 && end != -1) {
            nowData.clear();
            for (int i = start; i <= end; i++) {
                nowData.add(i);
                if (!hasIncludeNum(i)) {
                    if (fromType == 4) {
                        StatisticsEventAffair.getInstance().setFlag(getActivity(), "21_face", allData.get(i).getTitle());
                    } else {
                        StatisticsEventAffair.getInstance().setFlag(getActivity(), "1_mb_screen", allData.get(i).getTitle());
                    }

                }
            }
            lastData.clear();
            lastData.addAll(nowData);
        }
    }


    private boolean hasIncludeNum(int num) {
        for (int i = 0; i < lastData.size(); i++) {
            if (lastData.get(i) == num) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void showNoData(boolean isShowNoData) {
        if (isShowNoData) {
            lin_show_nodata.setVisibility(View.VISIBLE);
        } else {
            lin_show_nodata.setVisibility(View.GONE);
        }

    }


    /**
     * description ：请求到广告的回调
     * creation date: 2021/3/11
     * user : zhangtongju
     */
    public void FeedAdCallback(FeedAdConfigBean.FeedAdResultBean feedAdResultBean) {
        LogUtil.d("OOM2", "GetAdCallback");
        if (allData != null && allData.size() > 0) {
            int allSize = allData.size() - 1;
            LogUtil.d("OOM2", "allSize=" + allSize);
            for (int i = allSize; i > 0; i--) {
                boolean hasAd = allData.get(i).isHasShowAd();
                LogUtil.d("OOM2", "hasAd=" + hasAd);
                if (hasAd) {
                    if (allData.get(i).getFeedAdResultBean() == null) {
                        allData.get(i).setFeedAdResultBean(feedAdResultBean);
                        adapter.notifyItemChanged(i);
                        LogUtil.d("OOM2", "取消循环更新item" + i);
                        return;
                    }
                }
                LogUtil.d("OOM2", "还在循环" + i);
            }
        }
    }

    @Override
    public void needRequestFeedAd() {
        if(NowFragmentIsVisible&&getActivity()!=null){
            LogUtil.d("requestAd","onResume之模板1请求广告");
            requestFeedAd(mAdManager, new RequestFeedBack() {
                @Override
                public void GetAdCallback(FeedAdConfigBean.FeedAdResultBean bean) {
                    FeedAdCallback(bean);
                    HasShowAd=true;
                }
                @Override
                public void ChoseAdBack(int type, int adIndex) {
                    if (type != TYPE_GDT_FEED_EXPRESS_AD) {
                        adapter.remove(adIndex);
                    }
                }
            });
        }
    }


    @Override
    public void onClick(View view) {

    }


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
        if (event.getFrom() == 3) {
            int position = event.getPosition();
            boolean isPraise = event.isSeleted();
            if (allData != null && allData.size() > position) {
                new_fag_template_item item = allData.get(position);
                item.setIs_collection(event.isSeleted() ? 1 : 0);
                allData.set(position, item);
                adapter.notifyItemChanged(position);
            }
        }
    }


}


