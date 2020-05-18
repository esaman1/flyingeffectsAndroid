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
 * user :TongJu  ;描述：创作页面动画item
 * 时间：2018/5/3
 **/
public class TemplateGridViewAnimAdapter extends BaseAdapter {


    private List<String> list;
    private Context context;
//    private String mGifFolder;

    public TemplateGridViewAnimAdapter(List<String> list, Context context) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_template_anim, parent, false);
            holder.image = view.findViewById(R.id.iv_icon);
            holder.tv_name = view.findViewById(R.id.tv_name);
            view.setTag(holder);
        } else {
            holder = (ViewHold) view.getTag();
        }

        holder.tv_name.setText(list.get(position));
//        StickerList stickerList = list.get(position);
//        if (stickerList.isClearSticker()) {
//            holder.image.setImageResource(R.mipmap.sticker_clear);
//            holder.tv_name.setText("默认");
//        } else {
//            Glide.with(context)
//                    .load(list.get(position).getThumbnailimage())
//                    .apply(RequestOptions.bitmapTransform(new GlideRoundTransform(context, 3)))
//                    .into(holder.image);
//            holder.tv_name.setText(list.get(position).getTitle());
//        }
        return view;
    }


    class ViewHold {
        ImageView image;
        TextView tv_name;
    }


}
