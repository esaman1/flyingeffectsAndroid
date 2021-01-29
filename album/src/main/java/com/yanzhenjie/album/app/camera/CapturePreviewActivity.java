package com.yanzhenjie.album.app.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ClippingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.util.Util;
import com.yanzhenjie.album.R;

import java.io.File;
import java.lang.ref.SoftReference;

/**
 * 拍摄预览页面
 *
 * @author shijiaqi
 * 2020.6.15CapturePreviewActivity
 */
public class CapturePreviewActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CapturePreviewActivity";

    public static final String KEY_PREVIEW_URL = "preview_url";
    public static final String KEY_MUSIC_URL = "music_url";
    public static final String KEY_PREVIEW_VIDEO = "preview_video";
    public static final String KEY_PREVIEW_TITLE = "preview_title";
    public static final String KEY_PREVIEW_DURATION = "preview_duration";

    public static final int REQ_PREVIEW = 1000;
    private SimpleExoPlayer player;
    private SimpleExoPlayer musicPlayer;

    private AppCompatImageView mIvOk;
    private AppCompatImageView mIvClose;
    private PlayerView mPlayerView;
    private PhotoView mPhotoView;
    private AppCompatImageView mIvCancel;
    private AppCompatImageView mIvPlay;
    private AppCompatTextView mTvPlayTime;
    //正在播放
    private boolean mPlaying = false;
    private String mPreviewUrl;
    private String mTitle;
    private float mVideoDurationTime;
    private PlayHandler mPlayHandler;

    /**
     * 为其他页面提供跳转到这里需要的参数指引
     *
     * @param activity   activity
     * @param previewUrl 要预览的url
     * @param musicUrl   音乐url
     * @param isVideo    是否为视频
     * @param title      标题
     * @param duration   时长
     */
    public static void startActivityForResult(Activity activity, String previewUrl, String musicUrl, boolean isVideo, String title, float duration) {
        Intent intent = new Intent(activity, CapturePreviewActivity.class);
        intent.putExtra(KEY_PREVIEW_URL, previewUrl);
        intent.putExtra(KEY_MUSIC_URL, musicUrl);
        intent.putExtra(KEY_PREVIEW_VIDEO, isVideo);
        intent.putExtra(KEY_PREVIEW_TITLE, title);
        intent.putExtra(KEY_PREVIEW_DURATION, duration);
        activity.startActivityForResult(intent, REQ_PREVIEW);
        activity.overridePendingTransition(0, 0);
    }

    /**
     * 更新视频播放时间
     */
    static class PlayHandler extends Handler {
        private final SoftReference<CapturePreviewActivity> mActivity;

        PlayHandler(CapturePreviewActivity activity) {
            mActivity = new SoftReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CapturePreviewActivity activity = mActivity.get();
            if (activity!=null){
                activity.setProgressText(activity.player.getCurrentPosition()
                        , activity.player.getDuration());
                sendEmptyMessageDelayed(0, 300);
            }
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_activity_capture_preview);
        mPreviewUrl = getIntent().getStringExtra(KEY_PREVIEW_URL);
        String musicUrl = getIntent().getStringExtra(KEY_MUSIC_URL);
        mTitle = getIntent().getStringExtra(KEY_PREVIEW_TITLE);
        mVideoDurationTime = getIntent().getFloatExtra(KEY_PREVIEW_DURATION, 0);
        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultRenderersFactory(this), new DefaultTrackSelector(), new DefaultLoadControl());
        musicPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultRenderersFactory(this), new DefaultTrackSelector(), new DefaultLoadControl());
        mPlayHandler = new PlayHandler(this);

        Log.d(TAG, "onCreate: mVideoDurationTime = " + mVideoDurationTime);
        boolean isVideo = getIntent().getBooleanExtra(KEY_PREVIEW_VIDEO, true);
        initView();
        setOnClickListener();

        if (isVideo) {
            Uri videoUri = previewVideo(mPreviewUrl);
            Uri musicUri = previewVideo(musicUrl);
            prepareMediaSource(videoUri, musicUri);
        } else {
            previewImage(mPreviewUrl);
        }
    }

    /**
     * 通过获取到的uri准备mediasource
     *
     * @param videoUri 视频的uri
     * @param musicUri 音频的uri
     */
    private void prepareMediaSource(Uri videoUri, Uri musicUri) {
        ProgressiveMediaSource.Factory factory = new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(this, Util.getUserAgent(this, getPackageName())));
        ProgressiveMediaSource mediaSource = factory.createMediaSource(videoUri);
        ProgressiveMediaSource musicSource = factory.createMediaSource(musicUri);
        long durationUs = (long) (mVideoDurationTime * 1_000_000);
        Log.d(TAG, "prepareMediaSource: durationUs = " + durationUs);
        ClippingMediaSource clippingMediaSource = new ClippingMediaSource(musicSource, durationUs);
        player.prepare(mediaSource);
        musicPlayer.prepare(clippingMediaSource);
        play();
        mPlayHandler.sendEmptyMessage(0);
        //循环模式：单一资源无限循环
        player.setRepeatMode(Player.REPEAT_MODE_ONE);
        musicPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
        player.setVolume(0);
        mPlayerView.setPlayer(player);
        //监听播放状态
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                mPlaying = playWhenReady;
                if (playWhenReady) {
                    mIvPlay.setImageResource(R.drawable.album_icon_pause);
                } else {
                    mIvPlay.setImageResource(R.drawable.album_icon_play);
                }
            }
        });
    }

    /**
     * 开始播放
     */
    private void play() {
        mPlaying = true;
        player.setPlayWhenReady(true);
        musicPlayer.setPlayWhenReady(true);
    }

    /**
     * 停止播放
     */
    private void stop() {
        mPlaying = false;
        player.setPlayWhenReady(false);
        musicPlayer.setPlayWhenReady(false);
    }

    /**
     * player十分占用资源，要及时释放
     */
    private void releaseAllPlayer() {
        mPlaying = false;
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop(true);
            player.release();
        }
        if (musicPlayer != null) {
            musicPlayer.setPlayWhenReady(false);
            musicPlayer.stop(true);
            musicPlayer.release();
        }
    }

    private void setOnClickListener() {
        mIvOk.setOnClickListener(this);
        mIvClose.setOnClickListener(this);
        mIvPlay.setOnClickListener(this);
        mIvCancel.setOnClickListener(this);
    }

    private void initView() {
        mIvOk = findViewById(R.id.iv_ok);
        mIvClose = findViewById(R.id.iv_close);
        mPlayerView = findViewById(R.id.player_view);
        mPhotoView = findViewById(R.id.photo_view);
        mIvCancel = findViewById(R.id.iv_cancel);
        mIvPlay = findViewById(R.id.iv_play);
        mTvPlayTime = findViewById(R.id.tv_play_time);
        AppCompatTextView tvTitle = findViewById(R.id.tv_model_title);
        tvTitle.setText(String.format("模板：%s", mTitle));
    }

    /**
     * 预览照片
     *
     * @param previewUrl url
     */
    private void previewImage(String previewUrl) {
        mPhotoView.setVisibility(View.VISIBLE);
        Glide.with(this).load(previewUrl).into(mPhotoView);
    }

    /**
     * 通过url获取uri
     *
     * @param previewUrl url
     * @return uri
     */
    private Uri previewVideo(String previewUrl) {
        mPlayerView.setVisibility(View.VISIBLE);

        Uri uri = null;
        File file = new File(previewUrl);
        if (file.exists()) {
            DataSpec dataSpec = new DataSpec(Uri.fromFile(file));
            FileDataSource fileDataSource = new FileDataSource();
            try {
                fileDataSource.open(dataSpec);
                uri = fileDataSource.getUri();
            } catch (FileDataSource.FileDataSourceException e) {
                e.printStackTrace();
            }
        } else {
            uri = Uri.parse(previewUrl);
        }
        return uri;
    }

    /**
     * 设置倒计时的文字
     *
     * @param currentPosition 当前进度
     * @param duration        总时长
     */
    public void setProgressText(long currentPosition, long duration) {
        Log.d(TAG, "setProgressText: currentPosition = " + currentPosition + "duration = " + duration);
        //因为在测试的过程中，偶尔会出现duration很大的情况，故加个保险
        if (duration < 1000000 && duration > -1000000) {
            int cpInt = (int) ((currentPosition + 500) / 1000);
            int durationInt = (int) ((duration + 500) / 1000);
            Log.d(TAG, "setProgressText: cpInt = " + cpInt + "durationInt = " + durationInt);
            String progressStr = cpInt + "s / " + durationInt + "s";
            SpannableStringBuilder spannable = new SpannableStringBuilder(progressStr);
            if (cpInt >= 0 && cpInt < 10) {
                spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#5496FF")), 0, 2,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (cpInt >= 10 && cpInt < 100) {
                spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#5496FF")), 0, 3,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#5496FF")), 0, 4,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            mTvPlayTime.setText(spannable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            play();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseAllPlayer();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_close) {
            capturePreviewCancel();
        } else if (id == R.id.iv_ok) {
            setResult(RESULT_OK, new Intent());
            finish();
        } else if (id == R.id.iv_cancel) {
            capturePreviewCancel();
        } else if (id == R.id.iv_play) {
            switchPlayBtn();
        }
    }

    /**
     * 取消预览的同时，删除视频文件，并更新图库
     */
    private void capturePreviewCancel() {
        File file = new File(mPreviewUrl);
        boolean isDelete = file.delete();
        if (isDelete) {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file)));
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        capturePreviewCancel();
    }

    private void switchPlayBtn() {
        if (mPlaying) {
            stop();
            mIvPlay.setImageResource(R.drawable.album_icon_play);
        } else {
            play();
            mIvPlay.setImageResource(R.drawable.album_icon_pause);
        }
    }
}
