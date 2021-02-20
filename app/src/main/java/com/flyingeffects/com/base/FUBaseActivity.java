package com.flyingeffects.com.base;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.faceunity.FURenderer;
import com.faceunity.gles.core.GlUtil;
import com.faceunity.utils.BitmapUtil;
import com.faceunity.utils.Constant;
import com.faceunity.utils.FileUtils;
import com.faceunity.utils.LogUtils;
import com.faceunity.utils.MiscUtil;
import com.flyingeffects.com.R;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.view.activity.FUBeautyActivity;
import com.flyingeffects.com.ui.view.activity.TemplateAddStickerActivity;
import com.flyingeffects.com.utils.FuLive.BaseCameraRenderer;
import com.flyingeffects.com.utils.FuLive.CameraFocus;
import com.flyingeffects.com.utils.FuLive.CameraUtils;
import com.flyingeffects.com.utils.FuLive.PermissionUtil;
import com.flyingeffects.com.utils.FuLive.SwitchConfig;
import com.flyingeffects.com.utils.FuLive.ThreadHelper;
import com.flyingeffects.com.utils.FuLive.encoder.MediaAudioEncoder;
import com.flyingeffects.com.utils.FuLive.encoder.MediaEncoder;
import com.flyingeffects.com.utils.FuLive.encoder.MediaMuxerWrapper;
import com.flyingeffects.com.utils.FuLive.encoder.MediaVideoEncoder;
import com.flyingeffects.com.utils.FuLive.renderer.Camera1Renderer;
import com.flyingeffects.com.utils.FuLive.renderer.OnRendererStatusListener;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.lansosdk.box.LSOVideoOption;
import com.lansosdk.box.Layer;
import com.lansosdk.videoeditor.DrawPadAllExecute2;
import com.lansosdk.videoeditor.MediaInfo;
import com.nineton.ntadsdk.utils.ScreenUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Base Activity, 主要封装FUBeautyActivity与FUEffectActivity的公用界面与方法
 * CameraRenderer相关回调实现
 * Created by tujh on 2018/1/31.
 */
