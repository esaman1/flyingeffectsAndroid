package com.flyingeffects.com.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.ui.interfaces.VideoPlayerCallbackForTemplate;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.TimeUtils;
import com.flyingeffects.com.view.MarqueTextView;
import com.flyingeffects.com.view.SampleCoverVideo;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
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
    private ImageView iv_zan;
    private MarqueTextView tv_describe;
    private MarqueTextView tv_title_music;
    private int nowPreviewPosition;
    public TTNativeExpressAd ad;
    private TextView tv_zan_count;
    private String OldFromTo;
    private TextView tv_comment_count;
    private TextView tv_btn_follow;


    public Preview_up_and_down_adapter(int layoutResId, @Nullable List<new_fag_template_item> allData, Context context, String OldFromTo) {
        super(layoutResId, allData);
        this.context = context;
        this.OldFromTo = OldFromTo;
    }


    @Override
    protected void convert(final BaseViewHolder helper, final new_fag_template_item item) {
        int offset = helper.getLayoutPosition();
        ad = item.getAd();
        FrameLayout video_layout = helper.getView(R.id.video_layout);
        videoPlayer = helper.getView(R.id.video_item_player);
        LinearLayout ll_down_bj = helper.getView(R.id.ll_down_bj);
        tv_title_music=helper.getView(R.id.tv_title_music);
        LinearLayout ll_zan = helper.getView(R.id.ll_zan);
        TextView tv_make = helper.getView(R.id.tv_make);
        LinearLayout ll_comment = helper.getView(R.id.ll_comment);
        tv_btn_follow=helper.getView(R.id.tv_btn_follow);
        tv_comment_count=helper.getView(R.id.tv_comment_count);
        tv_zan_count = helper.getView(R.id.tv_zan_count);
        boolean readOnly = item.getTest() != 0;
        boolean needHideCreate;
        if (readOnly) {
            needHideCreate = OldFromTo.equals(FromToTemplate.ISHOMEFROMBJ) || OldFromTo.equals(FromToTemplate.ISMESSAGEMYPRODUCTION);
        } else {
            needHideCreate = false;
        }
        iv_zan = helper.getView(R.id.iv_zan);
        ImageView iv_writer = helper.getView(R.id.iv_writer);
        helper.addOnClickListener(R.id.iv_writer);
        helper.addOnClickListener(R.id.tv_describe);

        MarqueTextView tv_writer_name = helper.getView(R.id.tv_writer_name);
        TextView tv_title = helper.getView(R.id.tv_title);
        tv_describe = helper.getView(R.id.tv_describe);
        tv_describe.setVisibility(View.GONE);
        helper.addOnClickListener(R.id.iv_download_bj);
        helper.addOnClickListener(R.id.ll_comment);
        helper.addOnClickListener(R.id.tv_btn_follow);

        if (OldFromTo.equals(FromToTemplate.ISCHOOSEBJ)) {
            tv_make.setText("使用背景");
        } else {
            tv_make.setText("马上制作");
        }
        if (ad == null) {
            //无广告的情况
            tv_title_music.setVisibility(View.VISIBLE);
            videoPlayer.setVisibility(View.VISIBLE);
            tv_make.setVisibility(View.VISIBLE);
            iv_zan.setVisibility(View.VISIBLE);
            iv_writer.setVisibility(View.VISIBLE);
            tv_writer_name.setVisibility(View.VISIBLE);
            tv_title.setVisibility(View.VISIBLE);
            tv_describe.setVisibility(View.VISIBLE);
            tv_zan_count.setText(item.getPraise());
            tv_comment_count.setText(item.getComment());
            helper.setText(R.id.tv_download_count, item.getShare());
            helper.addOnClickListener(R.id.iv_zan);
            helper.addOnClickListener(R.id.tv_make);
            helper.addOnClickListener(R.id.tv_title_music);

            initVideoPlayer(item, offset);
            if (nowPreviewPosition == offset) {
                videoPlayer.startPlayLogic();
            }
            if (needHideCreate) {
                ll_down_bj.setVisibility(View.GONE);
                tv_make.setVisibility(View.GONE);
                ll_comment.setVisibility(View.GONE);
                ll_zan.setVisibility(View.GONE);
            } else {
                ll_down_bj.setVisibility(View.VISIBLE);
                ll_comment.setVisibility(View.VISIBLE);
                tv_make.setVisibility(View.VISIBLE);
                ll_zan.setVisibility(View.VISIBLE);
            }
            Glide.with(context)
                    .load(item.getAuth_image())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(iv_writer);
            tv_writer_name.setText(item.getAuth());
            tv_title.setText(item.getTitle());
            String str = item.getAuth() + "的原创音乐                        "+item.getAuth() + "的原创音乐                        ";
            tv_title_music.setText(str);
            //点赞功能
            if (item.getIs_praise() == 1 && BaseConstans.hasLogin()) {
                iv_zan.setImageResource(R.mipmap.zan_selected);
            } else {
                iv_zan.setImageResource(R.mipmap.zan);
            }

            if( BaseConstans.hasLogin()){
                if(item.getAdmin_id().equals(BaseConstans.GetUserId())){
                    tv_btn_follow.setVisibility(View.GONE);
                }else{
                    //关注按键
                    if (item.getIs_follow() == 1 ) {
                        tv_btn_follow.setText("取消关注");
                        tv_btn_follow.setVisibility(View.GONE);
                    } else {
                        tv_btn_follow.setVisibility(View.VISIBLE);
                        tv_btn_follow.setText("关注");
                    }
                }
            }
            if (video_layout.getChildCount() != 0) {
                video_layout.removeAllViews();
            }
            video_layout.setVisibility(View.GONE);
        } else {
            pauseVideo();
            //有广告的情况下，显示广告页面
            video_layout.setVisibility(View.VISIBLE);
            tv_title_music.setVisibility(View.GONE);
            videoPlayer.setVisibility(View.GONE);
            tv_make.setVisibility(View.GONE);
            iv_zan.setVisibility(View.GONE);
            iv_writer.setVisibility(View.GONE);
            tv_writer_name.setVisibility(View.GONE);
            tv_title.setVisibility(View.GONE);
            tv_describe.setVisibility(View.GONE);
            TTNativeExpressAd ttNativeExpressAd = item.getAd();
            View view = ttNativeExpressAd.getExpressAdView();
            if (view.getParent() != null) {
                ViewGroup vp = (ViewGroup) view.getParent();
                vp.removeAllViews();
            }
            video_layout.addView(view);
        }

    }

    /**
     * description ：点赞功能
     * creation date: 2020/8/5
     * user : zhangtongju
     */
    public void setIsZan(boolean isCollect) {
        if (iv_zan != null) {
            if (isCollect) {
                iv_zan.setImageResource(R.mipmap.zan_selected);
            } else {
                iv_zan.setImageResource(R.mipmap.zan);
            }
        }
    }

    public void setIsZanCount(int zanCount) {

        if (tv_zan_count != null) {
            tv_zan_count.setText(zanCount + "");
        }

    }

    public void setCommentCount(String commentCount) {

        if (tv_comment_count != null) {
            tv_comment_count.setText(commentCount);
        }
    }


    public void setIsFollow(int isFollow){
        //关注按键
        if (isFollow == 1 && BaseConstans.hasLogin()) {
//            tv_btn_follow.setText("取消关注");
            tv_btn_follow.setVisibility(View.GONE);
        } else {
            tv_btn_follow.setText("关注");
            tv_btn_follow.setVisibility(View.VISIBLE);
        }
    }


  




    /**
     * description ：初始化视频播放器，针对列表
     * creation date: 2020/7/2
     * user : zhangtongju
     */
    private void initVideoPlayer(new_fag_template_item item, int offset) {
        if (!TextUtils.isEmpty(item.getOriginfile())) {
            videoPlayer.loadCoverImage(item.getOriginfile(), R.mipmap.black_lucency);
        } else {
            videoPlayer.loadCoverImage(item.getImage(), R.mipmap.black_lucency);
        }
//        videoPlayer.setAnimation(null);
        videoPlayer.getStartButton().setVisibility(View.GONE);

        if (!TextUtils.isEmpty(item.getPre_url())) {
            videoPlayer.setUpLazy(item.getPre_url(), true, null, null, "这是title");
        } else {
            videoPlayer.setUpLazy(item.getVidoefile(), true, null, null, "这是title");
        }


//        if(item.isNeedChangeVideoPath()){
//            videoPlayer.setUpLazy(item.getPre_url(), true, null, null, "这是title");
//            LogUtil.d("OOM","播放的地址为"+item.getPre_url());
//        }else{
//            videoPlayer.setUpLazy(item.getVidoefile(), true, null, null, "这是title");
//            LogUtil.d("OOM","播放的地址为"+item.getVidoefile());
//        }
        videoPlayer.setPlayPosition(offset);
        videoPlayer.clearAnimation();
        videoPlayer.getTitleTextView().setVisibility(View.GONE);
        videoPlayer.setIsTouchWigetFull(false);
        videoPlayer.getBackButton().setVisibility(View.GONE);
        videoPlayer.setIsTouchWiget(false);
        videoPlayer.setNeedShowWifiTip(false);
        //设置全屏按键功能
        videoPlayer.getFullscreenButton().setVisibility(View.GONE);
        videoPlayer.setVideoAllCallBack(new VideoPlayerCallbackForTemplate(new VideoPlayerCallbackForTemplate.videoPlayerStopListener() {
            @Override
            public void isStop(boolean isSuccess) {
            }

            @Override
            public void onPrepared(boolean onPrepared) {
                tv_describe.setVisibility(View.VISIBLE);
                tv_describe.setText("时长" + TimeUtils.timeParse(videoPlayer.getDuration()) + "        上传" + item.getDefaultnum() + "个素材即可制作");
            }
        }));
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);
        videoPlayer.setLooping(true);
    }

    public float getVideoDuration() {
        return videoPlayer.getDuration();
    }

    /**
     * description ：当前正在预览的位置
     * creation date: 2020/7/1
     * user : zhangtongju
     */
    public void NowPreviewChooseItem(int nowPreviewPosition) {
        this.nowPreviewPosition = nowPreviewPosition;
    }


    public void pauseVideo() {
        LogUtil.d("OOM", "pauseVideo");
        videoPlayer.onVideoPause();
        GSYVideoManager.onPause();
    }


    public void startVideo() {
        if (videoPlayer != null && !videoPlayer.isInPlayingState()) {
            LogUtil.d("OOM", "isInPlayingState!=null?" + videoPlayer.isInPlayingState());
            videoPlayer.startPlayLogic();
        }
    }

    public void onDestroy() {
        videoPlayer.release();
    }

}









