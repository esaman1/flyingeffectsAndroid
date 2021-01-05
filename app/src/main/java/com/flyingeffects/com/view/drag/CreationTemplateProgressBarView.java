package com.flyingeffects.com.view.drag;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.flyingeffects.com.R;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.utils.screenUtil;

/**
 * @author ZhouGang
 * @date 2020/11/4
 * 自定义模板的进度条
 */
public class CreationTemplateProgressBarView extends RelativeLayout implements TemplateMaterialItemView.TouchDragListener,
        ObserveHorizontalScrollView.OnTouchListener{
    /**每一像素所占的时长为15毫秒*/
    public static final long PER_MS_IN_PX = 15;

    ObserveHorizontalScrollView mCreationTemplateProgressBar;
    LinearLayout mLlDragItem;
    TemplateMaterialItemView materialItemView;

    /**
     * 左右边距
     */
    int frameListPadding;
    int frameContainerWidth;
    int frameContainerHeight;

    /**视频原始时长*/
    long originalDuration;

    long startTime = 0;
    long endTime = 0;

    public CreationTemplateProgressBarView(Context context) {
        super(context);
        initView();
    }

    public CreationTemplateProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CreationTemplateProgressBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    boolean isDrag = false;
    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_creation_template_progress_bar, null);
        mCreationTemplateProgressBar = view.findViewById(R.id.creation_template_progressbar);
        mLlDragItem = view.findViewById(R.id.ll_item_drag);
        addView(view);
        mCreationTemplateProgressBar.setOnScrollChangeListener(new ObserveHorizontalScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt, boolean onDragChanged) {
                if (l * PER_MS_IN_PX < startTime) {
                    scrollToPosition(startTime);
                }
                if (l * PER_MS_IN_PX > endTime) {
                    scrollToPosition(endTime);
                }
                if (mProgressListener != null) {
                    mProgressListener.progress(l * PER_MS_IN_PX,isDrag);
                }
            }

            @Override
            public void onTouchEnd() {
                isDrag = false;
                if (mProgressListener != null) {
                    mProgressListener.onTouchEnd();
                }
            }
        });
        mCreationTemplateProgressBar.setOnTouchListener(this);
        frameContainerHeight = screenUtil.dip2px(getContext(), 40);
    }

    /***
     * 添加一个模板进度条
     * @param duration 时长
     * @param resPath 资源路径
     */
    String resPath;
    public void addProgressBarView(long duration, String resPath) {
        mLlDragItem.removeAllViews();
        this.originalDuration = duration;
        this.startTime = 0;
        this.endTime = duration;
        this.resPath = resPath;
        materialItemView = new TemplateMaterialItemView(getContext());
        materialItemView.setIdentityID(0);
        materialItemView.isShowArrow(false);
        materialItemView.setResPathAndDuration(resPath,duration,frameContainerHeight,false,"");
        materialItemView.setWidthAndHeight((int) (duration / PER_MS_IN_PX), frameContainerHeight);
        materialItemView.isNeedOverallDrag(false);

        mLlDragItem.addView(materialItemView);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) materialItemView.getLayoutParams();
        long intervalPX = frameListPadding - screenUtil.dip2px(getContext(), 43) - TemplateMaterialItemView.ARROW_WIDTH;
        //设置左的Margins为屏幕宽度的一半减去箭头的宽度
        params.setMargins((int) (intervalPX), 0, frameListPadding - TemplateMaterialItemView.ARROW_WIDTH, 0);
        materialItemView.setLayoutParams(params);
        materialItemView.setDragListener(this);
    }

    public void scrollToPosition(long process) {
        mCreationTemplateProgressBar.scrollTo((int) Math.ceil(process / (PER_MS_IN_PX * 1f)), 0);
    }

    public void hindArrow() {
        if (materialItemView != null) {
            materialItemView.isShowArrow(false);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        frameContainerWidth = measureDimension(widthMeasureSpec);
        frameListPadding = (frameContainerWidth + screenUtil.dip2px(getContext(), 43)) / 2;
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == mLlDragItem) {
            materialItemView.requestDisallowInterceptTouchEvent(false);
        }
        return false;
    }

    @Override
    public void leftTouch(boolean isDirection, float dragInterval, int position) {
        if (materialItemView != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) materialItemView.getLayoutParams();
            if (isDirection) {
                if (startTime - dragInterval * PER_MS_IN_PX < 0) {
                    startTime = 0;
                    return;
                } else {
                    startTime = (long) (startTime - dragInterval * PER_MS_IN_PX);
                }
            } else {
                if (endTime - (startTime + dragInterval * PER_MS_IN_PX) <= 3000) {
                    return;
                } else {
                    startTime = (long) (startTime + dragInterval * PER_MS_IN_PX);
                }
            }
            materialItemView.setWidthAndHeight((int) ((endTime - startTime) / PER_MS_IN_PX), frameContainerHeight);
            params.setMargins((int) (startTime / PER_MS_IN_PX + frameListPadding - screenUtil.dip2px(getContext(), 43) -
                    TemplateMaterialItemView.ARROW_WIDTH), 0, frameListPadding - TemplateMaterialItemView.ARROW_WIDTH, 0);
            materialItemView.setLayoutParams(params);
        }
    }

    @Override
    public void rightTouch(boolean isDirection, float dragInterval, int position) {
        if (materialItemView != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) materialItemView.getLayoutParams();
            if (isDirection) {
                if ((endTime - dragInterval * PER_MS_IN_PX) - startTime < 3000) {
                    return;
                } else {
                    endTime = (long) (endTime - dragInterval * PER_MS_IN_PX);
                }
            } else {
                if (endTime + dragInterval * PER_MS_IN_PX > originalDuration) {
                    endTime = originalDuration;
                    return;
                } else {
                    endTime = (long) (endTime+ dragInterval * PER_MS_IN_PX);
                }
            }
            materialItemView.setWidthAndHeight((int) ((endTime - startTime) / PER_MS_IN_PX), frameContainerHeight);
            params.setMargins((int) (startTime / PER_MS_IN_PX + frameListPadding - screenUtil.dip2px(getContext(), 43) -
                            TemplateMaterialItemView.ARROW_WIDTH), 0,
                    (int) ((originalDuration - endTime) / PER_MS_IN_PX + frameListPadding - TemplateMaterialItemView.ARROW_WIDTH), 0);
            materialItemView.setLayoutParams(params);

        }
    }

    @Override
    public void onTouchEnd(TemplateMaterialItemView view, boolean isDirection){
        scrollToPosition(isDirection ? startTime : endTime);
        isDrag = true;
//        scrollToPosition(startTime);
        if (mProgressListener != null) {
            mProgressListener.cutInterval(startTime, endTime,isDirection);
        }
    }

    @Override
    public void editStatistics(TemplateMaterialItemView view, boolean isOverallMove) {
        if (!isOverallMove) {
            if (TextUtils.isEmpty(resPath)) {
                statisticsEventAffair.getInstance().setFlag(getContext(), "21_bj_gd_clip");
            } else {
                statisticsEventAffair.getInstance().setFlag(getContext(), "21_mb_gd_clip");
            }
        }
    }

    @Override
    public void touchTextView(TemplateMaterialItemView view, boolean isDirection, float dragInterval, int position) {
       //TODO 该类不使用此方法
        isDrag = true;
    }

    @Override
    public void onClickTextView(TemplateMaterialItemView view) {
       //点击后显示拖动的箭头
        if (materialItemView != null) {
            materialItemView.isShowArrow(true);
        }
    }

    public interface SeekBarProgressListener {

        void progress(long progress,boolean isDrag);

        void cutInterval(long starTime, long endTime, boolean isDirection);

        void onTouchEnd();
    }

    SeekBarProgressListener mProgressListener;

    public void setProgressListener(SeekBarProgressListener progressListener) {
        mProgressListener = progressListener;
    }
}
