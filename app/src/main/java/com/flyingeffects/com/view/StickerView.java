package com.flyingeffects.com.view;

import android.app.Service;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.commonlyModel.GetVideoCover;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.manager.BitmapManager;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.interfaces.TickerAnimated;
import com.flyingeffects.com.utils.BitmapUtil;
import com.flyingeffects.com.utils.BitmapUtils;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.MeasureTextUtils;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.utils.screenUtil;
import com.flyingeffects.com.view.animations.CustomMove.AnimType;
import com.flyingeffects.com.view.lansongCommendView.RectUtil;
import com.flyingeffects.com.view.lansongCommendView.StickerItemOnDragListener;
import com.flyingeffects.com.view.lansongCommendView.StickerItemOnitemclick;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 文本贴图处理控件
 *
 * @author savion  区别于蓝松的StickerView
 * @date 2019/12/19
 */
public class StickerView<D extends Drawable> extends View implements TickerAnimated {

    public static final int CODE_STICKER_TYPE_NORMAL = 0;
    public static final int CODE_STICKER_TYPE_TEXT = 1;
    public static final int CODE_STICKER_TYPE_FLASH_PIC = 2;

    private static final String TAG = "StickerView";

    /**
     * 高光
     */
    private static final int[] COLORS = {Color.parseColor("#00000000"), Color.parseColor("#EEEEEE"),
            Color.parseColor("#00000000"), Color.parseColor("#EEEEEE"), Color.parseColor("#ffffff"),
            Color.parseColor("#00000000"), Color.parseColor("#EEEEEE"), Color.parseColor("#EEEEEE"), Color.parseColor("#00000000")};


    private int mStickerType = CODE_STICKER_TYPE_NORMAL;

    private boolean isFromStickerAnim = false;

    private float mMeasureWidth = 300;
    private float mMeasureHeight = 300;
    private String stickerText = "输入文本";

    /**
     * 选择字体的名字
     */
    private String textStyleTitle;
    /**
     * 选择边框的的名字
     */
    private String textFrameTitle;
    /**
     * 选择字体效果名字
     */
    private String textEffectTitle;
    /**
     * 定义对象依次存放每一个字符
     */
    private List<String> listFontList = new ArrayList<>();
    /**
     * 文字格式地址
     */
    private String TypefacePath;
    /**
     * 文字图片地址
     */
    private String getTypefaceBitmapPath;

    /**
     * 文字图片
     */
    private Bitmap bpForTextBj;

    /**
     * 是否设置花纹
     */
    public boolean mOpenThePattern = false;
    /**
     * 测试的文字边纹
     */
    private Bitmap bpTestTextBj;
    /**
     * 测试的文字花纹边纹地址
     */
    private String textFramePath;

    /**
     * 有花纹情况下文字的间距
     */
    private int letterSpacingSize = 1;

    /**
     * 选择的时背景效果
     */
    private boolean isChooseTextBjEffect = false;
    /**
     * 文字背景矩形变阵
     */
    Matrix matrixForBitmapShader = new Matrix();
    private float mTextScale;
    /**
     * 不包含动画view的ID
     */
    private int stickerNoIncludeAnimId;

    /**
     * 贴纸名字
     */
    private String downStickerTitle;

    /**
     * 当前贴纸选择的动画
     */
    public AnimType ChooseAnimId;

    /**
     * 镜像翻转后的bitmap
     */
    private Bitmap mMirrorBitmap;

    private boolean mIsMirror = false;

    /**
     * 镜像翻转数据分别保存
     */
    private Bitmap mClipMirrorBitmap;
    private Bitmap mOriginalMirrorBitmap;
    private String mClipMirrorPath;
    private String mOriginalMirrorPath;

    /**
     * 当前素材是否是视频
     */
    private boolean NowMaterialIsVideo = false;

    public static final int STICKER_BTN_HALF_SIZE = 30;
    // 控件的几种模式
    /**
     * 正常
     */
    public static final int IDLE_MODE = 2;

    /**
     * 移动模式
     */
    public static final int MOVE_MODE = 3;

    /**
     * 左上角动作
     */
    public static final int LEFT_TOP_MODE = 6;

    /**
     * 左下角动作
     */
    public static final int LEFT_BOTTOM_MODE = 7;
    /**
     * 右上角动作
     */
    public static final int RIGHT_TOP_MODE = 8;
    /**
     * 右下角动作
     */
    public static final int RIGHT_BOTTOM_MODE = 9;

    /**
     * 右中间动作
     */
    public static final int RIGHT_CENTER_MODE = 10;

    /**
     * 双指动作
     */
    public static final int NEW_POINTER_DOWN_MODE = 11;

    /**
     * 右侧滑动动作
     */
    public static final int RIGHT_MODE = 12;

    /**
     * 左侧保存动作
     */
    public static final int LEFT_MODE = 14;

    public static final int ONCLICK_MODE = 13;

    public boolean isOpenVoice = false;

    /**
     * 是否是第一次添加的贴纸
     */
    private boolean isFirstAddSticker = false;

    /**
     * 移动距离记录
     */
    public int layoutX = 0;
    public int layoutY = 0;

    private float moveX;
    private float moveY;
    /**
     * 中心
     */
    private PointF center = new PointF(0, 0);

    /**
     * 旋转角度
     */
    public float mRotateAngle = 0;

    /**
     * 缩放比
     */
    public float mScale = 1;
    private Paint debugPaint = new Paint();
    private TextPaint textPaint = new TextPaint();
    private TextPaint whitePaint = new TextPaint();
    private TextPaint shadowPaint = new TextPaint();
    private Paint mHelpPaint = new Paint();
    private RectF mHelpBoxRect = new RectF();

    /**
     * 图像透明化要用到的遮罩
     */
    private Paint mHelpDstPaint = new Paint();
    /**
     * 按钮大小
     */
    private Rect leftTopRect = new Rect();
    private Rect leftBottomRect = new Rect();
    private Rect rightBottomRect = new Rect();
    private Rect rightCenterRect = new Rect();
    private Rect rightTopRect = new Rect();
    private Rect rightRect = new Rect();
    private Rect leftRect = new Rect();

    /**
     * 钮位置
     */
    private RectF leftTopDstRect = new RectF();
    private RectF rightBottomDstRect = new RectF();
    private RectF rightCenterDstRect = new RectF();
    private RectF rightTopDstRect = new RectF();
    private RectF leftBottomDstRect = new RectF();
    private RectF rightDstRect = new RectF();
    private RectF leftDstRect = new RectF();
    private RectF textRect = new RectF();
    private Drawable leftTopBitmap;
    private Drawable rightTopBitmap;
    private Drawable leftBottomBitmap;
    private Drawable rightBottomBitmap;
    private Drawable rightCenterBitmap;
    private Drawable rightBitmap;
    private Drawable leftBitmap;
    private int frameColor = Color.WHITE;
    private float frameWidth = 1;
    private int rotateLocation = RIGHT_BOTTOM_MODE;
    private int mCurrentMode = IDLE_MODE;

    /**
     * 限制右侧滑动按钮在轨道之内
     */
    private int mRightLimited = 0;

    /**
     * 右侧滑动按钮距离底部的距离百分比
     */
    private float mRightOffsetPercent = 0f;

    /**
     * 右侧滑动按钮距离底部的距离
     */
    private float mRightOffset = 0f;

    /**
     * 是否允许辅助水平
     */
    private boolean enableAutoAdjustDegree = true;

    /**
     * 是否允许辅助居中
     */
    private boolean enableAutoAdjustCenter = false;
    private float lastX = 0;
    private float lastY = 0;

    /**
     * 辅助线颜色
     */
    private int guideLineColor = Color.WHITE;
    /**
     * 辅助线宽度
     */
    private float guideLineWidth = 5;
    /**
     * 是否显示辅助线
     */
    private boolean guideLineShow = true;
    /**
     * 当touch时显示辅助线
     */
    private boolean guideLineShowOntouch = false;
    private Vibrator vibrator;
    /**
     * 是否显示
     */
    private boolean frameShow = false;

    /**
     * 双指
     */
    float dx0 = 0f;
    float dy0 = 0f;

    private Bitmap originalBitmap;

    private int tag;

    private float originalScale;

    private int originalBitmapWidth;

    private int originalBitmapHeight;

    /**
     * 当前是否是子动画view
     */
    private boolean isFromAnim = false;

    /**
     * 边框自动消息时长
     */
    private static final long AUTO_FADE_FRAME_TIMEOUT = 5000;

    /**
     * 显示边框事件ID
     */
    private static final int SHOW_FRAME = 1;
    /**
     * 消失边框事件ID
     */
    private static final int DISMISS_FRAME = 2;

//    private StickerListener tickerListener;

    private Bitmap mMaskBitmap;


//    public StickerListener getTickerListener() {
//        return tickerListener;
//    }

    private StickerItemOnitemclick callback;
    private StickerItemOnDragListener dragCallback;

    /**
     * 视频原图地址
     */
    private String originalPath;

    private isFromCopy fromCopy;

    /**
     * 裁剪后地址
     */
    private String clipPath;

    private boolean isFromAlbum = false;

    /**
     * 文字paint
     */
    private Paint mTextPaint;

    /**
     * 没有选择效果之前的样式
     */
    private Paint mTextPaint2;
    /**
     * 高光
     */
    private Paint mPaintShadow;
    private float mTextSize = 100;
    private float paintWidth = 50;
    /**
     * 贴纸的时间轴 起止时间
     */
    private long showStickerStartTime;
    private long showStickerEndTime;
    private boolean mIsChange = false;


    public Bitmap getClipMirrorBitmap() {
        return mClipMirrorBitmap;
    }

    public void setClipMirrorBitmap(Bitmap clipMirrorBitmap) {
        mClipMirrorBitmap = clipMirrorBitmap;
    }

    public Bitmap getOriginalMirrorBitmap() {
        return mOriginalMirrorBitmap;
    }

