package com.shixing.sxve.ui.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.shixing.sxve.ui.AssetDelegate;
import com.shixing.sxve.ui.util.Size;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Observable;
import java.util.UUID;

public class TextUiModel extends AssetUi {
    private Typeface typeface;
    private final int mMax;
    private int[] mArea;
    private Rect mRect;
    private final Paint mRectPaint;
    private GroupModel mGroupModel;
    private final SXTextCanvas2 mTextCanvas;
    private float padding;
    private static String catchTextContent;

    public boolean isTextChanged() {
        return textChanged;
    }

    public void setTextChanged(boolean textChanged) {
        this.textChanged = textChanged;
    }

    private boolean textChanged;

    public TextUiModel(String folder, JSONObject asset, AssetDelegate delegate, Size size) throws JSONException {
        super(folder, asset.getJSONObject("ui"), delegate, size);
        JSONObject ui = asset.getJSONObject("ui");
        mMax = ui.getInt("max");
        padding = ui.getInt("size") * 0.1f;
        mArea = getIntArray(ui.getJSONArray("area"));
        mRect = new Rect(mArea[0], mArea[1], mArea[0] + mArea[2], mArea[1] + mArea[3]);
        PathEffect effect = new DashPathEffect(new float[]{1, 2, 4, 8}, 1);
        mRectPaint = new Paint();
        mRectPaint.setPathEffect(effect);
        mRectPaint.setStrokeWidth(3);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setColor(Color.YELLOW);
        mTextCanvas = new SXTextCanvas2(asset.toString(), 1);
        mTextCanvas.adjustSize();
        typeface = getTypeface(asset, ui, folder + "/font");
        setTypeface(typeface);
        textChanged = false;
    }

    @Override
    public void draw(Canvas canvas, int activeLayer) {
        if (drawBitmapBj != null) {
            canvas.drawBitmap(drawBitmapBj, 0, 0, null);
        }
        canvas.save();
        canvas.drawRect(mRect, mRectPaint);
        canvas.restore();
        mTextCanvas.draw(canvas);
    }


    @Override
    public void isShow(boolean show) {
        mTextCanvas.isShow(show);
    }

    @Override
    public void scale(float sx, float sy, float px, float py) {
        Log.d("scale", "scaleText");
    }


    @Override
    public boolean isPointInside(PointF point) {
        return mRect.contains((int) point.x, (int) point.y);
    }

    @Override
    public void singleTap(GroupModel groupModel) {
        mGroupModel = groupModel;
        mDelegate.editText(this);
    }

    @Override
    public String getSnapPath(String folder) {
        String path = folder + File.separator + UUID.randomUUID() + "text" + ".png";
        if (mTextCanvas != null) {
            if (mTextCanvas.getContent() == null || mTextCanvas.getContent().equals("")) {
                mTextCanvas.setContent(catchTextContent);
            }
            mTextCanvas.saveToPath(path);
        }

        return path;
    }

    @Override
    public String getSnapPathForKeep(String folder) {
        String path = folder + File.separator + UUID.randomUUID() + "text" + ".png";
        mTextCanvas.saveToPath(path);
        return path;
    }

    @Override
    public String getOriginPath(String folder) {
        String path = folder + File.separator + UUID.randomUUID() + "text" + ".png";
        mTextCanvas.saveToPath(path);
        return path;
    }


