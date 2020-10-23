package com.flyingeffects.com.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.SearchUserEntity;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author ZhouGang
 * @date 2020/10/20
 * 搜索用户的adapter
 */
public class SearchUserAdapter extends BaseQuickAdapter<SearchUserEntity,BaseViewHolder> {
    public SearchUserAdapter(int layoutResId, @Nullable List<SearchUserEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchUserEntity item) {
        helper.setText(R.id.tv_user_name, item.getNickname());
        helper.setText(R.id.tv_user_number, "飞友号:" + item.getId());
        TextView tvAttention = helper.getView(R.id.tv_attention);
        // 1已关注 0 未关注
        if (item.getIs_follow() == 0) {
            tvAttention.setSelected(false);
            tvAttention.setText("关注");
        } else {
            tvAttention.setSelected(true);
            tvAttention.setText("已关注");
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
    }

    public interface OnAttentionListener{
        void attention(int id);
    }

    OnAttentionListener onAttentionListener;

    public void setOnAttentionListener(OnAttentionListener onAttentionListener) {
        this.onAttentionListener = onAttentionListener;
    }
}
