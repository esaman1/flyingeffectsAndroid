package com.flyingeffects.com.ui.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.home_vp_frg_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.FirstLevelTypeEntity;
import com.flyingeffects.com.enity.SecondaryTypeEntity;
import com.flyingeffects.com.enity.StickerTypeEntity;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.ui.view.activity.CreationTemplateActivity;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;

public class CreationBottomFragment extends BaseFragment implements CreationBackListFragment.BackChooseListener, CreationFrameFragment.FrameChooseListener, StickerFragment.StickerListener {
    private static final String TAG = "CreationBottomFragment";
    private SlidingTabLayout mSlidingTabLayout;
    private TextView mTvFinish;
    private ViewPager mVpBottom;
    private ImageView mIvDeleteSticker;

    private int mId;
    private CreationBackListFragment.BackChooseListener mBackChooseListener;
    CreationFrameFragment.FrameChooseListener mFrameChooseListener;

    @Override
    protected int getContentLayout() {
        return R.layout.view_creation_back;
    }

    @Override
    protected void initView() {
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.tb_sticker);
        mTvFinish = (TextView) findViewById(R.id.iv_down_sticker);
        mVpBottom = (ViewPager) findViewById(R.id.view_pager);
        mIvDeleteSticker = (ImageView) findViewById(R.id.iv_delete_sticker);

