package com.flyingeffects.com.adapter;

import android.content.Context;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.systemessagelist;

import java.util.List;


/**
 * user :TongJu  ; email:jutongzhang@sina.com
 * time：2020/7/29
 * describe:消息页面
 **/
public class Fans_adapter extends BaseQuickAdapter<systemessagelist, BaseViewHolder> {

    private Context context;
    public final static String TAG = "main_recycler_adapter";

    public Fans_adapter(int layoutResId, @Nullable List<systemessagelist> allData, Context context) {
        super(layoutResId, allData);
        this.context = context;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final systemessagelist item) {
//        int offset = helper.getLayoutPosition();
        helper.setText(R.id.tv_content,item.getContent());
    }


}









