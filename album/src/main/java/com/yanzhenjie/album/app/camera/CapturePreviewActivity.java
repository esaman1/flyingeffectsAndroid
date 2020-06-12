package com.yanzhenjie.album.app.camera;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.util.Util;
import com.yanzhenjie.album.R;

import java.io.File;

public class CapturePreviewActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String KEY_PREVIEW_URL = "preview_url";
    public static final String KEY_PREVIEW_VIDEO = "preview_video";
    public static final int REQ_PREVIEW = 1000;
    private SimpleExoPlayer player;

    private AppCompatImageView mIvOk;
    private AppCompatImageView mIvClose;
    private PlayerView mPlayerView;
    private PhotoView mPhotoView;
    private AppCompatImageView mIvCancel;
    private AppCompatImageView mIvPlay;
    //正在播放
    private boolean mPlaying = false;
    private String mPreviewUrl;


    public static void startActivityForResult(Activity activity, String previewUrl, boolean isVideo) {
        Intent intent = new Intent(activity, CapturePreviewActivity.class);
        intent.putExtra(KEY_PREVIEW_URL, previewUrl);
        intent.putExtra(KEY_PREVIEW_VIDEO, isVideo);
        activity.startActivityForResult(intent, REQ_PREVIEW);
        activity.overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_activity_capture_preview);
        mPreviewUrl = getIntent().getStringExtra(KEY_PREVIEW_URL);
        boolean isVideo = getIntent().getBooleanExtra(KEY_PREVIEW_VIDEO, false);
        initView();
        setOnClickListener();


        if (isVideo) {
            previewVideo(mPreviewUrl);
        } else {
            previewImage(mPreviewUrl);
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
    }

    private void previewImage(String previewUrl) {
        mPhotoView.setVisibility(View.VISIBLE);
        Glide.with(this).load(previewUrl).into(mPhotoView);
    }

    private void previewVideo(String previewUrl) {
        mPlayerView.setVisibility(View.VISIBLE);
        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultRenderersFactory(this), new DefaultTrackSelector(), new DefaultLoadControl());

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
        ProgressiveMediaSource.Factory factory = new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(this, Util.getUserAgent(this, getPackageName())));
        ProgressiveMediaSource mediaSource = factory.createMediaSource(uri);
        player.prepare(mediaSource);
        mPlaying = true;
        player.setPlayWhenReady(true);
        mPlayerView.setPlayer(player);

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

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            mPlaying = false;
            player.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            mPlaying = true;
            player.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            mPlaying = false;
            player.setPlayWhenReady(false);
            player.stop(true);
            player.release();
        }
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

    private void capturePreviewCancel() {
        File file = new File(mPreviewUrl);
        boolean isDelete = file.delete();
        if (isDelete){
            // 最后通知图库更新
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
            mPlaying = false;
            player.setPlayWhenReady(false);
            mIvPlay.setImageResource(R.drawable.album_icon_play);
        } else {
            mPlaying = true;
            player.setPlayWhenReady(true);
            mIvPlay.setImageResource(R.drawable.album_icon_pause);
        }
    }
}
