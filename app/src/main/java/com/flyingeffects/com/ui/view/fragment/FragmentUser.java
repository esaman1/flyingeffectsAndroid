package com.flyingeffects.com.ui.view.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.SearchUserAdapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.entity.AttentionChange;
import com.flyingeffects.com.entity.LoginToAttentionUserEvent;
import com.flyingeffects.com.entity.SearchUserEntity;
import com.flyingeffects.com.entity.SendSearchText;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;

/**
 * @author ZhouGang
 * @date 2020/10/20
 * 搜索用户
 */
public class FragmentUser extends BaseFragment {
    @BindView(R.id.rc_user)
    RecyclerView rcUser;
    @BindView(R.id.smart_refresh_layout_user)
    SmartRefreshLayout smartRefreshLayoutUser;
    @BindView(R.id.ll_na_data)
    LinearLayout llNaData;

    SearchUserAdapter adapter;
    private boolean isRefresh = true;
    private int selectPage = 1;
    private int perPageCount = 10;
    /**默认值肯定为""*/
    private String searchText;
    List<SearchUserEntity> allData = new ArrayList<>();

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_search_user;
    }

    @Override
    protected void initView() {
        initSmartRefreshLayout();
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void initData() {
       adapter = new SearchUserAdapter(R.layout.item_search_user,allData,getActivity());
       adapter.setUserInfo(Hawk.get("UserInfo"));
       rcUser.setAdapter(adapter);
       adapter.setOnAttentionListener(new SearchUserAdapter.OnAttentionListener() {
           @Override
           public void attention(int id) {
               requestFocus(String.valueOf(id));
           }
       });
    }

    public void initSmartRefreshLayout() {
        smartRefreshLayoutUser.setOnRefreshListener(refreshLayout -> {
            isRefresh = true;
            refreshLayout.setEnableLoadMore(true);
            selectPage = 1;
            requestFagData(true);
        });
        smartRefreshLayoutUser.setOnLoadMoreListener(refresh -> {
            isRefresh = false;
            selectPage++;
            requestFagData(false);
        });
    }

    @Subscribe
    public void onEventMainThread(SendSearchText event) {
        if (getActivity() != null) {
            LogUtil.d("OOM", event.getText());
            //搜索了内容
            searchText = event.getText();
            if(!TextUtils.isEmpty(searchText)){
                isRefresh = true;
                selectPage = 1;
                smartRefreshLayoutUser.setEnableLoadMore(true);
                requestFagData(true);
            }
        } else {
            ToastUtil.showToast("目标页面已销毁");
        }
    }

    /**
     * description ：关注该用户
     * creation date: 2020/7/30
     * user : zhangtongju
     */
    private void requestFocus(String to_user_id) {
        HashMap<String, String> params = new HashMap<>();
        params.put("to_user_id", to_user_id);
        Observable ob = Api.getDefault().followUser(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(getContext()) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(Object data) {
                LogUtil.d("OOM", StringUtil.beanToJSONString(data));
                attentionUserRequestFagData();
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }

    /**关注用户刷新用户列表*/
    private void attentionUserRequestFagData(){
        isRefresh = true;
        smartRefreshLayoutUser.setEnableLoadMore(true);
        selectPage = 1;
        rcUser.scrollToPosition(0);
        requestFagData(false);
    }

    private void requestFagData(boolean isShowDialog) {
        if (!TextUtils.isEmpty(searchText)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("search", searchText);
            params.put("page", selectPage + "");
            params.put("pageSize", perPageCount + "");
            Observable ob = Api.getDefault().getSearchUserList(BaseConstans.getRequestHead(params));
            HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<SearchUserEntity>>(getContext()) {
                @Override
                protected void onSubError(String message) {
                    smartRefreshLayoutUser.finishRefresh();
                    smartRefreshLayoutUser.finishLoadMore();
                }

                @Override
                protected void onSubNext(List<SearchUserEntity> datas) {
                    LogUtil.d("OOM", StringUtil.beanToJSONString(datas));
                    smartRefreshLayoutUser.finishRefresh();
                    smartRefreshLayoutUser.finishLoadMore();
                    if (isRefresh) {
                        allData.clear();
                    }
                    if (isRefresh && datas.size() == 0) {
                        smartRefreshLayoutUser.setVisibility(View.GONE);
                        llNaData.setVisibility(View.VISIBLE);
                    }else {
                        smartRefreshLayoutUser.setVisibility(View.VISIBLE);
                        llNaData.setVisibility(View.GONE);
                    }
                    if (!isRefresh && datas.size() < perPageCount) {  //因为可能默认只请求8条数据
                        ToastUtil.showToast(getResources().getString(R.string.no_more_data));
                    }
                    if (datas.size() < perPageCount) {
                        smartRefreshLayoutUser.setEnableLoadMore(false);
                    }
                    allData.addAll(datas);
                    adapter.notifyDataSetChanged();
                }
            }, "FagData", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, isShowDialog);
        }
    }

    @Subscribe
    public void onEventMainThread(AttentionChange change){
        attentionUserRequestFagData();
    }

    @Subscribe
    public void onEventMainThread(LoginToAttentionUserEvent event){
        attentionUserRequestFagData();
        adapter.setUserInfo(Hawk.get("UserInfo"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
