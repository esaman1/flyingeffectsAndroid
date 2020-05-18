package com.lansosdk.videoeditor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lansosdk.box.LSOLayer;

import java.util.LinkedHashMap;

/**
 * 贴图操作控件
 */
public class LSOLayerTouchView extends View {
    private static int STATUS_IDLE = 0;
    private static int STATUS_MOVE = 1;// 移动状态
    private static int STATUS_DELETE = 2;// 删除状态
    private static int STATUS_ROTATE = 3;// 图片旋转状态

    private int imageCount;// 已加入照片的数量
    private int currentStatus;// 当前状态
    private LSOLayerTouch currentItem;// 当前操作的贴图数据
    private float oldX, oldY;

    private Paint rectPaint = new Paint();
    private Paint boxPaint = new Paint();

    private LinkedHashMap<Integer, LSOLayerTouch> layerMap = new LinkedHashMap<Integer, LSOLayerTouch>();// 存贮每层贴图数据
    private float heightRatio =1.0f;
    private float widthRatio=1.0f;

    public LSOLayerTouchView(Context context) {
        super(context);
        init(context);
    }

    public LSOLayerTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LSOLayerTouchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        currentStatus = STATUS_IDLE;
        //设置画笔的属性
        rectPaint.setColor(Color.RED);

        rectPaint.setAlpha(100);

    }

    private LSOConcatCompositionView compView;

    public void setVideoCompositionView(LSOConcatCompositionView compView){
        if(compView!=null){
            this.compView=compView;
            this.widthRatio = (float) compView.getWidth() / compView.getCompWidth();
            this.heightRatio = (float) compView.getHeight() / compView.getCompHeight();
        }
    }

    public void addLayer(final LSOLayer layer) {

        if(layer!=null && !layer.isRemovedFromComp()){
            LSOLayerTouch item = new LSOLayerTouch(this.getContext());

            item.init(layer, compView);

            if (currentItem != null) {
                currentItem.drawHelpTool = false;
            }
            currentItem = item;
            layerMap.put(++imageCount, item);
            this.invalidate();// 重绘视图
        }
    }

    /**
     * 绘制客户页面
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Integer id : layerMap.keySet()) {
            LSOLayerTouch item = layerMap.get(id);
            item.draw(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);// 是否向下传递事件标志 true为消耗

        int action = event.getAction();
        //当前用户的XY值；
        float x = event.getX();
        float y = event.getY();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                int deleteId = -1;
                for (Integer id : layerMap.keySet()) {

                    LSOLayerTouch item = layerMap.get(id);//拿到当前贴图
                    if (item.detectDeleteRect.contains(x, y)) {// 删除模式
                        // ret = true;
                        deleteId = id;
                        currentStatus = STATUS_DELETE;//你点到了左上角那么就删除
                    } else if (item.detectRotateRect.contains(x, y)) {// 点击了旋转按钮
                        ret = true;
                        // 当前操作的贴图数据
                        if (currentItem != null) {
                            //是否绘制辅助线？
                            currentItem.drawHelpTool = false;
                        }
                        currentItem = item;
                        currentItem.drawHelpTool = true;
                        currentStatus = STATUS_ROTATE;//转化旋转模式
                        oldX = x;
                        oldY = y;
                    } else if (item.dstRect.contains(x, y)) {// 移动模式
                        // 被选中一张贴图
                        ret = true;
                        if (currentItem != null) {
                            currentItem.drawHelpTool = false;
                        }
                        currentItem = item;
                        currentItem.drawHelpTool = true;
                        currentStatus = STATUS_MOVE;
                        oldX = x;
                        oldY = y;
                    }// end if
                }
                //点击之后的选择
                if (!ret && currentItem != null && currentStatus == STATUS_IDLE) {// 没有贴图被选择
                    currentItem.drawHelpTool = false;
                    currentItem = null;
                    invalidate();
                }

                if (deleteId > 0 && currentStatus == STATUS_DELETE) {// 删除选定贴图
                    LSOLayerTouch item= layerMap.get(deleteId);
                    item.removeLayer();
                    layerMap.remove(deleteId);
                    currentStatus = STATUS_IDLE;// 返回空闲状态
                    disappearIconBorder(); // 移除border
                }

                break;
            case MotionEvent.ACTION_MOVE:
                ret = true;
                if (currentStatus == STATUS_MOVE) {// 移动贴图
                    float dx = x - oldX;
                    float dy = y - oldY;
                    //你手指按下的位置
                    if (currentItem != null) {
                        //更新框和按钮的位置
                        currentItem.updatePos(dx, dy,widthRatio,heightRatio);
                        //重新绘制
                        invalidate();
                    }// end if
                    oldX = x;
                    oldY = y;
                } else if (currentStatus == STATUS_ROTATE) {// 旋转 缩放图片操作
                    float dx = x - oldX;
                    float dy = y - oldY;
                    if (currentItem != null) {
                        currentItem.updateRotateAndScale(oldX, oldY, dx, dy);// 旋转
                        invalidate();
                    }// end if
                    oldX = x;
                    oldY = y;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                ret = false;
                currentStatus = STATUS_IDLE;
                break;
        }// end switch
        return ret;
    }


    /**
     * 消除左上角的删除按钮, 右下角的拖动图标, 和黑色的边框.
     */
    public void disappearIconBorder() {
        if (currentItem != null) {
            currentItem.drawHelpTool = false;
        }
        currentItem = null;
        invalidate();
    }

    /**
     * 清除所有的贴纸
     */
    public void clear() {
        layerMap.clear();
        this.invalidate();
    }

    //LSTODO:
    /**
     * 更新是否要显示的边框;
     */
    public void updateLayerStatus(){
        boolean needUpdate=false;

        for (Integer id : layerMap.keySet()) {
            LSOLayerTouch item = layerMap.get(id);
            if(item.getLayer()!=null){
                if(!item.getLayer().isDisplayAtCurrentTime() && item.drawHelpTool){
                    item.drawHelpTool=false;
                    needUpdate=true;
                }
            }
        }
        if(needUpdate){
            invalidate();
        }

    }
}// end class
