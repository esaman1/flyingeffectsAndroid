package com.flyingeffects.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.StickerAnim;

import java.util.List;

/**
 * user :TongJu  ;描述：创作页面动画item
 * 时间：2018/5/3
 **/
public class TemplateGridViewAnimAdapter extends BaseAdapter {


    private List<StickerAnim> list;
    private Context context;

    public TemplateGridViewAnimAdapter(List<StickerAnim> list, Context context) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_template_anim, parent, false);
            holder.image = view.findViewById(R.id.iv_icon);
            holder.tv_name = view.findViewById(R.id.tv_name);
            holder.tv_checked=view.findViewById(R.id.tv_checked);
            view.setTag(holder);
        } else {
            holder = (ViewHold) view.getTag();
        }

        if(position==0){
            Glide.with(context).load(R.mipmap.sticker_clear).into( holder.image);
        }else{
            Glide.with(context).load(list.get(position).getIcon()).into( holder.image);
        }
        holder.tv_name.setText(list.get(position).getName());

        if (list.get(position).isChecked() ) {
            holder.tv_checked.setVisibility(View.VISIBLE);
        } else {
            holder.tv_checked.setVisibility(View.GONE);
        }

        return view;
    }


    class ViewHold {
        ImageView image;
        TextView tv_name;
        TextView tv_checked;
    }


}
