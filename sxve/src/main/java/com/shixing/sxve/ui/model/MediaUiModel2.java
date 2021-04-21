package com.shixing.sxve.ui.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.shixing.sxve.ui.AssetDelegate;
import com.shixing.sxve.ui.albumType;
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
    private boolean isMaskSlide = false;
    private String lastSavePath;
    //这里主要是为了，漫画和视频抠图这里，
    private String lastOtherPath;
    private String path;
    //是否有背景
    private boolean hasBg;

    private String nowChooseBjPath = "";

    private Bitmap bgBitmap;

    private Matrix mMatrixBj;

    private String PathOrigin;//视频的源文件

    //最后渲染出来模板大小
    private Size temSize;

    private float fps;

    //1 表示换装
    private int nowTemplateIsAnim;

    private boolean isToSing;


//    private float firstTranX;
//    private float FirstTranY;
    private float firstScale;
    private boolean isFirstMatrix = true;


    public MediaUiModel2(String folder, JSONObject ui, Bitmap bitmap, AssetDelegate delegate, Size size, Size temSize, float fps, int nowTemplateIsAnim, boolean isToSing) throws JSONException {
        super(folder, ui, delegate, size);
        mBitmap = bitmap;
        this.fps = fps;
        this.temSize = temSize;
        this.isToSing = isToSing;
        int[] editSize = getIntArray(ui.getJSONArray("editSize"));
        mClipWidth = editSize[0];
        mClipHeight = editSize[1];
        Log.d("OOM2", "mClipWidth=" + size.getWidth() + "mClipHeight=" + size.getHeight());
        this.nowTemplateIsAnim = nowTemplateIsAnim;
        int[] p = getIntArray(ui.getJSONArray("p")); //position
        int[] a = getIntArray(ui.getJSONArray("a")); //anchor
        float[] s = getFloatArray(ui.getJSONArray("s")); //scale
        double t = ui.getDouble("t"); //transparent
        mR = ui.getDouble("r");//rotation
        mDuration = ui.getInt("duration");
        Log.d("OOM", "mDuration=" + mDuration);
        mInitPaint = new Paint();
        mInitPaint.setAntiAlias(true);
        mInitPaint.setFilterBitmap(true);
        mInitPaint.setAlpha((int) (t * 255));
        mPaint = mInitPaint;

        AffineTransform affineTransform = new AffineTransform();
        affineTransform.set(new PointF(a[0], a[1]), new PointF(p[0], p[1]), new PointF(s[0], s[1]), (float) Math.toRadians(mR));
        mInitMatrix = affineTransform.getMatrix();
//        mMatrixBj = affineTransform.getMatrix();
        //默认居中
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
//        if (!IsAnim) {


        if (b != null && TextUtils.isEmpty(nowChooseBjPath)) {
            canvas.drawBitmap(b, 0, 0, null);
        } else {
            //有用户选择的背景的情况
            if (!TextUtils.isEmpty(nowChooseBjPath)) {
                canvas.drawBitmap(bgBitmap, mMatrixBj, null);
            }
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


        if (isFirstMatrix && isToSing) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    float values[] = new float[9];
                    mMatrix.getValues(values);
//                    firstTranX = values[2];
//                    FirstTranY = values[5];
                    firstScale = values[0];
                    Log.d("OOM22",  "scanx=" + firstScale);
                }
            },300);
            isFirstMatrix = false;
        }


