package com.flyingeffects.com.ui.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Handler;

import androidx.annotation.Nullable;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.entity.UserInfo;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.manager.huaweiObs;
import com.flyingeffects.com.manager.mediaManager;
import com.flyingeffects.com.ui.interfaces.AlbumChooseCallback;
import com.flyingeffects.com.ui.interfaces.view.UploadMaterialMVPView;
import com.flyingeffects.com.ui.model.videoAddCover;
import com.flyingeffects.com.ui.presenter.UploadMaterialMVPPresenter;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.keyBordUtils;
import com.flyingeffects.com.view.RangeSeekBarView;
import com.flyingeffects.com.view.RoundImageView;
import com.flyingeffects.com.view.VideoFrameRecycler;
import com.lansosdk.videoeditor.DrawPadView2;
import com.lansosdk.videoeditor.MediaInfo;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.suke.widget.SwitchButton;
import com.yanzhenjie.album.AlbumFile;

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
import rx.schedulers.Schedulers;

/**
 * description ?????????????????????
 * creation date: 2020/5/12
 * user : zhangtongju
 */
public class UploadMaterialActivity extends BaseActivity implements UploadMaterialMVPView {
    //0 ???????????????1????????????
    private int isLandscape;
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

    @BindView(R.id.iv_show_cover)
    ImageView iv_show_cover;

    @BindView(R.id.tv_choose_pic)
    TextView tv_choose_pic;

    @BindView(R.id.add_head)
    ImageView add_head;

    @BindView(R.id.ed_nickname)
    EditText ed_nickname;


    @BindView(R.id.ed_describe)
    EditText ed_describe;


    private String uploadPath;
    /**
     * ????????????
     */
    private String imageHeadPath;

    //????????????
    private String huaweiVideoPath;

    //????????????
    private String huaweiImagePath;

    //????????????
    private String huaweiSound;


    //????????????
    private String coverImagePath;

    private String huaweiFolder;

    //1 ???????????????????????? 0 ????????????????????????  2????????????????????????????????????
    private int isFrom;

    //0 ????????????????????????1??????????????????
    private int is_with_play;

    private ArrayList<String> uploadPathList;


    @BindView(R.id.switch_button)
    SwitchButton switch_button;

    @BindView(R.id.tv_is_can_in_step)
    TextView tv_is_can_in_step;

    private int isChecked = 1;

    private String videoPath;

    @Override
    protected int getLayoutId() {
        return R.layout.act_upload_material;
    }

    @Override
    protected void initView() {
        Presenter = new UploadMaterialMVPPresenter(this, this);
        //??????????????????????????????
        videoPath = getIntent().getStringExtra("videoPath");
        isFrom = getIntent().getIntExtra("isFrom", 0);
        if (isFrom != 2) {
            initVideoDrawPad(videoPath, false);
            iv_show_cover.setVisibility(View.GONE);
            videocontainer.setVisibility(View.VISIBLE);
        } else {
            ed_describe.setHint("???????????????????????????");
            drawPadView.setVisibility(View.GONE);
            videocontainer.setVisibility(View.GONE);
            tv_is_can_in_step.setText("?????????????????????????????????");
            iv_show_cover.setVisibility(View.VISIBLE);
            Glide.with(this).load(videoPath).into(iv_show_cover);
        }
        UiStep.nowUiTag = "";
        UiStep.isFromDownBj = false;
        FileManager fileManager = new FileManager();
        huaweiFolder = fileManager.getFileCachePath(this, "toHawei");
        StatisticsEventAffair.getInstance().setFlag(UploadMaterialActivity.this, "6_customize_bj_Crop");
        ed_nickname.addTextChangedListener(textpassWatcher);
        if (!TextUtils.isEmpty(BaseConstans.nickName())) {
            ed_nickname.setText(BaseConstans.nickName());
        }
        if (!TextUtils.isEmpty(BaseConstans.headUrl())) {
            Glide.with(UploadMaterialActivity.this)
                    .load(BaseConstans.headUrl())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(add_head);
            imageHeadPath = BaseConstans.headUrl();
        }
        switch_button.setOnCheckedChangeListener((view, isChecked) -> {
            if (!isFastDoubleClick()) {
                if (isChecked) {
                    LogUtil.d("OOM", "is_with_play=" + 1);
                    this.isChecked = 1;
                } else {
                    LogUtil.d("OOM", "is_with_play=" + 0);
                    this.isChecked = 0;
                }
            }
        });

    }

