package com.flyingeffects.com.adapter;

import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.enity.new_fag_template_item;
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

    public Preview_up_and_down_adapter(int layoutResId, @Nullable List<new_fag_template_item> allData, Context context) {
        super(layoutResId, allData);
        this.context = context;
        this.allData = allData;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final new_fag_template_item item) {
        int offset = helper.getLayoutPosition();
        videoPlayer = helper.getView(R.id.video_item_player);
        tv_make = helper.getView(R.id.tv_make);
        iv_zan = helper.getView(R.id.iv_zan);
        iv_writer = helper.getView(R.id.iv_writer);
        iv_video_play = helper.getView(R.id.iv_video_play);
        tv_writer_name = helper.getView(R.id.tv_writer_name);
        tv_title = helper.getView(R.id.tv_title);
        tv_describe = helper.getView(R.id.tv_describe);
        iv_show_cover = helper.getView(R.id.iv_show_cover);
        videoPlayer.loadCoverImage(item.getImage(), R.mipmap.ic_launcher);
        videoPlayer.setUpLazy(item.getVidoefile(), true, null, null, "这是title");
        videoPlayer.setPlayPosition(offset);
        videoPlayer.getTitleTextView().setVisibility(View.GONE);
        videoPlayer.getBackButton().setVisibility(View.GONE);
        videoPlayer.setIsTouchWigetFull(false);
        //设置全屏按键功能
        videoPlayer.getFullscreenButton().setVisibility(View.GONE);
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);
        videoPlayer.setLooping(true);
        if (nowPreviewPosition == offset) {
            videoPlayer.startPlayLogic();
        }
    }





    /**
     * description ：当前正在预览的位置
     * creation date: 2020/7/1
     * user : zhangtongju
     */
    public void NowPreviewChooseItem(int nowPreviewPosition) {
        this.nowPreviewPosition = nowPreviewPosition;

    }




    public void onDestroy(){
           videoPlayer.release();

    }


}









