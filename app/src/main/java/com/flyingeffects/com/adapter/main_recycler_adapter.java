package com.flyingeffects.com.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
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
public class main_recycler_adapter extends BaseQuickAdapter<new_fag_template_item, BaseViewHolder> {

    private Context context;
    public final static String TAG = "main_recycler_adapter";

    public main_recycler_adapter(int layoutResId, @Nullable List<new_fag_template_item> allData, Context context, showOnitemClick callback, int adType) {
        super(layoutResId, allData);
        this.context = context;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final new_fag_template_item item) {
        int offset = helper.getLayoutPosition();
        helper.setText(R.id.tv_name,item.getTitle());
        Glide.with(context)
                .load(item.getImage())
                .apply(RequestOptions.bitmapTransform(new GlideRoundTransform(context, 5)))
//                .apply(RequestOptions.placeholderOf(getDrawble(offset)))
                .into((ImageView) helper.getView(R.id.iv_cover));
    }

    public interface showOnitemClick {

        void clickItem(int position);

    }


}









