package com.flyingeffects.com.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.NewFragmentTemplateItem;
import com.flyingeffects.com.manager.GlideRoundTransform;

import java.util.List;

public class CreationBackListAdapter extends BaseQuickAdapter<NewFragmentTemplateItem, BaseViewHolder> {

    public CreationBackListAdapter(@Nullable List<NewFragmentTemplateItem> data) {
        super(R.layout.item_template_gridview, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, NewFragmentTemplateItem item) {
        int postion = helper.getAdapterPosition();
        ImageView image = helper.getView(R.id.iv_icon);
        if (postion == 0) {
            helper.setVisible(R.id.iv_local, true);
            image.setVisibility(View.INVISIBLE);
        } else {
            helper.setVisible(R.id.iv_local, false);
            image.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(item.getBackground_image())
                    .apply(RequestOptions.bitmapTransform(new GlideRoundTransform(mContext, 3)))
                    .into(image);
        }

        helper.setText(R.id.tv_name, item.getTitle());
        helper.setVisible(R.id.tv_checked, item.isChecked());
    }
}
