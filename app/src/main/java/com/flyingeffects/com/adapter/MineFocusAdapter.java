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
import com.flyingeffects.com.enity.fansEnity;

import java.util.List;


/**
 * user :TongJu  ; email:jutongzhang@sina.com
 * time：2020/7/29
 * describe:消息页面
 **/
public class MineFocusAdapter extends BaseQuickAdapter<fansEnity, BaseViewHolder> {

    private Context context;
    public final static String TAG = "main_recycler_adapter";

    public MineFocusAdapter(int layoutResId, @Nullable List<fansEnity> allData, Context context) {
        super(layoutResId, allData);
        this.context = context;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final fansEnity item) {
        helper.setText(R.id.nickname,item.getNickname());
        ImageView iv_icon=helper.getView(R.id.iv_icon);
        Glide.with(context)
                .load(item.getPhotourl())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(iv_icon);
    }


}









