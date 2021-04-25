package com.flyingeffects.com.ui.view.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.enity.FirstLevelTypeEntity;
import com.flyingeffects.com.enity.SecondChoosePageListener;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.view.DressUpMvpView;
import com.flyingeffects.com.ui.presenter.DressUpMvpPresenter;
import com.flyingeffects.com.ui.view.activity.TemplateSearchActivity;
import com.google.android.material.tabs.TabLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import de.greenrobot.event.EventBus;

/**
 * description ：换装
 * creation date: 2020/12/1
 * user : zhangtongju
 */
public class DressUpFragment extends BaseFragment implements DressUpMvpView {

    @BindView(R.id.tl_tabs)
    TabLayout tabLayout;

    @BindView(R.id.viewpager_bj)
    ViewPager viewpager;

    @BindView(R.id.tv_search_hint)
    TextView tvSearchHint;

    @BindView(R.id.relative_top)
    RelativeLayout mRelativeSearch;

    private List<FirstLevelTypeEntity> data;

    private FragmentManager manager;

    private DressUpMvpPresenter mPresenter;

    @Override
    protected int getContentLayout() {
        return R.layout.fag_dressup;
    }

    @Override
    protected void initView() {
        mPresenter = new DressUpMvpPresenter(getActivity(), this);
    }

    @Override
    protected void initAction() {
    }

    @Override
    protected void initData() {
        manager = getChildFragmentManager();
        mRelativeSearch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TemplateSearchActivity.class);
            intent.putExtra("isFrom", 3);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
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
                String[] titles = new String[data.size()];
                for (int i = 0; i < data.size(); i++) {
                    Bundle bundle = new Bundle();
                    titles[i] = data.get(i).getName();
                    if (TextUtils.equals("收藏", data.get(i).getName())) {
                        bundle.putSerializable("id", data.get(i).getId());
                        bundle.putString("tc_id", "-1");
                        bundle.putSerializable("num", i);
                        bundle.putSerializable("from", 4);
                        bundle.putString("tabName", data.get(i).getName());
                        bundle.putSerializable("homePageNum", 2);
                        HomeTemplateItemFragment fragment = new HomeTemplateItemFragment();
                        fragment.setArguments(bundle);
                        list.add(fragment);
                    } else {
                        if (data.get(i).getCategory() != null && !data.get(i).getCategory().isEmpty()) {
                            Bundle bundle1 = SecondaryTypeFragment.buildArgument(data.get(i).getCategory(), SecondaryTypeFragment.BUNDLE_VALUE_TYPE_FACE, data.get(i).getId(),
                                    -1, -1, 2, null, data.get(i).getName());
                            SecondaryTypeFragment fragment = new SecondaryTypeFragment();
                            fragment.setArguments(bundle1);
                            list.add(fragment);
                        } else {
                            bundle.putSerializable("id", data.get(i).getId());
                            bundle.putString("tc_id", "-1");
                            bundle.putSerializable("homePageNum", 2);
                            bundle.putSerializable("num", i);
                            bundle.putSerializable("from", 4);
                            HomeTemplateItemFragment fragment = new HomeTemplateItemFragment();
                            fragment.setArguments(bundle);
                            list.add(fragment);
                        }
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
                            StatisticsEventAffair.getInstance().setFlag(getActivity(), "hp_st_tab", titles[i]);
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
                    tvTabText.setText(titles[i]);
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
                        AppCompatTextView tvTabText = view.findViewById(R.id.tv_tab_item_text);
                        tvTabText.setTextSize(24);
                        tvTabText.setTextColor(Color.parseColor("#ffffff"));
                        StatisticsEventAffair.getInstance().setFlag(getActivity(), "21_fece_tab", tvTabText.getText().toString());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        View view = tab.getCustomView();
                        AppCompatTextView tvTabText = view.findViewById(R.id.tv_tab_item_text);
                        tvTabText.setTextSize(16);
                        tvTabText.setTextColor(Color.parseColor("#797979"));
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }

                });

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (data == null || data.size() == 0) {
            mPresenter.getFragmentList();
        }
    }
}
