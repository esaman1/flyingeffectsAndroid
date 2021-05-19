package com.imaginstudio.imagetools.pixellab;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class DrawingPanelRenderer {
    private static int getBitmapTopPadding(Bitmap src) {
        int w = src.getWidth();
        int h = src.getHeight();
        int[] pixels = new int[w];
        for (int vPos = 0; vPos < h; vPos++) {
            src.getPixels(pixels, 0, w, 0, vPos, w, 1);
            for (int color : pixels) {
                if (Color.alpha(color) != 0) {
                    if (vPos - 1 > 0) {
                        return vPos - 1;
                    } else {
                        return 0;
                    }
                }
            }
        }
        return -1;
    }

    private static int getBitmapLeftPadding(Bitmap src) {
        int w = src.getWidth();
        int h = src.getHeight();
        int[] pixels = new int[h];
        for (int hPos = 0; hPos < w; hPos++) {
            src.getPixels(pixels, 0, 1, hPos, 0, 1, h);
            for (int color : pixels) {
                if (Color.alpha(color) != 0) {
                    if (hPos - 1 > 0) {
                        return hPos - 1;
                    } else {
                        return 0;
                    }
                }
            }
        }
        return -1;
    }

    private static int getBitmapRightPadding(Bitmap src) {
        int w = src.getWidth();
        int h = src.getHeight();
        int[] pixels = new int[h];
        for (int hPos = w - 1; hPos >= 0; hPos--) {
            src.getPixels(pixels, 0, 1, hPos, 0, 1, h);
            for (int color : pixels) {
                if (Color.alpha(color) != 0) {
                    return hPos + 1 < src.getWidth() ? hPos + 1 : src.getWidth();
                }
            }
        }
        return -1;
    }

    private static int getBitmapBottomPadding(Bitmap src) {
        int w = src.getWidth();
        int[] pixels = new int[w];
        for (int vPos = src.getHeight() - 1; vPos >= 0; vPos--) {
            src.getPixels(pixels, 0, w, 0, vPos, w, 1);
            for (int color : pixels) {
                if (Color.alpha(color) != 0) {
                    return vPos + 1 < src.getHeight() ? vPos + 1 : src.getHeight();
                }
            }
        }
        return -1;
    }

//    public static Bitmap getBitmapFromDrawing(DrawingPanel panel, int scaleF) {
//        Bitmap bmOut = Bitmap.createBitmap(panel.getWidth() * scaleF, panel.getHeight() * scaleF, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bmOut);
//        canvas.scale((float) scaleF, (float) scaleF);
//        panel.draw(canvas);
//        return bmOut;
//    }

    public static Rect getAutoCropBound(Bitmap src) {
        int top = getBitmapTopPadding(src);
        return new Rect(getBitmapLeftPadding(src), top, getBitmapRightPadding(src), getBitmapBottomPadding(src));
    }

    public static Bitmap autoCropBitmap(Bitmap src, Rect bounds) {
        Bitmap bmOut = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888);
        new Canvas(bmOut).drawBitmap(src, (float) (-bounds.left), (float) (-bounds.top), new Paint(2));
        return bmOut;
    }
}
