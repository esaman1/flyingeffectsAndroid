package com.mobile.CloudMovie.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mobile.CloudMovie.R;
import com.mobile.CloudMovie.enity.HomeItemEnity;
import com.mobile.CloudMovie.enity.filtrateEnity;
import com.mobile.CloudMovie.utils.LogUtil;
import com.mobile.CloudMovie.view.MyGridview;

import java.util.ArrayList;
import java.util.List;

public class home_filtrate_adapter extends BaseQuickAdapter<filtrateEnity, BaseViewHolder> {

    private Context context;
    public final static String TAG = "RecyclerView2List";


    public home_filtrate_adapter(int layoutResId, @Nullable List<filtrateEnity> data, Context context) {
        super(layoutResId, data);
        this.context = context;

    }

    @Override
    protected void convert(final BaseViewHolder helper, final filtrateEnity item) {
        final int position = helper.getLayoutPosition();
        LogUtil.d("position","position="+position);
        helper.setText(R.id.tv_title, item.getName());
    }






}
