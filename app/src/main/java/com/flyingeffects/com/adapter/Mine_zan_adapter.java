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
import com.flyingeffects.com.enity.MineZanEnity;

import java.util.List;


/**
 * user :TongJu  ; email:jutongzhang@sina.com
 * time：2020/7/29
 * describe:消息页面
 **/
public class Mine_zan_adapter extends BaseQuickAdapter<MineZanEnity, BaseViewHolder> {

    private Context context;
    int isFrom;
    public final static String TAG = "main_recycler_adapter";

    public Mine_zan_adapter(int layoutResId, @Nullable List<MineZanEnity> data,int isFrom, Context context) {
        super(layoutResId, data);
        this.context = context;
        this.isFrom=isFrom;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final MineZanEnity item) {
//        int offset = helper.getLayoutPosition();

        if(isFrom==1){
            helper.setText(R.id.tv_content,item.getTitle());
        }else{
            helper.setText(R.id.tv_content,item.getComment());
        }

        helper.addOnClickListener(R.id.iv_icon);
        helper.addOnClickListener(R.id.tv_title);

        ImageView iv_head=helper.getView(R.id.iv_icon);
        Glide.with(context)
                .load(item.getPhotourl())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(iv_head);

        helper.setText(R.id.tv_title,item.getNickname());
        ImageView iv_cover=helper.getView(R.id.iv_cover);

        Glide.with(context)
                .load(item.getImage())
                .into(iv_cover);

    }


}









