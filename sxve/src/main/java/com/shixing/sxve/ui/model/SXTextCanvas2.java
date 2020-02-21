//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.shixing.sxve.ui.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.Layout.Alignment;
import android.util.Log;

import com.shixing.sxve.ui.util.ColorUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SXTextCanvas2 {
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Rect mRect;
    private TextPaint mTextPaint;
    private TextPaint mStrokePaint;
    private String mContent;
    private Alignment mAlignment;
    private int mFontSize;
    private  int[] size;
    private static Map<String, String> sFontMap;
    private boolean nowTextCanvasIsVisible=false;

    public SXTextCanvas2() {
        this((String)null, 1);
    }

    public SXTextCanvas2(String json) {
        this(json, 1);
    }

    public SXTextCanvas2(String json, int uiMajorVersion) {
        this.mAlignment = Alignment.ALIGN_NORMAL;
        this.init();
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONObject asset = new JSONObject(json);
                size   = this.getIntArray(asset.getJSONArray("size"));
                if(nowTextCanvasIsVisible){
                    Log.d("OOM","创建了bitmap");
                    this.setCanvasSize(size[0], size[1]);
                }
                JSONObject object = asset.getJSONObject("ui");
                this.mContent = object.optString("default");
                String font = object.getString("font");
                if (sFontMap != null && sFontMap.size() > 0 && sFontMap.containsKey(font)) {
                    String path = (String)sFontMap.get(font);
                    this.setFont(path);
                } else {
                    this.setFont(font, 0);
                }

                int fontSize = object.getInt("size");
                this.setFontSize(fontSize);
                String fill = object.getString("fill");
                int fillColor = ColorUtils.parseRGBAColor(fill);
                this.setFillColor(fillColor);
                String stroke = object.getString("stroke");
                int strokeColor = ColorUtils.parseRGBAColor(stroke);
                this.setStrokeColor(strokeColor);
                int width = object.getInt("width");
                this.setStrokeWidth(width);
                int align = object.getInt("align");
                this.setAlignment(align);
                int[] editSize;
                if (uiMajorVersion > 1) {
                    editSize = this.getIntArray(object.getJSONArray("editSize"));
                    this.setTextArea(0, 0, editSize[0], editSize[1]);
                } else {
                    editSize = this.getIntArray(object.getJSONArray("area"));
                    this.setTextArea(editSize[0], editSize[1], editSize[2], editSize[3]);
                }
            } catch (JSONException var15) {
                var15.printStackTrace();
            }

        }
    }

    private void init() {
        this.mTextPaint = new TextPaint(1);
        this.mStrokePaint = new TextPaint(1);
        this.mStrokePaint.setStyle(Style.FILL_AND_STROKE);
        this.mRect = new Rect();
    }

    public void setTextArea(int x, int y, int width, int height) {
        this.mRect.set(x, y, x + width, y + height);
    }
    public void setTextRect(Rect rect){
        this.mRect=rect;
    }
    public Rect getTextArea(){
        return this.mRect;
    }

    public void setCanvasSize(int width, int height) {
        try {
            this.mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
//            if(mCanvas==null){
                this.mCanvas = new Canvas(this.mBitmap);
//            }
        } catch (OutOfMemoryError e) {
            Log.d("SXTextCanvas2","内存溢出");
        }
    }

    public void setFontSize(int fontSize) {
        this.mFontSize = fontSize;
        this.mTextPaint.setTextSize((float)fontSize);
        this.mStrokePaint.setTextSize((float)fontSize);
    }

    public void setFont(String path) {
        this.setFont(Typeface.createFromFile(path));
    }

    public void setFont(String familyName, int style) {
        this.setFont(Typeface.create(familyName, style));
    }

    public void setFont(Typeface typeface) {
        this.mTextPaint.setTypeface(typeface);
        this.mStrokePaint.setTypeface(typeface);
    }

    public void setFillColor(float r, float g, float b, float a) {
        this.setFillColor(ColorUtils.argb(a, r, g, b));
    }

    public void setFillColor(int color) {
        this.mTextPaint.setColor(color);
    }

    public void setStrokeColor(float r, float g, float b, float a) {
        this.setStrokeColor(ColorUtils.argb(a, r, g, b));
    }

    public void setStrokeColor(int color) {
        this.mStrokePaint.setColor(color);
    }

    public void setStrokeWidth(int strokeWidth) {
        this.mStrokePaint.setStrokeWidth((float)strokeWidth);
    }

    public void setAlignment(int align) {
        switch(align) {
            case 0:
                this.mAlignment = Alignment.ALIGN_NORMAL;
                break;
            case 1:
                this.mAlignment = Alignment.ALIGN_OPPOSITE;
                break;
            case 2:
                this.mAlignment = Alignment.ALIGN_CENTER;
        }

    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public String getContent() {
        return this.mContent;
    }

    public void adjustSize() {
        for(int fontSize = this.mFontSize; fontSize > 1; --fontSize) {
            this.mTextPaint.setTextSize((float)fontSize);
            this.mStrokePaint.setTextSize((float)fontSize);
            StaticLayout staticLayout = new StaticLayout(this.mContent, this.mTextPaint, this.mRect.width(), this.mAlignment, 1.0F, 0.0F, true);
            Rect bounds = new Rect();
            this.mTextPaint.getTextBounds(this.mContent, 0, this.mContent.length(), bounds);
            int topSpace = staticLayout.getLineBaseline(0) + bounds.top;
            if (staticLayout.getHeight() - topSpace <= this.mRect.height()) {
                return;
            }
        }

    }

    public String saveToPath(String path) {
        if(mBitmap==null){
            createBitmap();
        }
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        this.mCanvas.drawPaint(paint);
        this.draw(this.mCanvas);
        this.saveBitmapToPath(this.mBitmap, path);
        if(!nowTextCanvasIsVisible){
            recycleBitmap();
        }
        return path;


    }

//    private void getCanvasData() {
//        this.draw(this.mCanvas);
//        int[] pixels = new int[this.mBitmap.getWidth() * this.mBitmap.getHeight()];
//        this.mBitmap.getPixels(pixels, 0, this.mBitmap.getWidth(), 0, 0, this.mBitmap.getWidth(), this.mBitmap.getHeight());
//    }

    public void draw(Canvas canvas) {
        StaticLayout text = new StaticLayout(this.mContent, this.mTextPaint, this.mRect.width(), this.mAlignment, 1.0F, 0.0F, true);
        StaticLayout stroke = new StaticLayout(this.mContent, this.mStrokePaint, this.mRect.width(), this.mAlignment, 1.0F, 0.0F, true);
        Rect bounds = new Rect();
        this.mTextPaint.getTextBounds(this.mContent, 0, this.mContent.length(), bounds);
        canvas.save();
        canvas.translate((float)this.mRect.left, (float)(this.mRect.top - (bounds.top + text.getLineBaseline(0))));
        if (this.mStrokePaint.getStrokeWidth() > 0.0F) {
            stroke.draw(canvas);
        }

        text.draw(canvas);
        canvas.restore();
    }

    private void drawRect(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Style.STROKE);
        paint.setColor(-256);
        canvas.drawRect(this.mRect, paint);
    }

    int[] getIntArray(JSONArray array) throws JSONException {
        int[] ints = new int[array.length()];

        for(int i = 0; i < array.length(); ++i) {
            ints[i] = array.getInt(i);
        }

        return ints;
    }

    private Bitmap saveBitmapToPath(Bitmap bitmap, String path) {
        if (!path.endsWith(".png") && !path.endsWith(".PNG")) {
            throw new IllegalArgumentException();
        } else {
            File file = new File(path);
            FileOutputStream out = null;

            try {
                out = new FileOutputStream(file);
                bitmap.compress(CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (FileNotFoundException var16) {
                var16.printStackTrace();
            } catch (IOException var17) {
                var17.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException var15) {
                        var15.printStackTrace();
                    }
                }

            }

            return bitmap;
        }
    }

    public static void setFontMap(Map<String, String> fontMap) {
        sFontMap = fontMap;
    }

    public static Map<String, String> getFontMap() {
        return sFontMap;
    }



    /**
     * description ：isShow方法，如果当前textUiModel 是选中状态，那么isShow 方法就会执行true ,正好不显示则执行false,为false 的时候
     * 需要回收所有的bitmap,为true 的时候需要重新创建出来。
     * date: ：2019/12/2 13:50
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public void isShow(boolean isShow){
        nowTextCanvasIsVisible=isShow;
        if(isShow){
            createBitmap();
        }else{
            recycleBitmap();
        }

    }


    private void createBitmap(){
        Log.d("OOM","文字创建");
        setCanvasSize(size[0], size[1]);
    }



    private void recycleBitmap(){
        if(mBitmap!=null&&!mBitmap.isRecycled()){
            mBitmap.isRecycled();
            mBitmap=null;
            mCanvas=null;
        }
        Log.d("OOM","文字回收");
    }







}
