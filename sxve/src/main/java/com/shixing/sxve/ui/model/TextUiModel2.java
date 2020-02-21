package com.shixing.sxve.ui.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;

import com.shixing.sxve.ui.AssetDelegate;
import com.shixing.sxve.ui.util.AffineTransform;
import com.shixing.sxve.ui.util.Size;
import com.shixing.sxvideoengine.SXTextCanvas;

import org.json.JSONException;
import org.json.JSONObject;

public class TextUiModel2 extends TextUiModel {
    private Matrix mMatrix;
    private Matrix mInverseMatrix;
    private Rect mEditSize;

    public TextUiModel2(String folder, JSONObject asset, Bitmap bitmap, AssetDelegate delegate, Size size) throws JSONException {
        super(folder, asset, bitmap, delegate, size);
        JSONObject ui = asset.getJSONObject("ui");

        //ui2.0中area用来确定手势区域，editSize确定文字框大小

        int[] p = getIntArray(ui.getJSONArray("p"));
        int[] a = getIntArray(ui.getJSONArray("a"));
        int r = ui.getInt("r");
        float[] s = getFloatArray(ui.getJSONArray("s"));
        double t = ui.getDouble("t");

        AffineTransform affineTransform = new AffineTransform();
        affineTransform.set(new PointF(a[0], a[1]), new PointF(p[0], p[1]), new PointF(s[0], s[1]), (float) Math.toRadians(r));
        mMatrix = affineTransform.getMatrix();
        mInverseMatrix = new Matrix();
        mMatrix.invert(mInverseMatrix);

        int[] editSize = getIntArray(ui.getJSONArray("editSize"));
        mEditSize = new Rect(0, 0, editSize[0], editSize[1]);

        mTextCanvas = new SXTextCanvas(asset.toString(), 2);
    }

    @Override
    public void draw(Canvas canvas, int activeLayer) {
        if (b != null) {
            canvas.drawBitmap(b, 0, 0, null);
        }

        canvas.save();
        canvas.concat(mMatrix);

        mTextCanvas.draw(canvas);
        canvas.drawRect(mEditSize, mRectPaint);

        canvas.restore();

        if (f != null) {
            canvas.drawBitmap(f, 0, 0, null);
        }
    }
}
