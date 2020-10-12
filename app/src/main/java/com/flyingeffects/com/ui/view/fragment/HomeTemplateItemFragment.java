package com.flyingeffects.com.ui.view.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.View;
import android.widget.LinearLayout;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.main_recycler_adapter;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.enity.ListForUpAndDown;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.enity.templateDataCollectRefresh;
import com.flyingeffects.com.enity.templateDataZanRefresh;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.view.HomeItemMvpView;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.presenter.home_fag_itemMvpPresenter;
import com.flyingeffects.com.ui.view.activity.PreviewUpAndDownActivity;
import com.flyingeffects.com.ui.view.activity.webViewActivity;
import com.flyingeffects.com.utils.BackgroundExecutor;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.NetworkUtils;
import com.nineton.market.android.sdk.AppMarketHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;


/**
 * description ：模板详情
 * creation date: 2020/8/18
 * user : zhangtongju
 */
public class HomeTemplateItemFragment extends BaseFragment implements HomeItemMvpView, View.OnClickListener {

    private home_fag_itemMvpPresenter Presenter;
    @BindView(R.id.RecyclerView)
    RecyclerView recyclerView;
    private main_recycler_adapter adapter;
    private List<new_fag_template_item> allData = new ArrayList<>();
    private String category_id = "";
    private StaggeredGridLayoutManager layoutManager;
    private int actTag;
    @BindView(R.id.smart_refresh_layout)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.lin_show_nodata)
    LinearLayout lin_show_nodata;
    /**
     * 0 表示来做模板，1表示来自背景 3表示来自背景下载
     */
    private int fromType;
    private int intoTiktokClickPosition;


    @Override
    protected int getContentLayout() {
        return R.layout.fag_0_item;
    }


    @Override
    protected void initView() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            category_id = bundle.getString("id");
            actTag = bundle.getInt("num");
            fromType = bundle.getInt("from");
        }
        EventBus.getDefault().register(this);
        LogUtil.d("OOM", "2222fromType=" + fromType);
        Presenter = new home_fag_itemMvpPresenter(getActivity(), this, fromType);
        initRecycler();
        Presenter.initSmartRefreshLayout(smartRefreshLayout);

        if (getActivity() != null) {
            if (NetworkUtils.isNetworkAvailable(getActivity())) {
                Presenter.requestData(category_id, actTag);
            }
        }
    }


    private void initRecycler() {
        adapter = new main_recycler_adapter(R.layout.list_main_item, allData, getActivity(), fromType);
        layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (!DoubleClick.getInstance().isFastDoubleClick()) {

                if(allData.get(position).getIs_ad_recommend()==1){
                    String url = allData.get(position).getRemark();
//                    String url = "http://transaction.chucitech.cn//#/index/?appid=76&NTExchange=true";
                    boolean result =   AppMarketHelper.of(getActivity()).skipMarket(url);
                    if(!result){
                        Intent intent = new Intent(getActivity(), webViewActivity.class);
                        intent.putExtra("webUrl", url);
                        startActivity(intent);
                    }
                }else{
                    statisticsEventAffair.getInstance().setFlag(getActivity(), "1_mb_click", allData.get(position).getTitle());
                    Intent intent = new Intent(getActivity(), PreviewUpAndDownActivity.class);
                    List<new_fag_template_item> data=  getFiltration(allData,position);
                    ListForUpAndDown listForUpAndDown = new ListForUpAndDown(data);
                    intent.putExtra("person", listForUpAndDown);//直接存入被序列化的对象实例
                    intent.putExtra("category_id", category_id);//直接存入被序列化的对象实例
                    intent.putExtra("position", intoTiktokClickPosition);
                    int selectPage = Presenter.getselectPage();
                    intent.putExtra("nowSelectPage", selectPage);
                    intent.putExtra("fromTo", FromToTemplate.ISTEMPLATE);
                    startActivity(intent);
                }
            }
        });
    }


    public List<new_fag_template_item> getFiltration(List<new_fag_template_item> allData,int position) {
        intoTiktokClickPosition=position;
        List<new_fag_template_item> needData = new ArrayList<>();
        for (int i = 0; i < allData.size(); i++) {
            new_fag_template_item item = allData.get(i);
            if (item.getIs_ad_recommend() == 0) {
                needData.add(item);
            }else{
                if(i<position){
                    intoTiktokClickPosition--;
                }
            }
        }
        return  needData;
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
            if (allData == null || allData.size() == 0) {
                LogUtil.d("OOM", "allData==null");
                Presenter.requestData(category_id, actTag);
            } else {
                LogUtil.d("OOM", "allData!=null");
            }
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
                    statisticsEventAffair.getInstance().setFlag(getActivity(), "1_mb_screen", allData.get(i).getTitle());
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
        if(event.getFrom()==3){
            int position = event.getPosition();
            boolean isPraise = event.isSeleted();
            if (allData != null && allData.size() > position) {
                new_fag_template_item item = allData.get(position);
                item.setIs_collection(event.isSeleted()?1:0);
                allData.set(position, item);
                adapter.notifyItemChanged(position);
            }
        }
    }


}


