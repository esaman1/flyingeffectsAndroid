package com.yanzhenjie.album.app.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.FocusMeteringResult;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.core.SurfaceOrientedMeteringPointFactory;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.ZoomState;
import androidx.camera.core.impl.VideoCaptureConfig;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.common.util.concurrent.ListenableFuture;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.R;
import com.yanzhenjie.album.widget.AlbumFocusImageView;
import com.yanzhenjie.album.widget.CameraXPreview;
import com.yanzhenjie.album.widget.LoadingDialog;
import com.yanzhenjie.album.widget.RecordView;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;


/**
 * ????????????
 *
 * @author shijiaqi
 * 2020.6.15
 */
public class CaptureActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CaptureActivity";

    public static final String RESULT_FILE_PATH = "file_path";
    public static final String RESULT_FILE_WIDTH = "file_width";
    public static final String RESULT_FILE_HEIGHT = "file_height";
    public static final String RESULT_FILE_TYPE = "file_type";
    public static final int REQ_CAPTURE = 10001;
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};
    private static final int PERMISSION_CODE = 1000;

    private ArrayList<String> deniedPermission = new ArrayList<>();
    private CameraSelector mCameraSelector;
    private int rotation = Surface.ROTATION_0;
    private Size resolution = new Size(1280, 720);

    private AppCompatImageView mIvFlip;
    private CameraXPreview mViewFinder;
    private RecordView mRecordView;
    private AppCompatImageView mIvBack;
    private AppCompatImageView mIvSwitchTimer;
    private AppCompatTextView mTvTitle;
    private AppCompatTextView mTvCaptureTime;
    private AppCompatTextView mTvTimer;
    private AlbumFocusImageView mIvFocus;

    private Preview preview;
    private ImageCapture mImageCapture;
    private VideoCapture mVideoCapture;
    private boolean takingPicture = false;
    private String outputFilePath;
    private Executor mCameraExecutor;
    private ListenableFuture<ProcessCameraProvider> mCameraProviderFuture;
    private Camera mCamera;

    private Context mContext;
    private int mCameraSelectorInt = CameraSelector.LENS_FACING_FRONT;
    private int mAspectRatioInt = AspectRatio.RATIO_16_9;
    private CameraInfo mCameraInfo;
    private CameraControl mCameraControl;
    //??????????????????
    private boolean mRecording = false;
    //????????????
    private float mRecordingTime = 0;
    //???????????????
    private int mTotalRecordingTime = 0;
    //???????????????
    private boolean mIsThreeSecondTimer = true;
    private boolean mIsCountingDown = false;
    private int mTime;

    private final Handler mHandler = new Handler();
    private String mTitle;
    private String mMusicPath;

    private SimpleExoPlayer player;
    private ProgressiveMediaSource mediaSource;

    private LoadingDialog mLoadingDialog;

    private boolean isOnDestroy = false;
    private boolean canClickRecordBtn;

    public static void startActivityForResult(Activity activity) {
        Intent intent = new Intent(activity, CaptureActivity.class);
        activity.startActivityForResult(intent, REQ_CAPTURE);
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = CaptureActivity.this;
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CODE);
        setContentView(R.layout.album_activity_capture);
        getBundle();
        initView();
        setOnclickListener();
        initExoPlayer();

        mRecordView.setMaxDuration(mTotalRecordingTime);
        mRecordView.setOnRecordListener(new RecordView.onRecordListener() {
            @Override
            public void onClick() {
                //takingPicture = true;
                if (canClickRecordBtn) {
                    if (!mRecording) {
                        if (!mIsCountingDown) {
                            initTimerAndVideoCapture();
                        }
                    } else {
                        stopRecord();//??????????????????????????????????????????
                    }
                }
            }

            @Override
            public void onRecording(float progress) {
                mRecordingTime = progress;
                setProgressText(mRecordingTime);
            }

            @Override
            public void onFinish() {
                stopRecord();//????????????
            }
        });
    }


    private void setOnclickListener() {
        mIvFlip.setOnClickListener(this);
        mIvSwitchTimer.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
    }

    private void initExoPlayer() {
        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultRenderersFactory(this), new DefaultTrackSelector(), new DefaultLoadControl());
        Uri uri = null;
        File file = new File(mMusicPath);
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
            uri = Uri.parse(mMusicPath);
        }
        ProgressiveMediaSource.Factory factory = new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(this, Util.getUserAgent(this, getPackageName())));
        mediaSource = factory.createMediaSource(uri);
        player.prepare(mediaSource);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //???????????????????????????????????????0
        mRecordingTime = 0;
        canClickRecordBtn = true;
        setProgressText(mRecordingTime);
    }

    @Override
    protected void onPause() {
        if (mRecording) {
            stopRecord();//onPause?????????????????????
            try {
                Thread.sleep(500);//?????????super?????????500ms???????????????????????????
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.onPause();
    }

    private void getBundle() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            long total = bundle.getLong(Album.VIDEOTIME);
            Log.d(TAG, "getBundle:  total = " + total);
            mTotalRecordingTime = (int) ((total + 1000) / 1000);
            mTitle = bundle.getString(Album.MODEL_TITLE);
            mMusicPath = bundle.getString(Album.MUSIC_PATH);
//            mMusicPath = mMusicPath + File.separator + "bj.mp3";
        }
        Log.d(TAG, "getBundle: mMusicPath = " + mMusicPath);
        Log.d(TAG, "getBundle: videoTime = " + mTotalRecordingTime);
    }

    //????????? ????????????????????????
    private void initTimerAndVideoCapture() {
        mTime = mIsThreeSecondTimer ? 3 : 7;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String time = mTime + "";
                mTvTimer.setText(time);
                if (mTime > 0) {
                    mTvTimer.setVisibility(View.VISIBLE);
                    mIsCountingDown = true;
                    mTime -= 1;
                    mHandler.postDelayed(this, 1000);
                } else {
                    mTvTimer.setVisibility(View.GONE);
                    RecordingStart();
                    mIsCountingDown = false;
                }
            }
        }, 0);
    }





    @SuppressLint("RestrictedApi")
    private void RecordingStart() {
        if(!isOnDestroy){
            player.prepare(mediaSource);
            player.setPlayWhenReady(true);
            mIvFlip.setVisibility(View.INVISIBLE);
            mIvSwitchTimer.setVisibility(View.INVISIBLE);
            mIvBack.setVisibility(View.INVISIBLE);
            //????????????
            takingPicture = false;
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), System.currentTimeMillis() + ".mp4");
            mRecording = true;
            Log.d("OOM", "????????????");
            mVideoCapture.startRecording(file, mCameraExecutor, new VideoCapture.OnVideoSavedCallback() {
                @Override
                public void onVideoSaved(@NonNull File file) {
                    Log.d("OOM", "????????????");
                    if (mCameraSelectorInt == CameraSelector.LENS_FACING_FRONT) {
                        mirrorFlip(file.getPath());
                    } else {
                        onFileSaved(file);
                    }
                }

                @Override
                public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                    canClickRecordBtn = true;
                    Log.e(TAG, "onError: int " + videoCaptureError + " message " + message, cause);
                }
            });
            mRecordView.startRecord();
        }
    }

    private void mirrorFlip(String path) {
        showLoadingDialog();
        mLoadingDialog.setMessage("?????????");
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), System.currentTimeMillis() + ".mp4");
        EpVideo epVideo = new EpVideo(path);
        //????????????180???????????????
        epVideo.rotation(0, true);
        EpEditor.exec(epVideo, new EpEditor.OutputOption(file.getPath()), new OnEditorListener() {
            @Override
            public void onSuccess() {
                dismissLoadingDialog();
                Log.e(TAG, "EpEditor onSuccess");
                File fileOld = new File(path);
                if (fileOld.exists()) {
                    fileOld.delete();
                }
                onFileSaved(file);
            }

            @Override
            public void onFailure() {
                Log.e(TAG, "EpEditor onFailure");
            }

            @Override
            public void onProgress(float progress) {
                Log.e(TAG, "EpEditor onProgress " + progress);
//                mLoadingDialog.setMessage("???????????????"+progress+"%");
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void stopRecord() {
        mIvFlip.setVisibility(View.VISIBLE);
        mIvSwitchTimer.setVisibility(View.VISIBLE);
        mIvBack.setVisibility(View.VISIBLE);
        player.setPlayWhenReady(false);
        player.stop(true);
        mRecording = false;
        canClickRecordBtn = false;
        mVideoCapture.stopRecording();
        mRecordView.stopRecord();
    }

    private void initView() {
        mRecordView = findViewById(R.id.record_view);
        mViewFinder = findViewById(R.id.view_finder);
        mIvFlip = findViewById(R.id.iv_flip);
        mIvBack = findViewById(R.id.iv_back);
        mIvSwitchTimer = findViewById(R.id.iv_switch_timer);

        mTvTitle = findViewById(R.id.tv_model_title);
        mTvTitle.setText(String.format("?????????%s", mTitle));

        mTvCaptureTime = findViewById(R.id.tv_capture_time);

        mTvCaptureTime.setText(String.format(getString(R.string.album_record_time), 0, mTotalRecordingTime));
        mTvTimer = findViewById(R.id.tv_timer);
        mIvFocus = findViewById(R.id.iv_focus);
    }

    private void showErrorToast(@NonNull String message) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(CaptureActivity.this, message, Toast.LENGTH_SHORT).show();
        } else {
            runOnUiThread(() -> Toast.makeText(CaptureActivity.this, message, Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CapturePreviewActivity.REQ_PREVIEW && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            intent.putExtra(RESULT_FILE_PATH, outputFilePath);
            //????????????????????????????????????????????? ??????????????????????????????
            intent.putExtra(RESULT_FILE_WIDTH, resolution.getHeight());
            intent.putExtra(RESULT_FILE_HEIGHT, resolution.getWidth());
            intent.putExtra(RESULT_FILE_TYPE, !takingPicture);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            bindCameraX();//??????????????????????????????????????????????????????????????????????????????????????????
        }
    }

    private void onFileSaved(File file) {
        outputFilePath = file.getAbsolutePath();
        String mimeType = takingPicture ? "image/jpeg" : "video/mp4";
        MediaScannerConnection.scanFile(this, new String[]{outputFilePath}, new String[]{mimeType}, null);
        CapturePreviewActivity.startActivityForResult(this, outputFilePath, mMusicPath, !takingPicture, mTitle, mRecordingTime);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            deniedPermission.clear();
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int result = grantResults[i];
                if (result != PackageManager.PERMISSION_GRANTED) {
                    deniedPermission.add(permission);
                }
            }

            if (deniedPermission.isEmpty()) {
                Log.d(TAG, "onRequestPermissionsResult: bindCameraX");
                bindCameraX();//?????????????????????????????????
            } else {
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.album_capture_permission_message))
                        .setNegativeButton(getString(R.string.album_capture_permission_no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                CaptureActivity.this.finish();
                            }
                        })
                        .setPositiveButton(getString(R.string.album_capture_permission_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String[] denied = new String[deniedPermission.size()];
                                ActivityCompat.requestPermissions(CaptureActivity.this, deniedPermission.toArray(denied), PERMISSION_CODE);
                            }
                        }).create().show();
            }
        }
    }

    /**
     * ?????????cameraX
     */
    private void bindCameraX() {
        mCameraExecutor = ContextCompat.getMainExecutor(this);

        mCameraProviderFuture = ProcessCameraProvider.getInstance(this);

        initUseCase();

        mCameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = mCameraProviderFuture.get();
                bindProvider(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
                Log.e(TAG, "bindCameraX: ", e);
            }
        }, mCameraExecutor);
    }

    private void initUseCase() {
        //?????????????????????????????????????????????????????????
        //initImageCapture();
        initCameraSelector();
        initVideoCapture();
        initPreview();
    }

    @SuppressLint("RestrictedApi")
    private void initVideoCapture() {
        mVideoCapture = new VideoCaptureConfig.Builder()
                //.setCameraSelector(mCameraSelector)
                .setTargetAspectRatio(mAspectRatioInt)
                .setTargetRotation(rotation)
                //.setTargetResolution(resolution)
                //????????????
                .setVideoFrameRate(20)
                //bit???
                .setBitRate(3 * 1024 * 1024).build();
    }

    @SuppressLint("RestrictedApi")
    private void initPreview() {
        preview = new Preview.Builder()
                .setTargetAspectRatio(mAspectRatioInt) //?????????
                .build();
    }

    /**
     * ???????????????
     */
    private void initCameraSelector() {
        mCameraSelector = new CameraSelector.Builder()
                .requireLensFacing(mCameraSelectorInt)
                .build();
    }

    private void bindProvider(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();

        Log.d(TAG, "bindPreview: takingPicture" + takingPicture);

        mCamera = cameraProvider.bindToLifecycle(this, mCameraSelector, mVideoCapture, preview);
        mCameraInfo = mCamera.getCameraInfo();
        mCameraControl = mCamera.getCameraControl();

        preview.setSurfaceProvider(mViewFinder.createSurfaceProvider());
        initCameraListener();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_flip) {
            switchCameraSelector();
        } else if (v.getId() == R.id.iv_switch_timer) {
            switchTimer();
        } else if (v.getId() == R.id.iv_back) {
            //????????????????????????????????????
            if (!mIsCountingDown) {
                if (mRecording) {
                    stopRecord();//?????????????????????????????????????????????????????????
                } else {
                    finish();
                }
            }
        }
    }

    //????????????????????????
    private void setProgressText(float progress) {
        int prgressInt = (int) (progress + 0.5);
        String progressStr = prgressInt + "s / " + mTotalRecordingTime + "s";
        SpannableStringBuilder spannable = new SpannableStringBuilder(progressStr);
        if (prgressInt >= 0 && prgressInt < 10) {
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#5496FF")), 0, 2,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (prgressInt >= 10 && prgressInt < 100) {
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#5496FF")), 0, 3,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#5496FF")), 0, 4,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        mTvCaptureTime.setText(spannable);
    }

    /**
     * ???????????????
     */
    private void switchTimer() {
        if (mIsThreeSecondTimer) {
            Log.d(TAG, "switchTimer: " + mIsThreeSecondTimer);
            mIsThreeSecondTimer = false;
            mIvSwitchTimer.setImageResource(R.drawable.album_icon_timer_7);
        } else {
            Log.d(TAG, "switchTimer: " + mIsThreeSecondTimer);
            mIsThreeSecondTimer = true;
            mIvSwitchTimer.setImageResource(R.drawable.album_icon_timer_3);
        }
    }

    /**
     * ?????????????????????
     */
    private void switchCameraSelector() {
        switch (mCameraSelectorInt) {
            case CameraSelector.LENS_FACING_BACK:
                mCameraSelectorInt = CameraSelector.LENS_FACING_FRONT;
                break;
            case CameraSelector.LENS_FACING_FRONT:
                mCameraSelectorInt = CameraSelector.LENS_FACING_BACK;
                break;
        }
        bindCameraX();//??????????????????????????????????????????
    }

    /**
     * ?????????????????????
     */
    private void initCameraListener() {
        LiveData<ZoomState> zoomState = mCameraInfo.getZoomState();
        float maxZoomRatio = zoomState.getValue().getMaxZoomRatio();
        float minZoomRatio = zoomState.getValue().getMinZoomRatio();
        Log.d(TAG, "initCameraListener: maxZoomRatio = " + maxZoomRatio);
        Log.d(TAG, "initCameraListener: minZoomRatio = " + minZoomRatio);
        mViewFinder.setCustomTouchListener(new CameraXPreview.CustomTouchListener() {
            @Override
            public void zoom() {
                //??????
                float zoomRatio = zoomState.getValue().getZoomRatio();
                if (zoomRatio < maxZoomRatio) {
                    mCameraControl.setZoomRatio((float) (zoomRatio + 0.1));
                }
            }

            @Override
            public void ZoomOut() {
                //??????
                float zoomRatio = zoomState.getValue().getZoomRatio();
                if (zoomRatio > minZoomRatio) {
                    mCameraControl.setZoomRatio((float) (zoomRatio - 0.1));
                }
            }

            @Override
            public void click(float x, float y) {
                //??????????????????????????????????????????????????????????????????????????????????????????
                if (mCameraSelectorInt == CameraSelector.LENS_FACING_BACK) {
                    startFocus(x, y);
                }
            }

            @Override
            public void doubleClick(float x, float y) {
                // ??????????????????
                float zoomRatio = zoomState.getValue().getZoomRatio();
                if (zoomRatio > minZoomRatio) {
                    mCameraControl.setLinearZoom(0f);
                } else {
                    mCameraControl.setLinearZoom(0.5f);
                }
            }

            @Override
            public void longClick(float x, float y) {

            }
        });
    }

    /**
     * ??????????????????????????????
     *
     * @param x
     * @param y
     */
    private void startFocus(float x, float y) {
        MeteringPointFactory factory = new SurfaceOrientedMeteringPointFactory(1.0f, 1.0f);
        MeteringPoint point = factory.createPoint(x, y);
        FocusMeteringAction action = new FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                // auto calling cancelFocusAndMetering in 3 seconds
                .setAutoCancelDuration(3, TimeUnit.SECONDS)
                .build();
        mIvFocus.startFocus(new Point((int) x, (int) y));
        ListenableFuture future = mCameraControl.startFocusAndMetering(action);
        future.addListener(() -> {
            try {
                FocusMeteringResult result = (FocusMeteringResult) future.get();
                if (result.isFocusSuccessful()) {
                    mIvFocus.onFocusSuccess();
                } else {
                    mIvFocus.onFocusFailed();
                }
            } catch (Exception e) {
                Log.e(TAG, "click: " + e.getMessage(), e);
                mIvFocus.onFocusFailed();
            }
        }, mCameraExecutor);
    }

    @Override
    public void onBackPressed() {
        //????????????????????????????????????
        if (!mIsCountingDown) {
            if (mRecording) {
                stopRecord();//?????????????????????????????????????????????????????????
            } else {
                super.onBackPressed();
            }
        }
    }


    @SuppressLint("RestrictedApi")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Shut down our background executor
        //mCameraExecutor.shutdown();
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop(true);
            player.release();
        }

        isOnDestroy = true;
    }


    //????????????????????????
    private void initImageCapture() {
        // ????????????????????????
        mImageCapture = new ImageCapture.Builder()
                .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
                //.setTargetAspectRatio(mAspectRatioInt)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setTargetRotation(rotation)
                .build();
    }


    /**
     * Display loading dialog.
     */
    private void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this);

//            mLoadingDialog.setupViews(Widget.Builder);
        }
        if (!mLoadingDialog.isShowing() && !isOnDestroy) {
            mLoadingDialog.show();
        }
    }

    /**
     * Dismiss loading dialog.
     */
    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }
}
