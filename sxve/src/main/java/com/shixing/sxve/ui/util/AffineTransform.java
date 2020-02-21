package com.shixing.sxve.ui.util;

import android.graphics.Matrix;
import android.graphics.PointF;

/**
 * Created by zhiqiangli on 24/11/2017.
 */

public class AffineTransform {
    public float mA = 0 ,
            mB = 0,
            mC = 0,
            mD = 0,
            mE = 0,
            mF = 0;
    public static final float degreesToRadiansFactor = 0.017453292519943f;


    public AffineTransform(boolean identitfy)
    {
        if (identitfy)
        {
            mA = mD = 1;
        }
    }

    public AffineTransform()
    {
        this(true);
    }

    public AffineTransform(float a, float b, float c, float d, float e, float f)
    {
        mA = a;
        mB = b;
        mC = c;
        mD = d;
        mE = e;
        mF = f;
    }

    public AffineTransform(AffineTransform other)
    {
        mA = other.mA;
        mB = other.mB;
        mC = other.mC;
        mD = other.mD;
        mE = other.mE;
        mF = other.mF;
    }

    public void setIdentity()
    {
        mA = 1.f;
        mD = 1.f;
        mB = 0.f;
        mC = 0.f;
        mE = 0.f;
        mF = 0.f;
    }

    public void set(Matrix matrix)
    {
        float[] values = new float[9];
        matrix.getValues(values);
        set(values[0], values[3], values[1], values[4], values[2], values[5]);
    }

    public Matrix getMatrix()
    {
        float[] values = new float[9];
        values[0] = mA;
        values[1] = mC;
        values[2] = mE;
        values[3] = mB;
        values[4] = mD;
        values[5] = mF;
        values[6] = 0;
        values[7] = 0;
        values[8] = 1;

        Matrix m = new Matrix();
        m.setValues(values);
        return m;
    }

    public void set(float a, float b, float c, float d, float e, float f)
    {
        mA = a;
        mB = b;
        mC = c;
        mD = d;
        mE = e;
        mF = f;
    }

    public void set(PointF anchor, PointF position, PointF scale, float rotation)
    {
        setIdentity();

        float cs = (float) Math.cos(rotation), sn = (float) Math.sin(rotation);
        mA = cs * scale.x;
        mB = sn * scale.x;
        mC = -sn* scale.y;
        mD = cs * scale.y;
        mE = position.x;
        mF = position.y;
        translate(-anchor.x, -anchor.y);
    }

    public void setTranslate(float tx, float ty) {
        mA = mD = 1.f;
        mB = mC = 0.f;
        mE = tx;
        mF = ty;
    }

    public void setScale(float sx, float sy) {
        mA = sx;
        mD = sy;
        mB = mC = mE = mF = 0.f;
    }

    public void setSkewX(float a) {
        a = a * degreesToRadiansFactor;
        mA = 1.f; mB = 0.f;
        mC = (float) Math.tan(a);
        mD = 1.f;
        mE = mF = 0.f;
    }

    public void setSkewY(float a) {
        a = a * degreesToRadiansFactor;
        mA = 1.f; mB = (float) Math.tan(a);
        mC = 0.f; mD = 1.f;
        mE = mF = 0.f;
    }

    public void setRotate(float a) {
        a = a * degreesToRadiansFactor;
        float cs = (float) Math.cos(a), sn = (float) Math.sin(a);
        mA = cs; mB = sn;
        mC = -sn; mD = cs;
        mE = mF = 0.f;
    }

    public void multiply( AffineTransform other) {
        float t0 = mA * other.mA + mB * other.mC;
        float t2 = mC * other.mA + mD * other.mC;
        float t4 = mE * other.mA + mF * other.mC + other.mE;
        mB = mA * other.mB + mB * other.mD;
        mD = mC * other.mB + mD * other.mD;
        mF = mE * other.mB + mF * other.mD + other.mF;
        mA = t0;
        mC = t2;
        mE = t4;
    }

    public void premultiply(AffineTransform other) {
        AffineTransform s2 = new AffineTransform(other);
        s2.multiply(this);
        mA = s2.mA;
        mB = s2.mB;
        mC = s2.mC;
        mD = s2.mD;
        mE = s2.mE;
        mF = s2.mF;
    }

    public boolean inverse(AffineTransform out) {
        double inv_det, det = (double)mA * mD - (double)mC * mB;
        if (det > -1e-6 && det < 1e-6)
        {
            return false;
        }

        inv_det = 1.0 / det;
        out.mA = (float)(mD * inv_det);
        out.mC = (float)(-mC * inv_det);
        out.mE = (float)(((double)mC * mF - (double)mD * mE) * inv_det);
        out.mB = (float)(-mB * inv_det);
        out.mD = (float)(mA * inv_det);
        out.mF = (float)(((double)mB * mE - (double)mA * mF) * inv_det);
        return true;
    }

    public AffineTransform getInverse()
    {
        double inv_det, det = (double)mA * mD - (double)mC * mB;
        if (det > -1e-6 && det < 1e-6)
        {
            return new AffineTransform();
        }

        inv_det = 1.0 / det;
        float a = (float)(mD * inv_det);
        float c = (float)(-mC * inv_det);
        float e = (float)(((double)mC * mF - (double)mD * mE) * inv_det);
        float b = (float)(-mB * inv_det);
        float d = (float)(mA * inv_det);
        float f = (float)(((double)mB * mE - (double)mA * mF) * inv_det);

        return new AffineTransform(a,b,c,d,e,f);
    }

    public void translate(float x, float y)
    {
        AffineTransform t = new AffineTransform();
        t.setTranslate(x, y);
        premultiply(t);
    }

    public void scale(float x, float y)
    {
        AffineTransform t = new AffineTransform();
        t.setScale(x, y);
        premultiply(t);
    }

    public void skewX(float a)
    {
        AffineTransform t = new AffineTransform();
        t.setSkewX(a);
        premultiply(t);
    }

    public void skewY(float a)
    {
        AffineTransform t = new AffineTransform();
        t.skewX(a);
        premultiply(t);
    }

    public void rotate(float a)
    {
        AffineTransform t = new AffineTransform();
        t.setRotate(a);
        premultiply(t);
    }

    public PointF transform(PointF point)
    {
        return new PointF( (float)(mA * point.x + mC * point.y + mE),
        (float)(mB * point.x + mD * point.y + mF));
    }

    public PointF transform(float x, float y)
    {
        return new PointF( (float)(mA * x + mC * y + mE),
                (float)(mB * x + mD * y + mF));
    }
}
