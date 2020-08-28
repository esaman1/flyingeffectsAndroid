package com.flyingeffects.com.adapter;

import android.content.Context;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.BlogFile.Video;

import java.util.List;


/**
 * user :TongJu  ; email:jutongzhang@sina.com
 * time：2019/1/25
 * describe:首页适配
 **/
public class music_local_adapter extends BaseQuickAdapter<Video, BaseViewHolder> {

    private Context context;
    public final static String TAG = "music_recent_adapter";

    public music_local_adapter(int layoutResId, @Nullable List<Video> listVideoFiltrateMp4, Context context) {
        super(layoutResId, listVideoFiltrateMp4);
        this.context = context;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final Video item) {
        int offset = helper.getLayoutPosition();
        helper.setText(R.id.tv_time,item.getName());


    }
}









