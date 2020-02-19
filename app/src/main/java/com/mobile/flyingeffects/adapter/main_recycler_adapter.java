package com.mobile.flyingeffects.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mobile.flyingeffects.enity.new_fag_template_item;
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


    public main_recycler_adapter(int layoutResId, @Nullable List<new_fag_template_item> allData, Context context, showOnitemClick callback, boolean hasContribute) {
        super(layoutResId, allData);
        this.context = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final new_fag_template_item item) {
        int offset = helper.getLayoutPosition();
        }

    public interface showOnitemClick {

        void clickItem(int position);

    }


    }