public abstract class FUBaseActivity extends AppCompatActivity
        implements OnRendererStatusListener,
        SensorEventListener,
        FURenderer.OnFUDebugListener,
        FURenderer.OnTrackingStatusChangedListener {
    public final static String TAG = FUBaseActivity.class.getSimpleName();
    /**
     * 当前是否在拍摄
     */
    private boolean isShooting = false;
    protected GLSurfaceView mGlSurfaceView;
    protected Camera1Renderer mCameraRenderer;
    protected volatile boolean mIsDualInput = true;
    private TextView mDebugText;
    protected TextView mTvTrackStatus;
    private TextView mEffectDescription;
    //    protected RecordBtn mTakePicBtn;
    protected ViewStub mBottomViewStub;
    private LinearLayout mLlLight;
    protected CameraFocus mCameraFocus;
    //    private VerticalSeekBar mVerticalSeekBar;
//    protected CameraFocus mCameraFocus;
    protected ConstraintLayout mClOperationView;
    protected ConstraintLayout mRootView;
    private PopupWindow mPopupWindow;
    protected RadioGroup mInputTypeRadioGroup;
    private ImageView mIvShowMore;
    public RelativeLayout relative_content;
    public  FUBeautyActivity fuBeautyActivity;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private final Runnable mCameraFocusDismiss = new Runnable() {
        @Override
        public void run() {
//            mCameraFocus.layout(0, 0, 0, 0);
            mLlLight.setVisibility(View.INVISIBLE);
            onLightFocusVisibilityChanged(false);
        }
    };

    protected FURenderer mFURenderer;
    protected int mFrontCameraOrientation;
    private float[][] mLandmarksDataArray;
    private int mTrackedFaceCount;
    protected Handler mMainHandler = new Handler(Looper.getMainLooper());
    protected volatile boolean mIsTakingPic = false;

    protected abstract void onCreate();

    protected abstract FURenderer initFURenderer();

    // 默认全部使用对焦
    protected boolean showAutoFocus() {
        return true;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (super.onTouchEvent(event)) {
            return true;
        }

        if (!showAutoFocus()) {
            return false;
        }
        if (event.getPointerCount() == 1 && event.getAction() == MotionEvent.ACTION_DOWN) {

            if (relative_content.getVisibility() == View.VISIBLE) {
                relative_content.setVisibility(View.GONE);
            }

            //   mLlLight.setVisibility(View.VISIBLE);
            // mVerticalSeekBar.setProgress((int) (100 * mCameraRenderer.getExposureCompensation()));

            float rawX = event.getRawX();
            float rawY = event.getRawY();
            int focusRectSize = 150;

            // skip light progress bar area
//            DisplayMetrics screenInfo = ScreenUtils.getScreenInfo(this);
            int screenWidth = ScreenUtils.getScreenWidth(this);
            int marginTop = 280;
            int padding = 44;
            int progressBarHeight = 460;
            if (rawX > screenWidth - focusRectSize && rawY > marginTop - padding
                    && rawY < marginTop + progressBarHeight + padding) {
                return false;
            }

            mCameraRenderer.handleFocus(rawX, rawY, focusRectSize);
            mCameraFocus.showCameraFocus(rawX, rawY);
            mMainHandler.removeCallbacks(mCameraFocusDismiss);
            mMainHandler.postDelayed(mCameraFocusDismiss, CameraUtils.FOCUS_TIME);
            return true;
        }
        return false;
    }

    protected void onLightFocusVisibilityChanged(boolean visible) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraRenderer.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        mCameraRenderer.onPause();
    }

    protected volatile boolean mIsNeedTakePic = false;
    private volatile long mStartTime = 0;

    private Runnable effectDescriptionHide = new Runnable() {
        @Override
        public void run() {
            mEffectDescription.setText("");
            mEffectDescription.setVisibility(View.INVISIBLE);
        }
    };

    protected void showDescription(int str, int time) {
        LogUtils.debug("OOM", "showDescription");
        if (str == 0) {
            return;
        }
        mEffectDescription.removeCallbacks(effectDescriptionHide);
        mEffectDescription.setVisibility(View.VISIBLE);
        mEffectDescription.setText(str);
        mEffectDescription.postDelayed(effectDescriptionHide, time);
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Sensor部分~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public void onSensorChanged(SensorEvent event) {
        LogUtil.d("OOM", "onSensorChanged");
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if (Math.abs(x) > 3 || Math.abs(y) > 3) {
                if (Math.abs(x) > Math.abs(y)) {
                    mFURenderer.setTrackOrientation(x > 0 ? 0 : 180);
                } else {
                    mFURenderer.setTrackOrientation(y > 0 ? 90 : 270);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        LogUtil.d("OOM", "onAccuracyChanged");
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~FURenderer信息回调~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public void onFpsChange(final double fps, final double renderTime) {
        LogUtil.d("OOM", "onFpsChange");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //    mDebugText.setText(String.format(getString(R.string.fu_base_debug), mCameraRenderer.getCameraWidth(), mCameraRenderer.getCameraHeight(), (int) fps, (int) renderTime));
            }
        });
    }

    @Override
    public void onTrackStatusChanged(int type, int status) {
        LogUtil.d("OOM", "onTrackStatusChanged");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                mTvTrackStatus.setVisibility(status > 0 ? View.INVISIBLE : View.VISIBLE);
//                if (status <= 0) {
//                    int strId = 0;
//                    if (type == FURenderer.TRACK_TYPE_FACE) {
//                        strId = R.string.fu_base_is_tracking_text;
//                    } else if (type == FURenderer.TRACK_TYPE_HUMAN) {
//                        strId = R.string.toast_not_detect_body;
//                    } else if (type == FURenderer.TRACK_TYPE_GESTURE) {
//                        strId = R.string.toast_not_detect_gesture;
//                    }
//                    if (strId > 0) {
//                        mTvTrackStatus.setText(strId);
//                    }
//                }
            }
        });
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~FURenderer调用部分~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public void onSurfaceCreated() {
        LogUtil.d("OOM", "onSurfaceCreated");
        mFURenderer.onSurfaceCreated();
        mFURenderer.setBeautificationOn(true);
    }

    @Override
    public void onSurfaceChanged(int viewWidth, int viewHeight) {
        LogUtil.d("OOM", "onSurfaceChanged");
    }

    @Override
    public int onDrawFrame(byte[] cameraNv21Byte, int cameraTextureId, int cameraWidth, int cameraHeight,
                           float[] mvpMatrix, float[] texMatrix, long timeStamp) {
        LogUtil.d("OOM", "onDrawFrame");
        int fuTexId;
        if (mIsDualInput) {
            fuTexId = mFURenderer.onDrawFrame(cameraNv21Byte, cameraTextureId, cameraWidth, cameraHeight);
        } else {
            fuTexId = mFURenderer.onDrawFrame(cameraNv21Byte, cameraWidth, cameraHeight);
        }
        showLandmarks();
        sendRecordingData(fuTexId, GlUtil.IDENTITY_MATRIX, texMatrix, timeStamp / Constant.NANO_IN_ONE_MILLI_SECOND);
        takePicture(fuTexId, GlUtil.IDENTITY_MATRIX, texMatrix, cameraHeight, cameraWidth);
        return fuTexId;
    }

    @Override
    public void onSurfaceDestroy() {
        LogUtil.d("OOM", "onSurfaceDestroy");
        mFURenderer.onSurfaceDestroyed();
    }

