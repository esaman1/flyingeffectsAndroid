package com.imaginstudio.imagetools.pixellab.functions;

public class Line2D {
    private float x1 = 0.0f;
    private float x2 = 0.0f;
    private float y1 = 0.0f;
    private float y2 = 0.0f;

    public Line2D(float x12, float y12, float x22, float y22) {
        set(x12, y12, x22, y22);
    }

    public Line2D() {
    }

    public void set(float x12, float y12, float x22, float y22) {
        this.x1 = x12;
        this.y1 = y12;
        this.x2 = x22;
        this.y2 = y22;
    }

    public float getX1() {
        return this.x1;
    }

    public float getY1() {
        return this.y1;
    }

    public float getX2() {
        return this.x2;
    }

    public float getY2() {
        return this.y2;
    }
}
