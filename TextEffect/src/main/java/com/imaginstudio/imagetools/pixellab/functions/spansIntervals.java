package com.imaginstudio.imagetools.pixellab.functions;

import androidx.core.view.ViewCompat;

import java.util.ArrayList;
import java.util.List;

public class spansIntervals {
    List<interval> intervals = new ArrayList();
    List<interval> tmpIntervals = new ArrayList();

    public void clear() {
        this.intervals.clear();
    }

    public interval getLastAttr() {
        if (this.intervals.size() > 0) {
            return this.intervals.get(this.intervals.size() - 1);
        }
        return null;
    }

    public int getLastColor() {
        return getLastAttr() != null ? getLastAttr().getColor() : ViewCompat.MEASURED_STATE_MASK;
    }

    public void addInterval(interval intervalIds) {
        if (intervalIds.getBase().isInterval()) {
            for (interval i : this.intervals) {
                if (!intervalIds.getBase().contains(i.getBase())) {
                    this.tmpIntervals.add(i);
                }
            }
            this.intervals = new ArrayList(this.tmpIntervals);
            this.tmpIntervals.clear();
            this.intervals.add(intervalIds);
        }
    }

    public void addIntervalNoCheck(interval intervalIds) {
        this.intervals.add(intervalIds);
    }

    public void updateEnds(int oldEnd, int newEnd) {
        for (interval i : this.intervals) {
            if (i.getBase().getEnd() == oldEnd) {
                i.getBase().setEnd(newEnd);
            }
        }
    }

    public void removeInterval(int start, int end) {
        for (interval i : this.intervals) {
            if (i.getBase().isNoOverlapInterval(start, end)) {
                this.tmpIntervals.add(i);
            } else {
                for (baseInterval j : i.getBase().removeInterval(new baseInterval(start, end))) {
                    if (j.isInterval()) {
                        this.tmpIntervals.add(new interval(j.getStart(), j.getEnd()));
                    }
                }
            }
        }
        this.intervals = new ArrayList(this.tmpIntervals);
        this.tmpIntervals.clear();
    }

    public List<interval> getIntervals() {
        return this.intervals;
    }

    public void setIntervals(List<interval> intervals2) {
        this.intervals = intervals2;
    }

    public boolean isCoverAll(int textLength) {
        if (this.intervals.size() <= 0 || this.intervals.get(this.intervals.size() - 1).getBase() == null || this.intervals.get(this.intervals.size() - 1).getBase().getLength() != textLength) {
            return false;
        }
        return true;
    }

    public spansIntervals copy() {
        List<interval> intervalsCopy = new ArrayList<>();
        for (interval i : this.intervals) {
            intervalsCopy.add(i.copy());
        }
        spansIntervals spansCopy = new spansIntervals();
        spansCopy.setIntervals(intervalsCopy);
        return spansCopy;
    }
}
