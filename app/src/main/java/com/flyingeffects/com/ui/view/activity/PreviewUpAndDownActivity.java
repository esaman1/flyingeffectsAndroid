package com.flyingeffects.com.ui.view.activity;

import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.Preview_up_and_down_adapter;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.enity.ListForUpAndDown;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.ui.interfaces.view.PreviewUpAndDownMvpView;
import com.flyingeffects.com.ui.presenter.PreviewUpAndDownMvpPresenter;
import com.flyingeffects.com.utils.LogUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * description ：仿抖音效果的预览页面,替换掉目前存在的预览页面
 * creation date: 2020/6/24
 * user : zhangtongju
 */
public class PreviewUpAndDownActivity extends BaseActivity implements PreviewUpAndDownMvpView {

    @BindView(R.id.page2)
    ViewPager2 viewPage2;

    @BindView(R.id.refresh)
    SmartRefreshLayout smartRefreshLayout;

    PreviewUpAndDownMvpPresenter Presenter;

    private Preview_up_and_down_adapter adapter;

    private List<new_fag_template_item> allData = new ArrayList<>();
    //当前选中的页码
    private int nowChoosePosition;

    private boolean isPause;


    @Override
    protected int getLayoutId() {
        return R.layout.act_preview_up_and_down;
    }

    @Override
    protected void initView() {
        ListForUpAndDown listForUpAndDown = (ListForUpAndDown) getIntent().getSerializableExtra("person");
        allData = listForUpAndDown.getAllData();
        nowChoosePosition = getIntent().getIntExtra("position", 0);
        Presenter = new PreviewUpAndDownMvpPresenter(this, this);
        Presenter.initSmartRefreshLayout(smartRefreshLayout);
        adapter = new Preview_up_and_down_adapter(R.layout.list_preview_up_down_item, allData, PreviewUpAndDownActivity.this);
        viewPage2.setAdapter(adapter);
        viewPage2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                adapter.NowPreviewChooseItem(position);
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
        viewPage2.setCurrentItem(nowChoosePosition,false);
    }

    @Override
    protected void initAction() {

    }


    @Override
    protected void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        GSYVideoManager.onResume();
        isPause = false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        if (adapter != null) {
            adapter.onDestroy();
        }
    }


    @OnClick({R.id.ibBack})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibBack:
                this.finish();
                break;

            default:
                break;
        }
    }





}
