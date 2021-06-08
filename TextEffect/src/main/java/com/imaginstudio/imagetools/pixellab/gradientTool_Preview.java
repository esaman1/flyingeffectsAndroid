package com.imaginstudio.imagetools.pixellab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import com.imaginstudio.imagetools.pixellab.GradientMaker;

public class gradientTool_Preview extends View {
    private final int DRAG_ID_HANDLE_END = 2;
    private final int DRAG_ID_HANDLE_START = 1;
    private final int DRAG_ID_NOTHING = -1;
    int arrowHeadSize = dpToPixels(5);
    int dragId = -1;
    public GradientMaker.GradientFill drawingGradient = new GradientMaker.GradientFill();
    final int gridSquare = dpToPixels(5);
    int handleRadius = dpToPixels(25);
    private boolean initializedColors = false;
    PointF pEnd = new PointF();
    PointF pStart = new PointF();
    Paint pnGridBlack = new Paint(1);
    Paint pnGridWhite;
    Paint pnHandles;

    public gradientTool_Preview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.pnGridBlack.setColor(Color.parseColor("#bdbdbd"));
        this.pnGridWhite = new Paint(1);
        this.pnGridWhite.setColor(-1);
        this.pnHandles = new Paint(1);
        this.pnHandles.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setDirection(final int type, final int[] direction) {
        post(new Runnable() {
            /* class com.imaginstudio.imagetools.pixellab.controls.widgets.gradientTool_Preview.AnonymousClass1 */

            public void run() {
                gradientTool_Preview.this.drawingGradient.setV2Direction(type, direction);
                gradientTool_Preview.this.updatePointsFromGradient();
                gradientTool_Preview.this.postInvalidate();
            }
        });
    }

