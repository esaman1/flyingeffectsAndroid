package com.flyingeffects.com.ui.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.flyingeffects.com.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.button.MaterialButton;

import java.io.File;

public class CapturePreviewActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String KEY_PREVIEW_URL = "preview_url";
    public static final String KEY_PREVIEW_VIDEO = "preview_video";
    public static final String KEY_PREVIEW_BTNTEXT = "preview_btntext";
    public static final int REQ_PREVIEW = 1000;
    private SimpleExoPlayer player;

    private MaterialButton mActionOk;
    private AppCompatImageView mActionClose;
    private PlayerView mPlayerView;
    private PhotoView mPhotoView;


    public static void startActivityForResult(Activity activity, String previewUrl, boolean isVideo, String btnText) {
        Intent intent = new Intent(activity, CapturePreviewActivity.class);
        intent.putExtra(KEY_PREVIEW_URL, previewUrl);
        intent.putExtra(KEY_PREVIEW_VIDEO, isVideo);
        intent.putExtra(KEY_PREVIEW_BTNTEXT, btnText);
        activity.startActivityForResult(intent, REQ_PREVIEW);
        activity.overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_preview);
        String previewUrl = getIntent().getStringExtra(KEY_PREVIEW_URL);
        boolean isVideo = getIntent().getBooleanExtra(KEY_PREVIEW_VIDEO, false);
        String btnText = getIntent().getStringExtra(KEY_PREVIEW_BTNTEXT);

        mActionOk = findViewById(R.id.action_ok);
        mActionClose = findViewById(R.id.action_close);
        mPlayerView = findViewById(R.id.player_view);
        mPhotoView = findViewById(R.id.photo_view);

        if (TextUtils.isEmpty(btnText)) {
            mActionOk.setVisibility(View.GONE);
        } else {
            mActionOk.setVisibility(View.VISIBLE);
            mActionOk.setText(btnText);
            mActionOk.setOnClickListener(this);
        }

        mActionClose.setOnClickListener(this);

        if (isVideo) {
            previewVideo(previewUrl);
        } else {
            previewImage(previewUrl);
        }
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
        player.setPlayWhenReady(true);
        mPlayerView.setPlayer(player);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop(true);
            player.release();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.action_close) {
            finish();
        } else if (v.getId() == R.id.action_ok) {
            setResult(RESULT_OK, new Intent());
            finish();
        }
    }

}
