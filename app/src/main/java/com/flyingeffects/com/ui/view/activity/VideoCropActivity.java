package com.flyingeffects.com.ui.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.commonlyModel.GetVideoCover;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.CompressionCuttingManage;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.AlbumChooseCallback;
import com.flyingeffects.com.ui.interfaces.view.VideoCropMVPView;
import com.flyingeffects.com.ui.presenter.VideoCropMVPPresenter;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.view.RangeSeekBarView;
import com.flyingeffects.com.view.RoundImageView;
import com.flyingeffects.com.view.VideoFrameRecycler;
import com.lansosdk.videoeditor.DrawPadView2;
import com.lansosdk.videoeditor.MediaInfo;
import com.yanzhenjie.album.AlbumFile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * @author Liya
 */
public class VideoCropActivity extends BaseActivity implements VideoCropMVPView {
    public static final int VIDEO_FOR_CROP = 0x100;
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
    /**
     * 用户设置的固定剪切时长，只针对自定义卡点视频界面
     */
    private long userSetDuration;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_crop;
    }

    @Override
    protected void initView() {
        Presenter = new VideoCropMVPPresenter(this, this);
        //点击进入视频剪切界面
        String videoPath = getIntent().getStringExtra("videoPath");
        userSetDuration = getIntent().getLongExtra("duration", 0);
        initVideoDrawPad(videoPath, false);
        UiStep.nowUiTag="";
        UiStep.isFromDownBj=false;
        statisticsEventAffair.getInstance().setFlag(VideoCropActivity.this, "6_customize_bj_Crop");
    }

    @Override
    protected void initAction() {

    }

    @Override
    @OnClick({R.id.iv_back, R.id.tv_choose_pic})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_choose_pic: //继续
                statisticsEventAffair.getInstance().setFlag(this, "2_Titles_cutdone", "手动卡点_片头裁剪完成");
                statisticsEventAffair.getInstance().setFlag(VideoCropActivity.this, "6_customize_bj_Cutout");
                saveVideo();
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
    private AlbumChooseCallback albumCallback = (tag, paths, isCancel, albumFileList) -> {
        hasResult = true;
        if (paths.size() > 0) {
            String path = paths.get(0);
            initVideoDrawPad(path, isCancel);
        } else {
            finish();
        }

    };


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
                if (duration > MAX_DURATION_SEC) {
                    ToastUtil.showToast("视频时长超过5分钟");
                    this.finish();
                    return;
                } else if (duration < MIN_DURATION_SEC) {
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
        //自定义只能够选择素材
        GetVideoCover getVideoCover=new GetVideoCover(this);
        getVideoCover.getCover(videoPath, path -> Observable.just(path).subscribeOn(AndroidSchedulers.mainThread()).subscribe(cover -> {
            Intent intent = new Intent(VideoCropActivity.this, CreationTemplateActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("paths", cover);
            bundle.putString("originalPath",videoPath );
            bundle.putString("video_path", "");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("Message", bundle);
            startActivity(intent);
            setResult(Activity.RESULT_OK, intent);
            finish();

        }));










//        AlbumManager.chooseWhichAlbum(VideoCropActivity.this, 1, 0, new AlbumChooseCallback() {
//            @Override
//            public void resultFilePath(int tag, List<String> paths, boolean isCancel, ArrayList<AlbumFile> albumFileList) {
//                CompressionCuttingManage manage = new CompressionCuttingManage(VideoCropActivity.this,"", tailorPaths -> {
//                    Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
//                        Intent intent = new Intent(VideoCropActivity.this, CreationTemplateActivity.class);
//                        Bundle bundle = new Bundle();
//                        bundle.putString("paths", tailorPaths.get(0));
//                        bundle.putString("originalPath",paths.get(0) );
//                        bundle.putString("video_path", videoPath);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.putExtra("Message", bundle);
//                        startActivity(intent);
//                        setResult(Activity.RESULT_OK, intent);
//                        finish();
//
//                    });
//                });
//                manage.ToMatting(paths);
//            }
//        }, 1, "");


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

    private void saveVideo() {

        if (!DoubleClick.getInstance().isFastDoubleClick()) {
            Presenter.saveVideo();
        }

    }
}
