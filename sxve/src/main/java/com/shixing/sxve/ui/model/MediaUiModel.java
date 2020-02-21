package com.shixing.sxve.ui.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.glidebitmappool.GlideBitmapPool;
import com.shixing.sxve.R;
import com.shixing.sxve.ui.AssetDelegate;
import com.shixing.sxve.ui.PhotoBitmapUtils;
import com.shixing.sxve.ui.SxveConstans;
import com.shixing.sxve.ui.renderFilterSuccess;
import com.shixing.sxve.ui.util.AffineTransform;
import com.shixing.sxve.ui.util.Size;
import com.shixing.sxve.ui.util.VEBitmapFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;


/**
 * description ： MediaUiModel 展示数据，json里面的图片数据，并且通过继承AssetUi把TemplateView 的事件分发
 * 到这里来，这里来处理手势，绘制等数据
 * date: ：2019/5/7 20:07
 * author: 张同举 @邮箱 jutongzhang@sina.com
 */
public class MediaUiModel extends AssetUi {

    /**
     * 是否是视频，并且变慢
     */
    public boolean hasVideoSlow=false;
    /**
     * 视频变慢开始帧
     */
    public String startFrame;
    /**
     * 视频变慢结束帧
     */
    public String endFrame;
    /**
     * 视频变慢等级
     */
    public String slowLevel;
    private final int mDuration;
    private final int[] p;
    private final double t;
    private final int[] a;
    private final int r;
    private final float[] s;
    private int filterPosition;

    private Bitmap mBitmap;
    private Bitmap bgBitmap;
    private final Matrix mMatrix;
    private Matrix mMatrixBj;
    private final Paint mInitPaint;
    private Matrix mInverseMatrix;
    private Paint mPaint;
    private boolean mIsVideo;
    private String mVideoPath;

    private String mImageviewPathOrigin;//图片的源文件
    private String mVideoPathOrigin;//视频的源文件
    public boolean isVideoSlide = false; //视频是否已经滑动
    public boolean isReplaceMaterial = false; //是否替换过了素材
    private boolean ImageHasVariation = false; //图片是否已经渲染完成了
    private Context context;
    private boolean nowViewIsVisible = false;
    private boolean isMaterialSlide = false;//为了省bitmap内存开销 ,而优化的 ,只针对缩略图
    private boolean isPlaceholder = true;
    private boolean hasBg = false;
    private String nowChooseBjPath = "";
    private Paint rPaint;
    private String imageCompoundPath;//合成后的地址
    private boolean isGenerateMatrix = false; //是否已经生成了matrix

    public MediaUiModel(String folder, JSONObject ui, Context context, AssetDelegate delegate, Size size,String startFrame,String endFrame, String slowLevel) throws JSONException {
        super(folder, ui, delegate, size);
        this.context = context;
        mBitmap = VEBitmapFactory.decodeResource(context.getResources(), R.drawable.default_bj);
        mDuration = ui.getInt("duration");
        p = getIntArray(ui.getJSONArray("p"));
        a = getIntArray(ui.getJSONArray("a"));
        t = ui.getDouble("t");
        r = ui.getInt("r");
        s = getFloatArray(ui.getJSONArray("s"));
        mInitPaint = new Paint();
        mInitPaint.setAntiAlias(true);
        mInitPaint.setFilterBitmap(true);
        mInitPaint.setAlpha((int) (t * 255));
        rPaint = new Paint();
        rPaint.setStyle(Paint.Style.STROKE);//空心矩形框
        rPaint.setColor(Color.WHITE);
        mInverseMatrix = new Matrix();
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.set(new PointF(a[0], a[1]), new PointF(p[0], p[1]), new PointF(s[0], s[1]), r);
        mMatrix = affineTransform.getMatrix();
        mMatrixBj = affineTransform.getMatrix();
        float scale = size.getWidth() / (float) mBitmap.getWidth();
        mMatrix.postScale(scale, scale);
        if(!TextUtils.isEmpty(startFrame)){
            //来自于视频放慢，startFrame只要有值，那么一定放慢
            hasVideoSlow=true;
            this.startFrame=startFrame;
            this.endFrame=endFrame;
            this.slowLevel=slowLevel;
        }



    }