//    @Override
//    public void onCameraChanged(int cameraFacing, int cameraOrientation) {
//        LogUtils.debug("OOM","onCameraChanged");
//        mFURenderer.onCameraChange(cameraFacing, cameraOrientation);
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                int progress = (int) (100 * mCameraRenderer.getExposureCompensation());
////                mVerticalSeekBar.setProgress(progress);
////            }
//        });
//    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~拍照录制部分~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    protected BitmapUtil.OnReadBitmapListener mOnReadBitmapListener = new BitmapUtil.OnReadBitmapListener() {

        @Override
        public void onReadBitmapListener(Bitmap bitmap) {
            LogUtil.d("OOM", "onReadBitmapListener");
            // Call on async thread
            final String filePath = MiscUtil.saveBitmap(bitmap, Constant.PHOTO_FILE_PATH, MiscUtil.getCurrentPhotoName());
            Log.d(TAG, "onReadBitmapListener: " + filePath);
            if (filePath != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast("保存照片成功");
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filePath)));
                        sendBroadcast(intent);
                    }
                });
            }
            mIsTakingPic = false;
        }
    };

    public void takePic() {
        LogUtils.debug("OOM", "takePic");
        if (mIsTakingPic) {
            return;
        }
        mIsNeedTakePic = true;
        mIsTakingPic = true;
    }

    /**
     * 拍照
     *
     * @param texId
     * @param texMatrix
     * @param texWidth
     * @param texHeight
     */
    protected void takePicture(int texId, float[] mvpMatrix, float[] texMatrix, final int texWidth, final int texHeight) {
        LogUtil.d("OOM", "takePicture");
        if (!mIsNeedTakePic) {
            return;
        }
        mIsNeedTakePic = false;
        BitmapUtil.glReadBitmap(texId, texMatrix, mvpMatrix, texWidth, texHeight, mOnReadBitmapListener, false);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //     ScreenUtils.fullScreen(this);
        EventBus.getDefault().register(this);
//        fuBeautyActivity=new FUBeautyActivity();
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.act_shoot);
        PermissionUtil.checkPermission(this);
        loadInternalConfigJson();
        mGlSurfaceView = (GLSurfaceView) findViewById(R.id.fu_base_gl_surface);
        mGlSurfaceView.setEGLContextClientVersion(GlUtil.getSupportGlVersion(this));
        mCameraRenderer = new Camera1Renderer(FUBaseActivity.this, mGlSurfaceView, this);
        mFrontCameraOrientation = CameraUtils.getCameraOrientation(Camera.CameraInfo.CAMERA_FACING_FRONT);
        mFURenderer = initFURenderer();
        relative_content = findViewById(R.id.relative_content);
        mGlSurfaceView.setRenderer(mCameraRenderer);
        mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mCameraFocus = findViewById(R.id.photograph_focus);
