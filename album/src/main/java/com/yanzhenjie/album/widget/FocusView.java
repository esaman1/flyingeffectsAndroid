package com.yanzhenjie.album.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.animation.Animation;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * 对焦的ImageView
 * @author sjq
 */
public class FocusView extends AppCompatImageView {

    private static final int NO_ID = -1;
    private int mFocusImg = NO_ID;
    private int mFocusSucceedImg = NO_ID;
    private int mFocusFailedImg = NO_ID;
    private Animation mAnimation;
    private Handler mHandler;

    public FocusView(Context context) {
        super(context);
    }

    public FocusView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
