package com.flyingeffects.com.ui.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.Upload_bj_list_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.ListForUpAndDown;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.statisticsEventAffair;
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
import rx.Observable;


/***
 * 我上传的背景
 */

public class frag_user_upload_bj extends BaseFragment {
    private static final String TAG = "frag_user_upload_bj";
    private boolean isRefresh = true;

    private Upload_bj_list_adapter adapter;

    private List<new_fag_template_item> allData = new ArrayList<>();
    @BindView(R.id.smart_refresh_layout_collect)
    SmartRefreshLayout smartRefreshLayout;
    private int perPageCount = 10;

    @BindView(R.id.recyclerView_collect)
    RecyclerView recyclerView;

    private Context mContext;

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    protected void initView() {
//        EventBus.getDefault().register(getActivity());
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
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<new_fag_template_item>>(getActivity()) {
            @Override
            protected void _onError(String message) {
                finishData();
                Log.e(TAG, "_onError: " + message);
            }

            @Override
            protected void _onNext(List<new_fag_template_item> data) {
                LogUtil.d(TAG, StringUtil.beanToJSONString(data));
                finishData();
                if (isRefresh) {
                    listData.clear();
                    new_fag_template_item item = new new_fag_template_item();
                    item.setTitle("test");
                    listData.add(item);
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
            if (BaseConstans.hasLogin()) {
                isRefresh = true;
                refreshLayout.setEnableLoadMore(true);
                selectPage = 1;
                requestUploadBjList(false);
            } else {
                finishData();
                ToastUtil.showToast("请先登录");
            }
        });
        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
            if (BaseConstans.hasLogin()) {
                isRefresh = false;
                selectPage++;
                requestUploadBjList(false);
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
            requestUploadBjList(false);
        } else {
            tv_hint.setVisibility(View.VISIBLE);
            tv_hint.setText("请先登录");
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
        adapter = new Upload_bj_list_adapter(R.layout.list_upload_bj_item, allData, getActivity(), (id) -> {
            statisticsEventAffair.getInstance().setFlag(getActivity(), "9_deletebj");
            new AlertDialog.Builder(mContext)
                    .setMessage("确定要删除这个背景吗？")
                    .setNegativeButton("取消", (dialog, which) -> {
                        dialog.dismiss();
                        statisticsEventAffair.getInstance().setFlag(getActivity(), "9_deletebj3");

                    })
                    .setPositiveButton("确定", (dialog, which) -> {
                        requestDelete(id);
                    }).create().show();
        });
        layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (!DoubleClick.getInstance().isFastDoubleClick()) {
                Intent intent = new Intent(getActivity(), PreviewUpAndDownActivity.class);
                ListForUpAndDown listForUpAndDown = new ListForUpAndDown(allData);
                intent.putExtra("person", listForUpAndDown);//直接存入被序列化的对象实例
                intent.putExtra("templateId", "");//直接存入被序列化的对象实例
                intent.putExtra("position", position);
                intent.putExtra("fromToMineCollect", true);
                if (allData.get(position).getTest() != 0) {
                    intent.putExtra("readOnly", true);
                } else {
                    intent.putExtra("readOnly", false);
                }
                ;
                intent.putExtra("nowSelectPage", selectPage);
                intent.putExtra("fromTo", FromToTemplate.ISFROMUPDATEBJ);
                startActivity(intent);
//                Intent intent = new Intent(getActivity(), PreviewActivity.class);
//                intent.putExtra("person", allData.get(position));//直接存入被序列化的对象实例
//                if (allData.get(position).getTest() != 0) {
//                    intent.putExtra("readOnly", true);
//                } else {
//                    intent.putExtra("readOnly", false);
//                }
//                intent.putExtra("fromToMineCollect", true);
//                intent.putExtra("fromTo", FromToTemplate.ISFROMUPDATEBJ);
//                intent.putExtra("person", allData.get(position));//直接存入被序列化的对象实例
//                startActivity(intent);
            }
        });
    }

    private void requestDelete(String id) {
        statisticsEventAffair.getInstance().setFlag(getActivity(), "9_deletebj2");
        HashMap<String, String> params = new HashMap<>();
        params.put("id", id);
        Observable ob = Api.getDefault().deleteBackground(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(getActivity()) {
            @Override
            protected void _onNext(Object o) {
                LogUtil.d(TAG, "requestDelete: " + o);
                //todo object为空，要解决返回信息的处理问题
                isRefresh = true;
                selectPage = 1;
                requestUploadBjList(false);
            }

            @Override
            protected void _onError(String message) {
                Log.e(TAG, "requestDelete_onError: " + message);
                finishData();
            }

//            @Override
//            protected void _onNext(List<new_fag_template_item> data) {
//
//                isRefresh = true;
//                selectPage = 1;
//                requestUploadBjList(false);
//            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);


    }


//    @Subscribe
//    public void onEventMainThread(uploadMaterialEvent event) {
//        uploadPathList
//        new_fag_template_item item = new new_fag_template_item();
//        item.setTitle("test");
//        item.setImage(event.getUploadPathList().get(3));
//        item.setTitle(event.getNickName());
//        listData.add(1,item);
//        adapter.notifyDataSetChanged();
//    }


//
//    //当前上传的标识
//    int nowUpdateIndex;
//    private ArrayList<String> uploadPathList;
//    private void uploadFileToHuawei(String videoPath, String copyName) {
//        Log.d("OOM2","uploadFileToHuawei");
//        new Thread(() -> huaweiObs.getInstance().uploadFileToHawei(videoPath, copyName, new huaweiObs.Callback() {
//            @Override
//            public void isSuccess(String str) {
//
//                Observable.just(str).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
//                    @Override
//                    public void call(String s) {
//                        if (nowUpdateIndex != uploadPathList.size()-1) {
//                            nowUpdateIndex++;
//                            Log.d("OOM2","nowUpdateIndex="+nowUpdateIndex);
//                            uploadFileToHuawei(uploadPathList.get(nowUpdateIndex),getPathName(nowUpdateIndex,uploadPathList.get(nowUpdateIndex)));
//                        } else {
//                            requestData();
//                        }
//                    }
//                });
//
//            }
//        })).start();
//    }

}


