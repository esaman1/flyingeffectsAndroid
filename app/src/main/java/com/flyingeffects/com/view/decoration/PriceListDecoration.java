package com.flyingeffects.com.view.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flyingeffects.com.base.BaseApplication;
import com.yanzhenjie.album.util.PxUtils;

public class PriceListDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "MineListDecoration";

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        if (position == 0){
            outRect.left = PxUtils.dp2px(BaseApplication.getInstance(), 16);
        }else if (position == (state.getItemCount() - 1)) {
            outRect.left = PxUtils.dp2px(BaseApplication.getInstance(), 12);
            outRect.right = PxUtils.dp2px(BaseApplication.getInstance(), 16);
        }else {
            outRect.left = PxUtils.dp2px(BaseApplication.getInstance(), 12);
        }
    }

}