    public void setColors(int[] colors, float[] positions) {
        this.drawingGradient.setV2Colors(colors, positions);
        this.initializedColors = true;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect previewArea = new Rect(0, 0, getWidth(), getHeight());
        if (this.initializedColors) {
            Paint pn = new Paint(1);
            pn.setShader(this.drawingGradient.getShader(this.pStart, this.pEnd));
            canvas.drawRect(previewArea, pn);
            this.pnHandles.setColor(-12303292);
            this.pnHandles.setAlpha(100);
            this.pnHandles.setStrokeWidth((float) dpToPixels(4));
            this.pnHandles.setStyle(Paint.Style.STROKE);
            float lineDistance = dist(this.pStart, this.pEnd) - ((float) (this.handleRadius * 2));
            double lineAngle = Math.atan2((double) (this.pEnd.y - this.pStart.y), (double) (this.pEnd.x - this.pStart.x));
            double lineNormalAngle = lineAngle + 1.5707963267948966d;
            lineEquation mainLine = new lineEquation(lineAngle, this.pStart);
            PointF segmentStart = new PointF();
            segmentStart.set(mainLine.getPointAtDistance((float) this.handleRadius));
            PointF segmentEnd = new PointF();
            segmentEnd.set(mainLine.getPointAtDistance(((float) this.handleRadius) + lineDistance));
            int steps = lineDistance > 0.0f ? Math.min(4, (int) (lineDistance / ((float) (this.arrowHeadSize * 2)))) : 0;
            float stepDistance = lineDistance / ((float) steps);
            for (int i = 1; i <= steps; i++) {
                float currentDistance = (((float) i) * stepDistance) + ((float) this.handleRadius);
                PointF pointOn1 = new PointF();
                pointOn1.set(mainLine.getPointAtDistance(currentDistance - ((float) this.arrowHeadSize)));
                PointF pointOn2 = new PointF();
                pointOn2.set(mainLine.getPointAtDistance(currentDistance));
                PointF pointOut = new PointF();
                lineEquation normalLine = new lineEquation(lineNormalAngle, pointOn1);
                pointOut.set(normalLine.getPointAtDistance((float) this.arrowHeadSize));
                canvas.drawLine(pointOn2.x, pointOn2.y, pointOut.x, pointOut.y, this.pnHandles);
                pointOut.set(normalLine.getPointAtDistance((float) (this.arrowHeadSize * -1)));
                canvas.drawLine(pointOn2.x, pointOn2.y, pointOut.x, pointOut.y, this.pnHandles);
            }
            canvas.drawLine(segmentStart.x, segmentStart.y, segmentEnd.x, segmentEnd.y, this.pnHandles);
            this.pnHandles.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawCircle(this.pStart.x, this.pStart.y, (float) this.handleRadius, this.pnHandles);
            canvas.drawCircle(this.pEnd.x, this.pEnd.y, (float) this.handleRadius, this.pnHandles);
            this.pnHandles.setColor(-1);
            this.pnHandles.setAlpha(230);
            this.pnHandles.setStrokeWidth((float) dpToPixels(2));
            this.pnHandles.setStyle(Paint.Style.STROKE);
            for (int i2 = 1; i2 <= steps; i2++) {
                float currentDistance2 = (((float) i2) * stepDistance) + ((float) this.handleRadius);
                PointF pointOn12 = new PointF();
                pointOn12.set(mainLine.getPointAtDistance(currentDistance2 - ((float) this.arrowHeadSize)));
                PointF pointOn22 = new PointF();
                pointOn22.set(mainLine.getPointAtDistance(currentDistance2));
                PointF pointOut2 = new PointF();
                lineEquation normalLine2 = new lineEquation(lineNormalAngle, pointOn12);
                pointOut2.set(normalLine2.getPointAtDistance((float) this.arrowHeadSize));
                canvas.drawLine(pointOn22.x, pointOn22.y, pointOut2.x, pointOut2.y, this.pnHandles);
                pointOut2.set(normalLine2.getPointAtDistance((float) (this.arrowHeadSize * -1)));
                canvas.drawLine(pointOn22.x, pointOn22.y, pointOut2.x, pointOut2.y, this.pnHandles);
            }
            canvas.drawLine(segmentStart.x, segmentStart.y, segmentEnd.x, segmentEnd.y, this.pnHandles);
            this.pnHandles.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawCircle(this.pStart.x, this.pStart.y, (float) this.handleRadius, this.pnHandles);
            canvas.drawCircle(this.pEnd.x, this.pEnd.y, (float) this.handleRadius, this.pnHandles);
            this.pnHandles.setStyle(Paint.Style.STROKE);
            this.pnHandles.setColor(-12303292);
            canvas.drawCircle(this.pStart.x, this.pStart.y, (float) (this.handleRadius / 4), this.pnHandles);
            canvas.drawCircle(this.pEnd.x, this.pEnd.y, (float) (this.handleRadius / 4), this.pnHandles);
        }
    }

    public GradientMaker.GradientFill getCurrentGradient() {
        return this.drawingGradient;
    }

    class lineEquation {
        double angle;
        float cosAlpha;
        PointF pointAlongLine = new PointF();
        float sinAlpha;
        PointF startingPoint;

        lineEquation(double angle2, PointF startingPoint2) {
            this.angle = angle2;
            this.startingPoint = startingPoint2;
            this.cosAlpha = (float) Math.cos(angle2);
            this.sinAlpha = (float) Math.sin(angle2);
        }

        /* access modifiers changed from: package-private */
        public PointF getPointAtDistance(float distance) {
            this.pointAlongLine.set(this.startingPoint.x + (this.cosAlpha * distance), this.startingPoint.y + (this.sinAlpha * distance));
            return this.pointAlongLine;
        }
    }

    /* access modifiers changed from: package-private */
    public void updateGradientDirectionFromPoints() {
        this.drawingGradient.getGradV2_direction()[0] = (int) ((this.pStart.x / ((float) getWidth())) * 100.0f);
        this.drawingGradient.getGradV2_direction()[1] = (int) ((this.pStart.y / ((float) getHeight())) * 100.0f);
        this.drawingGradient.getGradV2_direction()[2] = (int) ((this.pEnd.x / ((float) getWidth())) * 100.0f);
        this.drawingGradient.getGradV2_direction()[3] = (int) ((this.pEnd.y / ((float) getHeight())) * 100.0f);
    }

