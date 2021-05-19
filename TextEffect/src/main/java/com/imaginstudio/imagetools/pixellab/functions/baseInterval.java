package com.imaginstudio.imagetools.pixellab.functions;

import java.util.ArrayList;
import java.util.List;

public class baseInterval {
    int end;
    int start;

    public baseInterval(int start2, int end2) {
        this.start = start2;
        this.end = end2;
    }

    public int getLength() {
        return Math.max(getEnd() - getStart(), 0);
    }

    public boolean isInterval() {
        return this.start < this.end;
    }

    public boolean contains(baseInterval c) {
        return getStart() <= c.getStart() && getEnd() >= c.getEnd();
    }

    public boolean isNoOverlapInterval(int startO, int endO) {
        return endO < getStart() || startO > getEnd();
    }

    public String describe() {
        return getStart() + "-->" + getEnd();
    }

    public List<baseInterval> removeInterval(baseInterval toRemove) {
        List<baseInterval> result = new ArrayList<>();
        result.add(new baseInterval(getStart(), toRemove.getStart()));
        result.add(new baseInterval(toRemove.getEnd(), getEnd()));
        return result;
    }

    /* access modifiers changed from: package-private */
    public baseInterval intersect(baseInterval A, baseInterval B) {
        return new baseInterval(Math.max(A.getStart(), B.getStart()), Math.min(A.getEnd(), B.getEnd()));
    }

    public int getEnd() {
        return this.end;
    }

    public void setEnd(int end2) {
        this.end = end2;
    }

    public int getStart() {
        return this.start;
    }

    public void setStart(int start2) {
        this.start = start2;
    }
}
