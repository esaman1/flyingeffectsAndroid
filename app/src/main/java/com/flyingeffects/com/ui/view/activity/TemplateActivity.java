package com.flyingeffects.com.ui.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.TemplateThumbAdapter;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.commonlyModel.GetPathType;
import com.flyingeffects.com.enity.TemplateThumbItem;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.AnimForViewShowAndHide;
import com.flyingeffects.com.manager.CompressionCuttingManage;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.AlbumChooseCallback;
import com.flyingeffects.com.ui.interfaces.VideoPlayerCallbackForTemplate;
import com.flyingeffects.com.ui.interfaces.view.TemplateMvpView;
import com.flyingeffects.com.ui.model.FromToTemplate;
import com.flyingeffects.com.ui.presenter.TemplatePresenter;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.timeUtils;
import com.flyingeffects.com.view.EmptyControlVideo;
import com.flyingeffects.com.view.MattingVideoEnity;
import com.shixing.sxve.ui.AssetDelegate;
import com.shixing.sxve.ui.SxveConstans;
import com.shixing.sxve.ui.albumType;
import com.shixing.sxve.ui.model.GroupModel;
import com.shixing.sxve.ui.model.MediaUiModel;
import com.shixing.sxve.ui.model.MediaUiModel2;
import com.shixing.sxve.ui.model.TemplateModel;
import com.shixing.sxve.ui.model.TextUiModel;
import com.shixing.sxve.ui.view.TemplateView;
import com.shixing.sxve.ui.view.TextAssetEditLayout;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.shixing.sxve.ui.view.WaitingDialog_progress;
import com.shixing.sxvideoengine.SXPlayerSurfaceView;
import com.shixing.sxvideoengine.SXTemplate;
import com.shixing.sxvideoengine.SXTemplatePlayer;
import com.suke.widget.SwitchButton;
import com.yanzhenjie.album.AlbumFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * 模板页面
 * 漫画和抠图比较特殊,
 */
public class TemplateActivity extends BaseActivity implements TemplateMvpView, AssetDelegate, AlbumChooseCallback {

    @BindView(R.id.switch_button)
    SwitchButton switch_button;
    @BindView(R.id.edit_view_container)
    FrameLayout mContainer;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.iv_play)
    ImageView ivPlayButton;
    private SXTemplatePlayer mPlayer;
    @BindView(R.id.player_surface_view)
    SXPlayerSurfaceView mPlayerView;
    private TemplatePresenter presenter;
    private List<String> imgPath = new ArrayList<>();
    private TemplateModel mTemplateModel;
    private File mFolder;
    private TemplateThumbAdapter templateThumbAdapter;
    private ArrayList<TemplateThumbItem> listItem = new ArrayList<>();
    private ArrayList<TemplateView> mTemplateViews;
    private int nowChooseIndex = 0;
    private String mAudio1Path;
    private static final String MUSIC_PATH = "/bj.mp3";
    private TextAssetEditLayout mTextEditLayout;
    @BindView(R.id.video_player)
    EmptyControlVideo videoPlayer;

    /**
     * 原图地址,如果不需要抠图，原图地址为null,有抠图的情况下，默认使用原图
     */
    private List<String> originalPath;
    private String templateFilePath;

    /**
     * 底部按钮数量
     */
    private int bottomButtonCount;
    /**
     * 需要素材数量
     */
    private int needAssetsCount;
    private String templateName;
    private String fromTo;

    @BindView(R.id.Real_time_preview)
    FrameLayout real_time_preview;

    @BindView(R.id.tv_end_time)
    TextView tv_end_time;

    @BindView(R.id.tv_start_time)
    TextView tv_start_time;

    private String templateId;

    private String getCartoonPath;

    private static final int REQUEST_SINGLE_MEDIA = 11;

    private static final int REQUEST_SINGLE_MEDIA_VIDEO = 12;
    /**
     * 点击事件选择的组位置
     */
    private int pickIndex;

    /**
     *当前点击事件选择group 的位置
     */
    private int pickGroupIndex;




    /**
     * 是否是即时播放
     */
    private boolean isRealtime = true;

    private boolean isPlaying = false;

    private int nowChoosePosition;
    private int lastChoosePosition;
    private AlphaAnimation hideAnim;


    /**
     * 当前是不是动漫，1表示是动漫
     */
    private int nowTemplateIsAnim;


    /**
     * 当前是不是视频抠图功能,1是
     */
    private int nowTemplateIsMattingVideo;


    /**
     * 当前可以选择视频
     */
    private boolean isCanChooseVideo = false;

    /**
     * 有值表示视频
     */
    private String videoTime;


    /**
     * 只针对视频抠图，然后吧第一针的封面传过去
     */
    private Bitmap videoMattingCaver;

    private boolean nowIsChooseMatting = true;


