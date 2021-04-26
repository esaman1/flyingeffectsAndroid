package com.flyingeffects.com.ui.view.fragment;

import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.CreationBackListGridViewAdapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.enity.ClearChooseStickerState;
import com.flyingeffects.com.enity.HttpResult;
import com.flyingeffects.com.enity.NewFragmentTemplateItem;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.utils.LogUtil;
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
public class CreationBackListFragment extends BaseFragment {


    private static final String TAG = "CreationBackListFragmen";

    @BindView(R.id.smart_refresh_layout)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.gridView)
    GridView mGridView;

    List<NewFragmentTemplateItem> listForSticker = new ArrayList<>();
    private CreationBackListGridViewAdapter mGridViewAdapter;

    private int selectPage = 1;
    private int perPageCount = 10;
    private boolean isRefresh = true;

    private String mId;

    private BackChooseListener mBackChooseListener;
    private String mName;

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_sticker;
    }

    @Override
    protected void initView() {
        mId = getArguments().getString("id");
        mName = getArguments().getString("categoryName");
        LogUtil.d(TAG, "initView");
    }

    @Override
    protected void initAction() {
        isRefresh = true;
        selectPage = 1;

        requestBackList(false);
    }

    @Override
    protected void initData() {

        mGridViewAdapter = new CreationBackListGridViewAdapter(listForSticker, getContext());

        mGridView.setAdapter(mGridViewAdapter);

        mSmartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            isRefresh = true;
            refreshLayout.setEnableLoadMore(true);
            selectPage = 1;
            requestBackList(false);
        });

        mSmartRefreshLayout.setOnLoadMoreListener(refresh -> {
            isRefresh = false;
            selectPage++;
            requestBackList(false);
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
                            mBackChooseListener.chooseBack(listForSticker.get(position).getTitle(), listForSticker.get(position).getBackground_image());
                        }

                        if (UiStep.isFromDownBj) {
                            StatisticsEventAffair.getInstance().setFlag(getContext(), " 5_mb_bj_Sticker", listForSticker.get(position).getTitle());
                        } else {
                            StatisticsEventAffair.getInstance().setFlag(getContext(), " 6_customize_bj_Sticker", listForSticker.get(position).getTitle());
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

    public void requestBackList(boolean isShowDialog) {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", selectPage + "");
        params.put("pageSize", perPageCount + "");
        params.put("category_id", "2");
        if (Integer.parseInt(mId) >= 0) {
            params.put("tc_id", mId);
        }
        LogUtil.d(TAG, "category_id = " + mId);

        Observable<HttpResult<List<NewFragmentTemplateItem>>> ob = Api.getDefault().materialList(BaseConstans.getRequestHead(params));

        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<NewFragmentTemplateItem>>(getContext()) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(List<NewFragmentTemplateItem> list) {
                if (isRefresh) {
                    listForSticker.clear();
                }
                finishData();
                if (!isRefresh && list.size() < perPageCount) {  //因为可能默认只请求8条数据
                    ToastUtil.showToast(getString(R.string.no_more_data));
                }

                if (list.size() < perPageCount) {
                    mSmartRefreshLayout.setEnableLoadMore(false);
                }

                if (listForSticker.size() == 0) {
                    addBackChooseItem();
                }

                listForSticker.addAll(list);
                mGridViewAdapter.notifyDataSetChanged();
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, isShowDialog);
    }

    private void addBackChooseItem() {
        if ("全部".equals(mName)) {
            NewFragmentTemplateItem item = new NewFragmentTemplateItem();
            item.setTitle("本地背景");
            item.setBackground_image("");
            listForSticker.add(item);
        }
    }

    private void finishData() {
        mSmartRefreshLayout.finishRefresh();
        mSmartRefreshLayout.finishLoadMore();
    }

    private void modificationSingleItemIsChecked(int position) {
        for (NewFragmentTemplateItem item : listForSticker) {
            item.setChecked(false);
        }
        NewFragmentTemplateItem item1 = listForSticker.get(position);
        item1.setChecked(true);
        //修改对应的元素
        listForSticker.set(position, item1);
        mGridViewAdapter.notifyDataSetChanged();
    }

    public void setBackChooseListener(BackChooseListener listener) {
        mBackChooseListener = listener;
    }


    public interface BackChooseListener {
        void chooseBack(String title, String path);
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
        for (NewFragmentTemplateItem item : listForSticker) {
            item.setChecked(false);
        }
        mGridViewAdapter.notifyDataSetChanged();
    }

}
