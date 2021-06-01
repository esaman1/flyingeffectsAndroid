package com.flyingeffects.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.FontEnity;

import java.util.ArrayList;
import java.util.List;

/**
 * user :TongJu  ;描述：贴纸item
 * 时间：2018/5/3
 **/
public class JadeTypeFaceAdapter extends BaseAdapter {

    int lastSelectId = 0;
    private List<FontEnity> list;
    private Context context;

    public JadeTypeFaceAdapter(List<FontEnity>list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHold holder;
        if (view == null) {
            holder = new ViewHold();
            view = LayoutInflater.from(context).inflate(R.layout.item_create_template_text_effect, parent, false);
            holder.iv_logo = view.findViewById(R.id.iv_logo);
            holder.tv_name = view.findViewById(R.id.tv_name);
            holder.tv_checked = view.findViewById(R.id.tv_checked);
            holder.flFrame = view.findViewById(R.id.fl_frame);
            view.setTag(holder);
        } else {
            holder = (ViewHold) view.getTag();
        }
        FontEnity fontEnity = list.get(position);
        holder.tv_name.setText(fontEnity.getTitle());
        Glide.with(context).load(fontEnity.getIcon_image()).apply(new RequestOptions().placeholder(R.mipmap.placeholder)).into(holder.iv_logo);
        if (list.get(position).isSelected()) {
            holder.flFrame.setVisibility(View.VISIBLE);
        } else {
            holder.flFrame.setVisibility(View.GONE);
        }
        return view;
    }

    public void select(int i) {
        if (i < list.size() && i >= 0) {
            if (lastSelectId < list.size() && lastSelectId >= 0) {
                list.get(lastSelectId).setSelected(false);
            }
            list.get(i).setSelected(true);
            notifyDataSetChanged();
            lastSelectId = i;
        }
    }

    class ViewHold {
        ImageView iv_logo;
        TextView tv_name;
        TextView tv_checked;
        FrameLayout flFrame;
    }


}
