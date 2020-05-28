package com.flyingeffects.com.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

//com.flyingeffects.com.view.VideoFrameRecycler
/**
 * @Author: savion
 * @Date: 2019/3/4 10:06
 * @Des:
 **/
public class VideoFrameRecycler extends RecyclerView {
    private boolean scrollByUser = false;
    private OnScrollListener scrollListener;

    public interface OnScrollListener {
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy, boolean byuser);
    }

    public VideoFrameRecycler(Context context) {
        super(context);
    }

    public VideoFrameRecycler(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoFrameRecycler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScrollListener(OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (scrollListener != null) {
                    scrollListener.onScrolled(recyclerView, dx, dy, scrollByUser);
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                scrollByUser = true;
                break;
            case MotionEvent.ACTION_UP:
                scrollByUser = false;
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onSizeChanged(final int w, int h, final int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        if (w != oldW) {
        }
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        super.setAdapter(adapter);
    }
}
