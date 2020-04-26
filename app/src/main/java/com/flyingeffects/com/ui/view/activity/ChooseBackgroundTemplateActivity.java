package com.flyingeffects.com.ui.view.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.DownVideoPath;
import com.flyingeffects.com.enity.TemplateType;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.ui.view.fragment.fragBjItem;
import com.flyingeffects.com.ui.view.fragment.frag_user_collect;
import com.flyingeffects.com.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;


/**
 * description ：自定义背景选择模板
 * creation date: 2020/4/22
 * user : zhangtongju
 */

public class ChooseBackgroundTemplateActivity extends BaseActivity {


    @BindView(R.id.viewpager_bj)
    ViewPager viewpager;

    @BindView(R.id.tl_tabs)
    SlidingTabLayout tabLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.act_choose_background_template;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initAction() {
        requestMainData();
    }


    private void requestMainData() {
        HashMap<String, String> params = new HashMap<>();
        Observable ob = Api.getDefault().getbackCategoryType(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<TemplateType>>(this) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(List<TemplateType> data) {
                setFragmentList(data);
            }
        }, "mainData", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, true, true, true);
    }


    public void setFragmentList(List<TemplateType> data) {
        if (data != null && data.size() > 0) {
            TemplateType templateType =new TemplateType();
            templateType.setId("collect");
            templateType.setName("收藏");
            data.add(templateType);
            ArrayList<Fragment> list = new ArrayList<>();
            FragmentManager manager = getSupportFragmentManager();
            String[] titles = new String[data.size()];
            for (int i = 0; i < data.size(); i++) {
                if(i==data.size()-1){
                    //手动添加收藏模板
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("template_type", "2");
                    titles[i] = data.get(i).getName();
                    frag_user_collect fag_0 = new frag_user_collect();
                    fag_0.setArguments(bundle);
                    list.add(fag_0);
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("id", data.get(i).getId());
                    bundle.putSerializable("from", 3);
                    bundle.putSerializable("num", i);
                    titles[i] = data.get(i).getName();
                    fragBjItem fragment = new fragBjItem();
                    fragment.setArguments(bundle);
                    list.add(fragment);
                }
            }




            home_vp_frg_adapter adapter = new home_vp_frg_adapter(manager, list);
            viewpager.setAdapter(adapter);
            tabLayout.setViewPager(viewpager, titles);
        }
    }


    @OnClick({R.id.iv_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                this.finish();
                break;
        }


        super.onClick(v);
    }


    /**
     * description ：裁剪页面裁剪成功后返回的数据
     * creation date: 2020/4/13
     * user : zhangtongju
     */
    @Subscribe
    public void onEventMainThread(DownVideoPath event) {
        this.finish();
    }

}
