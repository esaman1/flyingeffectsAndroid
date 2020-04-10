package com.flyingeffects.com.ui.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.TemplateThumbAdapter;
import com.flyingeffects.com.base.BaseActivity;
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
import com.shixing.sxve.ui.AssetDelegate;
import com.shixing.sxve.ui.SxveConstans;
import com.shixing.sxve.ui.model.GroupModel;
import com.shixing.sxve.ui.model.MediaUiModel;
import com.shixing.sxve.ui.model.MediaUiModel2;
import com.shixing.sxve.ui.model.TemplateModel;
import com.shixing.sxve.ui.model.TextUiModel;
import com.shixing.sxve.ui.view.TemplateView;
import com.shixing.sxve.ui.view.TextAssetEditLayout;
import com.shixing.sxve.ui.view.WaitingDialog;
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
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 模板页面
 * 漫画比较特殊，独立于全部逻辑之外，不过漫画只有1个图片的情况，
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
     * 原图地址,如果不需要抠图，原图地址为null
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

    /**
     * 点击事件选择的位置
     */
    private int pickIndex;


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
     * 有值表示视频
     */
    private String videoTime;


    @Override
    protected int getLayoutId() {
        return R.layout.act_template_edit;
    }

    @Override
    protected void initView() {
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
            videoTime=bundle.getString("videoTime");
            originalPath = bundle.getStringArrayList("originalPath");
            templateName = bundle.getString("templateName");
            nowTemplateIsAnim = bundle.getInt("is_anime");
        }

        if(!TextUtils.isEmpty(videoTime)){
            nowTemplateIsAnim=1;
        }

        if (originalPath == null || originalPath.size() == 0 || nowTemplateIsAnim == 1) {
            //不需要抠图
            findViewById(R.id.ll_Matting).setVisibility(View.GONE);
        }
        mTextEditLayout = findViewById(R.id.text_edit_layout);
        mFolder = new File(templateFilePath);
        mAudio1Path = mFolder.getPath() + MUSIC_PATH;

        FileManager manager = new FileManager();
        String dir = manager.getFileCachePath(this, "");
        mTemplateViews = new ArrayList<>();

        SxveConstans.default_bg_path = new File(dir, "default_bj.png").getPath();
        seekBar.setOnSeekBarChangeListener(seekBarListener);
        switch_button.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (!isChecked) {
                    statisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "1_mb_bj_Cutoutopen");
                    //修改图为裁剪后的素材
                    presenter.ChangeMaterial(originalPath, bottomButtonCount, needAssetsCount);
                } else {
                    statisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "1_mb_bj_Cutoutoff");
                    //修改为裁剪前的素材
                    presenter.ChangeMaterial(imgPath, bottomButtonCount, needAssetsCount);
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
//        initTemplateThumb();
        presenter.loadTemplate(mFolder.getPath(), this, nowTemplateIsAnim);
        mPlayerView.setPlayCallback(mListener);
    }

    @Override
    public void completeTemplate(TemplateModel templateModel) {
        initTemplateThumb(templateModel.groupSize);
        mTemplateModel = templateModel;
        if (nowTemplateIsAnim == 1) {
//            mTemplateModel.cartoonPath = originalPath.get(0);  //todo

            mTemplateModel.cartoonPath = imgPath.get(0);
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
            } else {
                WaitingDialog.openPragressDialog(TemplateActivity.this);
                new Thread(() -> presenter.getReplaceableFilePath()).start();
            }
            showPreview(true, false);
        }

    }

    @Override
    public void returnReplaceableFilePath(String[] paths) {
        switchTemplate(mFolder.getPath(), paths);
    }

    @Override
    public void getCartoonPath(String getCartoonPath) {
        this.getCartoonPath = getCartoonPath;
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
            AlbumManager.chooseWhichAlbum(TemplateActivity.this, 1, REQUEST_SINGLE_MEDIA, this, 1, "");
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

            if (nowTemplateIsAnim == 1) {
                //漫画 特殊
                TemplateThumbItem templateThumbItem = new TemplateThumbItem();
                templateThumbItem.setPathUrl(originalPath.get(0));
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

//            if (nowTemplateIsAnim == 1) {
//                //漫画需要单独前面加一个原图的值，然后第二个值需要隐藏页面
//                list_all.add(paths.get(0));
//            }


            //这里是为了替换用户操作的页面
            List<String> listAssets = new ArrayList<>();
            for (int i = 0; i < needAssetsCount; i++) {  //填满数据，为了缩略图
                if (paths.size() > i && !TextUtils.isEmpty(paths.get(i))) {
                    listAssets.add(paths.get(i)); //前面的时path ，后面的为默认的path
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
                    if (mTemplateViews != null && mTemplateViews.size() > 0) {
                        mTemplateViews.get(nowChooseIndex).invalidate(); //提示重新绘制预览图
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
        if(templateThumbAdapter!=null){
            templateThumbAdapter.notifyDataSetChanged();
        }

    }


    private void ModificationSingleThumbItem(String path) {
        TemplateThumbItem item1 = listItem.get(lastChoosePosition);
        item1.setPathUrl(path);
        listItem.set(lastChoosePosition, item1);
        templateThumbAdapter.notifyDataSetChanged();
    }


    @OnClick({R.id.tv_top_submit, R.id.iv_play, R.id.edit_view_container})
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_top_submit:
                if (!TextUtils.isEmpty(fromTo) && fromTo.equals(FromToTemplate.ISFROMSEARCH)) {
                    statisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "4_search_save", templateName);
                }
                statisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "1_mb_bj_save", templateName);
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
      for(int i=0;i<mSources.length;i++){
          LogUtil.d("OOM","路徑為"+mSources[i]);

      }


        template.setReplaceableFilePaths(mSources);
        template.enableSourcePrepare();
        new Thread() {
            @Override
            public void run() {
                template.commit();
                runOnUiThread(() -> {
                    new Handler().post(WaitingDialog::closePragressDialog);
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
        if (!isCancel && tag == REQUEST_SINGLE_MEDIA && paths != null && paths.size() > 0) {
            if (originalPath == null || originalPath.size() == 0) {
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
            } else {
                boolean hasCache = nowTemplateIsAnim != 1;
                CompressionCuttingManage manage = new CompressionCuttingManage(TemplateActivity.this, templateId, hasCache, tailorPaths -> {
                    originalPath.set(lastChoosePosition, paths.get(0));
                    imgPath.set(lastChoosePosition, tailorPaths.get(0));
                    Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {

                        if (nowTemplateIsAnim == 1) {
                            //如果是漫画，逻辑会变
                            MediaUiModel2 mediaUi1 = (MediaUiModel2) mTemplateModel.getAssets().get(0).ui;
                            mediaUi1.setImageAsset(tailorPaths.get(0));

                            MediaUiModel2 mediaUi2 = (MediaUiModel2) mTemplateModel.getAssets().get(1).ui;
                            mediaUi2.setImageAsset(paths.get(0));

                            mTemplateViews.get(lastChoosePosition).invalidate();
                            ModificationSingleThumbItem(paths.get(0));
                        } else {
                            int total = mTemplateModel.getAssetsSize() - 1;
                            //倒敘
                            int nowChooseIndex = total - pickIndex;
                            MediaUiModel2 mediaUi2 = (MediaUiModel2) mTemplateModel.getAssets().get(nowChooseIndex).ui;
                            mediaUi2.setImageAsset(tailorPaths.get(0));
                            mTemplateViews.get(lastChoosePosition).invalidate();
                            ModificationSingleThumbItem(tailorPaths.get(0));
                        }

                    });
                });
                manage.ToMatting(paths);
            }
        }
    }
}
