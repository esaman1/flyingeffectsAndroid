package com.shixing.sxve.ui.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

public class GroupThumbDecoration extends RecyclerView.ItemDecoration {
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildLayoutPosition(view);
        if (position != 0) {
            int insetStart = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    10,
                    parent.getContext().getResources().getDisplayMetrics());
            outRect.set(insetStart, 0, 0, 0);
        }
    }
}