    public void setOriginalMirrorBitmap(Bitmap originalMirrorBitmap) {
        mOriginalMirrorBitmap = originalMirrorBitmap;
    }

    public boolean isMirror() {
        return mIsMirror;
    }

    public void setMirror(boolean mirror) {
        mIsMirror = mirror;
    }

    public StickerView(Context context) {
        this(context, null);
    }

    public StickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerView(Context context, int stickerType) {
        this(context);
        mStickerType = stickerType;
        initTextPainter(context);
        colors.add("#F4F4F4");
        colors.add("#FFFFFF");
    }

    public StickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handler = new GestureHandler();
        targer = null;
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attributeSet) {
//        setupGestureListeners();
        TypedArray mTypedArray = context.obtainStyledAttributes(attributeSet,
                R.styleable.StickerView);

        leftTopBitmap = mTypedArray.getDrawable(R.styleable.StickerView_sv_left_top_drawable);
        leftBottomBitmap = mTypedArray.getDrawable(R.styleable.StickerView_sv_left_bottom_drawable);
        rightBitmap = mTypedArray.getDrawable(R.styleable.StickerView_sv_right_drawable);
        leftBitmap = mTypedArray.getDrawable(R.styleable.StickerView_sv_left_drawable);
        rightTopBitmap = mTypedArray.getDrawable(R.styleable.StickerView_sv_right_top_drawable);
        rightBottomBitmap = mTypedArray.getDrawable(R.styleable.StickerView_sv_right_bottom_drawable);
        rotateLocation = mTypedArray.getInteger(R.styleable.StickerView_sv_contron_location, RIGHT_BOTTOM_MODE);
        guideLineColor = mTypedArray.getColor(R.styleable.StickerView_sv_guide_line_color, Color.WHITE);
        guideLineWidth = mTypedArray.getDimension(R.styleable.StickerView_sv_guide_line_width, 5f);
        enableAutoAdjustDegree = mTypedArray.getBoolean(R.styleable.StickerView_sv_auto_degree, true);
        enableAutoAdjustCenter = mTypedArray.getBoolean(R.styleable.StickerView_sv_auto_center, false);
        frameColor = mTypedArray.getColor(R.styleable.StickerView_sv_frame_color, getResources().getColor(R.color.white));
        frameWidth = mTypedArray.getDimension(R.styleable.StickerView_sv_frame_width, 1);
        mTypedArray.recycle();

        debugPaint.setColor(Color.parseColor("#000000"));
        debugPaint.setStrokeWidth(20);
        debugPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        textPaint.setTextSize(40);
        textPaint.setColor(Color.RED);
        shadowPaint.setTextSize(40);
        shadowPaint.setColor(Color.BLUE);

        whitePaint.setColor(Color.WHITE);
        whitePaint.setTextSize(40);
        whitePaint.getTextBounds("预览后人物可动", 0, "预览后人物可动".length(), bounds);
        initFrameBitmap();

        mHelpPaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
        mHelpPaint.setColor(frameColor);
        mHelpPaint.setStyle(Paint.Style.STROKE);
        mHelpPaint.setStrokeWidth(screenUtil.dip2px(BaseApplication.getInstance(), frameWidth));

        vibrator = (Vibrator) getContext().getSystemService(Service.VIBRATOR_SERVICE);
    }

    /**
     * 文字相关的初始化
     */
    private void initTextPainter(Context context) {
        mTextPaint = new Paint();
        mPaintShadow = new Paint();
        mTextPaint.setColor(Color.parseColor("#F4F4F4"));
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setStrokeWidth(paintWidth);
        mTextPaint.setAntiAlias(true);
        mTextPaint2 = new Paint();
        mTextPaint2.setColor(Color.parseColor("#ffffff"));
        mTextPaint2.setTextSize(mTextSize);
        mTextPaint2.setStrokeWidth(paintWidth);
        mTextPaint2.setAntiAlias(true);
        mPaintShadow.setColor(Color.parseColor("#ffffff"));
        mPaintShadow.setTextSize(mTextSize);
        mPaintShadow.setStrokeWidth(paintWidth / (float) 3);
        mPaintShadow.setAntiAlias(true);

    }

    static Bitmap makeSrc(int w, int h, float percent) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        int graLine = (int) (h - h * percent);

        LinearGradient gradient = new LinearGradient(w, graLine, w, graLine + 40,
                Color.parseColor("#ffffff"), Color.TRANSPARENT, Shader.TileMode.CLAMP);
        p.setShader(gradient);
        c.drawRect(0, 0, w, h, p);
        return bm;
    }

    static Bitmap makeDst(int w, int h, Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(w, h,
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    /**
     * 框架上按钮初始化
     */
    private void initFrameBitmap() {

        if (leftTopBitmap != null) {
            leftTopRect.set(0, 0, leftTopBitmap.getIntrinsicWidth(),
                    leftTopBitmap.getIntrinsicHeight());
            //相当于STICKER_BTN_HALF_SIZE*2 左移运算符
            leftTopDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }

        if (rightBottomBitmap != null) {
            rightBottomRect.set(0, 0, rightBottomBitmap.getIntrinsicWidth(),
                    rightBottomBitmap.getIntrinsicHeight());
            rightBottomDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }

        if (rightBitmap != null) {
            rightRect.set(0, 0, rightBitmap.getIntrinsicWidth(),
                    rightBitmap.getIntrinsicHeight());
            rightDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }

        if (leftBitmap != null) {
            leftRect.set(0, 0, leftBitmap.getIntrinsicWidth(), leftBitmap.getIntrinsicHeight());
            leftDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }

        if (rightCenterBitmap != null) {
            rightCenterRect.set(0, 0, rightCenterBitmap.getIntrinsicWidth(),
                    rightCenterBitmap.getIntrinsicHeight());
            rightCenterDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }

        if (leftBottomBitmap != null) {
            leftBottomRect.set(0, 0, leftBottomBitmap.getIntrinsicWidth(), leftBottomBitmap.getIntrinsicHeight());
            leftBottomDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }

        if (rightTopBitmap != null) {
            rightTopRect.set(0, 0, rightTopBitmap.getIntrinsicWidth(), rightTopBitmap.getIntrinsicHeight());
            rightTopDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }

    }


    @Override
    public void start() {
        if (currentDrawable != null && currentDrawable instanceof GifDrawable) {
            if (!isRunning()) {
                currentDrawable.setCallback(this);
                ((GifDrawable) currentDrawable).startFromFirstFrame();
            }
            isRunning = true;
        }
    }

    @Override
    public void pause() {
        //暂停
        if (currentDrawable != null && currentDrawable instanceof GifDrawable) {
            ((GifDrawable) currentDrawable).stop();
        }
    }

    @Override
    public void stop() {
        LogUtil.d("oom", "-----------------------StickerListenerstop------------------------------");
        if (currentDrawable != null && currentDrawable instanceof GifDrawable) {
            pause();
            currentDrawable.setCallback(null);
            ((GifDrawable) currentDrawable).recycle();
            currentDrawable = null;
        } else {
            currentDrawable = null;
            invalidate();
        }
        isRunning = false;
        targer = null;

//        Runtime.getRuntime().gc();
    }

    private boolean isRunning = false;

    @Override
    public boolean isRunning() {
        if (currentDrawable != null && currentDrawable instanceof GifDrawable) {
            return isRunning = ((GifDrawable) currentDrawable).isRunning();
        }
        return true;
    }

    /**
     * 设置辅助线颜色
     *
     * @param guideLineColor
     */
    public void setGuideLineColor(int guideLineColor) {
        if (this.guideLineColor != guideLineColor) {
            this.guideLineColor = guideLineColor;
            if (isGuideLineShow()) {
                invalidate();
                LogUtil.d("oom", "-----------------------setGuideLineColor------------------------------");
            }
        }
    }

    /**
     * 当前是否显示辅助线
     */
    public boolean isGuideLineShow() {
        return guideLineShow;
    }

    public void setNowMaterialIsVideo(boolean isVideo) {
        NowMaterialIsVideo = isVideo;
    }

    public void setFrameColor(int frameColor) {
        this.frameColor = frameColor;
    }

    public float getFrameWidth() {
        return frameWidth;
    }

    public void setFrameWidth(float frameWidth) {
        this.frameWidth = frameWidth;
    }

    public int getFrameColor() {
        return frameColor;
    }

    public void setRotateLocation(int rotateLocation) {
        this.rotateLocation = rotateLocation;
    }

    public void setRightBottomBitmap(Drawable rightBottomBitmap) {
        if (rightBottomBitmap != null) {
            this.rightBottomBitmap = rightBottomBitmap;
            rightBottomRect.set(0, 0, rightBottomBitmap.getIntrinsicWidth(),
                    rightBottomBitmap.getIntrinsicHeight());
            rightBottomDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }
    }

    public void setRightBitmap(Drawable rightBitmap) {
        if (rightBitmap != null) {
            this.rightBitmap = rightBitmap;
            rightRect.set(0, 0, rightBitmap.getIntrinsicWidth(),
                    rightBitmap.getIntrinsicHeight());
            rightDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }
    }

    public void setLeftBitmap(Drawable leftBitmap) {
        if (leftBitmap != null) {
            this.leftBitmap = leftBitmap;
            leftRect.set(0, 0, leftBitmap.getIntrinsicWidth(),
                    leftBitmap.getIntrinsicHeight());
            leftDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }
    }

    public void setLeftBitmapNoSave() {
        leftBitmap = null;
        invalidate();
    }

    public void setRightCenterBitmap(Drawable rightCenterBitmap) {
        if (rightCenterBitmap != null) {
            this.rightCenterBitmap = rightCenterBitmap;
            rightCenterRect.set(0, 0, rightCenterBitmap.getIntrinsicWidth(),
                    rightCenterBitmap.getIntrinsicHeight());
            rightCenterDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }
    }

    public void setRightCenterBitmapForChangeIcon(Drawable rightCenterBitmap) {
        if (rightCenterBitmap != null) {
            this.rightCenterBitmap = rightCenterBitmap;
        }
    }


    public void setLeftTopBitmap(Drawable leftTopBitmap) {
        if (leftTopBitmap != null) {
            this.leftTopBitmap = leftTopBitmap;
            leftTopDstRect.set(0, 0, leftTopBitmap.getIntrinsicWidth(),
                    leftTopBitmap.getIntrinsicHeight());
            leftTopDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }
    }

    public void setLeftBottomBitmap(Drawable leftBottomBitmap) {
        if (leftBottomBitmap != null) {
            this.leftBottomBitmap = leftBottomBitmap;
            leftBottomDstRect.set(0, 0, leftBottomBitmap.getIntrinsicWidth(),
                    leftBottomBitmap.getIntrinsicHeight());
            leftBottomDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }
    }

    public void setRightTopBitmap(Drawable rightTopBitmap) {
        if (rightTopBitmap != null) {
            this.rightTopBitmap = rightTopBitmap;
            rightTopDstRect.set(0, 0, rightTopBitmap.getIntrinsicWidth(),
                    rightTopBitmap.getIntrinsicHeight());
            rightTopDstRect = new RectF(0, 0, STICKER_BTN_HALF_SIZE << 1,
                    STICKER_BTN_HALF_SIZE << 1);
        }
    }

    public void setOnitemClickListener(StickerItemOnitemclick callback) {
        this.callback = callback;
    }

    public void setOnItemDragListener(StickerItemOnDragListener dragCallback) {
        this.dragCallback = dragCallback;
    }

    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }

    /**
     * 控制边框的显隐
     */
    private final GestureHandler handler;

    private class GestureHandler extends Handler {

        GestureHandler() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_FRAME:
                    if (!frameShow) {
                        frameShow = true;
                        invalidate();
                    }
                    break;
                case DISMISS_FRAME:
                    if (frameShow) {
                        frameShow = false;
                        invalidate();
                    }
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        super.invalidateDrawable(drawable);
//        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        LogUtil.d(TAG, "stickerView width = " + getWidth());
//        LogUtil.d(TAG, "stickerView height = " + getHeight());
        drawContent(canvas);
    }

    public void destory() {
        if (handler != null) {
            handler.removeMessages(SHOW_FRAME);
            handler.removeMessages(DISMISS_FRAME);
            handler.removeCallbacksAndMessages(null);
        }
        stop();
    }

    boolean hasVibrated = false;

    /**
     * 绘制基准线
     * 当中心点对准时出现 基准线
     */
    private void drawGuideLine(Canvas canvas) {
        if (isGuideLineShow() && center != null && canvas != null && guideLineShowOntouch) {
            //绘制水平辅助线
            debugPaint.setStrokeWidth(guideLineWidth);
            debugPaint.setColor(guideLineColor);
            float canvasWidth = canvas.getWidth();
            float canvasHeight = canvas.getHeight();
            float lineLength = Math.min(canvasWidth, canvasHeight) * 0.2f;
            boolean needVib = false;

            if (center.x < canvasWidth / 2f + 25 && center.x > canvasWidth / 2f - 25) {
                canvas.drawLine(canvasWidth / 2f, 0, canvasWidth / 2f, lineLength, debugPaint);
                canvas.drawLine(canvasWidth / 2f, canvasHeight, canvasWidth / 2f, canvasHeight - lineLength, debugPaint);
                needVib = true;
            }

            if (center.y < canvasHeight / 2f + 25 && center.y > canvasHeight / 2f - 25) {
                canvas.drawLine(0, canvasHeight / 2f, lineLength, canvasHeight / 2f, debugPaint);
                canvas.drawLine(canvasWidth, canvasHeight / 2f, canvasWidth - lineLength, canvasHeight / 2f, debugPaint);
                needVib = true;
            }

            if (needVib) {
                if (!hasVibrated) {
                    vibrate();
                    hasVibrated = true;
                }
            } else {
                hasVibrated = false;
            }
        }
    }

    private synchronized void vibrate() {
        Log.e("Sticker", "开始振动=====");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, 50));
        } else {
            vibrator.vibrate(50);
        }
    }

    protected StaticLayout getStaticLayout(String content, TextPaint textPaint, int drawWidth) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return StaticLayout.Builder.obtain(content, 0, content.length(), textPaint, drawWidth)
                    .setAlignment(Layout.Alignment.ALIGN_CENTER)
                    .setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE)
                    .setLineSpacing(0, 1f)
                    .setIncludePad(true)
                    .build();
        } else {
            return new StaticLayout(content, textPaint, drawWidth, Layout.Alignment.ALIGN_CENTER, 1f, 0, true);
        }
    }

    Rect bounds = new Rect();

    /**
     * 绘制具体内容
     *
     * @param canvas
     */
    private void drawContent(Canvas canvas) {
        if (mStickerType == CODE_STICKER_TYPE_TEXT) {
            if (mOpenThePattern) {
                listFontList = getFontList(stickerText);
                mMeasureWidth = MeasureTextUtils.getFontWidth(mTextPaint, stickerText) * 2;
                int size = stickerText.length();
                mMeasureHeight = mMeasureWidth / size;
            } else {
                mMeasureWidth = MeasureTextUtils.getFontWidth(mTextPaint, stickerText);
                mMeasureHeight = MeasureTextUtils.getFontHeight(mTextPaint);
            }
            RectF rectF = new RectF(0, 0, mMeasureWidth, mMeasureHeight);
            rectF.offset(center.x - rectF.centerX(), center.y - rectF.centerY());
            mHelpBoxRect.set(rectF);
            mTextScale = mMeasureWidth / (getMeasuredWidth() / 2f);

            float needRectHeight;
            if (mOpenThePattern) {
                needRectHeight = mHelpBoxRect.top + mHelpBoxRect.height() * 0.62f;
            } else {
                needRectHeight = mHelpBoxRect.top + mHelpBoxRect.height() * 0.8f;
            }
//            float halfTextWidth = mMeasureWidth / (float) 2;
            if (bpForTextBj != null) {
                BitmapShader bitmapShader = new BitmapShader(BitmapUtil.GetBitmapForScale(bpForTextBj, (int) mHelpBoxRect.width(),
                        (int) mHelpBoxRect.height()), Shader.TileMode.MIRROR, Shader.TileMode.MIRROR);
                matrixForBitmapShader.setTranslate(mHelpBoxRect.left, mHelpBoxRect.top);
                bitmapShader.setLocalMatrix(matrixForBitmapShader);
                mTextPaint.setShader(bitmapShader);
            }
            canvas.save();
            canvas.scale(mScale, mScale, center.x, center.y);
            canvas.rotate(mRotateAngle, center.x, center.y);

            if (mOpenThePattern) {
                //设置间距
                mTextPaint.setLetterSpacing(letterSpacingSize);
                mPaintShadow.setLetterSpacing(letterSpacingSize);
                mTextPaint2.setLetterSpacing(letterSpacingSize);
                float oneImageWith = MeasureTextUtils.getFontWidth(mTextPaint, "一");

                if (bpTestTextBj != null) {
                    bpTestTextBj = bitmapToCenter(bpTestTextBj, (int) mMeasureHeight, (int) mMeasureHeight);
                    for (int i = 0; i < stickerText.length(); i++) {
                        canvas.drawBitmap(bpTestTextBj, mHelpBoxRect.left + mMeasureHeight * i, mHelpBoxRect.top, mTextPaint);
                    }
                } else {
                    LogUtil.d("OOM4", "bpTestTextBj==NULL");
                }

                for (int y = 0; y < stickerText.length(); y++) {
                    for (int i = 1; i < 10; i++) {
                        canvas.drawText(listFontList.get(y), mHelpBoxRect.left + mMeasureHeight / 2 - (oneImageWith / 2) - i + mMeasureHeight * y, needRectHeight - 10 + i / (float) 2, mTextPaint);
                    }
                }

                RadialGradient radialGradient4 = new RadialGradient(mHelpBoxRect.centerX(),
                        mHelpBoxRect.centerY(), mHelpBoxRect.width(), COLORS, null, Shader.TileMode.CLAMP);
                mPaintShadow.setShader(radialGradient4);

                if (bpForTextBj == null) {
                    //只要没有选择图片背景
                    for (int i = 0; i < stickerText.length(); i++) {
                        canvas.drawText(listFontList.get(i), mHelpBoxRect.left + mMeasureHeight / 2 - (oneImageWith / 2) + mMeasureHeight * i, needRectHeight - 10, mTextPaint2);
                    }
                } else {
                    for (int i = 0; i < stickerText.length(); i++) {
                        canvas.drawText(listFontList.get(i), mHelpBoxRect.left + mMeasureHeight / 2 - (oneImageWith / 2) - 5 + mMeasureHeight * i, needRectHeight - 7.5f, mPaintShadow);
                        canvas.drawText(listFontList.get(i), mHelpBoxRect.left + mMeasureHeight / 2 - (oneImageWith / 2) + mMeasureHeight * i, needRectHeight - 10, mTextPaint);
                    }
                }

            } else {
                mTextPaint.setLetterSpacing(0);
                mPaintShadow.setLetterSpacing(0);
                mTextPaint2.setLetterSpacing(0);
                for (int i = 1; i < 10; i++) {
                    canvas.drawText(stickerText, mHelpBoxRect.left + 10 - i, needRectHeight - 10 + i / (float) 2, mTextPaint);
                }
                RadialGradient radialGradient4 = new RadialGradient(mHelpBoxRect.centerX(),
                        mHelpBoxRect.centerY(), mHelpBoxRect.width(), COLORS, null, Shader.TileMode.CLAMP);
                mPaintShadow.setShader(radialGradient4);
                if (bpForTextBj == null) {
                    //只要没有选择图片背景
                    canvas.drawText(stickerText, mHelpBoxRect.left + 10, needRectHeight - 10, mTextPaint2);
                } else {
                    canvas.drawText(stickerText, mHelpBoxRect.left + 10 - 5, needRectHeight - 7.5f, mPaintShadow);
                    canvas.drawText(stickerText, mHelpBoxRect.left + 10, needRectHeight - 10, mTextPaint);
                }
            }
            canvas.restore();
            RectUtil.scaleRect(mHelpBoxRect, mScale);
            drawFrame(canvas);


        } else if (currentDrawable != null) {
            //从闪图过来并且镜像bitmap不为空，将本身的图源换成镜像bitmap
            if (mStickerType == CODE_STICKER_TYPE_FLASH_PIC && mMirrorBitmap != null) {
                currentDrawable = (D) new BitmapDrawable(getResources(), mMirrorBitmap);
            }
            RectF rectF = new RectF(0, 0, currentDrawable.getIntrinsicWidth(), currentDrawable.getIntrinsicHeight());
            rectF.offset(center.x - rectF.centerX(), center.y - rectF.centerY());
            mHelpBoxRect.set(rectF);
            textRect.set(mHelpBoxRect.left, mHelpBoxRect.bottom - 50, mHelpBoxRect.right, mHelpBoxRect.bottom);
            //透明遮罩
            int w = (int) (currentDrawable.getIntrinsicWidth() + 0.5);
            int h = (int) (currentDrawable.getIntrinsicHeight() + 0.5);
            mMaskBitmap = makeSrc(w, h, mRightOffsetPercent);
            Bitmap dstBm = makeDst(w, h, currentDrawable);

            //实现透明可调节遮罩
            int layerID = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), mHelpDstPaint);

            canvas.scale(mScale, mScale, center.x, center.y);
            canvas.rotate(mRotateAngle, center.x, center.y);
            canvas.drawBitmap(dstBm, mHelpBoxRect.left, mHelpBoxRect.top, mHelpDstPaint);
            mHelpDstPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            canvas.drawBitmap(mMaskBitmap, mHelpBoxRect.left, mHelpBoxRect.top, mHelpDstPaint);
            mHelpDstPaint.setXfermode(null);
            canvas.restoreToCount(layerID);

            RectUtil.scaleRect(mHelpBoxRect, mScale);
