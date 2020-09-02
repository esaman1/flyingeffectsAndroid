package com.flyingeffects.com.adapter;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.ChooseMusic;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.ui.model.VideoManage;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.timeUtils;

import java.util.List;


/**
 * user :TongJu  ; email:jutongzhang@sina.com
 * time：2019/1/25
 * describe:首页适配
 **/
public class music_recent_adapter extends BaseQuickAdapter<ChooseMusic, BaseViewHolder> {

    private Context context;
    public final static String TAG = "music_recent_adapter";
    private int fromType;

    public music_recent_adapter(int layoutResId, @Nullable List<ChooseMusic> allData, Context context, int fromType) {
        super(layoutResId, allData);
        this.context = context;
        this.fromType = fromType;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final ChooseMusic item) {
//        int offset = helper.getLayoutPosition();
        ImageView iv_collect = helper.getView(R.id.iv_collect);
        ImageView cover = helper.getView(R.id.iv_cover);
        Glide.with(context).load(item.getImage()).into(cover);
        helper.setText(R.id.tv_user, item.getNickname());
        helper.setText(R.id.tv_title, item.getTitle());
        LogUtil.d("OOM2", "fromType=" + fromType);
        VideoInfo videoInfo = VideoManage.getInstance().getVideoInfo(context, item.getAudio_url());
        LogUtil.d("OOM2", "videoInfo.getDuration()=" + videoInfo.getDuration());
        helper.setText(R.id.tv_time, timeUtils.timeParse(videoInfo.getDuration()));
        helper.addOnClickListener(R.id.tv_make);
        helper.addOnClickListener(R.id.iv_collect);
        if (item.getIs_collection() == 0) {
            //未收藏
            iv_collect.setImageResource(R.mipmap.new_version_collect);
        } else {
            iv_collect.setImageResource(R.mipmap.new_version_collect_ed);
        }


    }


}









