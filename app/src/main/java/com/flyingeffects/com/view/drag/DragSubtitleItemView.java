package com.flyingeffects.com.view.drag;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
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

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.utils.screenUtil;

import java.io.Serializable;

/**
 * @author ZhouGang
 * @date 2020/11/2
 * 拖动的字幕的view
 */
public class DragSubtitleItemView extends LinearLayout implements View.OnTouchListener{

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

    public boolean isLongClickModule = false;
    private boolean isNeedOverallDrag = false;
    long lastTime = 0;
    long timeMove = 0;
    Vibrator vibrator;
    int identityID = 0;
    private long startTime;
    private long endTime;
    private long mDuration;
    private TextView mTvStickerView;
    int subtitleListId;

    public TouchDragListener dragListener;

    public DragSubtitleItemView(Context context) {
        super(context);
        initView();
    }

    public DragSubtitleItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DragSubtitleItemView(Context context, AttributeSet attrs, int defStyleAttr) {
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
                            dragListener.leftSubtitleTouch(true, leftDownX - event.getX(), identityID);
                        }
                    } else {
                        if (dragListener != null) {
                            dragListener.leftSubtitleTouch(false, event.getX() - leftDownX, identityID);
                        }
                    }
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                    if (dragListener != null) {
                        dragListener.onTouchEnd(this, true);
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
                            dragListener.rightSubtitleTouch(false, event.getX() - rightDownX, identityID);
                        }
                    } else {
                        if (dragListener != null) {
                            dragListener.rightSubtitleTouch(true, rightDownX - event.getX(), identityID);
                        }
                    }
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                    if (dragListener != null) {
                        dragListener.onTouchEnd(this, false);
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
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isNeedOverallDrag) {
                        timeMove = System.currentTimeMillis();
                        long durationMs = timeMove - lastTime;
                        if (durationMs > 500) {
                            isLongClickModule = true;
                            vibrator();
                            //是为了还在继续长按拖动的时候不再次震动
                            lastTime = timeMove + 1000*100;
                        }
                        if (isLongClickModule) {
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            if (llViewDownX < event.getX()) {
                                //整体往右移动
                                if (dragListener != null) {
                                    dragListener.touchSubtitleView(this, false, event.getX() - llViewDownX, identityID);
                                }
                            } else {
                                //整体往左移动
                                if (dragListener != null) {
                                    dragListener.touchSubtitleView(this, true, llViewDownX - event.getX(), identityID);
                                }
                            }
                        }
                    }else {
                        if (dragListener != null) {
                            dragListener.touchSubtitleView(this, false, 0, identityID);
                        }
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    if (dragListener != null) {
//                        dragListener.onTouchEnd(this, false);
                    }
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastTime <= 100) {
                        if (dragListener != null) {
                            dragListener.onClickSubtitleView(this);
                        }
                    }
                    isLongClickModule = false;
                    if (isNeedOverallDrag) {
                        cancelVibrator();
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
        return false;
    }

    private void vibrator() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(new long[]{10, 600}, -1));
        } else {
            vibrator.vibrate(new long[]{10, 600}, -1);
        }
    }

    public  void cancelVibrator() {
        if (vibrator != null) {
            vibrator.cancel();
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
        if (mLlThumbnail.getChildCount() > 0) {
            TextView textView = (TextView) mLlThumbnail.getChildAt(0);
            LayoutParams layoutParams = (LayoutParams) textView.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = height;
            textView.setLayoutParams(layoutParams);
        }
    }


    public String text;

    /**当前段落字幕的长度*/
    public int setResPathAndDuration(long duration, int containerHeight, String text) {
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
        FrameParams params = new FrameParams(count + reviseCount, reviseCount, frameSingleWidth, frameReviseWidth, containerHeight, containerHeight);
        //设置初始化
        for (int i = 0; i < params.count; i++) {
            int currentWidth = i >= params.count - params.reviseCount ? params.reviseWidth : params.singleWidth;
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
            LayoutParams parCenter = (LayoutParams) mTvStickerView.getLayoutParams();
            parCenter.width = thumbnailTotalWidth;
            parCenter.height = params.singleHeight;
            mTvStickerView.setLayoutParams(parCenter);
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
        void leftSubtitleTouch(boolean isDirection, float dragInterval, int position);

        //isDirection true左拖动 false右拖动   dragInterval拖动的时间
        void rightSubtitleTouch(boolean isDirection, float dragInterval, int position);

        void touchSubtitleView(DragSubtitleItemView view, boolean isDirection, float dragInterval, int position);

        void onClickSubtitleView(DragSubtitleItemView view);

        /***
         * 触摸事件结束
         * @param view 当前view
         * @param isDirection 为true时是左边的箭头拖动 false右边的箭头拖动
         */
        default void onTouchEnd(DragSubtitleItemView view, boolean isDirection){}

    }

    public static class FrameParams implements Serializable {
        public int count;
        public int reviseCount;
        public int singleWidth;
        public int reviseWidth;
        public int singleHeight;
        int reviseHeight;

        public FrameParams(int count, int reviseCount, int singleWidth, int reviseWidth, int singleHeight, int reviseHeight) {
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

    public int getSubtitleListId() {
        return subtitleListId;
    }

    public void setSubtitleListId(int subtitleListId) {
        this.subtitleListId = subtitleListId;
    }
}
