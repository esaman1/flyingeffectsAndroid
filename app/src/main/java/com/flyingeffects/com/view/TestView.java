package com.flyingeffects.com.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.bigkoo.convenientbanner.utils.ScreenUtil;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.utils.BitmapUtil;

import java.util.ArrayList;


public class TestView extends View {
    private int measureWidth = ScreenUtil.getScreenWidth(BaseApplication.getInstance());
    Paint paint = new Paint();
    Paint paintShadow = new Paint();
    Paint paintHighlight = new Paint();
    private int defaultHeight = dp2Px(380);
    private String text = "发财啊";
    private int textLengh;
    private String[]strData;
    private int paddingTop;
    private int paddingLeft;
    private int paddingBottom;
    private int paddingRight;
    private int textSize = 380;
    private float paintWidth = 50;
    private float paint3Width = 40;
    //高光
    int[] colors3 = {Color.parseColor("#00000000"), Color.parseColor("#ffffff"), Color.parseColor("#00000000"), Color.parseColor("#EEEEEE"), Color.parseColor("#ffffff"), Color.parseColor("#00000000"), Color.parseColor("#ffffff")};
    private Context context;

    public TestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        spiltText();
        initPaint();

    }


    public TestView(Context context) {
        super(context);
        this.context = context;
        spiltText();
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = 0;
        int height = 0;
        if (widthMode == MeasureSpec.EXACTLY) {
            measureWidth = width = widthSize;
        } else {
            width = getAndroiodScreenProperty().get(0);
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            defaultHeight = height = heightSize;
        } else {
            height = defaultHeight;
        }
        setMeasuredDimension(width, height);
        paddingTop = getPaddingTop();
        paddingLeft = getPaddingLeft();
        paddingBottom = getPaddingBottom();
        paddingRight = getPaddingRight();
    }

    private ArrayList<Integer> getAndroiodScreenProperty() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(screenWidth);
        integers.add(screenHeight);
        return integers;
    }


    private void initPaint() {
        paint.setColor(Color.parseColor("#000000"));
        paint.setTextSize(textSize);
        paint.setStrokeWidth(paintWidth);

        paintShadow.setColor(Color.parseColor("#000000"));
        paintShadow.setTextSize(textSize);
        paintShadow.setStrokeWidth(paintWidth);

        Bitmap bp = BitmapFactory.decodeResource(context.getResources(), R.mipmap.test_png);
        BitmapShader bitmapShader = new BitmapShader(BitmapUtil.GetBitmapForScale(bp, measureWidth / 2, defaultHeight / 3), Shader.TileMode.MIRROR, Shader.TileMode.MIRROR);
        paint.setShader(bitmapShader);
        paintShadow.setShader(bitmapShader);
        Typeface typeface = Typeface.createFromAsset(BaseApplication.getInstance().getAssets(), "ktjt.ttf");
        paint.setTypeface(typeface);
        Typeface typeface3 = Typeface.createFromAsset(BaseApplication.getInstance().getAssets(), "ktjt.ttf");
        Typeface typeface2 = Typeface.createFromAsset(BaseApplication.getInstance().getAssets(), "ktjt.ttf");
        paintShadow.setTypeface(typeface2);
        paintHighlight.setColor(Color.parseColor("#000000"));
        paintHighlight.setTextSize(textSize);
        paintHighlight.setStrokeWidth(paint3Width);
        RadialGradient radialGradient4 = new RadialGradient(measureWidth / (float) 4, defaultHeight / (float) 2, measureWidth / (float) 2, colors3, null, Shader.TileMode.CLAMP);
        paintHighlight.setShader(radialGradient4);
        paintHighlight.setAntiAlias(true);
        paintHighlight.setTypeface(typeface3);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        float textWidth = paint.measureText(text);
//        for (int i = 1; i < 15; i++) {
//            canvas.drawText(text, measureWidth / (float) 2 - (i * 2) - textWidth / (float) 2, defaultHeight / (float) 2 - 10 + i, paintShadow);
//        }
//        canvas.drawText(text, measureWidth / (float) 2 - textWidth / (float) 2 - 10, defaultHeight / (float) 2 - 10, paintHighlight);
//        canvas.drawText(text, measureWidth / (float) 2 - textWidth / (float) 2, defaultHeight / (float) 2 - 10, paint);

        float singleTextWidth = paint.measureText(strData[0]);
        for(int i=0;i<strData.length;i++){
            drawSingleText( canvas,strData[i], singleTextWidth+singleTextWidth*i,singleTextWidth/(float)2);
        }
    }



    private void drawSingleText(Canvas canvas,String str,float singleTextWidth,float halfTextWidth){
        for (int i = 1; i < 15; i++) {
            canvas.drawText(str, singleTextWidth+halfTextWidth - (i * 2) - singleTextWidth / (float) 2, defaultHeight / (float) 2 - 10 + i, paintShadow);
        }
        canvas.drawText(str, singleTextWidth+halfTextWidth- singleTextWidth / (float) 2 - 10, defaultHeight / (float) 2 - 10, paintHighlight);
        canvas.drawText(str, singleTextWidth+halfTextWidth- singleTextWidth / (float) 2, defaultHeight / (float) 2 - 10, paint);


    }

    private int dp2Px(float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private int sp2Px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }




    /**
     * description ：获得文字的宽度
     * creation date: 2020/9/17
     * user : zhangtongju
     */
    private int getTextWidth(String str, Paint paint) {
        Rect rect = new Rect(); // 文字所在区域的矩形
        paint.getTextBounds(str, 0, str.length(), rect);
        return rect.width();
    }


    private void spiltText(){
        textLengh=text.length();
        strData=new String[textLengh];
        for(int i=0;i<textLengh;i++){
            strData[i]= String.valueOf(text.charAt(i));
        }

    }




}
