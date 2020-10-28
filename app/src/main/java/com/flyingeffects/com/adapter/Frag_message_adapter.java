package com.flyingeffects.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.systemessagelist;

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
        if (view == null&&context!=null) {
            holder = new ViewHold();
            view = LayoutInflater.from(context).inflate(R.layout.item_system_message, parent, false);
            holder.tv_point = view.findViewById(R.id.tv_point);
            holder.tv_content_1 = view.findViewById(R.id.tv_content);
            view.setTag(holder);
        } else {
            holder = (ViewHold) view.getTag();
        }
        systemessagelist data = allData.get(position);
        holder.tv_content_1.setText(data.getContent());
        if (allData.get(position).getTotal() ==0) {
            holder.tv_point.setVisibility(View.GONE);
        } else {
            holder.tv_point.setVisibility(View.VISIBLE);
            holder.tv_point.setText(allData.get(position).getTotal()+"");
        }
        return view;
    }


    class ViewHold {
        TextView tv_content_1;
        TextView tv_point;
    }
}