//        mInputTypeRadioGroup = (RadioGroup) findViewById(R.id.fu_base_input_type_radio_group);
//        mInputTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                switch (checkedId) {
//                    case R.id.fu_base_input_type_double:
//                        mIsDualInput = true;
//                        break;
//                    case R.id.fu_base_input_type_single:
//                        mIsDualInput = false;
//                        break;
//                    default:
//                }
//                mFURenderer.cameraChanged();
//            }
//        });
//
//        CheckBox debugBox = (CheckBox) findViewById(R.id.fu_base_debug);
//        mDebugText = (TextView) findViewById(R.id.fu_base_debug_text);
//        debugBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                mDebugText.setVisibility(isChecked ? View.VISIBLE : View.GONE);
//            }
//        });

//        if (BaseCameraRenderer.ENABLE_DRAW_LANDMARKS) {
//            SwitchCompat sw = findViewById(R.id.sw_landmarks);
//            sw.setVisibility(View.VISIBLE);
//            sw.setChecked(BaseCameraRenderer.ENABLE_DRAW_LANDMARKS);
//            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    BaseCameraRenderer.ENABLE_DRAW_LANDMARKS = isChecked;
//                }
//            });
//        }

//        mIvShowMore = findViewById(R.id.fu_base_more);
//        if (isOpenResolutionChange()) {
//            mIvShowMore.setImageResource(R.drawable.demo_icon_more);
//        } else if (isOpenPhotoVideo()) {
//            mIvShowMore.setImageResource(R.drawable.photo);
//        } else {
//            mIvShowMore.setVisibility(View.INVISIBLE);
//        }
//        mIvShowMore.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isOpenResolutionChange()) {
//                    showMorePopupWindow();
//                } else {
//                    onSelectPhotoVideoClick();
//                }
//            }
//        });
//        mTvTrackStatus = (TextView) findViewById(R.id.fu_base_is_tracking_text);
//        mEffectDescription = (TextView) findViewById(R.id.fu_base_effect_description);
//        mTakePicBtn = (RecordBtn) findViewById(R.id.fu_base_take_pic);
//        mTakePicBtn.setOnRecordListener(new RecordBtn.OnRecordListener() {
//            @Override
//            public void takePic() {
//                FUBaseActivity.this.takePic();
//            }
//
//            @Override
//            public void startRecord() {
//                mIsRecordStopped = false;
//                AsyncTask.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        startRecording();
//                    }
//                });
//            }
//
//            @Override
//            public void stopRecord() {
//                mIsRecordStopped = true;
//                AsyncTask.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        stopRecording();
//                    }
//                });
//                mTakePicBtn.setSecond(0);
//            }
//        });
//        mClOperationView = (ConstraintLayout) findViewById(R.id.cl_custom_view);
//        mRootView = (ConstraintLayout) findViewById(R.id.cl_root);
//        mBottomViewStub = (ViewStub) findViewById(R.id.fu_base_bottom);
//        mBottomViewStub.setInflatedId(R.id.fu_base_bottom);

        mLlLight = (LinearLayout) findViewById(R.id.photograph_light_layout);
