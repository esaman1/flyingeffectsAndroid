package com.shixing.sxve.ui.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineHolder> {
    private int mWidth;
    private int mHeight;
    private int[] mTimePositions;
    private HashMap<Integer, Bitmap> mData;
    private Uri mUri;

    @NonNull
    @Override
    public TimelineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setBackgroundColor(Color.GRAY);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(mWidth, mHeight));
        return new TimelineHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineHolder holder, int position) {
        //Bitmap bm = mData.get(mTimePositions[position]);
        //ImageView itemView = (ImageView) holder.itemView;
        //if (bm != null) {
        //    itemView.setImageBitmap(bm);
        //} else {
        //    itemView.setImageBitmap(null);
        //}

        RequestOptions options = RequestOptions.frameOf(mTimePositions[position]); //截取视频指定的屏幕 ，微妙
        RequestOptions cacheOptions = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE);
        Glide.with(holder.itemView.getContext())
                .load(mUri)
                .apply(options)
                .apply(cacheOptions)
                .into(((ImageView) holder.itemView));

//        Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(mTimePositions[position] * 1000, MediaMetadataRetriever.OPTION_CLOSEST);

    }

    @Override
    public int getItemCount() {
        return mTimePositions == null ? 0 : mTimePositions.length;
    }

    public void setData(int[] timePositions, HashMap<Integer, Bitmap> data) {
        mTimePositions = timePositions;
        this.mData = data;
        notifyDataSetChanged();
    }

    public void setBitmapSize(int width, int height) { //计算得到图片的大小
        mWidth = width;
        mHeight = height;
    }

    public void setVideoUri(Uri uri) {
        mUri = uri;
    }

    class TimelineHolder extends RecyclerView.ViewHolder {
        public TimelineHolder(View itemView) {
            super(itemView);
        }
    }
}
