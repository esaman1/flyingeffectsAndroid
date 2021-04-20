package com.flyingeffects.com.ui.view.fragment;

import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.CreationFrameListGridViewAdapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.enity.ClearChooseStickerState;
import com.flyingeffects.com.enity.HttpResult;
import com.flyingeffects.com.enity.ImageFrameEntity;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;

/**
 * @author ZhouGang
 * @date 2020/12/7
 * 贴纸fragment
 */
public class CreationFrameFragment extends BaseFragment {
    private static final String TAG = "CreationFrameFragment";

    @BindView(R.id.smart_refresh_layout)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.gridView)
    GridView mGridView;

    List<ImageFrameEntity> mImageFrameList = new ArrayList<>();
    private CreationFrameListGridViewAdapter mGridViewAdapter;

    private int selectPage = 1;
    private int perPageCount = 20;
    private boolean isRefresh = true;


    private BackChooseListener mBackChooseListener;

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_sticker;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initAction() {
        mImageFrameList.clear();
        isRefresh = true;
        selectPage = 1;
        requestFrameList(false);
    }

    @Override
    protected void initData() {

        mGridViewAdapter = new CreationFrameListGridViewAdapter(mImageFrameList, getContext());

        mGridView.setAdapter(mGridViewAdapter);

        mSmartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            isRefresh = true;
            refreshLayout.setEnableLoadMore(true);
            selectPage = 1;
            requestFrameList(false);
        });
        mSmartRefreshLayout.setOnLoadMoreListener(refresh -> {
            isRefresh = false;
            selectPage++;
            requestFrameList(false);
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                EventBus.getDefault().post(new ClearChooseStickerState());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        modificationSingleItemIsChecked(position);
                        if (mBackChooseListener != null) {
                            mBackChooseListener.chooseBack(position);
                        }
                        if (UiStep.isFromDownBj) {
                            StatisticsEventAffair.getInstance().setFlag(getContext(), " 5_mb_bj_Sticker", mImageFrameList.get(position).getTitle());
                        } else {
                            StatisticsEventAffair.getInstance().setFlag(getContext(), " 6_customize_bj_Sticker", mImageFrameList.get(position).getTitle());
                        }
                    }
                }, 200);

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    public void requestFrameList(boolean isShowDialog) {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", selectPage + "");
        params.put("pageSize", perPageCount + "");

        Observable<HttpResult<List<ImageFrameEntity>>> ob = Api.getDefault().imageBorder(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<ImageFrameEntity>>(getContext()) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(List<ImageFrameEntity> data) {
                finishData();
                if (isRefresh){
                    mImageFrameList.clear();
                }
                if (!isRefresh && data.size() < perPageCount) {  //因为可能默认只请求8条数据
                    ToastUtil.showToast(getString(R.string.no_more_data));
                }
                if (data.size() < perPageCount) {
                    mSmartRefreshLayout.setEnableLoadMore(false);
                }

                mImageFrameList.addAll(data);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, isShowDialog);
    }

    private void finishData() {
        mSmartRefreshLayout.finishRefresh();
        mSmartRefreshLayout.finishLoadMore();
    }

    private void modificationSingleItemIsChecked(int position) {
        for (ImageFrameEntity item : mImageFrameList) {
            item.setChecked(false);
        }
        ImageFrameEntity item1 = mImageFrameList.get(position);
        item1.setChecked(true);
        //修改对应的元素
        mImageFrameList.set(position, item1);
        mGridViewAdapter.notifyDataSetChanged();
    }

    public void setBackChooseListener(BackChooseListener listener) {
        mBackChooseListener = listener;
    }


    public interface BackChooseListener {
        void chooseBack(int position);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe
    public void onEventMainThread(ClearChooseStickerState clearChooseStickerState) {
        modificationChecked();
    }


    private void modificationChecked() {
        for (ImageFrameEntity item : mImageFrameList) {
            item.setChecked(false);
        }
        mGridViewAdapter.notifyDataSetChanged();
    }

}
