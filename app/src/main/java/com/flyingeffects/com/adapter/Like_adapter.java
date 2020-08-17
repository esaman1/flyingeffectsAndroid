package com.flyingeffects.com.adapter;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.MineCommentEnity;

import java.util.List;


/**
 * user :TongJu  ; email:jutongzhang@sina.com
 * time：2020/7/29
 * describe:消息页面
 **/
public class Like_adapter extends BaseQuickAdapter<MineCommentEnity, BaseViewHolder> {

    private Context context;
    public final static String TAG = "main_recycler_adapter";

    public Like_adapter(int layoutResId, @Nullable List<MineCommentEnity> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final MineCommentEnity item) {
//        int offset = helper.getLayoutPosition();
        helper.setText(R.id.tv_content,item.getContent());
        ImageView iv_head=helper.getView(R.id.iv_icon);
        Glide.with(context)
                .load(item.getPhotourl())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(iv_head);

        helper.addOnClickListener(R.id.iv_icon);

        helper.setText(R.id.tv_title,item.getNickname());
        ImageView iv_cover=helper.getView(R.id.iv_cover);

        Glide.with(context)
                .load(item.getImage())
                .into(iv_cover);

    }


}









