package com.flyingeffects.com.ui.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.enity.UserInfo;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.huaweiObs;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.AlbumChooseCallback;
import com.flyingeffects.com.ui.interfaces.view.UploadMaterialMVPView;
import com.flyingeffects.com.ui.presenter.UploadMaterialMVPPresenter;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.view.RangeSeekBarView;
import com.flyingeffects.com.view.RoundImageView;
import com.flyingeffects.com.view.VideoFrameRecycler;
import com.lansosdk.videoeditor.DrawPadView2;
import com.lansosdk.videoeditor.MediaInfo;
import com.obs.services.ObsClient;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.yanzhenjie.album.AlbumFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * description ：上傳背景頁面
 * creation date: 2020/5/12
 * user : zhangtongju
 */
public class UploadMaterialActivity extends BaseActivity implements UploadMaterialMVPView {
    private UploadMaterialMVPPresenter Presenter;
    @BindView(R.id.crop_preivew_icon)
    ImageView playIcon;
    @BindView(R.id.videocrop_drawpadView)
    DrawPadView2 drawPadView;
    @BindView(R.id.rl_videocrop_rangeseekbar)
    RelativeLayout videocontainer;
    @BindView(R.id.videocrop_zoom_seekbar)
    SeekBar seekBar;
    @BindView(R.id.timeLineBar)
    RangeSeekBarView mRangeSeekBarView;
    @BindView(R.id.timeLineView)
    VideoFrameRecycler mTimeLineView;
    @BindView(R.id.crop_show_duration)
    TextView durationText;
    @BindView(R.id.videocrop_cursor)
    RoundImageView progressCursor;
    @BindView(R.id.iv_back)
    ImageView backButton;
    @BindView(R.id.crop_show_start)
    TextView startMs;
    @BindView(R.id.crop_show_end)
    TextView endMs;

    @BindView(R.id.tv_choose_pic)
    TextView tv_choose_pic;

    @BindView(R.id.add_head)
    ImageView add_head;

    @BindView(R.id.ed_nickname)
    EditText ed_nickname;


    private String uploadPath;
    /**
     * 用戶头像
     */
    private String imageHeadPath;

    private String huaweiVideoPath;
    private String huaweiImagePath;


    @Override
    protected int getLayoutId() {
        return R.layout.act_upload_material;
    }

    @Override
    protected void initView() {
        Presenter = new UploadMaterialMVPPresenter(this, this);
        //点击进入视频剪切界面
        String videoPath = getIntent().getStringExtra("videoPath");
        initVideoDrawPad(videoPath, false);
        UiStep.nowUiTag = "";
        UiStep.isFromDownBj = false;
        statisticsEventAffair.getInstance().setFlag(UploadMaterialActivity.this, "6_customize_bj_Crop");
    }

    @Override
    protected void initAction() {

    }

    @Override
    @OnClick({R.id.iv_back, R.id.tv_choose_pic, R.id.add_head
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_choose_pic:

                if (ed_nickname.getText() == null || ed_nickname.getText().equals("")) {
                    ToastUtil.showToast("请输入昵称");
                    return;
                }


                if(TextUtils.isEmpty(imageHeadPath)){
                    ToastUtil.showToast("请选择头像");
                    return;
                }


                saveVideo(false);

                break;

            case R.id.add_head:
                AlbumManager.chooseImageAlbum(UploadMaterialActivity.this, 1, 0, new AlbumChooseCallback() {
                    @Override
                    public void resultFilePath(int tag, List<String> paths, boolean isCancel, ArrayList<AlbumFile> albumFileList) {
                        Glide.with(UploadMaterialActivity.this).load(paths.get(0)).into(add_head);
                        imageHeadPath = paths.get(0);
                    }
                }, "");
                break;


            default:
                break;
        }
    }

