package com.flyingeffects.com.adapter;

import android.content.Context;
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
public class TemplateGridViewAdapter extends BaseAdapter {


    private List<String> SearchList;
    private Context context;


    public TemplateGridViewAdapter(List<String> SearchList, Context context) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_template_gridview, parent, false);
            holder.tv_name=view.findViewById(R.id.tv_name);
            view.setTag(holder);
        } else {
            holder = (ViewHold) view.getTag();
        }
        String data = SearchList.get(position);

        holder.tv_name.setText(data);
        return view;
    }




    class ViewHold {
        TextView tv_name;
    }
}
