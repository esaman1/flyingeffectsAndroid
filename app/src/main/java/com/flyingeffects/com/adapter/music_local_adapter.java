package com.flyingeffects.com.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.BlogFile.Video;
import com.flyingeffects.com.manager.GlideRoundTransform;
import com.flyingeffects.com.utils.timeUtils;

import java.util.List;


/**
 * user :TongJu  ; email:jutongzhang@sina.com
 * time：2019/1/25
 * describe:首页适配
 **/
public class music_local_adapter extends BaseQuickAdapter<Video, BaseViewHolder> {

    private Context context;
    public final static String TAG = "music_recent_adapter";

    public music_local_adapter(int layoutResId, @Nullable List<Video> listVideoFiltrateMp4, Context context) {
        super(layoutResId, listVideoFiltrateMp4);
        this.context = context;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final Video item) {
        int offset = helper.getLayoutPosition();
        long duration=item.getDuration();
        helper.setText(R.id.tv_time, timeUtils.timeParse(duration));
        helper.addOnClickListener(R.id.tv_make);
        helper.addOnClickListener(R.id.iv_play);
        ImageView iv_cover=helper.getView(R.id.iv_cover);
        ImageView iv_play_music=helper.getView(R.id.iv_play);
        Glide.with(context)
                .load(item.getPath())
                .apply(RequestOptions.bitmapTransform(new GlideRoundTransform(context, 5)))
                .apply(RequestOptions.placeholderOf(R.mipmap.placeholder)).into(iv_cover);

        if (item.isPlaying()) {
            iv_play_music.setImageResource(R.mipmap.choose_music_play);
        } else {
            iv_play_music.setImageResource(R.mipmap.choose_music_pause);
        }

    }
}









