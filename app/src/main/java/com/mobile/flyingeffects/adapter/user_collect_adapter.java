package com.mobile.flyingeffects.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mobile.flyingeffects.R;
import com.mobile.flyingeffects.enity.user_collect;
import com.mobile.flyingeffects.utils.LogUtil;

import java.util.List;

public class user_collect_adapter extends BaseQuickAdapter<user_collect, BaseViewHolder> {

    private Context context;
    public final static String TAG = "RecyclerView2List";


    public user_collect_adapter(int layoutResId, @Nullable List<user_collect> data, Context context) {
        super(layoutResId, data);
        this.context = context;

    }

    @Override
    protected void convert(final BaseViewHolder helper, final user_collect item) {
        final int position = helper.getLayoutPosition();
        LogUtil.d("position","position="+position);
        helper.setText(R.id.tv_title, item.getName());
    }






}
