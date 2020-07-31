package com.flyingeffects.com.adapter;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.MessageEnity;
import com.flyingeffects.com.enity.systemessagelist;

import java.util.List;


/**
 * user :TongJu  ; 预览视频详情评论页面
 * time：2020/7/29
 * describe:消息页面
 **/
public class Comment_message_adapter extends BaseItemDraggableAdapter<MessageEnity, BaseViewHolder> {

    private Context context;
    public final static String TAG = "main_recycler_adapter";

    public Comment_message_adapter(int layoutResId, List<MessageEnity>data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final MessageEnity item) {
//        int offset = helper.getLayoutPosition();

        ImageView iv_comment_head=helper.getView(R.id.iv_comment_head);
        //主层用户头像
        Glide.with(context)
                .load(item.getPhotourl())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(iv_comment_head);

        helper.setText(R.id.tv_user_id,item.getUser_id());





    }


}









