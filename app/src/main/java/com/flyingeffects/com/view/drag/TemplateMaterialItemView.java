package com.flyingeffects.com.view.drag;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.commonlyModel.GetPathType;
import com.flyingeffects.com.utils.BitmapUtil;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.screenUtil;
import com.flyingeffects.com.view.beans.VideoTrimmerFrameBean;
import com.lansosdk.videoeditor.MediaInfo;
import com.shixing.sxve.ui.albumType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author ZhouGang
 * @date 2020/11/2
 * 模板素材item 拖动的view
 */
public class TemplateMaterialItemView extends LinearLayout implements View.OnTouchListener{

    /**
     * 箭头的宽度
     */
    public static final int ARROW_WIDTH = 50;

    ImageView mLeftView;
    LinearLayout mLlThumbnail;
    ImageView mRightView;
    /**
     * 按下时左侧view的X值
     */
    float leftDownX = 0;
    /**
     * 按下时右侧view的X值
     */
    float rightDownX = 0;
    /**
     * 缩略图按下时的X值
     */
    float llViewDownX = 0;

    private boolean isLongClickModule = false;
    private boolean isNeedOverallDrag = false;
    private Runnable mLongPressRunnable =  new Runnable() {
        @Override
        public void run() {
            isLongClickModule = true;
            vibrator(50);
        }
    };;
    private Handler handler = new Handler();
    long lastTime = 0;
    Vibrator vibrator;
    int identityID = 0;
    private long startTime;
    private long endTime;
    private long mDuration;
    private TextView mTvStickerView;

    public TouchDragListener dragListener;

    public TemplateMaterialItemView(Context context) {
        super(context);
        initView();
    }

