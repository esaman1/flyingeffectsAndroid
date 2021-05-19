package com.imaginstudio.imagetools.pixellab.TextObject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

public class superCanvas extends Canvas {
    public superCanvas(Bitmap bitmap) {
        super(bitmap);
    }

    public superCanvas() {
    }

    @Override // android.graphics.Canvas
    public boolean clipRect(RectF rect, Region.Op op) {
        return false;
    }

    @Override // android.graphics.Canvas
    public boolean clipRect(Rect rect, Region.Op op) {
        return false;
    }

    @Override // android.graphics.Canvas
    public boolean clipRect(RectF rect) {
        return false;
    }

    @Override // android.graphics.Canvas
    public boolean clipRect(Rect rect) {
        return false;
    }

    public boolean clipRect(float left, float top, float right, float bottom, Region.Op op) {
        return false;
    }

    @Override // android.graphics.Canvas
    public boolean clipRect(float left, float top, float right, float bottom) {
        return false;
    }

    @Override // android.graphics.Canvas
    public boolean clipRect(int left, int top, int right, int bottom) {
        return false;
    }
}
