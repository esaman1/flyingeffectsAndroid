package com.flyingeffects.com.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.SearchUserEntity;
import com.flyingeffects.com.enity.UserInfo;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.view.activity.LoginActivity;
import com.flyingeffects.com.ui.view.activity.UserHomepageActivity;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author ZhouGang
 * @date 2020/10/20
 * 搜索用户的adapter
 */
public class SearchUserAdapter extends BaseQuickAdapter<SearchUserEntity,BaseViewHolder> {
    UserInfo userInfo;
    Activity activity;

    public SearchUserAdapter(int layoutResId, @Nullable List<SearchUserEntity> data,Activity activity) {
        super(layoutResId, data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchUserEntity item) {
        helper.setText(R.id.tv_user_name, item.getNickname());
        helper.setText(R.id.tv_user_number, "飞友号:" + item.getId());
        TextView tvAttention = helper.getView(R.id.tv_attention);
        //搜索的用户是自己的话 不显示关注按钮
        // 1已关注 0 未关注
        if (userInfo != null && TextUtils.equals(String.valueOf(item.getId()), userInfo.getId())) {
            tvAttention.setVisibility(View.INVISIBLE);
        } else {
            tvAttention.setVisibility(View.VISIBLE);
            if (item.getIs_follow() == 1) {
                tvAttention.setSelected(true);
                tvAttention.setText("已关注");
            } else {
                tvAttention.setSelected(false);
                tvAttention.setText("关注");
            }
        }
        Glide.with(mContext).load(item.getPhotourl()).into((ImageView) helper.getView(R.id.iv_user_avatar));
        tvAttention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onAttentionListener!=null){
                    onAttentionListener.attention(item.getId());
                }
            }
        });
        helper.setOnClickListener(R.id.iv_user_avatar, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BaseConstans.hasLogin()){
                    if (userInfo != null && !TextUtils.equals(String.valueOf(item.getId()), userInfo.getId())) {
                        StatisticsEventAffair.getInstance().setFlag(mContext, "4_search_user_click");
                        Intent intent = new Intent(activity, UserHomepageActivity.class);
                        intent.putExtra("toUserId", String.valueOf(item.getId()));
                        activity.startActivity(intent);
                    }
                }else {
                    activity.startActivity(new Intent(activity,LoginActivity.class));
                }
            }
        });
    }

    public interface OnAttentionListener{
        /***
         * 关注该用户
         * @param id 用户ID
         */
        void attention(int id);
    }

    OnAttentionListener onAttentionListener;

    public void setOnAttentionListener(OnAttentionListener onAttentionListener) {
        this.onAttentionListener = onAttentionListener;
    }

    public void setUserInfo(UserInfo userInfo){
        this.userInfo = userInfo;
    }
}
