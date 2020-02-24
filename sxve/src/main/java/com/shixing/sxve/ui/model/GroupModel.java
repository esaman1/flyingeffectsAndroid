package com.shixing.sxve.ui.model;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.SparseArray;
import android.view.View;

import com.shixing.sxve.ui.util.Size;

public class GroupModel {
    private SparseArray<AssetModel> mAssets = new SparseArray<>();
    private View mThumbTarget;
    private View mTemplateTarget;
    private AssetModel mActiveLayer;
    private Size mSize;

    public void add(AssetModel assetModel) {
        mAssets.put(assetModel.ui.index, assetModel);
    }

    public void draw(Canvas canvas) {
        for (int i = 0; i < mAssets.size(); i++) {
            mAssets.get(i).ui.draw(canvas, mActiveLayer == null ? -1 : mActiveLayer.ui.index);
        }
    }

    public Size getSize() {
        if (mSize != null) {
            return mSize;
        }
        return mAssets.size() > 0 ? mAssets.get(0).size : null;
    }

    public void setSize(Size size) {
        mSize = size;
    }

    public int getGroupIndex() {
        return mAssets.size() > 0 ? mAssets.get(0).ui.group : 0;
    }

    public void notifyRedraw() {
        if (mTemplateTarget != null) {
            mTemplateTarget.invalidate();
        }
        if (mThumbTarget != null) {
            mThumbTarget.invalidate();
        }
    }

    public void setThumbTarget(View thumbTarget) {
        mThumbTarget = thumbTarget;
    }

    public void setTemplateTarget(View templateTarget) {
        mTemplateTarget = templateTarget;
    }

    public void scroll(float distanceX, float distanceY) {
        if (mActiveLayer != null) {
            mActiveLayer.ui.scroll(distanceX, distanceY);
        }

        notifyRedraw();
    }

    public void scale(float sx, float sy, float px, float py) {
        if (mActiveLayer != null) {
            mActiveLayer.ui.scale(sx, sy, px, py);
        }

        notifyRedraw();
    }

    public void rotate(float degrees, float px, float py) {
        if (mActiveLayer != null) {
            mActiveLayer.ui.rotate(degrees, px, py);
        }

        notifyRedraw();
    }

    public void singleTap(PointF pointF) {
        AssetModel asset = getAssetAtLocation(pointF);
        if (asset != null) {
            asset.ui.singleTap(this);
        }
    }

    public void down(PointF pointF) {
        mActiveLayer = getAssetAtLocation(pointF);
        // if (mActiveLayer != null) {
            // notifyRedraw();
        // }
    }

    public void allFingerUp() {
        if (mActiveLayer != null) {
            mActiveLayer = null;
            notifyRedraw();
        }
    }

    public AssetModel getAssetAtLocation(PointF pointF) {
        for (int i = mAssets.size() - 1; i >= 0; i--) {
            if (mAssets.get(i).ui.isPointInside(pointF)) {
                return mAssets.get(i);
            }
        }
        return null;
    }

    public SparseArray<AssetModel> getAssets() {
        return mAssets;
    }
}
