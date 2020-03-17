package com.flyingeffects.com.ui.model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.widget.GridView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.TemplateGridViewAdapter;
import com.flyingeffects.com.adapter.TemplateViewPager;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.commonlyModel.SaveAlbumPathModel;
import com.flyingeffects.com.commonlyModel.getVideoInfo;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.manager.CopyFileFromAssets;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.ui.interfaces.model.CreationTemplateMvpCallback;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.lansongCommendView.StickerItem;
import com.flyingeffects.com.view.lansongCommendView.StickerItemOnitemclick;
import com.flyingeffects.com.view.lansongCommendView.StickerView;
import com.lansosdk.box.DrawPadUpdateMode;
import com.lansosdk.box.Layer;
import com.lansosdk.box.MVLayer;
import com.lansosdk.box.VideoLayer;
import com.lansosdk.box.ViewLayerRelativeLayout;
import com.lansosdk.videoeditor.AudioEditor;
import com.lansosdk.videoeditor.DrawPadView;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.lansosdk.videoeditor.MediaInfo;
import com.shixing.sxve.ui.adapter.TimelineAdapter;
import com.shixing.sxve.ui.util.BitmapCompress;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;


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
    private MediaPlayer mplayer;
    private DrawPadView mDrawPadView;
    private ViewLayerRelativeLayout viewLayerRelativeLayout;
    private static final int DRAWPAD_WIDTH = 720;
    private static final int DRAWPAD_HEIGHT = 1280;
    private String gifTest = "/storage/emulated/0/DCIM/Camera/IMG_20200130_142048.jpg";
    /**
     * 保存文件夹地址
     */
    private String editTmpPath ;
    /**
     * 主视频图层
     */
    private VideoLayer mLayerMain;
    private ArrayList<MVLayer> mvLayerArrayList = new ArrayList<>();
    private boolean isDestroy = false;
    private RecyclerView list_thumb;
    private StickerView stickView;
    private VideoInfo videoInfo;

    public CreationTemplateMvpModel(Context context, CreationTemplateMvpCallback callback, String mVideoPath, ViewLayerRelativeLayout viewLayerRelativeLayout) {
        this.context = context;
        this.callback = callback;
        this.mVideoPath = mVideoPath;
        this.viewLayerRelativeLayout = viewLayerRelativeLayout;
        editTmpPath = LanSongFileUtil.newMp4PathInBox();
        mLayerMain = null;
        videoInfo = getVideoInfo.getInstance().getRingDuring(mVideoPath);
//        mInfo = new MediaInfo(mVideoPath);
    }


    public void initStickerView(String imagePath) {
        stickView = new StickerView(context, new StickerItemOnitemclick() {

            @Override
            public void stickerOnclick(int type, StickerItem item) {
                if(type==0){
                    //复制
                    BitmapCompress bitmapManager = new BitmapCompress();
                    Bitmap bp = bitmapManager.getSmallBmpFromFile(gifTest, 720, 1280);
                    stickView.addBitImage(bp);


                }else{
                    //替换
//                    item.init();
                }


            }
        });
        firstAddImage(imagePath);
        viewLayerRelativeLayout.addView(stickView);
    }

    /**
     * description ：增加第一个用户抠图的stickView
     * creation date: 2020/3/11
     * user : zhangtongju
     */
    private void firstAddImage(String path) {
        Observable.just(path).map(BitmapFactory::decodeFile).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Bitmap>() {
            @Override
            public void call(Bitmap bitmap) {
                stickView.addBitImage(bitmap);
            }
        });
    }


    public void initBottomLayout(ViewPager viewPager) {
        View templateThumbView = LayoutInflater.from(context).inflate(R.layout.view_template_paster, viewPager, false);
        GridView gridView = templateThumbView.findViewById(R.id.gridView);
        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            BitmapCompress bitmapManager = new BitmapCompress();
            Bitmap bp = bitmapManager.getSmallBmpFromFile(gifTest, 720, 1280);
            stickView.addBitImage(bp);
        });
        List<String> test = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            test.add("啥");
        }
        TemplateGridViewAdapter gridAdapter = new TemplateGridViewAdapter(test, context);
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


    public void onDestroy() {
        isDestroy = true;
    }

    private TimelineAdapter mTimelineAdapter;
    private int mScrollX;

    public void initVideoProgressView(RecyclerView list_thumb) {
        this.list_thumb = list_thumb;
        list_thumb.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        mTimelineAdapter = new TimelineAdapter();
        list_thumb.setAdapter(mTimelineAdapter);
        list_thumb.setHasFixedSize(true);
        list_thumb.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    float percent = (float) mScrollX / mTotalWidth;
                    LogUtil.d("oom","percent="+percent);
                    int progress = (int) (videoInfo.getDuration() * percent);
                    callback.setgsyVideoProgress(progress);
                }
            }

            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                LogUtil.d("oom","dx="+dx);
                mScrollX += dx;
            }
        });
        initSingleThumbSize(videoInfo.getVideoWidth(), videoInfo.getVideoHeight(), videoInfo.getDuration(), 2000, mVideoPath);
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
        final int interval = (int) (duration / thumbCount );
        int[] mTimeUs = new int[thumbCount];
        for (int i = 0; i < thumbCount; i++) {
            mTimeUs[i] = i * interval;
        }
        mTimelineAdapter.setVideoUri(Uri.fromFile(new File(mVideoPath)));
        mTimelineAdapter.setData(mTimeUs);
        mTotalWidth = thumbWidth * thumbCount;
    }


    /**
     * description ：预览视频采用蓝松sdk提供的在预览功能
     * creation date: 2020/3/12
     * user : zhangtongju
     */
    public void toPrivateVideo(DrawPadView drawPadView) {
        this.mDrawPadView = drawPadView;
//        StickerForParents stickerForParents= listForStickerView.get(0).getParameterData();
        startPlayVideo(stickView);
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
        backgroundDraw.toSaveVideo(stickView);
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
            builder.setNegativeButton(context.getString(R.string.got_it), (dialog, which) -> {
                dialog.dismiss();
            });
            builder.setCancelable(true);
            Dialog dialog = builder.show();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    /**
     * VideoLayer是外部提供画面来源,
     * 您可以用你们自己的播放器作为画面输入源,也可以用原生的MediaPlayer,只需要视频播放器可以设置surface即可.
     * 一下举例是采用MediaPlayer作为视频输入源.
     */
    private void startPlayVideo(StickerView stickView) {
        if (mVideoPath != null) {
            mplayer = new MediaPlayer();
            try {
                mplayer.setDataSource(mVideoPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mplayer.setOnPreparedListener(mp -> initDrawPad(stickView));
            mplayer.setOnCompletionListener(mp -> stopDrawPad());
            mplayer.prepareAsync();
        } else {
            LogUtil.d(TAG, "Null Data Source\n");
        }
    }


    /**
     * Step1: 开始运行 drawPad 容器
     */
    private void initDrawPad(StickerView stickView) {
        mDrawPadView.setUpdateMode(DrawPadUpdateMode.AUTO_FLUSH, 30);
        // 设置当前DrawPad的宽度和高度,并把宽度自动缩放到父view的宽度,然后等比例调整高度.
        mDrawPadView.setDrawPadSize(DRAWPAD_WIDTH, DRAWPAD_HEIGHT, (viewWidth, viewHeight) -> {
            // 开始DrawPad的渲染线程.
            startDrawPad(stickView);
        });
    }


    /**
     * Step2: 开始运行 Drawpad线程.
     */
    private void startDrawPad(StickerView stickView) {
        if (mDrawPadView.startDrawPad()) {
            // 增加一个主视频的 VideoLayer
            mLayerMain = mDrawPadView.addMainVideoLayer(
                    mplayer.getVideoWidth(), mplayer.getVideoHeight(), null);
            if (mLayerMain != null) {
                mplayer.setSurface(new Surface(mLayerMain.getVideoTexture()));
            }
            mplayer.start();
            LinkedHashMap<Integer, StickerItem> linkedHashMap = stickView.getBank();
            for (int i = 1; i <= linkedHashMap.size(); i++) {
                //多个图层的情况
                StickerItem stickerItem = linkedHashMap.get(i);
                addMVLayer(stickerItem);
            }
        }
    }


    /**
     * 增加一个MV图层.
     */
    private void addMVLayer(StickerItem stickerItem) {
        String colorMVPath = CopyFileFromAssets.copyAssets(context, "mei.mp4");
        String maskMVPath = CopyFileFromAssets.copyAssets(context, "mei_b.mp4");
        MVLayer mvLayer = mDrawPadView.addMVLayer(colorMVPath, maskMVPath); // <-----增加MVLayer
        int rotate = (int) stickerItem.getRoatetAngle();
        if (rotate < 0) {
            rotate = 360 + rotate;
        }
        LogUtil.d("OOM", "scale=" + stickerItem.getScaleSize());
        LogUtil.d("OOM", "rotate=" + rotate);
        mvLayer.setRotate(rotate);
        mvLayer.setScale(stickerItem.getScaleSize() / 2);
        LogUtil.d("OOM", "setPositionX=" + stickerItem.getTranslation().getX());
        mvLayer.setPosition(stickerItem.getTranslation().getX(), mvLayer.getPositionY());
        LogUtil.d("OOM", "setPositionY=" + stickerItem.getTranslation().getY());
        mvLayer.setPosition(mvLayer.getPositionX(), stickerItem.getTranslation().getY());
        mvLayerArrayList.add(mvLayer);
    }


    /**
     * Step3: 停止容器,停止后,为新的视频文件增加上音频部分.
     */
    private void stopDrawPad() {
        if (mDrawPadView != null && mDrawPadView.isRunning()) {
            releaseLayer();
            mDrawPadView.removeAllLayer();
            mDrawPadView.stopDrawPad();
            if (LanSongFileUtil.fileExist(editTmpPath)) {
                String dstPath = AudioEditor.mergeAudioNoCheck(mVideoPath, editTmpPath, true);
                LogUtil.d(TAG, "dstPath=" + dstPath);
            } else {
                LogUtil.e(TAG, " player completion, but file:" + editTmpPath
                        + " is not exist!!!");
            }
            callback.hasPlayingComplete();
        }
    }


    private void releaseLayer() {
        if (mvLayerArrayList != null && mvLayerArrayList.size() > 0) {
            for (Layer layer : mvLayerArrayList
            ) {
                if (layer != null) {
                    mDrawPadView.removeLayer(layer);
                    layer = null;
                }
            }
        }
    }
}
