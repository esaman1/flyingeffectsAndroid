package com.flyingeffects.com.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.ChooseMusic;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.manager.GlideRoundTransform;
import com.flyingeffects.com.ui.model.VideoManage;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.timeUtils;

import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * user :TongJu  ; email:jutongzhang@sina.com
 * time：2019/1/25
 * describe:首页适配
 **/
public class music_recent_adapter extends BaseQuickAdapter<ChooseMusic, BaseViewHolder> {

    private Context context;
    public final static String TAG = "music_recent_adapter";
    private int fromType;
    //    private LinearLayout ll_show_progress;
    private TextView tv_playing_time;
    private SeekBar seekBar;

    public music_recent_adapter(int layoutResId, @Nullable List<ChooseMusic> allData, Context context, int fromType) {
        super(layoutResId, allData);
        this.context = context;
        this.fromType = fromType;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final ChooseMusic item) {
        ImageView iv_collect = helper.getView(R.id.iv_collect);
        seekBar = helper.getView(R.id.seekBar);
        ImageView cover = helper.getView(R.id.iv_cover);
        LinearLayout ll_show_progress = helper.getView(R.id.ll_show_progress);
        tv_playing_time = helper.getView(R.id.tv_playing_time);
        ImageView iv_play_music = helper.getView(R.id.iv_play_music);
        if (fromType == 1) {
            iv_collect.setVisibility(View.GONE);
        } else {
            iv_collect.setVisibility(View.VISIBLE);
        }
        helper.addOnClickListener(R.id.tv_user);
        Glide.with(context)
                .load(item.getImage())
                .apply(RequestOptions.bitmapTransform(new GlideRoundTransform(context, 3)))
                .apply(RequestOptions.placeholderOf(R.mipmap.placeholder))
                .into(cover);
        helper.setText(R.id.tv_user, item.getNickname());
        helper.setText(R.id.tv_title, item.getTitle());
        LogUtil.d("OOM2", "fromType=" + fromType);
        helper.addOnClickListener(R.id.tv_make);
        helper.addOnClickListener(R.id.iv_collect);
        if (item.isPlaying()) {
            if(fromType!=3){
                ll_show_progress.setVisibility(View.VISIBLE);
            }else{
                ll_show_progress.setVisibility(View.GONE);
            }
            iv_play_music.setImageResource(R.mipmap.choose_music_play);
//            seekBar.setProgress(item.getProgress());
//            tv_playing_time.setText(item.getTitle());
        } else {
            ll_show_progress.setVisibility(View.GONE);
            iv_play_music.setImageResource(R.mipmap.choose_music_pause);
        }
        helper.addOnClickListener(R.id.iv_play_music);
        if (item.getIs_collection() == 0) {
            //未收藏
            iv_collect.setImageResource(R.mipmap.zan_unselect);
        } else {
            iv_collect.setImageResource(R.mipmap.zan_new_select);
        }
        Observable.just(item.getAudio_url()).map(new Func1<String, Integer>() {
            @Override
            public Integer call(String s) {
                VideoInfo videoInfo = VideoManage.getInstance().getVideoInfo(context, s);
                return videoInfo.getDuration();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {

            @Override
            public void call(Integer integer) {
                helper.setText(R.id.tv_time, timeUtils.timeParse(integer));
            }
        });

    }


    /**
     * description ：设置播放进度
     * creation date: 2020/9/8
     * user : zhangtongju
     */
    public void setPlayingProgress(int progress, String time) {
        Observable.just(time).subscribeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
            seekBar.setProgress(progress);
            tv_playing_time.setText(s);
        });


    }


}









