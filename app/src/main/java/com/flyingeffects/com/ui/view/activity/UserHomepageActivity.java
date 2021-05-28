package com.flyingeffects.com.ui.view.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.home_vp_frg_adapter2;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.databinding.ActUserHomePageBinding;
import com.flyingeffects.com.entity.AttentionChange;
import com.flyingeffects.com.entity.UserInfo;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.view.fragment.fragHomePage;
import com.flyingeffects.com.utils.CheckVipOrAdUtils;
import com.flyingeffects.com.utils.TimeUtils;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.view.MarqueTextView;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.Observable;


/**
 * description ：观看用户主页
 * creation date: 2020/8/5
 * user : zhangtongju
 */
public class UserHomepageActivity extends BaseActivity {
    private String toUserId;
    //是否已经关注
    private boolean isFocus = false;
    private ActUserHomePageBinding mBinding;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {
        mBinding = ActUserHomePageBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        setContentView(rootView);

        setOnClickListener();

        StatisticsEventAffair.getInstance().setFlag(UserHomepageActivity.this, "12_Homepage");
        toUserId = getIntent().getStringExtra("toUserId");
        if (toUserId.equals(BaseConstans.getUserId())) {
            mBinding.tvFocus.setVisibility(View.GONE);
        }

    }

    private void setOnClickListener() {
        mBinding.tvNameBjHead.setOnClickListener(this::onViewClick);
        mBinding.tvLike.setOnClickListener(this::onViewClick);
        mBinding.ivBack.setOnClickListener(this::onViewClick);
        mBinding.tvFocus.setOnClickListener(this::onViewClick);
    }

    private void onViewClick(View view) {
        if (view == mBinding.tvNameBjHead) {
            mBinding.viewpager.setCurrentItem(0);
        } else if (view == mBinding.tvLike) {
            mBinding.viewpager.setCurrentItem(1);
        } else if (view == mBinding.ivBack) {
            finish();
        } else if (view == mBinding.tvFocus) {
            requestFocus();
        }
    }

    @Override
    protected void initAction() {
        requestUserInfo();
        addViewPager();
    }

    /**
     * description ：请求用户信息
     * creation date: 2020/7/30
     * user : zhangtongju
     */
    private void requestFocus() {
        HashMap<String, String> params = new HashMap<>();
        params.put("to_user_id", toUserId);

        if (isFocus) {
            //取消关注
            StatisticsEventAffair.getInstance().setFlag(UserHomepageActivity.this, "12_unsubscribe");
        } else {
            StatisticsEventAffair.getInstance().setFlag(UserHomepageActivity.this, "12_Attention");
        }

        // 启动时间
        Observable ob = Api.getDefault().followUser(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(this) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(Object data) {
                if (isFocus) {
                    mBinding.tvFocus.setText("关注");
                    isFocus = false;
                } else {
                    mBinding.tvFocus.setText("取消关注");
                    isFocus = true;
                }
                EventBus.getDefault().post(new AttentionChange());
                requestUserInfo();

            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }

    /**
     * description ：请求用户信息
     * creation date: 2020/7/30
     * user : zhangtongju
     */
    private void requestUserInfo() {
        HashMap<String, String> params = new HashMap<>();
        params.put("to_user_id", toUserId);
        // 启动时间
        Observable ob = Api.getDefault().getOtherUserinfo(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<UserInfo>(this) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(UserInfo data) {
                mBinding.tvName.setText(data.getNickname());
                Glide.with(UserHomepageActivity.this)
                        .load(data.getPhotourl())
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(mBinding.ivHead);
                //点赞数
                mBinding.tvZanCount.setText(data.getUser_praise());
                //我关注的数量
                mBinding.tvAttentionCount.setText(data.getUser_watch());
                //关注我的数量
                mBinding.tvFansCount.setText(data.getUser_follower());

                mBinding.tvNumber.setText("飞友号：" + data.getId());
                String isHasFollow = data.getIs_has_follow();
                if (!TextUtils.isEmpty(isHasFollow) && "0".equals(isHasFollow)) {
                    mBinding.tvFocus.setText("关注");
                    isFocus = false;
                } else {
                    mBinding.tvFocus.setText("取消关注");
                    isFocus = true;
                }
                if (TextUtils.isEmpty(data.getSkin())) {
                    Glide.with(UserHomepageActivity.this)
                            .load(R.mipmap.home_page_bj)
                            .into(mBinding.ivUserSkin);
                } else {
                    Glide.with(UserHomepageActivity.this)
                            .load(data.getSkin())
                            .into(mBinding.ivUserSkin);
                }
                if (!TextUtils.isEmpty(data.getRemark())) {
                    mBinding.tvIntroduction.setText(data.getRemark());
                } else {
                    mBinding.tvIntroduction.setText("这位友友很懒，什么也没留下");
                }

                startVipIcon(data.getIs_vip(),data.getVip_grade());
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }

    private void startVipIcon(int isVip, int vipGrade) {
        String vipIconStr = "";
        if (isVip == CheckVipOrAdUtils.IS_VIP) {
            mBinding.tvAvatarVipIcon.setVisibility(View.VISIBLE);
            switch (vipGrade) {
                case CheckVipOrAdUtils.VIP_GRADE_MONTH:
                    vipIconStr = "月";
                    break;
                case CheckVipOrAdUtils.VIP_GRADE_YEAR:
                    vipIconStr = "年";
                    break;
                case CheckVipOrAdUtils.VIP_GRADE_FOREVER:
                    vipIconStr = "永久";
                    break;
                default:
                    vipIconStr = "月";
                    break;
            }
        } else {
            mBinding.tvAvatarVipIcon.setVisibility(View.INVISIBLE);
        }
        mBinding.tvAvatarVipIcon.setText(vipIconStr);
    }


    private void addViewPager() {
        FragmentManager manager = getSupportFragmentManager();
        ArrayList<Fragment> list = new ArrayList<>();
        fragHomePage fag_1 = new fragHomePage();
        fragHomePage fag_0 = new fragHomePage();
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("toUserId", toUserId);
        bundle1.putSerializable("isFrom", 1);
        Bundle bundle = new Bundle();
        bundle.putSerializable("toUserId", toUserId);
        bundle.putSerializable("isFrom", 2);
        fag_0.setArguments(bundle);
        fag_1.setArguments(bundle1);
        list.add(fag_1);
        list.add(fag_0);
        home_vp_frg_adapter2 adapter = new home_vp_frg_adapter2(manager, list);
        mBinding.viewpager.setAdapter(adapter);
        mBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                showWitchBtn(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }


    private void showWitchBtn(int showWitch) {
        if (showWitch == 0) {
            //选中的是我的作品
            mBinding.viewLineHead1.setVisibility(View.INVISIBLE);
            mBinding.viewLineHead.setVisibility(View.VISIBLE);
        } else {
            mBinding.viewLineHead1.setVisibility(View.VISIBLE);
            mBinding.viewLineHead.setVisibility(View.INVISIBLE);
        }
        mBinding.viewpager.setCurrentItem(showWitch);
    }

}
