package com.shixing.sxve.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.shixing.sxve.R;

public class VideoClipLayout extends FrameLayout {

    private SXVideoView mVideoView;
    private int mTemplateWidth;
    private int mTemplateHeight;

    public VideoClipLayout(Context context) {
        super(context);
        init(context);
    }

    public VideoClipLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoClipLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.sxve_sx_vv, this, true);
        mVideoView = findViewById(R.id.video_view);
    }

    public SXVideoView getVideoView() {
        return mVideoView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mTemplateWidth > 0 && mTemplateHeight > 0) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);

            if (mTemplateHeight > height || mTemplateWidth > width) {
                if (mTemplateWidth * height > width * mTemplateHeight) {
                    height = width * mTemplateHeight / mTemplateWidth;
                } else if (mTemplateWidth * height < width * mTemplateHeight) {
                    width = height * mTemplateWidth / mTemplateHeight;
                }
            } else {
                width = mTemplateWidth;
                height = mTemplateHeight;
            }

            int widthMeasure = MeasureSpec.makeMeasureSpec(width, MeasureSpec.getMode(widthMeasureSpec));
            int heightMeasure = MeasureSpec.makeMeasureSpec(height, MeasureSpec.getMode(heightMeasureSpec));
            super.onMeasure(widthMeasure, heightMeasure);
        } else {
            //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(0, 0);
        }
    }

    public void setTemplateWidthAndHeight(int templateWidth, int templateHeight) {
        mTemplateWidth = templateWidth;
        mTemplateHeight = templateHeight;
        mVideoView.setTemplateWidthAndHeight(templateWidth, templateHeight);
    }
}
