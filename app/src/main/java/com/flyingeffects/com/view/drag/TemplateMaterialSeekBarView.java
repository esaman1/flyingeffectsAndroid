package com.flyingeffects.com.view.drag;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.flyingeffects.com.R;
import com.flyingeffects.com.utils.screenUtil;

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
    List<TemplateMaterialItemView> mTemplateMaterialItemViews = new ArrayList<>();

    /**
     * 左右边距
     */
    int frameListPadding;
    int frameContainerWidth;
    int frameContainerHeight;
    long mDuration;
    boolean isSetFrameWidth = false;
    public boolean dragScrollView;
    long cutStartTime;
    long cutEndTime;


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
                long process = l * PER_MS_IN_PX;
                for (int i = 0; mTemplateMaterialItemViews != null && i < mTemplateMaterialItemViews.size(); i++) {
                    TemplateMaterialItemView materialItemView = mTemplateMaterialItemViews.get(i);
                    if (process >= mTemplateMaterialItemViews.get(i).getStartTime() && process < mTemplateMaterialItemViews.get(i).getEndTime() &&
                            i == materialItemView.getIdentityID()) {
                        materialItemView.isShowArrow(true);
                    } else {
                        materialItemView.isShowArrow(false);
                    }
                }
                if(mProgressListener!=null){
                    mProgressListener.progress(process,dragScrollView);
                }
            }

            @Override
            public void onTouchStart() {

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

    /***
     * 预览视频时长裁剪 素材的起止时间也随着裁剪或偏移
     * @param startTime  开始时间
     * @param endTime   结束时间
     */
    public void setCutStartAndEndTime(long startTime, long endTime) {
        cutStartTime = startTime;
        cutEndTime = endTime;
        for (int i = 0; i < mTemplateMaterialItemViews.size(); i++) {
            TemplateMaterialItemView itemView = mTemplateMaterialItemViews.get(i);
            if (itemView.getStartTime() > cutStartTime && itemView.getEndTime() < cutEndTime) {
                continue;
            }
            if (itemView.getStartTime() < cutStartTime && itemView.getEndTime() > cutEndTime) {
                itemView.setStartTime(cutStartTime);
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
            if (itemView.getEndTime() > cutEndTime) {
                itemView.setEndTime(cutEndTime);
            }
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) itemView.getLayoutParams();
            itemView.setWidthAndHeight((int) ((itemView.getEndTime() - itemView.getStartTime()) / PER_MS_IN_PX), frameContainerHeight);
            params.setMargins((int) (itemView.getStartTime() / PER_MS_IN_PX + frameListPadding - TemplateMaterialItemView.ARROW_WIDTH),
                    screenUtil.dip2px(getContext(), 5), 0, 0);
            itemView.setLayoutParams(params);
        }
    }

    public void scrollToPosition(long process) {
        mMaterialSeekBar.scrollTo((int) Math.ceil(process / (PER_MS_IN_PX * 1f)), 0);
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

    public void addTemplateMaterialItemView(long duration, String resPath,long startTime,long endTime) {
        this.mDuration = duration;
        this.cutStartTime =0;
        this.cutEndTime = duration;
        TemplateMaterialItemView materialItemView = new TemplateMaterialItemView(getContext());
        mTemplateMaterialItemViews.add(materialItemView);
        materialItemView.setIdentityID(mTemplateMaterialItemViews.size() - 1);

        int thumbnailTotalWidth = materialItemView.setResPathAndDuration(resPath, duration, frameContainerHeight);
        if (!isSetFrameWidth) {
            isSetFrameWidth = true;
            RelativeLayout.LayoutParams reParams = (LayoutParams) mViewFrame.getLayoutParams();
            reParams.width = thumbnailTotalWidth + frameListPadding * 2;
            mViewFrame.setLayoutParams(reParams);
        }

        materialItemView.setStartTime(startTime);
        materialItemView.setEndTime(endTime);
        materialItemView.isShowArrow(false);

        materialItemView.setWidthAndHeight((int) ((endTime - startTime) / PER_MS_IN_PX), frameContainerHeight);
        mLlDragItem.addView(materialItemView);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) materialItemView.getLayoutParams();
        long intervalPX = frameListPadding - TemplateMaterialItemView.ARROW_WIDTH;
        //设置左的Margins为屏幕宽度的一半减去箭头的宽度
        params.setMargins((int) (intervalPX + startTime / PER_MS_IN_PX), screenUtil.dip2px(getContext(), 5), 0, 0);
        materialItemView.setLayoutParams(params);
        materialItemView.setDragListener(this);
    }

    @Override
    public void leftTouch(boolean isDirection, float dragInterval, int position) {
        if (mTemplateMaterialItemViews != null && mTemplateMaterialItemViews.size() > 0) {
            TemplateMaterialItemView materialItemView = mTemplateMaterialItemViews.get(position);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) materialItemView.getLayoutParams();
            if (position == materialItemView.getIdentityID()) {
                //左拖动
                if (isDirection) {
                    if (materialItemView.getStartTime() <= cutStartTime) {
                        return;
                    } else {
                        materialItemView.setStartTime((long) (materialItemView.getStartTime() - PER_MS_IN_PX * dragInterval));
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
                    if (et > cutEndTime) {
                        return;
                    } else {
                        materialItemView.setEndTime(et);
                    }
                }
            }
            materialItemView.setWidthAndHeight((int) ((materialItemView.getEndTime() - materialItemView.getStartTime()) / PER_MS_IN_PX), frameContainerHeight);
            params.setMargins((int) (materialItemView.getStartTime() / PER_MS_IN_PX + frameListPadding - TemplateMaterialItemView.ARROW_WIDTH),
                    screenUtil.dip2px(getContext(), 5), 0, 0);
            materialItemView.setLayoutParams(params);
        }
    }

    @Override
    public void touchTextView(TemplateMaterialItemView view, boolean isDirection, float dragInterval, int position) {
        dragScrollView = false;
        for (int i = 0; i < mTemplateMaterialItemViews.size(); i++) {
            TemplateMaterialItemView dragSubtitleView = mTemplateMaterialItemViews.get(i);
            if (view.getIdentityID() == dragSubtitleView.getIdentityID()) {
                dragSubtitleView.isShowArrow(true);
            } else {
                dragSubtitleView.isShowArrow(false);
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
        }
    }

    @Override
    public void onClickTextView(TemplateMaterialItemView view) {
        //TODO 该方法暂时不使用   点击某个时间轴  进度条跳转到指定位置
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == mMaterialSeekBar) {
            dragScrollView = true;
        }
        if (v == mLlDragItem) {
            for (int i = 0; i < mTemplateMaterialItemViews.size(); i++) {
                TemplateMaterialItemView materialItemView = mTemplateMaterialItemViews.get(i);
                materialItemView.requestDisallowInterceptTouchEvent(false);
            }
        }
        return false;
    }

    public interface SeekBarProgressListener{

        void progress(long progress,boolean manualDrag);

        void manualDrag(boolean manualDrag);
    }

    SeekBarProgressListener mProgressListener;

    public void setProgressListener(SeekBarProgressListener progressListener) {
        mProgressListener = progressListener;
    }
}
