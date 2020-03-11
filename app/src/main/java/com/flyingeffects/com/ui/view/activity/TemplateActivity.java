package com.flyingeffects.com.ui.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
     * 需要素材数量
     */
    private int defaultNum;
    private String templateName;
    private String fromTo;

    @BindView(R.id.Real_time_preview)
    FrameLayout real_time_preview;

    @BindView(R.id.tv_end_time)
    TextView tv_end_time;

    @BindView(R.id.tv_start_time)
    TextView tv_start_time;

    private static final int REQUEST_SINGLE_MEDIA = 11;

    /**
     * 点击事件选择的位置
     */
    private  int pickIndex;


    /**
     * 是否是即时播放
     */
    private boolean isRealtime = true;

    private boolean isPlaying = false;

    private int nowChoosePosition;
    private int lastChoosePosition;
    private    AlphaAnimation hideAnim;


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
            defaultNum = bundle.getInt("isPicNum");
            templateFilePath = bundle.getString("templateFilePath");
            imgPath = bundle.getStringArrayList("paths");
            originalPath = bundle.getStringArrayList("originalPath");
            templateName = bundle.getString("templateName");
        }
        if (originalPath == null || originalPath.size() == 0) {
            //不需要抠图
            findViewById(R.id.ll_Matting).setVisibility(View.GONE);
        }
        mTextEditLayout = findViewById(R.id.text_edit_layout);
        mFolder = new File(templateFilePath);
        mAudio1Path = mFolder.getPath() + MUSIC_PATH;

        FileManager manager=new FileManager();
        String dir = manager.getFileCachePath(this,"");
        mTemplateViews = new ArrayList<>();
        for (int i = 0; i < defaultNum; i++) {
            listItem.add(new TemplateThumbItem("", 1, false));
        }
        SxveConstans.default_bg_path = new File(dir, "default_bj.png").getPath();
        seekBar.setOnSeekBarChangeListener(seekBarListener);
        switch_button.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (!isChecked) {
                    statisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "1_mb_bj_Cutoutopen");
                    //修改图为裁剪后的素材
                    presenter.ChangeMaterial(originalPath, defaultNum);
                } else {
                    statisticsEventAffair.getInstance().setFlag(TemplateActivity.this, "1_mb_bj_Cutoutoff");
                    //修改为裁剪前的素材
                    presenter.ChangeMaterial(imgPath, defaultNum);
                }
                if (mPlayer != null) {
                    mPlayer.pause();
                    ivPlayButton.setImageResource(R.mipmap.iv_play);
                    isPlaying = false;
                }
                showPreview(false,true);
                AnimForViewShowAndHide.getInstance().show(mContainer);
            }
        });
    }


    @Override
    protected void initAction() {
        initTemplateThumb();
        presenter.loadTemplate(mFolder.getPath(), this);
        mPlayerView.setPlayCallback(mListener);
    }

    @Override
    public void completeTemplate(TemplateModel templateModel) {
        mTemplateModel = templateModel;
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
            showPreview(false,true);
        }));
        showPreview(true,true);
    }

    @Override
    public void ChangeMaterialCallback(ArrayList<TemplateThumbItem> callbackListItem, List<String> list_all) {
        listItem.clear();
        listItem.addAll(callbackListItem);
        templateThumbAdapter.notifyDataSetChanged();
        mTemplateModel.setReplaceAllMaterial(list_all);
        mTemplateViews.get(nowChooseIndex).invalidate();
    }

    @Override
    public void returnReplaceableFilePath(String[] paths) {
        if (isPlaying) {
            if (mPlayer != null) {
                mPlayer.pause();
                ivPlayButton.setImageResource(R.mipmap.iv_play);
                isPlaying = false;
            }
        } else {
            isPlaying = true;
            ivPlayButton.setImageResource(R.mipmap.pause);
            if (real_time_preview.getVisibility() == View.VISIBLE) {
                if (mPlayer != null) {
                    mPlayer.start();
                }
            } else {
                switchTemplate(mFolder.getPath(), paths);
            }
        }

        showPreview(true,false);
    }


    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onVideoPause();
        showPreview(false,true);
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


    private void showPreview(boolean isPreview,boolean hasAnim) {
        if (isPreview) {
            if (isRealtime) {
                real_time_preview.setVisibility(View.VISIBLE);
            } else {
                videoPlayer.setVisibility(View.VISIBLE);
            }
            if(hasAnim){
                AnimForViewShowAndHide.getInstance().hide(mContainer);
            }else{
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
        if(!DoubleClick.getInstance().isFastZDYDoubleClick(1000)){
            pickIndex=model.getNowIndex();
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


    private void isFirstReplace(List<String> paths) {
        if (mTemplateViews != null && mTemplateViews.size() > 0) {
            List<String> list_all = new ArrayList<>();
            for (int i = 0; i < defaultNum; i++) {  //填满数据，为了缩略图
                if (paths.size() > i && !TextUtils.isEmpty(paths.get(i))) {
                    list_all.add(paths.get(i)); //前面的时path ，后面的为默认的path
                } else {
                    list_all.add(SxveConstans.default_bg_path);
                }
            }
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
            templateThumbAdapter.notifyDataSetChanged();
            WaitingDialog.openPragressDialog(this);
            new Thread(() -> {
                mTemplateModel.setReplaceAllFiles(list_all, TemplateActivity.this, complete -> TemplateActivity.this.runOnUiThread(() -> {
                    WaitingDialog.closePragressDialog();
                    selectGroup(0);
                    nowChooseIndex = 0;
                    templateThumbAdapter.notifyDataSetChanged();
                    if (mTemplateViews != null && mTemplateViews.size() > 0) {
                        mTemplateViews.get(nowChooseIndex).invalidate(); //提示重新绘制预览图
                    }
                }), "FIRST_MEDIA");  //批量替换图片
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


    public void initTemplateThumb() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        templateThumbAdapter = new TemplateThumbAdapter(R.layout.item_group_thumb, listItem, TemplateActivity.this);
        templateThumbAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if(view.getId()==R.id.iv_show_un_select){
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
                showPreview(false,true);
                AnimForViewShowAndHide.getInstance().show(mContainer);
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
        for (TemplateThumbItem item:listItem
             ) {
            item.setRedate(isRedate);
        }
        templateThumbAdapter.notifyDataSetChanged();
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
                if (!DoubleClick.getInstance().isFastDoubleClick()) {
                    presenter.getReplaceableFilePath();
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
        template.setReplaceableFilePaths(mSources);
        template.enableSourcePrepare();
        new Thread() {
            @Override
            public void run() {
                template.commit();
                runOnUiThread(() -> {
                    mDuration = template.realDuration();
                    seekBar.setMax(mDuration);
                    mPlayer = mPlayerView.setTemplate(template);
                    seekBar.setProgress(0);
                    mPlayer.replaceAudio(mAudio1Path);
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
                showPreview(false,true);
                ivPlayButton.setImageResource(R.mipmap.iv_play);
                seekBar.setProgress(0);
            });
        }
    };


    @Override
    public void resultFilePath(int tag, List<String> paths, boolean isCancel, ArrayList<AlbumFile> albumFileList) {
        if (!isCancel && tag == REQUEST_SINGLE_MEDIA) {
            if (originalPath == null || originalPath.size() == 0) {
                //不需要抠图
                if(imgPath.size()>lastChoosePosition){
                    imgPath.set(lastChoosePosition, paths.get(0));
                }else{
                    imgPath.add( paths.get(0));
                }
                MediaUiModel2 mediaUi2 = (MediaUiModel2) mTemplateModel.getAssets().get(lastChoosePosition).ui;
                mediaUi2.setImageAsset(paths.get(0));
                mTemplateViews.get(lastChoosePosition).invalidate();
                ModificationSingleThumbItem(paths.get(0));
            } else {
                CompressionCuttingManage manage = new CompressionCuttingManage(TemplateActivity.this, tailorPaths -> {
                    originalPath.set(lastChoosePosition, paths.get(0));
                    imgPath.set(lastChoosePosition, tailorPaths.get(0));
                    Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
                        MediaUiModel2 mediaUi2 = (MediaUiModel2) mTemplateModel.getAssets().get(lastChoosePosition).ui;
                        mediaUi2.setImageAsset(tailorPaths.get(0));
                        mTemplateViews.get(lastChoosePosition).invalidate();
                        ModificationSingleThumbItem(tailorPaths.get(0));
                    });
                });
                manage.CompressImgAndCache(paths);
            }
        }
    }
}
