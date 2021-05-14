package com.flyingeffects.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.StickerList;
import com.flyingeffects.com.manager.GlideRoundTransform;

import java.util.List;

/**
 * user :TongJu  ;描述：贴纸item
 * 时间：2018/5/3
 **/
public class TemplateGridViewAdapter extends BaseAdapter {


    private List<StickerList> list;
    private Context context;
//    private String mGifFolder;

    public TemplateGridViewAdapter(List<StickerList> list, Context context) {
        this.list = list;
        this.context = context;
//        FileManager fileManager = new FileManager();
//        mGifFolder = fileManager.getFileCachePath(context, "gifFolder");
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
            holder.tv_name = view.findViewById(R.id.tv_name);
            holder.iv_download = view.findViewById(R.id.iv_download);
            holder.tv_checked = view.findViewById(R.id.tv_checked);
            view.setTag(holder);
        } else {
            holder = (ViewHold) view.getTag();
        }

        StickerList stickerList = list.get(position);
        if (stickerList.isClearSticker()) {

            Glide.with(context)
                    .load(R.mipmap.sticker_clear)
                    .into(holder.image);

            holder.tv_name.setText("默认");

        } else {

            Glide.with(context)
                    .load(list.get(position).getThumbnailimage())
                    .apply(RequestOptions.bitmapTransform(new GlideRoundTransform(context, 3)))
                    .into(holder.image);

            holder.tv_name.setText(list.get(position).getTitle());

            if (stickerList.isChecked()) {
                holder.tv_checked.setVisibility(View.VISIBLE);
            } else {
                holder.tv_checked.setVisibility(View.GONE);
            }

        }
        return view;
    }


    static class ViewHold {
        ImageView image;
        TextView tv_name;
        ImageView iv_download;
        TextView tv_checked;
    }


}
