package com.mobile.CloudMovie.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mobile.CloudMovie.R;
import com.mobile.CloudMovie.enity.selectEnity;
import com.mobile.CloudMovie.enity.starEnity;
import com.mobile.CloudMovie.utils.LogUtil;

import java.util.List;

public class select_adapter extends BaseQuickAdapter<selectEnity, BaseViewHolder> {

    private Context context;
    public final static String TAG = "RecyclerView2List";


    public select_adapter(int layoutResId, @Nullable List<selectEnity> data, Context context) {
        super(layoutResId, data);
        this.context = context;

    }

    @Override
    protected void convert(final BaseViewHolder helper, final selectEnity item) {
        final int position = helper.getLayoutPosition();
        LogUtil.d("position","position="+position);
        helper.setText(R.id.tv_title, item.getName());
    }






}