    public TemplateMaterialItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TemplateMaterialItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initView() {
        setGravity(Gravity.CENTER_VERTICAL);
        setOrientation(LinearLayout.HORIZONTAL);

        mLeftView = new ImageView(getContext());
        mLeftView.setBackgroundResource(R.mipmap.icon_sliding_block_left);
        addView(mLeftView);
        LayoutParams leftLayoutParams = (LayoutParams) mLeftView.getLayoutParams();
        leftLayoutParams.height = LayoutParams.MATCH_PARENT;
        leftLayoutParams.width = ARROW_WIDTH;
        mLeftView.setLayoutParams(leftLayoutParams);

        mLlThumbnail = new LinearLayout(getContext());
        mLlThumbnail.setOrientation(LinearLayout.HORIZONTAL);
        mLlThumbnail.setGravity(Gravity.CENTER_VERTICAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mLlThumbnail.setForeground(getResources().getDrawable(R.drawable.selector_dragsubtitleview_bg,null));
        }
        addView(mLlThumbnail);

        mRightView = new ImageView(getContext());
        mRightView.setBackgroundResource(R.mipmap.icon_sliding_block_right);
        addView(mRightView);
        LayoutParams rightLayoutParams = (LayoutParams) mRightView.getLayoutParams();
        rightLayoutParams.height = LayoutParams.MATCH_PARENT;
        rightLayoutParams.width = ARROW_WIDTH;
        mRightView.setLayoutParams(rightLayoutParams);

        mLeftView.setOnTouchListener(this);
        mRightView.setOnTouchListener(this);
        mLlThumbnail.setOnTouchListener(this);
        vibrator = (Vibrator) BaseApplication.getInstance().getSystemService(Service.VIBRATOR_SERVICE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //获取到手指处的横坐标
        if (v == mLeftView) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    leftDownX = event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (leftDownX > event.getX()) {
                        if (dragListener != null) {
                            dragListener.leftTouch(true, leftDownX - event.getX(), identityID);
                        }
                    } else {
                        if (dragListener != null) {
                            dragListener.leftTouch(false, event.getX() - leftDownX, identityID);
                        }
                    }
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                    if (dragListener != null) {
                        dragListener.onTouchEnd(this, true);
                        dragListener.editStatistics(this,false);
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
        if (v == mRightView) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    rightDownX = event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (rightDownX < event.getX()) {
                        if (dragListener != null) {
                            dragListener.rightTouch(false, event.getX() - rightDownX, identityID);
                        }
                    } else {
                        if (dragListener != null) {
                            dragListener.rightTouch(true, rightDownX - event.getX(), identityID);
                        }
                    }
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                    if (dragListener != null) {
                        dragListener.onTouchEnd(this, false);
                        dragListener.editStatistics(this,false);
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
        if (v == mLlThumbnail) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    llViewDownX = event.getX();
                    lastTime = System.currentTimeMillis();
                    if (isNeedOverallDrag) {
                        handler.postDelayed(mLongPressRunnable, 500);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isLongClickModule) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        if (llViewDownX < event.getX()) {
                            //整体往右移动
                            if (dragListener != null) {
                                dragListener.touchTextView(this, false, event.getX() - llViewDownX, identityID);
                            }
                        } else {
                            //整体往左移动
                            if (dragListener != null) {
                                dragListener.touchTextView(this, true, llViewDownX - event.getX(), identityID);
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (dragListener != null) {
//                        dragListener.onTouchEnd(this, false);
                        dragListener.editStatistics(this,true);
                    }
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastTime <= 100) {
                        if (dragListener != null) {
                            dragListener.onClickTextView(this);
                        }
                    }
                    isLongClickModule = false;
                    if (isNeedOverallDrag) {
                        handler.removeCallbacks(mLongPressRunnable);
                    }
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
                default:
                    break;
            }
            return true;
        }
        return false;
    }

    private void vibrator(long mill) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(mill, 50));
        } else {
            vibrator.vibrate(mill);
        }
    }

    /**
     * 类似于Tag
     *
     * @param identityID
     */
    public void setIdentityID(int identityID) {
        this.identityID = identityID;
    }

    public int getIdentityID() {
        return identityID;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void isShowArrow(boolean isShow) {
        if (isShow) {
            mLeftView.setVisibility(VISIBLE);
            mRightView.setVisibility(VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mLlThumbnail.setSelected(true);
            }
        } else {
            mLeftView.setVisibility(INVISIBLE);
            mRightView.setVisibility(INVISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mLlThumbnail.setSelected(false);
            }
        }
    }

    /**是否需要整体拖动*/
    public void isNeedOverallDrag(boolean isNeedOverallDrag){
        this.isNeedOverallDrag = isNeedOverallDrag;
    }

    /***
     * 设置显示的textview的宽度和高度
     * @param width
     * @param height
     */
    public void setWidthAndHeight(int width, int height) {
        LayoutParams params = (LayoutParams) mLlThumbnail.getLayoutParams();
        params.height = height;
        params.width = width;
        mLlThumbnail.setLayoutParams(params);
    }

    /***
     * 重新选择了视频或者改变了预览视频的时长  得到新的宽度设置给拖动条
     * @param duration 时长
     * @param containerHeight 拖动条高度
     * @return 返回高度
     */
    public int changeVideoPathWidth(long duration,int containerHeight){
        int frameSingleWidth = (int) (containerHeight * TemplateMaterialSeekBarView.NOVIDEO_STAGE_WIDTH / TemplateMaterialSeekBarView.NOVIDEO_STAGE_HEIGHT);
        long frameSingleMs = frameSingleWidth * TemplateMaterialSeekBarView.PER_MS_IN_PX;
        int count = (int) (duration * 1.0f / frameSingleMs);
        long reviseMs = duration - (frameSingleMs * count);
        int frameReviseWidth = (int) (reviseMs / TemplateMaterialSeekBarView.PER_MS_IN_PX);
        if (frameReviseWidth > frameSingleWidth) {
            count -= frameReviseWidth / frameSingleWidth;
            frameReviseWidth = frameReviseWidth % frameSingleWidth;
        }
        int reviseCount = frameReviseWidth > 0 ? 1 : 0;
        int thumbnailTotalWidth = 0;
        FrameParams params = new FrameParams("", count + reviseCount, reviseCount, frameSingleWidth, frameReviseWidth, containerHeight, containerHeight);
        //设置初始化
        for (int i = 0; i < params.count; i++) {
            int currentWidth = i >= params.count - params.reviseCount ? params.reviseWidth : params.singleWidth;
            thumbnailTotalWidth += currentWidth;
        }
        return thumbnailTotalWidth;
    }

    public String resPath;
    public boolean isText;
    public String text;
    public long originalVideoDuration;

    /**此处重新计算绘制视频或者图片的帧图*/
    public int setResPathAndDuration(String resPath, long duration, int containerHeight, boolean isText, String text){
        this.resPath =resPath;
        this.isText = isText;
        this.text = text;
        mLlThumbnail.removeAllViews();
        //单张图片宽度
        int frameSingleWidth = (int) (containerHeight * TemplateMaterialSeekBarView.NOVIDEO_STAGE_WIDTH / TemplateMaterialSeekBarView.NOVIDEO_STAGE_HEIGHT);
        long frameSingleMs = frameSingleWidth * TemplateMaterialSeekBarView.PER_MS_IN_PX;
        int count = (int) (duration * 1.0f / frameSingleMs);
        long reviseMs = duration - (frameSingleMs * count);
        int frameReviseWidth = (int) (reviseMs / TemplateMaterialSeekBarView.PER_MS_IN_PX);
        if (frameReviseWidth > frameSingleWidth) {
            count -= frameReviseWidth / frameSingleWidth;
            frameReviseWidth = frameReviseWidth % frameSingleWidth;
        }
        int reviseCount = frameReviseWidth > 0 ? 1 : 0;
        int thumbnailTotalWidth = 0;
        FrameParams params = new FrameParams(resPath, count + reviseCount, reviseCount, frameSingleWidth, frameReviseWidth, containerHeight, containerHeight);
        //设置初始化
        for (int i = 0; i < params.count; i++) {
            int currentWidth = i >= params.count - params.reviseCount ? params.reviseWidth : params.singleWidth;
            if(!isText){
                ImageView imgCenter = new ImageView(getContext());
                imgCenter.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mLlThumbnail.addView(imgCenter);
                LinearLayout.LayoutParams parCenter = (LinearLayout.LayoutParams) imgCenter.getLayoutParams();
                parCenter.width = currentWidth;
                parCenter.height = params.singleHeight;
                imgCenter.setLayoutParams(parCenter);
                if (TextUtils.isEmpty(resPath)) {
                    imgCenter.setBackgroundColor(Color.parseColor("#43A400"));
                } else {
                    imgCenter.setImageResource(R.mipmap.icon_img_default);
                }
            }
            thumbnailTotalWidth += currentWidth;
        }
        if (!TextUtils.isEmpty(text)) {
            mTvStickerView = new TextView(getContext());
            mTvStickerView.setTextSize(10);
            mTvStickerView.setTextColor(Color.WHITE);
            mTvStickerView.setLines(1);
            mTvStickerView.setGravity(Gravity.CENTER_VERTICAL);
            mTvStickerView.setBackgroundColor(Color.parseColor("#E57B28"));
            mTvStickerView.setText(text);
            mTvStickerView.setPadding(screenUtil.dip2px(getContext(),10),0,0,0);
            mLlThumbnail.addView(mTvStickerView);
            LinearLayout.LayoutParams parCenter = (LinearLayout.LayoutParams) mTvStickerView.getLayoutParams();
            parCenter.width = thumbnailTotalWidth;
            parCenter.height = params.singleHeight;
            mTvStickerView.setLayoutParams(parCenter);
        }

        if (albumType.isVideo(GetPathType.getInstance().getPathType(resPath))) {
            MediaInfo mediaInfo = new MediaInfo(resPath);
            mediaInfo.prepare();
            originalVideoDuration = (long) (mediaInfo.vDuration*1000);
            mediaInfo.release();
            Observable.create(new Observable.OnSubscribe<VideoTrimmerFrameBean>() {
                @Override
                public void call(Subscriber<? super VideoTrimmerFrameBean> subscriber) {
                    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                    mediaMetadataRetriever.setDataSource(params.outPath);
                    long duration1 = Long.parseLong(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                    long step = (long) (duration1 * 1.0f / params.count);
                    String filePath = FileUtil.getFrameTempPath();
                    FileUtil.deleteAllInDir(filePath);
                    for (int i = 0; i < params.count; i++) {
                        long current = i * step * 1000;
                        Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime(current);
                        File file = new File(filePath, String.format("frame_%s", System.currentTimeMillis()));
                        try (FileOutputStream fos = new FileOutputStream(file)) {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, fos);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            BitmapUtil.recycleBitmap(bitmap);
                        }
                        VideoTrimmerFrameBean bean = new VideoTrimmerFrameBean();
                        bean.setFramePath(file.getAbsolutePath());
                        bean.setIndex(i);
                        subscriber.onNext(bean);
                    }
                    mediaMetadataRetriever.release();
                    subscriber.onCompleted();

                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<VideoTrimmerFrameBean>() {
                @Override
                public void call(VideoTrimmerFrameBean bean) {
                    if (bean != null && mLlThumbnail.getChildCount() > bean.getIndex()) {
                        View view = mLlThumbnail.getChildAt(bean.getIndex());
                        if (view instanceof ImageView) {
                            Glide.with(BaseApplication.getInstance())
                                    .load(bean.getFramePath())
                                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                                    .into((ImageView) view);
                        }
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    LogUtil.d("初始化frame错误:%s", throwable.getMessage());
                }
            });
        } else if (!TextUtils.isEmpty(resPath) && albumType.isImage(GetPathType.getInstance().getPathType(resPath))) {
            for (int i = 0; i < mLlThumbnail.getChildCount(); i++) {
                View view = mLlThumbnail.getChildAt(i);
                if (view instanceof ImageView) {
                    Glide.with(BaseApplication.getInstance())
                            .load(resPath)
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                            .into((ImageView) view);
                }
            }
        }

        return thumbnailTotalWidth;
    }

    public void setDragListener(TouchDragListener dragListener) {
        this.dragListener = dragListener;
    }

    public void setTvStickerViewText(String text) {
        mTvStickerView.setText(text);
    }

    public interface TouchDragListener {
        //isDirection true左拖动 false右拖动  dragInterval拖动的时间
        void leftTouch(boolean isDirection, float dragInterval, int position);

        //isDirection true左拖动 false右拖动   dragInterval拖动的时间
        void rightTouch(boolean isDirection, float dragInterval, int position);

        void touchTextView(TemplateMaterialItemView view, boolean isDirection, float dragInterval, int position);

        void onClickTextView(TemplateMaterialItemView view);

        /***
         * 触摸事件结束
         * @param view 当前view
         * @param isDirection 为true时是左边的箭头拖动 false右边的箭头拖动
         */
        default void onTouchEnd(TemplateMaterialItemView view,boolean isDirection){}

        /**
         * 编辑统计
         * @param view 当前view
         * @param isOverallMove 是否是整体拖动
         */
        default void editStatistics(TemplateMaterialItemView view,boolean isOverallMove){}
    }

    public static class FrameParams implements Serializable {
        public String outPath;
        public int count;
        public int reviseCount;
        public int singleWidth;
        public int reviseWidth;
        public int singleHeight;
        int reviseHeight;

        public FrameParams(String outPath, int count, int reviseCount, int singleWidth, int reviseWidth, int singleHeight, int reviseHeight) {
            this.outPath = outPath;
            this.count = count;
            this.reviseCount = reviseCount;
            this.singleWidth = singleWidth;
            this.reviseWidth = reviseWidth;
            this.singleHeight = singleHeight;
            this.reviseHeight = reviseHeight;
        }
    }

    public long getDuration() {
        return mDuration;
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }
}
