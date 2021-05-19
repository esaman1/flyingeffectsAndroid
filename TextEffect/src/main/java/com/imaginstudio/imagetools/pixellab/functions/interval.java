package com.imaginstudio.imagetools.pixellab.functions;

import com.imaginstudio.imagetools.pixellab.font.customTypeface;

public class interval {
    private baseInterval base;
    private int color = 0;
    private customTypeface font;

    public interval(int start, int end) {
        this.base = new baseInterval(start, end);
    }

    public interval(int start, int end, int color2) {
        this.base = new baseInterval(start, end);
        this.color = color2;
    }

    public interval(int start, int end, customTypeface font2) {
        this.base = new baseInterval(start, end);
        this.font = font2;
    }

    public int getEnd() {
        return this.base.getEnd();
    }

    public int getStart() {
        return this.base.getStart();
    }

    public baseInterval getBase() {
        return this.base;
    }

    public int getColor() {
        return this.color;
    }

    public void setBase(baseInterval base2) {
        this.base = base2;
    }

    public void setColor(int color2) {
        this.color = color2;
    }

    public customTypeface getFont() {
        return this.font;
    }

    public void setFont(customTypeface font2) {
        this.font = font2;
    }

    public void setStart(int start) {
        this.base.setStart(start);
    }

    /* access modifiers changed from: package-private */
    public interval copy() {
        interval copy = new interval(getStart(), getEnd());
        copy.setColor(getColor());
        copy.setFont(getFont());
        return copy;
    }
}
