package com.flyingeffects.com.ui.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.TemplateGridViewAdapter;
import com.flyingeffects.com.adapter.TemplateViewPager;
import com.flyingeffects.com.adapter.listViewForVideoThumbAdapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.commonlyModel.GetPathType;
import com.flyingeffects.com.commonlyModel.GetVideoCover;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.enity.AllStickerData;
import com.flyingeffects.com.enity.StickerList;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.CompressionCuttingManage;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.mediaManager;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.model.CreationTemplateMvpCallback;
import com.flyingeffects.com.ui.view.activity.CreationTemplatePreviewActivity;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.screenUtil;
import com.flyingeffects.com.view.HorizontalListView;
import com.flyingeffects.com.view.StickerView;
import com.flyingeffects.com.view.lansongCommendView.StickerItemOnitemclick;
import com.glidebitmappool.GlideBitmapPool;
import com.lansosdk.box.ViewLayerRelativeLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shixing.sxve.ui.adapter.TimelineAdapter;
import com.shixing.sxve.ui.albumType;
import com.shixing.sxve.ui.view.WaitingDialog;
import com.shixing.sxve.ui.view.WaitingDialogProgressNowAnim;
import com.shixing.sxve.ui.view.WatingDialogProgressForTime;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static com.flyingeffects.com.manager.FileManager.saveBitmapToPath;


/**
 * description ：使用蓝松的drawPadView来绘制页面，实现方式为一个主视频图层加上多个动态的mv图层+ 多个图片图层，最后渲染出来视频
 * creation date: 2020/3/12
 * param :
 * user : zhangtongju
 */
public class CreationTemplateMvpModel {
    public final PublishSubject<ActivityLifeCycleEvent> lifecycleSubject = PublishSubject.create();
    private CreationTemplateMvpCallback callback;
    private Context context;
    private List<View> listForInitBottom = new ArrayList<>();
    private String mVideoPath;
    private ViewLayerRelativeLayout viewLayerRelativeLayout;
    private ArrayList<AnimStickerModel> listForStickerView = new ArrayList<>();
    private boolean isDestroy = false;
    private RecyclerView list_thumb;
    private VideoInfo videoInfo;
    private String mGifFolder;
    private String soundFolder;
    private Vibrator vibrator;
    private String mImageCopyFolder;
    private boolean isCheckedMatting = true;
    private HorizontalListView hListView;
    //需要裁剪视频的集合
    private ArrayList<videoType> cutVideoPathList = new ArrayList<>();
    private backgroundDraw backgroundDraw;
    //    WaitingDialogProgressNowAnim progressNowAnim;
//    WatingDialogProgressForTime progressNowAnim;
    private WaitingDialogProgressNowAnim dialog;
    private ArrayList<AllStickerData> listAllSticker = new ArrayList<>();
    /**
     * 视频默认声音
     */
    private String videoVoicePath;
    /**
     * 是否抠图,true 抠图
     */
    private boolean isMatting = true;


    /**
     * 默认视频时长,如果没选择背景的时候会用到
     */
    private int defaultVideoDuration = 0;

    /***
     * originalPath  初始化第一张的时长
     */
    private String originalPath;

    public CreationTemplateMvpModel(Context context, CreationTemplateMvpCallback callback, String mVideoPath, ViewLayerRelativeLayout viewLayerRelativeLayout, String originalPath) {
        this.context = context;
        this.callback = callback;
        this.originalPath = originalPath;
        this.mVideoPath = mVideoPath;
        dialog = new WaitingDialogProgressNowAnim(context);
        this.viewLayerRelativeLayout = viewLayerRelativeLayout;
        vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        if (!TextUtils.isEmpty(mVideoPath)) {
            videoInfo = getVideoInfo.getInstance().getRingDuring(mVideoPath);
        }
        FileManager fileManager = new FileManager();
        mGifFolder = fileManager.getFileCachePath(context, "gifFolder");
        soundFolder = fileManager.getFileCachePath(context, "soundFolder");
        mImageCopyFolder = fileManager.getFileCachePath(context, "imageCopy");
    }


