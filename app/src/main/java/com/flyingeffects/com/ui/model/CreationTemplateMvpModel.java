package com.flyingeffects.com.ui.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.TemplateGridViewAdapter;
import com.flyingeffects.com.adapter.TemplateViewPager;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.commonlyModel.SaveAlbumPathModel;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.AllStickerData;
import com.flyingeffects.com.enity.StickerList;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.AlbumManager;
import com.flyingeffects.com.manager.CompressionCuttingManage;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.ui.interfaces.model.CreationTemplateMvpCallback;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.screenUtil;
import com.flyingeffects.com.view.StickerView;
import com.flyingeffects.com.view.lansongCommendView.StickerItemOnitemclick;
import com.lansosdk.box.ViewLayerRelativeLayout;
import com.shixing.sxve.ui.adapter.TimelineAdapter;
import com.shixing.sxve.ui.view.WaitingDialog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
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
    private final String TAG = "OOM";
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

    public CreationTemplateMvpModel(Context context, CreationTemplateMvpCallback callback, String mVideoPath, ViewLayerRelativeLayout viewLayerRelativeLayout) {
        this.context = context;
        this.callback = callback;
        this.mVideoPath = mVideoPath;
        this.viewLayerRelativeLayout = viewLayerRelativeLayout;
        videoInfo = getVideoInfo.getInstance().getRingDuring(mVideoPath);
        FileManager fileManager = new FileManager();
        mGifFolder = fileManager.getFileCachePath(context, "gifFolder");
    }


    public void CheckedChanged(boolean isChecked) {
        MattingChange(isChecked);
    }

    public void initStickerView(String imagePath, String originalPath) {
        firstAddImage(imagePath, originalPath);
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


    public void scrollToPosition(int position) {

        linearLayoutManager.scrollToPositionWithOffset(position, 0);

    }

    /**
     * description ：增加第一个用户抠图的stickView
     * creation date: 2020/3/11
     * user : zhangtongju
     */
    private void firstAddImage(String path, String originalPath) {
        StickerView stickView = new StickerView(context);
        stickView.setOnitemClickListener(new StickerItemOnitemclick() {
            @Override
            public void stickerOnclick(int type) {
                if (type == StickerView.LEFT_TOP_MODE) {//刪除
                    viewLayerRelativeLayout.removeView(stickView);
                } else if (type == StickerView.RIGHT_TOP_MODE) {//copy
                    viewLayerRelativeLayout.addView(stickView);
                } else if (type == StickerView.LEFT_BOTTOM_MODE) {
                    //切換素材
                    AlbumManager.chooseImageAlbum(context, 1, 0, (tag, paths, isCancel, albumFileList) -> {
                        CompressionCuttingManage manage = new CompressionCuttingManage(context, tailorPaths -> {
                            Observable.just(tailorPaths.get(0)).subscribeOn(AndroidSchedulers.mainThread()).subscribe(s -> {
                                stickView.setImageRes(s, false);
                                stickView.update();
                            });

                        });
                        manage.CompressImgAndCache(paths);


                    }, "");
                }
            }

            @Override
            public void stickerMove() {
                if (stickView.getParent() != null) {
                    ViewGroup vp = (ViewGroup) stickView.getParent();
                    if (vp != null) {
                        vp.removeView(stickView);
                    }
                }
                viewLayerRelativeLayout.addView(stickView);
            }
        });
        stickView.setLeftBottomBitmap(context.getDrawable(R.mipmap.sticker_change));
        stickView.setRightTopBitmap(context.getDrawable(R.mipmap.sticker_copy));
        stickView.setLeftTopBitmap(context.getDrawable(R.drawable.sticker_delete));
        stickView.setRightBottomBitmap(context.getDrawable(R.mipmap.sticker_redact));
        stickView.setComeFrom(true);
        stickView.setOriginalPath(originalPath);
        stickView.setClipPath(path);
        stickView.setImageRes(path, false);
        AnimStickerModel animStickerModel = new AnimStickerModel(context, viewLayerRelativeLayout, stickView);
        listForStickerView.add(animStickerModel);
        callback.ItemClickForStickView(animStickerModel);
        if (stickView.getParent() != null) {
            ViewGroup vp = (ViewGroup) stickView.getParent();
            if (vp != null) {
                vp.removeAllViews();
            }
        }
        viewLayerRelativeLayout.addView(stickView);
    }

    private TemplateGridViewAdapter gridAdapter;
    private List<StickerList> listForSticker = new ArrayList<>();

    public void initBottomLayout(ViewPager viewPager) {
        View templateThumbView = LayoutInflater.from(context).inflate(R.layout.view_template_paster, viewPager, false);
        GridView gridView = templateThumbView.findViewById(R.id.gridView);
        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            if (i == 0) {
                //删除选择的帖子
                deleteAllSticker();
            } else {
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


    private void deleteAllSticker() {
        if (listForStickerView != null && listForStickerView.size() > 0) {
            for (AnimStickerModel stickerModel : listForStickerView
            ) {
                StickerView stickerView = stickerModel.getStickerView();
                if (stickerView != null && !stickerView.getComeFrom()) {
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
        if (listForStickerView != null && listForStickerView.size() > 0) {
            for (AnimStickerModel stickerModel : listForStickerView
            ) {
                StickerView stickerView = stickerModel.getStickerView();
                if (stickerView != null && stickerView.getComeFrom()) {
                    if (isMatting) {
                        stickerView.setImageRes(stickerView.getClipPath(), false);
                    } else {
                        stickerView.setImageRes(stickerView.getOriginalPath(), false);
                    }
                }
            }
        }
    }


    /**
     * 下载帖子功能
     * @param path 下载地址
     * @param imageId  gif 保存的图片id
     * @param position  当前点击的那个item ，主要用来更新数据
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
                if(nowStickerHasChoosse(imageId,path)){
                    String copyName = mGifFolder + File.separator + System.currentTimeMillis() + format;
                    copyGif(fileName,copyName);
                    WaitingDialog.closePragressDialog();
                    return;
                }else{
                    addSticker(fileName, false, false, null);
                    WaitingDialog.closePragressDialog();
                    return;
                }

            }
            Observable.just(path).map(new Func1<String, File>() {
                @Override
                public File call(String s) {
                    File file = null;
                    try {
                        file = Glide.with(context)
                                .load(finalPath)
                                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                .get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return file;
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<File>() {
                @Override
                public void call(File path) {
                    try {
                        FileUtil.copyFile(path, fileName);
                        addSticker(fileName, false, false, null);
                        WaitingDialog.closePragressDialog();
                        modificationSingleItem(position);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        } else {
            String finalPath = path;
            Observable.just(path).map(new Func1<String, Bitmap>() {
                @Override
                public Bitmap call(String s) {
                    Bitmap originalBitmap = null;
                    FutureTarget<Bitmap> futureTarget =
                            Glide.with(BaseApplication.getInstance())
                                    .asBitmap()
                                    .load(finalPath)
                                    .submit(720, 1280);
                    try {
                        originalBitmap = futureTarget.get();
                    } catch (Exception e) {
                        LogUtil.d("oom", e.getMessage());
                    }
                    Glide.with(BaseApplication.getInstance()).clear(futureTarget);
                    return originalBitmap;
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Bitmap>() {
                @Override
                public void call(Bitmap bitmap) {
                    String aa = finalPath.substring(finalPath.length() - 4);
                    String copyName = mGifFolder + File.separator + System.currentTimeMillis() + aa;
                    saveBitmapToPath(bitmap, copyName, new FileManager.saveBitmapState() {
                        @Override
                        public void succeed(boolean isSucceed) {
                            modificationSingleItem(position);
                            addSticker(copyName, true, false, null);
                        }
                    });

                }
            });
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
                if(path.equals(copyName)){
                    return  true;
                }

            }
        }

        return  false;

    }




    private void addSticker(String path, boolean hasReplace, boolean isFromAubum, String originalPath) {
        StickerView stickView = new StickerView(context);
        stickView.setOnitemClickListener(new StickerItemOnitemclick() {
            @Override
            public void stickerOnclick(int type) {
                if (type == StickerView.LEFT_TOP_MODE) {//刪除
                    viewLayerRelativeLayout.removeView(stickView);
                } else if (type == StickerView.RIGHT_TOP_MODE) {
                    //copy
                    copyGif(stickView.getResPath(),path);

                } else if (type == StickerView.LEFT_BOTTOM_MODE) {
                    //切換素材
                    AlbumManager.chooseImageAlbum(context, 1, 0, (tag, paths, isCancel, albumFileList) -> {
                        CompressionCuttingManage manage = new CompressionCuttingManage(context, tailorPaths -> {
                            Observable.just(tailorPaths.get(0)).subscribeOn(AndroidSchedulers.mainThread()).subscribe(s -> stickView.setImageRes(s, false));
                            stickView.update();


                        });
                        manage.CompressImgAndCache(paths);

                    }, "");
                }
            }

            @Override
            public void stickerMove() {
                if (stickView.getParent() != null) {
                    ViewGroup vp = (ViewGroup) stickView.getParent();
                    if (vp != null) {
                        vp.removeView(stickView);
                    }
                }
                viewLayerRelativeLayout.addView(stickView);
            }
        });
        stickView.setRightTopBitmap(context.getDrawable(R.mipmap.sticker_copy));
        stickView.setLeftTopBitmap(context.getDrawable(R.drawable.sticker_delete));
        stickView.setRightBottomBitmap(context.getDrawable(R.mipmap.sticker_redact));
        stickView.setComeFrom(isFromAubum);
        if (isFromAubum) {
            stickView.setClipPath(path);
            stickView.setOriginalPath(originalPath);
        }
        if (hasReplace) {
            stickView.setLeftBottomBitmap(context.getDrawable(R.mipmap.sticker_change));
        }
        stickView.setImageRes(path, false);
        AnimStickerModel animStickerModel = new AnimStickerModel(context, viewLayerRelativeLayout, stickView);
        listForStickerView.add(animStickerModel);
        if (stickView.getParent() != null) {
            ViewGroup vp = (ViewGroup) stickView.getParent();
            if (vp != null) {
                vp.removeAllViews();
            }
        }
        viewLayerRelativeLayout.addView(stickView);
    }


    private void copyGif(String originalPath,String path){
        try {
            String copyName = null;
            if (originalPath.endsWith(".gif")) {
                copyName = mGifFolder + File.separator + System.currentTimeMillis() + "synthetic.gif";
                FileUtil.copyFile(new File(originalPath), copyName);
                addSticker(copyName, false, false, null);
            } else {
                String aa = path.substring(path.length() - 4);
                copyName = mGifFolder + File.separator + System.currentTimeMillis() + aa;
                addSticker(copyName, true, false, null);
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

    private int mTotalWidth;

    private void initSingleThumbSize(int width, int height, float duration, float mTemplateDuration, String mVideoPath) {
        // 需要截取的listWidth宽度
        int listWidth = list_thumb.getWidth() - list_thumb.getPaddingLeft() - list_thumb.getPaddingRight();
        int listHeight = list_thumb.getHeight();
        float scale = (float) listHeight / height;
        int thumbWidth = (int) (scale * width);
        mTimelineAdapter.setBitmapSize(thumbWidth, listHeight);
        //其中listWidth表示当前截取的大小
        int thumbCount = (int) (listWidth * (duration / mTemplateDuration) / thumbWidth);
        thumbCount = thumbCount > 0 ? thumbCount : 0;
        //每帧所占的时间
        final int interval = (int) (duration / thumbCount);
        int[] mTimeUs = new int[thumbCount];
        for (int i = 0; i < thumbCount; i++) {
            mTimeUs[i] = i * interval * 1000;
        }
        mTimelineAdapter.setVideoUri(Uri.fromFile(new File(mVideoPath)));
        mTimelineAdapter.setData(mTimeUs);
        mTotalWidth = thumbWidth * thumbCount;
        callback.getVideoDuration(videoInfo.getDuration(), thumbCount);
    }


    /**
     * description ：保存视频采用蓝松sdk提供的在保存功能
     * creation date: 2020/3/12
     * user : zhangtongju
     */
    public void toSaveVideo() {
        backgroundDraw backgroundDraw = new backgroundDraw(context, mVideoPath, path -> {
            String albumPath = SaveAlbumPathModel.getInstance().getKeepOutput();
            try {
                FileUtil.copyFile(new File(path), albumPath);
                albumBroadcast(albumPath);
                showKeepSuccessDialog(albumPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        ArrayList<AllStickerData> list = new ArrayList<>();
        for (int i = 0; i < viewLayerRelativeLayout.getChildCount(); i++) {
            StickerView stickerView = (StickerView) viewLayerRelativeLayout.getChildAt(i);
            AllStickerData stickerData = new AllStickerData();
            stickerData.setRotation(stickerView.getRotateAngle());
            stickerData.setScale(stickerView.getScale());
            stickerData.setTranslationX(stickerView.getTranslationX());
            stickerData.setTranslationy(stickerView.getTranslationY());
            stickerData.setPath(stickerView.getResPath());
            list.add(stickerData);
        }
        backgroundDraw.toSaveVideo(list);
    }


    /**
     * description ：通知相册更新
     * date: ：2019/8/16 14:24
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    private void albumBroadcast(String outputFile) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(outputFile)));
        context.sendBroadcast(intent);
    }


    private void showKeepSuccessDialog(String path) {
        if (!isDestroy && !DoubleClick.getInstance().isFastDoubleClick()) {
            AlertDialog.Builder builder = new AlertDialog.Builder( //去除黑边
                    new ContextThemeWrapper(context, R.style.Theme_Transparent));
            builder.setTitle(R.string.notification);
            builder.setMessage(context.getString(R.string.have_saved_to_sdcard) +
                    "【" + path + context.getString(R.string.folder) + "】");
            builder.setNegativeButton(context.getString(R.string.got_it), (dialog, which) -> dialog.dismiss());
            builder.setCancelable(true);
            Dialog dialog = builder.show();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }


    public void requestStickersList() {
        HashMap<String, String> params = new HashMap<>();
        // 启动时间
        Observable ob = Api.getDefault().getStickerslist(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<ArrayList<StickerList>>(context) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(ArrayList<StickerList> list) {
                StickerList item1 = new StickerList();
                item1.setClearSticker(true);
                listForSticker.add(item1);
                listForSticker.addAll(list);
                modificationAllData(list);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }


    private void modificationAllData(ArrayList<StickerList> list) {
        for (int i = 0; i < list.size(); i++) {
            String fileName = mGifFolder + File.separator + list.get(i).getId() + ".gif";
            File file = new File(fileName);
            if (file.exists()) {
                StickerList item1 = list.get(i);
                item1.setIsDownload(1);
                list.set(i, item1);//修改对应的元素
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


    /**
     * description ：增加一个新的
     * creation date: 2020/3/19
     * param :
     * user : zhangtongju
     */
    public void addNewSticker(String path, String originalPath) {
        Observable.just(path).observeOn(AndroidSchedulers.mainThread()).subscribe(path1 -> addSticker(path1, true, true, originalPath));
    }

}
