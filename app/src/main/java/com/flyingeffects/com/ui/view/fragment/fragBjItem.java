package com.flyingeffects.com.ui.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.MainRecyclerAdapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.AttentionChange;
import com.flyingeffects.com.enity.BackgroundTemplateCollectionEvent;
import com.flyingeffects.com.enity.DownVideoPath;
import com.flyingeffects.com.enity.ListForUpAndDown;
import com.flyingeffects.com.enity.NewFragmentTemplateItem;
import com.flyingeffects.com.enity.templateDataCollectRefresh;
import com.flyingeffects.com.enity.templateDataZanRefresh;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.view.activity.PreviewUpAndDownActivity;
import com.flyingeffects.com.ui.view.activity.webViewActivity;
import com.flyingeffects.com.utils.BackgroundExecutor;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.nineton.market.android.sdk.AppMarketHelper;
import com.nineton.ntadsdk.bean.FeedAdConfigBean;
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

import static com.nineton.ntadsdk.bean.FeedAdConfigBean.FeedAdResultBean.TYPE_GDT_FEED_EXPRESS_AD;


/**
 * description ：背景页面，背景栏目下面模板列表，
 * creation date: 2020/4/20
 * param :
 * user : zhangtongju
 */
public class fragBjItem extends BaseFragment {


