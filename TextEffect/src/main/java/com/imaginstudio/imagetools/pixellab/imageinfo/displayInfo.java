package com.imaginstudio.imagetools.pixellab.imageinfo;

import android.graphics.PointF;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.imaginstudio.imagetools.pixellab.GridPanel;
import com.imaginstudio.imagetools.pixellab.ZoomWidget;
import com.imaginstudio.imagetools.pixellab.textContainer;

public class displayInfo {
    private GridPanel gridPanel;
    private ScrollView layerHolderScroll;
    ViewGroup previewPanel;
    private textContainer textContain;
    ZoomWidget zoom_widget;

    public displayInfo(ViewGroup previewPanelInstance, ZoomWidget zoom_widget2) {
        this.previewPanel = previewPanelInstance;
        this.zoom_widget = zoom_widget2;
    }


    public boolean safetyCheck() {
        return this.previewPanel != null;
    }

    public int getContainerHeight() {
        return Math.max(1, this.previewPanel.getHeight());
    }

    public int getContainerWidth() {
        return Math.max(1, this.previewPanel.getWidth());
    }

    public float getZoomFactor() {
        if (this.zoom_widget != null) {
            return Math.max(this.zoom_widget.getZoomFactor(), 1.0f);
        }
        return 1.0f;
    }

    public PointF getViewOrigin() {
        return this.zoom_widget != null ? this.zoom_widget.getViewOrigin() : new PointF(0.0f, 0.0f);
    }

    public ScrollView getLayerHolderScroll() {
        return this.layerHolderScroll;
    }

    public void setLayerHolderScroll(ScrollView view) {
        this.layerHolderScroll = view;
    }

    public void setTextContain(textContainer container) {
        this.textContain = container;
    }

    public textContainer getTextContain() {
        return this.textContain;
    }

    public void setGridPanel(GridPanel panel) {
        this.gridPanel = panel;
    }


    public float snapPosY(float y, float offset, boolean usingOffset) {
        return this.gridPanel != null ? this.gridPanel.snapPos(y, offset, false, usingOffset) : y;
    }

    public float snapPosY(float y, boolean usingOffset) {
        return snapPosY(y, 0.0f, usingOffset);
    }

    public float snapPosX(float x, float offset, boolean usingOffset) {
        return this.gridPanel != null ? this.gridPanel.snapPos(x, offset, true, usingOffset) : x;
    }

    public float snapPosX(float x, boolean usingOffset) {
        return snapPosX(x, 0.0f, usingOffset);
    }

    public void motionActionUp() {
        if (this.gridPanel != null) {
            this.gridPanel.actionUp();
        }
    }

    public boolean snapEnabled() {
        if (this.gridPanel == null) {
            return false;
        }
        return this.gridPanel.isSnapEnabled();
    }
}
