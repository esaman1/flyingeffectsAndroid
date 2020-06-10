package com.yanzhenjie.album.app.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaScannerConnection;
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
import androidx.camera.core.CameraX;
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

import com.google.common.util.concurrent.ListenableFuture;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.R;
import com.yanzhenjie.album.widget.AlbumFocusImageView;
import com.yanzhenjie.album.widget.CameraXPreview;
import com.yanzhenjie.album.widget.RecordView;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
    private ExecutorService mCameraExecutor;
    private ProcessCameraProvider mCameraProvider;
    private ListenableFuture<ProcessCameraProvider> mCameraProviderFuture;
    private Camera mCamera;

    private Context mContext;
    private int mCameraSelectorInt = CameraSelector.LENS_FACING_BACK;
    private int mAspectRatioInt = AspectRatio.RATIO_16_9;
    private CameraInfo mCameraInfo;
    private CameraControl mCameraControl;
    //录像中的判断
    private boolean mRecording = false;
    //当前时长
    private int mRecordingTime = 0;
    //录像总时长
    private int mTotalRecordingTime = 0;
    //计时器相关
    private boolean mIsThreeSecondTimer = true;
    private boolean mIsCountingDown = false;
    private int mTime;

    private final Handler mHandler = new Handler();
    private final Handler mRecordingHandler = new Handler();
    private String mTitle;

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

        // Initialize our background executor
        mCameraExecutor = Executors.newSingleThreadExecutor();
        mRecordView.setMaxDuration(mTotalRecordingTime);
        mRecordView.setOnRecordListener(new RecordView.onRecordListener() {
            @Override
            public void onClick() {
                //takingPicture = true;
                if (!mRecording) {
                    if (!mIsCountingDown) {
                        initTimerAndVideoCapture();
                    }
                } else {
                    stopRecord();
                }
            }

            @Override
            public void onRecording(int progress) {
                setProgressText(progress);
            }

            @Override
            public void onFinish() {
                stopRecord();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //从预览页回来的话就把时间归0
        setProgressText(0);
    }


    @SuppressLint("RestrictedApi")
    private void stopRecord() {
        mRecording = false;
        mVideoCapture.stopRecording();
        mRecordView.stopRecord();
    }

    private void getBundle() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            long total = bundle.getLong(Album.VIDEOTIME);
            mTotalRecordingTime = (int) (total / 1000);
            mTitle = bundle.getString(Album.MODEL_TITLE);
        }
        Log.d(TAG, "getBundle: videoTime = " + mTotalRecordingTime);
    }

    //计时器 倒计时后开始录制
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
                    mIsCountingDown = false;
                    mTvTimer.setVisibility(View.GONE);
                    RecordingStart();
                }
            }
        }, 0);
    }

    @SuppressLint("RestrictedApi")
    private void RecordingStart() {
        //开始录像
        takingPicture = false;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), System.currentTimeMillis() + ".mp4");
        mRecording = true;
        mVideoCapture.startRecording(file, mCameraExecutor, new VideoCapture.OnVideoSavedCallback() {
            @Override
            public void onVideoSaved(@NonNull File file) {
                onFileSaved(file);
            }

            @Override
            public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                showErrorToast(message);
            }
        });
        mRecordView.startRecord();
    }

    private void setOnclickListener() {
        mIvFlip.setOnClickListener(this);
        mIvSwitchTimer.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
    }

    private void initView() {
        mRecordView = findViewById(R.id.record_view);
        mViewFinder = findViewById(R.id.view_finder);
        mIvFlip = findViewById(R.id.iv_flip);
        mIvBack = findViewById(R.id.iv_back);
        mIvSwitchTimer = findViewById(R.id.iv_switch_timer);

        mTvTitle = findViewById(R.id.tv_model_title);
        mTvTitle.setText(String.format("模板：%s", mTitle));

        mTvCaptureTime = findViewById(R.id.tv_capture_time);

        mTvCaptureTime.setText(String.format(getString(R.string.album_record_time), mRecordingTime, mTotalRecordingTime));
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
            //当设备处于竖屏情况时，宽高的值 需要互换，横屏不需要
            intent.putExtra(RESULT_FILE_WIDTH, resolution.getHeight());
            intent.putExtra(RESULT_FILE_HEIGHT, resolution.getWidth());
            intent.putExtra(RESULT_FILE_TYPE, !takingPicture);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void onFileSaved(File file) {
        outputFilePath = file.getAbsolutePath();
        String mimeType = takingPicture ? "image/jpeg" : "video/mp4";
        MediaScannerConnection.scanFile(this, new String[]{outputFilePath}, new String[]{mimeType}, null);
        CapturePreviewActivity.startActivityForResult(this, outputFilePath, !takingPicture);
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
                bindCameraX();
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


    private void bindCameraX() {
        mCameraProviderFuture = ProcessCameraProvider.getInstance(this);
        mCameraProviderFuture.addListener(() -> {
            try {
                mCameraProvider = mCameraProviderFuture.get();
                bindPreview();
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void initUseCase() {
        //初始化用例，将三个用例封装为不同的方法
        //initImageCapture();
        initVideoCapture();
        initPreview();
    }

    @SuppressLint("RestrictedApi")
    private void initVideoCapture() {
        mVideoCapture = new VideoCaptureConfig.Builder()
                .setCameraSelector(mCameraSelector)
                .setTargetAspectRatio(mAspectRatioInt)
                .setTargetRotation(rotation)
                //.setTargetResolution(resolution)
                //视频帧率
                .setVideoFrameRate(25)
                //bit率
                .setBitRate(3 * 1024 * 1024).build();
    }

    @SuppressLint("RestrictedApi")
    private void initPreview() {
        preview = new Preview.Builder()
                .setCameraSelector(mCameraSelector) //前后摄像头
                .setTargetAspectRatio(mAspectRatioInt) //宽高比
                .setTargetRotation(rotation) //旋转角度
                .build();
    }

    /**
     * 选择摄像头
     */
    private void initCameraSelector() {
        mCameraSelector = new CameraSelector.Builder()
                .requireLensFacing(mCameraSelectorInt)
                .build();
    }

    @SuppressLint("RestrictedApi")
    private void bindPreview() {
        CameraX.unbindAll();
        initCameraSelector();
        //查询一下当前要使用的设备摄像头(比如后置摄像头)是否存在
        boolean hasAvailableCameraId = false;
        hasAvailableCameraId = CameraX.hasCamera(mCameraSelector);

        if (!hasAvailableCameraId) {
            showErrorToast("无可用的设备cameraId!,请检查设备的相机是否被占用");
            finish();
            return;
        }
        initUseCase();
        bindProvider();
    }

    private void bindProvider() {
        mCameraProvider.unbindAll();

        Log.d(TAG, "bindPreview: takingPicture" + takingPicture);

        mCamera = mCameraProvider.bindToLifecycle(this, mCameraSelector, mVideoCapture, preview);
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
            finish();
        }
    }

    //设置倒计时的文字
    private void setProgressText(int progress) {
        String progressStr = progress + " / " + mTotalRecordingTime;
        SpannableStringBuilder spannable = new SpannableStringBuilder(progressStr);
        if (progress >= 0 && progress < 10) {
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#5496FF")), 0, 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (progress >= 10 && progress < 100) {
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#5496FF")), 0, 2,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#5496FF")), 0, 3,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        mTvCaptureTime.setText(spannable);
    }

    /**
     * 切换定时器
     */
    private void switchTimer() {
        if (mIsThreeSecondTimer) {
            Log.d(TAG, "switchTimer: " + mIsThreeSecondTimer);
            mIsThreeSecondTimer = false;
            mIvSwitchTimer.setImageResource(R.drawable.album_icon_timer_7);
            showErrorToast("倒计时7秒钟");
        } else {
            Log.d(TAG, "switchTimer: " + mIsThreeSecondTimer);
            mIsThreeSecondTimer = true;
            mIvSwitchTimer.setImageResource(R.drawable.album_icon_timer_3);
            showErrorToast("倒计时3秒钟");
        }
    }

    /**
     * 切换前后摄像头
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
        bindCameraX();
    }

    /**
     * 预览控件的监听
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
                //放大
                float zoomRatio = zoomState.getValue().getZoomRatio();
                if (zoomRatio < maxZoomRatio) {
                    mCameraControl.setZoomRatio((float) (zoomRatio + 0.1));
                }
            }

            @Override
            public void ZoomOut() {
                //缩小
                float zoomRatio = zoomState.getValue().getZoomRatio();
                if (zoomRatio > minZoomRatio) {
                    mCameraControl.setZoomRatio((float) (zoomRatio - 0.1));
                }
            }

            @Override
            public void click(float x, float y) {
                // TODO 对焦
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

                    }
                }, mCameraExecutor);
            }

            @Override
            public void doubleClick(float x, float y) {
                // 双击放大缩小
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

    @SuppressLint("RestrictedApi")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraX.unbindAll();
        // Shut down our background executor
        mCameraExecutor.shutdown();
    }


    //本项目暂时用不到
    private void initImageCapture() {
        // 构建图像捕获用例
        mImageCapture = new ImageCapture.Builder()
                .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
                //.setTargetAspectRatio(mAspectRatioInt)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setTargetRotation(rotation)
                .build();
    }
}