//        mVerticalSeekBar = (VerticalSeekBar) findViewById(R.id.photograph_light_seek);
//        mCameraFocus = (CameraFocus) findViewById(R.id.photograph_focus);
//        mVerticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                mCameraRenderer.setExposureCompensation((float) progress / 100);
//                mMainHandler.removeCallbacks(mCameraFocusDismiss);
//                mMainHandler.postDelayed(mCameraFocusDismiss, CameraUtils.FOCUS_TIME);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//        });

        onCreate();
    }

    private void showLandmarks() {
        LogUtils.debug("OOM", "showLandmarks");
        if (!BaseCameraRenderer.ENABLE_DRAW_LANDMARKS) {
            return;
        }

        int trackedFaceCount = mFURenderer.getTrackedFaceCount();
        if (trackedFaceCount > 0) {
            if (mTrackedFaceCount != trackedFaceCount) {
                if (FURenderer.FACE_LANDMARKS_239 == getLandmarksType()) {
                    mLandmarksDataArray = new float[trackedFaceCount][239 * 2];
                } else {
                    mLandmarksDataArray = new float[trackedFaceCount][75 * 2];
                }
                mTrackedFaceCount = trackedFaceCount;
            }
            for (int i = 0; i < trackedFaceCount; i++) {
                mFURenderer.getLandmarksData(i, mLandmarksDataArray[i]);
            }
            mCameraRenderer.setLandmarksDataArray(mLandmarksDataArray);
        } else {
            if (mTrackedFaceCount != trackedFaceCount) {
                if (mLandmarksDataArray != null) {
                    for (float[] data : mLandmarksDataArray) {
                        Arrays.fill(data, 0F);
                    }
                    mCameraRenderer.setLandmarksDataArray(mLandmarksDataArray);
                }
                mTrackedFaceCount = trackedFaceCount;
            }
        }
    }

    /**
     * 发送录制数据
     *
     * @param texId
     * @param texMatrix
     * @param timeStamp
     */
    protected void sendRecordingData(int texId, float[] mvpMatrix, float[] texMatrix, final long timeStamp) {
        LogUtil.d("OOM", "sendRecordingData");
        synchronized (mRecordLock) {
            if (mVideoEncoder == null) {
                return;
            }
            mVideoEncoder.frameAvailableSoon(texId, texMatrix, mvpMatrix);
            if (mStartTime == 0) {
                mStartTime = timeStamp;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mIsRecordStopped) {
//                        mTakePicBtn.setSecond(timeStamp - mStartTime);
                    }
                }
            });
        }
    }

    private File mVideoOutFile;
    private MediaMuxerWrapper mMuxer;
    private MediaVideoEncoder mVideoEncoder;
    private final Object mRecordLock = new Object();
    private CountDownLatch mRecordBarrier;
    private volatile boolean mIsRecordStopped;

    /**
     * 录制封装回调
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        private long mStartRecordTime;

        @Override
        public void onPrepared(final MediaEncoder encoder) {
            LogUtil.d("OOM", "onPrepared");
            if (encoder instanceof MediaVideoEncoder) {
                Log.d(TAG, "onPrepared: tid:" + Thread.currentThread().getId());
                mGlSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        if (mIsRecordStopped) {
                            return;
                        }
                        MediaVideoEncoder videoEncoder = (MediaVideoEncoder) encoder;
                        videoEncoder.setEglContext(EGL14.eglGetCurrentContext());
                        synchronized (mRecordLock) {
                            mVideoEncoder = videoEncoder;
                        }
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        mTakePicBtn.setSecond(0);
                    }
                });
            }
            mStartRecordTime = System.currentTimeMillis();
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            LogUtil.d("OOM", "onStopped");
            mRecordBarrier.countDown();
            // Call when MediaVideoEncoder's callback and MediaAudioEncoder's callback both are called.
            if (mRecordBarrier.getCount() == 0) {
                Log.d(TAG, "onStopped: tid:" + Thread.currentThread().getId());
                // video time long than 1s
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        mTakePicBtn.setSecond(0);
                    }
                });
                if (System.currentTimeMillis() - mStartRecordTime <= 1000) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //      ToastUtil.showToast(FUBaseActivity.this, R.string.save_video_too_short);
                        }
                    });
                    return;
                }
                mStartRecordTime = 0;
                // onStopped is called on codec thread, it may be interrupted, so we execute following code async.
                ThreadHelper.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final File dcimFile = new File(Constant.VIDEO_FILE_PATH, mVideoOutFile.getName());
                            FileUtils.copyFile(mVideoOutFile, dcimFile);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(dcimFile)));
                                    if(TextUtils.isEmpty(nowChooseBjPath)){
                                        intoTemplate(dcimFile.getPath());
                                    }else{
                                        compoundVideo(dcimFile.getPath(),nowChooseBjPath );
                                    }

                                }
                            });
                        } catch (IOException e) {
                            Log.e(TAG, "copyFile: ", e);
                        }
                    }
                });
            }
        }
    };


    private void intoTemplate(String path) {



        fuBeautyActivity.ToNextPage( path);


    }


    /**
     * 开始录制
     */
    public void startRecording() {
        LogUtil.d("OOM", "startRecording");
        Log.d(TAG, "startRecording: ");
        try {
            mStartTime = 0;
            //线程同步
            mRecordBarrier = new CountDownLatch(2);
            String videoFileName = Constant.APP_NAME + "_" + MiscUtil.getCurrentDate() + ".mp4";
            mVideoOutFile = new File(FileUtils.getExternalCacheDir(this), videoFileName);
            mMuxer = new MediaMuxerWrapper(mVideoOutFile.getAbsolutePath());

            // for video capturing
            int videoWidth = mCameraRenderer.getCameraHeight();
            int videoHeight = mCameraRenderer.getCameraWidth();
            new MediaVideoEncoder(mMuxer, mMediaEncoderListener, videoWidth, videoHeight);
            new MediaAudioEncoder(mMuxer, mMediaEncoderListener);

            mMuxer.prepare();
            mMuxer.startRecording();
        } catch (IOException e) {
            Log.e(TAG, "startCapture:", e);
        }
    }

    /**
     * 停止录制
     */
    public void stopRecording() {
        LogUtil.d("OOM", "stopRecording");
        Log.d(TAG, "stopRecording: ");
        if (mMuxer != null) {
            synchronized (mRecordLock) {
                mVideoEncoder = null;
            }
            mMuxer.stopRecording();
            mMuxer = null;
        }
    }
