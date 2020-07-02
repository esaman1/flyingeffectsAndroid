package com.flyingeffects.com.adapter;

import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.ui.interfaces.VideoPlayerCallbackForTemplate;
import com.flyingeffects.com.utils.timeUtils;
import com.flyingeffects.com.view.MarqueTextView;
import com.flyingeffects.com.view.SampleCoverVideo;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;

import java.util.List;


/**
 * user :TongJu  ; email:jutongzhang@sina.com
 * time：2019/1/25
 * describe:首页适配
 **/
public class Preview_up_and_down_adapter extends BaseQuickAdapter<new_fag_template_item, BaseViewHolder> {

    private Context context;
    private SampleCoverVideo videoPlayer;
    private TextView tv_make;
    private ImageView iv_zan;
    private ImageView iv_writer;
    private ImageView iv_video_play;
    private TextView tv_writer_name;
    private TextView tv_title;
    private MarqueTextView tv_describe;
    private ImageView iv_show_cover;
    private List<new_fag_template_item> allData;
    private boolean isPlayComplete = false;
    private int nowPreviewPosition;
    private AlphaAnimation hideAnim;
    private boolean readOnly;

    public Preview_up_and_down_adapter(int layoutResId, @Nullable List<new_fag_template_item> allData, Context context, boolean readOnly) {
        super(layoutResId, allData);
        this.context = context;
        this.allData = allData;
        this.readOnly = readOnly;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final new_fag_template_item item) {
        int offset = helper.getLayoutPosition();
        videoPlayer = helper.getView(R.id.video_item_player);
        tv_make = helper.getView(R.id.tv_make);
        iv_zan = helper.getView(R.id.iv_zan);
        helper.addOnClickListener(R.id.iv_zan);
        helper.addOnClickListener(R.id.tv_make);
        iv_writer = helper.getView(R.id.iv_writer);
        iv_video_play = helper.getView(R.id.iv_video_play);
        tv_writer_name = helper.getView(R.id.tv_writer_name);
        tv_title = helper.getView(R.id.tv_title);
        tv_describe = helper.getView(R.id.tv_describe);
        iv_show_cover = helper.getView(R.id.iv_show_cover);
        initVideoPlayer(item, offset);
        if (nowPreviewPosition == offset) {
            videoPlayer.startPlayLogic();
        }
        if (readOnly) {
            tv_make.setVisibility(View.GONE);
            iv_zan.setVisibility(View.GONE);
        } else {
            tv_make.setVisibility(View.VISIBLE);
            iv_zan.setVisibility(View.VISIBLE);
        }
        Glide.with(context)
                .load(item.getAuth_image())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(iv_writer);
        tv_writer_name.setText(item.getAuth());
        tv_title.setText(item.getRemark());

        if (item.getIs_collection() == 1) {
            iv_zan.setImageResource(R.mipmap.zan_selected);
        } else {
            iv_zan.setImageResource(R.mipmap.zan);
        }

    }








    /**
     * description ：初始化视频播放器，针对列表
     * creation date: 2020/7/2
     * user : zhangtongju
     */
    private void initVideoPlayer(new_fag_template_item item, int offset) {
        videoPlayer.loadCoverImage(item.getImage(), R.mipmap.black_lucency);
        videoPlayer.setUpLazy(item.getVidoefile(), true, null, null, "这是title");
        videoPlayer.setPlayPosition(offset);
        videoPlayer.getTitleTextView().setVisibility(View.GONE);
        videoPlayer.getBackButton().setVisibility(View.GONE);
        videoPlayer.setIsTouchWigetFull(false);
        //设置全屏按键功能
        videoPlayer.getFullscreenButton().setVisibility(View.GONE);
        videoPlayer.setVideoAllCallBack(new VideoPlayerCallbackForTemplate(new VideoPlayerCallbackForTemplate.videoPlayerStopListener() {
            @Override
            public void isStop(boolean isSuccess) {
            }

            @Override
            public void onPrepared(boolean onPrepared) {
                tv_describe.setText("时长" + timeUtils.timeParse(videoPlayer.getDuration()) + "        上传" + item.getDefaultnum() + "个素材即可制作");
            }
        }));
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);
        videoPlayer.setLooping(true);
    }


    /**
     * description ：当前正在预览的位置
     * creation date: 2020/7/1
     * user : zhangtongju
     */
    public void NowPreviewChooseItem(int nowPreviewPosition) {
        this.nowPreviewPosition = nowPreviewPosition;
    }



    public void pauseVideo(){
        videoPlayer.onVideoPause();
    }





    public void onDestroy() {
        videoPlayer.release();
    }


}









