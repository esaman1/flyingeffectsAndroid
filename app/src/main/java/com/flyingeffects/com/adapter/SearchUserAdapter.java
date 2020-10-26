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
import com.flyingeffects.com.enity.SearchUserEntity;
import com.flyingeffects.com.enity.UserInfo;
import com.flyingeffects.com.ui.view.activity.UserHomepageActivity;
import com.orhanobut.hawk.Hawk;

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
        userInfo = Hawk.get("UserInfo");
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchUserEntity item) {
        helper.setText(R.id.tv_user_name, item.getNickname());
        helper.setText(R.id.tv_user_number, "飞友号:" + item.getId());
        TextView tvAttention = helper.getView(R.id.tv_attention);
        //搜索的用户是自己的话 不显示关注按钮
        // 1已关注 0 未关注
        if (TextUtils.equals(String.valueOf(item.getId()), userInfo.getId())) {
            tvAttention.setVisibility(View.INVISIBLE);
        } else {
            tvAttention.setVisibility(View.VISIBLE);
            if (item.getIs_follow() == 0) {
                tvAttention.setSelected(false);
                tvAttention.setText("关注");
            } else {
                tvAttention.setSelected(true);
                tvAttention.setText("已关注");
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
                if (!TextUtils.equals(String.valueOf(item.getId()), userInfo.getId())) {
                    Intent intent = new Intent(activity, UserHomepageActivity.class);
                    intent.putExtra("toUserId", String.valueOf(item.getId()));
                    activity.startActivity(intent);
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
}