//        float values[] = new float[9];
//        mMatrix.getValues(values);
//        float nowScale = values[0];
//        float needScale = nowScale / firstScale;
//        needScale = 1 / needScale;
//        Log.d("OOM22", "needScale=" + needScale+"firstScale="+firstScale);

    }


    /**
     * description :仿tiktok 唱歌动画,手势数据  0,420  1080   1080
     * creation date: 2021/3/3
     * user : zhangtongju
     */

    public void getTransFormChangeData(TranChangeCallback callback) {
        float[] values = new float[9];
        mMatrix.getValues(values);
        float nowTranX = values[2];
        float nowTranY = values[5];
        float nowScale = values[0];
        float bitmapH = mBitmap.getHeight() * nowScale;
        float bitmapW = mBitmap.getWidth() * nowScale;
        float reactLeftX = (0 - nowTranX) + 540f;
        float reactLeftY = 420 - nowTranY + 540f;
        float needX = reactLeftX / bitmapW;
        float needY = reactLeftY / (bitmapH);
        float needScale = nowScale / firstScale;
        needScale = 1 / needScale;
        callback.changeBack(needX, needY, needScale);
        Log.d("OOM22", "needScale=" + needScale);
    }


    public interface TranChangeCallback {
        void changeBack(float tranX, float tranY, float scale);
    }


    @Override
    public void scroll(float distanceX, float distanceY) {

        Log.d("OOM11", "scroll:distanceX=" + distanceX + "distanceY=" + distanceY);
        if (nowTemplateIsAnim != 1) {
            isVideoSlide = true;
            isMaskSlide = true;
            mMatrix.postTranslate(-distanceX, -distanceY);
        }

    }


    @Override
    public void scale(float sx, float sy, float px, float py) {
        Log.d("OOM11", "scale:sx=" + sx + "sy=" + sy + "px=" + px + "py=" + py);

        if (nowTemplateIsAnim != 1) {
            isVideoSlide = true;
            isMaskSlide = true;
            mMatrix.postScale(sx, sy, px, py);
        }

    }

    @Override
    public void rotate(float degrees, float px, float py) {
        if (nowTemplateIsAnim != 1 && !isToSing) {
            isVideoSlide = true;
            isMaskSlide = true;
            mMatrix.postRotate(degrees, px, py);
        }


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
    public String getOriginPath(String folder) {
        if (!mIsVideo) {
            return path;
        } else {
            return mVideoPath;
        }
    }

    @Override
    public void hasChooseBg(String path, boolean isVideo) {
        nowChooseBjPath = path;
        hasBg = path != null && !path.equals("");
        if (hasBg) {
            if (isVideo) {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(path);
                bgBitmap = retriever.getFrameAtTime(0);
                countMatrixBj(bgBitmap);
                retriever.release();
            } else {
                bgBitmap = getSmallBmpFromFile(path, size.getHeight(), size.getWidth());
                countMatrixBj(bgBitmap);
            }
        }
    }


    public int getOriginalBitmapWidth() {
        return mBitmap.getWidth();
    }


    public int getOriginalBitmapHeight() {
        return mBitmap.getHeight();
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

            if (isVideoSlide || TextUtils.isEmpty(lastSavePath)) {
                final String path = folder + File.separator + UUID.randomUUID() + ".mp4";
                Matrix matrix = new Matrix(mMatrix);
                matrix.postConcat(mInverseMatrix);
                SXCompositor sxCompositor = new SXCompositor(mVideoPath, path, matrix, !mMute);
                Log.d("OOM", "mVideoPath=" + mVideoPath);
                sxCompositor.setWidth(mClipWidth);
                sxCompositor.setHeight(mClipHeight);
                sxCompositor.setStartTime(mStartTime);
                float test = mDuration / (float) fps;
                if (test < 0.5) {
                    test = 0.5f;
                }
                Log.d("OOM", "需要裁剪的时长为" + test + 1);
                sxCompositor.setDuration(test);
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

                Log.d("OOM", "视频地址为" + mVideoPath);
                sxCompositor.run();
                lastSavePath = path;
                isVideoSlide = false;
                return path;
            } else {
                return lastSavePath;
            }


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

    @Override
    public int getNowGroup() {
        return group;
    }

    public void setImageAsset(String path) {
        isFirstMatrix=true;
        this.path = path;
        isVideoSlide = true;
        isMaskSlide = true;
        mIsVideo = false;
        mBitmap = getSmallBmpFromFile(path, size.getHeight(), size.getWidth());
        countMatrix(mBitmap, path, true);
        mInitPaint.setAlpha(255);
        initPosition();
        if (mGroupModel != null) {
            mGroupModel.notifyRedraw();
        }
    }

    public void setVideoPath(String path, boolean mute, float startTime) {
        mVideoPath = path;
        mMute = mute;
        isVideoSlide = true;
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

    public boolean isVideoType() {
        return mIsVideo;
    }


    public float getFps() {
        return fps;
    }


    /**
     * description ：设置第一针显示，只针对抠图视频的时候
     * creation date: 2020/4/14
     * user : zhangtongju
     */
    public void setVideoCover(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
//        countMatrix(BitmapCompress.zoomImg(mBitmap,size.getWidth(),size.getHeight()), path, true);
        initPosition();
    }


    public void resetUi() {
        lastOtherPath = "";
        isVideoSlide = true;
        isMaskSlide = true;
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


    /**
     * description ：获得边框大小的matrix
     * creation date: 2021/3/2
     * user : zhangtongju
     */
    public Matrix getMediaUiMatrix() {
        return mMatrix;
    }


    /**
     * description ：获得内边框大小的matrix
     * creation date: 2021/3/2
     * user : zhangtongju
     */
    public Matrix GetInverseMatrix() {
        Matrix matrix = new Matrix(mMatrix);
        matrix.postConcat(mInverseMatrix);
        return matrix;
    }


    /**
     * description ：只要不是视频，就都用白色的底图
     * creation date: 2020/4/17
     * user : zhangtongju
     */
    private String mimeType;
    private Bitmap bitmapWhite;

    public String getpathForThisMatrix(String folder, String cartoonPath) {

        if (!TextUtils.isEmpty(cartoonPath)) {
            String extension = MimeTypeMap.getFileExtensionFromUrl(cartoonPath); //获得格式
            if (extension != null && !extension.equals("")) {
                mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                if (mimeType == null) {
                    mimeType = getPathType(cartoonPath);
                }
            } else {  //有些手机获取不到，比如vivo 是中文目录
                mimeType = getPathType(mimeType);
            }

            //是图片或者第二张图片和第一张一样，说明用户选择了视频没有抠图，那么和图片的逻辑一样的，需要一个白色图片
            if (albumType.isImage(mimeType) || cartoonPath.equals(mVideoPath)) {
                Bitmap bitmap = Bitmap.createBitmap(mClipWidth, mClipHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                Matrix matrix = new Matrix(mMatrix);
                matrix.postConcat(mInverseMatrix);
                if (mBitmap != null) {
                    bitmapWhite = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), mBitmap.getConfig());
                    canvas.drawBitmap(getImage(bitmapWhite), matrix, mInitPaint);
                }
                String path = folder + File.separator + UUID.randomUUID() + ".png";
                saveBitmapToPath(bitmap, path);
                recycleWhiteBitmap();
                return path;
            } else {


                if (isMaskSlide || TextUtils.isEmpty(lastOtherPath)) {
                    final String path = folder + File.separator + UUID.randomUUID() + ".mp4";
                    Matrix matrix = new Matrix(mMatrix);
                    matrix.postConcat(mInverseMatrix);
                    SXCompositor sxCompositor = new SXCompositor(cartoonPath, path, matrix, !mMute);
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
                    isMaskSlide = false;
                    lastOtherPath = path;
                    Log.d("oom", "视频地址2为" + cartoonPath);
                    return path;
                } else {
                    return lastOtherPath;
                }
            }
        }

        return cartoonPath;

    }


    public String getpathForThisBjMatrixVideo(String folder, String cartoonPath) {
        final String path = folder + File.separator + UUID.randomUUID() + ".mp4";
        SXCompositor sxCompositor = new SXCompositor(cartoonPath, path, mMatrixBj, false);
        sxCompositor.setWidth(temSize.getWidth());
        sxCompositor.setHeight(temSize.getHeight());
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
        Log.d("oom", "视频地址2为" + cartoonPath);
        return path;
    }


    public String getpathForThisBjMatrixImage(String folder, String cartoonPath) {
        Bitmap bgBitmap = getSmallBmpFromFile(cartoonPath, size.getWidth(), size.getWidth());
        String keepPath = folder + File.separator + UUID.randomUUID() + ".png";
        countMatrixBj(bgBitmap);


        if (bgBitmap != null) {
            Bitmap bitmap = Bitmap.createBitmap(temSize.getWidth(), temSize.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bgBitmap, mMatrixBj, mInitPaint);
            saveBitmapToPath(bitmap, keepPath);
        }
        return keepPath;
    }

    private void recycleWhiteBitmap() {
        if (bitmapWhite != null && !bitmapWhite.isRecycled()) {
            bitmapWhite.recycle();
            bitmapWhite = null;
        }
    }

    private String getPathType(String path) {
        String mimeType;
        String suffix = path.substring(path.lastIndexOf(".") + 1).toLowerCase();
        if (suffix.equalsIgnoreCase("mp4") || suffix.equalsIgnoreCase("M4V") || suffix.equalsIgnoreCase("3gp") || suffix.equals("3G2") || suffix.equalsIgnoreCase("WMV") || suffix.equalsIgnoreCase("ASF") || suffix.equalsIgnoreCase("AVI") || suffix.equalsIgnoreCase("FLV") || suffix.equalsIgnoreCase("MKV") || suffix.equalsIgnoreCase("WEBM")) {
            mimeType = "video/*";
        } else {
            mimeType = "image/*";
        }
        return mimeType;
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

//    public String getOriginalPath() {
//        return path;
//    }


    //把透明转换成白色
    public static Bitmap getImage(Bitmap mBitmap) {
        if (mBitmap != null) {
            int mWidth = mBitmap.getWidth();
            int mHeight = mBitmap.getHeight();
            for (int i = 0; i < mHeight; i++) {
                for (int j = 0; j < mWidth; j++) {
                    int color = mBitmap.getPixel(j, i);
                    color = Color.argb(255, 255, 255, 255);
                    mBitmap.setPixel(j, i, color);
                }
            }
        }
        return mBitmap;
    }


    private void countMatrixBj(Bitmap bp) {
        if (bp != null) {
            float rw = temSize.getWidth();
            float rh = temSize.getHeight();
            float bw = bp.getWidth();
            float bh = bp.getHeight();

            Log.d("OOM2", "size.getWidth()=" + rw + "bw=" + bw);
            Log.d("OOM2", " size.getHeight()=" + rh + "bh=" + bh);
            Log.d("OOM2", " mClipWidth=" + mClipWidth + "mClipHeight=" + mClipHeight);
            float scale = Math.max(rw / bw, rh / bh);
            Log.d("OOM2", "scale=" + scale);
            Log.d("OOM2", "SIZEw=" + scale);
            mMatrixBj = new Matrix();
            float tranX = (rw - bw * scale) / 2;
//            if(tranX<0){
//                tranX=0;
//            }
            float tranY = (rh - bh * scale) / 2;
//            if(tranY<0){
//                tranY=0;
//            }

            mMatrixBj.postTranslate(tranX, tranY);
            Log.d("OOM2", "tranX=" + tranX + "tranY=" + tranY);
            mMatrixBj.preScale(scale, scale);
        }
    }


    public String getOriginalPath() {
        if (mIsVideo) {
            return mVideoPath;
        } else {
            return path;
        }
    }


    public void setPathOrigin(String path) {
        PathOrigin = path;
    }


    public String getPathOrigin() {
        return PathOrigin;
    }

}
