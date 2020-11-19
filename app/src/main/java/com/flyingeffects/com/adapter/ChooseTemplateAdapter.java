package com.flyingeffects.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.TemplateThumbItem;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.manager.GlideRoundTransform;
import com.flyingeffects.com.utils.LogUtil;

import java.util.List;

public class ChooseTemplateAdapter extends BaseItemDraggableAdapter<new_fag_template_item, BaseViewHolder> {

    private Context context;


    public ChooseTemplateAdapter(@LayoutRes int layoutResId, @Nullable List<new_fag_template_item> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }


    @SuppressLint("CheckResult")
    @Override
    protected void convert(BaseViewHolder helper, new_fag_template_item item) {
        ImageView iv_cover = helper.getView(R.id.iv_logo);
        LogUtil.d("OOM","iamge="+item.getImage());
        Glide.with(context).load(item.getImage()).apply(new RequestOptions().placeholder(R.mipmap.placeholder)).into(iv_cover);
        TextView tvTitle= helper.getView(R.id.tv_name);
        tvTitle.setText( item.getTitle());

        FrameLayout fl_frame = helper.getView(R.id.fl_frame);
        if (item.isCheckItem()) {

            fl_frame.setVisibility(View.VISIBLE);
        } else {
            fl_frame.setVisibility(View.GONE);
        }


    }
}