//            RectUtil.scaleRect(textRect, mScale);
            drawFrame(canvas);
        }
    }

    private void drawFrame(Canvas canvas) {
        //显示编辑框
        if (frameShow && !isFromAnim) {
            // draw x and rotate button
            int offsetValue = 0;
            if (leftTopBitmap != null) {
                offsetValue = ((int) leftTopDstRect.width()) >> 1;
            } else if (rightTopBitmap != null) {
                offsetValue = ((int) rightTopDstRect.width()) >> 1;
            } else if (leftBottomBitmap != null) {
                offsetValue = ((int) leftBottomDstRect.width()) >> 1;
            } else if (rightBottomBitmap != null) {
                offsetValue = ((int) rightBottomDstRect.width()) >> 1;
            } else if (rightCenterBitmap != null) {
                offsetValue = ((int) rightCenterDstRect.width()) >> 1;
            } else if (rightBitmap != null) {
                offsetValue = ((int) rightDstRect.width()) >> 1;
            } else if (leftBitmap != null) {
                offsetValue = ((int) leftDstRect.width()) >> 1;
            }
            leftTopDstRect.offsetTo(mHelpBoxRect.left - offsetValue,
                    mHelpBoxRect.top - offsetValue);
            rightBottomDstRect.offsetTo(mHelpBoxRect.right - offsetValue,
                    mHelpBoxRect.bottom - offsetValue);
            mRightLimited = offsetValue * 4;
            float rightOffset = mRightOffsetPercent * (mHelpBoxRect.bottom - mHelpBoxRect.top - offsetValue * 4);
            rightDstRect.offsetTo(mHelpBoxRect.right - offsetValue,
                    mHelpBoxRect.top + offsetValue + rightOffset);
            float centerHalfLeft = (mHelpBoxRect.bottom - mHelpBoxRect.top) / (float) 4;
            leftDstRect.offsetTo(mHelpBoxRect.left - offsetValue, mHelpBoxRect.top + centerHalfLeft);
            leftBottomDstRect.offsetTo(mHelpBoxRect.left - offsetValue,
                    mHelpBoxRect.bottom - offsetValue);
            rightTopDstRect.offsetTo(mHelpBoxRect.right - offsetValue,
                    mHelpBoxRect.top - offsetValue);
            float center = (mHelpBoxRect.bottom - mHelpBoxRect.top) / (float) 2;
            // 音量按键位置
            rightCenterDstRect.offsetTo(mHelpBoxRect.left - offsetValue,
                    mHelpBoxRect.top + center - offsetValue);
            //按钮随画布转动
            RectUtil.rotateRect(leftTopDstRect, mHelpBoxRect.centerX(),
                    mHelpBoxRect.centerY(), mRotateAngle);
            RectUtil.rotateRect(rightBottomDstRect, mHelpBoxRect.centerX(),
                    mHelpBoxRect.centerY(), mRotateAngle);
            RectUtil.rotateRect(rightDstRect, mHelpBoxRect.centerX(),
                    mHelpBoxRect.centerY(), mRotateAngle);
            RectUtil.rotateRect(leftDstRect, mHelpBoxRect.centerX(),
                    mHelpBoxRect.centerY(), mRotateAngle);
            RectUtil.rotateRect(leftBottomDstRect, mHelpBoxRect.centerX(),
                    mHelpBoxRect.centerY(), mRotateAngle);
            RectUtil.rotateRect(rightTopDstRect, mHelpBoxRect.centerX(),
                    mHelpBoxRect.centerY(), mRotateAngle);
            RectUtil.rotateRect(rightCenterDstRect, mHelpBoxRect.centerX(),
                    mHelpBoxRect.centerY(), mRotateAngle);
            RectUtil.rotateRect(textRect, mHelpBoxRect.centerX(),
                    mHelpBoxRect.centerY(), mRotateAngle);
            //画边框
            canvas.save();
            canvas.rotate(mRotateAngle, mHelpBoxRect.centerX(),
                    mHelpBoxRect.centerY());
            canvas.drawRoundRect(mHelpBoxRect, 10, 10, mHelpPaint);
            canvas.restore();
            //周边按钮
            if (leftTopBitmap != null) {
                leftTopBitmap.setBounds((int) leftTopDstRect.left, (int) leftTopDstRect.top, (int) leftTopDstRect.right, (int) leftTopDstRect.bottom);
                leftTopBitmap.draw(canvas);
            }
            if (rightBitmap != null) {
                rightBitmap.setBounds((int) rightDstRect.left, (int) rightDstRect.top, (int) rightDstRect.right, (int) rightDstRect.bottom);
                rightBitmap.draw(canvas);
            }

            if (leftBitmap != null) {
                leftBitmap.setBounds((int) leftDstRect.left, (int) leftDstRect.top, (int) leftDstRect.right, (int) leftDstRect.bottom);
                leftBitmap.draw(canvas);
            }

            if (rightBottomBitmap != null) {
                rightBottomBitmap.setBounds((int) rightBottomDstRect.left, (int) rightBottomDstRect.top, (int) rightBottomDstRect.right, (int) rightBottomDstRect.bottom);
                rightBottomBitmap.draw(canvas);
            }

            if (leftBottomBitmap != null) {
                leftBottomBitmap.setBounds((int) leftBottomDstRect.left, (int) leftBottomDstRect.top, (int) leftBottomDstRect.right, (int) leftBottomDstRect.bottom);
                leftBottomBitmap.draw(canvas);
            }

            if (rightTopBitmap != null) {
                rightTopBitmap.setBounds((int) rightTopDstRect.left, (int) rightTopDstRect.top, (int) rightTopDstRect.right, (int) rightTopDstRect.bottom);
                rightTopBitmap.draw(canvas);
            }

            if (rightCenterBitmap != null) {
                rightCenterBitmap.setBounds((int) rightCenterDstRect.left, (int) rightCenterDstRect.top, (int) rightCenterDstRect.right, (int) rightCenterDstRect.bottom);
                rightCenterBitmap.draw(canvas);
            }
            if (NowMaterialIsVideo) {
                //动态设置文字大小
                int desiredTextSize = (int) (40 * (rightBottomDstRect.left - leftBottomDstRect.right) / bounds.width());
//                    LogUtil.d("OOM", "desiredTextSize=" + desiredTextSize);
                whitePaint.setTextAlign(Paint.Align.CENTER);
                whitePaint.setTextSize(desiredTextSize);
                Path circlePath = new Path();
                circlePath.moveTo(leftBottomDstRect.left, leftBottomDstRect.bottom);
                circlePath.lineTo(rightBottomDstRect.left, rightBottomDstRect.bottom);
                canvas.drawTextOnPath("预览后人物可动", circlePath, 20, 20, whitePaint);
            }
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    /**
     * 变更当前的按钮模式
     *
     * @param x            按压位置x
     * @param y            按压位置y
     * @param pointerCount 手指数量
     * @param pointerUp    双指抬起
     * @return 模式code
     */
    private int adjustMode(float x, float y, int pointerCount, boolean pointerUp) {
        if (leftTopDstRect != null && leftTopDstRect.contains(x, y)) {
            return LEFT_TOP_MODE;
        } else if (leftBottomDstRect != null && leftBottomDstRect.contains(x, y)) {
            return LEFT_BOTTOM_MODE;
        } else if (rightTopDstRect != null && rightTopDstRect.contains(x, y)) {
            return RIGHT_TOP_MODE;
        } else if (rightBottomDstRect != null && rightBottomDstRect.contains(x, y)) {
            return RIGHT_BOTTOM_MODE;
        } else if (rightCenterDstRect != null && rightCenterDstRect.contains(x, y)) {
            return RIGHT_CENTER_MODE;
        } else if (rightDstRect != null && rightDstRect.contains(x, y)) {
            return RIGHT_MODE;
        } else if (leftDstRect != null && leftDstRect.contains(x, y)) {
            return LEFT_MODE;
        } else if (isIn(mHelpBoxRect, x, y, mRotateAngle) && (pointerCount == 1) && !pointerUp) {
            return MOVE_MODE;
        } else if (pointerCount > 1) {
            return NEW_POINTER_DOWN_MODE;
        } else {
            return IDLE_MODE;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 是否向下传递事件标志 true为消耗
        super.onTouchEvent(event);
        if (!isFromAnim) {
            int pointerCount = event.getPointerCount();
            int action = event.getAction();
            float x = event.getX();
            float y = event.getY();
            LogUtil.d("event", "x =" + x);
            LogUtil.d("event", "y =" + y);
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    LogUtil.d("event", "ACTION_DOWN");
                    if (callback != null) {
                        callback.stickerMove();
                        //callback.stickerOnclick(ONCLICK_MODE);
                    }

                    mCurrentMode = adjustMode(x, y, pointerCount, false);
                    if (mCurrentMode == IDLE_MODE) {
                        return false;
                    } else if (mCurrentMode == LEFT_TOP_MODE) {
                        if (this.isRunning) {
                            stop();
                        }
                        callback.stickerOnclick(LEFT_TOP_MODE);
                        return true;
                    } else if (mCurrentMode == RIGHT_TOP_MODE) {
                        callback.stickerOnclick(RIGHT_TOP_MODE);
                        return true;
                    } else if (mCurrentMode == LEFT_BOTTOM_MODE) {
                        //添加来源判断
                        if (mStickerType == CODE_STICKER_TYPE_FLASH_PIC) {
                            if (mIsMirror) {
                                mMirrorBitmap = BitmapUtils.toHorizontalMirror(mMirrorBitmap);
                            } else {
                                mMirrorBitmap = BitmapUtils.toHorizontalMirror(currentDrawable);
                            }
                            mIsMirror = !mIsMirror;
                        } else {
                            callback.stickerOnclick(LEFT_BOTTOM_MODE);
                        }
                        return true;
                    } else if (mCurrentMode == RIGHT_CENTER_MODE) {
                        callback.stickerOnclick(RIGHT_CENTER_MODE);
                        return true;
                    } else if (mCurrentMode == LEFT_MODE) {
                        callback.stickerOnclick(LEFT_MODE);
                        return true;
                    } else if (mCurrentMode == RIGHT_BOTTOM_MODE) {
                        if (UiStep.isFromDownBj) {
                            StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), " 5_mb_bj_Spin");
                        } else {
                            StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), " 6_customize_bj_Spin");
                        }
                    }
                    lastX = x;
                    lastY = y;
                    guideLineShowOntouch = true;
//                    if (tickerListener != null) {
//                        tickerListener.onActionStart(this, mCurrentMode, mScale, mRotateAngle, center);
//                    }
                    if (!frameShow) {
                        frameShow = true;
                        invalidate();
                    }
                    callback.stickerClickShowFrame();
                    handler.removeMessages(DISMISS_FRAME);
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    //float startRotation = getRotation(event);
                    mCurrentMode = adjustMode(x, y, pointerCount, false);
                    dx0 = event.getX(1) - event.getX(0);
                    dy0 = event.getY(1) - event.getY(0);
                    LogUtil.d("event", "ACTION_POINTER_DOWN pointerCount =" + pointerCount);
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    pointerCount = 1;
                    mCurrentMode = adjustMode(x, y, pointerCount, true);
                    LogUtil.d("event", "ACTION_POINTER_UP pointerCount =" + pointerCount);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (dragCallback != null) {
                        dragCallback.stickerDragMove();
                    }
                    if (UiStep.isFromDownBj) {
                        StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "5_mb_bj_drag");
                    } else {
                        StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "6_customize_bj_drag");
                    }

                    if (mCurrentMode == IDLE_MODE) {
                        return false;
                    }

                    if (mCurrentMode == MOVE_MODE) {
                        // 移动贴图
                        float dx = x - lastX;
                        float dy = y - lastY;
                        layoutX += dx;
                        layoutY += dy;
                        adjustCenter(dx, dy);
                        invalidate();

                        lastX = x;
                        lastY = y;
                        moveX = mHelpBoxRect.right;
                        moveY = mHelpBoxRect.bottom;
                    } else if (mCurrentMode == rotateLocation) {
                        // 旋转 缩放文字操作
                        float dx = x - lastX;
                        float dy = y - lastY;
                        updateRotateAndScale(dx, dy);
                        invalidate();
                        lastX = x;
                        lastY = y;
                    } else if (mCurrentMode == RIGHT_MODE) {
                        LogUtil.d("event", "RIGHT_MODE");
                        float dx = x - lastX;
                        float dy = y - lastY;
                        changeRightOffset(dx, dy);
                        invalidate();
                        lastX = x;
                        lastY = y;
                    } else if (mCurrentMode == NEW_POINTER_DOWN_MODE) {
                        LogUtil.d("event", "NEW_POINTER_DOWN_MODE");
                        float dx1 = event.getX(1) - event.getX(0);
                        float dy1 = event.getY(1) - event.getY(0);
                        updateRotateAndScalePointerDown(dx0, dy0, dx1, dy1);
                        invalidate();
                        dx0 = dx1;
                        dy0 = dy1;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if (dragCallback != null) {
                        dragCallback.stickerDragUp();
                    }
                    LogUtil.d("event", "ACTION_CANCEL");
                    if (mCurrentMode == IDLE_MODE) {
                        return false;
                    }
                    handler.removeMessages(DISMISS_FRAME);
                    handler.sendEmptyMessageDelayed(DISMISS_FRAME, AUTO_FADE_FRAME_TIMEOUT);
                    guideLineShowOntouch = false;
                    invalidate();
//                    if (tickerListener != null) {
//                        tickerListener.onActionEnd(this, mCurrentMode, mScale, mRotateAngle, center);
//                    }
                    mCurrentMode = IDLE_MODE;
                    break;
                default:
                    break;

            }// end switch
            return true;
        }
        return false;
    }

    //右侧滑动
    private void changeRightOffset(float dx, float dy) {
        float sqrt = (float) Math.sqrt(dx * dx + dy * dy);

        if (mRotateAngle >= 90 && mRotateAngle <= 270 && dy > 0) {
            mRightOffset -= sqrt;
        } else if (mRotateAngle >= 90 && mRotateAngle <= 270 && dy < 0) {
            mRightOffset += sqrt;
        } else if (dy < 0) {
            mRightOffset -= sqrt;
        } else if (dy > 0) {
            mRightOffset += sqrt;
        }
        LogUtil.d("change", mRotateAngle + " mRotateAngle");
        LogUtil.d("change", mRightOffsetPercent + " mRightOffset1");
        if (mRightOffset < 0) {
            mRightOffset = 0;
        } else if (mRightOffset >= mHelpBoxRect.bottom - mHelpBoxRect.top - mRightLimited) {
            mRightOffset = mHelpBoxRect.bottom - mHelpBoxRect.top - mRightLimited;
        }
        mRightOffsetPercent = mRightOffset / (mHelpBoxRect.bottom - mHelpBoxRect.top - mRightLimited);
        LogUtil.d("change", mRightOffsetPercent + " mRightOffset2");
        LogUtil.d("change", mHelpBoxRect.top + " top");
        LogUtil.d("change", mHelpBoxRect.bottom + " bottom");
    }

    public boolean isIn(RectF source, float x, float y, float angle) {
        RectF rectF = new RectF(source);
        if (angle != 0) {
            Matrix matrix = new Matrix();
            //设置旋转角度时，一定要记得设置旋转的中心，该中心与绘图时的中心点是一致的。
            matrix.setRotate(angle, rectF.centerX(), rectF.centerY());
            matrix.mapRect(rectF);
        }
        return rectF.contains(x, y);
    }


    /**
     * 旋转 缩放 更新
     *
     * @param dx X坐标距离
     * @param dy Y坐标距离
     */
    public void updateRotateAndScale(final float dx, final float dy) {
        float cx = mHelpBoxRect.centerX();
        float cy = mHelpBoxRect.centerY();

        float x = rightBottomDstRect.centerX();
        float y = rightBottomDstRect.centerY();

        float nx = x + dx;
        float ny = y + dy;

        float xa = x - cx;
        float ya = y - cy;

        float xb = nx - cx;
        float yb = ny - cy;

        float srcLen = (float) Math.sqrt(xa * xa + ya * ya);
        float curLen = (float) Math.sqrt(xb * xb + yb * yb);
        // 计算缩放比
        float scale = curLen / srcLen;

        mScale *= scale;

        float newWidth = mHelpBoxRect.width() * mScale;

        if (newWidth < 70) {
            mScale /= scale;
            return;
        }

        double cos = (xa * xb + ya * yb) / (srcLen * curLen);
        if (cos > 1 || cos < -1) {
            return;
        }
        float angle = (float) Math.toDegrees(Math.acos(cos));
        // 行列式计算 确定转动方向
        float calMatrix = xa * yb - xb * ya;

        int flag = calMatrix > 0 ? 1 : -1;
        angle = flag * angle;

        //+= angle;
        mRotateAngle = adjustDegree(mRotateAngle, angle);

        moveX = mHelpBoxRect.right;
        moveY = mHelpBoxRect.bottom;
    }

    /**
     * 双指 旋转 缩放 更新
     *
     * @param dx0 初始x坐标差
     * @param dy0 初始y坐标差
     * @param dx1 第二次x坐标查
     * @param dy1 第二次y坐标差
     */
    public void updateRotateAndScalePointerDown(final float dx0, final float dy0,
                                                final float dx1, final float dy1) {

        float srcLen = (float) Math.sqrt(dx0 * dx0 + dy0 * dy0);
        float curLen = (float) Math.sqrt(dx1 * dx1 + dy1 * dy1);
        // 计算缩放比
        float scale = curLen / srcLen;

        mScale *= scale;

        float newWidth = mHelpBoxRect.width() * mScale;

        if (newWidth < 70) {
            mScale /= scale;
            return;
        }

        double cos = (dx0 * dx1 + dy0 * dy1) / (srcLen * curLen);
        if (cos > 1 || cos < -1) {
            return;
        }
        float angle = (float) Math.toDegrees(Math.acos(cos));
        // 行列式计算 确定转动方向
        float calMatrix = dx0 * dy1 - dx1 * dy0;

        int flag = calMatrix > 0 ? 1 : -1;
        angle = flag * angle;

        //+= angle;
        mRotateAngle = adjustDegree(mRotateAngle, angle);


        LogUtil.d("updateRotateAndScale", "mScale=" + mScale);
        LogUtil.d("updateRotateAndScale", "mRotateAngle=" + mRotateAngle);

        moveX = mHelpBoxRect.right;
        moveY = mHelpBoxRect.bottom;
    }

    boolean degreeTurned = false;
    float tempDegree = 0;

    /**
     * 辅助居中
     *
     * @param currentDegree 当前角度
     * @param newDegree     新角度
     * @return degree
     */
    private float adjustDegree(float currentDegree, float newDegree) {
        if (!enableAutoAdjustDegree) {
            return currentDegree + newDegree;
        }
        tempDegree += newDegree;
        int current = Math.round(currentDegree % 360);
        if (current < 0) {
            current = 360 + current;
        }
        if (!degreeTurned) {
            if (newDegree > 0) {
                if (current > 85 && current < 90) {
                    current = 90;
                    degreeTurned = true;
                    tempDegree = 0f;
                }
                if (current > 355 && current < 360) {
                    current = 0;
                    degreeTurned = true;
                    tempDegree = 0f;
                }
            } else {
                if (current < 95 && current > 90) {
                    current = 90;
                    degreeTurned = true;
                    tempDegree = 0f;
                }
                if (current > 0 && current < 5) {
                    current = 0;
                    degreeTurned = true;
                    tempDegree = 0f;
                }
            }
        }

        if (degreeTurned) {
            mHelpPaint.setColor(Color.RED);
            if (Math.abs(tempDegree) <= 10f) {
                return current;
            } else {
                tempDegree = 0;
                degreeTurned = false;
                return current + tempDegree;
            }

        } else {
            mHelpPaint.setColor(Color.WHITE);
        }
        return current + newDegree;
    }

    float autoCenterRange = 30;
    float autoCenterMoveX = 0;
    float autoCenterMoveY = 0;

    /**
     * 辅助层中
     *
     * @param dx
     * @param dy
     */
    private void adjustCenter(float dx, float dy) {

        if (!enableAutoAdjustCenter) {
            center.offset(dx, dy);
        } else {
            //计算x軕
            if (isInCenterX(center)) {
                if (Math.abs(autoCenterMoveX) >= autoCenterRange) {
                    center.offset(autoCenterMoveX, 0);
                    Log.e("savion", "当前移动X距离已够:" + autoCenterMoveX + ",dx:" + dx);
                    autoCenterMoveX = 0;
                } else {
                    autoCenterMoveX += dx;
                }
            } else {
                boolean xWillInCenter = Math.abs((center.x + dx) - getWidth() / 2f) < autoCenterRange;
                if (xWillInCenter) {
                    autoCenterMoveX = 0;
                    center.set(getWidth() / 2f, center.y);
                } else {
                    center.offset(dx, 0);
                }
            }
            //计算Y軕
            if (isInCenterY(center)) {
                if (Math.abs(autoCenterMoveY) >= autoCenterRange) {
                    center.offset(0, autoCenterMoveY);
                    Log.e("savion", "当前移动Y距离已够:" + autoCenterMoveY + ",dy:" + dy);
                    autoCenterMoveY = 0;
                } else {
                    Log.e("savion", "当前移动Y距离不够:" + autoCenterMoveY + ",dy:" + dy);
                    autoCenterMoveY += dy;
                }
            } else {
                boolean yWillInCenter = Math.abs((center.y + dy) - getHeight() / 2f) < autoCenterRange;
                if (yWillInCenter) {
                    autoCenterMoveY = 0;
                    center.set(center.x, getHeight() / 2f);
                } else {
                    center.offset(0, dy);
                }
            }
        }
    }

    private boolean isInCenterX(PointF point) {
        if (point != null) {
            return Math.abs(point.x - getWidth() / 2f) < autoCenterRange;
        }
        return false;
    }

    private boolean isInCenterY(PointF point) {
        if (point != null) {
            return Math.abs(point.y - getHeight() / 2f) < autoCenterRange;
        }
        return false;
    }


    public void setCenter(float centerx, float centery) {
        if (center == null) {
            center = new PointF(centerx, centery);
        } else {
            center.set(centerx, centery);
        }
    }


    public void setIntoCenter() {
        setCenter(getWidth() / 2f, getHeight() / 2f);
    }

    public PointF getCenter() {
        return center;
    }

    public void setScale(float scale) {
        this.mScale = scale;
    }

    public void setRotate(float rotate) {
        this.mRotateAngle = rotate;
    }

    public void setDegree(float degree) {
        this.mRotateAngle = degree;
    }

    public float getScale() {
        if (mStickerType == CODE_STICKER_TYPE_TEXT) {
            return mScale * mTextScale;
        } else {
            return mScale;
        }
    }

    public float getCopyScale() {
        return mScale;
    }


    /**
     * description ：得到的是一个比例
     * creation date: 2020/6/3
     * user : zhangtongju
     */
    @Override
    public float getTranslationX() {
        //获得整个绘制区域的宽
        float HelpBoxRectWidth = mHelpBoxRect.width();
        //应为蓝松是0.5表示居中，所以这里搞了个2
        float centerLine = HelpBoxRectWidth / 2;

        float centerPosition = mHelpBoxRect.right - centerLine;
        return centerPosition / getMeasuredWidth();
    }


    public float getmHelpBoxRectW() {
        return mHelpBoxRect.width();
    }

    public float getmHelpBoxRectH() {
        return mHelpBoxRect.height();
    }


    boolean isMaterial = true;

    public void setIsmaterial(boolean isMaterial) {
        this.isMaterial = isMaterial;
    }

    public boolean getIsmaterial() {
        return isMaterial;
    }


    @Override
    public float getTranslationY() {
        float HelpBoxRectWidth = mHelpBoxRect.height();
        float centerLine = HelpBoxRectWidth / 2;
        float centerPosition = mHelpBoxRect.bottom - centerLine;
        return centerPosition / getMeasuredHeight();
    }


    public float getRotateAngle() {
        return mRotateAngle;
    }

    public float getRightOffsetPercent() {
        return mRightOffsetPercent;
    }

    public Bitmap getMaskBitmap() {
        return mMaskBitmap;
    }

    public Bitmap getMirrorBitmap() {
        return mMirrorBitmap;
    }

    public StickerTarger getTarger() {
        if (targer == null) {
            targer = new StickerTarger();
        }
        return targer;
    }

    StickerTarger targer;

    class StickerTarger extends SimpleTarget<D> {

        boolean autoRun = false;

        public void setAutoRun(boolean autoRun) {
            this.autoRun = autoRun;
        }

        @Override
        public void onResourceReady(@NonNull D resource, @Nullable Transition<? super D> transition) {
            Log.e("Sticker", "onResourceReady:" + resource.getIntrinsicWidth() + "==" + resource.getIntrinsicHeight());
            currentDrawable = resource;

            if (autoRun) {
                start();
            }

            if (!isMatting) {
                moveX = (getMeasuredWidth() + originalBitmapWidth) >> 1;
                moveY = (getMeasuredHeight() + originalBitmapHeight) >> 1;

                if (fromCopy != null) {
                    setScale(fromCopy.getScale());
                    setDegree(fromCopy.getDegree());
                    setCenter(fromCopy.tranX, fromCopy.tranY);
                    mRightOffsetPercent = fromCopy.getRightOffsetPercent();
                } else {
                    setScale(1f);
                    setDegree(0f);
                    setCenter(getWidth() / 2f, getHeight() / 2f);
                }

            } else if (isMatting && mStickerType == CODE_STICKER_TYPE_FLASH_PIC) {

                if (mIsMirror) {
                    mMirrorBitmap = BitmapUtils.toHorizontalMirror(currentDrawable);
                } else {
                    mMirrorBitmap = BitmapUtils.drawableToBitmap(currentDrawable);
                }

            } else if (mStickerType == CODE_STICKER_TYPE_FLASH_PIC && mIsChange) {
                mMirrorBitmap = BitmapUtils.drawableToBitmap(currentDrawable);
            }

            invalidate();
        }
    }


    private void loadOriginalMirrorRes(String path) {
        if (BaseApplication.getInstance() != null) {
            RequestManager manager = Glide.with(BaseApplication.getInstance());
            RequestBuilder builder = null;

            builder = manager.asDrawable();

            options.override((int) contentWidth, (int) contentHeight);

            builder.load(path)
                    .apply(options)
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition transition) {
                            if (TextUtils.isEmpty(mOriginalMirrorPath)) {
                                mOriginalMirrorBitmap = BitmapUtils.toHorizontalMirror(resource);
                                mOriginalMirrorPath = FileUtil.saveBitmap(mOriginalMirrorBitmap, "saveAlbum");
                            }
                            invalidate();
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }

    }

    /**
     * 闪图第一次进入，加载镜像资源
     */
    private void loadClipMirrorRes(String path) {
        if (BaseApplication.getInstance() != null) {
            RequestManager manager = Glide.with(BaseApplication.getInstance());
            RequestBuilder builder = null;

            builder = manager.asDrawable();

            options.override((int) contentWidth, (int) contentHeight);

            builder.load(path)
                    .apply(options)
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition transition) {
                            if (TextUtils.isEmpty(mClipMirrorPath)) {
                                mClipMirrorBitmap = BitmapUtils.toHorizontalMirror(resource);
                                mClipMirrorPath = FileUtil.saveBitmap(mClipMirrorBitmap, "saveAlbum");
                            }
                            invalidate();
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }


    }


    public boolean getComeFrom() {
        return isFromAlbum;
    }

    public void setComeFromAlbum(boolean isFromAlbum) {
        this.isFromAlbum = isFromAlbum;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }


    /**
     * 贴纸里面获得源地址，切记，选择的gif 是没得这个功能的
     */
    public String getOriginalPath() {
        return originalPath;
    }

    public void setClipPath(String clipPath) {
        this.clipPath = clipPath;
    }

    public String getClipPath() {
        return clipPath;
    }

    public String getResPath() {
        return resPath;
    }

    public String getClipMirrorPath() {
        return mClipMirrorPath;
    }

    public String getOriginalMirrorPath() {
        return mOriginalMirrorPath;
    }

    private String resPath = null;
    private float contentWidth;
    private float contentHeight;
    D currentDrawable = null;
    final RequestOptions options = new RequestOptions().centerCrop();

    public void setImageRes(final String path, final boolean autoRun, isFromCopy fromCopy) {
        this.fromCopy = fromCopy;
        if (!TextUtils.isEmpty(path)) {
            stop();
            this.resPath = path;
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    getTarger().setAutoRun(autoRun);
                    contentWidth = getMeasuredWidth() / 2f;
                    originalBitmapWidth = (int) contentWidth;
                    originalBitmap = BitmapFactory.decodeFile(path);
                    if (originalBitmap != null) {
                        int bitmapW = originalBitmap.getWidth();
                        int bitmapH = originalBitmap.getHeight();
                        boolean direction = BitmapManager.getInstance().getOrientation(path);
                        if (!direction) {
                            contentHeight = widthBigger ? contentWidth * (bitmapH / (float) bitmapW) : contentWidth * (bitmapW / (float) bitmapH);
                        } else {
                            //正常模式
                            contentHeight = widthBigger ? contentWidth * (bitmapW / (float) bitmapH) : contentWidth * (bitmapH / (float) bitmapW);
                        }
                        originalBitmapHeight = (int) contentHeight;
//                        LogUtil.d("OOM", "contentHeight=" + contentHeight);
//                        LogUtil.d("OOM", "contentWidth=" + contentWidth);
                    } else {
                        contentHeight = getMeasuredHeight() >> 1;
                    }
                    // contentHeight = (int) (getMinDisplayWidth() / 2f);
                    RequestManager manager = Glide.with(getContext());
                    RequestBuilder builder = null;
                    if (path.endsWith(".gif")) {
                        builder = manager.asGif();
                    } else {
                        builder = manager.asDrawable();
                    }
                    options.override((int) contentWidth, (int) contentHeight);

                    builder.load(path)
                            .apply(options)
                            .into(getTarger());


                    if (mStickerType == CODE_STICKER_TYPE_FLASH_PIC && isFromAlbum) {

                        if (TextUtils.isEmpty(mClipMirrorPath)) {
                            loadClipMirrorRes(clipPath);
                        }

                        if (TextUtils.isEmpty(mOriginalMirrorPath)) {
                            loadOriginalMirrorRes(originalPath);
                        }
                    }

                    recyclerBitmap();
                }
            });
        } else if (mStickerType != CODE_STICKER_TYPE_TEXT) {
            //路径不存在
            ToastUtil.showToast("文件不存在");
        }
    }


    public void onresmeView() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                getTarger().setAutoRun(false);
                contentWidth = getMeasuredWidth() / 2f;
                originalBitmapWidth = (int) contentWidth;
                originalBitmap = BitmapFactory.decodeFile(resPath);
                if (originalBitmap != null) {
                    int bitmapW = originalBitmap.getWidth();
                    int bitmapH = originalBitmap.getHeight();
                    boolean direction = BitmapManager.getInstance().getOrientation(resPath);
                    if (!direction) {
                        contentHeight = widthBigger ? contentWidth * (bitmapH / (float) bitmapW) : contentWidth * (bitmapW / (float) bitmapH);
                    } else {
                        //正常模式
                        contentHeight = widthBigger ? contentWidth * (bitmapW / (float) bitmapH) : contentWidth * (bitmapH / (float) bitmapW);
                    }
                    originalBitmapHeight = (int) contentHeight;
//                        LogUtil.d("OOM", "contentHeight=" + contentHeight);
//                        LogUtil.d("OOM", "contentWidth=" + contentWidth);
                } else {
                    contentHeight = getMeasuredHeight() >> 1;
                }
                // contentHeight = (int) (getMinDisplayWidth() / 2f);
                RequestManager manager = Glide.with(getContext());
                RequestBuilder builder;
                if (resPath.endsWith(".gif")) {
                    builder = manager.asGif();
                } else {
                    builder = manager.asDrawable();
                }
                options.override((int) contentWidth, (int) contentHeight);
                builder.load(resPath)
                        .apply(options)
                        .into(getTarger());
                recyclerBitmap();
            }
        });
    }


    public static class isFromCopy {

        public float getScale() {
            return scale;
        }

        public void setScale(float scale) {
            this.scale = scale;
        }

        float scale;

        public float getDegree() {
            return degree;
        }

        public void setDegree(float degree) {
            this.degree = degree;
        }

        float degree;

        float tranX;

        public float getRightOffsetPercent() {
            return RightOffsetPercent;
        }

        public void setRightOffsetPercent(float rightOffsetPercent) {
            RightOffsetPercent = rightOffsetPercent;
        }

        float RightOffsetPercent;

        public float getTranX() {
            return tranX;
        }

        public void setTranX(float tranX) {
            this.tranX = tranX;
        }

        public float getTranY() {
            return tranY;
        }

        public void setTranY(float tranY) {
            this.tranY = tranY;
        }

        float tranY;
    }


    /**
     * 區別第一次設置素材，这里主要用于切换素材，切换素材后，需要重新计算
     *
     * @param path
     * @param autoRun
     */
    public void changeImage(final String path, final boolean autoRun) {
        if (!TextUtils.isEmpty(path)) {
            mIsChange = true;
            stop();
            this.resPath = path;
            getTarger().setAutoRun(autoRun);
            contentWidth = getMeasuredWidth() / 2f;
            originalBitmapWidth = (int) contentWidth;
//            originalBitmap = BitmapFactory.decodeFile(path);
            if (BaseApplication.getInstance() != null) {
                GetVideoCover getVideoCover = new GetVideoCover(BaseApplication.getInstance());
                getVideoCover.getFileCoverForBitmap(path, cover -> {
                    originalBitmap = cover;
                    int bitmapW = originalBitmap.getWidth();
                    int bitmapH = originalBitmap.getHeight();
                    boolean direction = BitmapManager.getInstance().getOrientation(path);
                    if (!direction) {
                        contentHeight = widthBigger ? contentWidth * (bitmapH / (float) bitmapW) : contentWidth * (bitmapW / (float) bitmapH);
                    } else {
                        //正常模式
                        contentHeight = widthBigger ? contentWidth * (bitmapW / (float) bitmapH) : contentWidth * (bitmapH / (float) bitmapW);
                    }
                    originalBitmapHeight = (int) contentHeight;
//                LogUtil.d("OOM", "contentHeight=" + contentHeight);
//                LogUtil.d("OOM", "contentWidth=" + contentWidth);
                    if (BaseApplication.getInstance() != null) {
                        RequestManager manager = Glide.with(BaseApplication.getInstance());
                        RequestBuilder builder = null;
                        if (path.endsWith(".gif")) {
                            builder = manager.asGif();
                        } else {
                            builder = manager.asDrawable();
                        }
                        options.override((int) contentWidth, (int) contentHeight);
                        builder.load(path)
                                .apply(options)
                                .into(getTarger());
                        recyclerBitmap();
                    }

                    if (mStickerType == CODE_STICKER_TYPE_FLASH_PIC) {
                        loadClipMirrorRes(clipPath);
                        loadOriginalMirrorRes(originalPath);
                    }

                });
            }
        } else {
            //路径不存在
            LogUtil.d("OOM", "文件不存在");
        }
    }


    boolean isMatting = false;
    boolean isMattingOn = true;

    /**
     * 切换抠图/不抠图
     *
     * @param isMatting
     * @param path      图片地址
     */
    public void mattingChange(boolean isMatting, String path) {
        this.isMatting = true;
        isMattingOn = isMatting;
        RequestManager manager = Glide.with(BaseApplication.getInstance());
        RequestBuilder builder;
        if (path.endsWith(".gif")) {
            builder = manager.asGif();
        } else {
            builder = manager.asDrawable();
        }

        builder.load(path)
                .apply(options)
                .into(getTarger());
    }

    private void recyclerBitmap() {
        if (originalBitmap != null && !originalBitmap.isRecycled()) {
            originalBitmap.recycle();
            originalBitmap = null;
            LogUtil.d("OOM", "recycle=" + true);
        }
    }

    private boolean widthBigger;

    public float getCenterX() {
//        LogUtil.d("getCenterX", "getCenterX=" + mHelpBoxRect.right);
        float xx = (mHelpBoxRect.right - mHelpBoxRect.left) / 2;
        return mHelpBoxRect.right - xx;
    }

    public float getCenterXAdd30() {
//        LogUtil.d("getCenterX", "getCenterX=" + mHelpBoxRect.right);
        float xx = (mHelpBoxRect.right - mHelpBoxRect.left) / 2;
        return mHelpBoxRect.right - xx + 30;
    }

    public float getCenterY() {
        float yy = (mHelpBoxRect.bottom - mHelpBoxRect.top) / 2;
        return mHelpBoxRect.bottom - yy;
    }

    public float getCenterYAdd30() {
        float yy = (mHelpBoxRect.bottom - mHelpBoxRect.top) / 2;
        return mHelpBoxRect.bottom - yy + 30;
    }


    public float getMBoxCenterX() {
        return mHelpBoxRect.centerX();
    }


    public float getMBoxCenterY() {
        return mHelpBoxRect.centerY();
    }


    public float getMBoxLeft() {
        return mHelpBoxRect.left;
    }

    public float getMBoxTop() {
        return mHelpBoxRect.top;
    }

    public float getMBoxRight() {
        return mHelpBoxRect.right;
    }

    public float getMBoxBottom() {
        return mHelpBoxRect.bottom;
    }


    public void showFrame() {
        LogUtil.d("oom", "-----------------------showFrame------------------------------");
        handler.sendEmptyMessage(SHOW_FRAME);
        handler.sendEmptyMessageDelayed(DISMISS_FRAME, AUTO_FADE_FRAME_TIMEOUT);
    }

    public void dismissFrame() {
        if (frameShow) {
            frameShow = false;
            invalidate();
            LogUtil.d("oom", "-----------------------dismissFrame------------------------------");

        }
    }

    public boolean isFirstAddSticker() {
        return isFirstAddSticker;
    }

    public void setFirstAddSticker(boolean firstAddSticker) {
        isFirstAddSticker = firstAddSticker;
    }


    public boolean isOpenVoice() {
        return isOpenVoice;
    }

    public void setOpenVoice(boolean openVoice) {
        isOpenVoice = openVoice;
    }

    public float GetHelpBoxRectScale() {
        return mScale;
    }

    public float GetHelpBoxRectWidth() {
        return mHelpBoxRect.width();
    }

    public float GetHelpBoxRectRight() {
        return mHelpBoxRect.right;
    }

    public float GetHelpBoxRectCenterX() {
        return mHelpBoxRect.centerX();
    }

    public float GetHelpBoxRectLeftX() {
        return mHelpBoxRect.left;
    }

    public float GetHelpBoxRectBottom() {
        return mHelpBoxRect.bottom;
    }


    public void toRotate(float rotate) {
        mRotateAngle = rotate;
//        invalidate();
    }

    public void toTranMoveX(float needToX) {
        center.set(needToX, mHelpBoxRect.centerY());
//        invalidate();
        lastX = needToX;
    }

    public void toTranMoveY(float needToY) {
        center.set(mHelpBoxRect.centerX(), needToY);
//        invalidate();
        lastY = needToY;
    }

    public void toTranMoveXY(float needToX, float needToY) {
        center.set(needToX, needToY);
//        invalidate();
        lastY = needToY;
        lastX = needToX;
    }


    public void toScale(Float percent, float lastScale, boolean isDone) {
        if (isDone) {
            mScale = lastScale;
        } else {
            mScale = lastScale + percent * lastScale;
        }
    }

    /**
     * 是否来自动画页面
     */
    public void setIsfromAnim(boolean isFromAnim) {
        this.isFromAnim = isFromAnim;
    }


    public void setIsFromStickerAnim(boolean isFromStickerAnim) {
        this.isFromStickerAnim = isFromStickerAnim;
    }


    //----------------------------文字相关逻辑---------------------------------
    public void setStickerText(String text) {
        if (TextUtils.isEmpty(text)) {
            this.stickerText = "输入文本";
        } else {
            this.stickerText = text;
        }
    }


    public String getStickerText() {
        return stickerText;
    }


    public void setTextStyle(String path, String textStyleTitle) {
        this.textStyleTitle = textStyleTitle;
        TypefacePath = path;
        LogUtil.d("OOM5", "setTextStylepATH=" + path);
        Typeface typeface = Typeface.createFromFile(path);
        Typeface typeface2 = Typeface.createFromFile(path);
        Typeface typeface1 = Typeface.createFromFile(path);
        mTextPaint.setTypeface(typeface);
        mPaintShadow.setTypeface(typeface1);
        mTextPaint2.setTypeface(typeface2);
    }


    public String getTypefacePath() {
        return TypefacePath;
    }


    public void setTextBitmapStyle(String path, String effectsTitle) {
        textEffectTitle = effectsTitle;
        getTypefaceBitmapPath = path;
        isChooseTextBjEffect = true;
        bpForTextBj = BitmapFactory.decodeFile(path);
        mOpenThePattern = false;
        textFrameTitle = "";
    }

    public boolean getIsTextSticker() {
        return mStickerType == CODE_STICKER_TYPE_TEXT;
    }

    public String getTypefaceBitmapPath() {
        return getTypefaceBitmapPath;
    }

    public String getBjFramePath() {
        return textFramePath;
    }


    public void disMissFrame() {
        handler.sendEmptyMessage(DISMISS_FRAME);
    }


    public void setTextScale(float scale) {

        mScale = scale;
    }


    public void setTextAngle(float angle) {
        mRotateAngle = angle;
    }


    private ArrayList<String> colors = new ArrayList<>();

    public void setTextPaintColor(String paintColor1, String paintColor2, String effectsTitle) {
        textEffectTitle = effectsTitle;
        if (bpForTextBj != null) {
            bpForTextBj.recycle();
            bpForTextBj = null;
        }
        isChooseTextBjEffect = false;
        mTextPaint.setShader(null);
        mTextPaint.setColor(Color.parseColor(paintColor1));
        mTextPaint2.setColor(Color.parseColor(paintColor2));
        colors.clear();
        colors.add(paintColor1);
        colors.add(paintColor2);
        mOpenThePattern = false;
        textFrameTitle = "";
    }


    public void changeTextFrame(String textBjPath, String textFramePath, String frameTitle) {
        this.textFramePath = textFramePath;
        getTypefaceBitmapPath = textBjPath;
        this.textFrameTitle = frameTitle;
        isChooseTextBjEffect = true;
        bpForTextBj = BitmapFactory.decodeFile(textBjPath);
        mOpenThePattern = true;
        bpTestTextBj = BitmapFactory.decodeFile(textFramePath);
    }


    public void changeTextFrame(String color0, String color1, String textFramePath, String frameTitle) {
        setTextPaintColor(color0, color1, "");
        mOpenThePattern = true;
        bpTestTextBj = BitmapFactory.decodeFile(textFramePath);
        this.textFramePath = textFramePath;
        this.textFrameTitle = frameTitle;
    }


    public String getTextEffectTitle() {
        return textEffectTitle;
    }


    public String getTextStyleTitle() {
        return textStyleTitle;
    }

    public String getTextFrameTitle() {
        return textFrameTitle;
    }

    public boolean getIsChooseTextBjEffect() {
        return isChooseTextBjEffect;
    }


    public boolean getOpenThePattern() {
        return mOpenThePattern;
    }

    public ArrayList<String> getTextColors() {
        return colors;
    }


    /**
     * * description ：在保存的时候，如果有文字功能，并且超过了屏幕外，那么截屏的时候会出现截屏出问题，蓝松位置计算有问题
     * * 这里修改为，在保存的时候默认把位置居中，然后等比例放大到屏幕的位置，截了图在恢复正常
     * * creation date: 2020/9/25
     * * user : zhangtongju
     */
    private float keepToScreenScale;
    private float[] keepToCenter = new float[2];
    private boolean hasChangeTextPosition = false;
    private boolean hasChangeTextScale = false;

    public void changePositionToScreenShot() {
        if (mStickerType == CODE_STICKER_TYPE_TEXT) {
//            if (mHelpBoxRect.left < 0 || mHelpBoxRect.right > getMeasuredWidth() || mHelpBoxRect.top < 0 || mHelpBoxRect.bottom > getMeasuredHeight()) {
            keepToScreenScale = mScale;
            hasChangeTextPosition = true;
            keepToCenter[0] = center.x;
            keepToCenter[1] = center.y;
            setCenter(getMeasuredWidth() / (float) 2, getMeasuredHeight() / (float) 2);
            float width = mHelpBoxRect.width();
            if (width > getMeasuredWidth()) {
                hasChangeTextScale = true;
                float scale = getMeasuredWidth() / width;
                setScale(keepToScreenScale * scale);
            }
//            }
        }
    }

    public void restoreToScreenShot() {
        if (mStickerType == CODE_STICKER_TYPE_TEXT && hasChangeTextPosition) {
            setCenter(keepToCenter[0], keepToCenter[1]);
            hasChangeTextPosition = false;
            if (hasChangeTextScale) {
                setScale(keepToScreenScale);
                hasChangeTextScale = false;
            }
        }
    }


    public Bitmap bitmapToCenter(Bitmap bitmap, int width, int height) {
        int bmpWidth = bitmap.getWidth();
        int bmpHeight = bitmap.getHeight();
        float scaleWidth = ((float) width) / bmpWidth;
        Bitmap target = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas temp_canvas = new Canvas(target);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleWidth);
        int tranH = (int) (bmpHeight * scaleWidth - height);
        if (tranH > 0) {
            tranH = Math.abs(tranH) / 2;
            tranH = -tranH;

        } else {
            tranH = Math.abs(tranH) / 2;
        }
        matrix.postTranslate(0, tranH);
        temp_canvas.drawBitmap(bitmap, matrix, new Paint());
        return target;
    }


    private List<String> getFontList(String str) {
        listFontList.clear();
        for (int i = 0; i < str.length(); i++) {
            String ss = String.valueOf(str.charAt(i));
            listFontList.add(ss);
        }
        return listFontList;
    }


    public long getShowStickerStartTime() {
        return showStickerStartTime;
    }

    public void setShowStickerStartTime(long showStickerStartTime) {
        this.showStickerStartTime = showStickerStartTime;
    }

    public long getShowStickerEndTime() {
        return showStickerEndTime;
    }

    public void setShowStickerEndTime(long showStickerEndTime) {
        this.showStickerEndTime = showStickerEndTime;
    }

    public AnimType getChooseAnimId() {
        return ChooseAnimId;
    }

    public void setChooseAnimId(AnimType chooseAnimId) {
        ChooseAnimId = chooseAnimId;
    }

    public String getDownStickerTitle() {
        return downStickerTitle;
    }

    public void setDownStickerTitle(String downStickerTitle) {
        this.downStickerTitle = downStickerTitle;
    }

    public int getStickerNoIncludeAnimId() {
        return stickerNoIncludeAnimId;
    }

    public void setStickerNoIncludeAnimId(int stickerNoIncludeAnimId) {
        this.stickerNoIncludeAnimId = stickerNoIncludeAnimId;
    }

    public void setMaskBitmap(Bitmap maskBitmap) {
        mMaskBitmap = maskBitmap;
    }
}