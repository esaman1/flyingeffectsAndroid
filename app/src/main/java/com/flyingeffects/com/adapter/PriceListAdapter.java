package com.flyingeffects.com.adapter;

import android.graphics.Paint;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.entity.PriceListEntity;
import com.flyingeffects.com.entity.PrivilegeEntity;

import java.util.List;

public class PriceListAdapter extends BaseQuickAdapter<PriceListEntity, BaseViewHolder> {

    public PriceListAdapter(@Nullable @org.jetbrains.annotations.Nullable List<PriceListEntity> data) {
        super(R.layout.item_vip_price_list, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PriceListEntity item) {
        helper.setText(R.id.tv_price_name, item.getName())
                .setText(R.id.tv_price, item.getPrice())
                .setText(R.id.tv_old_price, item.getOld_price());

        if ("0".equals(item.getInfo())) {
            helper.setVisible(R.id.tv_left_top, false);
        } else {
            helper.setVisible(R.id.tv_left_top, true)
                    .setText(R.id.tv_left_top, item.getInfo());
        }

        if (item.isChecked()) {
            helper.setBackgroundRes(R.id.v_vip_cost_back, R.drawable.shape_price_list_item_checked)
                    .setTextColor(R.id.tv_price_name,
                            ContextCompat.getColor(mContext, R.color.color_vip_price_list_text_checked))
                    .setTextColor(R.id.tv_price,
                            ContextCompat.getColor(mContext, R.color.color_vip_price_list_price_checked))
                    .setTextColor(R.id.tv_old_price,
                            ContextCompat.getColor(mContext, R.color.color_vip_price_list_price_checked));

        } else {
            helper.setBackgroundRes(R.id.v_vip_cost_back, R.drawable.shape_price_list_item_unchecked)
                    .setTextColor(R.id.tv_price_name,
                            ContextCompat.getColor(mContext, R.color.white))
                    .setTextColor(R.id.tv_price,
                            ContextCompat.getColor(mContext, R.color.color_vip_price_list_price_unchecked))
                    .setTextColor(R.id.tv_old_price,
                            ContextCompat.getColor(mContext, R.color.white));
        }
        TextView tvOldPrice = helper.getView(R.id.tv_old_price);
        //中划线
        tvOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        //去掉锯齿
        tvOldPrice.getPaint().setAntiAlias(true);

    }
}
