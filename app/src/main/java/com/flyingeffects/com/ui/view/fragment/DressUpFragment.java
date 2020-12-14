package com.flyingeffects.com.ui.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.enity.FirstLevelTypeEntity;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.view.DressUpMvpView;
import com.flyingeffects.com.ui.presenter.DressUpMvpPresenter;
import com.flyingeffects.com.ui.view.activity.TemplateSearchActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

/**
 * description ：换装
 * creation date: 2020/12/1
 * user : zhangtongju
 */
public class DressUpFragment extends BaseFragment  implements DressUpMvpView {

    @BindView(R.id.tl_tabs)
    SlidingTabLayout tabLayout;

    @BindView(R.id.viewpager_bj)
    ViewPager viewpager;

    @BindView(R.id.tv_search_hint)
    TextView tvSearchHint;

    @BindView(R.id.relative_top)
    RelativeLayout mRelativeSearch;

    private List<FirstLevelTypeEntity> data;

    private FragmentManager manager;

    private  DressUpMvpPresenter Presenter;

    @Override
    protected int getContentLayout() {
        return R.layout.fag_dressup;
    }

    @Override
    protected void initView() {
        Presenter = new DressUpMvpPresenter(getActivity(), this);
    }

    @Override
    protected void initAction() {
    }

    @Override
    protected void initData() {
        manager = getChildFragmentManager();
        mRelativeSearch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TemplateSearchActivity.class);
            intent.putExtra("isFrom",3);
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
                        bundle.putString("tc_id","-1");
                        bundle.putSerializable("num", i);
                        bundle.putSerializable("from", 4);
                        bundle.putString("tabName", data.get(i).getName());
                        HomeTemplateItemFragment fragment = new HomeTemplateItemFragment();
                        fragment.setArguments(bundle);
                        list.add(fragment);
                    } else {
                        bundle.putSerializable("secondaryType", (Serializable) data.get(i).getCategory());
                        bundle.putSerializable("id", data.get(i).getId());
                        bundle.putInt("type",2);
                        bundle.putString("categoryTabName",data.get(i).getName());
                        SecondaryTypeFragment fragment = new SecondaryTypeFragment();
                        fragment.setArguments(bundle);
                        list.add(fragment);
                    }
                }
                home_vp_frg_adapter adapter = new home_vp_frg_adapter(manager, list);
                viewpager.setAdapter(adapter);
                viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int i, float v, int i1) {

                    }

                    @Override
                    public void onPageSelected(int i) {
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
                        statisticsEventAffair.getInstance().setFlag(getActivity(), "21_fece_tab", titles[position]);
                    }

                    @Override
                    public void onTabReselect(int position) {

                    }
                });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (data == null || data.size() == 0) {
            Presenter.getFragmentList();
        }
    }
}
