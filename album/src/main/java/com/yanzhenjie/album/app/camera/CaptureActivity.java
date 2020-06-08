package com.yanzhenjie.album.app.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.MotionEvent;
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
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.FocusMeteringResult;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.core.SurfaceOrientedMeteringPointFactory;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.ZoomState;
import androidx.camera.core.impl.VideoCaptureConfig;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;

import com.google.common.util.concurrent.ListenableFuture;
import com.yanzhenjie.album.R;
import com.yanzhenjie.album.widget.CameraXPreview;
import com.yanzhenjie.album.widget.RecordView;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
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

    private Preview preview;
    private ImageCapture mImageCapture;
    private VideoCapture mVideoCapture;
    private boolean takingPicture = true;
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

        initView();
        setOnclickListener();

        // Initialize our background executor
        mCameraExecutor = Executors.newSingleThreadExecutor();

        mRecordView.setOnRecordListener(new RecordView.onRecordListener() {
            @Override
            public void onClick() {
                takingPicture = true;
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), System.currentTimeMillis() + ".jpeg");

                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file)
                        .build();

                mImageCapture.takePicture(outputFileOptions, mCameraExecutor, new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        onFileSaved(file);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        showErrorToast(Objects.requireNonNull(exception.getMessage()));
                        Log.e(TAG, "Photo capture failed: " + exception.getMessage(), exception);
                    }
                });
            }


            @Override
            public void onLongClick() {
                takingPicture = false;
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), System.currentTimeMillis() + ".mp4");
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
            }

            @Override
            public void onFinish() {
                mVideoCapture.stopRecording();
            }
        });
    }

    private void setOnclickListener() {
        mIvFlip.setOnClickListener(this);

    }

    private void initView() {
        mRecordView = findViewById(R.id.record_view);
        mViewFinder = findViewById(R.id.view_finder);
        mIvFlip = findViewById(R.id.iv_flip);
        mIvBack = findViewById(R.id.iv_back);
        mIvSwitchTimer = findViewById(R.id.iv_switch_timer);
        mTvTitle = findViewById(R.id.tv_model_title);
        mTvCaptureTime = findViewById(R.id.tv_capture_time);
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
        CapturePreviewActivity.startActivityForResult(this, outputFilePath, !takingPicture, "完成");
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
//        mPreview = new Preview.Builder()
//                .setTargetAspectRatio(mAspectRatioInt)
//                .build();
        preview = new Preview.Builder()
                .setCameraSelector(mCameraSelector) //前后摄像头
                .setTargetAspectRatio(mAspectRatioInt) //宽高比
                .setTargetRotation(rotation) //旋转角度
                .build();
    }

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


    @SuppressLint("RestrictedApi")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraX.unbindAll();
        // Shut down our background executor
        mCameraExecutor.shutdown();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_flip) {
            switchCameraSelector();
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

                //focusView.startFocus(new Point((int) x, (int) y));
                ListenableFuture future = mCameraControl.startFocusAndMetering(action);
                future.addListener(() -> {
                    try {
                        FocusMeteringResult result = (FocusMeteringResult) future.get();
                        if (result.isFocusSuccessful()) {
                            //mBinding.focusView.onFocusSuccess();
                        } else {
                            //mBinding.focusView.onFocusFailed();
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
}
