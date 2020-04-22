package com.flyingeffects.com.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.StickerList;
import com.flyingeffects.com.manager.GlideRoundTransform;
import com.flyingeffects.com.utils.LogUtil;

import java.util.List;

/**
 * user :TongJu  ;描述：社区item
 * 时间：2018/5/3
 **/
public class listViewForVideoThumbAdapter extends BaseAdapter {

    private int[] mTimePositions;
    private Context context;
    private int marginRight;
    private Uri mUri;
    private int mWidth;
    private int mHeight;
    private int marginLeft;

    public listViewForVideoThumbAdapter(Context context, int[] mTimePositions, Uri mUri, int thumbWidth, int listHeight, int marginRight,int marginLeft) {
        this.context = context;
        this.mTimePositions = mTimePositions;
        this.mUri = mUri;
        this.mWidth = thumbWidth;
        this.mHeight = listHeight;
        this.marginRight = marginRight;
        this.marginLeft=marginLeft;
    }

    @Override
    public int getCount() {
        return mTimePositions.length;
    }

    @Override
    public Object getItem(int i) {
        return mTimePositions[i];
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
            view = LayoutInflater.from(context).inflate(R.layout.view_video_thumb, parent, false);
            holder.image = view.findViewById(R.id.iv_show_cover);
            holder.image.setLayoutParams(new LinearLayout.LayoutParams(mWidth, mHeight));
            holder.view_left=view.findViewById(R.id.view_left);
            holder.view_right=view.findViewById(R.id.view_right);

            view.setTag(holder);
        } else {
            holder = (ViewHold) view.getTag();
        }
        RequestOptions options = RequestOptions.frameOf(mTimePositions[position]);
        RequestOptions cacheOptions = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE);

        if(mUri!=null){
            Glide.with(holder.image.getContext())
                    .load(mUri)
                    .apply(options)
                    .apply(cacheOptions)
                    .into(holder.image);
        }else{
            Glide.with(holder.image.getContext())
                    .load(R.mipmap.green)
                    .apply(options)
                    .apply(cacheOptions)
                    .into(holder.image);
        }

        if (position == mTimePositions.length - 1) {
            holder.view_right.setLayoutParams(new LinearLayout.LayoutParams(marginRight, 0));
        } else  if(position==0){
            holder.view_right.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
            holder.view_left.setLayoutParams(new LinearLayout.LayoutParams(marginLeft, 0));
        }else {
            holder.view_right.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
            holder.view_left.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        }
        return view;
    }





    class ViewHold {
        ImageView image;
        View view_left;
        View view_right;

    }


}
