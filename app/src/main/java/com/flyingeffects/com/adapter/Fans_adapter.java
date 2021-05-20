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
import com.flyingeffects.com.entity.fansEnity;
import com.flyingeffects.com.utils.TimeUtils;

import java.util.List;


/**
 * user :TongJu  ; email:jutongzhang@sina.com
 * time：2020/7/29
 * describe:消息页面
 **/
public class Fans_adapter extends BaseQuickAdapter<fansEnity, BaseViewHolder> {

    private Context context;
    public final static String TAG = "MainRecyclerAdapter";

    public Fans_adapter(int layoutResId, @Nullable List<fansEnity> allData, Context context) {
        super(layoutResId, allData);
        this.context = context;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final fansEnity item) {
        helper.setText(R.id.nickname,item.getNickname());
        helper.setText(R.id.tv_time, TimeUtils.getNewChatTime(item.getCreate_time()));
        ImageView iv_icon=helper.getView(R.id.iv_icon);
        helper.addOnClickListener(R.id.tv_follow);
        Glide.with(context)
                .load(item.getPhotourl())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(iv_icon);

        if(item.getIs_has_follow()==0){
            helper.setText(R.id.tv_follow,"关注");
        }else{
            helper.setText(R.id.tv_follow,"取关");
        }


//        helper.setText(R.id.tv_content,TimeUtils.xxx(item.getCreate_time()));


    }


}









