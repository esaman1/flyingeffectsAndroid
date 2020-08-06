package com.flyingeffects.com.ui.view.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.home_vp_frg_adapter2;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.UserInfo;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.view.fragment.fragHomePage;
import com.flyingeffects.com.ui.view.fragment.frag_user_collect;
import com.flyingeffects.com.ui.view.fragment.frag_user_upload_bj;
import com.flyingeffects.com.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import rx.Observable;


/**
 * description ：观看用户主页
 * creation date: 2020/8/5
 * user : zhangtongju
 */
public class UserHomepageActivity extends BaseActivity {


    private String toUserId;

    @BindView(R.id.iv_head)
    ImageView iv_head;
    private ArrayList<TextView> listTv = new ArrayList<>();

    @BindView(R.id.tv_name)
    TextView tv_name;

    @BindView(R.id.fans_count)
    TextView fans_count;

    @BindView(R.id.attention_count)
    TextView attention_count;


    @BindView(R.id.tv_video_count)
    TextView tv_video_count;

//    @BindView(R.id.ll_title)
//    LinearLayout ll_title;

    @BindView(R.id.viewpager)
    ViewPager viewpager;

    private ArrayList<View> listView = new ArrayList<>();

    private String[] str = {"我的作品", "喜欢"};


    @Override
    protected int getLayoutId() {
        return R.layout.act_user_home_page;
    }

    @Override
    protected void initView() {
        toUserId = getIntent().getStringExtra("toUserId");
//        addHead();
    }

    @Override
    protected void initAction() {

    }


//    private void addHead() {
//        for (int i = 0; i < str.length; i++) {
//            View view = LayoutInflater.from(UserHomepageActivity.this).inflate(R.layout.view_home_page_head, null);
//            TextView tv = view.findViewById(R.id.tv_name_bj_head);
//            View view_line = view.findViewById(R.id.view_line_head);
//            tv.setText(str[i]);
//            listTv.add(tv);
//            listView.add(view);
//            ll_title.addView(view);
//        }
//    }


    @Override
    protected void onResume() {
        super.onResume();
        requestUserInfo();
        addViewPager();
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
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(UserInfo data) {
                tv_name.setText(data.getNickname());
                Glide.with(UserHomepageActivity.this)
                        .load(data.getPhotourl())
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(iv_head);
                fans_count.setText(data.getUser_follower());
                attention_count.setText(data.getUser_watch());
                tv_video_count.setText(data.getUser_video());
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    private void addViewPager() {
        FragmentManager manager = getSupportFragmentManager();
        ArrayList<Fragment> list = new ArrayList<>();
        fragHomePage fag_1 = new fragHomePage();
        fragHomePage fag_0 = new fragHomePage();
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("toUserId", toUserId);
        bundle1.putSerializable("isFrom", 0);
        Bundle bundle = new Bundle();
        bundle.putSerializable("toUserId", toUserId);
        bundle.putSerializable("isFrom", 1);
        fag_0.setArguments(bundle);
        fag_1.setArguments(bundle1);
        list.add(fag_0);
        list.add(fag_1);
        home_vp_frg_adapter2 adapter = new home_vp_frg_adapter2(manager, list);
        viewpager.setAdapter(adapter);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        for (int i = 0; i < listTv.size(); i++) {
            TextView tv = listTv.get(i);
            View view = listView.get(i);
            if (i == showWitch) {
                tv.setTextSize(21);
                int width = tv.getWidth();
                view.setVisibility(View.VISIBLE);
//                setViewWidth(view, width);
            } else {
                tv.setTextSize(17);
                view.setVisibility(View.INVISIBLE);
            }
        }
        viewpager.setCurrentItem(showWitch);
    }

}