    @Override
    public void draw(Canvas canvas, int activeLayer) {
        if (nowViewIsVisible) {
            if (hasBg && bgBitmap != null) { //绘制背景
                canvas.drawBitmap(bgBitmap, mMatrixBj, null);
            }
            mBitmap = getSaveBitmap();
            canvas.drawBitmap(mBitmap, mMatrix, null);
            if (drawBitmapBj != null) {
                canvas.drawBitmap(drawBitmapBj, 0, 0, null);
            }
        }
    }






    /**
     * description ：拿到保存的bitmap
     * date: ：2019/6/17 17:28
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public Bitmap getSaveBitmap() {
        if (mBitmap == null) {
            Log.d("OOM", "bitmap=null");
            if (mImageviewPathOrigin == null || mImageviewPathOrigin.equals("")) {
                mBitmap = VEBitmapFactory.decodeResource(context.getResources(), R.drawable.default_bj);
            } else {
                File file = new File(mImageviewPathOrigin); //有些错误的地址
                if (file.exists()) {
                    Bitmap bp = getSmallBmpFromFile(mImageviewPathOrigin, size.getWidth(), size.getHeight());
                    if (bp != null) {
                        try {
                            mBitmap = PhotoBitmapUtils.amendRotatePhoto(mImageviewPathOrigin, context, bp);
                        } catch (Exception e) {
                            mBitmap = bp;
                            e.printStackTrace();
                        }
                    }
                } else {
                    mBitmap = VEBitmapFactory.decodeResource(context.getResources(), R.drawable.default_bj);
                }
            }
            Log.d("OOM", "bitmap!=null");
            return mBitmap;
        }
        return mBitmap;
    }


    public int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void isShow(boolean show) {  //当前是否显示
        this.nowViewIsVisible = show;
        if (show) {
            if (hasBg) {  //先生成背景，在生成滤镜
                bgBitmap = getSmallBmpFromFile(nowChooseBjPath, 360, 540); //NullPointerException
                countMatrixBj(bgBitmap);
            }
            if (mBitmap == null) {
                Log.d("OOM", "mBitmap=null");
                if (mIsVideo) {
                    File file = new File(mVideoPath);
                    if (file.exists()) {
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        FileInputStream inputStream = null; //bug 解决
                        try {
                            inputStream = new FileInputStream(new File(mVideoPath).getAbsolutePath());
                            retriever.setDataSource(inputStream.getFD());
                            mBitmap = retriever.getFrameAtTime(); //获得第一针图片
//                            mBitmap=compressBp(mBitmap);
                            if (!isGenerateMatrix) {
                                countMatrixForVideo(mBitmap);
                                isGenerateMatrix = true;
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        Bitmap bp = getSmallBmpFromFile(mImageviewPathOrigin, size.getWidth(), size.getHeight()); //NullPointerException
                        if (!isGenerateMatrix) {
                            countMatrix(bp, mImageviewPathOrigin, true);
                            isGenerateMatrix = true;
                        } else {
                            if (bp != null) {
                                try {
                                    mBitmap = PhotoBitmapUtils.amendRotatePhoto(mImageviewPathOrigin, context, bp);
                                } catch (Exception e) {
                                    mBitmap = bp;
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.d("OOM", "mBitmap!=null???");
        } else {
            Log.d("OOM", "回收");
            recycleBitmap();
        }

    }


    /**
     * description ：调用显示方法，重新渲染滤镜，最后回调是否生成成功
     * date: ：2019/11/27 16:28
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    private renderFilterSuccess callback;

    public void isShow(boolean show, renderFilterSuccess callback) {
        this.callback = callback;
        this.nowViewIsVisible = show;
        if (show) {
            if (hasBg) {  //先生成背景，在生成滤镜
                bgBitmap = getSmallBmpFromFile(nowChooseBjPath, 360, 540); //NullPointerException
                countMatrixBj(bgBitmap);
            }
            if (mBitmap == null) {
                Log.d("OOM", "mBitmap=null");
                if (mIsVideo) {
                    File file = new File(mVideoPath);
                    if (file.exists()) {
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        FileInputStream inputStream = null; //bug 解决
                        try {
                            inputStream = new FileInputStream(new File(mVideoPath).getAbsolutePath());
                            retriever.setDataSource(inputStream.getFD());
                            mBitmap = retriever.getFrameAtTime(); //获得第一针图片
//                            mBitmap=compressBp(mBitmap);
                            if (!isGenerateMatrix) {
                                countMatrixForVideo(mBitmap);
                                isGenerateMatrix = true;
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        Bitmap bp = getSmallBmpFromFile(mImageviewPathOrigin, size.getWidth(), size.getHeight()); //NullPointerException
                        if (!isGenerateMatrix) {
                            countMatrix(bp, mImageviewPathOrigin, true);
                            isGenerateMatrix = true;
                        } else {
                            if (bp != null) {
                                try {
                                    mBitmap = PhotoBitmapUtils.amendRotatePhoto(mImageviewPathOrigin, context, bp);
                                } catch (Exception e) {
                                    mBitmap = bp;
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                callback.isSuccess(true);
            }
            Log.d("OOM", "mBitmap!=null???");
        } else {
            Log.d("OOM", "回收");
            recycleBitmap();
        }

    }


//    /**
//     * description ：bitmap 压缩
//     * date: ：2019/11/27 15:22
//     * author: 张同举 @邮箱 jutongzhang@sina.com
//     */
//    private Bitmap compressBp(Bitmap bp){
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bp.compress(Bitmap.CompressFormat.JPEG, 10, baos);
//        byte[] bytes = baos.toByteArray();
//        return  BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//    }


