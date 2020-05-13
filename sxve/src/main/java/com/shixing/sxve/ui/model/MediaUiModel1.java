package com.shixing.sxve.ui.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.shixing.sxve.ui.AssetDelegate;
import com.shixing.sxve.ui.util.AffineTransform;
import com.shixing.sxve.ui.util.Size;
import com.shixing.sxvideoengine.SXCompositor;
import com.shixing.sxvideoengine.SXRenderListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.UUID;

public class MediaUiModel1 extends MediaUiModel {

    private final int mDuration;
    private final int[] p;
    private final int[] a;
    private final double r;
    private final float[] s;
    private final double t;
    private final RectF mRect;
    private Bitmap mBitmap;
    private Matrix mMatrix;
    private final Paint mInitPaint;
    private Paint mTransparentPaint;
    private Matrix mInverseMatrix;
    private Paint mPaint;
    private boolean mIsVideo;
    private String mVideoPath;
    private boolean mMute;
    private float mStartTime;
    private GroupModel mGroupModel;

    public MediaUiModel1(String folder, JSONObject ui, Bitmap bitmap, AssetDelegate delegate, Size size) throws JSONException {
        super(folder, ui, delegate, size);
        mBitmap = bitmap;

        mDuration = ui.getInt("duration");
        p = getIntArray(ui.getJSONArray("p"));
        a = getIntArray(ui.getJSONArray("a"));
        r = ui.getDouble("r");
        s = getFloatArray(ui.getJSONArray("s"));
        t = ui.getDouble("t");

        mInitPaint = new Paint();
        mInitPaint.setAntiAlias(true);
        mInitPaint.setFilterBitmap(true);
        mInitPaint.setAlpha((int) (t * 255));

        AffineTransform affineTransform = new AffineTransform();
        affineTransform.set(new PointF(a[0], a[1]), new PointF(p[0], p[1]), new PointF(s[0], s[1]), (float) Math.toRadians(r));
        mMatrix = affineTransform.getMatrix();
        mInverseMatrix = new Matrix();
        mMatrix.invert(mInverseMatrix);

        mRect = new RectF(0, 0, size.getWidth(), size.getHeight());
        mMatrix.mapRect(mRect);

        mTransparentPaint = new Paint();
        mTransparentPaint.setAlpha(102);

        mPaint = mInitPaint;
    }

    @Override
    public void draw(Canvas canvas, int activeLayer) {
        mPaint = mInitPaint;

        //后图
        if (b != null) {
            canvas.drawBitmap(b, 0, 0, null);
        }

        if (activeLayer >= 0 && activeLayer < index) {
            mPaint = mTransparentPaint;
        }
        //绘制的素材

        if (mBitmap != null) {
            if (activeLayer != index) {
                canvas.save();
                canvas.clipRect(mRect);
                canvas.drawBitmap(mBitmap, mMatrix, mPaint);
                canvas.restore();
            } else {
                canvas.drawBitmap(mBitmap, mMatrix, mPaint);
            }
        }

        //前景图
        if (f != null) {
            if (activeLayer > 0) {
                mPaint = mTransparentPaint;
            }
            canvas.drawBitmap(f, 0, 0, mPaint);
        }
    }

    @Override
    public void scroll(float distanceX, float distanceY) {
//        if (!mIsVideo) {
            mMatrix.postTranslate(-distanceX, -distanceY);
//        }
    }

    @Override
    public void scale(float sx, float sy, float px, float py) {
//        if (!mIsVideo) {
            mMatrix.postScale(sx, sy, px, py);
//        }
    }

    @Override
    public void rotate(float degrees, float px, float py) {
//        if (!mIsVideo) {
            mMatrix.postRotate(degrees, px, py);
//        }
    }

    @Override
    public boolean isPointInside(PointF point) {
//        mMatrix.invert(mInverseMatrix);
//        float[] dst = new float[2];
//        mInverseMatrix.mapPoints(dst, new float[]{point.x, point.y});
//        return dst[0] >= 0 && dst[0] <= mBitmap.getWidth() && dst[1] >= 0 && dst[1] <= mBitmap.getHeight();
        return mRect.contains(point.x, point.y);
    }

    @Override
    public void singleTap(GroupModel groupModel) {
        mGroupModel = groupModel;
        mDelegate.pickMedia(this);
    }

    @Override
    public void hasChooseBg(String path,boolean isVideo) {

    }

    @Override
    public String getSnapPath(String folder) {
        if (!mIsVideo) {
            Bitmap bitmap = Bitmap.createBitmap(size.getWidth(), size.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(mBitmap, mMatrix, mInitPaint);
            String path = folder + File.separator + UUID.randomUUID() + ".png";
            saveBitmapToPath(bitmap, path);
            return path;
        } else {
            final String path = folder + File.separator + UUID.randomUUID() + ".mp4";
            Matrix matrix = new Matrix(mMatrix);
            matrix.postConcat(mInverseMatrix);
            SXCompositor sxCompositor = new SXCompositor(mVideoPath, path, matrix, !mMute);
            sxCompositor.setWidth(size.getWidth());
            sxCompositor.setHeight(size.getHeight());
            sxCompositor.setStartTime(mStartTime);
            sxCompositor.setDuration(mDuration);
            sxCompositor.setBitrateFactor(1f);
            sxCompositor.setRenderListener(new SXRenderListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void onUpdate(int progress) {

                }

                @Override
                public void onFinish(boolean success, String msg) {
                    Log.d("TEST", "mediaUiModel clip finish: " + path);
                }

                @Override
                public void onCancel() {

                }
            });
            sxCompositor.run();
            return path;
//            return mVideoPath;
        }
    }

    /**
     * @return 素材时长，单位为帧
     */
    public int getDuration() {
        return mDuration;
    }

    @Override
    public int getNowIndex() {
        return 0;
    }

    @Override
    public int getNowGroup() {
        return 0;
    }

    public void setImageAsset(String path) {
        mIsVideo = false;
        mBitmap = BitmapFactory.decodeFile(path);
        mInitPaint.setAlpha(255);

        initPosition();

        if (mGroupModel != null) {
            mGroupModel.notifyRedraw();
        }
    }

    public void setVideoPath(String path, boolean mute, float startTime) {
        mVideoPath = path;
        mMute = mute;
        mStartTime = startTime;
        mIsVideo = true;
        mInitPaint.setAlpha(255);

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        mBitmap = retriever.getFrameAtTime((long) (startTime * 1000 * 1000));
        retriever.release();

        initPosition();

        if (mGroupModel != null) {
            mGroupModel.notifyRedraw();
        }
    }

    private void initPosition() {
        float rw = mRect.width();
        float rh = mRect.height();
        int bw = mBitmap.getWidth();
        int bh = mBitmap.getHeight();

        float scale = Math.max(rw / bw, rh / bh);
        mMatrix.reset();
        mMatrix.postTranslate(mRect.left + (rw - bw * scale) / 2, mRect.top + (rh - bh * scale) / 2);
        mMatrix.preScale(scale, scale);
    }
}
