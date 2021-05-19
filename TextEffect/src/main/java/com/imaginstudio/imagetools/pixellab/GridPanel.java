package com.imaginstudio.imagetools.pixellab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import com.imaginstudio.imagetools.pixellab.functions.Line2D;


public class GridPanel extends View {
    MarkerList HorizontalMarkers = new MarkerList(true);
    MarkerList VerticalMarkers = new MarkerList(false);
    OnGridUpdated controlsListener = null;
    float dX;
    float dY;
    boolean gridEnabled = false;
    float halfCircleRad = commonFuncs.dpToPx(10);
    float markerSelectionRad = commonFuncs.dpToPx(15);
    Paint pntMarker = new Paint(1);
    Paint pntMarkerSelected = new Paint(1);
    Paint pntMarkerSnap = new Paint(1);
    Paint pntMarkerStroke = new Paint(1);
    float prevX;
    float prevY;
    float remainderH = 0.0f;
    float remainderV = 0.0f;
    Marker selectedMarker = null;
    public boolean selectionEnabled = false;
    boolean snapEnabled = false;
    float snapRad = commonFuncs.dpToPx(10);
    Marker snappedToHor = null;
    Marker snappedToVer = null;

    public interface OnGridUpdated {
        void gridSelectionToggled(boolean z);

        void gridSnapToggled(boolean z);

        void gridToggled(boolean z);
    }

    public void toggle() {
        toggle(!this.gridEnabled);
    }

    public void toggle(boolean enabled) {
        this.gridEnabled = enabled;
        setVisibility(this.gridEnabled ? View.VISIBLE:View.GONE);
        if (this.controlsListener != null) {
            this.controlsListener.gridToggled(this.gridEnabled);
        }
        if (!this.gridEnabled) {
            this.selectionEnabled = false;
            if (this.controlsListener != null) {
                this.controlsListener.gridSelectionToggled(false);
            }
        }
    }

    public void startNewFromDialog(String h, String v) {
        int newH;
        int newV;
        try {
            newH = Integer.parseInt(h);
        } catch (Exception e) {
            newH = 3;
        }
        try {
            newV = Integer.parseInt(v);
        } catch (Exception e2) {
            newV = 3;
        }
        int newH2 = Math.max(0, Math.min(newH, 100));
        int newV2 = Math.max(0, Math.min(newV, 100));
        if (newH2 == newV2 && newV2 == 0) {
            newV2 = 1;
            newH2 = 1;
        }
        this.HorizontalMarkers.clear();
        this.VerticalMarkers.clear();
        if (newH2 != 0) {
            float stepH = 1.0f / ((float) (newH2 + 1));
            float[] newHorizontals = new float[newH2];
            for (int i = 1; i - 1 < newH2; i++) {
                newHorizontals[i - 1] = ((float) i) * stepH;
            }
            this.HorizontalMarkers.addRelativePos(newHorizontals);
        }
        if (newV2 != 0) {
            float stepV = 1.0f / ((float) (newV2 + 1));
            float[] newVerticals = new float[newV2];
            for (int i2 = 1; i2 - 1 < newV2; i2++) {
                newVerticals[i2 - 1] = ((float) i2) * stepV;
            }
            this.VerticalMarkers.addRelativePos( newVerticals);
        }
        invalidate();
    }

    public void setControlsListener(OnGridUpdated controlsListener2) {
        this.controlsListener = controlsListener2;
    }

    public GridPanel(Context context) {
        super(context);
        this.HorizontalMarkers.addRelativePos(new float[]{0.25f, 0.5f, 0.75f});
        this.VerticalMarkers.addRelativePos(new float[]{0.25f, 0.5f, 0.75f});
        selectMarker(this.HorizontalMarkers.find(0.5f));
        this.pntMarker.setStrokeWidth(commonFuncs.dpToPx(1));
        this.pntMarker.setColor(-1);
        this.pntMarkerStroke.setStrokeWidth(commonFuncs.dpToPx(2));
        this.pntMarkerStroke.setColor(Color.argb(60, 0, 0, 0));
        this.pntMarkerSelected.setStrokeWidth(commonFuncs.dpToPx(3));
        this.pntMarkerSelected.setColor(Color.parseColor("#C8ff9800"));
        this.pntMarkerSnap.setStrokeWidth(commonFuncs.dpToPx(3));
        this.pntMarkerSnap.setColor(Color.parseColor("#C8f44336"));
    }

