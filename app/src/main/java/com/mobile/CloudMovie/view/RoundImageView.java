package com.mobile.CloudMovie.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;


/**
 * Created by leo 圆形2
 * on 17/3/14.
 */
//com.mobile.myzx.view.RoundImageView
public class RoundImageView extends ImageView {
    private Paint paint;

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint();
    }

    /**
     * 绘制圆角矩形图片
     *
     * @author caizhiming
     */
    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (null != drawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Bitmap b = getRoundBitmap(bitmap, 20);
            final Rect rectSrc = new Rect(0, 0, b.getWidth(), b.getHeight());
            final Rect rectDest = new Rect(0, 0, getWidth(), getHeight());
            paint.reset();
            canvas.drawBitmap(b, rectSrc, rectDest, paint);
            paint.setColor(Color.parseColor("#33000000"));       //设置画笔颜色
            paint.setStyle(Paint.Style.FILL);  //设置画笔模式为填充
            RectF oval3 = new RectF(rectDest);// 设置个新的长方形
            canvas.drawRoundRect(oval3, 20, 20, paint);//第二个参数是x半径，第三个参数是y半径
        } else {
            super.onDraw(canvas);
        }


    }

    /**
     * 获取圆角矩形图片方法
     *
     * @param bitmap
     * @param roundPx,圆角的弧度
     * @return Bitmap
     * @author caizhiming
     */
    private Bitmap getRoundBitmap(Bitmap bitmap, int roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        int x = bitmap.getWidth();

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);


//        paint.setColor(Color.parseColor("#33000000"));       //设置画笔颜色
//        paint.setStyle(Paint.Style.FILL);  //设置画笔模式为填充
//        paint.setStrokeWidth(10f);
//        paint.setAlpha(10);
//        canvas.drawRoundRect(rectF, roundPx, roundPx,paint);//第二个参数是x半径，第三个参数是y半径

        return output;


    }
}