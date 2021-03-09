package com.flyingeffects.com.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.flyingeffects.com.utils.LogUtil;

import java.util.HashMap;

public class TimelineAdapterForCutVideo extends RecyclerView.Adapter<TimelineAdapterForCutVideo.TimelineHolder> {
    private int mWidth;
    private int mHeight;
    private int[] mTimePositions;
    private HashMap<Integer, Bitmap> mData;
    private Uri mUri;

    @NonNull
    @Override
    public TimelineAdapterForCutVideo.TimelineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setBackgroundColor(Color.GRAY);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(mWidth, mHeight));
        return new TimelineAdapterForCutVideo.TimelineHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineAdapterForCutVideo.TimelineHolder holder, int position) {
        //Bitmap bm = mData.get(mTimePositions[position]);
        //ImageView itemView = (ImageView) holder.itemView;
        //if (bm != null) {
        //    itemView.setImageBitmap(bm);
        //} else {
        //    itemView.setImageBitmap(null);
        //}


//        if(position==mTimePositions.length-1){
//            //todo 强力解决最后一帧黑屏
//            position=position-1;
//            LogUtil.d("OOM2","当前已经为了最后一帧");
//        }
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
