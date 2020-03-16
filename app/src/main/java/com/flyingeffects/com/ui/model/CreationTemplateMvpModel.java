package com.flyingeffects.com.ui.model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.TemplateGridViewAdapter;
import com.flyingeffects.com.adapter.TemplateViewPager;
import com.flyingeffects.com.adapter.VideoTimelineAdapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.commonlyModel.SaveAlbumPathModel;
import com.flyingeffects.com.enity.StickerForParents;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.CopyFileFromAssets;
import com.flyingeffects.com.manager.DoubleClick;
import com.flyingeffects.com.ui.interfaces.model.CreationTemplateMvpCallback;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.view.VideoFrameRecycler;
import com.flyingeffects.com.view.lansongCommendView.StickerView;
import com.lansosdk.box.AudioLayer;
import com.lansosdk.box.DrawPad;
import com.lansosdk.box.DrawPadUpdateMode;
import com.lansosdk.box.MVLayer;
import com.lansosdk.box.VideoLayer;
import com.lansosdk.box.ViewLayerRelativeLayout;
import com.lansosdk.box.onDrawPadProgressListener;
import com.lansosdk.box.onDrawPadSizeChangedListener;
import com.lansosdk.videoeditor.AudioEditor;
import com.lansosdk.videoeditor.AudioPadExecute;
import com.lansosdk.videoeditor.DrawPadView;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.lansosdk.videoeditor.MediaInfo;
import com.shixing.sxve.ui.adapter.TimelineAdapter;
import com.shixing.sxve.ui.util.BitmapCompress;
import com.shixing.sxve.ui.view.VEBitmapFactory;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.subjects.PublishSubject;


/**
 * description ：使用蓝松的drawPadView来绘制页面，实现方式为一个主视频图层加上多个动态的mv图层+ 多个图片图层，最后渲染出来视频
 * creation date: 2020/3/12
 * param :
 * user : zhangtongju
 */
public class CreationTemplateMvpModel {
    private final String TAG = "CreationTemplateMvpModel";
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
    private String editTmpPath = null;
    /**
     * 主视频图层
     */
    private VideoLayer mLayerMain;
    private ArrayList<MVLayer> mvLayerArrayList = new ArrayList<>();
    private String path = "";
//    private MediaInfo mInfo;
    private ArrayList<AnimStickerModel> listForStickerView = new ArrayList<>();
    private boolean isDestroy=false;
    private  RecyclerView list_thumb;
    private  MediaInfo    mInfo;
    private StickerView stickView;

    public CreationTemplateMvpModel(Context context, CreationTemplateMvpCallback callback, String mVideoPath, ViewLayerRelativeLayout viewLayerRelativeLayout, StickerView stickView) {
        this.context = context;
        this.callback = callback;
        this.stickView=stickView;
        this.mVideoPath = mVideoPath;
        this.viewLayerRelativeLayout = viewLayerRelativeLayout;
        editTmpPath = LanSongFileUtil.newMp4PathInBox();
        mLayerMain = null;
        mInfo = new MediaInfo(mVideoPath);
    }

