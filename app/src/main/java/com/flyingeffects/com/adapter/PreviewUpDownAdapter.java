package com.flyingeffects.com.adapter;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.flyingeffects.com.R;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.NewFragmentTemplateItem;
import com.flyingeffects.com.ui.interfaces.VideoPlayerCallbackForTemplate;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.utils.ButtonJitterAnimatorUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.TimeUtils;
import com.flyingeffects.com.view.MarqueTextView;
import com.flyingeffects.com.view.SampleCoverVideo;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;

import java.util.List;

import androidx.annotation.Nullable;


/**
 * user :TongJu  ; email:jutongzhang@sina.com
 * time：2019/1/25
 * describe:首页适配
 **/
public class PreviewUpDownAdapter extends BaseQuickAdapter<NewFragmentTemplateItem, BaseViewHolder> {
    private static final String TAG = "Preview_up_and_down_ada";


    private SampleCoverVideo videoPlayer;
    private ImageView iv_zan;
    private MarqueTextView tv_describe;
    private MarqueTextView tv_title_music;
    private TextView tv_zan_count;
    private TextView tv_comment_count;
    private TextView tv_btn_follow;

    private String OldFromTo;
    private int nowPreviewPosition;
    public TTNativeExpressAd ad;

    public PreviewUpDownAdapter(int layoutResId, @Nullable List<NewFragmentTemplateItem> allData, String OldFromTo) {
        super(layoutResId, allData);
        this.OldFromTo = OldFromTo;
    }