        setOnClickListener();
    }

    private void setOnClickListener() {
        mTvFinish.setOnClickListener(this::onViewClicked);
        mIvDeleteSticker.setOnClickListener(this::onViewClicked);
    }

    private void onViewClicked(View view) {
        if (view.getId() == R.id.iv_down_sticker) {
            mFinishListener.onFinishClicked();
        } else if (view.getId() == R.id.iv_delete_sticker) {
            mFinishListener.onClearClicked(mId);
        }
    }

    @Override
    protected void initAction() {
        mId = getArguments().getInt("id");
    }

    @Override
    protected void initData() {
        if (mId == 0) {
            requestBackList(mVpBottom, mSlidingTabLayout, getChildFragmentManager());
            mIvDeleteSticker.setVisibility(View.GONE);
        } else if (mId == 1) {
            requestPhotoFrameList(mVpBottom, mSlidingTabLayout, getChildFragmentManager());
            mIvDeleteSticker.setVisibility(View.GONE);
        } else {
            getStickerTypeList(getChildFragmentManager(), mVpBottom, mSlidingTabLayout);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * 请求背景
     *
     * @param backViewPager
     * @param backTab
     * @param fragmentManager
     */
    public void requestBackList(ViewPager backViewPager, SlidingTabLayout backTab, FragmentManager fragmentManager) {
        HashMap<String, String> params = new HashMap<>();
        //类型 1模板 2背景 3换脸  4 加上了最新的闪图
        params.put("type", "4");

        Observable ob = Api.getDefault().getCategoryList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<FirstLevelTypeEntity>>(getContext()) {
            @Override
            protected void onSubError(String message) {
                LogUtil.e(TAG, message);
                ToastUtil.showToast("背景列表加载错误：" + message);
            }

            @Override
            protected void onSubNext(List<FirstLevelTypeEntity> data) {
                String dataStr = StringUtil.beanToJSONString(data);
                LogUtil.d(TAG, dataStr);

                List<Fragment> fragments = new ArrayList<>();

                for (int i = 0; i < data.size(); i++) {
                    if ("换背景".equals(data.get(i).getName())) {
                        List<SecondaryTypeEntity> categoryList = data.get(i).getCategory();
                        String[] titles = new String[categoryList.size()];
                        for (int j = 0; j < categoryList.size(); j++) {
                            titles[j] = categoryList.get(j).getName();
                            Bundle bundle = new Bundle();
                            bundle.putString("id", categoryList.get(j).getId());
                            bundle.putString("categoryName", categoryList.get(j).getName());
                            CreationBackListFragment fragment = new CreationBackListFragment();
                            fragment.setBackChooseListener(CreationBottomFragment.this);
                            fragment.setArguments(bundle);
                            fragments.add(fragment);
                        }

                        LogUtil.d(TAG, "requestBackList");
                        home_vp_frg_adapter backFragAdapter = new home_vp_frg_adapter(fragmentManager, fragments);
                        backViewPager.setAdapter(backFragAdapter);
                        backTab.setViewPager(backViewPager, titles);
                    }
                }
            }
        }, "mainData", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, true, true, false);

    }

    /**
     * 请求相框
     *
     * @param frameViewPager
     * @param frameTab
     * @param fragmentManager
     */
    public void requestPhotoFrameList(ViewPager frameViewPager, SlidingTabLayout frameTab, FragmentManager fragmentManager) {
        List<Fragment> fragments = new ArrayList<>();
        CreationFrameFragment fragment = new CreationFrameFragment();
        fragment.setFrameChooseListener(CreationBottomFragment.this);
        String[] titles = {"相框"};
        fragments.add(fragment);

        home_vp_frg_adapter vpFrgAdapter = new home_vp_frg_adapter(fragmentManager, fragments);

        frameViewPager.setAdapter(vpFrgAdapter);
        frameTab.setViewPager(frameViewPager, titles);
    }


    private void getStickerTypeList(FragmentManager fragmentManager, ViewPager stickerViewPager, SlidingTabLayout stickerTab) {
        HashMap<String, String> params = new HashMap<>();
        // 启动时间
        Observable ob = Api.getDefault().getStickerTypeList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<ArrayList<StickerTypeEntity>>(getContext()) {

            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(ArrayList<StickerTypeEntity> list) {
                List<Fragment> fragments = new ArrayList<>();
                String[] titles = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    titles[i] = list.get(i).getName();
                    Bundle bundle = new Bundle();
                    bundle.putInt("stickerType", list.get(i).getId());
                    bundle.putInt("from", CreationTemplateActivity.FROM_DRESS_UP_BACK_CODE);
                    StickerFragment fragment = new StickerFragment();
                    fragment.setStickerListener(CreationBottomFragment.this);
                    fragment.setArguments(bundle);
                    fragments.add(fragment);
                }

                home_vp_frg_adapter vp_frg_adapter = new home_vp_frg_adapter(fragmentManager, fragments);

                stickerViewPager.setOffscreenPageLimit(list.size() - 1);
                stickerViewPager.setAdapter(vp_frg_adapter);
                stickerTab.setViewPager(stickerViewPager, titles);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }


    public void setBackChooseListener(CreationBackListFragment.BackChooseListener listener) {
        mBackChooseListener = listener;
    }

    public void setFrameChooseListener(CreationFrameFragment.FrameChooseListener listener) {
        mFrameChooseListener = listener;
    }


    @Override
    public void chooseBack(String title, String path) {
        if (mBackChooseListener != null) {
            mBackChooseListener.chooseBack(title, path);
        }
    }

    @Override
    public void chooseFrame(String title, String path) {
        if (mFrameChooseListener != null) {
            mFrameChooseListener.chooseFrame(title, path);
        }
    }

    @Override
    public void addSticker(String stickerPath, String name) {
        mStickerItemOnDragListener.addSticker(stickerPath, name);
    }

    @Override
    public void copyGif(String fileName, String copyName, String title) {
        mStickerItemOnDragListener.copyGif(fileName, copyName, title);
    }

    @Override
    public void clickItemSelected(int position) {
        mStickerItemOnDragListener.clickItemSelected(position);
    }

    private StickerFragment.StickerListener mStickerItemOnDragListener;

    public void setStickerListener(StickerFragment.StickerListener listener) {
        mStickerItemOnDragListener = listener;
    }

    private FinishListener mFinishListener;

    public void setFinishListener(FinishListener listener) {
        mFinishListener = listener;
    }

    public interface FinishListener {
        void onFinishClicked();

        void onClearClicked(int id);
    }
}
