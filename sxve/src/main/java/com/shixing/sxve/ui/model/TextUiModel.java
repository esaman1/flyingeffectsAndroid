package com.shixing.sxve.ui.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.shixing.sxve.ui.AssetDelegate;
import com.shixing.sxve.ui.util.AffineTransform;
import com.shixing.sxve.ui.util.ColorUtils;
import com.shixing.sxve.ui.util.Size;
import com.shixing.sxvideoengine.SXTextCanvas;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.UUID;

public class TextUiModel extends AssetUi {

    private final int mMax;
    private final int[] mArea;
    private final Bitmap mBitmap;
    private final Rect mRect;
    private GroupModel mGroupModel;
    protected Paint mRectPaint;
    protected SXTextCanvas mTextCanvas;

    public TextUiModel(String folder, JSONObject asset, Bitmap bitmap, AssetDelegate delegate, Size size) throws JSONException {
        super(folder, asset.getJSONObject("ui"), delegate, size);
        JSONObject ui = asset.getJSONObject("ui");
        mBitmap = bitmap;

        mMax = ui.getInt("max");
        mArea = getIntArray(ui.getJSONArray("area"));
        mRect = new Rect(mArea[0], mArea[1], mArea[0] + mArea[2], mArea[1] + mArea[3]);
        mRectPaint = new Paint();
        mRectPaint.setStrokeWidth(2);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setColor(Color.YELLOW);

        mTextCanvas = new SXTextCanvas(asset.toString());
//        mTextCanvas.adjustSize();
    }

    @Override
    public void draw(Canvas canvas, int activeLayer) {
        if (b != null) {
            canvas.drawBitmap(b, 0, 0, null);
        }

        canvas.drawRect(mRect, mRectPaint);

        mTextCanvas.draw(canvas);

        if (f != null) {
            canvas.drawBitmap(f, 0, 0, null);
        }
    }

    @Override
    public void isShow(boolean show) {

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
        String path = folder + File.separator + UUID.randomUUID() + ".png";
        mTextCanvas.saveToPath(path);
        return path;
    }

    public String getText() {
        return mTextCanvas.getContent();
    }

    public void setText(String text) {
        mTextCanvas.setContent(text);
//        mTextCanvas.adjustSize();
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
}
