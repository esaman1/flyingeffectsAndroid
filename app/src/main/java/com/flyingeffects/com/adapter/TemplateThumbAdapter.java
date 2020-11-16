package com.flyingeffects.com.adapter;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.commonlyModel.GetPathType;
import com.flyingeffects.com.enity.TemplateThumbItem;
import com.flyingeffects.com.manager.GlideRoundTransform;
import com.flyingeffects.com.utils.LogUtil;
import com.shixing.sxve.ui.albumType;

import org.w3c.dom.Text;

import java.util.List;

public class TemplateThumbAdapter extends BaseItemDraggableAdapter<TemplateThumbItem, BaseViewHolder> {

    private Context context;


    public TemplateThumbAdapter(@LayoutRes int layoutResId, @Nullable List<TemplateThumbItem> data, Context context) {
        super(layoutResId, data);
        this.context = context;
    }


    @Override
    protected void convert(BaseViewHolder helper, TemplateThumbItem item) {
        helper.addOnClickListener(R.id.iv_show_un_select);
        TextView tv_compile=helper.getView(R.id.tv_compile);
        int position = helper.getLayoutPosition();
        ImageView iv_show_un_select = helper.getView(R.id.iv_show_un_select);
        if (item.getPathUrl() != null && !item.getPathUrl().equals("")) {
            Glide.with(context)
                    .load(item.getPathUrl())
                    .apply(RequestOptions.bitmapTransform(new GlideRoundTransform(context, 5)))
                    .into((ImageView) helper.getView(R.id.iv_show_un_select));
        } else {
            iv_show_un_select.setImageResource(R.mipmap.logo);
        }
        if(item.getIsCheck()==0){
            tv_compile.setVisibility(View.VISIBLE);
        }else{
            tv_compile.setVisibility(View.GONE);
        }
        if (item.getPathUrl() != null && !item.getPathUrl().equals("")) {
            if (albumType.isImage(GetPathType.getInstance().getPathType(item.getPathUrl()))) {
                tv_compile.setText("图片");
            }else{
                tv_compile.setText("编辑");
            }
        }
        helper.setText(R.id.tv_num, position + 1 + "");

    }
}