    private void showVibrator() {
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(5);  //设置手机振动
        }
    }

    /**
     * description ：单独设置背景视频，有可能用户第一次进来不选择背景视频
     * creation date: 2020/4/22
     * user : zhangtongju
     */
    public void setmVideoPath(String mVideoPath) {
        if (!TextUtils.isEmpty(mVideoPath)) {
            this.mVideoPath = mVideoPath;
            videoInfo = getVideoInfo.getInstance().getRingDuring(mVideoPath);
        } else {
            this.mVideoPath = null;
            videoInfo = null;
        }

    }


    public void CheckedChanged(boolean isChecked) {
        this.isCheckedMatting = isChecked;
        MattingChange(isChecked);
    }

    public void initStickerView(String imagePath, String originalPath) {
        new Handler().postDelayed(() -> addSticker(imagePath, true, true, true, originalPath, false, null), 500);
    }


    public void showGifAnim(boolean isShow) {
        if (listForStickerView != null && listForStickerView.size() > 0) {
            for (AnimStickerModel stickerModel : listForStickerView
            ) {
                StickerView stickerView = stickerModel.getStickerView();
                if (stickerView != null) {
                    if (isShow) {
                        stickerView.start();
                    } else {
                        stickerView.pause();
                    }
                }
            }
        }
    }


    public void GetVideoCover(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        Bitmap mBitmap = retriever.getFrameAtTime(0);
        String fileName = mImageCopyFolder + File.separator + UUID.randomUUID() + ".png";
        BitmapManager.getInstance().saveBitmapToPath(mBitmap, fileName, isSuccess -> {
            CompressionCuttingManage manage = new CompressionCuttingManage(context, "", false, tailorPaths -> {
                callback.getVideoCover(tailorPaths.get(0), path);
            });
            List mattingPath = new ArrayList();
            mattingPath.add(fileName);
            manage.ToMatting(mattingPath);
            GlideBitmapPool.putBitmap(mBitmap);
        });
    }


    public void scrollToPosition(int position) {
        linearLayoutManager.scrollToPositionWithOffset(position, 0);
    }


    private TemplateGridViewAdapter gridAdapter;
    private List<StickerList> listForSticker = new ArrayList<>();
    private int selectPage = 1;
    private int perPageCount = 20;
    private SmartRefreshLayout smartRefreshLayout;
    private boolean isRefresh = true;

    public void initBottomLayout(ViewPager viewPager) {
        View templateThumbView = LayoutInflater.from(context).inflate(R.layout.view_template_paster, viewPager, false);
        smartRefreshLayout = templateThumbView.findViewById(R.id.smart_refresh_layout);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            isRefresh = true;
            refreshLayout.setEnableLoadMore(true);
            selectPage = 1;
            requestStickersList(true);
        });
        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
            isRefresh = false;
            selectPage++;
            requestStickersList(false);
        });

        GridView gridView = templateThumbView.findViewById(R.id.gridView);
        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            callback.needPauseVideo();
            modificationSingleItemIsChecked(i);
            if (i == 0) {
                //删除选择的帖子
                deleteAllSticker();
                if (UiStep.isFromDownBj) {
                    statisticsEventAffair.getInstance().setFlag(context, " 5_mb_bj_Stickeroff");
                } else {
                    statisticsEventAffair.getInstance().setFlag(context, " 6_customize_bj_Stickeroff");
                }
            } else {
                if (UiStep.isFromDownBj) {
                    statisticsEventAffair.getInstance().setFlag(context, " 5_mb_bj_Sticker", listForSticker.get(i).getTitle());
                } else {
                    statisticsEventAffair.getInstance().setFlag(context, " 6_customize_bj_Sticker", listForSticker.get(i).getTitle());
                }
                downSticker(listForSticker.get(i).getImage(), listForSticker.get(i).getId(), i);
            }

        });
        gridAdapter = new TemplateGridViewAdapter(listForSticker, context);
        gridView.setAdapter(gridAdapter);
        listForInitBottom.add(templateThumbView);
        TemplateViewPager adapter = new TemplateViewPager(listForInitBottom);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }


    private void finishData() {
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }

    private void deleteAllSticker() {
        if (listForStickerView != null && listForStickerView.size() > 0) {
            for (AnimStickerModel stickerModel : listForStickerView
            ) {
                StickerView stickerView = stickerModel.getStickerView();
                if (stickerView != null && !stickerView.getComeFrom()) {
                    stickerView.stop();
                    viewLayerRelativeLayout.removeView(stickerView);
                }
            }
        }
    }


    /*
     * @Author Zhangtj
     * @Date 2020/3/21
     * @Des 抠图和原图之间切换  isMatting 是否抠图
     */
    private void MattingChange(boolean isMatting) {
        this.isMatting = isMatting;
        if (listForStickerView != null && listForStickerView.size() > 0) {
            for (AnimStickerModel stickerModel : listForStickerView
            ) {
                StickerView stickerView = stickerModel.getStickerView();
                if (stickerView != null && stickerView.getComeFrom()) {
                    if (isMatting) {
                        stickerView.mattingChange(stickerView.getClipPath());
                    } else {
                        stickerView.mattingChange(stickerView.getOriginalPath());
                    }
                }
            }
        }
    }


    /**
     * 下载帖子功能
     *
     * @param path     下载地址
     * @param imageId  gif 保存的图片id
     * @param position 当前点击的那个item ，主要用来更新数据
     */
    private void downSticker(String path, String imageId, int position) {
        WaitingDialog.openPragressDialog(context);
        if (path.endsWith(".gif")) {
            String finalPath = path;
            String format = finalPath.substring(finalPath.length() - 4);
            String fileName = mGifFolder + File.separator + imageId + format;
            File file = new File(fileName);
            if (file.exists()) {
                //如果已经下载了，就用已经下载的，但是如果已经展示了，就不能复用，需要类似于复制功能，只针对gif
                if (nowStickerHasChoosse(imageId, path)) {
                    String copyName = mGifFolder + File.separator + System.currentTimeMillis() + format;
                    copyGif(fileName, copyName, false, null, fileName);
                    WaitingDialog.closePragressDialog();
                    return;
                } else {
                    addSticker(fileName, false, false, false, null, false, null);
                    WaitingDialog.closePragressDialog();
                    return;
                }

            }
            Observable.just(path).map(s -> {
                File file1 = null;
                try {
                    file1 = Glide.with(context)
                            .load(finalPath)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return file1;
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(path1 -> {
                try {
                    if (path1 != null) {
                        FileUtil.copyFile(path1, fileName);
                        addSticker(fileName, false, false, false, null, false, null);
                        WaitingDialog.closePragressDialog();
                        modificationSingleItem(position);
                    } else {
                        WaitingDialog.closePragressDialog();
                        ToastUtil.showToast("请重试");
                    }

                } catch (IOException e) {
                    WaitingDialog.closePragressDialog();
                    e.printStackTrace();
                }
            });

        } else {
            new Thread(() -> {

                Bitmap originalBitmap = null;
                FutureTarget<Bitmap> futureTarget =
                        Glide.with(BaseApplication.getInstance())
                                .asBitmap()
                                .load(path)
                                .submit(720, 1280);
                try {
                    originalBitmap = futureTarget.get();
                    Bitmap finalOriginalBitmap = originalBitmap;
                    Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Integer>() {
                        @Override
                        public void call(Integer integer) {
                            WaitingDialog.closePragressDialog();
                            String aa = path.substring(path.length() - 4);
                            String copyName = mGifFolder + File.separator + System.currentTimeMillis() + aa;
                            saveBitmapToPath(finalOriginalBitmap, copyName, new FileManager.saveBitmapState() {
                                @Override
                                public void succeed(boolean isSucceed) {
                                    modificationSingleItem(position);
                                    addSticker(copyName, false, false, false, null, false, null);
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    LogUtil.d("oom", e.getMessage());
                }
                Glide.with(BaseApplication.getInstance()).clear(futureTarget);
            }).start();
        }
    }


    /**
     * 当前的item 是否已经被选中上了预览页面
     */
    private boolean nowStickerHasChoosse(String id, String imagePath) {
        for (int i = 0; i < viewLayerRelativeLayout.getChildCount(); i++) {
            StickerView stickerView = (StickerView) viewLayerRelativeLayout.getChildAt(i);
            String path = stickerView.getResPath();
            String format = imagePath.substring(imagePath.length() - 4);
            if (imagePath.endsWith(".gif")) {
                String copyName = mGifFolder + File.separator + id + format;
                if (path.equals(copyName)) {
                    return true;
                }

            }
        }

        return false;

    }


    /**
     * description ：添加一个新的sticker ,
     * creation date: 2020/3/23
     *
     * @param path         资源地址
     * @param hasReplace   是有有替换功能，目前替换功能只针对用户从相册里面选择的，
     * @param isFirstAdd    第一个贴纸
     * @param isFromAubum  是否来自于相册选择的素材，而不是自己点击下载的，
     * @param originalPath 如果是相册选择的，没抠图的的地址，
     * @param isCopy       是否来自复制功能
     * user : zhangtongju
     */

    int stickerViewID;

    private void addSticker(String path, boolean isFirstAdd, boolean hasReplace, boolean isFromAubum, String originalPath, boolean isCopy, StickerView copyStickerView) {
        closeAllAnim();
        StickerView stickView = new StickerView(context);
        stickerViewID++;
        stickView.setId(stickerViewID);
        stickView.setOnitemClickListener(new StickerItemOnitemclick() {
            @Override
            public void stickerOnclick(int type) {
                if (type == StickerView.LEFT_TOP_MODE) {//刪除
                    viewLayerRelativeLayout.removeView(stickView);
                    int nowId = stickView.getId();
                    if (stickView.isFirstAddSticker()) {
                        if (stickView.isOpenVoice()) {
                            stickView.setOpenVoice(false);
                            callback.getBgmPath("");
                        }
                    }
                    delectedListForSticker(nowId);
                } else if (type == StickerView.RIGHT_TOP_MODE) {
                    stickView.dismissFrame();
                    //copy
                    copyGif(stickView.getResPath(), path, stickView.getComeFrom(), stickView, stickView.getOriginalPath());
                    if (!TextUtils.isEmpty(stickView.getOriginalPath())) {
                        if (albumType.isVideo(GetPathType.getInstance().getMediaType(stickView.getOriginalPath()))) {
                            if (UiStep.isFromDownBj) {
                                statisticsEventAffair.getInstance().setFlag(context, "7_plusone");
                            } else {
                                statisticsEventAffair.getInstance().setFlag(context, "8_plusone");
                            }
                        }
                    }
                } else if (type == StickerView.RIGHT_CENTER_MODE) {
                    showVibrator();
                    if (!stickView.isOpenVoice) {
                        //打开声音
                        stickView.setOpenVoice(true);
                        stickView.setRightCenterBitmapForChangeIcon(context.getDrawable(R.mipmap.sticker_open_voice));
                        getVideoVoice(stickView.getOriginalPath(), soundFolder);
                        if (UiStep.isFromDownBj) {
                            statisticsEventAffair.getInstance().setFlag(context, "7_open");
                        } else {
                            statisticsEventAffair.getInstance().setFlag(context, "8_open");
                        }
                    } else {
                        //关闭声音
                        videoVoicePath = "";
                        stickView.setOpenVoice(false);
                        stickView.setRightCenterBitmapForChangeIcon(context.getDrawable(R.mipmap.sticker_close_voice));
                        callback.getBgmPath("");
                        if (UiStep.isFromDownBj) {
                            statisticsEventAffair.getInstance().setFlag(context, "7_turnoff");
                        } else {
                            statisticsEventAffair.getInstance().setFlag(context, "8_turnoff");
                        }

                    }

                } else if (type == StickerView.LEFT_BOTTOM_MODE) {
                    if (UiStep.isFromDownBj) {
                        statisticsEventAffair.getInstance().setFlag(context, " 5_mb_bj_replace");
                    } else {
                        statisticsEventAffair.getInstance().setFlag(context, " 6_customize_bj_replace");
                    }
                    //切換素材
                    AlbumManager.chooseAlbum(context, 1, 0, (tag, paths, isCancel, albumFileList) -> {
                        if (!isCancel) {
                            if (albumType.isVideo(GetPathType.getInstance().getPathType(paths.get(0)))) {
                                GetVideoCover getVideoCover = new GetVideoCover(context);
                                getVideoCover.getCover(paths.get(0), path1 -> {
                                    Observable.just(path1).subscribeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
                                        stickView.setOriginalPath(paths.get(0));
                                        stickView.setClipPath(s);
                                        if (!isCheckedMatting) {
                                            stickView.changeImage(paths.get(0), false);
                                        } else {
                                            stickView.changeImage(s, false);
                                        }

                                        if (stickView.isFirstAddSticker()) {
                                            stickView.setRightCenterBitmap(context.getDrawable(R.mipmap.sticker_close_voice));
                                            callback.changFirstVideoSticker(paths.get(0));
                                            callback.getBgmPath("");
                                        }

                                    });
                                });
                            } else {
                                CompressionCuttingManage manage = new CompressionCuttingManage(context, "", tailorPaths -> {
                                    Observable.just(tailorPaths.get(0)).subscribeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
                                        stickView.setOriginalPath(paths.get(0));
                                        stickView.setClipPath(s);
                                        if (!isCheckedMatting) {
                                            stickView.changeImage(paths.get(0), false);
                                        } else {
                                            stickView.changeImage(s, false);
                                        }
                                    });
                                });
                                manage.ToMatting(paths);


                                if (stickView.isFirstAddSticker()) {
                                    if (stickView.isOpenVoice()) {
                                        stickView.setOpenVoice(false);
                                        stickView.setRightCenterBitmap(context.getDrawable(R.mipmap.sticker_close_voice));
                                        callback.getBgmPath("");
                                    }
                                }

                            }
                        }
                    }, "");


                }
            }

            @Override
            public void stickerMove() {
                closeAllAnim();
                if (stickView.getParent() != null) {
                    ViewGroup vp = (ViewGroup) stickView.getParent();
                    if (vp != null) {
                        vp.removeView(stickView);
                    }
                }
                viewLayerRelativeLayout.addView(stickView);
                stickView.start();
            }
        });
        stickView.setRightTopBitmap(context.getDrawable(R.mipmap.sticker_copy));
        stickView.setLeftTopBitmap(context.getDrawable(R.drawable.sticker_delete));
        stickView.setRightBottomBitmap(context.getDrawable(R.mipmap.sticker_redact));

        stickView.setComeFromAlbum(isFromAubum);
        if (isFromAubum) {
            stickView.setClipPath(path);
            stickView.setOriginalPath(originalPath);
            if (albumType.isVideo(GetPathType.getInstance().getPathType(stickView.getOriginalPath()))) {
                stickView.setNowMaterialIsVideo(true);
            } else {
                stickView.setNowMaterialIsVideo(false);
            }
        }

        if (isFirstAdd) {
            stickView.setFirstAddSticker(true);
            if (albumType.isVideo(GetPathType.getInstance().getPathType(stickView.getOriginalPath()))) {

                LogUtil.d("OOM", "mVideoPath=" + mVideoPath);
                if (!TextUtils.isEmpty(mVideoPath)) {
                    LogUtil.d("OOM", "默认是有背景");
                    //有背景音乐
                    stickView.setRightCenterBitmap(context.getDrawable(R.mipmap.sticker_close_voice));
                    callback.getBgmPath("");
                    stickView.setOpenVoice(false);
                } else {
                    LogUtil.d("OOM", "默认是没有背景");
                    //无背景音乐
                    stickView.setRightCenterBitmap(context.getDrawable(R.mipmap.sticker_open_voice));
                    stickView.setOpenVoice(true);
                    getVideoVoice(stickView.getOriginalPath(), soundFolder);
                }
            }
        }
        if (hasReplace) {
            stickView.setLeftBottomBitmap(context.getDrawable(R.mipmap.sticker_change));
        }


        if (isCopy && copyStickerView != null) {
            //来做复制或者来自联系点击下面的item
            StickerView.isFromCopy fromCopy = new StickerView.isFromCopy();
            fromCopy.setScale(copyStickerView.getScale());
            fromCopy.setDegree(copyStickerView.getRotateAngle());
            fromCopy.setTranX(copyStickerView.getCenterX());
            fromCopy.setTranY(copyStickerView.getCenterY());
            stickView.setImageRes(path, false, fromCopy);
            stickView.showFrame();
        } else {
            stickView.setImageRes(path, true, null);
        }
        AnimStickerModel animStickerModel = new AnimStickerModel(context, viewLayerRelativeLayout, stickView);
        //如果关闭了原图的，并且是用户添加的，那么就关闭扣的图，不过每次都是默认抠图的
        if (isFromAubum && !isCheckedMatting) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stickView.changeImage(originalPath, false);
                }
            }, 500);
        }


        if (isFromAubum && isCopy && isCheckedMatting) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stickView.changeImage(path, false);
                }
            }, 500);

        }


        listForStickerView.add(animStickerModel);
        if (stickView.getParent() != null) {
            ViewGroup vp = (ViewGroup) stickView.getParent();
            if (vp != null) {
                vp.removeAllViews();
            }
        }
        viewLayerRelativeLayout.addView(stickView);

        if (isFirstAdd) {
            callback.isFirstAddSuccess();
        }


    }


    private void delectedListForSticker(int id) {

        for (int i = 0; i < listForStickerView.size(); i++) {

            AnimStickerModel model = listForStickerView.get(i);
            StickerView stackView = model.getStickerView();
            if (stackView.getId() == id) {
                listForStickerView.remove(i);
            }
        }


    }


    private void closeAllAnim() {
        ArrayList<AllStickerData> list = new ArrayList<>();
        for (int i = 0; i < viewLayerRelativeLayout.getChildCount(); i++) {
            StickerView stickerView = (StickerView) viewLayerRelativeLayout.getChildAt(i);
            stickerView.pause();
        }
    }


    private void copyGif(String getResPath, String path, boolean isFromAubum, StickerView stickerView, String OriginalPath) {
        try {
            String copyName = null;
            if (getResPath.endsWith(".gif")) {
                if (UiStep.isFromDownBj) {
                    statisticsEventAffair.getInstance().setFlag(context, "5_mb_sticker_plus");
                } else {
                    statisticsEventAffair.getInstance().setFlag(context, "6_mb_sticker_plus");
                }
                copyName = mGifFolder + File.separator + System.currentTimeMillis() + "synthetic.gif";
                String finalCopyName = copyName;
                FileUtil.copyFile(new File(getResPath), copyName, new FileUtil.copySucceed() {
                    @Override
                    public void isSucceed() {
                        addSticker(finalCopyName, false, false, isFromAubum, getResPath, true, stickerView);
                    }
                });
            } else {
                if (UiStep.isFromDownBj) {
                    statisticsEventAffair.getInstance().setFlag(context, "5_mb_bj_plus one");
                } else {
                    statisticsEventAffair.getInstance().setFlag(context, "6_customize_bj_plus one");
                }
                String aa = path.substring(path.length() - 4);
                copyName = mImageCopyFolder + File.separator + System.currentTimeMillis() + aa;
                String finalCopyName1 = copyName;
                FileUtil.copyFile(new File(path), copyName, new FileUtil.copySucceed() {
                    @Override
                    public void isSucceed() {
                        addSticker(finalCopyName1, false, true, isFromAubum, OriginalPath, true, stickerView);
                    }
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void onDestroy() {
        isDestroy = true;
    }

    private TimelineAdapter mTimelineAdapter;
    private int mScrollX;
    private LinearLayoutManager linearLayoutManager;

    public void initVideoProgressView(HorizontalListView hListView) {
        this.hListView = hListView;
        //动态设置距离左边的位置
        if (videoInfo != null) {
            initSingleThumbSize(videoInfo.getVideoWidth(), videoInfo.getVideoHeight(), videoInfo.getDuration(), videoInfo.getDuration() / 2, mVideoPath);
        } else {
            getPlayVideoDuration();
            initSingleThumbSize(720, 1280, defaultVideoDuration, defaultVideoDuration / 2, "");
        }
    }


    private List<Integer> perSticker = new ArrayList<>();

    private void getPlayVideoDuration() {
        defaultVideoDuration = 0;
        LogUtil.d("OOM", " viewLayerRelativeLayout.getChildCount())=" + viewLayerRelativeLayout.getChildCount());
        perSticker.clear();
        if (viewLayerRelativeLayout.getChildCount() > 0) {
            for (int i = 0; i < viewLayerRelativeLayout.getChildCount(); i++) {
                StickerView stickerView = (StickerView) viewLayerRelativeLayout.getChildAt(i);
                if (!TextUtils.isEmpty(stickerView.getOriginalPath())) {
                    if (albumType.isVideo(GetPathType.getInstance().getPathType(stickerView.getOriginalPath()))) {
                        VideoInfo materialVideoInfo = getVideoInfo.getInstance().getRingDuring(stickerView.getOriginalPath());
                        LogUtil.d("OOM", "materialVideoInfo.getDuration()=" + materialVideoInfo.getDuration());
                        perSticker.add(materialVideoInfo.getDuration());
                    }
                }
            }
        } else {
            //只有第一次初始化的时候，可能为0.因为viewLayerRelativeLayout还没加载进入数据，所有就需要手动加上
            if (!TextUtils.isEmpty(originalPath)) {
                if (albumType.isVideo(GetPathType.getInstance().getPathType(originalPath))) {
                    VideoInfo materialVideoInfo = getVideoInfo.getInstance().getRingDuring(originalPath);
                    LogUtil.d("OOM", "materialVideoInfo.getDuration()=" + materialVideoInfo.getDuration());
                    perSticker.add(materialVideoInfo.getDuration());
                }
            }

        }
        //只有一个的情况就不需要比较大小了
        if (perSticker != null && perSticker.size() > 0) {
            if (perSticker.size() == 1) {
                defaultVideoDuration = perSticker.get(0);
            } else {
                for (int duration : perSticker
                ) {
                    if (defaultVideoDuration < duration) {
                        defaultVideoDuration = duration;
                    }
                }
            }
            LogUtil.d("OOM", "获得贴纸时长为" + defaultVideoDuration);
        } else {
            LogUtil.d("OOM", "获得贴纸时长失败");
            defaultVideoDuration = 10 * 1000;
        }

//        callback.showRenderVideoTime(defaultVideoDuration);
    }


    private int mTotalWidth;
    float allDistance;

    private void initSingleThumbSize(int width, int height, float duration, float mTemplateDuration, String mVideoPath) {
        // 需要截取的listWidth宽度
        int listWidth = hListView.getWidth() - hListView.getPaddingLeft() - hListView.getPaddingRight();
        int listHeight = hListView.getHeight();
        float scale = (float) listHeight / height;
        int thumbWidth = (int) (scale * width);
        //其中listWidth表示当前截取的大小
        int thumbCount = (int) (listWidth * (duration / mTemplateDuration) / thumbWidth);
        thumbCount = thumbCount > 0 ? thumbCount : 0;
        //每帧所占的时间
        final int interval = (int) (duration / thumbCount);
        int[] mTimeUs = new int[thumbCount];
        for (int i = 0; i < thumbCount; i++) {
            mTimeUs[i] = i * interval * 1000;
        }
        mTotalWidth = thumbWidth * thumbCount;
        callback.getVideoDuration((int) duration, thumbCount);
        int dp40 = screenUtil.dip2px(context, 43);
        int screenWidth = screenUtil.getScreenWidth((Activity) context);
        listViewForVideoThumbAdapter adapter;
        if (!TextUtils.isEmpty(mVideoPath)) {
            adapter = new listViewForVideoThumbAdapter(context, mTimeUs, Uri.fromFile(new File(mVideoPath)), thumbWidth, listHeight, screenWidth / 2, screenWidth / 2 - dp40);
        } else {
            adapter = new listViewForVideoThumbAdapter(context, mTimeUs, null, thumbWidth, listHeight, screenWidth / 2, screenWidth / 2 - dp40);
        }

        hListView.setAdapter(adapter);
        int realWidth = (screenWidth - screenUtil.dip2px(context, 43)) * 2;
        LogUtil.d("OOM", "realWidth=" + realWidth);
        hListView.setOnScrollListener(mNextX -> {
            float preF = mNextX / realWidth;
            int frame = (int) (duration * preF);
            LogUtil.d("OOM", "preF=" + preF);
            LogUtil.d("OOM", "frame=" + frame);
            callback.setgsyVideoProgress(frame);
        });


    }


    public void initVideoProgressView(RecyclerView list_thumb) {
        //动态设置距离左边的位置
        int screenWidth = screenUtil.getScreenWidth((Activity) context);
        int dp40 = screenUtil.dip2px(context, 40);
        list_thumb.setPadding(screenWidth / 2 - dp40, 0, 0, 0);
        this.list_thumb = list_thumb;
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        list_thumb.setLayoutManager(linearLayoutManager);
        mTimelineAdapter = new TimelineAdapter();
        mTimelineAdapter.marginRight(screenWidth / 2);
        list_thumb.setAdapter(mTimelineAdapter);
        list_thumb.setHasFixedSize(true);
        list_thumb.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                mScrollX += dx;
                float percent = (float) mScrollX / mTotalWidth;
                LogUtil.d("oom", "percent=" + percent);
                int progress = (int) (videoInfo.getDuration() * percent);
                callback.setgsyVideoProgress(progress);
            }
        });
        initSingleThumbSize(videoInfo.getVideoWidth(), videoInfo.getVideoHeight(), videoInfo.getDuration(), videoInfo.getDuration() / 2, mVideoPath);

    }


    /**
     * description ：保存视频采用蓝松sdk提供的在保存功能
     * creation date: 2020/3/12
     * user : zhangtongju
     */

    private boolean isIntoSaveVideo = false;

    public void toSaveVideo(String imageBjPath) {

        if (!isIntoSaveVideo) {
            isIntoSaveVideo = true;
            listAllSticker.clear();
            cutSuccessNum = 0;
            cutVideoPathList.clear();
            backgroundDraw = new backgroundDraw(context, mVideoPath, videoVoicePath, imageBjPath, new backgroundDraw.saveCallback() {
                @Override
                public void saveSuccessPath(String path, int progress) {

                    if (!TextUtils.isEmpty(path)) {
                        dialog.closePragressDialog();
                        //成功后的回调
                        Intent intent = new Intent(context, CreationTemplatePreviewActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("path", path);
                        context.startActivity(intent);
                        Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> new Handler().postDelayed(() -> isIntoSaveVideo = false,500));
                    } else {
                        if (progress == 10000) {
                            isIntoSaveVideo = false;
                            //渲染失败
                            dialog.closePragressDialog();
                        } else {
                            dialogProgress = progress;
                            handler.sendEmptyMessage(1);
                        }
                    }
                }
            });


            for (int i = 0; i < viewLayerRelativeLayout.getChildCount(); i++) {
                StickerView stickerView = (StickerView) viewLayerRelativeLayout.getChildAt(i);
                AllStickerData stickerData = new AllStickerData();
                stickerData.setRotation(stickerView.getRotateAngle());
                stickerData.setScale(stickerView.getScale());
                stickerData.setTranslationX(stickerView.getTranslationX());
                stickerData.setTranslationy(stickerView.getTranslationY());
                if (!TextUtils.isEmpty(stickerView.getOriginalPath())) {
                    String pathType = GetPathTypeModel.getInstance().getMediaType(stickerView.getOriginalPath());
                    stickerData.setVideo(albumType.isVideo(pathType));
                }
                if (stickerView.getComeFrom()) {
                    //来自相册，不是gif
                    if (isMatting) {
                        stickerData.setPath(stickerView.getClipPath());
                        stickerData.setOriginalPath(stickerView.getOriginalPath());
                        VideoInfo materialVideoInfo = getVideoInfo.getInstance().getRingDuring(stickerView.getOriginalPath());
                        stickerData.setDuration(materialVideoInfo.getDuration());

                    } else { //这里也会出现蓝松一样的，相同地址只有一个图层
                        stickerData.setPath(stickerView.getOriginalPath());
                        stickerData.setOriginalPath(stickerView.getOriginalPath());
                        VideoInfo materialVideoInfo = getVideoInfo.getInstance().getRingDuring(stickerView.getOriginalPath());
                        int materialDuration = materialVideoInfo.getDuration();
                        int needDuration = 0;
                        if (videoInfo != null) {
                            if (videoInfo.getDuration() < materialDuration) {
                                needDuration = videoInfo.getDuration();
                            } else {
                                needDuration = materialDuration;
                            }
                        } else {
                            needDuration = materialDuration;
                        }
                        stickerData.setDuration(needDuration);
                    }
                } else {
                    stickerData.setPath(stickerView.getResPath());
                }
                listAllSticker.add(stickerData);
            }

            if (listAllSticker.size() == 0) {
                isIntoSaveVideo = false;
                Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> new Handler().post(() -> Toast.makeText(context,"你未选择素材",Toast.LENGTH_SHORT).show()));
                return;
            }

            for (int i = 0; i < listAllSticker.size(); i++) {
                if (defaultVideoDuration < listAllSticker.get(i).getDuration()) {
                    defaultVideoDuration = (int) listAllSticker.get(i).getDuration();
                }
                if (listAllSticker.get(i).isVideo()) {
                    cutVideoPathList.add(new videoType(listAllSticker.get(i).getOriginalPath(), i, listAllSticker.get(i).getDuration()));
                }
            }
            if (cutVideoPathList.size() == 0) {
                dialog.openProgressDialog();
                //都不是视频的情况下，就直接渲染
                backgroundDraw.toSaveVideo(listAllSticker, isMatting);
            } else {
                dialog.openProgressDialog();
                cutList.clear();
                if (videoInfo != null) {
                    cutVideo(cutVideoPathList.get(0), videoInfo.getDuration(), cutVideoPathList.get(0).getDuration());
                } else {
                    //没选择背景默认裁剪10秒
                    cutVideo(cutVideoPathList.get(0), defaultVideoDuration, cutVideoPathList.get(0).getDuration());
                }
            }
        }


    }


    //裁剪成功数量
    private int cutSuccessNum;
    private ArrayList<String> cutList = new ArrayList<>();


    /**
     * description ：裁剪视频，如果视频小于模板时长，那么就裁剪当前时长，如果视频大于当前模板时长，那么就裁剪模板时长
     * creation date: 2020/4/21
     * user : zhangtongju
     */
    private void cutVideo(videoType videoType, long duration, long materialDuration) {
        LogUtil.d("oom3", "需要裁剪的时长为" + materialDuration);
        videoCutDurationForVideoOneDo.getInstance().CutVideoForDrawPadAllExecute2(context, materialDuration, videoType.getPath(), 0, new videoCutDurationForVideoOneDo.isSuccess() {
            @Override
            public void progresss(int progress) {
                float positionF = progress / (float) 100;
                Log.d("OOM", "裁剪的进度百分比为" + positionF);
                float prencent = 5 / (float) (cutVideoPathList.size() + 1);
                Log.d("OOM", "裁剪有几份" + prencent);
                int position = (int) ((int) (positionF * prencent) + cutSuccessNum * prencent);
                Log.d("OOM", "裁剪的总进度为" + position);
                dialogProgress = position;
                handler.sendEmptyMessage(1);
            }

            @Override
            public void isSuccess(boolean isSuccess, String path) {
                int position = videoType.getPosition();
                cutList.add(path);
                AllStickerData sticker = listAllSticker.get(position);
                sticker.setPath(path);
                cutSuccessNum++;
                if (cutSuccessNum == cutVideoPathList.size()) {
                    if (isMatting) {
                        LogUtil.d("OOM2", "裁剪完成，准备抠图");
//                        progressNowAnim.closePragressDialog();
                        //全部裁剪完成之后需要去把视频裁剪成全部帧
                        videoGetFrameModel getFrameModel = new videoGetFrameModel(context, cutList, (isSuccess1, progress) -> {
                            LogUtil.d("OOM2", "全部抠图完成");
                            if (isSuccess1) {
                                backgroundDraw.toSaveVideo(listAllSticker, true);
                            } else {
                                //todo  临时手段
                                if (progress <= 5) {
                                    progress = 5;
                                }
                                dialogProgress = progress;
                                handler.sendEmptyMessage(1);
                            }

                        });
                        getFrameModel.startExecute();
                    } else {
                        dialog.closePragressDialog();
                        backgroundDraw.toSaveVideo(listAllSticker, false);
                    }
                } else {
                    if (videoInfo != null) {
                        cutVideo(cutVideoPathList.get(cutSuccessNum), videoInfo.getDuration(), cutVideoPathList.get(cutSuccessNum).getDuration());
                    } else {
                        cutVideo(cutVideoPathList.get(cutSuccessNum), defaultVideoDuration, cutVideoPathList.get(cutSuccessNum).getDuration());
                    }
                }
            }
        });
    }


    public void requestStickersList(boolean isShowDialog) {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", selectPage + "");
        params.put("pageSize", perPageCount + "");
        // 启动时间
        Observable ob = Api.getDefault().getStickerslist(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<ArrayList<StickerList>>(context) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(ArrayList<StickerList> list) {


                finishData();
                if (isRefresh) {
                    listForSticker.clear();
                    StickerList item1 = new StickerList();
                    item1.setClearSticker(true);
                    listForSticker.add(item1);
                }


                if (!isRefresh && list.size() < perPageCount) {  //因为可能默认只请求8条数据
                    ToastUtil.showToast(context.getResources().getString(R.string.no_more_data));
                }
                if (list.size() < perPageCount) {
                    smartRefreshLayout.setEnableLoadMore(false);
                }


                listForSticker.addAll(list);
                modificationAllData(list);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, isShowDialog);
    }


    private void modificationAllData(ArrayList<StickerList> list) {
        for (int i = 0; i < list.size(); i++) {
            String fileName = mGifFolder + File.separator + list.get(i).getId() + ".gif";
            File file = new File(fileName);
            if (file.exists()) {
                StickerList item1 = list.get(i);
                item1.setIsDownload(1);
                list.set(i, item1);
            }
        }
        gridAdapter.notifyDataSetChanged();
    }


    private void modificationSingleItem(int position) {
        StickerList item1 = listForSticker.get(position);
        item1.setIsDownload(1);
        listForSticker.set(position, item1);//修改对应的元素
        gridAdapter.notifyDataSetChanged();
    }


    private void modificationSingleItemIsChecked(int position) {
        for (StickerList item : listForSticker
        ) {
            item.setChecked(false);
        }
        StickerList item1 = listForSticker.get(position);
        item1.setChecked(true);
        listForSticker.set(position, item1);//修改对应的元素
        gridAdapter.notifyDataSetChanged();
    }


    /**
     * description ：增加一个新的
     * creation date: 2020/3/19
     * user : zhangtongju
     */
    public void addNewSticker(String path, String originalPath) {
        Observable.just(path).observeOn(AndroidSchedulers.mainThread()).subscribe(path1 -> addSticker(path1, false, true, true, originalPath, false, null));
    }


    /**
     * description ：视频音视频分离，获得视频的声音
     * creation date: 2020/4/23
     * user : zhangtongju
     */
    private void getVideoVoice(String videoPath, String outputPath) {
        WaitingDialog.openPragressDialog(context);
        new Thread(() -> {
            mediaManager manager = new mediaManager(context);
            manager.splitMp4(videoPath, new File(outputPath), (isSuccess, putPath) -> {
                WaitingDialog.closePragressDialog();
                if (isSuccess) {
                    LogUtil.d("OOM2", "分离出来的因为地址为" + outputPath);
                    videoVoicePath = outputPath + File.separator + "bgm.mp3";
                    callback.getBgmPath(videoVoicePath);
                } else {
                    LogUtil.d("OOM2", "分离出来的因为地址为null" + outputPath);
                    callback.getBgmPath("");
                }
            });
        }).start();
    }


    class videoType {

        public videoType(String path, int position, long duration) {
            this.path = path;
            this.duration = duration;
            this.position = position;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        String path;

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        int position;

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        long duration;
    }


    private int dialogProgress;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (dialogProgress <= 25) {
                dialog.setProgress("飞闪预览处理中" + dialogProgress + "%\n" + "请耐心等待 不要离开");
            } else if (dialogProgress <= 40) {
                dialog.setProgress("飞闪音频添加中" + dialogProgress + "%\n" + "快了，友友稍等片刻");
            } else if (dialogProgress <= 60) {
                dialog.setProgress("飞闪视频处理中" + dialogProgress + "%\n" + "抠像太强大，即将生成");
            } else if (dialogProgress <= 80) {
                dialog.setProgress("飞闪视频合成中" + dialogProgress + "%\n" + "马上就好，不要离开");
            } else {
                dialog.setProgress("视频即将呈现啦" + dialogProgress + "%\n" + "最后合成中，请稍后");
            }
        }
    };


}
