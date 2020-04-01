package com.shixing.sxve.ui.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.util.Log;

import com.shixing.sxve.ui.AssetDelegate;
import com.shixing.sxve.ui.util.AffineTransform;
import com.shixing.sxve.ui.util.BitmapCompress;
import com.shixing.sxve.ui.util.PhotoBitmapUtils;
import com.shixing.sxve.ui.util.Size;
import com.shixing.sxvideoengine.SXCompositor;
import com.shixing.sxvideoengine.SXRenderListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

public class MediaUiModel2 extends MediaUiModel {

    private final int mDuration;
    private final Rect mRect;
    private Bitmap mBitmap;
    private Matrix mInitMatrix;
    private Matrix mMatrix;
    private Paint mTransparentPaint;
    private final Paint mInitPaint;
    private Matrix mInverseMatrix;
    private Paint mPaint;
    private boolean mIsVideo;
    private String mVideoPath;
    private boolean mMute;
    private float mStartTime;
    private GroupModel mGroupModel;

    private final int mClipWidth;
    private final int mClipHeight;
    private final Path mPath;
    private final double mR;
//    private Paint paintTest;

    private boolean isVideoSlide = false;
    private String lastSavePath;
    private String path;

    public MediaUiModel2(String folder, JSONObject ui, Bitmap bitmap, AssetDelegate delegate, Size size) throws JSONException {
        super(folder, ui, delegate, size);
        mBitmap = bitmap;

        int[] editSize = getIntArray(ui.getJSONArray("editSize"));
        mClipWidth = editSize[0];
        mClipHeight = editSize[1];

        int[] p = getIntArray(ui.getJSONArray("p")); //position
        int[] a = getIntArray(ui.getJSONArray("a")); //anchor
        float[] s = getFloatArray(ui.getJSONArray("s")); //scale
        double t = ui.getDouble("t"); //transparent
        mR = ui.getDouble("r");//rotation
        mDuration = ui.getInt("duration");

        mInitPaint = new Paint();
        mInitPaint.setAntiAlias(true);
        mInitPaint.setFilterBitmap(true);
        mInitPaint.setAlpha((int) (t * 255));
        mPaint = mInitPaint;

        AffineTransform affineTransform = new AffineTransform();
        affineTransform.set(new PointF(a[0], a[1]), new PointF(p[0], p[1]), new PointF(s[0], s[1]), (float) Math.toRadians(mR));
        mInitMatrix = affineTransform.getMatrix();
        mMatrix = new Matrix(mInitMatrix);
        mInverseMatrix = new Matrix();
        mInitMatrix.invert(mInverseMatrix);

        //手势触发区域
        int[] area = getIntArray(ui.getJSONArray("area"));
        mRect = new Rect(area[0], area[1], area[0] + area[2], area[1] + area[3]);

        mTransparentPaint = new Paint();
        mTransparentPaint.setAlpha(102);

        //在界面上的显示区域
        mPath = new Path();
        mPath.addRect(0, 0, mClipWidth, mClipHeight, Path.Direction.CCW);//ccw 逆时针
        mPath.transform(mMatrix);


        DashPathEffect pathEffect = new DashPathEffect(new float[]{20, 20}, 0);
        //绘制边框
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(6);
        //绘制虚线
        mPaint.setPathEffect(pathEffect);
        mPaint.setColor(Color.parseColor("#FF0000"));
    }

    @Override
    public void draw(Canvas canvas, int activeLayer) {
        mPaint = mInitPaint;
        if (!IsAnim) {
            if (b != null) {
                canvas.drawBitmap(b, 0, 0, null);
            }

//        隐藏的这段代码是控制同组里面不同位置，滑动前面一个，后面一个就透明
//        if (activeLayer >= 0 && activeLayer < index) {
//            mPaint = mTransparentPaint;
//        }

            if (mBitmap != null) {
                if (activeLayer != index) {
                    Log.d("OOM", "activeLayer != index");
                    //静态的时候
                    canvas.save();
                    canvas.clipPath(mPath);
                    canvas.drawBitmap(mBitmap, mMatrix, mPaint);
                    canvas.restore();
                } else {
                    Log.d("OOM", "activeLayer == index");
                    //滑动的时候
                    canvas.drawPath(mPath, mPaint);
                    canvas.drawBitmap(mBitmap, mMatrix, mPaint);
                }
            }

            if (f != null) {
                if (activeLayer > 0) {
                    mPaint = mTransparentPaint;
                }
                canvas.drawBitmap(f, 0, 0, mPaint);
            }

        }

    }

    @Override
    public void scroll(float distanceX, float distanceY) {
        isVideoSlide = true;
        mMatrix.postTranslate(-distanceX, -distanceY);
    }

    @Override
    public void scale(float sx, float sy, float px, float py) {
        isVideoSlide = true;
        mMatrix.postScale(sx, sy, px, py);
    }

    @Override
    public void rotate(float degrees, float px, float py) {
        isVideoSlide = true;
        mMatrix.postRotate(degrees, px, py);
    }