    public void initBottomLayout(ViewPager viewPager) {
        View templateThumbView = LayoutInflater.from(context).inflate(R.layout.view_template_paster, viewPager, false);
        GridView gridView = templateThumbView.findViewById(R.id.gridView);
        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            BitmapCompress bitmapManager=new BitmapCompress();
            Bitmap bp=bitmapManager.getSmallBmpFromFile(gifTest,720,1280);
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




    public void onDestroy(){
        isDestroy=true;
    }

    private TimelineAdapter   mTimelineAdapter;
    public void initVideoProgressView(RecyclerView list_thumb) {
        this.list_thumb=list_thumb;
//        mScrollX = 0;
        list_thumb.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        mTimelineAdapter    = new TimelineAdapter();
        list_thumb.setAdapter(mTimelineAdapter);
        list_thumb.setHasFixedSize(true);
        list_thumb.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    float percent = (float) mScrollX / mTotalWidth;
//                    mStartDuration = (int) (mVideoDuration * percent);
//                    mEndDuration = mStartDuration + mTemplateDuration;
//                    seekTo(mStartDuration);
//                    startTimer();
//                }
            }

            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
//                mScrollX += dx;
            }
        });

    //    initSingleThumbSize( mInfo.getDurationUs(), path);
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
        int thumbCount = (int) (listWidth * (duration / mTemplateDuration / 1000) / thumbWidth);
        thumbCount = thumbCount > 0 ? thumbCount : 0;
        //每帧所占的时间
        final int interval = (int) (duration / thumbCount * 1000);
        int[] mTimeUs = new int[thumbCount];
        for (int i = 0; i < thumbCount; i++) {
            mTimeUs[i] = i * interval;
        }
        HashMap<Integer, Bitmap> mData = new HashMap<>();
        mTimelineAdapter.setVideoUri(Uri.fromFile(new File(mVideoPath)));
        mTimelineAdapter.setData(mTimeUs, mData);
        mTotalWidth = thumbWidth * thumbCount;
    }




    /**
     * description ：预览视频采用蓝松sdk提供的在预览功能
     * creation date: 2020/3/12
     * user : zhangtongju
     */
    public void toPrivateVideo(DrawPadView drawPadView) {
        this.mDrawPadView = drawPadView;
        StickerForParents stickerForParents= listForStickerView.get(0).getParameterData();
        startPlayVideo(stickerForParents);
    }

    /**
     * description ：保存视频采用蓝松sdk提供的在保存功能
     * creation date: 2020/3/12
     * user : zhangtongju
     */
    public void toSaveVideo(){
        StickerForParents stickerForParents= listForStickerView.get(0).getParameterData();
        backgroundDraw backgroundDraw=new backgroundDraw(context, mVideoPath, path -> {
            String albumPath = SaveAlbumPathModel.getInstance().getKeepOutput();
            try {
                FileUtil.copyFile(new File(path), albumPath);
                albumBroadcast(albumPath);
                showKeepSuccessDialog(albumPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        backgroundDraw.toSaveVideo(mInfo.getDurationUs(),stickerForParents);

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
    private void startPlayVideo( StickerForParents stickerForParents) {
        if (mVideoPath != null) {
            mplayer = new MediaPlayer();
            try {
                mplayer.setDataSource(mVideoPath);

            } catch (IOException e) {
                e.printStackTrace();
            }
            mplayer.setOnPreparedListener(mp -> initDrawPad( stickerForParents));
            mplayer.setOnCompletionListener(mp -> stopDrawPad());
            mplayer.prepareAsync();
        } else {
            LogUtil.d(TAG, "Null Data Source\n");
        }
    }


    /**
     * Step1: 开始运行 drawPad 容器
     */
    private void initDrawPad(StickerForParents stickerForParents) {
        mDrawPadView.setUpdateMode(DrawPadUpdateMode.AUTO_FLUSH, 30);
        // 设置当前DrawPad的宽度和高度,并把宽度自动缩放到父view的宽度,然后等比例调整高度.
        mDrawPadView.setDrawPadSize(DRAWPAD_WIDTH, DRAWPAD_HEIGHT, (viewWidth, viewHeight) -> {
            // 开始DrawPad的渲染线程.
            startDrawPad(stickerForParents);
        });
    }


    /**
     * Step2: 开始运行 Drawpad线程.
     */
    private void startDrawPad(StickerForParents stickerForParents) {
        if (mDrawPadView.startDrawPad()) {
            // 增加一个主视频的 VideoLayer
            mLayerMain = mDrawPadView.addMainVideoLayer(
                    mplayer.getVideoWidth(), mplayer.getVideoHeight(), null);
            if (mLayerMain != null) {
                mplayer.setSurface(new Surface(mLayerMain.getVideoTexture()));
            }
            mplayer.start();
            addMVLayer(stickerForParents);
        }
    }


    /**
     * 增加一个MV图层.
     */
    private void addMVLayer(StickerForParents stickerForParents) {
        String colorMVPath = CopyFileFromAssets.copyAssets(context, "laohu.mp4");
        String maskMVPath = CopyFileFromAssets.copyAssets(context, "mask.mp4");
        MVLayer mvLayer = mDrawPadView.addMVLayer(colorMVPath, maskMVPath); // <-----增加MVLayer
        mvLayer.setRotate(stickerForParents.getRoation());
        mvLayer.setScale(stickerForParents.getScale());
        mvLayerArrayList.add(mvLayer);
    }


    /**
     * Step3: 停止容器,停止后,为新的视频文件增加上音频部分.
     */
    private void stopDrawPad() {
        if (mDrawPadView != null && mDrawPadView.isRunning()) {
            mDrawPadView.stopDrawPad();
//            toastStop();
            if (LanSongFileUtil.fileExist(editTmpPath)) {
                String dstPath = AudioEditor.mergeAudioNoCheck(mVideoPath, editTmpPath, true);
//                findViewById(R.id.id_mvlayer_saveplay).setVisibility(View.VISIBLE);
                LogUtil.d(TAG, "dstPath=" + dstPath);
            } else {
                LogUtil.e(TAG, " player completion, but file:" + editTmpPath
                        + " is not exist!!!");
            }

            callback.hasPlayingComplete();
        }
    }


}
