package com.flyingeffects.com.ui.view.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.TimelineAdapterForCutVideo;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.manager.DataCleanManager;
import com.flyingeffects.com.ui.interfaces.VideoPlayerCallbackForTemplate;
import com.flyingeffects.com.ui.model.VideoMattingModel;
import com.flyingeffects.com.ui.model.videoCutDurationForVideoOneDo;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.view.EmptyControlVideo;
import com.flyingeffects.com.view.MattingVideoEnity;
import com.glidebitmappool.GlideBitmapPool;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.shixing.sxve.ui.view.WaitingDialogProgressNowAnim;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;


/**
 * description ：只针对一键模板的视频裁剪页面
 * creation date: 2020/1/19
 * user : zhangtongju
 */
public class TemplateCutVideoActivity extends BaseActivity {

    @BindView(R.id.list_thumb)
    RecyclerView list_thumb;

    WaitingDialogProgressNowAnim progressNowAnim;

    private TimelineAdapterForCutVideo mTimelineAdapter;


    @BindView(R.id.relative_select_duration)
    RelativeLayout relative_select_duration;

    @BindView(R.id.duration)
    TextView tv_duration;

    @BindView(R.id.view_line_progress)
    View view_line_progress;

    private int mTotalWidth;

    /**
     * 整个视频时长
     */
    private int mVideoDuration;

    private float needDuration;

    /**
     * 视频源地址
     */
    private String videoPath;


    @BindView(R.id.video_player)
    EmptyControlVideo videoPlayer;

    VideoInfo videoInfo;
    private int mScrollX;


    /**
     * 滑动后选择开始的位置 单位ms
     */
    private int mStartDuration;

    /**
     * 滑动后选择结束的位置,单位ms
     */
    private int mEndDuration;

    /**
     * 来自哪个页面，2来自模板想起切换视频素材  0表示预览选择视频入口页面
     */
    private int isFrom;


    @Override
    protected int getLayoutId() {
        return R.layout.act_template_cut_video;
    }