    /**
     * description ：是否存在占位图，只针对mediaUiModel
     * date: ：2019/10/30 14:20
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    @Override
    public boolean hasPlaceholder() {
        return false;
    }


    @Override
    public boolean hasChooseFilter(int filterPosition) {
        return false;
    }

    @Override
    public boolean hasChooseBg(String path) {
        return false;
    }

    public String getText() {
        return mTextCanvas.getContent();
    }

    public void setText(String text) {
        catchTextContent = text;
        mTextCanvas.setContent(text);
        mTextCanvas.adjustSize();
        if (mGroupModel != null) {
            mGroupModel.notifyRedraw();
        }
    }

    public int getMax() {
        return mMax;
    }

    public void setTypeface(Typeface typeface) {
        mTextCanvas.setFont(typeface);
        if (mGroupModel != null) {
            mGroupModel.notifyRedraw();
        }
    }

    public void setTextColor(int color) {
        mTextCanvas.setFillColor(color);
        if (mGroupModel != null) {
            mGroupModel.notifyRedraw();
        }
    }

    public static Typeface getTypeface(JSONObject asset, JSONObject ui, String folderPath) {
        try {
            String ui_extra = asset.getString("ui_extra");
            int fontStyle = 0;
            if (!ui_extra.isEmpty()) {
                String[] o = ui_extra.split(",");
                int bold = Integer.parseInt(o[0]);
                int italic = Integer.parseInt(o[1]);
                if (bold != 0 || italic != 0) {
                    if (bold == 0 && italic == 1) {
                        fontStyle = Typeface.ITALIC;
                    } else if (bold == 1 && italic == 0) {
                        fontStyle = Typeface.BOLD;
                    } else if (bold == 1 && italic == 1) {
                        fontStyle = Typeface.BOLD_ITALIC;
                    }
                }
            }
            String fontFamily = ui.getString("font_family");
            String fontFileName = ui.getString("font_file");

            File directory = new File(folderPath);
            File font = null;
            if (directory.exists() && directory.isDirectory()) {
                //优先从模板font文件夹读取，取出对应该Model的字体文件，按名字匹配
                String font_path;
                if (fontFileName.isEmpty()) {
                    String font_ttf = folderPath + File.separator + fontFamily + ".ttf";
                    String font_TTF = folderPath + File.separator + fontFamily + ".TTF";
                    String font_ttc = folderPath + File.separator + fontFamily + ".ttc";
                    if (new File(font_ttf).exists()) {
                        font_path = font_ttf;
                    } else if (new File(font_TTF).exists()) {
                        font_path = font_TTF;
                    } else if (new File(font_ttc).exists()) {
                        font_path = font_ttc;
                    } else {
                        font_path = directory.listFiles()[0].getPath();
                    }
                } else {
                    font_path = folderPath + File.separator + fontFileName;
                }
                font = new File(font_path);
                return decodeTypeface(font, fontFamily, fontStyle);
            } else {
                return decodeTypeface(font, fontFamily, fontStyle);
            }
        } catch (JSONException e) {
        }
        //没有配置，找不到font字段，找不到对应字体，使用系统默认字体
        return Typeface.defaultFromStyle(Typeface.NORMAL);
    }

    public static String toUtf8(String str) {
        String result = null;
        try {
            result = new String(str.getBytes("UTF-8"), "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Typeface decodeTypeface(File font, String fontFamily, int fontStyle) {
        Typeface typeface;
        if (font != null && font.exists()) {
            typeface = Typeface.createFromFile(font);
            typeface = Typeface.create(typeface, fontStyle);
            if (!typeface.equals(Typeface.DEFAULT)) {
                //获取成功
                return typeface;
            } else {
                typeface = Typeface.createFromFile(font);
                if (!typeface.equals(Typeface.DEFAULT)) {
                    return typeface;
                }
                //获取失败
                return Typeface.SANS_SERIF;
            }
        } else {
            //没有配置字体文件夹,但json里面有字段，从系统字库Map里面搜索
            typeface = Typeface.create(fontFamily, fontStyle);
            if (!typeface.equals(Typeface.DEFAULT)) {
                return typeface;
            } else {
                typeface = Typeface.create(fontFamily, Typeface.NORMAL);
                if (!typeface.equals(Typeface.DEFAULT)) {
                    return typeface;
                }
                //获取失败
                return Typeface.SANS_SERIF;
            }
        }

    }

    public Typeface getTypeface() {
        return typeface;
    }

    public void setStrokeColor(int color) {
        mTextCanvas.setStrokeColor(color);
        if (mGroupModel != null) {
            mGroupModel.notifyRedraw();
        }
    }

    public void setStrokeWidth(int strokeWidth) {
        mTextCanvas.setStrokeWidth(strokeWidth);
        if (mGroupModel != null) {
            mGroupModel.notifyRedraw();
        }
    }

    public void adjustTextArea(int height) {
        mRect.offset(0, -height);
        Rect rect = mTextCanvas.getTextArea();
        rect.top -= height;
        mTextCanvas.setTextRect(rect);


//        if (mGroupModel != null) {  //todo ztj  不用这里及时刷新
//            mGroupModel.notifyRedraw();
//        }


    }


}
