package com.flyingeffects.com.ui.view.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyingeffects.com.BuildConfig;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.MainRecyclerAdapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.ListForUpAndDown;
import com.flyingeffects.com.enity.SendSearchText;
import com.flyingeffects.com.enity.NewFragmentTemplateItem;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.view.activity.LoginActivity;
import com.flyingeffects.com.ui.view.activity.PreviewUpAndDownActivity;
import com.flyingeffects.com.ui.view.activity.UploadMaterialActivity;
import com.flyingeffects.com.utils.BackgroundExecutor;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.PermissionUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;


/**
 * description ：背景页面，背景栏目下面模板列表，
 * creation date: 2020/4/20
 * param :
 * user : zhangtongju
 */

public class fragBjSearch extends BaseFragment {

    @BindView(R.id.RecyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.smart_refresh_layout_bj)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.lin_show_nodata_bj)
    LinearLayout lin_show_nodata;
    @BindView(R.id.relative_add)
    RelativeLayout relative_add;
    @BindView(R.id.tv_hint_search)
    TextView mTVHintSearch;
    @BindView(R.id.tv_add_title)
    TextView mAddTitle;

    private int perPageCount = 10;
    private MainRecyclerAdapter adapter;
    private List<NewFragmentTemplateItem> allData = new ArrayList<>();

    private boolean isRefresh = true;
    private int selectPage = 1;
    /**
     * 默认值肯定为""
     */
    private String searchText;
    /**
     * 0 表示搜索出来模板 1表示搜索内容为背景  3代表换装（闪图）
     */
    private int isFrom;
    private boolean hasSearch = false;


    @Override
    protected int getContentLayout() {
        return R.layout.fag_bj_search_item;
    }

    @Override
    protected void initView() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            isFrom = bundle.getInt("from");
        }
        initRecycler();
        initSmartRefreshLayout();
        LogUtil.d("OOM2", "isFrom=" + isFrom);
    }

    @Override
    protected void initAction() {
    }

    @Override
    protected void initData() {

    }


    private void initRecycler() {
        adapter = new MainRecyclerAdapter(allData, isFrom, true, null);
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            StatisticsEventAffair.getInstance().setFlag(getActivity(), "11_yj_searchfor", allData.get(position).getTitle());
            Intent intent = new Intent(getActivity(), PreviewUpAndDownActivity.class);
            ListForUpAndDown listForUpAndDown = new ListForUpAndDown(allData);
            intent.putExtra("person", listForUpAndDown);//直接存入被序列化的对象实例
            intent.putExtra("templateId", "");//直接存入被序列化的对象实例
            intent.putExtra("position", position);
            intent.putExtra("nowSelectPage", selectPage);
            intent.putExtra("searchText", searchText);
            if (isFrom == 0) {
                //模板页面
                intent.putExtra("fromTo", FromToTemplate.ISSEARCHTEMPLATE);
                StatisticsEventAffair.getInstance().setFlag(getActivity(), "20_search_mb_click", allData.get(position).getTitle());
            } else if (isFrom == 3) {
                String templateType = allData.get(position).getTemplate_type();
                if(!TextUtils.isEmpty(templateType)&& "3".equals(templateType)){
                    intent.putExtra("fromTo", FromToTemplate.DRESSUP);
                }else if(!TextUtils.isEmpty(templateType)&& "4".equals(templateType)){
                    intent.putExtra("fromTo", FromToTemplate.DRESSUP);
                }else{
                    intent.putExtra("fromTo", FromToTemplate.ISTEMPLATE);
                }
            } else {
                //背景页面
                intent.putExtra("fromTo", FromToTemplate.ISSEARCHBJ);
                StatisticsEventAffair.getInstance().setFlag(getActivity(), "20_search_bj_click", allData.get(position).getTitle());
            }
            startActivity(intent);
        });
    }


    /**
     * Fragment当前状态是否可见
     */
    protected boolean isVisible;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
        } else {
            isVisible = false;
        }
    }


    public void initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            isOnRefresh();
            isRefresh = true;
            refreshLayout.setEnableLoadMore(true);
            selectPage = 1;
            requestFagData(true);
        });
        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
            isOnLoadMore();
            isRefresh = false;
            selectPage++;
            requestFagData(false);
        });
    }


    //得到banner缓存数据
    public void requestData() {
        requestFagData(true); //首页杂数据
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
            if (isFrom == 3) {
                mTVHintSearch.setText("请上传需要的换装模板");
                mAddTitle.setText("上传换装模板");
            } else {
                mTVHintSearch.setText("请上传需要的视频模板");
                mAddTitle.setText("上传视频模板");
            }
        } else {
            lin_show_nodata.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d("OOM", "onDestroy");
        EventBus.getDefault().unregister(this);
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


    private void requestFagData(boolean isShowDialog) {

        if (!TextUtils.isEmpty(searchText)) {
            hasSearch = true;
            HashMap<String, String> params = new HashMap<>();
            params.put("search", searchText);
            params.put("page", selectPage + "");
            params.put("pageSize", perPageCount + "");
            if (isFrom == 0) {
                params.put("template_type", "1");
            } else if (isFrom == 3) {
                //不传表示所有
             //   params.put("template_type", "3");
            } else {
                params.put("template_type", "2");
            }
            Observable ob;
            if (isFrom == 3) {
                ob = Api.getDefault().materialList(BaseConstans.getRequestHead(params));
            } else {
                ob = Api.getDefault().getTemplate(BaseConstans.getRequestHead(params));
            }
            LogUtil.d("oom3", "搜索" + StringUtil.beanToJSONString(params));
            HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<NewFragmentTemplateItem>>(getActivity()) {
                @Override
                protected void onSubError(String message) {
                    finishData();
//                ToastUtil.showToast(message);
                }

                @Override
                protected void onSubNext(List<NewFragmentTemplateItem> data) {
                    LogUtil.d("oom3", "搜索结果" + StringUtil.beanToJSONString(data));

                    finishData();
                    if (isRefresh) {
                        allData.clear();
                    }
                    if (isRefresh && data.size() == 0) {

                        StatisticsEventAffair.getInstance().setFlag(getActivity(), "10_Noresults", searchText);
                        showNoData(true);
                        if (isVisible) {
                            if (isFrom == 1) {//背景无内容
                                StatisticsEventAffair.getInstance().setFlag(getActivity(), "20_search_bj", searchText);
                            } else {//模板无内容
                                StatisticsEventAffair.getInstance().setFlag(getActivity(), "20_search_mb", searchText);
                            }
                            ToastUtil.showToast("没有查询到输入内容，换个关键词试试");
                        }
                        if (isVisible) {
                            if (isFrom == 0) {
                                StatisticsEventAffair.getInstance().setFlag(getActivity(), "4_search_none", searchText);
                            } else {
                                StatisticsEventAffair.getInstance().setFlag(getActivity(), "4_search_none_bj", searchText);
                            }
                        }
                    } else {
                        showNoData(false);
                    }
                    if (!isRefresh && data.size() < perPageCount) {  //因为可能默认只请求8条数据
                        ToastUtil.showToast(getResources().getString(R.string.no_more_data));
                    }
                    if (data.size() < perPageCount) {
                        smartRefreshLayout.setEnableLoadMore(false);
                    }
                    allData.addAll(data);
                    adapter.notifyDataSetChanged();
                }
            }, "FagData", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, isShowDialog);
        }
    }


    @Subscribe
    public void onEventMainThread(SendSearchText event) {
        if (getActivity() != null) {
            LogUtil.d("OOM", event.getText());
            //搜索了内容
            searchText = event.getText();
            if (!TextUtils.isEmpty(searchText)) {
                isRefresh = true;
                selectPage = 1;
                smartRefreshLayout.setEnableLoadMore(true);
                requestFagData(true);
            }
        } else {
            ToastUtil.showToast("目标页面已销毁");
        }
    }

    @OnClick({R.id.relative_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.relative_add:
                if (getActivity() != null) {
                    if (getActivity().getPackageManager().checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, BuildConfig.APPLICATION_ID)
                            == PackageManager.PERMISSION_GRANTED) {
                        toUpLoad();
                    } else {
                        new AlertDialog.Builder(getActivity())
                                .setMessage("读取相册必须获取存储权限，如需使用接下来的功能，请同意授权~")
                                .setNegativeButton("取消", (dialog, which) -> {
                                    dialog.dismiss();
                                })
                                .setPositiveButton("去授权", (dialog, which) -> {
                                    PermissionUtil.gotoPermission(getActivity());
                                    dialog.dismiss();
                                }).create()
                                .show();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void toUpLoad() {
        if (BaseConstans.hasLogin()) {
            Intent intent = new Intent(getActivity(), UploadMaterialActivity.class);
            if (isFrom == 3) {
                AlbumManager.chooseImageAlbum(getContext(), 1, 0, (tag, paths, isCancel, isFromCamera, albumFileList) -> {
                    if (!isCancel) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("videoPath", paths.get(0));
                        intent.putExtra("isFrom", 2);
                        startActivity(intent);
                    }
                }, "");
            } else {
                AlbumManager.chooseVideo(getActivity(), 1, 1, (tag, paths, isCancel, isFromCamera, albumFileList) -> {
                    if (!isCancel) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("videoPath", paths.get(0));
                        intent.putExtra("isFrom", 1);
                        startActivity(intent);
                    }
                }, "");
            }
        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}
