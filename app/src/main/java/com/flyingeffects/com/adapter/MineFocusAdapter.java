package com.flyingeffects.com.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.fansEnity;
import com.flyingeffects.com.utils.TimeUtils;

import java.util.List;

import androidx.annotation.Nullable;


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
        helper.setText(R.id.tv_time, TimeUtils.getNewChatTime(item.getUpdate_time() == null || TextUtils.isEmpty(item.getUpdate_time()) ?
                item.getCreate_time() : Long.parseLong(item.getUpdate_time())));
        ImageView iv_icon=helper.getView(R.id.iv_icon);
        Glide.with(context)
                .load(item.getPhotourl())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(iv_icon);
    }


}









