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
import com.flyingeffects.com.entity.SubtitleEntity;
import com.flyingeffects.com.utils.screenUtil;
import com.lansosdk.videoeditor.MediaInfo;
import com.shixing.sxve.ui.AlbumType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZhouGang
 * @date 2020/11/2
 * 模板素材拖动view
 */
public class JakeFontMakeSeekBarView extends RelativeLayout implements TemplateMaterialItemView.TouchDragListener,
        ObserveHorizontalScrollView.OnTouchListener,DragSubtitleItemView.TouchDragListener{
    public static final float NOVIDEO_STAGE_WIDTH = 720f;
    public static final float NOVIDEO_STAGE_HEIGHT = 1280f;
    /**每一像素所占的时长为15毫秒*/
    public static final long PER_MS_IN_PX = 15;


    ObserveHorizontalScrollView mMaterialSeekBar;
    LinearLayout mLlDragItem;
    View mViewFrame;
    ScrollView mScrollViewMaterialSeekbar;
    /**单个的文本view或者视频或者图片贴纸view*/
    private List<TemplateMaterialItemView> mTemplateMaterialItemViews = new ArrayList<>();
    /**所有的拖动字幕的集合*/
    private List<DragSubtitleItemView> mDragSubtitleItemViews = new ArrayList<>();

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
    public int subtitleIndex =-1;


    public JakeFontMakeSeekBarView(Context context) {
        super(context);
        initView();
    }

    public JakeFontMakeSeekBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public JakeFontMakeSeekBarView(Context context, AttributeSet attrs, int defStyleAttr) {
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
                if (dragScrollView) {
                    isDrag = true;
                } else {
                    isDrag = false;
                }
                if (mProgressListener != null) {
                    mProgressListener.progress(process, isDrag);
                }
            }

            @Override
            public void onTouchStart() {
                dragScrollView = true;
                if (mProgressListener != null) {
                    mProgressListener.trackPause();
                }
            }

            @Override
            public void onTouchEnd() {
                dragScrollView = true;
                if (mProgressListener != null) {
                    mProgressListener.manualDrag(true);
                }
            }
        });
        mMaterialSeekBar.setOnTouchListener(this);
        frameContainerHeight = screenUtil.dip2px(getContext(), 40);
    }

    boolean isDrag = false;
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
                    itemView.setStartTime(cutStartTime);
                    itemView.setEndTime(cutStartTime + 1000);
                }
                if (itemView.getEndTime() > cutEndTime) {
                    itemView.setEndTime(cutEndTime);
                    if (cutEndTime - itemView.getStartTime() <= 1000) {
                        itemView.setStartTime(cutEndTime - 1000);
                    }
                }
                if (itemView.getStartTime() < startTime) {
                    itemView.setStartTime(cutStartTime);
                    if (itemView.getEndTime() - startTime <= 1000) {
                        itemView.setEndTime(cutStartTime + 1000);
                    }
                }

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) itemView.getLayoutParams();
                itemView.setWidthAndHeight((int) ((itemView.getEndTime() - itemView.getStartTime()) / PER_MS_IN_PX), frameContainerHeight);
                params.setMargins((int) (itemView.getStartTime() / PER_MS_IN_PX + frameListPadding - TemplateMaterialItemView.ARROW_WIDTH),
                        screenUtil.dip2px(getContext(), 5), 0, 0);
                itemView.setLayoutParams(params);
            }
        }
        for (int j = 0; j < mDragSubtitleItemViews.size(); j++) {
            if (mDragSubtitleItemViews.get(j) != null) {
                DragSubtitleItemView subtitleItemView = mDragSubtitleItemViews.get(j);
                if (j == 0) {
                    subtitleItemView.setStartTime(cutStartTime);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) subtitleItemView.getLayoutParams();
                    subtitleItemView.setWidthAndHeight((int) ((subtitleItemView.getEndTime() - subtitleItemView.getStartTime()) / PER_MS_IN_PX), frameContainerHeight);
                    params.setMargins((int) (subtitleItemView.getStartTime() / PER_MS_IN_PX + frameListPadding - TemplateMaterialItemView.ARROW_WIDTH),
                            screenUtil.dip2px(getContext(), 5), 0, 0);
                    subtitleItemView.setLayoutParams(params);
                }
                if (j == mDragSubtitleItemViews.size() - 1) {
                    subtitleItemView.setEndTime(cutEndTime);
                    subtitleItemView.setWidthAndHeight((int) ((subtitleItemView.getEndTime() - subtitleItemView.getStartTime()) / PER_MS_IN_PX), frameContainerHeight);
                }
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

    /**点击当前字幕后显示箭头*/
    public void isCurrentSubtitleShowArrow(String id) {
        for (int i = 0; i < mDragSubtitleItemViews.size(); i++) {
            if (mDragSubtitleItemViews.get(i) != null) {
                if (TextUtils.equals(String.valueOf(mDragSubtitleItemViews.get(i).getIdentityID()), id)) {
                    mDragSubtitleItemViews.get(i).isShowArrow(true);
                } else {
                    mDragSubtitleItemViews.get(i).isShowArrow(false);
                }
            }
        }
    }


    /**
     * 修改了素材 重新设置缩略图 或者修改字幕的内容
     * @param path 新的素材路径
     * @param id 素材ID
     * @param isSubtitles 是否字幕
     */
    public void modifyMaterialOrSubtitle(String path, String id, boolean isSubtitles,String text) {
        if(!isSubtitles){
            for (int i = 0; i < mTemplateMaterialItemViews.size(); i++) {
                if (mTemplateMaterialItemViews.get(i) != null) {
                    if (TextUtils.equals(String.valueOf(mTemplateMaterialItemViews.get(i).getIdentityID()), id)) {
                        TemplateMaterialItemView itemView = mTemplateMaterialItemViews.get(i);
                        if (AlbumType.isVideo(GetPathType.getInstance().getPathType(path))) {
                            MediaInfo mediaInfo = new MediaInfo(path);
                            mediaInfo.prepare();
                            long duration = (long) (mediaInfo.vDuration * 1000);
                            mediaInfo.release();
                            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) itemView.getLayoutParams();
                            itemView.setResPathAndDuration(path, duration, frameContainerHeight, itemView.isText, itemView.text);
                            itemView.setStartTime(cutStartTime);
                            long minDuration = Math.min(duration, cutEndTime);
                            itemView.setEndTime(minDuration);
                            itemView.setDuration(minDuration);
                            itemView.setWidthAndHeight((int) ((itemView.getEndTime()-itemView.getStartTime()) / PER_MS_IN_PX), frameContainerHeight);
                            params.setMargins((int) (itemView.getStartTime() / PER_MS_IN_PX + frameListPadding - TemplateMaterialItemView.ARROW_WIDTH),
                                    screenUtil.dip2px(getContext(), 5), 0, 0);
                            itemView.setLayoutParams(params);
                        } else if (AlbumType.isImage(GetPathType.getInstance().getPathType(path))) {
                            itemView.setResPathAndDuration(path, cutEndTime - cutStartTime, frameContainerHeight, false, "");
                        } else {
                            itemView.setTvStickerViewText(text);
                        }
                        break;
                    }
                }
            }
        } else {
            for (int i = 0; i < mDragSubtitleItemViews.size(); i++) {
                if (mDragSubtitleItemViews.get(i) != null) {
                    if (TextUtils.equals(String.valueOf(mDragSubtitleItemViews.get(i).getSubtitleListId()), id)) {
                        DragSubtitleItemView subtitleItemView = mDragSubtitleItemViews.get(i);
                        subtitleItemView.setTvStickerViewText(text);
                    }
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
     * 添加一个素材 增加一个时间轴拖动条 添加多个字幕  增加一个时间轴
     * @param duration 时长
     * @param resPath 素材路径
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param isText 是否是文本
     * @param text 文本内容
     * @param id 素材ID
     * @param subtitles 字幕集合
     * @param subtitleListId 字幕集合的ID
     * @param totalWidth 时间轴的最长宽度
     */
    List<SubtitleEntity> subtitles;
    public void addTemplateMaterialItemView(long duration, String resPath, long startTime, long endTime,
                                            boolean isText, String text, int id, List<SubtitleEntity> subtitles,
                                            int subtitleListId,int totalWidth) {
        this.mDuration = duration;
        this.cutStartTime = 0;
        this.cutEndTime = duration;
        LayoutParams reParams = (LayoutParams) mViewFrame.getLayoutParams();
        reParams.width = totalWidth + frameListPadding * 2;
        mViewFrame.setLayoutParams(reParams);
        if (subtitles != null && !subtitles.isEmpty()) {
            this.subtitles = subtitles;
            RelativeLayout mDragSubtitleLl = new RelativeLayout(getContext());
            mDragSubtitleLl.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, frameContainerHeight));
            mLlDragItem.addView(mDragSubtitleLl);
            subtitleIndex = subtitleListId;
            //先添加一个所有字幕的容器  在把字幕的view添加到这个容器去

            for (int i = 0; i < subtitles.size(); i++) {
                DragSubtitleItemView subtitleItemView = new DragSubtitleItemView(getContext());
                subtitleItemView.setStartTime(subtitles.get(i).getStartTime());
                subtitleItemView.setEndTime(subtitles.get(i).getEndTime());
                subtitleItemView.setResPathAndDuration(subtitles.get(i).getEndTime() - subtitles.get(i).getStartTime(), frameContainerHeight, subtitles.get(i).getText());
                subtitleItemView.setSubtitleListId(subtitleListId);
                subtitleItemView.setIdentityID(i);
                subtitleItemView.isShowArrow(false);
                subtitleItemView.isNeedOverallDrag(true);
                subtitleItemView.setDragListener(this);
                subtitleItemView.setWidthAndHeight((int) ((subtitles.get(i).getEndTime() - subtitles.get(i).getStartTime()) / PER_MS_IN_PX), frameContainerHeight);
                mDragSubtitleLl.addView(subtitleItemView);
                RelativeLayout.LayoutParams subtitleItemViewLayoutParams = (RelativeLayout.LayoutParams) subtitleItemView.getLayoutParams();

                subtitleItemViewLayoutParams.setMargins((int) (subtitles.get(i).getStartTime() / PER_MS_IN_PX), 0, 0, 0);
                subtitleItemView.setLayoutParams(subtitleItemViewLayoutParams);
                mDragSubtitleItemViews.add(subtitleItemView);
            }

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mDragSubtitleLl.getLayoutParams();
            //设置左的Margins为屏幕宽度的一半减去箭头的宽度
            params.setMargins(frameListPadding- DragSubtitleItemView.ARROW_WIDTH,screenUtil.dip2px(getContext(), 5), 0, 0);
            mDragSubtitleLl.setLayoutParams(params);
        } else {
            TemplateMaterialItemView materialItemView = new TemplateMaterialItemView(getContext());
            if (AlbumType.isVideo(GetPathType.getInstance().getPathType(resPath))) {
                MediaInfo mediaInfo = new MediaInfo(resPath);
                mediaInfo.prepare();
                materialItemView.setDuration((long) (mediaInfo.vDuration * 1000));
                mediaInfo.release();
            } else {
                materialItemView.setDuration(duration);
            }
            mTemplateMaterialItemViews.add(materialItemView);

            materialItemView.setIdentityID(id);
            materialItemView.setResPathAndDuration(resPath, duration, frameContainerHeight, isText, text);
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
        }
    }

    /**所有素材轨道的scrollView滚动到底部*/
    public void scrollToTheBottom() {
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

    /**删除当前字幕时间轴的某view*/
    public void deleteSubtitleMaterialItemView(String id,int listId) {
        for (int i = 0; i < mDragSubtitleItemViews.size(); i++) {
            if (mDragSubtitleItemViews.get(i) != null) {
                if (TextUtils.equals(String.valueOf(mDragSubtitleItemViews.get(i).getIdentityID()), id)) {
                    RelativeLayout relativeLayout = (RelativeLayout) mLlDragItem.getChildAt(listId);
                    relativeLayout.removeView(mDragSubtitleItemViews.get(i));
                    mDragSubtitleItemViews.set(i, null);
                    break;
                }
            }
        }
    }

    /**删除当前所有字幕时间轴的view*/
    public void deleteSubtitleView(int listId) {
        if (mLlDragItem.getChildAt(listId) != null) {
            mLlDragItem.removeView(mLlDragItem.getChildAt(listId));
            mDragSubtitleItemViews.clear();
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
        dragScrollView = false;
        if (mTemplateMaterialItemViews != null && mTemplateMaterialItemViews.size() > 0) {
            TemplateMaterialItemView materialItemView = mTemplateMaterialItemViews.get(position);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) materialItemView.getLayoutParams();
            if (position == materialItemView.getIdentityID()) {
                //左拖动
                if (isDirection) {
                    if (AlbumType.isVideo(GetPathType.getInstance().getPathType(materialItemView.resPath))) {
                        long st = (long) (materialItemView.getStartTime() - (PER_MS_IN_PX * dragInterval));
                        if (st <= cutStartTime) {
                            materialItemView.setStartTime(cutStartTime);
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
                mProgressListener.timelineChange(materialItemView.getStartTime(), materialItemView.getEndTime(), String.valueOf(materialItemView.getIdentityID()),false);
                mProgressListener.trackPause();
            }
        }
    }

    @Override
    public void rightTouch(boolean isDirection, float dragInterval, int position) {
        dragScrollView = false;
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
                    if (AlbumType.isVideo(GetPathType.getInstance().getPathType(materialItemView.resPath))) {
                        if (et > cutEndTime) {
                            materialItemView.setEndTime(cutEndTime);
                            return;
                        }else if (materialItemView.getEndTime() - materialItemView.getStartTime() >= materialItemView.originalVideoDuration) {
                            return;
                        }else {
                            materialItemView.setEndTime(et);
                        }
                    } else {
                        if (et > cutEndTime) {
                            materialItemView.setEndTime(cutEndTime);
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
                mProgressListener.timelineChange(materialItemView.getStartTime(), materialItemView.getEndTime(), String.valueOf(materialItemView.getIdentityID()),false);
                mProgressListener.trackPause();
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
                    view.setStartTime(cutStartTime);
                    return;
                } else {
                    view.setStartTime((long) (view.getStartTime() - dragInterval * PER_MS_IN_PX));
                    view.setEndTime((long) (view.getEndTime() - dragInterval * PER_MS_IN_PX));
                }
            } else {
                //整体右移动
                if (view.getEndTime() + dragInterval * PER_MS_IN_PX > cutEndTime) {
                    view.setEndTime(cutEndTime);
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
                mProgressListener.timelineChange(view.getStartTime(), view.getEndTime(), String.valueOf(view.getIdentityID()),false);
                mProgressListener.trackPause();
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
        switch (event.getAction()){
            case  MotionEvent.ACTION_UP:
                for (int i = 0; i < mTemplateMaterialItemViews.size(); i++) {
                    if (mTemplateMaterialItemViews.get(i) != null) {
                        TemplateMaterialItemView itemView = mTemplateMaterialItemViews.get(i);
                        itemView.isLongClickModule = false;
                    }
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void leftSubtitleTouch(boolean isDirection, float dragInterval, int position) {
        dragScrollView = false;
        if (mDragSubtitleItemViews != null && mDragSubtitleItemViews.size() > 0) {
            DragSubtitleItemView materialItemView = mDragSubtitleItemViews.get(position);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) materialItemView.getLayoutParams();
            if (position == materialItemView.getIdentityID()) {
                //左拖动
                if (isDirection) {
                    //字幕的列表数大于1
                    if (subtitles.size() > 1) {
                        if (position <= subtitles.size() - 1) {
                            //移动的选取时间小于文件开始时间 直接结束方法
                            if ((long) (subtitles.get(position).getStartTime() - (PER_MS_IN_PX * dragInterval)) < 0) {
                                return;
                            } else {
                                if (position > 0) {
                                    long st = (long) (subtitles.get(position).getStartTime() - (PER_MS_IN_PX * dragInterval));
                                    //当前操作的字幕开始时间必须大于等于上一句的结束时间
                                    if (st >= subtitles.get(position - 1).getEndTime()) {
                                        subtitles.get(position).setStartTime(st);
                                    } else {
                                        return;
                                    }
                                } else {
                                    //并且操作的是第一句字幕
                                    subtitles.get(position).setStartTime((long) (subtitles.get(position).getStartTime() - (PER_MS_IN_PX * dragInterval)));
                                }
                            }
                        }
                    } else {
                        //只有一句字幕 选取的开始时间小于文件的开始时间
                        if ((long) (subtitles.get(position).getStartTime() - (PER_MS_IN_PX * dragInterval)) < 0) {
                            return;
                        } else {
                            subtitles.get(position).setStartTime((long) (subtitles.get(position).getStartTime() - (PER_MS_IN_PX * dragInterval)));
                        }
                    }
                } else {
                    //右拖动
                    //字幕的列表数大于1
                    if (subtitles.size() > 1) {
                        if (position <= subtitles.size() - 1) {
                            //移动的选取时间大于文件时长 直接结束方法
                            if ((long) (subtitles.get(position).getStartTime() + (PER_MS_IN_PX * dragInterval)) > mDuration) {
                                return;
                            } else {
                                //除去最后一句字幕的其他行字幕
                                if (position < subtitles.size() - 1) {
                                    long st = (long) (subtitles.get(position).getStartTime() + (PER_MS_IN_PX * dragInterval));
                                    //选取的时候大于下一句的开始时间
                                    if (st > subtitles.get(position + 1).getStartTime()) {
                                        return;
                                    } else {
                                        if (subtitles.get(position).getEndTime() - st < 500) {
                                            return;
                                        } else {
                                            subtitles.get(position).setStartTime(st);
                                        }
                                    }
                                } else {
                                    long st = (long) (subtitles.get(position).getStartTime() + (PER_MS_IN_PX * dragInterval));
                                    if (st > mDuration) {
                                        return;
                                    } else {
                                        //最后一行字幕的选取时间修改
                                        if (subtitles.get(position).getEndTime() - st < 500) {
                                            return;
                                        } else {
                                            subtitles.get(position).setStartTime(st);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        //只有一句字幕 选取的开始时间大于文件的结束时间
                        if ((long) (subtitles.get(position).getStartTime() + (PER_MS_IN_PX * dragInterval)) > mDuration) {
                            return;
                        } else {
                            long st = (long) (subtitles.get(position).getStartTime() + (PER_MS_IN_PX * dragInterval));
                            //最小的字幕时长为500毫秒
                            if (subtitles.get(position).getEndTime() - st < 500) {
                                return;
                            } else {
                                subtitles.get(position).setStartTime(st);
                            }
                        }
                    }
                }
            }
            materialItemView.setWidthAndHeight((int) ((subtitles.get(position).getEndTime() - subtitles.get(position).getStartTime()) / PER_MS_IN_PX), frameContainerHeight);
            params.setMargins((int) (subtitles.get(position).getStartTime() / PER_MS_IN_PX), 0, 0, 0);
            materialItemView.setLayoutParams(params);
            if (mProgressListener != null) {
                mProgressListener.timelineChange(materialItemView.getStartTime(), materialItemView.getEndTime(), String.valueOf(materialItemView.getIdentityID()),true);
                mProgressListener.trackPause();
            }
        }
    }

    @Override
    public void rightSubtitleTouch(boolean isDirection, float dragInterval, int position) {
        dragScrollView = false;
        if (mDragSubtitleItemViews != null && mDragSubtitleItemViews.size() > 0) {
            DragSubtitleItemView materialItemView = mDragSubtitleItemViews.get(position);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) materialItemView.getLayoutParams();
            if (position == materialItemView.getIdentityID()) {
                //左拖动
                if (isDirection) {
                    //字幕的列表数大于1
                    if (subtitles.size() > 1) {
                        if (position <= subtitles.size() - 1) {
                            if ((long) (subtitles.get(position).getEndTime() - (PER_MS_IN_PX * dragInterval)) < 0) {
                                return;
                            } else {
                                long se = (long) (subtitles.get(position).getEndTime() - (PER_MS_IN_PX * dragInterval));
                                if (position > 0) {
                                    //当前操作的字幕结束时间必须大于等于上一句的结束时间
                                    if (se >= subtitles.get(position - 1).getEndTime()) {
                                        if (se - subtitles.get(position).getStartTime() < 500) {
                                            return;
                                        } else if (subtitles.get(position).getEndTime() - subtitles.get(position).getStartTime() < 1000) {
                                            return;
                                        } else {
                                            subtitles.get(position).setEndTime((long) (subtitles.get(position).getEndTime() - (PER_MS_IN_PX * dragInterval)));
                                        }
                                    } else {
                                        return;
                                    }
                                } else {
                                    //并且操作的是第一句字幕
                                    if (se - subtitles.get(position).getStartTime() < 500) {
                                        return;
                                    } else {
                                        subtitles.get(position).setEndTime((long) (subtitles.get(position).getEndTime() - (PER_MS_IN_PX * dragInterval)));
                                    }
                                }
                            }
                        }
                    } else {
                        long se = (long) (subtitles.get(position).getEndTime() - (PER_MS_IN_PX * dragInterval));
                        //只有一句字幕 选取的结束时间小于文件的开始时间
                        if (se < 0) {
                            return;
                        } else {
                            if (se - subtitles.get(position).getStartTime() < 500) {
                                return;
                            } else {
                                subtitles.get(position).setEndTime(se);
                            }
                        }
                    }
                } else {
                    //右拖动
                    //字幕的列表数大于1
                    if (subtitles.size() > 1) {
                        if (position <= subtitles.size() - 1) {
                            //移动的选取时间大于文件时长 直接结束方法
                            if ((long) (subtitles.get(position).getEndTime() + (PER_MS_IN_PX * dragInterval)) > mDuration) {
                                return;
                            } else {
                                //除去最后一句字幕的其他行字幕
                                long se = (long) (subtitles.get(position).getEndTime() + (PER_MS_IN_PX * dragInterval));
                                if (position < subtitles.size() - 1) {
                                    //选取的时间大于下一句的开始时间
                                    if (se > subtitles.get(position + 1).getStartTime()) {
                                        return;
                                    } else {
                                        subtitles.get(position).setEndTime(se);
                                    }
                                } else {
                                    if (se > mDuration) {
                                        return;
                                    } else {
                                        //最后一行字幕的选取时间修改
                                        subtitles.get(position).setEndTime(se);
                                    }
                                }
                            }
                        }
                    } else {
                        //只有一句字幕 选取的结束时间大于文件的结束时间
                        if ((long) (subtitles.get(position).getEndTime() + (PER_MS_IN_PX * dragInterval)) > mDuration) {
                            return;
                        } else {
                            subtitles.get(position).setEndTime((long) (subtitles.get(position).getEndTime() + (PER_MS_IN_PX * dragInterval)));
                        }
                    }
                }
            }
            materialItemView.setWidthAndHeight((int) ((subtitles.get(position).getEndTime() - subtitles.get(position).getStartTime()) / PER_MS_IN_PX), frameContainerHeight);
            params.setMargins((int) (subtitles.get(position).getStartTime() / PER_MS_IN_PX), 0, 0, 0);
            materialItemView.setLayoutParams(params);
            if (mProgressListener != null) {
                mProgressListener.timelineChange(materialItemView.getStartTime(), materialItemView.getEndTime(), String.valueOf(materialItemView.getIdentityID()),true);
                mProgressListener.trackPause();
            }
        }
    }

    @Override
    public void touchSubtitleView(DragSubtitleItemView view, boolean isDirection, float dragInterval, int position) {
        dragScrollView = false;
        for (int i = 0; i < subtitles.size(); i++) {
            DragSubtitleItemView dragSubtitleView = mDragSubtitleItemViews.get(i);
            if (view.getIdentityID() == dragSubtitleView.getIdentityID()) {
                dragSubtitleView.isShowArrow(true);
                dragSubtitleView.bringToFront();
            } else {
                dragSubtitleView.isShowArrow(false);
            }
        }
        RelativeLayout.LayoutParams params = (LayoutParams) view.getLayoutParams();
        if (position == view.getIdentityID()) {
            //整体往左移动
            if (isDirection) {
                if (position == 0) {
                    if (subtitles.get(position).getStartTime() - (dragInterval * PER_MS_IN_PX) < 0) {
                        return;
                    } else {
                        subtitles.get(position).setStartTime((long) (subtitles.get(position).getStartTime() - (dragInterval * PER_MS_IN_PX)));
                        subtitles.get(position).setEndTime((long) (subtitles.get(position).getEndTime() - (dragInterval * PER_MS_IN_PX)));
                    }
                } else {
                    if (subtitles.get(position).getStartTime() - (dragInterval * PER_MS_IN_PX) < subtitles.get(position - 1).getEndTime()) {
                        return;
                    } else {
                        subtitles.get(position).setStartTime((long) (subtitles.get(position).getStartTime() - (dragInterval * PER_MS_IN_PX)));
                        subtitles.get(position).setEndTime((long) (subtitles.get(position).getEndTime() - (dragInterval * PER_MS_IN_PX)));
                    }
                }
            } else {
                //整体往右移动
                if (position == subtitles.size() - 1) {
                    if (subtitles.get(position).getEndTime() + (dragInterval * PER_MS_IN_PX) > mDuration) {
                        return;
                    } else {
                        subtitles.get(position).setStartTime((long) (subtitles.get(position).getStartTime() + (dragInterval * PER_MS_IN_PX)));
                        subtitles.get(position).setEndTime((long) (subtitles.get(position).getEndTime() + (dragInterval * PER_MS_IN_PX)));
                    }
                } else {
                    if (subtitles.get(position).getEndTime() + (dragInterval * PER_MS_IN_PX) > subtitles.get(position + 1).getStartTime()) {
                        return;
                    } else {
                        subtitles.get(position).setStartTime((long) (subtitles.get(position).getStartTime() + (dragInterval * PER_MS_IN_PX)));
                        subtitles.get(position).setEndTime((long) (subtitles.get(position).getEndTime() + (dragInterval * PER_MS_IN_PX)));
                    }
                }
            }
            if (mProgressListener != null) {
                mProgressListener.timelineChange(view.getStartTime(), view.getEndTime(), String.valueOf(view.getIdentityID()),true);
                mProgressListener.trackPause();
            }
        }
        params.setMargins((int) (subtitles.get(position).getStartTime() / PER_MS_IN_PX), 0, 0, 0);
        view.setLayoutParams(params);
    }

    @Override
    public void onClickSubtitleView(DragSubtitleItemView view) {
        if (mDragSubtitleItemViews != null && mDragSubtitleItemViews.size() > 0) {
            for (int i = 0; i < mDragSubtitleItemViews.size(); i++) {
                DragSubtitleItemView dragSubtitleView = mDragSubtitleItemViews.get(i);
                if (view.getIdentityID() == dragSubtitleView.getIdentityID()) {
                    dragSubtitleView.isShowArrow(true);
                    dragSubtitleView.bringToFront();
                    dragScrollView = true;
                    scrollToPosition(subtitles.get(i).getStartTime());
                } else {
                    dragSubtitleView.isShowArrow(false);
                }
            }
            dragScrollView = false;
//            if (progresListener != null) {
//                progresListener.touchDragSubtitleView();
//            }
        }
    }

    public interface SeekBarProgressListener{

        void progress(long progress,boolean manualDrag);

        void manualDrag(boolean manualDrag);

        void timelineChange(long startTime,long endTime,String id,boolean isSubtitle);

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
