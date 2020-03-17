package com.flyingeffects.com.view.lansongCommendView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.widget.VideoView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.utils.LogUtil;


/**
 * @author panyi
 */
public class StickerItem {
    private static final float MIN_SCALE = 0.15f;
    private static final int HELP_BOX_PAD = 25;

    private static final int BUTTON_WIDTH = 30;
    private static Bitmap deleteBit;
    private static Bitmap rotateBit;
    private static Bitmap deleteAdd;
    private static Bitmap rotateReplace;
    private VideoView videoView;
    private float tranX;
    private float tranY;

    public Bitmap bitmap;
    public Rect srcRect;// 原始图片坐标
    public RectF dstRect;// 绘制目标坐标
    public RectF deleteRect;// 删除按钮位置
    public RectF rotateRect;// 旋转按钮位置
    public RectF addRect;// 添加按钮位置
    public RectF changeRect;// 变换按钮位置
    public Matrix matrix;// 变化矩阵
    public RectF detectRotateRect;//旋转按钮的范围
    public RectF detectDeleteRect;//删除按钮的范围
    public RectF detectAddRect;//添加按钮的范围
    public RectF detectChangeRect;//替换按钮的范围

    RectF helpBox;
    boolean isDrawHelpTool = false;
    private Rect helpToolsRect;
    private float scaleSize;
    private float roatetAngle = 0;
    private Paint dstPaint = new Paint();
    private Paint paint = new Paint();
    private Paint helpBoxPaint = new Paint();
    private float initWidth;// 加入屏幕时原始宽度
    //整个图层移动的位置
    private float tranLayerX;
    private float tranLayerY;
    private Paint greenPaint = new Paint();
    //原始缩放值
    private float originalScale;

