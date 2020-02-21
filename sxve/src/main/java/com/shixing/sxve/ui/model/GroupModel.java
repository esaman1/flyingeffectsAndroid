package com.shixing.sxve.ui.model;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.View;

import com.shixing.sxve.ui.util.Size;

//GroupModel.add
public class GroupModel {
    private SparseArray<AssetModel> mAssets = new SparseArray<>();
    private View mThumbTarget;  //底部的view
    private View mTemplateTarget;
    private AssetModel mActiveLayer;
    private int lastSelectedItem;

    public void add(AssetModel assetModel) {
        mAssets.put(assetModel.ui.index, assetModel);
    }

    public void draw(Canvas canvas) {
        if (canvas != null) {
            for (int i = 0; i < mAssets.size(); i++) {
                mAssets.get(i).ui.draw(canvas, mActiveLayer == null ? -1 : mActiveLayer.ui.index);
            }
        }
    }


    public void isShow(boolean isShow) {
        if (mAssets != null && mAssets.size() > 0) {
            AssetModel mActiveLayer = mAssets.get(0);
            if (mActiveLayer != null) {
                mActiveLayer.ui.isShow(isShow);
            }
        }
    }


    public Size getSize() {
        return mAssets.size() > 0 ? mAssets.get(0).size : null;
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
            if (mActiveLayer.type == 2) { //如果是滑动的文字层，那么转交给图片成
                for (int i = 0; i < mAssets.size(); i++) {
                    if (mAssets.get(i) != null && mAssets.get(i).type == 1) {
                        mActiveLayer = mAssets.get(i);
                        mActiveLayer.ui.scroll(distanceX, distanceY);
                        break;
                    }
                }
            } else {
                mActiveLayer.ui.scroll(distanceX, distanceY);
            }
        }

        notifyRedraw();
    }

    public void scale(float sx, float sy, float px, float py) {
        if (mActiveLayer != null) {
            if (mActiveLayer.type == 2) { //如果是滑动的文字层，那么转交给图片成
                for (int i = 0; i < mAssets.size(); i++) {
                    if (mAssets.get(i) != null && mAssets.get(i).type == 1) {
                        mActiveLayer = mAssets.get(i);
                        mActiveLayer.ui.scale(sx, sy, px, py);
                        break;
                    }
                }
            } else {
                mActiveLayer.ui.scale(sx, sy, px, py);
            }
        }
        notifyRedraw();
    }

    public void rotate(float degrees, float px, float py) {
        if (mActiveLayer != null) {
            if (mActiveLayer.type == 2) { //如果是滑动的文字层，那么转交给图片成
                for (int i = 0; i < mAssets.size(); i++) {
                    if (mAssets.get(i) != null && mAssets.get(i).type == 1) {
                        mActiveLayer = mAssets.get(i);
                        mActiveLayer.ui.rotate(degrees, px, py);
                        break;
                    }
                }
            } else {
                mActiveLayer.ui.rotate(degrees, px, py);
            }
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
        mActiveLayer = getAssetAtLocation(pointF);  //获得当前点击的那个MediaUiModel
        if (mActiveLayer != null) {
            notifyRedraw();
        }
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