    /* access modifiers changed from: package-private */
    public void updatePointsFromGradient() {
        this.pStart.set((((float) this.drawingGradient.getGradV2_direction()[0]) / 100.0f) * ((float) getWidth()), (((float) this.drawingGradient.getGradV2_direction()[1]) / 100.0f) * ((float) getHeight()));
        this.pEnd.set((((float) this.drawingGradient.getGradV2_direction()[2]) / 100.0f) * ((float) getWidth()), (((float) this.drawingGradient.getGradV2_direction()[3]) / 100.0f) * ((float) getHeight()));
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean draggingStart;
        int i = 2;
        boolean draggingEnd = false;
        PointF fingerPos = new PointF(Math.min(Math.max(0.0f, event.getX()), (float) getWidth()), Math.min(Math.max(0.0f, event.getY()), (float) getHeight()));
        switch (event.getAction() & 255) {
            case 0:
                if (dist(fingerPos, this.pStart) <= ((float) this.handleRadius)) {
                    draggingStart = true;
                } else {
                    draggingStart = false;
                }
                if (!draggingStart && dist(fingerPos, this.pEnd) <= ((float) this.handleRadius)) {
                    draggingEnd = true;
                }
                if (draggingStart) {
                    i = 1;
                } else if (!draggingEnd) {
                    i = -1;
                }
                this.dragId = i;
                break;
            case 1:
                this.dragId = -1;
                break;
            case 2:
                if (this.dragId != -1) {
                    if (this.dragId == 1) {
                        this.pStart.set(fingerPos);
                    } else if (this.dragId == 2) {
                        this.pEnd.set(fingerPos);
                    }
                    updateGradientDirectionFromPoints();
                    invalidate();
                    break;
                }
                break;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public int dpToPixels(int dp) {
        return (int) TypedValue.applyDimension(1, (float) dp, getResources().getDisplayMetrics());
    }

    /* access modifiers changed from: package-private */
    public float dist(PointF p1, PointF p2) {
        return (float) Math.sqrt((double) (((p1.x - p2.x) * (p1.x - p2.x)) + ((p1.y - p2.y) * (p1.y - p2.y))));
    }

    public void modifyDirectionsWithDefault(int preset) {
        defaultDirections.modifyGradientWith(this.drawingGradient, preset);
        updatePointsFromGradient();
    }

    public static class defaultDirections {
        public static final int LIN_1 = 1;
        public static final int LIN_2 = 2;
        public static final int LIN_3 = 3;
        public static final int RAD_1 = 4;
        public static final int RAD_2 = 5;
        public static final int RAD_3 = 6;

        public static void modifyGradientWith(GradientMaker.GradientFill gradient, int preset) {
            int currType;
            int[] currDirections;
            switch (preset) {
                case 1:
                    currType = 3;
                    currDirections = new int[]{0, 50, 100, 50};
                    break;
                case 2:
                    currType = 3;
                    currDirections = new int[]{50, 0, 50, 100};
                    break;
                case 3:
                    currType = 3;
                    currDirections = new int[]{0, 0, 100, 100};
                    break;
                case 4:
                    currType = 4;
                    currDirections = new int[]{50, 50, 100, 100};
                    break;
                case 5:
                    currType = 4;
                    currDirections = new int[]{50, 0, 50, 100};
                    break;
                case 6:
                    currType = 4;
                    currDirections = new int[]{0, 50, 100, 50};
                    break;
                default:
                    currType = 3;
                    currDirections = new int[]{0, 50, 100, 50};
                    break;
            }
            gradient.setV2Direction(currType, currDirections);
        }
    }
}
