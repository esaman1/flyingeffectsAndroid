package com.flyingeffects.com.adapter;

import android.content.Context;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.entity.systemessagelist;

import java.util.List;


/**
 * user :TongJu  ; email:jutongzhang@sina.com
 * time：2020/7/29
 * describe:消息页面
 **/
public class System_message_adapter extends BaseItemDraggableAdapter<systemessagelist, BaseViewHolder> {

    private Context context;
    public final static String TAG = "MainRecyclerAdapter";

    public System_message_adapter(int layoutResId, @Nullable List<systemessagelist> allData, Context context) {
        super(layoutResId, allData);
        this.context = context;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final systemessagelist item) {
//        int offset = helper.getLayoutPosition();
        helper.setText(R.id.tv_content,item.getContent());
    }


}









