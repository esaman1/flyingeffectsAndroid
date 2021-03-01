package com.flyingeffects.com.view.interfaces;


import com.flyingeffects.com.view.RangeSeekBarForMusicView;
import com.flyingeffects.com.view.RangeSeekBarView;

public interface OnRangeSeekBarListener {
    void onCreate(RangeSeekBarView rangeSeekBarView, int index, float value);

    void onSeek(RangeSeekBarView rangeSeekBarView, int index, float value);

    void onSeekStart(RangeSeekBarView rangeSeekBarView, int index, float value);

    void onSeekStop(RangeSeekBarView rangeSeekBarView, int index, float value);




    void onCreate(RangeSeekBarForMusicView rangeSeekBarView, int index, float value);

    void onSeek(RangeSeekBarForMusicView rangeSeekBarView, int index, float value);

    void onSeekStart(RangeSeekBarForMusicView rangeSeekBarView, int index, float value);

    void onSeekStop(RangeSeekBarForMusicView rangeSeekBarView, int index, float value);

}
