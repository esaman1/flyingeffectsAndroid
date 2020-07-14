package com.flyingeffects.com.ui.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.TemplateThumbAdapter;
import com.flyingeffects.com.adapter.TemplateViewPager;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.commonlyModel.GetPathType;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.DownVideoPath;
import com.flyingeffects.com.enity.TabEntity;
import com.flyingeffects.com.enity.TemplateThumbItem;
import com.flyingeffects.com.enity.new_fag_template_item;
import com.flyingeffects.com.enity.showAdCallback;
import com.flyingeffects.com.manager.AdConfigs;
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
import com.flyingeffects.com.ui.model.GetPathTypeModel;
import com.flyingeffects.com.ui.presenter.TemplatePresenter;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.timeUtils;
import com.flyingeffects.com.view.EmptyControlVideo;
import com.flyingeffects.com.view.MattingVideoEnity;
import com.flyingeffects.com.view.NoSlidingViewPager;
import com.nineton.ntadsdk.itr.VideoAdCallBack;
import com.nineton.ntadsdk.manager.VideoAdManager;
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
import java.util.Objects;

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
    //    @BindView(R.id.recyclerView)
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
    private String mAudio1Path;
    private static final String MUSIC_PATH = "/bj.mp3";
    private TextAssetEditLayout mTextEditLayout;
    @BindView(R.id.video_player)
    EmptyControlVideo videoPlayer;
    private int nowSeekBarProgress;

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

//    private String getCartoonPath;

    private static final int REQUEST_SINGLE_MEDIA = 11;

