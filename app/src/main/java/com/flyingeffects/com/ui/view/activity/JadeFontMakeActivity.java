package com.flyingeffects.com.ui.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.TemplateViewPager;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.databinding.ActivityJadeFontMakeBinding;
import com.flyingeffects.com.enity.CutSuccess;
import com.flyingeffects.com.enity.SubtitleEntity;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.ui.interfaces.view.JadeFontMakeMvpView;
import com.flyingeffects.com.ui.presenter.JadeFontMakePresenter;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.TimeUtils;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.record.AutoIdentifySubtitlesDialog;
import com.flyingeffects.com.view.drag.CreationTemplateProgressBarView;
import com.flyingeffects.com.view.drag.JakeFontMakeSeekBarView;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.lansosdk.videoeditor.MediaInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.core.content.ContextCompat;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;


/**
 * @author ZhouGang
 * @date 2021/5/21
 * 玉体字制作activity
 */
public class JadeFontMakeActivity extends BaseActivity implements JakeFontMakeSeekBarView.SeekBarProgressListener,
        AutoIdentifySubtitlesDialog.OnIdentifySubtitleListener, JadeFontMakeMvpView {

    ImageView check_box_0;
    ImageView check_box_1;
    ImageView check_box_2;
    ImageView check_box_3;

    TextView tv_0;
    TextView tv_1;
    TextView tv_2;
    TextView tv_3;

    ActivityJadeFontMakeBinding mBinding;
    String mVideoPath;
    String mImagePath;
    /**是不是横屏*/
    private boolean nowUiIsLandscape = false;
    private boolean isPlaying= false;
    private boolean isEndDestroy = false;
    private boolean isPlayComplete = false;
    private boolean isIntoPause = false;
    /**
     * 素材手动拖动
     */
    boolean mSeekBarViewManualDrag = false;
    SimpleExoPlayer exoPlayer;
    /**0为素材音乐 2为背景视频的音乐*/
    private int musicChooseIndex = 0;
    /**
     * 背景音乐播放的开始位置
     */
    private long musicStartTime = 0;
    /**
     * 背景音乐播放的结束位置
     */
    private long musicEndTime;
    /**视频或图片玉体字编辑默认的时长*/
    private long allVideoDuration;
    /**
     * 裁剪后主轨道播放开始时间
     */
    private long mCutStartTime;

    /**
     * 裁剪后主轨道播放结束时间
     */
    private long mCutEndTime;
    /**进度条当前的进度位置*/
    private long progressBarProgress;
    /**
     * 背景音乐播放器
     */
    private MediaPlayer bgmPlayer;
    private MediaSource mediaSource;
    /**
     * 是否初始化过播放器
     */
    private boolean isInitVideoLayer = false;

    AutoIdentifySubtitlesDialog mSubtitlesDialog;

    JadeFontMakePresenter mPresenter;

    String mChangeMusicPath ="";
    /**
     * 获得背景视频音乐
     */
    private String bgmPath;
    List<View> listForInitBottom = new ArrayList<>();
    /**玉体字view的id，创建一个自增一个*/
    int mJadeFontViewIndex = 0;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {
        mBinding = ActivityJadeFontMakeBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);

        mVideoPath = getIntent().getStringExtra("videoPath");
        mImagePath = getIntent().getStringExtra("imagePath");
        mPresenter = new  JadeFontMakePresenter(this,this,mVideoPath);

        setOnClickListener();
        setDefaultVideoPlayerView();
        setProgressBarListener();
        mBinding.jakeFontSeekBarView.setProgressListener(this);
        mSubtitlesDialog = new AutoIdentifySubtitlesDialog(this);
        mSubtitlesDialog.setSubtitleListener(this);
    }

    @Override
    protected void initAction() {
        //设置预览界面大小
        initViewLayerRelative();
        initBottomLayout();
        EventBus.getDefault().register(this);
    }

    private void initBottomLayout() {
        initViewAddWord();
        initViewForChooseMusic();
        TemplateViewPager templateViewPager = new TemplateViewPager(listForInitBottom);
        mBinding.viewPager.setAdapter(templateViewPager);
    }


    private void setOnClickListener() {
        mBinding.tvTopSubmit.setOnClickListener(this::onViewClicked);
        mBinding.llPlay.setOnClickListener(this::onViewClicked);
        mBinding.ivAddSticker.setOnClickListener(this::onViewClicked);
        mBinding.ivTopBack.setOnClickListener(this::onViewClicked);
        mBinding.ivChangeUi.setOnClickListener(this::onViewClicked);
        mBinding.tvAddWord.setOnClickListener(this::onViewClicked);
        mBinding.tvIdentifySubtitles.setOnClickListener(this::onViewClicked);
        mBinding.tvChangeMusic.setOnClickListener(this::onViewClicked);
        mBinding.rlCreationContainer.setOnClickListener(this::onViewClicked);
    }

    private void onViewClicked(View view) {
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            if (view == mBinding.tvTopSubmit) {

            } else if (view == mBinding.llPlay) {
                onPlayClick();
            } else if (view == mBinding.ivAddSticker) {
//            addSticker();
            } else if (view == mBinding.ivTopBack) {
                onBackPressed();
            } else if (view == mBinding.tvAddWord) {
                onClickAddWordBtn();
            } else if (view == mBinding.tvIdentifySubtitles) {
                if (!TextUtils.isEmpty(mImagePath)) {
                    if (TextUtils.isEmpty(bgmPath)) {
                        ToastUtil.showToast("请选择一个音频文件再识别哟~");
                    } else {
                        mSubtitlesDialog.show();
                    }
                } else {
                    if (musicChooseIndex == 2) {
                        if (TextUtils.isEmpty(bgmPath)) {
                            ToastUtil.showToast("没有音频文件可以识别~");
                        } else {
                            mSubtitlesDialog.show();
                        }
                    } else {
                        mSubtitlesDialog.show();
                    }
                }
            } else if (view == mBinding.tvChangeMusic) {
                onClickMusicBtn();
            } else if (view == mBinding.ivChangeUi) {
                changeLandscape();
            } else if (view == mBinding.rlCreationContainer) {
                mBinding.progressBarView.hindArrow();
            }
        }
    }

    private static final int[] LIN_ID = {R.id.tv_add_word, R.id.tv_change_music};

    private void setTextColor(int chooseItem) {
        for (int value : LIN_ID) {
            ((TextView) findViewById(value)).setTextColor(ContextCompat.getColor(this, R.color.white));
        }
        if (chooseItem >= 0) {
            ((TextView) findViewById(LIN_ID[chooseItem])).setTextColor(Color.parseColor("#5496FF"));
        }
    }

    /**
     * 是否显示多时间线
     *
     * @param isShow
     */
    private void seekBarViewIsShow(boolean isShow) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mBinding.llProgress.getLayoutParams();
        if (isShow) {
            layoutParams.addRule(RelativeLayout.ABOVE, R.id.rl_seek_bar);
            mBinding.viewPager.setVisibility(View.GONE);
            mBinding.rlSeekBar.setVisibility(View.VISIBLE);
            setTextColor(-1);
        } else {
            layoutParams.addRule(RelativeLayout.ABOVE, R.id.viewPager);
            mBinding.viewPager.setVisibility(View.VISIBLE);
            mBinding.rlSeekBar.setVisibility(View.GONE);
        }
        mBinding.llProgress.setLayoutParams(layoutParams);
    }

    public void chooseTab(int pageNum) {
        mBinding.viewPager.setCurrentItem(pageNum,false);
    }

    /**
     * 选择音乐
     */
    private void onClickMusicBtn() {
        seekBarViewIsShow(false);
        chooseTab(1);
        setTextColor(1);
    }

    private void onClickAddWordBtn() {
        seekBarViewIsShow(false);
        chooseTab(0);
        setTextColor(0);
        mBinding.jakeFontSeekBarView.addTemplateMaterialItemView(mCutEndTime, "", getCurrentPos(), getCurrentPos() + 5000, true,
                "是单个玉体字的文本", mJadeFontViewIndex, null, -1, mBinding.progressBarView.progressTotalWidth);
        mJadeFontViewIndex++;
    }

    /**
     * 初始化加字页面
     */
    private void initViewAddWord() {
        View addJadeFontView = LayoutInflater.from(this).inflate(R.layout.view_add_jade_font,mBinding.viewPager,false);
        listForInitBottom.add(addJadeFontView);
    }

    boolean isPlayVideoInAudio = false;
     /**
     * 初始化选音乐页面
     */
    private void initViewForChooseMusic() {
        //添加音乐
        View viewForChooseMusic = LayoutInflater.from(this).inflate(R.layout.view_choose_music, mBinding.viewPager, false);

        TextView tvAddMusic = viewForChooseMusic.findViewById(R.id.tv_add_music);
        TextView tvDownMusic = viewForChooseMusic.findViewById(R.id.iv_down_music);

        tvDownMusic.setVisibility(View.VISIBLE);
        tvDownMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBarViewIsShow(true);
            }
        });

        tvAddMusic.setOnClickListener(view -> {
            isPlayVideoInAudio = false;
            videoToPause();
            Intent intent = new Intent(this, ChooseMusicActivity.class);
            intent.putExtra("needDuration", allVideoDuration);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        tv_0 = viewForChooseMusic.findViewById(R.id.tv_0);
        tv_1 = viewForChooseMusic.findViewById(R.id.tv_1);
        tv_2 = viewForChooseMusic.findViewById(R.id.tv_2);
        tv_3 = viewForChooseMusic.findViewById(R.id.tv_3);

        check_box_0 = viewForChooseMusic.findViewById(R.id.iv_check_box_0);
        check_box_1 = viewForChooseMusic.findViewById(R.id.iv_check_box_1);
        check_box_2 = viewForChooseMusic.findViewById(R.id.iv_check_box_2);
        check_box_3 = viewForChooseMusic.findViewById(R.id.iv_check_box_3);

        tv_0.setText("背景音乐");
        tv_1.setText("素材音乐");
        tv_2.setText("提取音乐");

        //是选择的图片作为背景的话 就没有背景音乐
        if (!TextUtils.isEmpty(mImagePath)) {
            tv_0.setVisibility(View.GONE);
            check_box_0.setVisibility(View.GONE);
        }
        tv_1.setVisibility(View.GONE);
        check_box_1.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(mVideoPath)) {
            isPlayVideoInAudio  = true;
            chooseCheckBox(0);
        }

        View.OnClickListener tvMusicListener = view -> {
            switch (view.getId()) {
                case R.id.iv_check_box_0:
                case R.id.tv_0:
                    musicChooseIndex = 0;
                    if (!TextUtils.isEmpty(mVideoPath)) {
                        if (isPlayVideoInAudio) {
                            clearCheckBox();
                            isPlayVideoInAudio = false;
                            if (exoPlayer != null) {
                                exoPlayer.setVolume(0f);
                            }
                           getBgmPath("");
                        } else {
                            mPresenter.chooseVideoInAudio(0);
                            isPlayVideoInAudio = true;
                        }
                    } else {
                        ToastUtil.showToast("图片没有背景音乐哟");
                    }
                    break;
                case R.id.tv_1:
                case R.id.iv_check_box_1:
                    mPresenter.chooseNowStickerMaterialMusic(1);
                    break;
                case R.id.tv_2:
                case R.id.iv_check_box_2:
                    isPlayVideoInAudio = false;
                    mPresenter.extractedAudio(mChangeMusicPath,2);
                    break;
                default:
                    break;
            }
        };

        tv_0.setOnClickListener(tvMusicListener);
        tv_1.setOnClickListener(tvMusicListener);
        tv_2.setOnClickListener(tvMusicListener);
        tv_3.setOnClickListener(tvMusicListener);

        check_box_0.setOnClickListener(tvMusicListener);
        check_box_1.setOnClickListener(tvMusicListener);
        check_box_2.setOnClickListener(tvMusicListener);
        check_box_3.setOnClickListener(tvMusicListener);

        listForInitBottom.add(viewForChooseMusic);
    }

    /**
     * 横竖屏切换
     */
    private void changeLandscape() {
        //横竖屏切换
        nowUiIsLandscape = !nowUiIsLandscape;
        if (!TextUtils.isEmpty(mImagePath)) {
            setImageBackSize(nowUiIsLandscape);
        } else {
            setPlayerViewSize(nowUiIsLandscape);
        }
    }

    /**默认选的时候就是横屏*/
    boolean isInitLandscape = false;
    /**
     * 设置默认视频背景
     */
    private void setDefaultVideoPlayerView() {
        //有视频的时候，初始化视频
        if (!TextUtils.isEmpty(mVideoPath)) {
            //改变默认横竖屏，需知视频宽高比
            MediaInfo mediaInfo = new MediaInfo(mVideoPath);
            mediaInfo.prepare();
            nowUiIsLandscape = !mediaInfo.isPortVideo();
            isInitLandscape = !mediaInfo.isPortVideo();
            mediaInfo.release();
            setPlayerViewSize(nowUiIsLandscape);
            initExo(mVideoPath);
        } else {
            mBinding.rlBackImage.setVisibility(View.VISIBLE);
            mBinding.relativePlayerView.setVisibility(View.INVISIBLE);
            setImageBackSize(false);
            if (!TextUtils.isEmpty(mImagePath)) {
                Glide.with(this).load(mImagePath).into(mBinding.ivBackImage);
            }
        }
        //从前一个页面设置的横竖屏判断
        if (nowUiIsLandscape) {
            new Handler().postDelayed(() -> setPlayerViewSize(nowUiIsLandscape), 500);
        }
    }

    /**
     * 进度条监听
     */
    private void setProgressBarListener() {
        mBinding.progressBarView.setProgressListener(new CreationTemplateProgressBarView.SeekBarProgressListener() {
            @Override
            public void progress(long progress, boolean isDrag) {
                LogUtil.d("OOM4", "mProgressBarViewProgress=" + progress);
                setGSYVideoProgress(progress);

                if (progress < mCutStartTime) {
                    progress = mCutStartTime;
                }

                if (progress > mCutEndTime) {
                    progress = mCutEndTime;
                }

                if (isDrag) {
                    mSeekBarViewManualDrag = false;
                }

                if (!mSeekBarViewManualDrag) {
                    mBinding.jakeFontSeekBarView.dragScrollView = false;
                    mBinding.jakeFontSeekBarView.scrollToPosition(progress);
                }
                progressBarProgress = progress;
                mPresenter.getNowPlayingTimeViewShow(progressBarProgress, mCutEndTime);
                mBinding.tvCurrentTime.setText(String.format("%ss", TimeUtils.timeParse(progress - mCutStartTime)));
            }

            @Override
            public void cutInterval(long starTime, long endTime, boolean isDirection) {
                videoToPause();
                if (starTime < mCutStartTime) {
                    mBinding.tvCurrentTime.setText(String.format("%ss", TimeUtils.timeParse(0)));
                    mCutStartTime = starTime;
                } else {
                    mCutStartTime = starTime;
                    mBinding.tvCurrentTime.setText(String.format("%ss", TimeUtils.timeParse(progressBarProgress - mCutStartTime)));
                }

                mCutEndTime = endTime;

                mBinding.tvTotal.setText(String.format("%ss", TimeUtils.timeParse(mCutEndTime - mCutStartTime)));
                mBinding.jakeFontSeekBarView.setCutStartAndEndTime(starTime, endTime);
//                stickerTimeLineOffset();
//                LogUtil.d("oom44", "musicStartTime=" + musicStartTime + "starTime=" + starTime + "musicEndTime=" + musicEndTime + "mCutStartTime=" + mCutStartTime);

                if (isDirection) {
                    mBinding.jakeFontSeekBarView.scrollToPosition(starTime);
                    //--------------ztj   解决bug拖动主进度条，素材音乐没修改的情况
                    if (musicStartTime < starTime) {
                        musicStartTime = starTime;
                        LogUtil.d("oom44", "musicStartTime=" + musicStartTime + "starTime=" + starTime + "musicEndTime=" + musicEndTime + "mCutStartTime=" + mCutStartTime);
                    }
                    //ztj  音乐向后挤 ，然后音乐就是最短位置1000+end
                    if (musicEndTime < starTime) {
                        musicEndTime = musicStartTime + 1000;
                        LogUtil.d("oom44", "音乐向后挤musicEndTime=" + musicEndTime + "musicStartTime=" + musicStartTime);
                    }

                } else {
                    LogUtil.d("oom444", "xx=");
                    mBinding.jakeFontSeekBarView.scrollToPosition(endTime);
                }

                mPresenter.getNowPlayingTimeViewShow(progressBarProgress, mCutEndTime);
            }

            @Override
            public void onTouchEnd() {
                videoToPause();
                mPresenter.getNowPlayingTimeViewShow(progressBarProgress, mCutEndTime);
            }
        });
    }

    /**
     *设置播放器尺寸,如果不设置的话会出现黑屏，因为外面嵌套了ScrollView
     * 横竖屏切换的时候例外2层都需要修改尺寸
     */
    private float percentageH = 0;

    private void setPlayerViewSize(boolean isLandscape) {

        videoToPause();
        //切换横竖屏
        LinearLayout.LayoutParams relativeLayoutParams = (LinearLayout.LayoutParams) mBinding.exoPlayer.getLayoutParams();
        float oriRatio = 9f / 16f;

        if (isLandscape) {
            //横屏的情况
            mBinding.scrollView.post(() -> {
                int oriWidth = mBinding.llSpace.getWidth();
                RelativeLayout.LayoutParams relativeLayoutParams2 = (RelativeLayout.LayoutParams) mBinding.scrollView.getLayoutParams();
                relativeLayoutParams2.width = oriWidth;
                relativeLayoutParams2.height = Math.round(1f * oriWidth * oriRatio);
                mBinding.scrollView.setLayoutParams(relativeLayoutParams2);
                relativeLayoutParams.width = oriWidth;
                if (isInitLandscape) {
                    relativeLayoutParams.height = Math.round(1f * oriWidth * oriRatio);
                } else {
                    relativeLayoutParams.height = Math.round(1f * oriWidth / oriRatio);
                }
                mBinding.exoPlayer.setLayoutParams(relativeLayoutParams);
                //设置预览编辑界面
                mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams2);
                RelativeLayout.LayoutParams contentParam = (RelativeLayout.LayoutParams) mBinding.relativeContentAllContent2.getLayoutParams();
                contentParam.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                contentParam.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                contentParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                mBinding.relativeContentAllContent2.setLayoutParams(contentParam);

            });
        } else {
            //横屏模式下切换到了竖屏
            mBinding.scrollView.post(() -> {
                RelativeLayout.LayoutParams relativeLayoutParams2 = (RelativeLayout.LayoutParams) mBinding.scrollView.getLayoutParams();

                int height = mBinding.llSpace.getHeight();
                relativeLayoutParams2.height = height;
                relativeLayoutParams2.width = Math.round(1f * height * oriRatio);
                mBinding.scrollView.setLayoutParams(relativeLayoutParams2);

                relativeLayoutParams.width = Math.round(1f * height * oriRatio);
                relativeLayoutParams.height = height;
                mBinding.exoPlayer.setLayoutParams(relativeLayoutParams);
                //设置预览编辑界面
                mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams2);
                RelativeLayout.LayoutParams contentParam = (RelativeLayout.LayoutParams) mBinding.relativeContentAllContent2.getLayoutParams();
                contentParam.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                contentParam.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                contentParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                mBinding.relativeContentAllContent2.setLayoutParams(contentParam);

            });
        }

        mBinding.scrollView.setOnScrollListener(scrollY -> {
            int totalHeight = mBinding.scrollView.getChildAt(0).getHeight();
            percentageH = scrollY / (float) totalHeight;
            LogUtil.d("OOM3", "percentageH" + percentageH);
        });

        new Handler().postDelayed(() -> {
            if (isLandscape) {
                int height = Math.round(1f * mBinding.llSpace.getWidth() / oriRatio);
                mBinding.scrollView.scrollTo(0, height / 2 - mBinding.scrollView.getHeight() / 2);
            }
        }, 500);
    }

    private void setImageBackSize(boolean isLandscape) {

        videoToPause();
        //切换横竖屏
        ViewGroup.LayoutParams relativeLayoutParams = mBinding.ivBackImage.getLayoutParams();
        float oriRatio = 9f / 16f;

        if (isLandscape) {
            //横屏的情况
            mBinding.svBackImage.post(() -> {
                int oriWidth = mBinding.llSpace.getWidth();
                int spaceHeight = mBinding.llSpace.getHeight();

                RelativeLayout.LayoutParams relativeLayoutParams2 = (RelativeLayout.LayoutParams) mBinding.svBackImage.getLayoutParams();
                relativeLayoutParams2.width = oriWidth;
                relativeLayoutParams2.height = Math.round(1f * oriWidth * oriRatio);
                mBinding.svBackImage.setLayoutParams(relativeLayoutParams2);

                relativeLayoutParams.width = oriWidth;
                relativeLayoutParams.height = Math.round(1f * oriWidth / oriRatio);
                mBinding.ivBackImage.setLayoutParams(relativeLayoutParams);

                //设置预览编辑界面
                mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams2);
                RelativeLayout.LayoutParams contentParam = (RelativeLayout.LayoutParams) mBinding.relativeContentAllContent2.getLayoutParams();
                contentParam.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                contentParam.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                contentParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);

                mBinding.relativeContentAllContent2.setLayoutParams(contentParam);
            });


        } else {
            //横屏模式下切换到了竖屏
            mBinding.svBackImage.post(() -> {
                RelativeLayout.LayoutParams relativeLayoutParams2 = (RelativeLayout.LayoutParams) mBinding.svBackImage.getLayoutParams();

                int height = mBinding.llSpace.getHeight();
                int width = mBinding.llSpace.getWidth();


                relativeLayoutParams2.height = height;
                relativeLayoutParams2.width = Math.round(1f * height * oriRatio);
                mBinding.svBackImage.setLayoutParams(relativeLayoutParams2);

                relativeLayoutParams.width = Math.round(1f * height * oriRatio);
                relativeLayoutParams.height = height;
                mBinding.ivBackImage.setLayoutParams(relativeLayoutParams);
                //设置预览编辑界面
                mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams2);
                RelativeLayout.LayoutParams contentParam = (RelativeLayout.LayoutParams) mBinding.relativeContentAllContent2.getLayoutParams();
                contentParam.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                contentParam.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                contentParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);

                mBinding.relativeContentAllContent2.setLayoutParams(contentParam);

            });
        }

        mBinding.svBackImage.setOnScrollListener(scrollY -> {
            int totalHeight = mBinding.svBackImage.getChildAt(0).getHeight();
            int svBackImageHeight = mBinding.svBackImage.getHeight();
            percentageH = scrollY / (float) (totalHeight - svBackImageHeight);
        });

        new Handler().postDelayed(() -> {
            if (isLandscape) {
                int height = Math.round(1f * mBinding.llSpace.getWidth() / oriRatio);
                mBinding.svBackImage.scrollTo(0, height / 2 - mBinding.svBackImage.getHeight() / 2);
            }
        }, 500);
    }

    /**
     * description ：设置预览界面大小
     * date: ：2019/11/18 20:24
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    private void initViewLayerRelative() {
        ViewGroup.LayoutParams relativeLayoutParams = mBinding.idVviewRealtimeGllayout.getLayoutParams();
        ViewGroup.LayoutParams relativeLayoutParams2 = mBinding.relativeContentAllContent2.getLayoutParams();

        float oriRatio;
        oriRatio = 9f / 16f;
        //保证获得mContainer大小不为0
        mBinding.idVviewRealtimeGllayout.post(() -> {
            int oriHeight = nowUiIsLandscape ? mBinding.idVviewRealtimeGllayout.getWidth() : mBinding.idVviewRealtimeGllayout.getHeight();
            relativeLayoutParams.width = Math.round(1f * oriHeight * oriRatio);
            relativeLayoutParams.height = oriHeight;
            mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams);
            mBinding.relativeContentAllContent2.setLayoutParams(relativeLayoutParams);
            mBinding.relativeContentAllContent2.setGravity(Gravity.CENTER_HORIZONTAL);
            mBinding.relativeContentAllContent.setGravity(Gravity.CENTER_HORIZONTAL);
            relativeLayoutParams2.width = relativeLayoutParams.width;
            relativeLayoutParams2.height = relativeLayoutParams.height;
            mBinding.idVviewRealtimeGllayout.setLayoutParams(relativeLayoutParams2);
            mBinding.llContentPatents.setGravity(Gravity.CENTER_HORIZONTAL);
        });

        setVideoBJOrImageBJPath();
    }

    /**
     * 设置视频背景或者图片背景
     */
    public void setVideoBJOrImageBJPath() {
        if (!TextUtils.isEmpty(mVideoPath)) {
            MediaInfo mediaInfo = new MediaInfo(mVideoPath);
            mediaInfo.prepare();
            allVideoDuration = (long) (mediaInfo.vDuration * 1000);
            mediaInfo.release();
        } else {
            allVideoDuration = 5 * 1000;
        }

        mBinding.progressBarView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mBinding.progressBarView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mCutStartTime = 0;
                mCutEndTime = allVideoDuration;
                musicStartTime = 0;
                musicEndTime = allVideoDuration;
                mBinding.tvTotal.setText(TimeUtils.timeParse(mCutEndTime - mCutStartTime) + "s");
                mBinding.progressBarView.addProgressBarView(allVideoDuration, !TextUtils.isEmpty(mVideoPath) ? mVideoPath : mImagePath);
            }
        });
        mBinding.jakeFontSeekBarView.setGreenScreen(false);
    }

    private void initExo(String videoPath) {
        if (TextUtils.isEmpty(videoPath)) {
            return;
        }

        exoPlayer = new SimpleExoPlayer.Builder(this)
                .build();
        mBinding.exoPlayer.setPlayer(exoPlayer);
        //不使用控制器
        mBinding.exoPlayer.setUseController(false);
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    case Player.STATE_READY:
                        break;
                    case Player.STATE_ENDED:
                        videoToStart();
                        break;
                    case Player.STATE_BUFFERING:
                    case Player.STATE_IDLE:
                    default:
                        break;
                }
            }
        });

        mediaSource = new ProgressiveMediaSource.Factory(
                new DefaultDataSourceFactory(JadeFontMakeActivity.this, "exoplayer-codelab")).
                createMediaSource(new MediaItem.Builder()
                        .setUri(Uri.fromFile(new File(videoPath))).build());

        exoPlayer.setMediaSource(mediaSource, true);
        exoPlayer.prepare();

        pauseExoPlayer();
    }

    private void setGSYVideoProgress(long progress) {
        LogUtil.d("OOM", "videoProgress=" + progress);
        if (!isPlaying) {
            seekToVideo(progress);
            seekToMusic(progress);
        }
    }

    /**
     * 点击播放按键时
     */
    private void onPlayClick() {
        if (!DoubleClick.getInstance().isFastZDYDoubleClick(500)) {
            if (isPlaying) {
                pauseBgmMusic();
                isIntoPause = false;
                isPlayComplete = false;
                videoToPause();//点击播放暂定
                isPlaying = false;
                nowStateIsPlaying(false);
            } else {
                nowStateIsPlaying(true);
                if (!TextUtils.isEmpty(mVideoPath)) {
                    seekToVideo(mCutStartTime);
                    seekToMusic(mCutStartTime);
                    if (isPlayComplete) {
                        videoPlay();
                        isIntoPause = false;
                    } else {
                        if (isInitVideoLayer) {
                            if (!isIntoPause) {
                                videoPlay();
                            } else {
                                videoPlay();
                                isIntoPause = false;
                                isInitVideoLayer = true;
                            }
                        } else {
                            isIntoPause = false;
                            isInitVideoLayer = true;
                            videoPlay();
                        }
                    }
                } else {
                    //播放背景音乐,如果有背景音乐且是第一次初始化
                    if (!TextUtils.isEmpty(bgmPath) && musicEndTime == 0) {
                        if (bgmPlayer != null) {
                            //继续播放
                            bgmPlayer.start();
                            LogUtil.d("playBGMMusic", " bgmPlayer.start()");
                        } else {
                            seekToVideo(mCutStartTime);
                            seekToMusic(mCutStartTime);
                            LogUtil.d("playBGMMusic", "animIsComplate");
                            playBGMMusic();
                        }
                    }
                }
                isPlaying = true;
                startTimer();
            }
            mSeekBarViewManualDrag = false;
        }
    }

    private void videoToStart() {
        isPlayComplete = true;
        endTimer();
        isPlaying = false;
        videoToPause();
        seekToVideo(mCutStartTime);
        seekToMusic(mCutStartTime);
        nowStateIsPlaying(false);
    }

    private void videoToPause() {
        pauseExoPlayer();
        isPlaying = false;
        endTimer();
        nowStateIsPlaying(false);
    }

    /**
     * 释放资源
     */
    private void videoStop() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
        }
    }

    /**
     * 开始播放
     * 背景音乐的逻辑全部放在了保存的时候去了
     * 这里只控制是否播放背景视频
     */
    private void videoPlay() {
        isPlaying = true;
        isPlayComplete = false;
        if (exoPlayer != null) {
            LogUtil.d("video", "play");
            if (!TextUtils.isEmpty(bgmPath)) {
                exoPlayer.setVolume(0f);
            } else {
                if (!TextUtils.isEmpty(mVideoPath)) {
                    if (noBgMusicPlay) {
                        exoPlayer.setVolume(0f);
                    } else {
                        exoPlayer.setVolume(1f);
                    }
                } else {
                    exoPlayer.setVolume(1f);
                }
            }
            if (getCurrentPos() >= mCutEndTime) {
                exoPlayer.seekTo(mCutStartTime);
            } else if (getCurrentPos() < mCutStartTime) {
                exoPlayer.seekTo(mCutStartTime);
            }
            exoPlayer.setPlayWhenReady(true);
        }
        startTimer();
    }

    private void nowStateIsPlaying(boolean isPlaying) {
        if (isPlaying) {
            mBinding.ivPlay.setImageResource(R.mipmap.pause);
        } else {
            mBinding.ivPlay.setImageResource(R.mipmap.iv_play_creation);
        }
    }

    /**
     * 获取当前进度
     */
    private long getCurrentPos() {
        return exoPlayer != null ? exoPlayer.getCurrentPosition() : 0;
    }

    private void pauseExoPlayer() {
        if (exoPlayer != null) {
            LogUtil.d("video", "videoPause");
            exoPlayer.setPlayWhenReady(false);
        }
    }

    private void seekToVideo(long to) {
        if (exoPlayer != null) {
            exoPlayer.seekTo(to);
        }
    }

    private void seekToMusic(long to) {
        if (bgmPlayer != null) {
            bgmPlayer.seekTo((int) to);
        }
    }

    private void playBGMMusic() {
        bgmPlayer = new MediaPlayer();
        try {
            bgmPlayer.setDataSource(bgmPath);
            bgmPlayer.prepare();
            bgmPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void pauseBgmMusic() {
        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.pause();
        }
    }

    /**
     * 拖动后时候可以播放音乐
     */

    private void bjMusicControl() {
        if (!isEndDestroy) {
            LogUtil.d("playBGMMusic", "bjMusicControl");
            if (!TextUtils.isEmpty(bgmPath)) {
                if (musicEndTime != 0) {
                    float needMusicStartTime = musicStartTime;
                    float needTime = totalPlayTime;
                    LogUtil.d("playBGMMusic", "totalPlayTime=" + totalPlayTime + "mCutStartTime=" + mCutStartTime + "needTime=" + needTime + "musicStartTime=" + musicStartTime + "needMusicStartTime=" + needMusicStartTime);
                    if (needTime > musicEndTime || needTime < needMusicStartTime) {
                        LogUtil.d("playBGMMusic2", "需要暂停音乐");
                        isNeedPlayBjMusic = false;
                        pauseBgmMusic();
                    } else {
                        if (!isNeedPlayBjMusic) {
                            LogUtil.d("playBGMMusic2", "播放音乐");
                            LogUtil.d("playBGMMusic2", "totalPlayTime=" + totalPlayTime + "mCutStartTime=" + mCutStartTime + "needTime=" + needTime + "musicStartTime=" + musicStartTime + "needMusicStartTime=" + needMusicStartTime);
                            playBjMusic();
                        }
                        isNeedPlayBjMusic = true;
                    }
                } else {
                    LogUtil.d("playBGMMusic", "musicEndTime=" + musicEndTime);
                    if (!isNeedPlayBjMusic) {
                        LogUtil.d("playBGMMusic", "播放音乐");
                        playBjMusic();
                    }
                    isNeedPlayBjMusic = true;
                }
            } else {
                LogUtil.d("playBGMMusic", "bgmPath==null");
            }
        }

    }


    private void playBjMusic() {
        LogUtil.d("playBGMMusic", "playBjMusic");
        if (!TextUtils.isEmpty(bgmPath)) {
            if (bgmPlayer != null) {
                bgmPlayer.start();
                seekToMusic(mCutStartTime);
            } else {
                seekToMusic(mCutStartTime);
                playBGMMusic();
            }
        }
    }

    private Timer timer;
    private TimerTask task;
    private long nowTime = 5;
    /**自己计算的播放时间*/
    private long totalPlayTime;
    private boolean isNeedPlayBjMusic = false;

    private void startTimer() {

        if (musicChooseIndex == 2) {
            musicStartTime = 0;
            musicEndTime = allVideoDuration;
        }

        isEndDestroy = false;
        nowTime = 5;
        totalPlayTime = mCutStartTime;
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }

        timer = new Timer();

        task = new TimerTask() {
            @Override
            public void run() {
                creationTimerTask();
            }
        };
        timer.schedule(task, 0, 5);
    }

    private void creationTimerTask() {
        totalPlayTime = totalPlayTime + 5;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bjMusicControl();
                if (!TextUtils.isEmpty(mVideoPath)) {
                    if (isPlaying) {
                        long nowCurrentPos = getCurrentPos();
                        if (nowCurrentPos < 0) {
                            nowCurrentPos = 0;
                        }
                        if (nowCurrentPos >= mCutEndTime) {
                            exoPlayer.seekTo(mCutStartTime);
                            videoToPause();
                        } else if (nowCurrentPos < mCutStartTime) {
                            exoPlayer.seekTo(mCutStartTime);
                            videoToPause();
                        }
                    }
                    if (mBinding != null) {
                        mBinding.progressBarView.scrollToPosition(getCurrentPos());
                    }
                } else {
                    //图片背景
                    nowTime = nowTime + 5;

                    if (nowTime >= mCutEndTime) {
                        nowTime = mCutStartTime;
                        isPlayComplete = true;
                        endTimer();
                        isPlaying = false;
                        nowStateIsPlaying(false);
                    } else if (nowTime < mCutStartTime) {
                        nowTime = mCutStartTime;
                    }

                    if (mBinding.progressBarView != null) {
                        mBinding.progressBarView.scrollToPosition(nowTime);
                    }

                }
            }
        });
    }

    /**
     * 关闭timer 和task
     */
    private void endTimer() {
        isNeedPlayBjMusic = false;
        isEndDestroy = true;
        destroyTimer();
        if (bgmPlayer != null) {
            bgmPlayer.stop();
            bgmPlayer = null;
        }
    }

    /**
     * 严防内存泄露
     **/
    private void destroyTimer() {
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public void progress(long progress, boolean manualDrag) {
        mSeekBarViewManualDrag = manualDrag;
        if (manualDrag) {
            mBinding.progressBarView.scrollToPosition(progress);
        }
    }

    @Override
    public void manualDrag(boolean manualDrag) {
        mSeekBarViewManualDrag = manualDrag;
        videoToPause();
        //做玉体字view的显示隐藏逻辑判断
        mPresenter.getNowPlayingTimeViewShow(progressBarProgress, mCutEndTime);
    }

    @Override
    public void timelineChange(long startTime, long endTime, String id, boolean isSubtitle) {
        //玉体字view的起止时间修改和玉体字view的显示隐藏逻辑判断
        mPresenter.getNowPlayingTimeViewShow(progressBarProgress, mCutEndTime);
    }

    @Override
    public void currentViewSelected(String id) {

    }

    @Override
    public void trackPause() {
        videoToPause();
    }

    @Override
    public void startIdentifySubtitle() {
        videoToPause();
        if (musicChooseIndex == 0) {
            mPresenter.startIdentify(true, mVideoPath, "");
        }
        if (musicChooseIndex == 2) {
            mPresenter.startIdentify(false, "", bgmPath);
        }
    }


    @Override
    public void identifySubtitle(List<SubtitleEntity> subtitles, boolean isVideoInAudio, String audioPath) {
        if (mBinding.jakeFontSeekBarView.subtitleIndex != -1) {
            //清空已识别的字幕  删除字幕时间轴和字幕的玉体字的view
            mBinding.jakeFontSeekBarView.deleteSubtitleView(mBinding.jakeFontSeekBarView.subtitleIndex);
        }
        mBinding.jakeFontSeekBarView.addTemplateMaterialItemView(allVideoDuration, "", 0, 0, false,
                "", -1, subtitles, mJadeFontViewIndex, mBinding.progressBarView.progressTotalWidth);
        mBinding.jakeFontSeekBarView.setCutEndTime(mCutEndTime);
        mBinding.jakeFontSeekBarView.scrollToTheBottom();
        mJadeFontViewIndex++;
    }

    @Override
    public void getBgmPath(String bgmPath) {
        this.bgmPath = bgmPath;
        if (TextUtils.isEmpty(bgmPath)) {
            if (isPlaying) {
                videoToPause();
            }
        } else {
            if (isPlaying) {
                if (!TextUtils.isEmpty(bgmPath)) {
                    if (exoPlayer != null) {
                        exoPlayer.setVolume(0f);
                    }
                    pauseBgmMusic();
                    playBGMMusic();
                    if (bgmPlayer != null) {
                        if (exoPlayer != null) {
                            bgmPlayer.seekTo((int) getCurrentPos());
                        } else {
                            bgmPlayer.seekTo((int) totalPlayTime);
                        }
                    }
                } else {
                    if (exoPlayer != null) {
                        exoPlayer.setVolume(1f);
                    }
                    pauseBgmMusic();
                }
            } else {
                videoToStart();
            }
        }
    }

    boolean noBgMusicPlay = false;
    @Override
    public void clearCheckBox() {
        noBgMusicPlay = true;
        check_box_0.setImageResource(R.mipmap.template_btn_unselected);
        check_box_1.setImageResource(R.mipmap.template_btn_unselected);
        check_box_2.setImageResource(R.mipmap.template_btn_unselected);
        check_box_3.setImageResource(R.mipmap.template_btn_unselected);
    }

    @Override
    public void chooseCheckBox(int i) {
        switch (i) {
            case 0:
                noBgMusicPlay = false;
                check_box_0.setImageResource(R.mipmap.template_btn_selected);
                break;
            case 1:
                check_box_1.setImageResource(R.mipmap.template_btn_selected);
                break;
            case 2:
                check_box_2.setImageResource(R.mipmap.template_btn_selected);
                break;
            case 3:
                check_box_3.setImageResource(R.mipmap.template_btn_selected);
                break;
            default:
                break;
        }
    }

    @Subscribe
    public void onEventMainThread(CutSuccess cutSuccess) {
        mChangeMusicPath = cutSuccess.getFilePath();
        mPresenter.setExtractedAudioBjMusicPath(mChangeMusicPath);
        musicChooseIndex = 2;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isIntoPause && exoPlayer != null) {
            exoPlayer.prepare(mediaSource, true, false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    pauseExoPlayer();
                    destroyTimer();
                }
            }, 200);
            isIntoPause = false;
        }
    }

    @Override
    protected void onPause() {
        videoToPause();
        isIntoPause = true;
        super.onPause();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        destroyTimer();
        videoStop();
        if (bgmPlayer != null) {
            bgmPlayer.pause();
            bgmPlayer.release();
        }
        super.onDestroy();
    }
}