    @Override
    protected void initView() {
        progressNowAnim=new WaitingDialogProgressNowAnim(this);
        DataCleanManager.deleteFilesByDirectory(getExternalFilesDir("cacheMattingFolder"));
        videoPath = getIntent().getStringExtra("videoPath");
        needDuration = getIntent().getFloatExtra("needCropDuration", 1);
        isFrom=getIntent().getIntExtra("isFrom",0);
        videoInfo = getVideoInfo.getInstance().getRingDuring(videoPath);
        videoPlayer.setUp(videoPath, true, "");
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);
        videoPlayer.setVideoAllCallBack(new VideoPlayerCallbackForTemplate(isSuccess -> {
            videoPlayer.startPlayLogic();
        }));
        mEndDuration = (int) (needDuration * 1000);
        tv_duration.setText("模板时长 "+needDuration+"s");
    }

    @Override
    protected void initAction() {
        initThumbList();
        list_thumb.post(() -> initSingleThumbSize(videoInfo.getVideoWidth(), videoInfo.getVideoHeight(), needDuration, videoInfo.getDuration() / (float) 1000, videoPath));
    }


    private void startVideo() {
        videoPlayer.startPlayLogic();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @OnClick({R.id.iv_close, R.id.iv_correct, R.id.tv_kt,R.id.tv_no_kt})
    public void onMyClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                this.finish();
                break;


            case R.id.iv_correct:
                File file = new File(videoPath);
                if (file.exists()) {
                    WaitingDialog.openPragressDialog(this);
                } else {
                    ToastUtil.showToast(getResources().getString(R.string.write_error));
//                    endTimer();
                }
                break;

            case R.id.tv_kt:
            case R.id.tv_no_kt:
                videoPlayer.onVideoPause();
                progressNowAnim.openProgressDialog();
                new Thread(() -> videoCutDurationForVideoOneDo.getInstance().CutVideoForDrawPadAllExecute2(TemplateCutVideoActivity.this, needDuration * 1000,videoPath,mStartDuration, new videoCutDurationForVideoOneDo.isSuccess() {
                    @Override
                    public void progresss(int progress) {
                        progressNowAnim.setProgress("正在裁剪中"+progress);
                    }

                    @Override
                    public void isSuccess(boolean isSuccess, String path) {
                        progressNowAnim.closePragressDialog();
                        if (isSuccess) {
                            if(v.getId()==R.id.tv_kt){
                                gotoMattingVideo(path);
                            }else{
                                TemplateCutVideoActivity.this.finish();
                                EventBus.getDefault().post(new MattingVideoEnity(null, path,isFrom));
                            }
                        }

                    }
                })).start();
                break;
        }
    }


    private void gotoMattingVideo(String originalPath) {
        Observable.just(originalPath).subscribeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
            VideoMattingModel videoMattingModel = new VideoMattingModel(originalPath, TemplateCutVideoActivity.this, (isSuccess, path) -> {
                TemplateCutVideoActivity.this.finish();
                EventBus.getDefault().post(new MattingVideoEnity(originalPath, path,isFrom));
            });
            videoMattingModel.ToExtractFrame();
        });
    }


    private void initThumbList() {
        list_thumb.setLayoutManager(new LinearLayoutManager(TemplateCutVideoActivity.this, LinearLayoutManager.HORIZONTAL, false));
        mTimelineAdapter = new TimelineAdapterForCutVideo();
        list_thumb.setAdapter(mTimelineAdapter);
        list_thumb.setHasFixedSize(true);
        list_thumb.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    float percent = (float) mScrollX / mTotalWidth;
                    mStartDuration = (int) (mVideoDuration * percent) * 1000;
                    mEndDuration = (int) (mStartDuration + (needDuration * 1000));
                    LogUtil.d("OOM", "mStartDuration=" + mStartDuration + "mEndDuration=" + mEndDuration);
                    seekTo(mStartDuration);
                }
            }

            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                mScrollX += dx;
            }
        });

    }


    private void seekTo(long to) {
        //跳转时暂停播放
        if (videoPlayer != null) {
            LogUtil.d("OOM","seekTo="+to);
            if(to<1000){
                to=1000;
            }
            videoPlayer.seekTo(to);
        }
    }




    /**
     * description ：
     * creation date: 2020/1/20
     * param : height 视频高度  width 视频宽度   duration总时长  mTemplateDuration需要的时长  mVideoPath视频地址
     * user : zhangtongju
     */
    private void initSingleThumbSize(int width, int height, float mTemplateDuration, float duration, String mVideoPath) {
        this.mVideoDuration = (int) duration;
        // 需要截取的listWidth宽度
        int listWidth = list_thumb.getWidth() - list_thumb.getPaddingLeft() - list_thumb.getPaddingRight();
        int listHeight = list_thumb.getHeight();
        float scale = (float) listHeight / height;
        int thumbWidth = (int) (scale * width);
        mTimelineAdapter.setBitmapSize(thumbWidth, listHeight);
        //其中listWidth表示当前截取的大小
        int thumbCount = (int) (listWidth * (duration / mTemplateDuration / thumbWidth));
//        int thumbCount = (int) (listWidth * (duration / mTemplateDuration) / thumbWidth);
        thumbCount = thumbCount > 0 ? thumbCount : 0;
        //每帧所占的时间
        final int interval = (int) (duration / thumbCount * 1000);
        int[] mTimeUs = new int[thumbCount];
        for (int i = 0; i < thumbCount; i++) {
            mTimeUs[i] = i * interval;
        }
        HashMap<Integer, Bitmap> mData = new HashMap<>();
        mTimelineAdapter.setVideoUri(Uri.fromFile(new File(mVideoPath)));
        mTimelineAdapter.setData(mTimeUs, mData);
        mTotalWidth = thumbWidth * thumbCount;
        startVideo();
    }


//    /**
//     * description ：添加滑动效果
//     * creation date: 2020/1/20
//     * user : zhangtongju
//     */
//    int relativeSelectDurationW;
//
//    private void seekProgress(float progress) {
//        //  LogUtil.d("OOM", "progress=" + progress);
//        if (relativeSelectDurationW == 0) {
//            relativeSelectDurationW = relative_select_duration.getMeasuredWidth();
//        }
//        int marginProgress = (int) (progress * relativeSelectDurationW);
//        setMargins(view_line_progress, marginProgress, 0, 0, 0);
//    }


//    /**
//     * 关闭timer 和task
//     */
//    private void endTimer() {
//        destroyTimer();
//    }


//    /**
//     * user :TongJu  ; email:jutongzhang@sina.com
//     * time：2018/10/15
//     * describe:严防内存泄露
//     **/
//    private void destroyTimer() {
//        if (timer != null) {
//            timer.purge();
//            timer.cancel();
//            timer = null;
//        }
//        if (task != null) {
//            task.cancel();
//            task = null;
//        }
//    }


//    /**
//     * 获取当前进度
//     */
//    private long getCurrentPos() {
//        return videoPlayer.getCurrentPositionWhenPlaying();
//    }
//
//
//    public static void setMargins(View v, int l, int t, int r, int b) {
//        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
//            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
//            p.setMargins(l, t, r, b);
//            v.requestLayout();
//        }
//    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        videoPlayer.release();
        GlideBitmapPool.clearMemory();
//        endTimer();
    }
}