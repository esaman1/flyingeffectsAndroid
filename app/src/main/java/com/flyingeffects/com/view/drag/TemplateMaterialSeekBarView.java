package com.flyingeffects.com.view.drag;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.commonlyModel.GetPathType;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.utils.screenUtil;
import com.shixing.sxve.ui.albumType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZhouGang
 * @date 2020/11/2
 * 模板素材拖动view
 */
public class TemplateMaterialSeekBarView extends RelativeLayout implements TemplateMaterialItemView.TouchDragListener,
        ObserveHorizontalScrollView.OnTouchListener{
    public static final float NOVIDEO_STAGE_WIDTH = 720f;
    public static final float NOVIDEO_STAGE_HEIGHT = 1280f;
    /**每一像素所占的时长为15毫秒*/
    public static final long PER_MS_IN_PX = 15;


    ObserveHorizontalScrollView mMaterialSeekBar;
    LinearLayout mLlDragItem;
    View mViewFrame;
    ScrollView mScrollViewMaterialSeekbar;
    private List<TemplateMaterialItemView> mTemplateMaterialItemViews = new ArrayList<>();

    public boolean dragScrollView;
    /**
     * 左右边距
     */
    int frameListPadding;
    int frameContainerWidth;
    int frameContainerHeight;
    long mDuration;
    long cutStartTime;
    long cutEndTime;
    boolean isGreenScreen;
    int oldThumbnailTotalWidth = 0;


    public TemplateMaterialSeekBarView(Context context) {
        super(context);
        initView();
    }

    public TemplateMaterialSeekBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TemplateMaterialSeekBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_template_material_seekbar, null);
        mScrollViewMaterialSeekbar = view.findViewById(R.id.scrollView_material_seekbar);
        mMaterialSeekBar = view.findViewById(R.id.material_seekbar);
        mLlDragItem = view.findViewById(R.id.ll_item_drag);
        mViewFrame = view.findViewById(R.id.view_frame);
        addView(view);
        mMaterialSeekBar.setOnScrollChangeListener(new ObserveHorizontalScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt, boolean onDragChanged) {
                if (l * PER_MS_IN_PX < cutStartTime) {
                    scrollToPosition(cutStartTime);
                }
                if (l * PER_MS_IN_PX > cutEndTime) {
                    scrollToPosition(cutEndTime);
                }
                //获取当前操作的字幕POS位置
                long  process = l * PER_MS_IN_PX;
//                for (int i = 0; mTemplateMaterialItemViews != null && i < mTemplateMaterialItemViews.size(); i++) {
//                    TemplateMaterialItemView materialItemView = mTemplateMaterialItemViews.get(i);
//                    if (process >= mTemplateMaterialItemViews.get(i).getStartTime() && process < mTemplateMaterialItemViews.get(i).getEndTime() &&
//                            i == materialItemView.getIdentityID()) {
//                        materialItemView.isShowArrow(true);
//                    } else {
//                        materialItemView.isShowArrow(false);
//                    }
//                }
                if (mProgressListener != null) {
                    mProgressListener.progress(process, dragScrollView);
                }
            }

            @Override
            public void onTouchStart() {
                if (mProgressListener != null) {
                    mProgressListener.trackPause();
                }
            }

            @Override
            public void onTouchEnd() {
                dragScrollView = false;
                if (mProgressListener != null) {
                    mProgressListener.manualDrag(dragScrollView);
                }
            }
        });
        mMaterialSeekBar.setOnTouchListener(this);
        frameContainerHeight = screenUtil.dip2px(getContext(), 40);
    }


    public void setGreenScreen(boolean isGreenScreen){
        this.isGreenScreen = isGreenScreen;
    }

    /***
     * 预览视频时长裁剪 素材的起止时间也随着裁剪或偏移
     * @param startTime  开始时间
     * @param endTime   结束时间
     */
    public void setCutStartAndEndTime(long startTime, long endTime) {
        cutStartTime = startTime;
        cutEndTime = endTime;
        for (int i = 0; i < mTemplateMaterialItemViews.size(); i++) {
            if (mTemplateMaterialItemViews.get(i) != null) {
                TemplateMaterialItemView itemView = mTemplateMaterialItemViews.get(i);
                if (itemView.getStartTime() > cutStartTime && itemView.getEndTime() < cutEndTime) {
                    continue;
                }
                if (itemView.getStartTime() < cutStartTime && itemView.getEndTime() > cutEndTime) {
                    itemView.setStartTime(cutStartTime);
                    itemView.setEndTime(cutEndTime);
                }
                if (itemView.getStartTime() > cutStartTime && itemView.getEndTime() > cutEndTime) {
                    itemView.setStartTime(cutEndTime - (itemView.getEndTime() - itemView.getStartTime()));
                    itemView.setEndTime(cutEndTime);
                }
                if (itemView.getStartTime() > cutStartTime && cutEndTime - itemView.getStartTime() < 1000) {
                    itemView.setStartTime(cutStartTime);
                }
                if (itemView.getEndTime() < cutStartTime) {
                    long offsetTime = cutStartTime - itemView.getStartTime();
                    itemView.setStartTime(itemView.getStartTime() + offsetTime);
                    itemView.setEndTime(itemView.getEndTime() + offsetTime);
                }
                if (itemView.getEndTime() > cutEndTime) {
                    itemView.setEndTime(cutEndTime);
                }
                if (itemView.getStartTime() < cutStartTime) {
                    itemView.setStartTime(cutStartTime);
                }
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) itemView.getLayoutParams();
                itemView.setWidthAndHeight((int) ((itemView.getEndTime() - itemView.getStartTime()) / PER_MS_IN_PX), frameContainerHeight);
                params.setMargins((int) (itemView.getStartTime() / PER_MS_IN_PX + frameListPadding - TemplateMaterialItemView.ARROW_WIDTH),
                        screenUtil.dip2px(getContext(), 5), 0, 0);
                itemView.setLayoutParams(params);
            }
        }
    }

    /**重置素材时间轴的起止时间*/
    public void resetStartAndEndTime(long startTime, long endTime) {
        cutStartTime = startTime;
        cutEndTime = endTime;
        for (int i = 0; i < mTemplateMaterialItemViews.size(); i++) {
            if (mTemplateMaterialItemViews.get(i) != null) {
                TemplateMaterialItemView itemView = mTemplateMaterialItemViews.get(i);
                itemView.setStartTime(0);
                if (endTime > itemView.getDuration()) {
                    if (albumType.isImage(GetPathType.getInstance().getPathType(itemView.resPath))) {
                        itemView.setEndTime(endTime);
                    } else if (!TextUtils.isEmpty(itemView.text)) {
                        itemView.setEndTime(endTime);
                    } else {
                        itemView.setEndTime(itemView.getDuration());
                    }
                } else {
                    itemView.setEndTime(endTime);
                }
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) itemView.getLayoutParams();
                itemView.setWidthAndHeight((int) ((itemView.getEndTime() - itemView.getStartTime()) / PER_MS_IN_PX), frameContainerHeight);
                params.setMargins((int) (itemView.getStartTime() / PER_MS_IN_PX + frameListPadding - TemplateMaterialItemView.ARROW_WIDTH),
                        screenUtil.dip2px(getContext(), 5), 0, 0);
                itemView.setLayoutParams(params);
                int thumbnailTotalWidth = itemView.setResPathAndDuration(itemView.resPath, endTime - startTime,
                        frameContainerHeight, itemView.isText, itemView.text);
                RelativeLayout.LayoutParams reParams = (LayoutParams) mViewFrame.getLayoutParams();
                reParams.width = thumbnailTotalWidth + frameListPadding * 2;
                mViewFrame.setLayoutParams(reParams);
            }
        }
    }

    public void scrollToPosition(long process) {
        mMaterialSeekBar.scrollTo((int) Math.ceil(process / (PER_MS_IN_PX * 1f)), 0);
    }

    /**点击当前贴纸后显示箭头*/
    public void isCurrentMaterialShowArrow(String id) {
        for (int i = 0; i < mTemplateMaterialItemViews.size(); i++) {
            if (mTemplateMaterialItemViews.get(i) != null) {
                if (TextUtils.equals(String.valueOf(mTemplateMaterialItemViews.get(i).getIdentityID()), id)) {
                    mTemplateMaterialItemViews.get(i).isShowArrow(true);
                } else {
                    mTemplateMaterialItemViews.get(i).isShowArrow(false);
                }
            }
        }
    }

    /**重新选择了背景视频 时长改变宽度也随之改变 重新设置宽度*/
    public void changeVideoPathViewFrameSetWidth(long duration) {
        TemplateMaterialItemView itemView = null;
        for (int i = 0; i < mTemplateMaterialItemViews.size(); i++) {
            if (mTemplateMaterialItemViews.get(i) != null) {
                itemView = mTemplateMaterialItemViews.get(i);
            }
        }
        int thumbnailTotalWidth = itemView.changeVideoPathWidth(duration, frameContainerHeight);
        RelativeLayout.LayoutParams reParams = (LayoutParams) mViewFrame.getLayoutParams();
        reParams.width = thumbnailTotalWidth + frameListPadding * 2;
        mViewFrame.setLayoutParams(reParams);
    }

    /**
     * 修改了素材 重新设置缩略图
     * @param path 新的素材路径
     * @param id 素材ID
     */
    public void modifyMaterialThumbnail(String path, String id) {
        for (int i = 0; i < mTemplateMaterialItemViews.size(); i++) {
            if (mTemplateMaterialItemViews.get(i) != null) {
                if (TextUtils.equals(String.valueOf(mTemplateMaterialItemViews.get(i).getIdentityID()), id)) {
                    TemplateMaterialItemView itemView = mTemplateMaterialItemViews.get(i);
                    itemView.setResPathAndDuration(path, cutEndTime - cutStartTime, frameContainerHeight, false, "");
                    break;
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        frameContainerWidth = measureDimension(widthMeasureSpec);
        frameListPadding = frameContainerWidth / 2;
        setMeasuredDimension(frameContainerWidth,heightMeasureSpec);
    }

    public int measureDimension(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        }
        return result;
    }

    /***
     * 添加一个素材 增加一个时间轴拖动条
     * @param duration 时长
     * @param resPath 素材路径
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param isText 是否是文本
     * @param text 文本内容
     * @param id 素材ID
     */
    public void addTemplateMaterialItemView(long duration, String resPath,long startTime,long endTime,boolean isText,String text,String id) {
        this.mDuration = duration;
        this.cutStartTime = 0;
        this.cutEndTime = duration;
        TemplateMaterialItemView materialItemView = new TemplateMaterialItemView(getContext());
        materialItemView.setDuration(duration);
        mTemplateMaterialItemViews.add(materialItemView);
        materialItemView.setIdentityID(Integer.valueOf(id));

        int thumbnailTotalWidth = materialItemView.setResPathAndDuration(resPath, duration, frameContainerHeight, isText, text);
        if (thumbnailTotalWidth > oldThumbnailTotalWidth) {
            RelativeLayout.LayoutParams reParams = (LayoutParams) mViewFrame.getLayoutParams();
            reParams.width = thumbnailTotalWidth + frameListPadding * 2;
            mViewFrame.setLayoutParams(reParams);
            oldThumbnailTotalWidth = thumbnailTotalWidth;
        }

        materialItemView.setStartTime(startTime);
        materialItemView.setEndTime(endTime);
        materialItemView.isShowArrow(false);
        materialItemView.isNeedOverallDrag(true);
        materialItemView.setWidthAndHeight((int) ((endTime - startTime) / PER_MS_IN_PX), frameContainerHeight);

        mLlDragItem.addView(materialItemView);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) materialItemView.getLayoutParams();
        long intervalPX = frameListPadding - TemplateMaterialItemView.ARROW_WIDTH;
        //设置左的Margins为屏幕宽度的一半减去箭头的宽度
        params.setMargins((int) (intervalPX + startTime / PER_MS_IN_PX), screenUtil.dip2px(getContext(), 5), 0, 0);
        materialItemView.setLayoutParams(params);
        materialItemView.setDragListener(this);
        if (mLlDragItem.getChildCount() >= 3) {
            mScrollViewMaterialSeekbar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mScrollViewMaterialSeekbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mScrollViewMaterialSeekbar.post(new Runnable() {
                        @Override
                        public void run() {
                            mScrollViewMaterialSeekbar.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
            });
        }
    }

    /**删除当前贴纸时间轴的view*/
    public void deleteTemplateMaterialItemView(String id) {
        for (int i = 0; i < mTemplateMaterialItemViews.size(); i++) {
            if (mTemplateMaterialItemViews.get(i) != null) {
                if (TextUtils.equals(String.valueOf(mTemplateMaterialItemViews.get(i).getIdentityID()), id)) {
                    mLlDragItem.removeView(mTemplateMaterialItemViews.get(i));
                    mTemplateMaterialItemViews.set(i, null);
                    break;
                }
            }
        }
    }

    /**更新文本贴纸时间轴上的文字*/
    public void updateStickerViewText(String text, String id) {
        for (int i = 0; i < mTemplateMaterialItemViews.size(); i++) {
            if (mTemplateMaterialItemViews.get(i) != null) {
                if (TextUtils.equals(String.valueOf(mTemplateMaterialItemViews.get(i).getIdentityID()), id)) {
                    mTemplateMaterialItemViews.get(i).setTvStickerViewText(text);
                    break;
                }
            }
        }
    }

    @Override
    public void leftTouch(boolean isDirection, float dragInterval, int position) {
        if (mTemplateMaterialItemViews != null && mTemplateMaterialItemViews.size() > 0) {
            TemplateMaterialItemView materialItemView = mTemplateMaterialItemViews.get(position);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) materialItemView.getLayoutParams();
            if (position == materialItemView.getIdentityID()) {
                //左拖动
                if (isDirection) {
                    if (albumType.isVideo(GetPathType.getInstance().getPathType(materialItemView.resPath))) {
                        long st = (long) (materialItemView.getStartTime() - (PER_MS_IN_PX * dragInterval));
                        if (st <= cutStartTime) {
                            return;
                        } else if (materialItemView.getEndTime() - materialItemView.getStartTime() >= materialItemView.originalVideoDuration) {
                            return;
                        }else {
                            materialItemView.setStartTime(st);
                        }
                    }else {
                        if (materialItemView.getStartTime() <= cutStartTime) {
                            return;
                        } else {
                            materialItemView.setStartTime((long) (materialItemView.getStartTime() - PER_MS_IN_PX * dragInterval));
                        }
                    }
                } else {
                    //右拖动
                    if (materialItemView.getStartTime() + PER_MS_IN_PX * dragInterval > cutEndTime) {
                        return;
                    } else {
                        long st = (long) (materialItemView.getStartTime() + (PER_MS_IN_PX * dragInterval));
                        if (materialItemView.getEndTime() - st < 1000) {
                            return;
                        } else {
                            materialItemView.setStartTime(st);
                        }
                    }
                }
            }
            materialItemView.setWidthAndHeight((int) ((materialItemView.getEndTime() - materialItemView.getStartTime()) / PER_MS_IN_PX), frameContainerHeight);
            params.setMargins((int) (materialItemView.getStartTime() / PER_MS_IN_PX + frameListPadding - TemplateMaterialItemView.ARROW_WIDTH),
                    screenUtil.dip2px(getContext(), 5), 0, 0);
            materialItemView.setLayoutParams(params);
            if (mProgressListener != null) {
                mProgressListener.timelineChange(materialItemView.getStartTime(), materialItemView.getEndTime(), String.valueOf(materialItemView.getIdentityID()));
            }
        }
    }

    @Override
    public void rightTouch(boolean isDirection, float dragInterval, int position) {
        if (mTemplateMaterialItemViews != null && mTemplateMaterialItemViews.size() > 0) {
            TemplateMaterialItemView materialItemView = mTemplateMaterialItemViews.get(position);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) materialItemView.getLayoutParams();
            if (position == materialItemView.getIdentityID()) {
                //左拖动
                if (isDirection) {
                    if ((materialItemView.getEndTime() - PER_MS_IN_PX * dragInterval) - materialItemView.getStartTime() < 1000) {
                        return;
                    } else {
                        materialItemView.setEndTime((long) (materialItemView.getEndTime() - PER_MS_IN_PX * dragInterval));
                    }
                } else {
                    //右拖动
                    long et = (long) (materialItemView.getEndTime() + (PER_MS_IN_PX * dragInterval));
                    if (albumType.isVideo(GetPathType.getInstance().getPathType(materialItemView.resPath))) {
                        if (et > cutEndTime) {
                            return;
                        }else if (materialItemView.getEndTime() - materialItemView.getStartTime() >= materialItemView.originalVideoDuration) {
                            return;
                        }else {
                            materialItemView.setEndTime(et);
                        }
                    } else {
                        if (et > cutEndTime) {
                            return;
                        } else {
                            materialItemView.setEndTime(et);
                        }
                    }
                }
            }
            materialItemView.setWidthAndHeight((int) ((materialItemView.getEndTime() - materialItemView.getStartTime()) / PER_MS_IN_PX), frameContainerHeight);
            params.setMargins((int) (materialItemView.getStartTime() / PER_MS_IN_PX + frameListPadding - TemplateMaterialItemView.ARROW_WIDTH),
                    screenUtil.dip2px(getContext(), 5), 0, 0);
            materialItemView.setLayoutParams(params);
            if (mProgressListener != null) {
                mProgressListener.timelineChange(materialItemView.getStartTime(), materialItemView.getEndTime(), String.valueOf(materialItemView.getIdentityID()));
            }
        }
    }

    @Override
    public void editStatistics(TemplateMaterialItemView view, boolean isOverallMove) {
        if (isOverallMove) {
            if (isGreenScreen) {
                if (albumType.isVideo(GetPathType.getInstance().getPathType(view.resPath))) {
                    statisticsEventAffair.getInstance().setFlag(getContext(), "21_bj_gd1_move");
                } else if (TextUtils.isEmpty(view.text)) {
                    statisticsEventAffair.getInstance().setFlag(getContext(), "21_bj_gd3_move");
                } else {
                    statisticsEventAffair.getInstance().setFlag(getContext(), "21_bj_gd2_move");
                }
            } else {
                if (albumType.isVideo(GetPathType.getInstance().getPathType(view.resPath))) {
                    statisticsEventAffair.getInstance().setFlag(getContext(), "21_mb_gd1_move");
                } else if (TextUtils.isEmpty(view.text)) {
                    statisticsEventAffair.getInstance().setFlag(getContext(), "21_mb_gd3_move");
                } else {
                    statisticsEventAffair.getInstance().setFlag(getContext(), "21_mb_gd2_move");
                }
            }
        } else {
            if (isGreenScreen) {
                if (albumType.isVideo(GetPathType.getInstance().getPathType(view.resPath))) {
                    statisticsEventAffair.getInstance().setFlag(getContext(), "21_bj_gd1_clip");
                } else if (TextUtils.isEmpty(view.text)) {
                    statisticsEventAffair.getInstance().setFlag(getContext(), "21_bj_gd3_clip");
                } else {
                    statisticsEventAffair.getInstance().setFlag(getContext(), "21_bj_gd2_clip");
                }
            } else {
                if (albumType.isVideo(GetPathType.getInstance().getPathType(view.resPath))) {
                    statisticsEventAffair.getInstance().setFlag(getContext(), "21_mb_gd1_clip");
                } else if (TextUtils.isEmpty(view.text)) {
                    statisticsEventAffair.getInstance().setFlag(getContext(), "21_mb_gd3_clip");
                } else {
                    statisticsEventAffair.getInstance().setFlag(getContext(), "21_mb_gd2_clip");
                }
            }
        }
    }

    @Override
    public void touchTextView(TemplateMaterialItemView view, boolean isDirection, float dragInterval, int position) {
        dragScrollView = false;
        for (int i = 0; i < mTemplateMaterialItemViews.size(); i++) {
            if (mTemplateMaterialItemViews.get(i) != null) {
                TemplateMaterialItemView dragSubtitleView = mTemplateMaterialItemViews.get(i);
                if (view.getIdentityID() == dragSubtitleView.getIdentityID()) {
                    dragSubtitleView.isShowArrow(true);
                } else {
                    dragSubtitleView.isShowArrow(false);
                }
            }
        }
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (position == view.getIdentityID()) {
            //整体左移动
            if (isDirection) {
                if (view.getStartTime() - dragInterval * PER_MS_IN_PX < cutStartTime) {
                    return;
                } else {
                    view.setStartTime((long) (view.getStartTime() - dragInterval * PER_MS_IN_PX));
                    view.setEndTime((long) (view.getEndTime() - dragInterval * PER_MS_IN_PX));
                }
            } else {
                //整体右移动
                if (view.getEndTime() + dragInterval * PER_MS_IN_PX > cutEndTime) {
                    return;
                } else {
                    view.setStartTime((long) (view.getStartTime() + dragInterval * PER_MS_IN_PX));
                    view.setEndTime((long) (view.getEndTime() + dragInterval * PER_MS_IN_PX));
                }
            }
            params.setMargins((int) (view.getStartTime() / PER_MS_IN_PX + frameListPadding - TemplateMaterialItemView.ARROW_WIDTH),
                    screenUtil.dip2px(getContext(), 5), 0, 0);
            view.setLayoutParams(params);
            if (mProgressListener != null) {
                mProgressListener.timelineChange(view.getStartTime(), view.getEndTime(), String.valueOf(view.getIdentityID()));
            }
        }
    }

    @Override
    public void onClickTextView(TemplateMaterialItemView view) {
        for (int i = 0; i < mTemplateMaterialItemViews.size(); i++) {
            if (mTemplateMaterialItemViews.get(i) != null) {
                TemplateMaterialItemView itemView = mTemplateMaterialItemViews.get(i);
                if (TextUtils.equals(String.valueOf(view.getIdentityID()), String.valueOf(itemView.getIdentityID()))) {
                    itemView.isShowArrow(true);
                    if (mProgressListener != null) {
                        mProgressListener.currentViewSelected(String.valueOf(view.getIdentityID()));
                    }
                } else {
                    itemView.isShowArrow(false);
                }
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == mMaterialSeekBar) {
            dragScrollView = true;
        }
        if (v == mLlDragItem) {
            for (int i = 0; i < mTemplateMaterialItemViews.size(); i++) {
                if (mTemplateMaterialItemViews.get(i) != null) {
                    TemplateMaterialItemView itemView = mTemplateMaterialItemViews.get(i);
                    itemView.requestDisallowInterceptTouchEvent(false);
                }
            }
        }
        return false;
    }

    public interface SeekBarProgressListener{

        void progress(long progress,boolean manualDrag);

        void manualDrag(boolean manualDrag);

        void timelineChange(long startTime,long endTime,String id);

        void currentViewSelected(String id);

        void trackPause();
    }

    SeekBarProgressListener mProgressListener;

    public void setProgressListener(SeekBarProgressListener progressListener) {
        mProgressListener = progressListener;
    }

    /***
     * 得到所有素材
     * @return
     */
    public List<TemplateMaterialItemView> getTemplateMaterialItemViews() {
        return mTemplateMaterialItemViews;
    }

    public void setCutEndTime(long cutEndTime) {
        this.cutEndTime = cutEndTime;
    }

    public void setCutStartTime(long cutStartTime) {
        this.cutStartTime = cutStartTime;
    }
}
