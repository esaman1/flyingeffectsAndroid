/*
 * Copyright 2016 Yan Zhenjie.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.album.app.album;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.R;
import com.yanzhenjie.album.impl.OnCheckedClickListener;
import com.yanzhenjie.album.impl.OnItemClickListener;
import com.yanzhenjie.album.util.AlbumUtils;
import com.yanzhenjie.album.widget.SquareCardView;

import java.util.List;

/**
 * <p>Picture list display adapter.</p>
 * Created by Yan Zhenjie on 2016/10/18.
 */
public class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_BUTTON = 1;
    private static final int TYPE_IMAGE = 2;
    private static final int TYPE_VIDEO = 3;

    private final LayoutInflater mInflater;
    private final boolean hasCamera;
    private final int mChoiceMode;
    private final ColorStateList mSelector;

    private List<AlbumFile> mAlbumFiles;

    private OnItemClickListener mAddPhotoClickListener;
    private OnItemClickListener mItemClickListener;
    private OnCheckedClickListener mCheckedClickListener;
    private View.OnLongClickListener mlongClickListener;

    public AlbumAdapter(Context context, boolean hasCamera, int choiceMode, ColorStateList selector) {
        this.mInflater = LayoutInflater.from(context);
        this.hasCamera = hasCamera;
        this.mChoiceMode = choiceMode;
        this.mSelector = selector;
    }

    public void setAlbumFiles(List<AlbumFile> albumFiles) {
        this.mAlbumFiles = albumFiles;
    }


    public List<AlbumFile> getAlbumFiles() {
        return  mAlbumFiles;
    }

    public void setAddClickListener(OnItemClickListener addPhotoClickListener) {
        this.mAddPhotoClickListener = addPhotoClickListener;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void setCheckedClickListener(OnCheckedClickListener checkedClickListener) {
        this.mCheckedClickListener = checkedClickListener;
    }


    public void setLongClickListener(View.OnLongClickListener clickListener) {
        this.mlongClickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        int camera = hasCamera ? 1 : 0;
        return mAlbumFiles == null ? camera : mAlbumFiles.size() + camera;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0: {
                Log.d("getItemViewType","getItemViewType");
                return hasCamera ? TYPE_BUTTON : TYPE_VIDEO;
            }
            default: {
                position = hasCamera ? position - 1 : position;

                AlbumFile albumFile = mAlbumFiles.get(position);
                return albumFile.getMediaType() == AlbumFile.TYPE_VIDEO ? TYPE_VIDEO : TYPE_IMAGE;
            }
        }
    }
    /**
     * dip转px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_BUTTON: {
                return new ButtonViewHolder(mInflater.inflate(R.layout.album_item_content_button, parent, false), mAddPhotoClickListener);
            }
            case TYPE_IMAGE: {
                ImageHolder imageViewHolder = new ImageHolder(mInflater.inflate(R.layout.album_item_content_image, parent, false),
                        hasCamera,
                        mItemClickListener,
                        mCheckedClickListener,
                        mlongClickListener);
                if (mChoiceMode == Album.MODE_MULTIPLE) {
                    imageViewHolder.mCheckBox.setVisibility(View.VISIBLE);
//                    imageViewHolder.mCheckBox.setButtonTintList(mSelector);
                    Drawable drawable_news = parent.getContext().getResources().getDrawable(R.drawable.album_checkbox_create_video);
                    //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形
                    int radio_size = dip2px(parent.getContext(), 20);
                    drawable_news.setBounds(0, 0, radio_size, radio_size);
                    imageViewHolder.mCheckBox.setCompoundDrawables(drawable_news, null, null, null);
                    imageViewHolder.mCheckBox.setTextColor(mSelector);
                } else {
                    imageViewHolder.mCheckBox.setVisibility(View.GONE);
                }
                return imageViewHolder;
            }
            case TYPE_VIDEO: {
                VideoHolder videoViewHolder = new VideoHolder(mInflater.inflate(R.layout.album_item_content_video, parent, false),
                        hasCamera,
                        mItemClickListener,
                        mCheckedClickListener,
                        mlongClickListener);
                if (mChoiceMode == Album.MODE_MULTIPLE) {
                    videoViewHolder.mCheckBox.setVisibility(View.VISIBLE);
                    Drawable drawable_news = parent.getContext().getResources().getDrawable(R.drawable.album_checkbox_create_video);
                    //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形
                    int radio_size = dip2px(parent.getContext(), 20);
                    drawable_news.setBounds(0, 0, radio_size, radio_size);
                    videoViewHolder.mCheckBox.setCompoundDrawables(drawable_news, null, null, null);
//                    videoViewHolder.mCheckBox.setButtonTintList(mSelector);
                    videoViewHolder.mCheckBox.setTextColor(mSelector);
                } else {
                    videoViewHolder.mCheckBox.setVisibility(View.GONE);
                }
                return videoViewHolder;
            }
            default: {
                throw new AssertionError("This should not be the case.");
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_BUTTON: {
                // Nothing.
                break;
            }
            case TYPE_IMAGE:
            case TYPE_VIDEO: {
                MediaViewHolder mediaHolder = (MediaViewHolder) holder;
                int camera = hasCamera ? 1 : 0;
                position = holder.getAdapterPosition() - camera;
                AlbumFile albumFile = mAlbumFiles.get(position);
                mediaHolder.setData(albumFile);
                break;
            }
            default: {
                throw new AssertionError("This should not be the case.");
            }
        }
    }

    private static class ButtonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final OnItemClickListener mItemClickListener;

        ButtonViewHolder(View itemView, OnItemClickListener itemClickListener) {
            super(itemView);
            this.mItemClickListener = itemClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null && v == itemView) {
                mItemClickListener.onItemClick(v, 0);
            }
        }
    }

    private static class ImageHolder extends MediaViewHolder implements View.OnClickListener {

        private final boolean hasCamera;

        private final OnItemClickListener mItemClickListener;
        private final OnCheckedClickListener mCheckedClickListener;

        private ImageView mIvImage;
        private CheckBox mCheckBox;
        private TextView tv_count;
        private FrameLayout mLayoutLayer;
        private SquareCardView squareCardView;

        ImageHolder(View itemView, boolean hasCamera,
                    OnItemClickListener itemClickListener, OnCheckedClickListener checkedClickListener, View.OnLongClickListener mlongClickListener) {
            super(itemView);
            this.hasCamera = hasCamera;
            this.mItemClickListener = itemClickListener;
            this.mCheckedClickListener = checkedClickListener;
            tv_count=itemView.findViewById(R.id.tv_count);
            mIvImage = itemView.findViewById(R.id.iv_album_content_image);
            mCheckBox = itemView.findViewById(R.id.check_box);
            mLayoutLayer = itemView.findViewById(R.id.layout_layer);
            squareCardView=itemView.findViewById(R.id.squareCardView);
            itemView.setOnClickListener(this);
            squareCardView.setOnLongClickListener(mlongClickListener);
            mCheckBox.setOnClickListener(this);
            mLayoutLayer.setOnClickListener(this);
        }

        @Override
        public void setData(AlbumFile albumFile) {
            mCheckBox.setChecked(albumFile.isChecked());
            Album.getAlbumConfig()
                    .getAlbumLoader()
                    .load(mIvImage, albumFile);

            if(albumFile.getNowChooseIndex()!=0){
                tv_count.setText(albumFile.getNowChooseIndex()+"");
                tv_count.setVisibility(View.VISIBLE);
            }else{
                tv_count.setVisibility(View.GONE);
            }
            mLayoutLayer.setVisibility(albumFile.isDisable() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                int camera = hasCamera ? 1 : 0;
                mItemClickListener.onItemClick(v, getAdapterPosition() - camera);
            } else if (v == mCheckBox) {
                int camera = hasCamera ? 1 : 0;
                mCheckedClickListener.onCheckedClick(mCheckBox, getAdapterPosition() - camera);
            } else if (v == mLayoutLayer) {
                int camera = hasCamera ? 1 : 0;
                mItemClickListener.onItemClick(v, getAdapterPosition() - camera);
            }
        }
    }

    private static class VideoHolder extends MediaViewHolder implements View.OnClickListener {

        private final boolean hasCamera;

        private final OnItemClickListener mItemClickListener;
        private final OnCheckedClickListener mCheckedClickListener;

        private ImageView mIvImage;
        private CheckBox mCheckBox;
        private TextView mTvDuration;
        private SquareCardView squareCardView;
        private FrameLayout mLayoutLayer;
        private TextView tv_count;

        VideoHolder(View itemView, boolean hasCamera,
                    OnItemClickListener itemClickListener, OnCheckedClickListener checkedClickListener, View.OnLongClickListener LongClickListener) {
            super(itemView);
            this.hasCamera = hasCamera;
            this.mItemClickListener = itemClickListener;
            this.mCheckedClickListener = checkedClickListener;
            tv_count=itemView.findViewById(R.id.tv_count);
            mIvImage = itemView.findViewById(R.id.iv_album_content_image);
            mCheckBox = itemView.findViewById(R.id.check_box);
            mTvDuration = itemView.findViewById(R.id.tv_duration);
            mLayoutLayer = itemView.findViewById(R.id.layout_layer);
            squareCardView=itemView.findViewById(R.id.squareCardView);
            itemView.setOnClickListener(this);
            squareCardView.setOnLongClickListener(LongClickListener);
            mCheckBox.setOnClickListener(this);
            mLayoutLayer.setOnClickListener(this);
        }

        @Override
        public void setData(AlbumFile albumFile) {
            Album.getAlbumConfig().getAlbumLoader().load(mIvImage, albumFile);
            mCheckBox.setChecked(albumFile.isChecked());
            if(albumFile.getDuration()==0){
                mTvDuration.setVisibility(View.INVISIBLE);
            }else{
                mTvDuration.setVisibility(View.VISIBLE);
                mTvDuration.setText(AlbumUtils.convertDuration(albumFile.getDuration()));
            }

            if(albumFile.getNowChooseIndex()!=0){
                tv_count.setText(albumFile.getNowChooseIndex()+"");
                tv_count.setVisibility(View.VISIBLE);
            }else{
                tv_count.setVisibility(View.GONE);
            }

            mLayoutLayer.setVisibility(albumFile.isDisable() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                int camera = hasCamera ? 1 : 0;
                mItemClickListener.onItemClick(v, getAdapterPosition() - camera);
            } else if (v == mCheckBox) {
                int camera = hasCamera ? 1 : 0;
                mCheckedClickListener.onCheckedClick(mCheckBox, getAdapterPosition() - camera);
            } else if (v == mLayoutLayer) {
                if (mItemClickListener != null) {
                    int camera = hasCamera ? 1 : 0;
                    mItemClickListener.onItemClick(v, getAdapterPosition() - camera);
                }
            }
        }
    }

    private abstract static class MediaViewHolder extends RecyclerView.ViewHolder {
        public MediaViewHolder(View itemView) {
            super(itemView);
        }

        /**
         * Bind Item data.
         */
        public abstract void setData(AlbumFile albumFile);
    }



//    public void setSelected(int position ,boolean isSelect){
//        int camera = hasCamera ? 1 : 0;
//        mCheckedClickListener.onCheckedClick(mCheckBox, position - camera);
//
//    }
}