//    private int picout;

//    private String whiteImagePng;


    /**
     * 只针对预览显示的文案
     */
    private WaitingDialog_progress waitingDialogProgress;


    @Override
    protected int getLayoutId() {
        return R.layout.act_template_edit;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        findViewById(R.id.iv_top_back).setOnClickListener(this);
        findViewById(R.id.tv_top_submit).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_top_submit)).setText("保存");
        presenter = new TemplatePresenter(this, this);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("Message");
        if (bundle != null) {
            fromTo = bundle.getString("fromTo");
            needAssetsCount = bundle.getInt("isPicNum");
            templateId = bundle.getString("templateId");
            templateFilePath = bundle.getString("templateFilePath");
            imgPath = bundle.getStringArrayList("paths");
            videoTime = bundle.getString("videoTime");
            originalPath = bundle.getStringArrayList("originalPath");
            templateName = bundle.getString("templateName");
            nowTemplateIsAnim = bundle.getInt("is_anime");
        }

        if (!TextUtils.isEmpty(videoTime) && !videoTime.equals("0") && albumType.isVideo(GetPathType.getInstance().getMediaType(imgPath.get(0)))) {
            nowTemplateIsMattingVideo = 1;
            //不需要抠图就不需要扣第一帧页面
            if (originalPath != null && originalPath.size() != 0) {
                handler.sendEmptyMessage(1);
                presenter.getMattingVideoCover(originalPath.get(0));
            }
        }

        if (!TextUtils.isEmpty(videoTime) && !videoTime.equals("0")) {
            isCanChooseVideo = true;
        }
//        //如果是选择视频，那么需要第一针显示为用户上传的视频  //todo test
//        if (originalPath != null && originalPath.size() > 0) {
//            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//            retriever.setDataSource(originalPath.get(0));
//            String sss = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT);
//            LogUtil.d("OOM2", "原视频帧数是" + sss);
//        }
//        MediaMetadataRetriever retriever2 = new MediaMetadataRetriever();
//        retriever2.setDataSource(imgPath.get(0));
//        String sss2 = retriever2.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT);
//        LogUtil.d("OOM2", "灰度图帧数是" + sss2);

