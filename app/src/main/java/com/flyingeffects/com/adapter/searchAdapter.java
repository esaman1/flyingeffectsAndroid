package com.flyingeffects.com.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.hotSearch;

import java.util.List;

/**
 * user :TongJu  ;描述：社区item
 * 时间：2018/5/3
 **/
public class searchAdapter extends BaseAdapter {


    private List<hotSearch> SearchList;
    private Context context;


    public searchAdapter(List<hotSearch> SearchList, Context context) {
        this.SearchList = SearchList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return SearchList.size();
    }

    @Override
    public Object getItem(int i) {
        return SearchList.get(i);
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
            view = LayoutInflater.from(context).inflate(R.layout.list_item_search, parent, false);
            holder.tv_rank = view.findViewById(R.id.tv_rank);
            holder.tv_text = view.findViewById(R.id.tv_text);
            holder.tv_tag=view.findViewById(R.id.tv_tag);
            view.setTag(holder);
        } else {
            holder = (ViewHold) view.getTag();
        }
        hotSearch data = SearchList.get(position);

        if(position==0||position==1){
            holder.tv_rank.setTextColor(Color.parseColor("#FF6666"));
        }else if(position==2){
            holder.tv_rank.setTextColor(Color.parseColor("#FF6666"));
        }else{
            holder.tv_rank.setTextColor(Color.parseColor("#BBBBBB"));
        }
        holder.tv_rank.setText(data.getSort());
        holder.tv_text.setText(data.getTitle());
        return view;
    }




    class ViewHold {
        TextView tv_rank;
        TextView tv_tag;
        TextView tv_text;
    }
}
