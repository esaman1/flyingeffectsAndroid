package com.flyingeffects.com.ui.view.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.databinding.ActChooseBackgroundTemplateBinding;
import com.flyingeffects.com.entity.DownVideoPath;
import com.flyingeffects.com.entity.FirstLevelTypeEntity;
import com.flyingeffects.com.entity.HttpResult;
import com.flyingeffects.com.entity.NewFragmentTemplateItem;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.ui.view.fragment.SecondaryTypeFragment;
import com.flyingeffects.com.ui.view.fragment.fragBjItem;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;


/**
 * description ：自定义背景选择模板
 * creation date: 2020/4/22
 * @author : zhangtongju
 */
public class ChooseBackgroundTemplateActivity extends BaseActivity {
    private NewFragmentTemplateItem templateItem;
    private ActChooseBackgroundTemplateBinding mBinding;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {
        mBinding = ActChooseBackgroundTemplateBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        setContentView(rootView);

        EventBus.getDefault().register(this);
        templateItem = (NewFragmentTemplateItem) getIntent().getSerializableExtra("templateItem");
        setOnClickListener();
    }

    private void setOnClickListener() {
        mBinding.ivBack.setOnClickListener(this::onViewClicked);
    }

    private void onViewClicked(View view) {
        if (view == mBinding.ivBack) {
            finish();
        }
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
        params.put("type", "2");
        Observable<HttpResult<List<FirstLevelTypeEntity>>> ob = Api.getDefault().getCategoryList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<FirstLevelTypeEntity>>(this) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(List<FirstLevelTypeEntity> data) {
                setFragmentList(data);
            }
        }, "mainData", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, true, true, false);
    }


    public void setFragmentList(List<FirstLevelTypeEntity> data) {
        if (data != null && data.size() > 0) {
            ArrayList<Fragment> list = new ArrayList<>();
            FragmentManager manager = getSupportFragmentManager();
            String[] titles = new String[data.size()];
            for (int i = 0; i < data.size(); i++) {
                if (TextUtils.equals("关注", data.get(i).getName()) || TextUtils.equals("收藏", data.get(i).getName())) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("id", data.get(i).getId());
                    bundle.putSerializable("from", 3);
                    bundle.putSerializable("num", i);
                    fragBjItem fragment = new fragBjItem();
                    fragment.setArguments(bundle);
                    list.add(fragment);
                } else {
                    Bundle bundle = new Bundle();
                    if (data.get(i).getCategory() != null && !data.get(i).getCategory().isEmpty()) {
                        bundle.putSerializable("secondaryType", (Serializable) data.get(i).getCategory());
                        bundle.putInt("type", 1);
                        bundle.putSerializable("id", data.get(i).getId());
                        bundle.putInt("from", 3);
                        bundle.putString("categoryTabName", data.get(i).getName());
                        if (templateItem != null) {
                            //一键模板选择背景
                            bundle.putSerializable("cover", templateItem.getImage());
                        }
                        SecondaryTypeFragment fragment = new SecondaryTypeFragment();
                        fragment.setArguments(bundle);
                        list.add(fragment);
                    } else {
                        bundle.putSerializable("id", data.get(i).getId());
                        bundle.putString("tc_id", "-1");
                        bundle.putInt("from", 3);
                        bundle.putSerializable("num", i);
                        if (templateItem != null) {
                            //一键模板选择背景
                            bundle.putSerializable("cover", templateItem.getImage());
                        }
                        fragBjItem fragment = new fragBjItem();
                        fragment.setArguments(bundle);
                        list.add(fragment);
                    }
                }
                titles[i] = data.get(i).getName();
            }
            home_vp_frg_adapter adapter = new home_vp_frg_adapter(manager, list);
            mBinding.viewpagerBj.setAdapter(adapter);
            mBinding.tlTabs.setViewPager(mBinding.viewpagerBj, titles);
        }
    }


    /**
     * description ：裁剪页面裁剪成功后返回的数据
     * creation date: 2020/4/13
     * user : zhangtongju
     */
    @Subscribe
    public void onEventMainThread(DownVideoPath event) {
        LogUtil.d("OOM2", "销毁了onEventMainThread");
        this.finish();
    }

}
