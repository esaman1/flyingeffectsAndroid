package com.flyingeffects.com.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.flyingeffects.com.R;
import com.flyingeffects.com.view.beans.VideoTrimmerFrameBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VideoTimelineAdapter extends RecyclerView.Adapter<VideoTimelineAdapter.EditViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<VideoTrimmerFrameBean> mFrameList = null;
    private int itemWidth = 0;

    public VideoTimelineAdapter(Context context, Uri uri, VideoFrameListener listener) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.mVideoUri = uri;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EditViewHolder(inflater.inflate(R.layout.adapter_video_trimmer, parent, false));
    }

    @Override
    public void onBindViewHolder(VideoTimelineAdapter.EditViewHolder holder, int position) {
        ImageView imageView = holder.img;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        params.width = mFrameList.get(position).getFrameWidth();
        imageView.setLayoutParams(params);

        Glide.with(context)
                .load(TextUtils.isEmpty(mFrameList.get(position).getFramePath()) ? R.mipmap.icon_img_default : mFrameList.get(position).getFramePath())
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE).skipMemoryCache(true).centerCrop().placeholder(R.mipmap.icon_img_default).error(R.mipmap.icon_img_default))
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return mFrameList != null ? mFrameList.size() : 0;
    }

    public int getItemWidth() {
        return itemWidth;
    }


    public final class EditViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.adapter_video_trimmer_iv)
        public ImageView img;

        EditViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void clear() {
        if (mFrameList != null) {
            mFrameList.clear();
            notifyDataSetChanged();
        }
    }

    public void destory() {
        if (mFrameList != null) {
            mFrameList.clear();
        }
    }

    public static final int FULL_SCROLL_DURATION = 180 * 1000;
    private static final int FULL_SCROLL_FRAMES = 8;

    public void getFrames(final int viewWidth, final int viewHeight, boolean canScroll) {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Subscriber<VideoTrimmerFrameBean> beanRetriever = new Subscriber<VideoTrimmerFrameBean>() {
            @Override
            public void onCompleted() {
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onFrameReady();
                }
                this.unsubscribe();
            }

            @Override
            public void onError(Throwable e) {
                Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                this.unsubscribe();
            }

            @Override
            public void onNext(VideoTrimmerFrameBean frameBean) {
                if (frameBean.getFramePath() != null) {
                    mFrameList.add(frameBean);
                }
            }
        };
        Observable.create((Observable.OnSubscribe<VideoTrimmerFrameBean>) subscriber -> {
            if (mVideoUri == null) {
                subscriber.onError(new FileNotFoundException());
            }
            mFrameList = new ArrayList<>();
            retriever.setDataSource(context, mVideoUri);
            // Retrieve media data
            long videoLengthInMs = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            //Calculate thumb dimension
            int thumbWidth, thumbHeight, numThumbs;
            long interval;
            if (!canScroll) {
                // Set thumbnail properties (Thumbs are squares)
                thumbWidth = Math.round(1f * viewWidth / FULL_SCROLL_FRAMES);
                thumbHeight = viewHeight;
                numThumbs = (int) Math.ceil(((float) viewWidth) / thumbWidth);
                interval = 1000 * videoLengthInMs / numThumbs;
            } else {
                //计算当视频大于一分钟，缩略图的数量和每张的尺寸
                thumbWidth = Math.round(1f * viewWidth / FULL_SCROLL_FRAMES);
                thumbHeight = viewHeight;
                int totalWidth = Math.round(viewWidth * (1f * videoLengthInMs / FULL_SCROLL_DURATION));
                numThumbs = (int) Math.ceil(((float) totalWidth) / thumbWidth);
                interval = 1000 * videoLengthInMs / numThumbs;
            }
            String filePath = context.getExternalFilesDir("runCatch/").getPath();
            for (int i = 0; i < numThumbs; i++) {
                Bitmap bitmap = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
                    bitmap = retriever.getScaledFrameAtTime(i * interval, MediaMetadataRetriever.OPTION_CLOSEST_SYNC, thumbWidth, thumbHeight);
                } else {
                    bitmap = retriever.getFrameAtTime(i * interval);
                }
                if (bitmap != null) {
                    File file = new File(filePath, String.format("frame_%s", i));
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
                    } catch (FileNotFoundException e) {
                        subscriber.onError(new FileNotFoundException());
                    } catch (IOException e) {
                        subscriber.onError(new IOException());
                    } finally {
                        bitmap.recycle();
                    }
                    VideoTrimmerFrameBean bean = new VideoTrimmerFrameBean();
                    bean.setIndex(i);
                    bean.setFramePath(file.getAbsolutePath());
                    bean.setFrameWidth(thumbWidth);
                    bean.setFrameHeight(thumbHeight);
                    itemWidth = thumbWidth;
                    subscriber.onNext(bean);
                }
            }
            subscriber.onCompleted();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(beanRetriever);

    }

    private Uri mVideoUri;

    public void setVideo(@NonNull Uri data) {
        mVideoUri = data;
    }

    private VideoFrameListener listener;

    public interface VideoFrameListener {
        void onFrameReady();
    }
}
