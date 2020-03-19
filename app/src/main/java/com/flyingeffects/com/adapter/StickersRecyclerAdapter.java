package com.flyingeffects.com.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.manager.GlideRoundTransform;

import java.util.List;


/**
 * user :TongJu  ; email:jutongzhang@sina.com
 * time：2019/1/25
 * describe:首页适配
 **/
public class StickersRecyclerAdapter extends BaseQuickAdapter<Integer, BaseViewHolder> {

    public final static String TAG = "main_recycler_adapter";
    private  int mWidth;
    private int mHeight;
    private Context context;
    private Uri mUri;

    public StickersRecyclerAdapter(int layoutResId, @Nullable List<Integer> allData,Context context) {
        super(layoutResId, allData);
        this.context=context;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final Integer item) {
        ImageView iv=helper.getView(R.id.iv_show_cover);
        iv.setLayoutParams(new LinearLayout.LayoutParams(mWidth, mHeight));
        RequestOptions options = RequestOptions.frameOf(item);
        Glide.with(context)
                .load(mUri)
                .apply(options)
                .into(iv);
    }


    public void setBitmapSize(int mWidth,int mHeight){
        this.mWidth=mWidth;
        this.mHeight=mHeight;
    }


    public void setVideoUri(Uri uri) {
        mUri = uri;
    }


}









