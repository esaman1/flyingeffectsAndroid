package com.mobile.CloudMovie.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mobile.CloudMovie.R;
import com.mobile.CloudMovie.enity.starEnity;
import com.mobile.CloudMovie.utils.LogUtil;

import java.util.List;

public class myOrder_adapter extends BaseQuickAdapter<starEnity, BaseViewHolder> {

    private Context context;
    public final static String TAG = "RecyclerView2List";


    public myOrder_adapter(int layoutResId, @Nullable List<starEnity> data, Context context) {
        super(layoutResId, data);
        this.context = context;

    }

    @Override
    protected void convert(final BaseViewHolder helper, final starEnity item) {
        final int position = helper.getLayoutPosition();
        LogUtil.d("position","position="+position);
        helper.setText(R.id.tv_title, item.getName());
    }






}
