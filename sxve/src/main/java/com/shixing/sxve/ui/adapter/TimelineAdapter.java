package com.shixing.sxve.ui.adapter;

import android.graphics.Color;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineHolder> {
    private int mWidth;
    private int mHeight;
    private int[] mTimePositions;
    private Uri mUri;
    private int marginRight;

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




        RequestOptions options = RequestOptions.frameOf(mTimePositions[position]);
        Log.d("options","mTimePositions[position]="+mTimePositions[position]);
        RequestOptions cacheOptions = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE);
        Glide.with(holder.itemView.getContext())
                .load(mUri)
                .apply(options)
                .apply(cacheOptions)
                .into(((ImageView) holder.itemView));

        if(position==mTimePositions.length-1){
            test(holder.itemView);
        }


    }

    private void test(View iv){
        Log.d("OOM","test");
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,marginRight,0);//4个参数按顺序分别是左上右下
        iv.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return mTimePositions == null ? 0 : mTimePositions.length;
    }

    public void setData(int[] timePositions) {
        mTimePositions = timePositions;
        notifyDataSetChanged();
    }

    public void setBitmapSize(int width, int height) {
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


    public void marginRight(int marginRight){
        this.marginRight=marginRight;
    }
}
