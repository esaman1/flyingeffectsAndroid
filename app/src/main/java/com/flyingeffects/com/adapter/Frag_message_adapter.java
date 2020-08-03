package com.flyingeffects.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.MessageReply;
import com.flyingeffects.com.enity.systemessagelist;

import java.util.ArrayList;
import java.util.List;

/**
 * user :TongJu  ;描述：评论的查看更多
 * 时间：2018/5/3
 **/
public class Frag_message_adapter extends BaseAdapter {


    private List<systemessagelist> allData;
    private Context context;


    public Frag_message_adapter(List<systemessagelist> allData, Context context) {
        this.allData = allData;
        this.context = context;
    }

    @Override
    public int getCount() {
        return allData.size();
    }

    @Override
    public Object getItem(int i) {
        return allData.get(i);
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
            view = LayoutInflater.from(context).inflate(R.layout.item_system_message, parent, false);
            holder.tv_content_1 = view.findViewById(R.id.tv_content);
            view.setTag(holder);
        } else {
            holder = (ViewHold) view.getTag();
        }
        systemessagelist data = allData.get(position);
        holder.tv_content_1.setText(data.getContent());


        return view;
    }




    class ViewHold {
        TextView tv_content_1;
    }
}
