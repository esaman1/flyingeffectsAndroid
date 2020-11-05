package com.flyingeffects.com.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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


    @Override
    protected void convert(BaseViewHolder helper, new_fag_template_item item) {
        ImageView iv_cover=helper.getView(R.id.iv_logo);
        Glide.with(context).load(item.getImage()).load(iv_cover);
    }
}