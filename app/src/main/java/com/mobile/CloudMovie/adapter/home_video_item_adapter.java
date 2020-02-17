package com.mobile.CloudMovie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mobile.CloudMovie.R;

import java.util.List;

/**
 * user :TongJu  ;描述：首頁的熱門科室列表
 * 时间：2018/6/27
 **/

public class home_video_item_adapter extends BaseAdapter {


    private List<String> listData;
    private Context context;

    public home_video_item_adapter(Context context, List<String> listData) {
        this.listData = listData;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int i) {
        return listData.get(i);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHold holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.home_item_video_cover, parent, false);
            holder = new ViewHold();
            view.setTag(holder);
        } else {
            holder = (ViewHold) view.getTag();
        }
//        holder.tv_name.setText(listData.get(position).getName());
//        Glide.with(context).load(listData.get(position).getPicture().getOrigin()).apply(new RequestOptions().placeholder(R.mipmap.loading_circle)).into(holder.iv_icon);

        return view;
    }

    class ViewHold {
//        TextView tv_name;
//        XCRoundImageView iv_icon;
    }
}