    @Override
    public boolean isPointInside(PointF point) {
        return mRect.contains((int) point.x, (int) point.y);
    }

    @Override
    public void singleTap(GroupModel groupModel) {
        mGroupModel = groupModel;

        mDelegate.pickMedia(this);
    }

    @Override
    public String getSnapPath(String folder) {
        if (!mIsVideo) {
            if (isVideoSlide || TextUtils.isEmpty(lastSavePath)) {
                Bitmap bitmap = Bitmap.createBitmap(mClipWidth, mClipHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                Matrix matrix = new Matrix(mMatrix);
                matrix.postConcat(mInverseMatrix);
                if (mBitmap != null) {
                    //解决bug 异常情况下bitmap 为null
                    canvas.drawBitmap(mBitmap, matrix, mInitPaint);
                }
                String path = folder + File.separator + UUID.randomUUID() + ".png";
                saveBitmapToPath(bitmap, path);
                isVideoSlide = false;
                lastSavePath = path;
                return path;
            } else {
                return lastSavePath;
            }


        } else {
            final String path = folder + File.separator + UUID.randomUUID() + ".mp4";
            Matrix matrix = new Matrix(mMatrix);
            matrix.postConcat(mInverseMatrix);
            SXCompositor sxCompositor = new SXCompositor(mVideoPath, path, matrix, !mMute);
            sxCompositor.setWidth(mClipWidth);
            sxCompositor.setHeight(mClipHeight);
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
        return index;
    }

    public void setImageAsset(String path) {
        this.path=path;
        isVideoSlide = true;
        mIsVideo = false;
        mBitmap = getSmallBmpFromFile(path, size.getHeight(), size.getWidth());
        countMatrix(mBitmap, path, false);
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
        if (mBitmap != null) {
            float rw = mClipWidth;
            float rh = mClipHeight;
            int bw = mBitmap.getWidth();
            int bh = mBitmap.getHeight();
            float scale = Math.max(rw / bw, rh / bh);
            Matrix matrix = new Matrix();
            matrix.postTranslate((rw - bw * scale) / 2, (rh - bh * scale) / 2);
            matrix.preScale(scale, scale);
            mMatrix.set(mInitMatrix);
            mMatrix.preConcat(matrix);
        }
    }

    private Bitmap getSmallBmpFromFile(String filePath, int targetW, int targetH) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
//                FileInputStream fis = new FileInputStream(filePath);
//                return VEBitmapFactory.decodeFileDescriptor(fis.getFD(), targetW, targetH);
                return BitmapCompress.zoomImg(file.getPath(), targetW, targetH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public Matrix getMediaUiMatrix() {
        return mMatrix;
    }

    public String getpathForThisMatrix(String folder, String cartoonPath) {

        Bitmap cartoonBitmap = getSmallBmpFromFile(cartoonPath, size.getHeight(), size.getWidth());
        Bitmap bitmap = Bitmap.createBitmap(mClipWidth, mClipHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Matrix matrix = new Matrix(mMatrix);
        matrix.postConcat(mInverseMatrix);
        if (cartoonBitmap != null) {
            //解决bug 异常情况下bitmap 为null
            canvas.drawBitmap(cartoonBitmap, matrix, mInitPaint);
        }
        String path = folder + File.separator + UUID.randomUUID() + ".png";
        saveBitmapToPath(bitmap, path);
        return path;
    }


    /**
     * 获得图片的选旋转角度
     */
    public boolean getOrientation(String path) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            return orientation != 90 && orientation != 270;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    /**
     * description ：计算matrix
     * date: ：2019/9/4 15:53
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    private void countMatrix(Bitmap bp, String path, boolean isExchangeDirection) {

        if (bp != null) {
            Log.d("OOM", "重新计算matrix");
            int widthSize = size.getWidth();
            try {
                if (isExchangeDirection) {
                    mBitmap = PhotoBitmapUtils.amendRotatePhoto(path, bp);
                } else {
                    mBitmap = bp;
                }
                mMatrix.reset();
                float scale = widthSize / (float) mBitmap.getWidth();
                Log.d("mMatrix", "mBitmap=getWidth" + (float) mBitmap.getWidth());
                Log.d("mMatrix", "scale=" + scale);
                mMatrix.postScale(scale, scale);
                double tranY = mBitmap.getHeight() * scale;
                if (size.getHeight() - tranY > 0) {
                    int needY = (int) (size.getHeight() - tranY) / 2;
                    Log.d("mMatrix", "needY=" + needY);
                    mMatrix.postTranslate(0, needY);
                } else {
                    int needY = (int) (tranY - size.getHeight()) / 2;
                    Log.d("mMatrix", "needY=" + needY);
                    mMatrix.postTranslate(0, -needY);
                }

            } catch (Exception e) {
                mBitmap = bp;
                e.printStackTrace();
            }
        }
    }

    public String getOriginalPath(){
        return path;
    }


}