        @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        makeSureSomethingIsSelected();
        this.VerticalMarkers.draw(canvas, this.pntMarker, this.pntMarkerStroke, this.pntMarkerSelected, this.pntMarkerSnap, this.selectionEnabled);
        this.HorizontalMarkers.draw(canvas, this.pntMarker, this.pntMarkerStroke, this.pntMarkerSelected, this.pntMarkerSnap, this.selectionEnabled);
    }

    /* access modifiers changed from: package-private */
    public boolean findAndSelectMarker(float x, float y) {
        Marker oldSelected = this.selectedMarker;
        Marker nearestHorizontal = this.HorizontalMarkers.getAtPos(y, this.markerSelectionRad);
        Marker nearestVertical = this.VerticalMarkers.getAtPos(x, this.markerSelectionRad);
        if (nearestHorizontal != null && nearestVertical != null) {
            if (Math.abs(nearestVertical.getAbsPos() - x) >= Math.abs(nearestHorizontal.getAbsPos() - y)) {
                nearestVertical = nearestHorizontal;
            }
            selectMarker(nearestVertical);
        } else if (nearestVertical == null) {
            selectMarker(nearestHorizontal);
        } else {
            selectMarker(nearestVertical);
        }
        return oldSelected != this.selectedMarker;
    }

    /* access modifiers changed from: package-private */
    public MarkerList getContainer(Marker m) {
        return (m == null || m.isHorizontal) ? this.HorizontalMarkers : this.VerticalMarkers;
    }

    /* access modifiers changed from: package-private */
    public void currentlySnappingTo(Marker m, boolean hor) {
        this.snappedToHor = hor ? m : this.snappedToHor;
        if (hor) {
            m = this.snappedToVer;
        }
        this.snappedToVer = m;
        invalidate();
    }

    public boolean onTouchEvent(MotionEvent event) {
        makeSureSomethingIsSelected();
        if (!this.selectionEnabled) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        switch (event.getActionMasked()) {
            case 0:
                this.prevX = x;
                this.prevY = y;
                this.dX = 0.0f;
                this.dY = 0.0f;
                if (findAndSelectMarker(x, y)) {
                    invalidate();
                    break;
                }
                break;
            case 2:
                this.dX = x - this.prevX;
                this.dY = y - this.prevY;
                this.prevX = x;
                this.prevY = y;
                if (this.selectedMarker != null) {
                    getContainer(this.selectedMarker).moveBy(this.selectedMarker, this.selectedMarker.isHorizontal ? this.dY : this.dX);
                    invalidate();
                    break;
                }
                break;
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void selectMarker(Marker select) {
        if (select != null) {
            this.selectedMarker = select;
        }
    }

    public void toggleSelection(boolean toggle) {
        this.selectionEnabled = toggle;
        if (this.controlsListener != null) {
            this.controlsListener.gridSelectionToggled(this.selectionEnabled);
        }
        invalidate();
    }

    public void setSnap(boolean snap) {
        if (this.controlsListener != null) {
            this.controlsListener.gridSnapToggled(snap);
        }
        this.snapEnabled = snap;
    }

    public boolean isSnapEnabled() {
        return this.snapEnabled;
    }

    public void remMarker() {
        if (this.selectedMarker != null) {
            getContainer(this.selectedMarker).remove(this.selectedMarker, true);
            if (this.selectedMarker == null) {
                toggle();
                this.HorizontalMarkers.addRelativePos(new float[]{0.25f, 0.5f, 0.75f});
                this.VerticalMarkers.addRelativePos(new float[]{0.25f, 0.5f, 0.75f});
                toggle(false);
                return;
            }
            invalidate();
        }
    }

    public void addVertical() {
        this.VerticalMarkers.addAfterSelected();
        invalidate();
    }

    public void addHorizontal() {
        this.HorizontalMarkers.addAfterSelected();
        invalidate();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void makeSureSomethingIsSelected() {
        if (this.selectedMarker == null) {
            this.selectedMarker = this.HorizontalMarkers.getLast();
            if (this.selectedMarker == null) {
                this.selectedMarker = this.VerticalMarkers.getLast();
            }
        }
    }

    public float snapPos(float pos, float offset, boolean horizontally, boolean usingOffset) {
        float newPos;
        if (!this.snapEnabled || !this.gridEnabled) {
            return pos;
        }
        MarkerList whereToLook = horizontally ? this.VerticalMarkers : this.HorizontalMarkers;
        Marker snapMarker = whereToLook.getAtPos(pos + offset, this.snapRad);
        currentlySnappingTo(snapMarker, horizontally);
        if (!usingOffset) {
            if (snapMarker != null) {
                return snapMarker.getAbsPos() - offset;
            }
            return pos;
        } else if (snapMarker == null) {
            resetCurrentRemainder(horizontally);
            return pos;
        } else {
            if (whereToLook.getAtPos((horizontally ? this.remainderH : this.remainderV) + snapMarker.getAbsPos(), this.snapRad) != snapMarker) {
                newPos = ((horizontally ? this.remainderH : this.remainderV) + snapMarker.getAbsPos()) - offset;
                resetCurrentRemainder(horizontally);
            } else {
                newPos = snapMarker.getAbsPos() - offset;
                offsetCurrentRemainder(horizontally, pos - newPos);
            }
            return newPos;
        }
    }

    /* access modifiers changed from: package-private */
    public void offsetCurrentRemainder(boolean hor, float off) {
        float f;
        float f2 = 0.0f;
        float f3 = this.remainderH;
        if (hor) {
            f = off;
        } else {
            f = 0.0f;
        }
        this.remainderH = f + f3;
        float f4 = this.remainderV;
        if (!hor) {
            f2 = off;
        }
        this.remainderV = f4 + f2;
    }

    /* access modifiers changed from: package-private */
    public void resetCurrentRemainder(boolean hor) {
        float f = 0.0f;
        this.remainderH = hor ? 0.0f : this.remainderH;
        if (hor) {
            f = this.remainderV;
        }
        this.remainderV = f;
    }

    public void actionUp() {
        this.remainderV = 0.0f;
        this.remainderH = 0.0f;
        this.snappedToVer = null;
        this.snappedToHor = null;
        invalidate();
    }

    public float getCurrentMarkerPosition() {
        if (this.selectedMarker == null) {
            return 0.0f;
        }
        return this.selectedMarker.getRelPos();
    }

    public void setCurrentMarkerPosition(float position) {
        if (this.selectedMarker != null) {
            getContainer(this.selectedMarker).move(this.selectedMarker, position, true);
            invalidate();
        }
    }

    /* access modifiers changed from: package-private */
    public class Marker {
        boolean isHorizontal;
        Marker next = null;
        private float relPos;

        public void setPos(float relPos2) {
            this.relPos = Math.max(Math.min(relPos2, 1.0f), 0.0f);
        }

        public float getRelPos() {
            return this.relPos;
        }

        public float getAbsPos() {
            return ((float) (this.isHorizontal ? GridPanel.this.getHeight() : GridPanel.this.getWidth())) * this.relPos;
        }

        public Marker(float pos, boolean isHorizontal2) {
            this.relPos = Math.max(Math.min(pos, 1.0f), 0.0f);
            this.isHorizontal = isHorizontal2;
        }
    }

    /* access modifiers changed from: package-private */
    public class MarkerList {
        Marker first = null;
        boolean isHorizontalList;

        public MarkerList(boolean isHorizontalList2) {
            this.isHorizontalList = isHorizontalList2;
        }

        public Marker getLast() {
            Marker curr = this.first;
            while (curr != null && curr.next != null) {
                curr = curr.next;
            }
            return curr;
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private void addAfterSelected() {
            Marker tmpSelected = (GridPanel.this.selectedMarker == null || GridPanel.this.getContainer(GridPanel.this.selectedMarker) == this) ? GridPanel.this.selectedMarker : getLast();
            GridPanel.this.selectMarker(addRelativePos((((tmpSelected == null || tmpSelected.next == null) ? 1.0f : tmpSelected.next.getRelPos()) + (tmpSelected != null ? tmpSelected.getRelPos() : 0.0f)) * 0.5f));
        }

        /* access modifiers changed from: package-private */
        public boolean checkIfValidRect() {
            return GridPanel.this.getWidth() > 0 && GridPanel.this.getHeight() > 0;
        }

        /* access modifiers changed from: package-private */
        public void move(Marker m, float newPos, boolean rel) {
            if (checkIfValidRect() && remove(m, false)) {
                if (!rel) {
                    newPos /= (float) (this.isHorizontalList ? GridPanel.this.getHeight() : GridPanel.this.getWidth());
                }
                m.setPos(newPos);
                add(m);
            }
        }

        /* access modifiers changed from: package-private */
        public void moveBy(Marker m, float deltaPos) {
            move(m, m.getAbsPos() + deltaPos, false);
        }

        /* access modifiers changed from: package-private */
        public boolean remove(Marker m, boolean reselectIfNone) {
            Marker marker;
            Marker prev = null;
            if (this.first == m) {
                this.first = this.first.next;
            } else {
                prev = this.first;
                while (prev != null && prev.next != m) {
                    prev = prev.next;
                }
                if (prev == null) {
                    return false;
                }
                prev.next = m.next;
            }
            if (reselectIfNone) {
                if (GridPanel.this.selectedMarker != null) {
                    GridPanel gridPanel = GridPanel.this;
                    if (GridPanel.this.selectedMarker.next != null) {
                        marker = GridPanel.this.selectedMarker.next;
                    } else {
                        marker = prev;
                    }
                    gridPanel.selectedMarker = marker;
                }
                GridPanel.this.makeSureSomethingIsSelected();
            }
            return true;
        }

        public Marker getAtPos(float absPos, float rad) {
            if (this.first == null) {
                return null;
            }
            Marker atPos = this.first;
            float currDist = Math.abs(atPos.getAbsPos() - absPos);
            while (atPos.next != null) {
                float tmpDist = Math.abs(atPos.next.getAbsPos() - absPos);
                if (tmpDist > currDist) {
                    break;
                }
                currDist = tmpDist;
                atPos = atPos.next;
            }
            if (Math.abs(atPos.getAbsPos() - absPos) > rad) {
                atPos = null;
            }
            return atPos;
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private void addRelativePos(float... pos) {
            for (float p : pos) {
                addRelativePos(p);
            }
        }

        private Marker addRelativePos(float pos) {
            Marker tmp = new Marker(pos, this.isHorizontalList);
            add(tmp);
            return tmp;
        }

        public void clear() {
            this.first = null;
        }

        private void add(Marker m) {
            if (this.first == null) {
                this.first = m;
            } else if (m.getRelPos() <= this.first.getRelPos()) {
                m.next = this.first;
                this.first = m;
            } else {
                Marker curr = this.first;
                while (curr.next != null && curr.next.getRelPos() <= m.getRelPos()) {
                    curr = curr.next;
                }
                Marker tmp = curr.next;
                curr.next = m;
                m.next = tmp;
            }
        }

        public void draw(Canvas canvas, Paint pnt, Paint pntStroke, Paint pntSelected, Paint pntSnap, boolean highlightSelected) {
            float f;
            float absPos;
            float f2;
            float dim = this.isHorizontalList ? (float) GridPanel.this.getWidth() : (float) GridPanel.this.getHeight();
            Line2D currLine = new Line2D();
            for (Marker curr = this.first; curr != null; curr = curr.next) {
                float absPos2 = this.isHorizontalList ? 0.0f : curr.getAbsPos();
                if (this.isHorizontalList) {
                    f = curr.getAbsPos();
                } else {
                    f = 0.0f;
                }
                if (this.isHorizontalList) {
                    absPos = dim;
                } else {
                    absPos = curr.getAbsPos();
                }
                if (this.isHorizontalList) {
                    f2 = curr.getAbsPos();
                } else {
                    f2 = dim;
                }
                currLine.set(absPos2, f, absPos, f2);
                canvas.drawLine(currLine.getX1(), currLine.getY1(), currLine.getX2(), currLine.getY2(), pntStroke);
                if (curr == GridPanel.this.snappedToHor || curr == GridPanel.this.snappedToVer) {
                    canvas.drawLine(currLine.getX1(), currLine.getY1(), currLine.getX2(), currLine.getY2(), pntSnap);
                }
                if (curr == GridPanel.this.selectedMarker && highlightSelected) {
                    canvas.drawLine(currLine.getX1(), currLine.getY1(), currLine.getX2(), currLine.getY2(), pntSelected);
                }
                canvas.drawLine(currLine.getX1(), currLine.getY1(), currLine.getX2(), currLine.getY2(), pnt);
                if (GridPanel.this.selectionEnabled) {
                    canvas.drawCircle(currLine.getX1(), currLine.getY1(), GridPanel.this.halfCircleRad * 1.1f, pntStroke);
                    canvas.drawCircle(currLine.getX1(), currLine.getY1(), GridPanel.this.halfCircleRad, pnt);
                    canvas.drawCircle(currLine.getX2(), currLine.getY2(), GridPanel.this.halfCircleRad * 1.1f, pntStroke);
                    canvas.drawCircle(currLine.getX2(), currLine.getY2(), GridPanel.this.halfCircleRad, pnt);
                }
            }
        }

        private Marker find_r(Marker m, float v) {
            if (m == null) {
                return null;
            }
            return m.getRelPos() != v ? find_r(m.next, v) : m;
        }

        public Marker find(float v) {
            return find_r(this.first, v);
        }
    }
}