    private int perPageCount = 9;
    @BindView(R.id.RecyclerView)
    RecyclerView recyclerView;
    private MainRecyclerAdapter adapter;
    private List<NewFragmentTemplateItem> allData = new ArrayList<>();
    private String templateId = "";
    private int nowPageNum;
    @BindView(R.id.smart_refresh_layout_bj)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.lin_show_nodata_bj)
    LinearLayout lin_show_nodata;
    private boolean isRefresh = true;
    private ArrayList<NewFragmentTemplateItem> listData = new ArrayList<>();
    private int selectPage = 1;
    private boolean HasShowAd;

    /**
     * 0 表示来做模板，1表示来自背景 3表示创作下载页面
     */
    private int fromType;

    /**
     * 封面图，来自一键模板切换背景的时候，这个封面图代表默认的背景选择
     */
    private String cover;

    private FeedAdManager mAdManager;

    private int intoTiktokClickPosition;
    String tc_id = "";


    @Override
    protected int getContentLayout() {
        return R.layout.fag_bj_item;
    }

    @Override
    protected void initView() {
        mAdManager = new FeedAdManager();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            templateId = bundle.getString("id");
            fromType = bundle.getInt("from");
            cover = bundle.getString("cover");
            nowPageNum=bundle.getInt("num");
            tc_id = bundle.getString("tc_id");
        }
        initRecycler();
        initSmartRefreshLayout();
        LogUtil.d("OOM", "fromType=" + fromType);
        LogUtil.d("OOM", "templateId=" + templateId);
        requestFagData(true, true);
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void initData() {
        ChoosePageChange2(() -> {
            if (getActivity() != null) {
                if (listData!=null&&listData.size()>0) {
                    LogUtil.d("requestAd", "onResume之背景请求广告");
                    requestFeedAd();
                }
            }
        });

    }


    private void initRecycler() {
        adapter = new MainRecyclerAdapter(allData, fromType, false, mAdManager);
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.
                        VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (!DoubleClick.getInstance().isFastDoubleClick()&&!allData.get(position).isHasShowAd()) {
                if (!TextUtils.isEmpty(cover) && position == 0) {
                    EventBus.getDefault().post(new DownVideoPath(""));
                } else {
                    if (allData.get(position).getIs_ad_recommend() == 1) {
                        String url = allData.get(position).getRemark();
                        boolean result = AppMarketHelper.of(getActivity()).skipMarket(url);
                        if (!result) {
                            Intent intent = new Intent(getActivity(), webViewActivity.class);
                            intent.putExtra("webUrl", url);
                            startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(getActivity(), PreviewUpAndDownActivity.class);
                        List<NewFragmentTemplateItem> data = getFiltration(allData, position);
                        ListForUpAndDown listForUpAndDown = new ListForUpAndDown(data);
                        intent.putExtra("person", listForUpAndDown);//直接存入被序列化的对象实例
                        intent.putExtra("position", intoTiktokClickPosition);
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
            }
        });
    }


    public List<NewFragmentTemplateItem> getFiltration(List<NewFragmentTemplateItem> allData, int position) {
        intoTiktokClickPosition = position;
        List<NewFragmentTemplateItem> needData = new ArrayList<>();
        for (int i = 0; i < allData.size(); i++) {
            NewFragmentTemplateItem item = allData.get(i);
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
    public void onResume() {
        super.onResume();
        LogUtil.d("OOM","onResume");
        mAdManager.adResume();
        if (getActivity() != null && "12".equals(templateId)) {
            isRefresh = true;
            selectPage = 1;
            requestFagData(false, false);
        }



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
        List<NewFragmentTemplateItem> data = Hawk.get("fagBjItem", new ArrayList<>());
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
        if (!TextUtils.isEmpty(tc_id) && Integer.parseInt(tc_id) >= 0) {
            params.put("tc_id", tc_id);
        }
        params.put("template_type", "2");
        params.put("page", selectPage + "");
        params.put("pageSize", perPageCount + "");

        Observable ob = Api.getDefault().getTemplate(BaseConstans.getRequestHead(params));

        LogUtil.d("OOM2", "requestFagData背景模板请求的数据为" + StringUtil.beanToJSONString(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<NewFragmentTemplateItem>>(getActivity()) {
            @Override
            protected void onSubError(String message) {
                finishData();
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(List<NewFragmentTemplateItem> data) {
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("OOM", "str=" + str);
                finishData();
                if (isRefresh) {
                    listData.clear();
                    if (!TextUtils.isEmpty(cover)) {
                        if (!("11".equals(templateId) || "12".equals(templateId))) {
                            //关注和收藏
                            NewFragmentTemplateItem item = new NewFragmentTemplateItem();
                            item.setImage(cover);
                            item.setTitle("默认背景");
                            listData.add(item);
                        }
                    }
                }
                if (isRefresh && data.size() == 0) {
                    showNoData(true);
                } else {
                    showNoData(false);
                }
                if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser() && data.size() > BaseConstans.NOWADSHOWPOSITION) {
                    NewFragmentTemplateItem item = new NewFragmentTemplateItem();
                    item.setHasShowAd(true);
                    //设置当前是导流，进入抖音列表页就会自动过滤
                    item.setIs_ad_recommend(1);
                    data.add(BaseConstans.NOWADSHOWPOSITION, item);
                }


                if (!isRefresh && data.size() < perPageCount) {  //因为可能默认只请求8条数据
                    ToastUtil.showToast(getResources().getString(R.string.no_more_data));
                }


                if (data.size() < perPageCount) {
                    smartRefreshLayout.setEnableLoadMore(false);
                }
                listData.addAll(data);
                isShowData(listData);
                requestFeedAd();

            }
        }, "fagBjItem", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, isSave, true, isCanRefresh);
    }





    private void requestFeedAd(){
       LogUtil.d("page2Change", "背景请求广告NowHomePageChooseNum=" + NowHomePageChooseNum+"NowSecondChooseNum="+NowSecondChooseNum+"actTag"+nowPageNum);
        if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()&&  NowHomePageChooseNum==0 &&nowPageNum==NowSecondChooseNum) {
//            LogUtil.d("page2Change", "背景请求广告NowHomePageChooseNum=" + NowHomePageChooseNum+"NowSecondChooseNum="+NowSecondChooseNum+"actTag"+nowPageNum);
            HasShowAd=true;
            requestFeedAd(mAdManager, new RequestFeedBack() {
                @Override
                public void GetAdCallback(FeedAdConfigBean.FeedAdResultBean bean) {
                    getAdCallback(bean);
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


    private void getAdCallback(FeedAdConfigBean.FeedAdResultBean feedAdResultBean) {
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


    @Subscribe
    public void onEventMainThread(templateDataCollectRefresh event) {
        if (event.getFrom() == 4) {
            int position = event.getPosition();
            if (allData != null && allData.size() > position) {
                NewFragmentTemplateItem item = allData.get(position);
                item.setIs_collection(event.isSeleted() ? 1 : 0);
                allData.set(position, item);
                adapter.notifyItemChanged(position);
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            mAdManager.adDestroy();
        }
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(AttentionChange event) {
        isRefresh = true;
        selectPage = 1;
        if (getActivity() != null && TextUtils.isEmpty(templateId) && "12".equals(templateId)) {
            requestFagData(false, false);
        }
    }


    @Subscribe
    public void onEventMainThread(BackgroundTemplateCollectionEvent event) {
        isRefresh = true;
        selectPage = 1;
        if (getActivity() != null && !TextUtils.isEmpty(templateId) && "11".equals(templateId)) {
            requestFagData(false, false);
        }
    }
}
