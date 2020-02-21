package com.shixing.sxve.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.shixing.sxve.ui.model.GroupModel;
import com.shixing.sxve.ui.util.RotationGestureDetector;
import com.shixing.sxve.ui.util.Size;

import java.lang.reflect.Field;

public class TemplateView extends View {
    //    private static final String TAG = "TemplateView";
    private float mMidPntX;
    private float mMidPntY;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleDetector;
    private RotationGestureDetector mRotationDetector;
    private GroupModel mGroup;
    private float mOverallScale;
    int viewW;
    int viewH;
    private Paint paint;
    private boolean isSliding = false;
    private isTemplateSlide templateSlide;

    public TemplateView(Context context) {
        super(context);
        init();

    }


    public TemplateView(Context context, isTemplateSlide templateSlide) {
        super(context);
        this.templateSlide = templateSlide;
        init();

    }

    public TemplateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TemplateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint();

        paint.setStyle(Paint.Style.STROKE);//空心矩形框
        paint.setColor(Color.WHITE);
        setupGestureListeners();
    }

    private void setupGestureListeners() {
        mGestureDetector = new GestureDetector(getContext(), new GestureListener());
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        mRotationDetector = new RotationGestureDetector(new RotationListener());

        try {
            Field minSpan = mScaleDetector.getClass().getDeclaredField("mMinSpan");
            minSpan.setAccessible(true);
            minSpan.set(mScaleDetector, 20);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == event.ACTION_UP) {
            isSliding = false;
            if (templateSlide != null) {
                templateSlide.isTemplateSlide();
            }
        }
        if (event.getPointerCount() > 1) {
            mMidPntX = (event.getX(0) + event.getX(1)) / 2 / mOverallScale;
            mMidPntY = (event.getY(0) + event.getY(1)) / 2 / mOverallScale;
        }
        mGestureDetector.onTouchEvent(event);
        mScaleDetector.onTouchEvent(event);
        mRotationDetector.onTouchEvent(event);
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            mGroup.allFingerUp();
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mGroup != null) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);

            Size size = mGroup.getSize();
            if (width * size.getHeight() < height * size.getWidth()) {
                height = width * size.getHeight() / size.getWidth();
                mOverallScale = (float) width / size.getWidth();
            } else {
                width = height * size.getWidth() / size.getHeight();
                mOverallScale = (float) height / size.getHeight();
            }
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

//    private boolean hasBackground=false;
//    private Bitmap bgBitmap;
//    private Matrix bgMatrix;
//    public void setCustomBg(Drawable resource){
//        if (resource==null){
//            hasBackground=false;
//            if (bgBitmap!=null){
//                GlideBitmapPool.putBitmap(bgBitmap);
//                bgBitmap=null;
//            }
//        }else {
//            hasBackground=true;
//            bgBitmap= drawableToBitmap(resource);
//        }
//        invalidate();
//    }


    private Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    RectF borderRect = new RectF(5, 5, viewW - 5, viewH - 5);

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            if (viewW == 0 || viewH == 0) {
                viewW = this.getWidth();
                viewH = this.getHeight();
                borderRect = new RectF(5, 5, viewW - 5, viewH - 5);
            }
            if (isSliding) {
                canvas.drawRect(borderRect, paint);
            }
            canvas.save();
            canvas.scale(mOverallScale, mOverallScale);
            mGroup.draw(canvas);
            canvas.restore();
        }
    }

    public void setAssetGroup(GroupModel group) {
        if (group != null) {
            mGroup = group;
            group.setTemplateTarget(this);
        }

    }


    public GroupModel getAssetGroup() {
        return mGroup;
    }


    public void isViewVisible(boolean isShow) {
        if(mGroup!=null){
            mGroup.isShow(isShow);
        }

    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            mGroup.singleTap(new PointF(e.getX() / mOverallScale, e.getY() / mOverallScale));
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            isSliding = false;


            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            isSliding = true;
            mGroup.down(new PointF(e.getX() / mOverallScale, e.getY() / mOverallScale));
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mGroup.scroll(distanceX / mOverallScale, distanceY / mOverallScale);
            return true;
        }


    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float deltaScale = detector.getScaleFactor();
            mGroup.scale(deltaScale, deltaScale, mMidPntX, mMidPntY);
            return true;
        }
    }

    private class RotationListener extends RotationGestureDetector.SimpleOnRotationGestureListener {
        @Override
        public boolean onRotation(RotationGestureDetector detector) {
            mGroup.rotate(detector.getAngle(), mMidPntX, mMidPntY);
            return true;
        }
    }


    public interface isTemplateSlide {

        void isTemplateSlide();
    }


}
