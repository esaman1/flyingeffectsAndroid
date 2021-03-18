package com.flyingeffects.com.ui.view.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.FirstLevelTypeEntity;
import com.flyingeffects.com.enity.SecondChoosePageListener;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.view.home_fagMvpView;
import com.flyingeffects.com.ui.presenter.home_fagMvpPresenter;
import com.flyingeffects.com.ui.view.activity.TemplateSearchActivity;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.Observable;


/**
 * user :TongJu  ;描述：模板页面
 * 时间：2018/4/24
 **/

public class FragForTemplate extends BaseFragment implements home_fagMvpView {

    home_fagMvpPresenter Presenter;
    @BindView(R.id.tl_tabs)
    TabLayout tabLayout;

    @BindView(R.id.viewpager_bj)
    ViewPager viewpager;
    @BindView(R.id.tv_search_hint)
    TextView tvSearchHint;

    private List<FirstLevelTypeEntity> data;
    FragmentManager manager;
    ScheduledExecutorService mScheduledExecutorService;

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
        }


        LogUtil.d("OOM4","setUserVisibleHint="+getUserVisibleHint());
        listSearchKeyIndex = 0;
        if (!listSearchKey.isEmpty()) {
            if (mScheduledExecutorService != null) {
                mScheduledExecutorService.shutdownNow();
                mScheduledExecutorService = null;

            }
            pollingSetSearchText();
        } else {
            requestKeywordList();
        }

    }


    @Override
    public void setFragmentList(List<FirstLevelTypeEntity> data) {
        if (getActivity() != null) {
            if (data != null && data.size() > 0) {
                this.data = data;
                ArrayList<Fragment> list = new ArrayList<>();
                if (manager == null) {
                    manager = getFragmentManager();
                }
                for (int i = 0; i < data.size(); i++) {
                    Bundle bundle = new Bundle();
                    if (data.get(i).getCategory() != null && !data.get(i).getCategory().isEmpty()) {
                        bundle.putSerializable("secondaryType", (Serializable) data.get(i).getCategory());
                        bundle.putInt("type", 0);
                        bundle.putSerializable("id", data.get(i).getId());
                        bundle.putSerializable("homePageNum", 1);
                        bundle.putString("categoryTabName", data.get(i).getName());
                        SecondaryTypeFragment fragment = new SecondaryTypeFragment();
                        fragment.setArguments(bundle);
                        list.add(fragment);
                    } else {
                        bundle.putSerializable("id", data.get(i).getId());
                        bundle.putString("tc_id", "-1");
                        bundle.putSerializable("num", i);
                        bundle.putSerializable("homePageNum", 1);
                        bundle.putSerializable("from", 0);
                        HomeTemplateItemFragment fragment = new HomeTemplateItemFragment();
                        fragment.setArguments(bundle);
                        list.add(fragment);
                    }

                }
                home_vp_frg_adapter adapter = new home_vp_frg_adapter(manager, list);
                viewpager.setAdapter(adapter);
                viewpager.setOffscreenPageLimit(1);
                viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i1) {

                    }

                    @Override
                    public void onPageSelected(int i) {
                        EventBus.getDefault().post(new SecondChoosePageListener(i));
                        if (i <= data.size() - 1) {
                            StatisticsEventAffair.getInstance().setFlag(getActivity(), "1_tab", data.get(i).getName());
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int i) {

                    }
                });
                tabLayout.setupWithViewPager(viewpager);
                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    tabLayout.getTabAt(i).setCustomView(R.layout.item_home_tab);
                    View view = tabLayout.getTabAt(i).getCustomView();
                    AppCompatTextView tvTabText = view.findViewById(R.id.tv_tab_item_text);
                    tvTabText.setText(data.get(i).getName());
                    tvTabText.setTextColor(Color.parseColor("#797979"));
                    if (i == 0) {
                        tvTabText.setTextSize(24);
                        tvTabText.setTextColor(Color.parseColor("#ffffff"));
                    }
                }
                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        View view = tab.getCustomView();
                        if (view != null) {
                            AppCompatTextView tvTabText = view.findViewById(R.id.tv_tab_item_text);
                            tvTabText.setTextSize(24);
                            tvTabText.setTextColor(Color.parseColor("#ffffff"));
                            StatisticsEventAffair.getInstance().setFlag(getActivity(), "13_template_tab_click", tvTabText.getText().toString());

                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        View view = tab.getCustomView();
                        if (view!=null){
                            AppCompatTextView tvTabText = view.findViewById(R.id.tv_tab_item_text);
                            tvTabText.setTextSize(16);
                            tvTabText.setTextColor(Color.parseColor("#797979"));
                        }
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

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
                Intent intent = new Intent(getActivity(), TemplateSearchActivity.class);
                intent.putExtra("isFrom", 1);
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
            protected void onSubError(String message) {
            }

            @Override
            protected void onSubNext(Object data) {
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
        mScheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        mScheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (listSearchKeyIndex >= listSearchKey.size()) {
                listSearchKeyIndex = 0;
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        tvSearchHint.setText("友友们都在搜\"" + listSearchKey.get(listSearchKeyIndex) + "\"");
                        listSearchKeyIndex++;
                    } catch (Exception e) {
                        tvSearchHint.setText("请输入视频关键字");
                        listSearchKeyIndex++;
                    }
                }
            });
        }, 0, 8, TimeUnit.SECONDS);

    }
}