//
//    private void showMorePopupWindow() {
//        LogUtils.debug("OOM","showMorePopupWindow");
//        if (mPopupWindow == null) {
//            int width = getResources().getDimensionPixelSize(R.dimen.x682);
//            View view = LayoutInflater.from(this).inflate(R.layout.layout_popup_more, null);
//            RadioGroup rgSolution = view.findViewById(R.id.rg_resolutions);
//            rgSolution.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(RadioGroup group, int checkedId) {
//                    switch (checkedId) {
//                        case R.id.rb_resolution_480p:
//                            mCameraRenderer.changeResolution(640, 480);
//                            break;
//                        case R.id.rb_resolution_720p:
//                            mCameraRenderer.changeResolution(1280, 720);
//                            break;
//                        case R.id.rb_resolution_1080p:
//                            mCameraRenderer.changeResolution(1920, 1080);
//                            break;
//                        default:
//                    }
//                    mFURenderer.cameraChanged();
//                }
//            });
//            if (isOpenPhotoVideo()) {
//                ConstraintLayout clSelectPhoto = view.findViewById(R.id.cl_select_photo);
//                clSelectPhoto.setVisibility(View.VISIBLE);
//                clSelectPhoto.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        onSelectPhotoVideoClick();
//                    }
//                });
//            } else {
//                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) rgSolution.getLayoutParams();
//                params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
//                params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.x40);
//            }
//
//            mPopupWindow = new PopupWindow(view, width, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            mPopupWindow.setOutsideTouchable(true);
//            mPopupWindow.setTouchable(true);
//            mPopupWindow.setAnimationStyle(R.style.photo_more_popup_anim_style);
//        }
//
//        int xOffset = getResources().getDimensionPixelSize(R.dimen.x386);
//        int yOffset = getResources().getDimensionPixelSize(R.dimen.x12);
//        mPopupWindow.showAsDropDown(mIvShowMore, -xOffset + mIvShowMore.getWidth() / 2, yOffset);
//    }

    protected void onSelectPhotoVideoClick() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    protected boolean isOpenPhotoVideo() {
        return false;
    }

    protected boolean isOpenResolutionChange() {
        return false;
    }

    protected int getLandmarksType() {
        return FURenderer.FACE_LANDMARKS_75;
    }

    // only for complete requirement quickly
    private void loadInternalConfigJson() {
        LogUtil.d("OOM", "loadInternalConfigJson");
        File file = new File(Constant.EXTERNAL_FILE_PATH, "switch_config.json");
        if (!file.exists()) {
            return;
        }
        try {
            String jsonStr = FileUtils.readStringFromFile(file);
            JSONObject jsonObject = new JSONObject(jsonStr);
            int drawLandmarks = jsonObject.optInt("draw_landmarks", 0);
            BaseCameraRenderer.ENABLE_DRAW_LANDMARKS = drawLandmarks == 1;
            int stickerImportFile = jsonObject.optInt("sticker_import_file", 0);
            SwitchConfig.ENABLE_LOAD_EXTERNAL_FILE_TO_EFFECT = stickerImportFile == 1;
            int makeupImportFile = jsonObject.optInt("makeup_import_file", 0);
            SwitchConfig.ENABLE_LOAD_EXTERNAL_FILE_TO_MAKEUP = makeupImportFile == 1;
            int hairImportFile = jsonObject.optInt("hair_import_file", 0);
            SwitchConfig.ENABLE_LOAD_EXTERNAL_FILE_TO_HAIR = hairImportFile == 1;
            int bodyImportFile = jsonObject.optInt("body_import_file", 0);
            SwitchConfig.ENABLE_LOAD_EXTERNAL_FILE_TO_BODY = bodyImportFile == 1;
            int lightMakeupImportFile = jsonObject.optInt("light_makeup_import_file", 0);
            SwitchConfig.ENABLE_LOAD_EXTERNAL_FILE_TO_LIGHT_MAKEUP = lightMakeupImportFile == 1;
            int videoRecordDuration = jsonObject.optInt("video_record_duration", 10_000);
            SwitchConfig.VIDEO_RECORD_DURATION = videoRecordDuration;

        } catch (IOException | JSONException e) {
            Log.e(TAG, "loadInternalConfigJson: ", e);
        }
    }


    /**
     * description ：合成视频
     * creation date: 2021/2/7
     * user : zhangtongju
     */
    private void compoundVideo(String videoPath, String musicPath) {
        MediaInfo mediaInfo = new MediaInfo(videoPath);
        if (mediaInfo.prepare()){
            LogUtil.d("OOM2", "mediaInfo=" + mediaInfo.getWidth()+"---"+mediaInfo.getHeight()+"duration="+mediaInfo.getDurationUs());
            try {
                DrawPadAllExecute2 execute = new DrawPadAllExecute2(this, mediaInfo.getWidth(), mediaInfo.getHeight(), mediaInfo.getDurationUs() );
                execute.setFrameRate(20);
                execute.setEncodeBitrate(5 * 1024 * 1024);
                execute.setOnLanSongSDKErrorListener(message -> {
                    LogUtil.d("OOM2", "错误信息为" + message);
                });
                execute.setOnLanSongSDKProgressListener((l, i) -> {
                    LogUtil.d("OOM2", "进度为" + i + "%");
                });
                execute.setOnLanSongSDKCompletedListener(this::intoTemplate);
                //添加视频
                LSOVideoOption option = new LSOVideoOption(videoPath);
                option.setLooping(false);
                execute.addVideoLayer(option, 0, Long.MAX_VALUE, false, true);
                //添加音频
                if (!TextUtils.isEmpty(musicPath)) {
                    execute.addAudioLayer(musicPath, true);
                }
                execute.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * description ：得到选择的音乐
     * creation date: 2021/2/7
     * user : zhangtongju
     */
    private String nowChooseBjPath;
    private String nowOriginal;

    public void SetNowChooseMusic(String nowChooseBjPath, String nowOriginal) {
        this.nowChooseBjPath = nowChooseBjPath;
        this.nowOriginal = nowOriginal;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
