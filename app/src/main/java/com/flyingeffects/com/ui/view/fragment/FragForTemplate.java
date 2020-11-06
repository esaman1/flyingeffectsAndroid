package com.flyingeffects.com.ui.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.TemplateType;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.view.home_fagMvpView;
import com.flyingeffects.com.ui.presenter.home_fagMvpPresenter;
import com.flyingeffects.com.ui.view.activity.BackgroundSearchActivity;
import com.flyingeffects.com.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;


/**
 * user :TongJu  ;描述：模板页面
 * 时间：2018/4/24
 **/

public class FragForTemplate extends BaseFragment implements home_fagMvpView {

    home_fagMvpPresenter Presenter;
    @BindView(R.id.tl_tabs)
    SlidingTabLayout tabLayout;

    @BindView(R.id.viewpager_bj)
    ViewPager viewpager;
    @BindView(R.id.tv_search_hint)
    TextView tvSearchHint;

    private List<TemplateType> data;
    FragmentManager manager;

    private int nowChooseIndex;
    private ArrayList<String> listSearchKey = new ArrayList<>();
    int listSearchKeyIndex = 0;


    @Override
    protected int getContentLayout() {
        return R.layout.fragment_template;
    }


    @Override
    protected void initView() {
        Presenter = new home_fagMvpPresenter(getActivity(), this);
    }

    @Override
    protected void initAction() {
        //findViewById(R.id.iv_back).setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        manager = getChildFragmentManager();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (data == null || data.size() == 0) {
            Presenter.getFragmentList();
        } else {
            setFragmentList(data);
            if (viewpager != null && tabLayout != null) {
                viewpager.setCurrentItem(nowChooseIndex);
                tabLayout.setCurrentTab(nowChooseIndex);
            }
        }
        listSearchKeyIndex = 0;
        listSearchKey.clear();
        requestKeywordList();
    }


    @Override
    public void setFragmentList(List<TemplateType> data) {
        if (getActivity() != null) {
            if (data != null && data.size() > 0) {
                this.data = data;
                ArrayList<Fragment> list = new ArrayList<>();
                if (manager == null) {
                    manager = getFragmentManager();
                }
                String[] titles = new String[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("id", data.get(i).getId());
                    bundle.putSerializable("num", i);
                    bundle.putSerializable("from", 0);
                    titles[i] = data.get(i).getName();
                    HomeTemplateItemFragment fragment = new HomeTemplateItemFragment();
                    fragment.setArguments(bundle);
                    list.add(fragment);
                }
                home_vp_frg_adapter adapter = new home_vp_frg_adapter(manager, list);
                viewpager.setAdapter(adapter);
                viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i1) {

                    }

                    @Override
                    public void onPageSelected(int i) {
                        nowChooseIndex = i;
                        if (i <= data.size() - 1) {
                            statisticsEventAffair.getInstance().setFlag(getActivity(), "1_tab", titles[i]);
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {

                    }
                });
                tabLayout.setViewPager(viewpager, titles);
                tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
                    @Override
                    public void onTabSelect(int position) {
                        statisticsEventAffair.getInstance().setFlag(getActivity(), "13_template_tab_click", titles[position]);
                    }

                    @Override
                    public void onTabReselect(int position) {

                    }
                });
            }
        }

    }


    @OnClick({R.id.relative_top})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.relative_top:
                //搜索栏目
                Intent intent = new Intent(getActivity(), BackgroundSearchActivity.class);
                intent.putExtra("isFrom",1);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 请求友友推荐
     */
    private void requestKeywordList() {
        listSearchKey.clear();
        HashMap<String, String> params = new HashMap<>();
        params.put("template_type", "1");
        Observable ob = Api.getDefault().keywordList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(getContext()) {
            @Override
            protected void _onError(String message) {
            }

            @Override
            protected void _onNext(Object data) {
                String str = StringUtil.beanToJSONString(data);
                try {
                    JSONArray array = new JSONArray(str);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject ob = array.getJSONObject(i);
                        listSearchKey.add(ob.getString("name"));
                    }
                    pollingSetSearchText();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }

    /**
     * 轮询设置搜索关键字
     */
    private void pollingSetSearchText() {
        ScheduledExecutorService mScheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        mScheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (listSearchKeyIndex >= listSearchKey.size()) {
                    listSearchKeyIndex = 0;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            tvSearchHint.setText("友友们都在搜\"" + listSearchKey.get(listSearchKeyIndex) + "\"");
                            listSearchKeyIndex++;
                        }catch (Exception e){
                            tvSearchHint.setText("请输入视频关键字");
                            listSearchKeyIndex++;
                        }
                    }
                });
            }
        }, 0, 8, TimeUnit.SECONDS);
    }
}