    public StickerItem(Context context) {

        helpBoxPaint.setColor(Color.BLACK);
        helpBoxPaint.setStyle(Style.STROKE);
        helpBoxPaint.setAntiAlias(true);
        helpBoxPaint.setStrokeWidth(4);

        dstPaint = new Paint();
        dstPaint.setColor(Color.RED);
        dstPaint.setAlpha(120);

        greenPaint = new Paint();
        greenPaint.setColor(Color.GREEN);
        greenPaint.setAlpha(120);

        // 导入工具按钮位图
        if (deleteBit == null) {
            deleteBit = BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.sticker_close);
        }// end if
        if (rotateBit == null) {
            rotateBit = BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.sticker_redact);
        }// end if


        if (deleteAdd == null) {
            deleteAdd = BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.sticker_copy);
        }// end if


        if (rotateReplace == null) {
            rotateReplace = BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.sticker_change);
        }// end if

    }

    public void init(Bitmap addBit, View parentView) {
        this.bitmap = addBit;
        LogUtil.d("OOM","parentView="+parentView.getWidth());
        originalScale=addBit.getWidth()/(float)parentView.getWidth();
        if(originalScale>1){
            originalScale=1-originalScale;
        }
        this.srcRect = new Rect(0, 0, addBit.getWidth(), addBit.getHeight());
        int bitWidth = Math.min(addBit.getWidth(), parentView.getWidth() >> 1);
        int bitHeight = (int) bitWidth * addBit.getHeight() / addBit.getWidth();
        int left = (parentView.getWidth() >> 1) - (bitWidth >> 1);
        int top = (parentView.getHeight() >> 1) - (bitHeight >> 1);
        this.dstRect = new RectF(left, top, left + bitWidth, top + bitHeight);
        this.matrix = new Matrix();
        this.matrix.postTranslate(this.dstRect.left, this.dstRect.top);
        this.matrix.postScale((float) bitWidth / addBit.getWidth(),
                (float) bitHeight / addBit.getHeight(), this.dstRect.left,
                this.dstRect.top);
        initWidth = this.dstRect.width();// 记录原始宽度
        // item.matrix.setScale((float)bitWidth/addBit.getWidth(),
        // (float)bitHeight/addBit.getHeight());
        this.isDrawHelpTool = true;
        this.helpBox = new RectF(this.dstRect);
        updateHelpBoxRect();

        helpToolsRect = new Rect(0, 0, deleteBit.getWidth(),
                deleteBit.getHeight());

        deleteRect = new RectF(helpBox.left - BUTTON_WIDTH, helpBox.top
                - BUTTON_WIDTH, helpBox.left + BUTTON_WIDTH, helpBox.top
                + BUTTON_WIDTH);
        rotateRect = new RectF(helpBox.right - BUTTON_WIDTH, helpBox.bottom
                - BUTTON_WIDTH, helpBox.right + BUTTON_WIDTH, helpBox.bottom
                + BUTTON_WIDTH);

        addRect = new RectF(helpBox.right - BUTTON_WIDTH, helpBox.top
                - BUTTON_WIDTH, helpBox.right + BUTTON_WIDTH, helpBox.top
                + BUTTON_WIDTH);


        changeRect = new RectF(helpBox.left - BUTTON_WIDTH, helpBox.bottom
                - BUTTON_WIDTH, helpBox.left + BUTTON_WIDTH, helpBox.bottom
                + BUTTON_WIDTH);


        detectRotateRect = new RectF(rotateRect);
        detectDeleteRect = new RectF(deleteRect);

        detectAddRect = new RectF(addRect);
        detectChangeRect = new RectF(changeRect);
    }











    private void updateHelpBoxRect() {
        this.helpBox.left -= HELP_BOX_PAD;
        this.helpBox.right += HELP_BOX_PAD;
        this.helpBox.top -= HELP_BOX_PAD;
        this.helpBox.bottom += HELP_BOX_PAD;
    }

    /**
     * 位置更新
     *
     * @param dx
     * @param dy
     */
    public void updatePos(final float dx, final float dy) {
        this.matrix.postTranslate(dx, dy);// 记录到矩阵中

        LogUtil.d("OOM","dx="+dx);

        tranX=dx;
        tranY=dy;
        dstRect.offset(dx, dy);

        // 工具按钮随之移动
        helpBox.offset(dx, dy);
        deleteRect.offset(dx, dy);
        rotateRect.offset(dx, dy);
        addRect.offset(dx, dy);
        changeRect.offset(dx, dy);

        this.detectRotateRect.offset(dx, dy);
        this.detectDeleteRect.offset(dx, dy);

        this.detectAddRect.offset(dx, dy);
        this.detectChangeRect.offset(dx, dy);


    }

    /**
     * 旋转 缩放 更新
     *
     * @param dx
     * @param dy
     */
    public void updateRotateAndScale(final float oldx, final float oldy,
                                     final float dx, final float dy) {
        float c_x = dstRect.centerX();
        float c_y = dstRect.centerY();

        float x = this.detectRotateRect.centerX();
        float y = this.detectRotateRect.centerY();

        // float x = oldx;
        // float y = oldy;

        float n_x = x + dx;
        float n_y = y + dy;

        float xa = x - c_x;
        float ya = y - c_y;

        float xb = n_x - c_x;
        float yb = n_y - c_y;

        float srcLen = (float) Math.sqrt(xa * xa + ya * ya);
        float curLen = (float) Math.sqrt(xb * xb + yb * yb);

        // System.out.println("srcLen--->" + srcLen + "   curLen---->" +
        // curLen);

        scaleSize = curLen / srcLen;// 计算缩放比

        float newWidth = dstRect.width() * scaleSize;
        if (newWidth / initWidth < MIN_SCALE) {// 最小缩放值检测
            return;
        }

        this.matrix.postScale(scaleSize, scaleSize, this.dstRect.centerX(),
                this.dstRect.centerY());// 存入scale矩阵
        // this.matrix.postRotate(5, this.dstRect.centerX(),
        // this.dstRect.centerY());
        RectUtil.scaleRect(this.dstRect, scaleSize);// 缩放目标矩形

        // 重新计算工具箱坐标
        helpBox.set(dstRect);
        updateHelpBoxRect();// 重新计算
        rotateRect.offsetTo(helpBox.right - BUTTON_WIDTH, helpBox.bottom
                - BUTTON_WIDTH);
        deleteRect.offsetTo(helpBox.left - BUTTON_WIDTH, helpBox.top
                - BUTTON_WIDTH);


        addRect.offsetTo(helpBox.right - BUTTON_WIDTH, helpBox.top
                - BUTTON_WIDTH);
        changeRect.offsetTo(helpBox.left - BUTTON_WIDTH, helpBox.bottom
                - BUTTON_WIDTH);


        detectRotateRect.offsetTo(helpBox.right - BUTTON_WIDTH, helpBox.bottom
                - BUTTON_WIDTH);
        detectDeleteRect.offsetTo(helpBox.left - BUTTON_WIDTH, helpBox.top
                - BUTTON_WIDTH);


        detectAddRect.offsetTo(helpBox.right - BUTTON_WIDTH, helpBox.top
                - BUTTON_WIDTH);
        detectChangeRect.offsetTo(helpBox.left - BUTTON_WIDTH, helpBox.bottom
                - BUTTON_WIDTH);


        double cos = (xa * xb + ya * yb) / (srcLen * curLen);
        if (cos > 1 || cos < -1)
            return;
        float angle = (float) Math.toDegrees(Math.acos(cos));
        // System.out.println("angle--->" + angle);

        // 定理
        float calMatrix = xa * yb - xb * ya;// 行列式计算 确定转动方向

        int flag = calMatrix > 0 ? 1 : -1;
        angle = flag * angle;

        // System.out.println("angle--->" + angle);
        roatetAngle += angle;
        this.matrix.postRotate(angle, this.dstRect.centerX(),
                this.dstRect.centerY());

        RectUtil.rotateRect(this.detectRotateRect, this.dstRect.centerX(),
                this.dstRect.centerY(), roatetAngle);
        RectUtil.rotateRect(this.detectDeleteRect, this.dstRect.centerX(),
                this.dstRect.centerY(), roatetAngle);

        RectUtil.rotateRect(this.detectAddRect, this.dstRect.centerX(),
                this.dstRect.centerY(), roatetAngle);
        RectUtil.rotateRect(this.detectChangeRect, this.dstRect.centerX(),
                this.dstRect.centerY(), roatetAngle);


        // System.out.println("angle----->" + angle + "   " + flag);

        // System.out
        // .println(srcLen + "     " + curLen + "    scale--->" + scale);

    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(this.bitmap, this.matrix, null);// 贴图元素绘制
        // canvas.drawRect(this.dstRect, dstPaint);

        if (this.isDrawHelpTool) {// 绘制辅助工具线
            canvas.save();
            canvas.rotate(roatetAngle, helpBox.centerX(), helpBox.centerY());
            canvas.drawRoundRect(helpBox, 10, 10, helpBoxPaint);
            // 绘制工具按钮
            canvas.drawBitmap(deleteBit, helpToolsRect, deleteRect, null);
            canvas.drawBitmap(rotateBit, helpToolsRect, rotateRect, null);
            canvas.drawBitmap(deleteAdd, helpToolsRect, addRect, null);
            canvas.drawBitmap(rotateReplace, helpToolsRect, changeRect, null);

            canvas.restore();
            // canvas.drawRect(deleteRect, dstPaint);
            // canvas.drawRect(rotateRect, dstPaint);
            // canvas.drawRect(detectRotateRect, this.greenPaint);
            // canvas.drawRect(detectDeleteRect, this.greenPaint);
        }// end if

        // detectRotateRect
    }



    /**
     * description ：获得旋转
     * creation date: 2020/3/16
     * user : zhangtongju
     */
    public float getRoatetAngle() {
        return roatetAngle;
    }

    /**
     * description ：获得缩放
     * creation date: 2020/3/16
     * user : zhangtongju
     */
    public float getScaleSize() {
        return scaleSize;
    }


    /**
     * description ：获得平移
     * creation date: 2020/3/16
     * user : zhangtongju
     */
    public Translation getTranslation() {
        Translation translation=new Translation();
        translation.setX(tranLayerX);
        translation.setY(tranLayerY);
        return translation;
    }



    public void setTranslation(float x,float y){
        this.tranLayerX=x;
        this.tranLayerY=y;
    }




}// end class
