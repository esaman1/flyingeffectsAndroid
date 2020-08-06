package com.flyingeffects.com.adapter;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.MyProduction;
import com.flyingeffects.com.manager.GlideRoundTransform;

import java.util.List;


/**
 * user :TongJu  ; email:jutongzhang@sina.com
 * time：2019/1/25
 * describe:首页适配
 **/
public class frag_home_page_adapter extends BaseQuickAdapter<MyProduction, BaseViewHolder> {

    private Context context;
    public final static String TAG = "frag_home_page_adapter";


    public frag_home_page_adapter(int layoutResId, @Nullable List<MyProduction> allData, Context context) {
        super(layoutResId, allData);
        this.context = context;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final MyProduction item) {
        Glide.with(context)
                .load(item.getImage())
                .apply(RequestOptions.bitmapTransform(new GlideRoundTransform(context, 5)))
                .apply(RequestOptions.placeholderOf(R.mipmap.placeholder))
                .into((ImageView) helper.getView(R.id.iv_cover));


        helper.setText(R.id.tv_home_title,item.getTitle());
        helper.setText(R.id.tv_watch_count,item.getPreview()+"人观看");
    }


}









