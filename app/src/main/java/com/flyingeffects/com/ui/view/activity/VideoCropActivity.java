package com.flyingeffects.com.ui.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.commonlyModel.GetVideoCover;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.enity.ChooseVideoAddSticker;
import com.flyingeffects.com.enity.CreateCutCallback;
import com.flyingeffects.com.enity.DownVideoPath;
import com.flyingeffects.com.enity.UserInfo;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.view.VideoCropMVPView;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.presenter.VideoCropMVPPresenter;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.view.RangeSeekBarView;
import com.flyingeffects.com.view.RoundImageView;
import com.flyingeffects.com.view.VideoFrameRecycler;
import com.lansosdk.videoeditor.DrawPadView2;
import com.lansosdk.videoeditor.MediaInfo;
import com.shixing.sxve.ui.view.WaitingDialog;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * @author Liya
 */
public class VideoCropActivity extends BaseActivity implements VideoCropMVPView {
    private VideoCropMVPPresenter Presenter;
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
    @BindView(R.id.tv_no_kt)
    TextView tv_no_kt;

    @BindView(R.id.tv_choose_pic)
    TextView tv_choose_pic;

    private boolean isNeedCut=true;

    private String isFrom;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_crop;
    }

    @Override
    protected void initView() {
        Presenter = new VideoCropMVPPresenter(this, this);
        //点击进入视频剪切界面
        String videoPath = getIntent().getStringExtra("videoPath");
        isFrom=getIntent().getStringExtra("comeFrom");
//        userSetDuration = getIntent().getLongExtra("duration", 0);
        initVideoDrawPad(videoPath, false);
        UiStep.nowUiTag="";
        UiStep.isFromDownBj=false;
        statisticsEventAffair.getInstance().setFlag(VideoCropActivity.this, "6_customize_bj_Crop");
        if(!TextUtils.isEmpty(isFrom)){
            if (isFrom.equals(FromToTemplate.ISFROMEDOWNVIDEOFORUSER)||isFrom.equals(FromToTemplate.ISFROMEDOWNVIDEOFORADDSTICKER)){
                tv_choose_pic.setVisibility(View.GONE);
                tv_no_kt.setText("下一步");
            }
        }


    }

    @Override
    protected void initAction() {

    }

    @Override
    @OnClick({R.id.iv_back, R.id.tv_choose_pic,R.id.tv_no_kt
    })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_choose_pic: //抠图

                if (TextUtils.isEmpty(isFrom)&&isFrom.equals(FromToTemplate.ISFROMEDOWNVIDEOFORADDSTICKER)){
                    statisticsEventAffair.getInstance().setFlag(this, "7_Chromakey" );
                }else if(TextUtils.isEmpty(isFrom)&&isFrom.equals(FromToTemplate.ISFROMBJ)){
                    statisticsEventAffair.getInstance().setFlag(this, "8_Chromakey" );
                }else{
                    statisticsEventAffair.getInstance().setFlag(this, "2_Titles_cutdone", "手动卡点_片头裁剪完成");
                    statisticsEventAffair.getInstance().setFlag(VideoCropActivity.this, "6_customize_bj_Cutout");
                }


                saveVideo(true);
                isNeedCut=true;
                break;

            case R.id.tv_no_kt: //不需要抠图

                if (TextUtils.isEmpty(isFrom)&&isFrom.equals(FromToTemplate.ISFROMEDOWNVIDEOFORADDSTICKER)){
                    statisticsEventAffair.getInstance().setFlag(this, "7_Nokeying" );
                }else if(TextUtils.isEmpty(isFrom)&&isFrom.equals(FromToTemplate.ISFROMBJ)){
                    statisticsEventAffair.getInstance().setFlag(this, "8_Nokeying" );
                }else{
                    statisticsEventAffair.getInstance().setFlag(this, "2_Titles_cutdone", "手动卡点_片头裁剪完成");
                    statisticsEventAffair.getInstance().setFlag(VideoCropActivity.this, "6_customize_bj_Cutout");
                }
                saveVideo(false);
                isNeedCut=false;
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
    private static final int MAX_DURATION_SEC = 300;
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
//                if (duration > MAX_DURATION_SEC) {
//                    ToastUtil.showToast("视频时长超过3分钟");
//                    this.finish();
//                    return;
//                } else
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

        LogUtil.d("OOM","finishCrop"+"isFrom="+isFrom);
        //自定义只能够选择素材
        GetVideoCover getVideoCover=new GetVideoCover(this);
        getVideoCover.getCover(videoPath, path -> Observable.just(path).subscribeOn(AndroidSchedulers.mainThread()).subscribe(cover -> {
            if(!TextUtils.isEmpty(isFrom)&&isFrom.equals(FromToTemplate.ISFROMEDOWNVIDEO)){
                Presenter.hasFinishCrop();
                EventBus.getDefault().post(new CreateCutCallback(cover,videoPath,isNeedCut));
            }else if(!TextUtils.isEmpty(isFrom)&&isFrom.equals(FromToTemplate.ISFROMEDOWNVIDEOFORUSER)){
                Presenter.hasFinishCrop();
                EventBus.getDefault().post(new DownVideoPath(videoPath));
            }else if(!TextUtils.isEmpty(isFrom)&&isFrom.equals(FromToTemplate.ISFROMEDOWNVIDEOFORADDSTICKER)){
                Presenter.hasFinishCrop();
                EventBus.getDefault().post(new ChooseVideoAddSticker(videoPath));
            }
            else{
                Presenter.hasFinishCrop();
                Intent intent = new Intent(VideoCropActivity.this, CreationTemplateActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("paths", cover);
                bundle.putString("originalPath",videoPath );
                bundle.putString("video_path", "");
                bundle.putBoolean("isNeedCut",isNeedCut);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Message", bundle);
                startActivity(intent);
                setResult(Activity.RESULT_OK, intent);
            }
            finish();
        }));

    }

    @Override
    public void getRealCutTime(float RealCutTime) {
        if(!TextUtils.isEmpty(isFrom)&&isFrom.equals(FromToTemplate.ISFROMEDOWNVIDEOFORUSER)) {
            requestLoginForSdk(RealCutTime);
        }
    }



    private void requestLoginForSdk(float cutTime) {
        if(!DoubleClick.getInstance().isFastDoubleClick()){
            HashMap<String, String> params = new HashMap<>();
            params.put("type","1");
            params.put("timelength",cutTime+"");
            // 启动时间
            Observable ob = Api.getDefault().userDefine(BaseConstans.getRequestHead(params));
            HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<UserInfo>(VideoCropActivity.this) {
                @Override
                protected void _onError(String message) {
                }

                @Override
                protected void _onNext(UserInfo data) {

                }
            }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
        }
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




}