//        if (originalPath == null || originalPath.size() == 0) {
//            if (nowTemplateIsMattingVideo != 1) {
////                //不需要抠图,视频抠图无论如何都需要的
////                findViewById(R.id.ll_Matting).setVisibility(View.GONE);
//                switch_button.setChecked(false);
//                nowIsChooseMatting = false;
//            }
//        }


        mTextEditLayout = findViewById(R.id.text_edit_layout);
        mFolder = new File(templateFilePath);
        mAudio1Path = mFolder.getPath() + MUSIC_PATH;

        FileManager manager = new FileManager();
        String dir = manager.getFileCachePath(this, "");
        mTemplateViews = new ArrayList<>();

        SxveConstans.default_bg_path = new File(dir, "default_bj.png").getPath();
        seekBar.setOnSeekBarChangeListener(seekBarListener);
        if (nowTemplateIsMattingVideo == 1 ) {
            if( originalPath == null || originalPath.size() == 0){
                //当前是视频的情况下，且用户没有选择扣视频,上面的选中效果就取消
                switch_button.setChecked(false);
                nowIsChooseMatting = false;
            }else{
                switch_button.setChecked(true);
                nowIsChooseMatting = true;
            }
        }else{
            if( originalPath == null || originalPath.size() == 0){
                //当前是视频的情况下，且用户没有选择扣视频,上面的选中效果就取消
                switch_button.setChecked(false);
                nowIsChooseMatting = false;
            }
        }

        if(nowTemplateIsAnim == 1){
            findViewById(R.id.ll_Matting).setVisibility(View.GONE);
        }
        switch_button.setOnCheckedChangeListener((view, isChecked) -> {
            if (!isFastDoubleClick()) {
                mTemplateModel.resetUi();
                if (!isChecked) {
                    nowIsChooseMatting = false;
                    if (nowTemplateIsMattingVideo == 1 && !albumType.isImage(GetPathType.getInstance().getPathType(imgPath.get(0)))) {
                        if(originalPath!=null&&originalPath.size()!=0){
                            ChangeMaterialCallbackForVideo(null, originalPath.get(0), false);
                        }else{
                            ChangeMaterialCallbackForVideo(null,imgPath.get(0), false);
                        }


                    } else {
                        statisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "1_mb_bj_Cutoutopen");
                        //修改图为裁剪后的素材
                        presenter.ChangeMaterial(originalPath, bottomButtonCount, needAssetsCount);
                    }
                } else {
                    nowIsChooseMatting = true;
                    //选中状态
                    if (nowTemplateIsMattingVideo == 1 && !albumType.isImage(GetPathType.getInstance().getPathType(imgPath.get(0)))) {
                        handler.sendEmptyMessage(1);
                        new Thread(() -> presenter.intoMattingVideo(imgPath.get(0),templateName)).start();
                    } else {
                        statisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "1_mb_bj_Cutoutoff");
                        //修改为裁剪前的素材
                        presenter.ChangeMaterial(imgPath, bottomButtonCount, needAssetsCount);
                    }
                }
                if (mPlayer != null) {
                    mPlayer.pause();
                    ivPlayButton.setImageResource(R.mipmap.iv_play);
                    isPlaying = false;
                }
                showPreview(false, true);
                AnimForViewShowAndHide.getInstance().show(mContainer);
            }
        });


    }


    @Override
    protected void initAction() {
        //漫画逻辑和视频抠图逻辑大体差不多
        if (nowTemplateIsMattingVideo == 1 || nowTemplateIsAnim == 1) {
            presenter.loadTemplate(mFolder.getPath(), this, 1);
        } else {
            presenter.loadTemplate(mFolder.getPath(), this, 0);
        }
        mPlayerView.setPlayCallback(mListener);
    }

    @Override
    public void completeTemplate(TemplateModel templateModel) {
//        this.whiteImagePng=whiteImagePng;
        initTemplateThumb(templateModel.groupSize);
        mTemplateModel = templateModel;

        if (nowTemplateIsAnim == 1 || nowTemplateIsMattingVideo == 1) {
            mTemplateModel.cartoonPath = imgPath.get(0);  //设置灰度图
        }
        bottomButtonCount = templateModel.groupSize;
        int duration = mTemplateModel.getDuration();
        float allDuration = duration / mTemplateModel.fps;
        tv_end_time.setText(timeUtils.secondToTime((long) (allDuration)));
        initTemplateViews(mTemplateModel);  //初始化templateView 等数据
    }

    @Override
    public void toPreview(String path) {
        videoPlayer.setUp(path, true, "");
        videoPlayer.startPlayLogic();
        videoPlayer.setGSYVideoProgressListener((progress, secProgress, currentPosition, duration) -> seekBar.setProgress(progress));
        videoPlayer.setVideoAllCallBack(new VideoPlayerCallbackForTemplate(isSuccess -> {
            showPreview(false, true);
        }));
        showPreview(true, true);
    }

    @Override
    public void ChangeMaterialCallback(ArrayList<TemplateThumbItem> callbackListItem, List<String> list_all, List<String> listAssets) {
        listItem.clear();
        listItem.addAll(callbackListItem);
        templateThumbAdapter.notifyDataSetChanged();
        mTemplateModel.setReplaceAllMaterial(listAssets);
        mTemplateViews.get(nowChooseIndex).invalidate();
    }


    private void onclickPlaying() {
        if (isPlaying) {
            if (mPlayer != null) {
                mPlayer.pause();
                ivPlayButton.setImageResource(R.mipmap.iv_play);
                isPlaying = false;
                showPreview(true, false);
            }
        } else {
            isPlaying = true;
            ivPlayButton.setImageResource(R.mipmap.pause);
            if (real_time_preview.getVisibility() == View.VISIBLE) {
                if (mPlayer != null) {
                    mPlayer.start();
                }
                showPreview(true, false);
            } else {
                waitingDialogProgress = new WaitingDialog_progress(this);
                waitingDialogProgress.openProgressDialog();
                waitingDialogProgress.setProgress("生成中~\n" +
                        "如预览卡顿\n" +
                        "保存效果最佳");
                new Thread(() -> presenter.getReplaceableFilePath()).start();
            }

        }

    }

    @Override
    public void returnReplaceableFilePath(String[] paths) {
        for(int i=0;i<paths.length;i++){
            LogUtil.d("oom","渲染需要的地址为"+paths[i]);
        }
        Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> new Handler().post(() -> showPreview(true, false)));
        switchTemplate(mFolder.getPath(), paths);
    }

    @Override
    public void getCartoonPath(String getCartoonPath) {
        this.getCartoonPath = getCartoonPath;
    }


    /**
     * description ：获得的视频封面，且扣了图片的
     * creation date: 2020/4/14
     * user : zhangtongju
     */
    @Override
    public void showMattingVideoCover(Bitmap bp, String bpPath) {
        if (mTemplateModel != null) {
            if (!TextUtils.isEmpty(videoTime) && !videoTime.equals("0")) {
                if (bp != null) {
                    for (int i = 0; i < mTemplateModel.mAssets.size(); i++) {
                        MediaUiModel2 mediaUiModel2 = (MediaUiModel2) mTemplateModel.mAssets.get(i).ui;
                        mediaUiModel2.setVideoCover(bp);
                    }
                    if (mTemplateViews != null && mTemplateViews.size() > 0) {
                        Observable.just(nowChooseIndex).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                WaitingDialog.closePragressDialog();
                                mTemplateViews.get(nowChooseIndex).invalidate(); //提示重新绘制预览图
                            }
                        });
                    }
                }
            }
        }
        videoMattingCaver = bp;
        runOnUiThread(() -> ModificationSingleThumbItem(bpPath));
    }

    @Override
    public void showBottomIcon(String path) {
        LogUtil.d("OOM","底部图的路径为"+path);
        TemplateThumbItem item1 = listItem.get(lastChoosePosition);
        item1.setPathUrl(path);
        listItem.set(lastChoosePosition, item1);
        templateThumbAdapter.notifyItemChanged(lastChoosePosition);
    }


    /**
     * description ：是否抠图按钮切换 针对于视频
     * needMatting 是否需要抠图
     * creation date: 2020/4/14
     * user : zhangtongju
     */
    @Override
    public void ChangeMaterialCallbackForVideo(String originalVideoPath, String path, boolean needMatting) {
        //可能之前没勾选抠图，所以originalPath 为null，这里需要null 判断
        if (needMatting) {
            if (originalPath == null) {
                originalPath = new ArrayList<>();
            }
            originalPath.clear();
            originalPath.add(originalVideoPath);
            imgPath.clear();
            imgPath.add(path);
            List<String> list = new ArrayList<>();
            if (!TextUtils.isEmpty(originalVideoPath)) {
                list.add(originalVideoPath);
                mTemplateModel.setReplaceAllMaterial(list);
            } else {
                list.add(path);
                mTemplateModel.setReplaceAllMaterial(list);
            }
            //不需要抠图就不需要扣第一帧页面
            if (originalPath != null && originalPath.size() != 0) {
                presenter.getMattingVideoCover(originalPath.get(0));
                mTemplateModel.cartoonPath = imgPath.get(0);  //设置灰度图
            } else {
                waitingDialogProgress.openProgressDialog();
            }
        } else {
            //不需要抠图
            originalPath = null;
            imgPath.clear();
            imgPath.add(path);
            mTemplateModel.cartoonPath = path;
            mTemplateModel.setReplaceAllMaterial(imgPath);
            WaitingDialog.closePragressDialog();
            mTemplateViews.get(nowChooseIndex).invalidate();
            presenter.getButtomIcon(path);
        }


    }


    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onVideoPause();
        showPreview(false, true);
        isPlaying = false;
        ivPlayButton.setImageResource(R.mipmap.iv_play);
        if (mPlayer != null) {
            mPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.onVideoResume();
    }


    private void showPreview(boolean isPreview, boolean hasAnim) {
        LogUtil.d("OOM", "showPreview=" + isPreview + "isRealtime=" + isRealtime);

        if (isPreview) {
            if (isRealtime) {
                real_time_preview.setVisibility(View.VISIBLE);
            } else {
                videoPlayer.setVisibility(View.VISIBLE);
            }
            if (hasAnim) {
                AnimForViewShowAndHide.getInstance().hide(mContainer);
            } else {
                mContainer.setVisibility(View.GONE);
            }
            modificationThumbForRedactData(true);
        } else {
            videoPlayer.setVisibility(View.GONE);
            real_time_preview.setVisibility(View.INVISIBLE);
            mContainer.setVisibility(View.VISIBLE);
            modificationThumbForRedactData(false);
        }
    }


    @Override
    public void pickMedia(MediaUiModel model) {
        if (!DoubleClick.getInstance().isFastZDYDoubleClick(1000)) {
            pickIndex = model.getNowIndex();
            pickGroupIndex=model.getNowGroup();
            LogUtil.d("OOM","当前的点击位置为"+pickIndex);
            if (isCanChooseVideo) {
                // 只有是否选择视频的区别
                float videoTimeF = Float.parseFloat(videoTime);
                AlbumManager.chooseAlbum(TemplateActivity.this, 1, REQUEST_SINGLE_MEDIA, this, "", (long) (videoTimeF * 1000));
            } else {
                AlbumManager.chooseWhichAlbum(TemplateActivity.this, 1, REQUEST_SINGLE_MEDIA, this, 1, "");

            }


        }
    }

    @Override
    public void editText(TextUiModel model) {
        mTextEditLayout.setVisibility(View.VISIBLE);
        mTextEditLayout.setupWidth(model);
    }


    /**
     * description ：如果有背景，这里需要忽略最后一个值，因为最后一个模板是背景模板，用户是不能够操作的。
     * date: ：2019/11/28 14:10
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    private void initTemplateViews(TemplateModel templateModel) {
        for (int i = 1; i <= templateModel.groupSize; i++) {
            TemplateView templateView = new TemplateView(TemplateActivity.this);
            templateView.SetonGestureCallback(() -> statisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "1_mb_bj_drag"));
            templateView.setBackgroundColor(Color.BLACK);
            templateView.setVisibility(i == 1 ? View.VISIBLE : View.GONE);
            GroupModel groupModel = templateModel.groups.get(i);
            templateView.setAssetGroup(groupModel);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            params.gravity = Gravity.CENTER;
            mTemplateViews.add(templateView);
            mContainer.addView(templateView, params);
        }


        isFirstReplace(imgPath);
    }


    /**
     * description ：第一次添加素材
     * creation date: 2020/4/8
     * param :  paths 是第一次用户选择的素材
     * user : zhangtongju
     */
    private void isFirstReplace(List<String> paths) {
        if (mTemplateViews != null && mTemplateViews.size() > 0) {


            //这里只是为了底部按钮

            List<String> list_all = new ArrayList<>();
            for (int i = 0; i < bottomButtonCount; i++) {  //填满数据，为了缩略图
                if (paths.size() > i && !TextUtils.isEmpty(paths.get(i))) {
                    list_all.add(paths.get(i)); //前面的时path ，后面的为默认的path
                } else {
                    list_all.add(SxveConstans.default_bg_path);
                }
            }

            if (nowTemplateIsAnim == 1 || nowTemplateIsMattingVideo == 1) {
                //漫画 特殊
                TemplateThumbItem templateThumbItem = new TemplateThumbItem();
                if (originalPath != null&&originalPath.size()!=0) {
                    templateThumbItem.setPathUrl(originalPath.get(0));
                } else {
                    templateThumbItem.setPathUrl(imgPath.get(0));
                }
                templateThumbItem.setIsCheck(0);
                listItem.set(0, templateThumbItem);
            } else {
                for (int i = 0; i < list_all.size(); i++) {  //合成底部缩略图
                    TemplateThumbItem templateThumbItem = new TemplateThumbItem();
                    templateThumbItem.setPathUrl(list_all.get(i));
                    if (i == 0) {
                        templateThumbItem.setIsCheck(0);
                    } else {
                        templateThumbItem.setIsCheck(1);
                    }
                    listItem.set(i, templateThumbItem);
                }
            }
            templateThumbAdapter.notifyDataSetChanged();
            WaitingDialog.openPragressDialog(this);
            //这里是为了替换用户操作的页面
            List<String> listAssets = new ArrayList<>();
            for (int i = 0; i < needAssetsCount; i++) {  //填满数据，为了缩略图
                if (paths.size() > i && !TextUtils.isEmpty(paths.get(i))) {
                    if (nowTemplateIsAnim == 1 || nowTemplateIsMattingVideo == 1) {
                        //漫画或者灰度图，
                        if (originalPath != null&&originalPath.size()!=0) {
                            listAssets.add(originalPath.get(i));
                        } else {
                            listAssets.add(paths.get(i));
                        }
                    } else {
                        listAssets.add(paths.get(i));
                    }
                } else {
                    listAssets.add(SxveConstans.default_bg_path);
                }
            }

            new Thread(() -> {
                mTemplateModel.setReplaceAllFiles(listAssets, complete -> TemplateActivity.this.runOnUiThread(() -> {
                    WaitingDialog.closePragressDialog();
                    selectGroup(0);
                    nowChooseIndex = 0;
                    templateThumbAdapter.notifyDataSetChanged();
                    if (!TextUtils.isEmpty(videoTime) && !videoTime.equals("0")) {
                        if (videoMattingCaver != null) {
                            for (int i = 0; i < mTemplateModel.mAssets.size(); i++) {
                                MediaUiModel2 mediaUiModel2 = (MediaUiModel2) mTemplateModel.mAssets.get(i).ui;
                                mediaUiModel2.setVideoCover(videoMattingCaver);
                            }
                        }
                    }
                    if (mTemplateViews != null && mTemplateViews.size() > 0) {
                        mTemplateViews.get(nowChooseIndex).invalidate(); //提示重新绘制预览图
                    }

//                    if(picout==0){
//                        //不抠图默认就取消选中，但是切换按钮还是存在
//                        switch_button.setChecked(false);
//                    }

                }));  //批量替换图片
            }).start();
        }
    }


    /**
     * description ：选择当前的点，里面有个mModel ，其中mModel 一定要保证是当前可见mediaModel 的mModel,否则会出现灰屏的情况
     * date: ：2019/11/28 13:58
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public void selectGroup(final int index) {
        if (mTemplateViews != null && mTemplateViews.size() > 0) {
            try {
                TemplateView nowChooseTemplateView = mTemplateViews.get(index);
                nowChooseTemplateView.setVisibility(View.VISIBLE);
                nowChooseTemplateView.invalidate();
                rx.Observable.from(mTemplateViews).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(templateView -> {
                    if (templateView != nowChooseTemplateView && templateView.getVisibility() != View.GONE) {
                        templateView.setVisibility(View.GONE);
                    }
                });
            } catch (Exception e) {
                LogUtil.d("Exception", e.getMessage());
            }
        }
    }


    public void initTemplateThumb(int bottomButtonCount) {
        for (int i = 0; i < bottomButtonCount; i++) {
            listItem.add(new TemplateThumbItem("", 1, false));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        templateThumbAdapter = new TemplateThumbAdapter(R.layout.item_group_thumb, listItem, TemplateActivity.this);
        templateThumbAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (!DoubleClick.getInstance().isFastZDYDoubleClick(1000)) {
                if (view.getId() == R.id.iv_show_un_select) {
                    if (mPlayer != null) {
                        mPlayer.pause();
                        ivPlayButton.setImageResource(R.mipmap.iv_play);
                        isPlaying = false;
                    }
                    nowChoosePosition = position;
                    if (nowChoosePosition != lastChoosePosition) {
                        selectGroup(position);
                        modificationThumbData(lastChoosePosition, position);
                    }
                    lastChoosePosition = nowChoosePosition;
                    showPreview(false, true);
                    AnimForViewShowAndHide.getInstance().show(mContainer);
                }
            }
        });
        recyclerView.setAdapter(templateThumbAdapter);
    }


    private void modificationThumbData(int lastPosition, int position) {
        TemplateThumbItem item1 = listItem.get(position);
        item1.setIsCheck(0);
        listItem.set(position, item1);
        TemplateThumbItem item2 = listItem.get(lastPosition);
        item2.setIsCheck(1);
        listItem.set(lastPosition, item2);
        templateThumbAdapter.notifyDataSetChanged();
    }

    private void modificationThumbForRedactData(boolean isRedate) {
        for (TemplateThumbItem item : listItem
        ) {
            item.setRedate(isRedate);
        }
        if (templateThumbAdapter != null) {
            templateThumbAdapter.notifyDataSetChanged();
        }

    }


    private void ModificationSingleThumbItem(String path) {
        if (listItem != null && listItem.size() > 0) {
            TemplateThumbItem item1 = listItem.get(lastChoosePosition);
            item1.setPathUrl(path);
            listItem.set(lastChoosePosition, item1);
            templateThumbAdapter.notifyItemChanged(lastChoosePosition);
        }
    }


    @OnClick({R.id.tv_top_submit, R.id.iv_play, R.id.edit_view_container})
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_top_submit:
                if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMSEARCH)) {
                    statisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "4_search_save", templateName);
                }
                statisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "1_mb_bj_save", templateName);

                if (isPlaying) {
                    if (mPlayer != null) {
                        mPlayer.pause();
                        mPlayer.stop();
                        ivPlayButton.setImageResource(R.mipmap.iv_play);
                        isPlaying = false;
                        showPreview(true, false);
                    }
                }

                presenter.renderVideo(mFolder.getPath(), mAudio1Path, false);
                break;

            case R.id.iv_play:
                if (!DoubleClick.getInstance().isFastZDYDoubleClick(500)) {
                    onclickPlaying();
                }
                break;

            case R.id.edit_view_container:
                break;

            default:
                break;

        }
        super.onClick(v);
    }

    @Override
    public void onBackPressed() {
        if (mTextEditLayout.getVisibility() == View.VISIBLE) {
            mTextEditLayout.hide();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        videoPlayer.release();
        EventBus.getDefault().unregister(this);
    }


    SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int nowProgress, boolean fromUser) {
            if (fromUser && mPlayer != null) {
                mPlayer.seek(nowProgress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };


    private int mDuration;

    private void switchTemplate(String folder, String[] mSources) {
        final SXTemplate template = new SXTemplate(folder, SXTemplate.TemplateUsage.kForPreview);
        for (int i = 0; i < mSources.length; i++) {
            LogUtil.d("OOM", "路徑為" + mSources[i]);

        }
        template.setReplaceableFilePaths(mSources);
        template.enableSourcePrepare();
        new Thread() {
            @Override
            public void run() {
                template.commit();
                runOnUiThread(() -> {
                    new Handler().post(waitingDialogProgress::closePragressDialog);
                    showPreview(true, false);
                    mDuration = template.realDuration();
                    seekBar.setMax(mDuration);
                    mPlayer = mPlayerView.setTemplate(template);
                    seekBar.setProgress(0);
                    mPlayer.replaceAudio(mAudio1Path);
                    LogUtil.d("OOM", "start");
                    mPlayer.start();
                    isPlaying = true;
                });
            }
        }.start();
    }


    private SXTemplatePlayer.PlayStateListener mListener = new SXTemplatePlayer.PlayStateListener() {
        @Override
        public void onProgressChanged(final int frame) {
            mPlayerView.post(() -> {
                seekBar.setProgress(frame);
                float nowDuration = frame / mTemplateModel.fps;
                tv_start_time.setText(timeUtils.secondToTime((long) (nowDuration)));
            });
        }

        @Override
        public void onFinish() {
            runOnUiThread(() -> {
                isPlaying = false;
                tv_start_time.setText("00:00");
                showPreview(false, true);
                ivPlayButton.setImageResource(R.mipmap.iv_play);
                seekBar.setProgress(0);
            });
        }
    };


    @Override
    public void resultFilePath(int tag, List<String> paths, boolean isCancel, ArrayList<AlbumFile> albumFileList) {
        if (!isCancel) {
            if (tag == REQUEST_SINGLE_MEDIA) {
                if (paths != null && paths.size() > 0) {
                    String mimeType;
                    String path = paths.get(0);
                    String extension = MimeTypeMap.getFileExtensionFromUrl(path);
                    if (!TextUtils.isEmpty(extension)) {
                        mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                        if (mimeType == null) {
                            mimeType = GetPathType.getInstance().getPathType(path);
                        }
                    } else {
                        mimeType = GetPathType.getInstance().getPathType(path);
                    }
                    if (albumType.isImage(mimeType)) {
                        if (originalPath == null || originalPath.size() == 0) {
                            if (nowTemplateIsMattingVideo == 1) {
                                mattingImage(paths);
                            } else {
                                //不需要抠图
                                if (imgPath.size() > lastChoosePosition) {
                                    imgPath.set(lastChoosePosition, paths.get(0));
                                } else {
                                    imgPath.add(paths.get(0));
                                }
                                MediaUiModel2 mediaUi2 = (MediaUiModel2) mTemplateModel.getAssets().get(lastChoosePosition).ui;
                                mediaUi2.setImageAsset(paths.get(0));
                                mTemplateViews.get(lastChoosePosition).invalidate();
                                ModificationSingleThumbItem(paths.get(0));
                            }

                        } else {
                            mattingImage(paths);
                        }
                    } else {
                        //如果是视频.就进入裁剪页面
                        float needVideoTime = Float.parseFloat(videoTime);
                        Intent intoCutVideo = new Intent(TemplateActivity.this, TemplateCutVideoActivity.class);
                        intoCutVideo.putExtra("needCropDuration", needVideoTime);
                        intoCutVideo.putExtra("videoPath", paths.get(0));
                        intoCutVideo.putExtra("picout", 1);
                        intoCutVideo.putExtra("templateName",templateName);
                        intoCutVideo.putExtra("isFrom", 2);
                        startActivity(intoCutVideo);
                    }
                }
            }
        }
    }

    private void mattingImage(List<String> paths) {
        boolean hasCache = nowTemplateIsAnim != 1;
        CompressionCuttingManage manage = new CompressionCuttingManage(TemplateActivity.this, templateId, hasCache, tailorPaths -> {
            if (originalPath != null&&originalPath.size()!=0) {
                originalPath.set(lastChoosePosition, paths.get(0));
            } else {
                //可能来自视频抠图页面，所以会出现出现null
                originalPath = new ArrayList<>();
                originalPath.add(lastChoosePosition, paths.get(0));
            }
            imgPath.set(lastChoosePosition, tailorPaths.get(0));
            Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
                if (nowTemplateIsAnim == 1 || nowTemplateIsMattingVideo == 1) {
                    //如果是漫画，逻辑会变
                    MediaUiModel2 mediaUi1 = (MediaUiModel2) mTemplateModel.getAssets().get(0).ui;
                    mediaUi1.setImageAsset(tailorPaths.get(0));

                    MediaUiModel2 mediaUi2 = (MediaUiModel2) mTemplateModel.getAssets().get(1).ui;
                    mediaUi2.setImageAsset(paths.get(0));

                    mTemplateViews.get(lastChoosePosition).invalidate();
                    ModificationSingleThumbItem(paths.get(0));

                    mTemplateModel.cartoonPath = paths.get(0);
                } else {
//                    int total = mTemplateModel.getAssetsSize() - 1;
                    //倒敘
//                    int nowChooseIndex = total - pickIndex;
//                    MediaUiModel2 mediaUi2 = (MediaUiModel2) mTemplateModel.getAssets().get(nowChooseIndex).ui;
//
                    for (int i=0;i<mTemplateModel.getAssets().size();i++){
                        MediaUiModel2 mediaUi2 = (MediaUiModel2) mTemplateModel.getAssets().get(i).ui;
                        if(pickGroupIndex==mediaUi2.getNowGroup()){
                            if(pickIndex==mediaUi2.getNowIndex()){
                                if (nowIsChooseMatting) {
                                    mediaUi2.setImageAsset(tailorPaths.get(0));
                                    ModificationSingleThumbItem(tailorPaths.get(0));
                                } else {
                                    mediaUi2.setImageAsset(paths.get(0));
                                    ModificationSingleThumbItem(paths.get(0));
                                }
                                return;
                            }
                        }
                        mTemplateViews.get(lastChoosePosition).invalidate();
                    }
                }

            });
        });
        manage.ToMatting(paths);
    }


    /**
     * description ：来自抠图按钮切换或者替换素材
     * creation date: 2020/4/14
     * user : zhangtongju
     */
    @Subscribe
    public void onEventMainThread(MattingVideoEnity event) {
        mTemplateModel.resetUi();
        if (event.getTag() == 1) {
            //点击了切换按钮且没扣过视频
            ChangeMaterialCallbackForVideo(event.getOriginalPath(), event.getMattingPath(), true);
        } else if (event.getTag() == 2) {
            nowTemplateIsMattingVideo = 1;
            mTemplateModel.mAssets.get(0).setIsAnim(true);
            //替换素材
            if (event.getOriginalPath() == null || !nowIsChooseMatting) {
                if (event.getOriginalPath() == null
                ) {
                    //用户没有选择抠图
                    ChangeMaterialCallbackForVideo(null, event.getMattingPath(), false);
                    //这里需要重新设置底部图，但是glide 视频路径相同。所以glide 不会刷新
                    presenter.getButtomIcon(event.getMattingPath());
                    switch_button.setChecked(false);

                } else {
                    //用户选择了抠图但是没有切换抠图
                    ChangeMaterialCallbackForVideo(null, event.getOriginalPath(), false);
                    //这里需要重新设置底部图，但是glide 视频路径相同。所以glide 不会刷新
                    presenter.getButtomIcon(event.getOriginalPath());
                }
            } else {
                //用户选择了抠图
                ChangeMaterialCallbackForVideo(event.getOriginalPath(), event.getMattingPath(), true);
                presenter.getButtomIcon(event.getOriginalPath());
            }
        }
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            WaitingDialog.openPragressDialog(TemplateActivity.this);
        }
    };

}