//    private static final int REQUEST_SINGLE_MEDIA_VIDEO = 12;
    /**
     * 点击事件选择的组位置
     */
    private int pickIndex;

    /**
     * 当前点击事件选择group 的位置
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
     * 当前素材正式视频
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


    /**
     * 0 表示不需要抠图，1表示需要抠图，无论扣不抠图，都是默认进来就抠好了图，然后在通过这个字段在改变顶部按钮选中状态
     */
    private int picout;

    /**
     * 只针对预览显示的文案
     */
    private WaitingDialog_progress waitingDialogProgress;


    @BindView(R.id.template_viewPager)
    NoSlidingViewPager viewPager;


    @BindView(R.id.template_tablayout)
    CommonTabLayout commonTabLayout;

    //模板背景音乐 0表示模板音乐 1表示素材音乐  2 表示背景音乐
    private int nowChooseMusic = 0;

    /**
     * 当前分离出来的视频音乐
     */
    private String nowSpliteMusic;

    /**
     * 这里也是原图地址，如果是扣完图视频后，这个值是指真正意义上的原图地址且包含音乐而originalPath 是拼装后的原图地址
     */
    private String primitivePath;

    private new_fag_template_item templateItem;


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
            primitivePath = bundle.getString("primitivePath");
            picout = bundle.getInt("picout");

            originalPath = bundle.getStringArrayList("originalPath");
            templateName = bundle.getString("templateName");
            nowTemplateIsAnim = bundle.getInt("is_anime");
        }
        templateItem = (new_fag_template_item) getIntent().getSerializableExtra("person");
        if (originalPath != null && originalPath.size() > 0) {
            int totalMaterial = needAssetsCount;
            if (originalPath.size() < totalMaterial) {
                //说明用户没有选完素材，那么就需要补足素材，不然会出现数组越界的情况
                for (int i = 0; i < totalMaterial; i++) {
                    if (originalPath.size() > i && !TextUtils.isEmpty(originalPath.get(i))) {
                        LogUtil.d("OOM", "正常位置");
                    } else {
                        originalPath.add(SxveConstans.default_bg_path);
                        imgPath.add(SxveConstans.default_bg_path);
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(videoTime) && !videoTime.equals("0") && albumType.isVideo(GetPathType.getInstance().
                getMediaType(imgPath.get(0)))) {
            nowTemplateIsMattingVideo = 1;
            //不需要抠图就不需要扣第一帧页面
            if (originalPath != null && originalPath.size() != 0) {
                handler.sendEmptyMessage(1);
                new Thread(() -> presenter.getMattingVideoCover(originalPath.get(0))).start();

            }
        }

        if (!TextUtils.isEmpty(videoTime) && !videoTime.equals("0")) {
            isCanChooseVideo = true;
        }


        mTextEditLayout =
                findViewById(R.id.text_edit_layout);
        mFolder = new
                File(templateFilePath);
        mAudio1Path = mFolder.getPath() + MUSIC_PATH;
        FileManager manager = new FileManager();
        String dir = manager.getFileCachePath(this, "");
        mTemplateViews = new ArrayList<>();
        SxveConstans.default_bg_path = new
                File(dir, "default_bj.png").
                getPath();
        seekBar.setOnSeekBarChangeListener(seekBarListener);
        switch_button.setOnCheckedChangeListener((view, isChecked) ->
        {
            if (!isFastDoubleClick()) {
                mTemplateModel.resetUi();
                if (!isChecked) {
                    nowIsChooseMatting = false;
                    if (nowTemplateIsMattingVideo == 1 && !albumType.isImage(GetPathType.getInstance().getPathType(imgPath.get(0)))) {
                        if (originalPath != null && originalPath.size() != 0) {
                            ChangeMaterialCallbackForVideo(null, originalPath.get(0), false);
                        } else {
                            ChangeMaterialCallbackForVideo(null, imgPath.get(0), false);
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
                        new Thread(() -> presenter.intoMattingVideo(imgPath.get(0), templateName)).start();
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

//        test();
    }


    private void setMattingBtnState() {
        if (picout == 0) {
            //当前是视频的情况下，且用户没有选择扣视频,上面的选中效果就取消
            switch_button.setChecked(false);
            nowIsChooseMatting = false;
        } else {
            switch_button.setChecked(true);
            nowIsChooseMatting = true;
        }
        //漫画的时候取消上面的切换按钮
        if (nowTemplateIsAnim == 1) {
            findViewById(R.id.ll_Matting).setVisibility(View.GONE);
        }
    }


    private void test() {
        if (originalPath != null && originalPath.size() > 0) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(originalPath.get(0));
            String sss = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT);
            LogUtil.d("OOM2", "原视频帧数是" + sss);
        }
        MediaMetadataRetriever retriever2 = new MediaMetadataRetriever();
        retriever2.setDataSource(imgPath.get(0));
        String sss2 = retriever2.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT);
        LogUtil.d("OOM2", "灰度图帧数是" + sss2);
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
        mTemplateModel = templateModel;
//        if (templateModel.HasBj) {
//            findViewById(R.id.ll_viewpager_container).setVisibility(View.VISIBLE);
//            initBottomLayout();
//        } else {
//            recyclerView = findViewById(R.id.recyclerView);
//            recyclerView.setVisibility(View.VISIBLE);
//            initTemplateThumb(templateModel.groupSize);
//        }


        findViewById(R.id.ll_viewpager_container).setVisibility(View.VISIBLE);
        initBottomLayout();


        if (nowTemplateIsAnim == 1 || nowTemplateIsMattingVideo == 1) {
            mTemplateModel.cartoonPath = imgPath.get(0);  //设置灰度图
        }
        bottomButtonCount = templateModel.groupSize;
        int duration = mTemplateModel.getDuration();
        float allDuration = duration / mTemplateModel.fps;
        tv_end_time.setText(timeUtils.secondToTime((long) (allDuration)));
        initTemplateViews(mTemplateModel);
        //设置切换按钮

        new Handler().postDelayed(this::setMattingBtnState, 500);
    }

    @Override
    public void toPreview(String path) {
        videoPlayer.setUp(path, true, "");
        videoPlayer.startPlayLogic();
        videoPlayer.setGSYVideoProgressListener((progress, secProgress, currentPosition, duration) -> seekBar.setProgress(progress));
//        videoPlayer.setVideoAllCallBack(new VideoPlayerCallbackForTemplate(isSuccess -> ));
        videoPlayer.setVideoAllCallBack(new VideoPlayerCallbackForTemplate(new VideoPlayerCallbackForTemplate.videoPlayerStopListener() {
            @Override
            public void isStop(boolean isSuccess) {
                showPreview(false, true);
            }

            @Override
            public void onPrepared(boolean onPrepared) {
            }
        }));

        showPreview(true, true);
    }

    @Override
    public void ChangeMaterialCallback(ArrayList<TemplateThumbItem> callbackListItem, List<String> list_all, List<String> listAssets) {
        listItem.clear();
        listItem.addAll(callbackListItem);
        templateThumbAdapter.notifyDataSetChanged();
        mTemplateModel.setReplaceAllMaterial(listAssets);
//        mTemplateViews.get(nowChooseIndex).invalidate();
        invalidateView();
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
                    showPreview(true, false);
                } else {
                    toShowPreview();
                }
            } else {
                toShowPreview();
            }
        }
    }


    private void toShowPreview() {
        waitingDialogProgress = new WaitingDialog_progress(this);
        waitingDialogProgress.openProgressDialog();
        waitingDialogProgress.setProgress("生成中~\n" +
                "如预览卡顿\n" +
                "保存效果最佳");
        new Thread(() -> presenter.getReplaceableFilePath()).start();
    }

    @Override
    public void returnReplaceableFilePath(String[] paths) {
        for (String path : paths) {
            LogUtil.d("oom", "渲染需要的地址为" + path);
        }
        Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> new Handler().post(() -> showPreview(true, false)));

        if (mTemplateModel.HasBj && !TextUtils.isEmpty(mTemplateModel.getBackgroundPath())) {
            String[] newPaths = new String[paths.length + 1];
            System.arraycopy(paths, 0, newPaths, 0, paths.length);
            MediaUiModel2 mediaUiModel2 = (MediaUiModel2) mTemplateModel.mAssets.get(0).ui;
            if(albumType.isVideo(GetPathType.getInstance().getPathType(mTemplateModel.getBackgroundPath()))){
                newPaths[newPaths.length - 1] = mediaUiModel2.getpathForThisBjMatrixVideo(Objects.requireNonNull(getExternalFilesDir("runCatch/")).getPath(), mTemplateModel.getBackgroundPath());
            }else{
                newPaths[newPaths.length - 1] =mediaUiModel2.getpathForThisBjMatrixImage(Objects.requireNonNull(getExternalFilesDir("runCatch/")).getPath(),mTemplateModel.getBackgroundPath());
            }
            switchTemplate(mFolder.getPath(), newPaths);
        } else {
            switchTemplate(mFolder.getPath(), paths);
        }


    }

    @Override
    public void getCartoonPath(String getCartoonPath) {
//        this.getCartoonPath = getCartoonPath;
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
                        Observable.just(nowChoosePosition).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                WaitingDialog.closePragressDialog();
                                mTemplateViews.get(nowChoosePosition).invalidate(); //提示重新绘制预览图
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
        LogUtil.d("OOM", "底部图的路径为" + path);
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
        LogUtil.d("OOM","进度到了ChangeMaterialCallbackForVideo"+"needMatting="+needMatting);
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
//
//                //todo  2020-7-10
//                if(!switch_button.isChecked()){
//                    new Handler().postDelayed(() -> switch_button.setChecked(true),500);
//                }


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
            presenter.getButtomIcon(path);
            Observable.just(nowChoosePosition).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> new Handler().postDelayed(() -> mTemplateViews.get(integer).invalidate(), 200));
        }
    }


    @Override
    public void getSpliteMusic(String path) {
        nowSpliteMusic = path;
        setBjMusic();
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
                AnimForViewShowAndHide.getInstance().show(real_time_preview);
//                real_time_preview.setVisibility(View.VISIBLE);
            } else {
//                videoPlayer.setVisibility(View.VISIBLE);
                AnimForViewShowAndHide.getInstance().show(videoPlayer);
            }
            if (hasAnim) {
                AnimForViewShowAndHide.getInstance().hide(mContainer);
            } else {
//                mContainer.setVisibility(View.GONE);
                AnimForViewShowAndHide.getInstance().hide(mContainer);

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
            pickGroupIndex = model.getNowGroup();
            LogUtil.d("OOM", "当前的点击位置为" + pickIndex);
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
                if (originalPath != null && originalPath.size() != 0) {
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
                        if (originalPath != null && originalPath.size() != 0) {
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
                    nowChoosePosition = 0;
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
                        mTemplateViews.get(nowChoosePosition).invalidate(); //提示重新绘制预览图
                    }


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
                Observable.from(mTemplateViews).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(templateView -> {
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
                        mPlayer = null;
                        ivPlayButton.setImageResource(R.mipmap.iv_play);
                        isPlaying = false;
                        showPreview(true, false);
                    }
                }
                if (nowChooseMusic != 0) {
                    presenter.renderVideo(mFolder.getPath(), nowSpliteMusic, false);
                } else {
                    presenter.renderVideo(mFolder.getPath(), mAudio1Path, false);
                }

                presenter.StatisticsToSave(templateId);

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
                LogUtil.d("OOM", "nowProgress=" + nowProgress);
                nowSeekBarProgress = nowProgress;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (mPlayer != null) {
                mPlayer.seek(nowSeekBarProgress);
            }
        }
    };


    private int mDuration;

    private void switchTemplate(String folder, String[] mSources) {
        final SXTemplate template = new SXTemplate(folder, SXTemplate.TemplateUsage.kForPreview);
        for (String mSource : mSources) {
            LogUtil.d("OOM", "路徑為" + mSource);
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
                    LogUtil.d("OOM", "start");

                    if (nowChooseMusic != 0) {
//                        presenter.playBGMMusic(nowSpliteMusic, 0);
                        mPlayer.replaceAudio(nowSpliteMusic);
                    } else {
//                        presenter.playBGMMusic(nowSpliteMusic, 0);
                        mPlayer.replaceAudio(mAudio1Path);
                    }
                    isPlaying = true;
                    mPlayer.start();
                });
            }
        }.start();
    }


    private SXTemplatePlayer.PlayStateListener mListener = new SXTemplatePlayer.PlayStateListener() {
        @Override
        public void onProgressChanged(final int frame) {
            mPlayerView.post(() -> {
                LogUtil.d("OOM", "onProgressChangedFrame=" + frame);
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
                        chooseTemplateMusic();
                        templateThumbForMusic.findViewById(R.id.ll_0).setVisibility(View.GONE);
                    } else {
                        //如果是视频.就进入裁剪页面
                        float needVideoTime = Float.parseFloat(videoTime);
                        Intent intoCutVideo = new Intent(TemplateActivity.this, TemplateCutVideoActivity.class);
                        intoCutVideo.putExtra("needCropDuration", needVideoTime);
                        intoCutVideo.putExtra("videoPath", paths.get(0));
                        intoCutVideo.putExtra("picout", 1);
                        intoCutVideo.putExtra("templateName", templateName);
                        intoCutVideo.putExtra("isFrom", 2);
                        startActivity(intoCutVideo);
                    }
                }
            }
        }
    }


    /**
     * description ：跳转到抠图页面
     * creation date: 2020/5/6
     * user : zhangtongju
     */
    private void mattingImage(List<String> paths) {
        boolean hasCache = nowTemplateIsAnim != 1;
        CompressionCuttingManage manage = new CompressionCuttingManage(TemplateActivity.this, templateId, hasCache, tailorPaths -> {
            if (originalPath != null && originalPath.size() != 0) {
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
                    //这里是兼容多图，通过id 来得到具体是那个位置
                    for (int i = 0; i < mTemplateModel.getAssets().size(); i++) {
                        MediaUiModel2 mediaUi2 = (MediaUiModel2) mTemplateModel.getAssets().get(i).ui;
                        if (pickGroupIndex == mediaUi2.getNowGroup()) {
                            if (pickIndex == mediaUi2.getNowIndex()) {
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
                    changeMaterialMusic(event.getMattingPath());

                } else {
                    //用户选择了抠图但是没有切换抠图
                    ChangeMaterialCallbackForVideo(null, event.getOriginalPath(), false);
                    //这里需要重新设置底部图，但是glide 视频路径相同。所以glide 不会刷新
                    presenter.getButtomIcon(event.getOriginalPath());
                    changeMaterialMusic(event.getOriginalPath());
                }
            } else {
                //用户选择了抠图
                ChangeMaterialCallbackForVideo(event.getOriginalPath(), event.getMattingPath(), true);
                presenter.getButtomIcon(event.getOriginalPath());
                changeMaterialMusic(event.getMattingPath());
//                if(!switch_button.isChecked()){
//                    new Handler().postDelayed(() -> switch_button.setChecked(true),500);
//                }
//                LogUtil.d("OOM","重新选择了抠图");
            }
        }
        templateThumbForMusic.findViewById(R.id.ll_0).setVisibility(View.VISIBLE);
        primitivePath = event.getPrimitivePath();
    }

    private void changeMaterialMusic(String musicPath){
        if(nowChooseMusic==1){
            presenter.getBjMusic(musicPath);
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


    private void invalidateView() {
        Observable.just(nowChoosePosition).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> new Handler().postDelayed(() -> mTemplateViews.get(integer).invalidate(), 200));
    }


    private int lastChooseCommonTabLayout;
    private CheckBox cb_0;
    private CheckBox cb_1;
    private CheckBox cb_2;
    String[] titlesHasBj;
    private View templateThumbForMusic;

    public void initBottomLayout() {
        ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
        if (mTemplateModel.HasBj) {
            titlesHasBj = new String[]{getString(R.string.template_edit), getString(R.string.template_bj),
                    getString(R.string.template_music)};
        } else {
            titlesHasBj = new String[]{getString(R.string.template_edit),
                    getString(R.string.template_music)};
        }

        for (String title : titlesHasBj) {
            mTabEntities.add(new TabEntity(title, 0, 0));
        }
        commonTabLayout.setTabData(mTabEntities);
        commonTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (position == 1) {
                    if (mTemplateModel.HasBj) {
                        presenter.chooseBj(templateItem);
                        commonTabLayout.setCurrentTab(lastChooseCommonTabLayout);
                    } else {
                        //如果不是背景模板，那么还是有音乐
                        lastChooseCommonTabLayout = 2;
                        viewPager.setCurrentItem(1);
                        statisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "11_yj_muscle");

                    }
                } else if (position == 2) {
                    lastChooseCommonTabLayout = 2;
                    viewPager.setCurrentItem(1);
                    statisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "11_yj_muscle");
                } else {
                    lastChooseCommonTabLayout = 0;
                    viewPager.setCurrentItem(0);
                }
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
        ArrayList<View> pagerList = new ArrayList<>();
        View templateThumb = LayoutInflater.from(this).inflate(R.layout.view_template_bg, null);
        recyclerView = templateThumb.findViewById(R.id.recyclerView);
        pagerList.add(recyclerView);
        initTemplateThumb(mTemplateModel.groupSize);
        templateThumbForMusic = LayoutInflater.from(this).inflate(R.layout.view_template_music, null);
        //素材
        LinearLayout tv0 = templateThumbForMusic.findViewById(R.id.ll_0);
        //背景
        LinearLayout tv1 = templateThumbForMusic.findViewById(R.id.ll_1);
        //模板
        LinearLayout tv2 = templateThumbForMusic.findViewById(R.id.ll_2);

        if (!mTemplateModel.HasBj) {
            tv1.setVisibility(View.GONE);
        }
        if (albumType.isImage(GetPathTypeModel.getInstance().getMediaType(imgPath.get(0)))) {
            tv0.setVisibility(View.GONE);
        }
        tv0.setOnClickListener(tvMusicListener);
        tv1.setOnClickListener(tvMusicListener);
        tv2.setOnClickListener(tvMusicListener);
        cb_0 = templateThumbForMusic.findViewById(R.id.cb_0);
        cb_1 = templateThumbForMusic.findViewById(R.id.cb_1);
        cb_2 = templateThumbForMusic.findViewById(R.id.cb_2);
        Drawable drawable_news = getResources().getDrawable(R.drawable.template_choose_btn);
        Drawable drawable_news1 = getResources().getDrawable(R.drawable.template_choose_btn);
        Drawable drawable_news2 = getResources().getDrawable(R.drawable.template_choose_btn);
        //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形
        int radio_size = StringUtil.dip2px(this, 16);
        drawable_news.setBounds(0, 0, radio_size, radio_size);
        drawable_news1.setBounds(0, 0, radio_size, radio_size);
        drawable_news2.setBounds(0, 0, radio_size, radio_size);
        cb_0.setCompoundDrawables(drawable_news, null, null, null);
        cb_1.setCompoundDrawables(drawable_news1, null, null, null);
        cb_2.setCompoundDrawables(drawable_news2, null, null, null);
        cb_0.setOnClickListener(tvMusicListener);
        cb_1.setOnClickListener(tvMusicListener);
        cb_2.setOnClickListener(tvMusicListener);


        cb_2.setChecked(true);
        pagerList.add(templateThumbForMusic);
        TemplateViewPager adapter = new TemplateViewPager(pagerList);
        viewPager.setAdapter(adapter);
    }


    View.OnClickListener tvMusicListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ll_0:
                case R.id.cb_0:



                    clearCheckBox();
                    cb_0.setChecked(true);
                    nowChooseMusic = 1;
                    String path = imgPath.get(0);
                    if (albumType.isVideo(GetPathTypeModel.getInstance().getMediaType(path))) {
                        presenter.getBjMusic(primitivePath);
                        changeMusic();
                    } else {
                        cb_2.setChecked(true);
                        ToastUtil.showToast("没有素材");
                    }
                    break;
                case R.id.ll_1:
                case R.id.cb_1:
                    clearCheckBox();
                    cb_1.setChecked(true);
                    if(TextUtils.isEmpty(mTemplateModel.getBackgroundPath())){
                        chooseTemplateMusic();
                        ToastUtil.showToast("背景音乐为默认模板音乐");
                    }else{
                        nowChooseMusic = 2;
                        presenter.getBjMusic(mTemplateModel.getBackgroundPath());
                    }


                    changeMusic();

                    break;
                case R.id.ll_2:
                case R.id.cb_2:
                    changeMusic();
                    chooseTemplateMusic();
                    break;
            }
        }
    };




    private void chooseTemplateMusic() {
        clearCheckBox();
        cb_2.setChecked(true);
        nowChooseMusic = 0;
        if (isPlaying) {
            mPlayer.replaceAudio(mAudio1Path);
        }
    }


    private void changeMusic(){
        statisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "11_yj_background");
        if (real_time_preview.getVisibility() == View.VISIBLE) {
            //预览暂停状态
            if (!isPlaying) {
                showPreview(false, true);
                AnimForViewShowAndHide.getInstance().show(mContainer);
            }
        }
    }

    private void setBjMusic() {
        if (isPlaying) {
//            int progress=mPlayer.getDuration();
            mPlayer.replaceAudio(nowSpliteMusic);
//            presenter.playBGMMusic(nowSpliteMusic,progress);
        }
    }


    private void clearCheckBox() {
        cb_0.setChecked(false);
        cb_1.setChecked(false);
        cb_2.setChecked(false);
    }

    @Subscribe
    public void onEventMainThread(DownVideoPath event) {
        Observable.just(event.getPath()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
            LogUtil.d("OOM", "重新选择了视频背景,地址为" + event.getPath());
            String videoBjPath = event.getPath();
            if(TextUtils.isEmpty(videoBjPath)){
                ToastUtil.showToast("选择了默认背景");
                mTemplateModel.setHasBg("",false);
            }else{
                if(albumType.isVideo(GetPathType.getInstance().getPathType(videoBjPath))){
                    mTemplateModel.setHasBg(videoBjPath,true);
                    LogUtil.d("OOM","当前选择的位置为"+nowChooseMusic);
                    LogUtil.d("OOM","videoBjPath="+videoBjPath);
                    if(nowChooseMusic==2){
                        //如果本来就是选择的背景音乐，那么需要重新得到背景音乐
                        presenter.getBjMusic(videoBjPath);
                    }
                }else{
                    mTemplateModel.setHasBg(videoBjPath,false);
                }
            }
        });
    }


    @Subscribe
    public void onEventMainThread(showAdCallback event) {
        if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
            VideoAdManager videoAdManager = new VideoAdManager();
            String adId;
            if(BaseConstans.getOddNum()){
                adId= AdConfigs.AD_save_video;
            }else{
                adId= AdConfigs.AD_save_video2;
            }
            videoAdManager.showVideoAd(this, adId, new VideoAdCallBack() {
                @Override
                public void onVideoAdSuccess() {
                    statisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "video_ad_alert_request_sucess");
                    LogUtil.d("OOM", "onVideoAdSuccess");
                }

                @Override
                public void onVideoAdError(String s) {
                    statisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "video_ad_alert_request_fail");
                    LogUtil.d("OOM", "onVideoAdError"+s);
                    presenter.alertAlbumUpdate(false);
                }

                @Override
                public void onVideoAdClose() {
                    presenter.alertAlbumUpdate(true);
                }

                @Override
                public void onVideoAdSkip() {
                    LogUtil.d("OOM", "onVideoAdSkip");
                }

                @Override
                public void onVideoAdComplete() {
                }

                @Override
                public void onVideoAdClicked() {
                    LogUtil.d("OOM", "onVideoAdClicked");
                }
            });
        }else{
            presenter.alertAlbumUpdate(true);
        }



    }

}