    //????????????
    TextWatcher textpassWatcher = new TextWatcher() {
        private CharSequence temp;

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            temp = arg0;
        }

        @Override
        public void afterTextChanged(Editable arg0) {
            if ((temp.length()) > 10) {
                arg0.delete(10, 11);
                ToastUtil.showToast("??????????????????");
            }
        }
    };

    @Override
    protected void initAction() {

    }

    @Override
    @OnClick({R.id.iv_back, R.id.tv_choose_pic, R.id.add_head})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_choose_pic:

                if (ed_nickname.getText() == null || "".equals(ed_nickname.getText().toString())) {
                    ToastUtil.showToast("???????????????");
                    return;
                }

                if (TextUtils.isEmpty(imageHeadPath)) {
                    ToastUtil.showToast("???????????????");
                    return;
                }

                if (ed_describe.getText() == null || "".equals(ed_describe.getText().toString())) {
                    ToastUtil.showToast("???????????????");
                    return;
                }

                if (isFrom != 2) {
                    saveVideo();
                } else {
                    LogUtil.d("oom3", "??????????????????");
                    uploadDressUpImage(videoPath);
                }


                break;

            case R.id.add_head:
                AlbumManager.chooseImageAlbum(UploadMaterialActivity.this, 1, 0, new AlbumChooseCallback() {
                    @Override
                    public void resultFilePath(int tag, List<String> paths, boolean isCancel, boolean isFromCamera, ArrayList<AlbumFile> albumFileList) {
                        if (!isCancel) {
                            Glide.with(UploadMaterialActivity.this).load(paths.get(0)).into(add_head);
                            imageHeadPath = huaweiFolder + File.separator + "head.png";
                            //??????????????????
                            getHeadForPathToCompress(paths.get(0), imageHeadPath);

//                            CompressImgManage compressImgManage=new CompressImgManage(UploadMaterialActivity.this, new CompressImgManage.compressCallback() {
//                                @Override
//                                public void isSuccess(boolean b, String filePath) {
//                                    LogUtil.d("OOM","?????????????????????"+b+"????????????????????????"+filePath);
//                                    if(b){
//                                        imageHeadPath=filePath;
//                                    }else{
//                                        imageHeadPath = paths.get(0);
//                                    }
//                                }
//                            });
//                            compressImgManage.toCompressImg(paths);
                        }
                    }
                }, "");
                break;


            default:
                break;
        }
    }


    public void getHeadForPathToCompress(String path, String fileName) {
        Bitmap bp = BitmapManager.getInstance().getOrientationBitmap(path);
        bp = StringUtil.zoomImgNoDeformation(bp, 320, 180);
        BitmapManager.getInstance().saveBitmapToPath(bp, fileName);
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
                    ToastUtil.showToast("?????????????????????");
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
                    ToastUtil.showToast("??????????????????2???");
                    this.finish();
                    return;
                }
            } else {
                ToastUtil.showToast("?????????????????????");
                MediaInfo.checkFile(path);
                this.finish();
                return;
            }
            //????????????
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
    public void finishCrop(String videoPath, boolean Landscape) {
//        WaitingDialog.openPragressDialog(this);

        if (Landscape) {
            isLandscape = 1;
        } else {
            isLandscape = 0;
        }
        //??????3??? 1 ???????????????2????????????  3 ???????????????
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                WaitingDialog.openPragressDialog(UploadMaterialActivity.this, "?????????");
            }
        }, 500);
        new Thread(() -> toNext(videoPath)).start();

    }


    private void toNext(String videoPath) {
        coverImagePath = huaweiFolder + File.separator + "cover.png";
        //??????????????????
        videoAddCover.getInstance().getCoverForPathToCompress(videoPath, coverImagePath);
        //??????????????????
        mediaManager manager = new mediaManager(UploadMaterialActivity.this);
        manager.splitMp4(videoPath, new File(huaweiFolder), (isSuccess, putPath) -> {
            if (isSuccess) {
                LogUtil.d("OOM2", "??????????????????????????????" + huaweiFolder);
                huaweiSound = huaweiFolder + File.separator + "bgm.mp3";
            } else {
                LogUtil.d("OOM2", "??????????????????????????????null");
                huaweiSound = "";
            }
        });
        uploadPathList = new ArrayList<>();
        uploadPathList.add(videoPath);
        LogUtil.d("OOM", "imageHeadPath=" + imageHeadPath);
        if (!imageHeadPath.contains("http")) {
            LogUtil.d("OOM", "?????????http");
            uploadPathList.add(imageHeadPath);
        } else {
            LogUtil.d("OOM", "??????http");
            uploadPathList.add("");
            huaweiImagePath = imageHeadPath;
        }
        if (!TextUtils.isEmpty(huaweiSound)) {
            uploadPathList.add(huaweiSound);
        } else {
            uploadPathList.add("");
            huaweiSound = "";
        }
        uploadPathList.add(coverImagePath);
        nowUpdateIndex = 0;
        uploadFileToHuawei(videoPath, getPathName(0, videoPath));
    }


    @Override
    public void getRealCutTime(float realCutTime) {

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
            Presenter.saveVideo(false);
        }
    }

    /**
     * description ???
     * creation date: 2020/8/13
     * param : is_with_play 0 ????????????????????????1??????????????????
     * user : zhangtongju
     */
    private void requestData() {
        HashMap<String, String> params = new HashMap<>();
        if (isFrom != 2) {
            params.put("videofile", huaweiVideoPath);
            params.put("audiourl", huaweiSound);
            params.put("isLandscape", isLandscape + "");

        }

//        params.put("videofile", huaweiVideoPath);
//        params.put("audiourl", huaweiSound);
//        params.put("isLandscape", isLandscape + "");
        params.put("is_with_play", isChecked + ""); //1 ??????????????????
        params.put("auth", ed_nickname.getText().toString());
        params.put("title", ed_describe.getText().toString());
        params.put("auth_image", huaweiImagePath);
        params.put("image", coverImagePath);
        Observable ob;
        if (isFrom == 1) {
            ob = Api.getDefault().uploadSearchResult(BaseConstans.getRequestHead(params));
        } else if (isFrom == 2) {
            ob = Api.getDefault().uploadHumanTemplate(BaseConstans.getRequestHead(params));
        } else {
            ob = Api.getDefault().toLoadTemplate(BaseConstans.getRequestHead(params));
        }
        LogUtil.d("OOM3", "params=" + StringUtil.beanToJSONString(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<UserInfo>(UploadMaterialActivity.this) {
            @Override
            protected void onSubError(String message) {
                LogUtil.d("OOM3", "_onError=" + message);
                WaitingDialog.closeProgressDialog();
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(UserInfo data) {
//                String str = StringUtil.beanToJSONString(data);
//                LogUtil.d("OOM3", "requestLogin=" + str);
                StatisticsEventAffair.getInstance().setFlag(UploadMaterialActivity.this, "13_video");
                WaitingDialog.closeProgressDialog();
                UploadMaterialActivity.this.finish();
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);

    }

    //?????????????????????
    int nowUpdateIndex;

    private void uploadFileToHuawei(String videoPath, String copyName) {
        Log.d("OOM2", "uploadFileToHuawei" + "????????????????????????" + videoPath + "??????????????????" + copyName);
        new Thread(() -> huaweiObs.getInstance().uploadFileToHawei(videoPath, copyName, new huaweiObs.Callback() {
            @Override
            public void isSuccess(String str) {

                Observable.just(str).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if (nowUpdateIndex != uploadPathList.size() - 1) {
                            nowUpdateIndex++;
                            if (nowUpdateIndex == 1 && TextUtils.isEmpty(uploadPathList.get(nowUpdateIndex))) {
                                //???????????????????????????????????????????????????
                                nowUpdateIndex = nowUpdateIndex + 1;
                                Log.d("OOM2", "?????????????????????" + "videoPath=" + videoPath);
                            }

                            if (nowUpdateIndex == 2 && TextUtils.isEmpty(uploadPathList.get(nowUpdateIndex))) {
                                //???????????????????????????????????????????????????
                                nowUpdateIndex = nowUpdateIndex + 1;
                                Log.d("OOM2", "?????????????????????" + "videoPath=" + videoPath);
                            }

                            uploadFileToHuawei(uploadPathList.get(nowUpdateIndex), getPathName(nowUpdateIndex, uploadPathList.get(nowUpdateIndex)));
                        } else {
                            requestData();
                        }
                    }
                });

            }
        })).start();
    }


    private String getPathName(int position, String videoPath) {
        String type = videoPath.substring(videoPath.length() - 4);
        String nowTime = StringUtil.getCurrentTimeymd();
        if (position == 0) {
            //??????
            LogUtil.d("OOM", "nowTime=" + nowTime);
            String path = "media/android/video/" + nowTime + "/" + System.currentTimeMillis() + type;
            huaweiVideoPath = "http://cdn.flying.flyingeffect.com/" + path;
            return path;
        } else if (position == 1) {
            //??????
            LogUtil.d("OOM", "nowTime=" + nowTime);
            String path = "media/android/image/" + nowTime + "/" + System.currentTimeMillis() + type;
            huaweiImagePath = "http://cdn.flying.flyingeffect.com/" + path;
            return path;
        } else if (position == 2) {
            //????????????
            LogUtil.d("OOM", "nowTime=" + nowTime);
            String path = "media/android/sound/" + nowTime + "/" + System.currentTimeMillis() + type;
            huaweiSound = "http://cdn.flying.flyingeffect.com/" + path;
            return path;

        } else {

            //??????
            LogUtil.d("OOM", "nowTime=" + nowTime);
            String path = "media/android/cover/" + nowTime + "/" + System.currentTimeMillis() + type;
            coverImagePath = "http://cdn.flying.flyingeffect.com/" + path;
            return path;
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                View view = getCurrentFocus();
                keyBordUtils.hideKeyboard(ev, view, UploadMaterialActivity.this);//??????????????????????????????????????????
                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    /**
     * description ?????????????????????
     * creation date: 2020/12/7
     * user : zhangtongju
     */
    private void uploadDressUpImage(String path) {
        WaitingDialog.openPragressDialog(this);
        new Thread(() -> {
            String type = path.substring(path.length() - 4);
            String nowTime = StringUtil.getCurrentTimeymd();
            String copyPath = "media/android/dressUpImage/" + nowTime + "/" + System.currentTimeMillis() + type;
            coverImagePath = "http://cdn.flying.flyingeffect.com/" + copyPath;
            uploadImage(path, copyPath, false);
        }).start();
    }


    private void uploadImage(String videoPath, String copyName, boolean isUploadImage) {
        huaweiObs.getInstance().uploadFileToHawei(videoPath, copyName, new huaweiObs.Callback() {
            @Override
            public void isSuccess(String str) {
                Observable.just(str).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {

                        Observable.just(s).observeOn(Schedulers.io()).subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                LogUtil.d("oom3", "??????????????????");
                                if (isUploadImage) {
                                    requestData();
                                } else {
                                    if (imageHeadPath.contains("http")) {
                                        huaweiImagePath = imageHeadPath;
                                        LogUtil.d("OOM3", "huaweiImagePath=" + huaweiImagePath);
                                        requestData();
                                    } else {
                                        uploadImage(imageHeadPath, getPathName(1, imageHeadPath), true);
                                    }

                                }

                            }
                        });
                    }
                });
            }
        });
    }

}