    public void SetOldFromTo(String OldFromTo) {
        this.OldFromTo = OldFromTo;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final NewFragmentTemplateItem item) {

        ad = item.getAd();
        int offset = helper.getLayoutPosition();
        FrameLayout video_layout = helper.getView(R.id.video_layout);
        videoPlayer = helper.getView(R.id.video_item_player);
        LinearLayout ll_down_bj = helper.getView(R.id.ll_down_bj);
        ImageView iv_show_cover = helper.getView(R.id.iv_show_cover);
        tv_title_music = helper.getView(R.id.tv_title_music);
        LinearLayout ll_zan = helper.getView(R.id.ll_zan);
        TextView tv_make = helper.getView(R.id.tv_make);
        LinearLayout ll_comment = helper.getView(R.id.ll_comment);
        LinearLayout ll_describe = helper.getView(R.id.ll_describe);
        tv_btn_follow = helper.getView(R.id.tv_btn_follow);
        tv_comment_count = helper.getView(R.id.tv_comment_count);
        tv_zan_count = helper.getView(R.id.tv_zan_count);
        iv_zan = helper.getView(R.id.iv_zan);
        ImageView iv_writer = helper.getView(R.id.iv_writer);
        TextView tv_title = helper.getView(R.id.tv_title);
        tv_describe = helper.getView(R.id.tv_describe);
        MarqueTextView tv_writer_name = helper.getView(R.id.tv_writer_name);
        helper.addOnClickListener(R.id.iv_writer);
        helper.addOnClickListener(R.id.tv_describe);
        helper.addOnClickListener(R.id.iv_download_bj);
        helper.addOnClickListener(R.id.ll_comment);
        helper.addOnClickListener(R.id.tv_btn_follow);
        helper.addOnClickListener(R.id.tv_writer_name);

        boolean readOnly = item.getTest() != 0;
        boolean needHideCreate;
        tv_describe.setVisibility(View.GONE);
        if (readOnly) {
            needHideCreate = OldFromTo.equals(FromToTemplate.ISHOMEFROMBJ) || OldFromTo.equals(FromToTemplate.ISMESSAGEMYPRODUCTION) || OldFromTo.equals(FromToTemplate.DRESSUP);
        } else {
            needHideCreate = false;
        }

        ObjectAnimator animator = ButtonJitterAnimatorUtil.jitter(tv_make);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();

        if (OldFromTo.equals(FromToTemplate.ISCHOOSEBJ)) {
            tv_make.setText("使用背景");
        } else {
            tv_make.setText("马上制作");
        }

        if (ad == null) {
            boolean hasVideo=true;
            LogUtil.d("OOM22", "无广告");
            if (OldFromTo.equals(FromToTemplate.DRESSUP) || OldFromTo.equals(FromToTemplate.CHOOSEBJ) || OldFromTo.equals(FromToTemplate.SPECIAL)) {
                videoPlayer.setVisibility(View.GONE);
                pauseVideo();
                hasVideo=false;
                iv_show_cover.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(item.getImage())
                        .into(iv_show_cover);
            } else {
                hasVideo=true;
                videoPlayer.setVisibility(View.VISIBLE);
                initVideoPlayer(item, offset);
                iv_show_cover.setVisibility(View.GONE);
            }
            //无广告的情况
            tv_title_music.setVisibility(View.VISIBLE);
//            videoPlayer.setVisibility(View.VISIBLE);
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
            if (nowPreviewPosition == offset&&hasVideo) {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
                        videoPlayer.startPlayLogic();
                        videoPlayer.onVideoResume();
//                    }
//                }, 200);
                LogUtil.d(TAG, "startPlayLogic");
                LogUtil.d(TAG, "nowPreviewPosition = " + nowPreviewPosition);
                LogUtil.d(TAG, "offset = " + offset);
            }

            if (needHideCreate) {
                ll_down_bj.setVisibility(View.GONE);
                //tv_make.setVisibility(View.GONE);
                ll_comment.setVisibility(View.GONE);
                ll_zan.setVisibility(View.GONE);
            } else {
                ll_down_bj.setVisibility(View.VISIBLE);
                ll_comment.setVisibility(View.VISIBLE);
                tv_make.setVisibility(View.VISIBLE);
                ll_zan.setVisibility(View.VISIBLE);
            }

            Glide.with(mContext)
                    .load(item.getAuth_image())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(iv_writer);

            tv_writer_name.setText("@" + item.getAuth());
            tv_title.setText(item.getTitle());
            String str = item.getAuth() + "的原声音乐                        " + item.getAuth() + "的原声音乐                        ";
            tv_title_music.setText(str);
            //点赞功能
            if (item.getIs_praise() == 1 && BaseConstans.hasLogin()) {
                iv_zan.setImageResource(R.mipmap.zan_selected);
            } else {
                iv_zan.setImageResource(R.mipmap.zan);
            }

            if (BaseConstans.hasLogin()) {
                if (item.getAdmin_id() != null && BaseConstans.GetUserId() != null && item.getAdmin_id().equals(BaseConstans.GetUserId())) {
                    tv_btn_follow.setVisibility(View.GONE);
                } else {
                    //关注按键
                    if (item.getIs_follow() == 1) {
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
            if (OldFromTo.equals(FromToTemplate.DRESSUP) || OldFromTo.equals(FromToTemplate.CHOOSEBJ) || OldFromTo.equals(FromToTemplate.SPECIAL) || OldFromTo.equals(FromToTemplate.FACEGIF) || OldFromTo.equals(FromToTemplate.TEMPLATESPECIAL)) {
                ll_describe.setVisibility(View.GONE);
            } else {
                ll_describe.setVisibility(View.VISIBLE);
            }
        } else {
            LogUtil.d("OOM22", "是广告");
            iv_show_cover.setVisibility(View.GONE);
            pauseVideo();
            //有广告的情况下，显示广告页面
            video_layout.setVisibility(View.VISIBLE);
            tv_title_music.setVisibility(View.GONE);
            videoPlayer.setVisibility(View.GONE);
            tv_make.setVisibility(View.GONE);
            iv_zan.setVisibility(View.GONE);
            iv_writer.setVisibility(View.GONE);
            ll_describe.setVisibility(View.GONE);
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
            if (zanCount < 0) {
                zanCount = 0;
            }
            tv_zan_count.setText(zanCount + "");
        }
    }

    public void setCommentCount(String commentCount) {

        if (tv_comment_count != null) {
            tv_comment_count.setText(commentCount);
        }
    }

    public void setIsFollow(int isFollow, String Admin_id) {

        if (BaseConstans.hasLogin()) {
            if (Admin_id.equals(BaseConstans.GetUserId())) {
                tv_btn_follow.setVisibility(View.GONE);
            } else {
                //关注按键
                if (isFollow == 1) {
                    tv_btn_follow.setText("取消关注");
                    tv_btn_follow.setVisibility(View.GONE);
                } else {
                    tv_btn_follow.setVisibility(View.VISIBLE);
                    tv_btn_follow.setText("关注");
                }
            }
        }

//        //关注按键
//        if (isFollow == 1 && BaseConstans.hasLogin()) {
////            tv_btn_follow.setText("取消关注");
//            tv_btn_follow.setVisibility(View.GONE);
//        } else {
//            tv_btn_follow.setText("关注");
//            tv_btn_follow.setVisibility(View.VISIBLE);
//        }
    }

    /**
     * description ：初始化视频播放器，针对列表
     * creation date: 2020/7/2
     * user : zhangtongju
     */
    private void initVideoPlayer(NewFragmentTemplateItem item, int offset) {
        if (!TextUtils.isEmpty(item.getOriginfile())) {
            videoPlayer.loadCoverImage(item.getOriginfile(), R.mipmap.black_lucency);
        } else {
            videoPlayer.loadCoverImage(item.getImage(), R.mipmap.black_lucency);
        }
        videoPlayer.getStartButton().setVisibility(View.GONE);
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
                tv_describe.setText("时长" + TimeUtils.timeParse(videoPlayer.getDuration())
                        + "  上传" + item.getDefaultnum() + "个素材");
            }
        }));
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);
        videoPlayer.setLooping(true);
        if (!TextUtils.isEmpty(item.getPre_url())) {
            videoPlayer.setUpLazy(item.getPre_url(), true, null, null, "这是title");
            LogUtil.d(TAG, "Pre_url = " + item.getPre_url());
        } else {
            videoPlayer.setUpLazy(item.getVidoefile(), true, null, null, "这是title");
            LogUtil.d(TAG, "Vidoefile = " + item.getVidoefile());
        }
    }

    public float getVideoDuration() {
        return videoPlayer.getDuration();
    }

    /**
     * description ：当前正在预览的位置
     * creation date: 2020/7/1
     * user : zhangtongju
     */
    public void nowPreviewChooseItem(int nowPreviewPosition) {
        this.nowPreviewPosition = nowPreviewPosition;
    }

    public void pauseVideo() {
        LogUtil.d("OOM22", "pauseVideo");
        if (videoPlayer != null) {
            videoPlayer.onVideoPause();
        }
        GSYVideoManager.onPause();
    }

//    public void startVideo() {
//        if (videoPlayer != null && !videoPlayer.isInPlayingState()) {
//            LogUtil.d(TAG, "isInPlayingState!=null?" + videoPlayer.isInPlayingState());
//            videoPlayer.startPlayLogic();
//        }
//    }

    public void onDestroy() {
        videoPlayer.release();
    }

}