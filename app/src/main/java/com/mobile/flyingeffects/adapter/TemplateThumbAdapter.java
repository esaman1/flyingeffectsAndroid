package com.mobile.flyingeffects.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mobile.flyingeffects.R;
import com.mobile.flyingeffects.enity.TemplateThumbItem;
import com.mobile.flyingeffects.utils.LogUtil;
import com.mobile.flyingeffects.view.AutoScaleWidthImageView;
import com.shixing.sxve.ui.model.TemplateModel;
import com.shixing.sxve.ui.view.GroupThumbView;

import java.util.List;

public class TemplateThumbAdapter extends BaseItemDraggableAdapter<TemplateThumbItem, BaseViewHolder> {

    private TemplateModel mTemplateModel;
    private int mSelectedItem;
    private Context context;


    public TemplateThumbAdapter(@LayoutRes int layoutResId, @Nullable List<TemplateThumbItem> data, Context context) {
        super(layoutResId, data);
        this.context = context;

    }


    @Override
    protected void convert(BaseViewHolder helper, TemplateThumbItem item) {
        LogUtil.d("convert", "path=" + item.getPathUrl());
        boolean isAuto = item.isAuto();
        RelativeLayout rl_parent = helper.getView(R.id.rela_parent);
       int position = helper.getLayoutPosition();
        GroupThumbView groupThumbView = helper.getView(R.id.GroupThumbView);
        ImageView iv_select = helper.getView(R.id.iv_select);
        AutoScaleWidthImageView iv_show_un_select = helper.getView(R.id.iv_show_un_select);
        if (mTemplateModel != null) {
            groupThumbView.setAssetGroup(mTemplateModel.groups.get(position + 1));
            groupThumbView.setSelected(position == mSelectedItem);
        }
        if (item.getPathUrl() != null && !item.getPathUrl().equals("")) {
            Glide.with(context).load(item.getPathUrl()).into(iv_show_un_select);
        } else {
            iv_show_un_select.setImageResource(R.mipmap.ic_launcher);
        }
        helper.setText(R.id.tv_num, position + 1 + "");
        LogUtil.d("getIsCheck", "getIsCheck=" + item.getIsCheck());
        if (item.getIsCheck() == 0) {  //选中状态
            iv_select.setVisibility(View.VISIBLE);
            if (isAuto) { //主动
                groupThumbView.setVisibility(View.GONE);
                iv_show_un_select.setVisibility(View.VISIBLE);
            } else {
                groupThumbView.setVisibility(View.VISIBLE);
                iv_show_un_select.setVisibility(View.GONE);
            }
        } else {
            iv_select.setVisibility(View.GONE);
            groupThumbView.setVisibility(View.GONE);
            iv_show_un_select.setVisibility(View.VISIBLE);
        }
        if (item.getBgPath() != null) {
            rl_parent.setBackground(item.getBgPath());
        }
    }

    public void setTemplateModel(TemplateModel templateModel) {
        mTemplateModel = templateModel;
    }


}