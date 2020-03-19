package com.flyingeffects.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.StickerList;

import java.util.List;

/**
 * user :TongJu  ;描述：社区item
 * 时间：2018/5/3
 **/
public class TemplateGridViewAdapter extends BaseAdapter {


    private List<StickerList> list;
    private Context context;


    public TemplateGridViewAdapter(List<StickerList> list, Context context) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_template_gridview, parent, false);
            holder.image = view.findViewById(R.id.iv_icon);
            view.setTag(holder);
        } else {
            holder = (ViewHold) view.getTag();
        }
        Glide.with(context).load(list.get(position).getImage()).into(holder.image);
        return view;
    }


    class ViewHold {
        ImageView image;
    }
}