    private SeekBar.OnSeekBarChangeListener zoomChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (progress <= 50) {
                Presenter.changeVideoZoom(progress);
            } else {
                Presenter.changeVideoZoom(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean hasResult = false;
    private static final int MIN_DURATION_SEC = 2;


    private void initVideoDrawPad(String path, boolean isCancel) {
        if (!isCancel && !path.trim().isEmpty()) {
            MediaInfo info = new MediaInfo(path);
            if (info.prepare()) {
                if (!info.isSupport() || !FileManager.isLansongVESuppport(path)) {
                    ToastUtil.showToast("本视频暂不支持");
                    this.finish();
                    return;
                }
                float duration = info.vDuration;
                if (duration < 0) {
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(path);
                    duration = Float.parseFloat(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;
                    retriever.release();
                }
                if (duration < MIN_DURATION_SEC) {
                    ToastUtil.showToast("视频时长小于2秒");
                    this.finish();
                    return;
                }
            } else {
                ToastUtil.showToast("本视频暂不支持");
                MediaInfo.checkFile(path);
                this.finish();
                return;
            }
            //启动容器
            seekBar.setOnSeekBarChangeListener(zoomChangeListener);
            Presenter.initDrawpad(drawPadView, path);
        }
    }


    @Override
    public RelativeLayout getVideoContainer() {
        return videocontainer;
    }

    @Override
    public void initTrimmer() {
        Presenter.setUpTrimmer(mRangeSeekBarView, mTimeLineView, progressCursor);
    }

    @Override
    public void showTimeMs(long durationMs, long startTimeMs, long endTimeMs) {
        durationText.setText(showTimeInFormat(durationMs));
        startMs.setText(showTimeInFormat(startTimeMs));
        endMs.setText(showTimeInFormat(endTimeMs));
    }

    private static String showTimeInFormat(long timeMs) {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss:SS");
        format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        return format.format(timeMs);
    }

    @Override
    public void updateCursor(float currentX) {
        this.runOnUiThread(() -> {
            progressCursor.setTranslationX(currentX);
        });
    }

    @Override
    public void hideCursor() {
        playIcon.setVisibility(View.VISIBLE);
        progressCursor.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showCursor() {
        playIcon.setVisibility(View.GONE);
        progressCursor.setVisibility(View.VISIBLE);
    }

    @Override
    public void finishCrop(String videoPath) {
        WaitingDialog.openPragressDialog(this);
        String aa = videoPath.substring(videoPath.length() - 4);
        String nowTime = StringUtil.getCurrentTime_hh();
        LogUtil.d("OOM", "nowTime=" + nowTime);
        String copyName = "media/android/" + nowTime + "/" + System.currentTimeMillis() + aa;
        uploadFileToHuawei(videoPath, copyName, false);
    }

    @Override
    public void getRealCutTime(float RealCutTime) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Presenter.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Presenter.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Presenter.onResume();
    }

    @Override
    public void onBackPressed() {
        if (hasResult) {
            Presenter.onDestroy();
        }
        this.finish();
    }


    private void saveVideo(boolean needCut) {
        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            Presenter.saveVideo(needCut);
        }
    }


    private void requestData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("videofile", huaweiVideoPath);
        params.put("auth", ed_nickname.getText().toString());
        params.put("auth_image", huaweiImagePath);
        // 启动时间
        Observable ob = Api.getDefault().toLogin(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<UserInfo>(UploadMaterialActivity.this) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(UserInfo data) {
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("OOM", "requestLogin=" + str);
                UploadMaterialActivity.this.finish();
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);





    }


    private void uploadFileToHuawei(String videoPath, String copyName, boolean isHeadIcon) {
        new Thread(() -> huaweiObs.getInstance().uploadFileToHawei(videoPath, copyName, new huaweiObs.Callback() {
            @Override
            public void isSuccess(String str) {
                Observable.just(str).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        LogUtil.d("PutObject------result", s);
//                        WaitingDialog.closePragressDialog();
                        String str = s.substring(s.indexOf("[") + 1, s.indexOf("]"));
                        LogUtil.d("PutObject------str", str);
                        String[] test = str.split(",");
                        for (int i = 0; i < test.length; i++) {
                            Log.d("PutObject------test", test[i]);
                            if (test[i].contains("objectUrl")) {
                                String path = test[i];
                                if(!isHeadIcon){
                                    huaweiVideoPath= path.substring(path.indexOf("objectUrl=") + 1);
                                    LogUtil.d("OOM", "huaweiVideoPath=" + huaweiVideoPath);
                                }else{
                                    huaweiImagePath= path.substring(path.indexOf("objectUrl=") + 1);
                                    LogUtil.d("OOM", "huaweiVideoPath=" + huaweiImagePath);
                                }
                                if (!isHeadIcon) {
                                    String aa = videoPath.substring(videoPath.length() - 4);
                                    String nowTime = StringUtil.getCurrentTime_hh();
                                    LogUtil.d("OOM", "nowTime=" + nowTime);
                                    String copyName = "image/android/" + nowTime + "/" + System.currentTimeMillis() + aa;
                                    uploadFileToHuawei(imageHeadPath,copyName,true);
                                }else{


                                    requestData();

                                }


                                break;
                            }
                        }
                    }
                });

            }
        })).start();
    }









}