    public void recycleBitmap() {
        if (mBitmap != null) {
            GlideBitmapPool.putBitmap(mBitmap);
            mBitmap = null;
        }

        if (bgBitmap != null && !bgBitmap.isRecycled()) {
            GlideBitmapPool.putBitmap(bgBitmap);
            bgBitmap = null;
        }


    }




    private Bitmap getSmallBmpFromFile(String filePath, int targetW, int targetH) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(filePath);
                return VEBitmapFactory.decodeFileDescriptor(fis.getFD(), targetW, targetH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void scroll(float distanceX, float distanceY) {
        mMatrix.postTranslate(-distanceX, -distanceY);
        isVideoSlide = true;
        isReplaceMaterial = true;
        ImageHasVariation = false;
        isMaterialSlide = true;
    }

    @Override
    public void scale(float sx, float sy, float px, float py) {
        Log.d("scale", "scaleMediaUi");
        mMatrix.postScale(sx, sx, px, py);
        isVideoSlide = true;
        isReplaceMaterial = true;
        isMaterialSlide = true;
        ImageHasVariation = false;
    }

    @Override
    public void rotate(float degrees, float px, float py) {
        mMatrix.postRotate(degrees, px, py);
        isVideoSlide = true;
        isReplaceMaterial = true;
        ImageHasVariation = false;
        isMaterialSlide = true;
    }

    @Override
    public boolean isPointInside(PointF point) {
        mMatrix.invert(mInverseMatrix);
        float[] dst = new float[2];
        mInverseMatrix.mapPoints(dst, new float[]{point.x, point.y});
        if (mBitmap != null) {
            return dst[0] >= 0 && dst[0] <= mBitmap.getWidth() && dst[1] >= 0 && dst[1] <= mBitmap.getHeight();
        } else {
            return false;
        }
    }

    @Override
    public void singleTap(GroupModel groupModel) {
        mDelegate.pickMedia(this);
    }

    @Override
    public String getSnapPath(String folder) {
        if (!mIsVideo) {
            if (imageCompoundPath != null && !imageCompoundPath.equals("") && ImageHasVariation) {  //没有变动过就不需要重新再合成这张图片
                Log.d("OOM", "复用了");
                return imageCompoundPath;
            } else {
                try {
                    Log.d("OOM", "没有复用,是否会回收" + !nowViewIsVisible);
//                    Bitmap bitmap = Bitmap.createBitmap(size.getWidth(), size.getHeight(), Bitmap.Config.ARGB_8888);
                    Bitmap bitmap = GlideBitmapPool.getBitmap(size.getWidth(), size.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    mBitmap = getSaveBitmap();
                    if (!isGenerateMatrix) {
                        countMatrix(mBitmap, mImageviewPathOrigin, false);
                        isGenerateMatrix = true;
                    }
                    canvas.drawBitmap(mBitmap, mMatrix, mPaint);
                    final String path = folder + File.separator + UUID.randomUUID() + ".png";
                    saveBitmapToPath(bitmap, path, new saveBitmapState() {
                        @Override
                        public void succeed(boolean isSucceed) {
                            Log.d("OOM", "得到了滤镜地址");
                            imageCompoundPath = path;
                            ImageHasVariation = true;
                            if (!nowViewIsVisible) {
                                recycleBitmap();
                            }
                        }
                    });
                    return path;
                } catch (Exception e) {
                    e.printStackTrace();
                    return "";
                }
            }

        } else {
            return mVideoPath;
        }

    }

    @Override
    public String getSnapPathForKeep(String folder) {
        if (!mIsVideo) {
            if (isPlaceholder) { //用户还没替换过素材
                return "";
            }
            if (mImageviewPathOrigin != null && mImageviewPathOrigin.contains("default_bj.png")) {  //之后会在保存的时候自动填满
                return "";
            }
            if (imageCompoundPath != null && !imageCompoundPath.equals("") && ImageHasVariation) {  //没有变动过就不需要重新再合成这张图片
                return imageCompoundPath;
            } else {
                try {
                    Bitmap bitmap = GlideBitmapPool.getBitmap(size.getWidth(), size.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    mBitmap = getSaveBitmap();

                    if (!isGenerateMatrix) {
                        countMatrix(mBitmap, mImageviewPathOrigin, false);
                        isGenerateMatrix = true;
                    }
                    canvas.drawBitmap(mBitmap, mMatrix, mPaint);
                    final String path = folder + File.separator + UUID.randomUUID() + ".png";
                    saveBitmapToPath(bitmap, path, new saveBitmapState() {
                        @Override
                        public void succeed(boolean isSucceed) {
                            Log.d("succeed", "isSucceedForKeep=" + isSucceed);
                            imageCompoundPath = path;
                            ImageHasVariation = true;
                            if (!nowViewIsVisible) {
                                recycleBitmap();
                            }
                        }
                    });
                    return path;
                } catch (Exception e) {
                    e.printStackTrace();
                    return "";
                }
            }

        } else {
            return mVideoPath;
        }
    }


    public String getSnapPathForVideoThumb(String folder) {
        Bitmap bitmap = GlideBitmapPool.getBitmap(size.getWidth(), size.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        mBitmap = getSaveBitmap();
        canvas.drawBitmap(mBitmap, mMatrix, mPaint);
        String path = folder + File.separator + UUID.randomUUID() + ".png";
        saveBitmapToPath(bitmap, path, new saveBitmapState() {
            @Override
            public void succeed(boolean isSucceed) {

            }
        });
        return path;
    }


    @Override
    public String getOriginPath(String folder) {
        if (!mIsVideo) {
            return mImageviewPathOrigin;
        } else {
            return mVideoPathOrigin;
        }
    }


    /**
     * description ：是否存在占位图，只针对mediaUiModel
     * date: ：2019/10/30 14:20
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    @Override
    public boolean hasPlaceholder() {
        return isPlaceholder;
    }


    @Override
    public boolean hasChooseFilter(int filterPosition) {
        recycleBitmap();
        ImageHasVariation = false;  //需要重新渲染，不能复用之前渲染完成的图片
        this.filterPosition = filterPosition;
        return false;
    }

    @Override
    public boolean hasChooseBg(String path) {

        nowChooseBjPath = path;
        if (path != null && !path.equals("")) {
            hasBg = true;
        } else {
            hasBg = false;
        }
        return false;
    }


    /**
     * description ：视频取截取的视频地址
     * date: ：2019/6/24 18:13
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public String getOriginPathForThumb(String folder) {
        if (!mIsVideo) {
            return mImageviewPathOrigin;
        } else {
            return mVideoPath;
        }
    }


    /**
     * @return 素材时长，单位为帧
     */
    public int getDuration() {
        return mDuration;
    }

    public void setImageAsset(String path, Context context) {
        Log.d("setImageAsset", "path=" + path);
        if (!path.equals(SxveConstans.default_bg_path)) {
            isPlaceholder = false;
        }
        File file = new File(path);
        if (file.exists()) {
            mMatrix.reset();
            isGenerateMatrix = false;
            mImageviewPathOrigin = path;
            ImageHasVariation = false;
            mIsVideo = false;
            if (nowViewIsVisible) {
                try {
                    Bitmap bp = getSmallBmpFromFile(path, size.getWidth(), size.getHeight());
                    //      Bitmap bp = BitmapFactory.decodeFile(path);
                    countMatrix(bp, path, true);
                    isGenerateMatrix = true;
                } catch (OutOfMemoryError e) {
                    Toast.makeText(context, "图片太大", Toast.LENGTH_SHORT).show();
                }
            }

            if (!nowViewIsVisible) {
                recycleBitmap();
            }
        }


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
                    mBitmap = PhotoBitmapUtils.amendRotatePhoto(path, context, bp);
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


    private void countMatrixForVideo(Bitmap bitmap) {
        if (bitmap != null) {
            int widthSize = size.getWidth();
            float scale = widthSize / (float) bitmap.getWidth();
            mMatrix.postScale(scale, scale);
            double tranY = mBitmap.getHeight() * scale;  //设置居中
            if (size.getHeight() - tranY > 0) {
                int needY = (int) (size.getHeight() - tranY) / 2;
                mMatrix.postTranslate(0, needY);
            } else {
                int needY = (int) (tranY - size.getHeight()) / 2;
                mMatrix.postTranslate(0, -needY);
            }
        }
    }


    private void countMatrixBj(Bitmap bp) {
        if (bp != null) {
            int widthSize = size.getWidth();
            mMatrixBj.reset();
            float scale = widthSize / (float) bp.getWidth();
            Log.d("mMatrix", "mBitmap=getWidth" + (float) bp.getWidth());
            Log.d("mMatrix", "scale=" + scale);
            mMatrixBj.postScale(scale, scale);
            double tranY = bp.getHeight() * scale;
            if (size.getHeight() - tranY > 0) {
                int needY = (int) (size.getHeight() - tranY) / 2;
                Log.d("mMatrix", "needY=" + needY);
                mMatrixBj.postTranslate(0, needY);
            } else {
                int needY = (int) (tranY - size.getHeight()) / 2;
                Log.d("mMatrix", "needY=" + needY);
                mMatrixBj.postTranslate(0, -needY);
            }

        }
    }


    public void setVideoPath(String path) {
        isPlaceholder = false;
        isGenerateMatrix = false;
        mVideoPath = path;
        mIsVideo = true;
        mMatrix.reset();
        if (nowViewIsVisible) {
            try {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                FileInputStream inputStream = new FileInputStream(new File(path).getAbsolutePath());
                retriever.setDataSource(inputStream.getFD());//设置数据源为该文件对象指定的绝对路径
                mBitmap = retriever.getFrameAtTime(); //获得第一针图片
//                mBitmap=compressBp(mBitmap);
                countMatrixForVideo(mBitmap);
                isGenerateMatrix = true;
            } catch (Exception e) {
                mIsVideo = false;
                mVideoPath = "";
                e.printStackTrace();
            }
        }


        if (!nowViewIsVisible) {
            recycleBitmap();
        }
    }


    public void setVideoPathOrigin(String path) {
        mVideoPathOrigin = path;
    }


    public String getVideoPathOrigin() {
        return mVideoPathOrigin;
    }


    public Matrix getmMatrix() {
        if (!isGenerateMatrix && mIsVideo) {
            if (mBitmap == null) {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(new File(mVideoPath).getAbsolutePath()); //bug 解决
                    retriever.setDataSource(inputStream.getFD());
//                    retriever.setDataSource(mVideoPath);
                    mBitmap = retriever.getFrameAtTime(); //获得第一针图片
//                    mBitmap=compressBp(mBitmap);
                    countMatrixForVideo(mBitmap);
                    isGenerateMatrix = true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                countMatrixForVideo(mBitmap);
                isGenerateMatrix = true;
            }
            if (!nowViewIsVisible) {
                recycleBitmap();
            }
        }
        return mMatrix;
    }


    public boolean isConversion() {
        return isMaterialSlide;
    }

    public void setIsConversion(boolean isMaterialSlide) {
        this.isMaterialSlide = isMaterialSlide;

    }


    public boolean isVideoType() {
        return mIsVideo;
    }





    /**
     * description ：提示重新渲染
     * date: ：2019/11/25 14:19
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public void againVariation() {
        ImageHasVariation = false;
    }

}
