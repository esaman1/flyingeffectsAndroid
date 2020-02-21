package com.mobile.flyingeffects.commonlyModel;

import android.graphics.Bitmap;

import com.lansosdk.LanSongFilter.LanSongFilter;
import com.lansosdk.box.BitmapGetFilters;
import com.mobile.kadian.base.BaseApplication;
import com.mobile.kadian.manager.fliterCollectionManage;

import java.util.ArrayList;

public class FilterModel {


    private  static FilterModel  thisModel;

    public static FilterModel getInstance(){

        if(thisModel==null){
            thisModel=new FilterModel();
        }
        return  thisModel;

    }


    private filterBitmapCallback callback;


    private  ArrayList<Bitmap>listForFilter=new ArrayList<>();
    public void getFilterBitmap(Bitmap bitmap,int position,filterBitmapCallback callback){
        this.callback=callback;
        ArrayList<LanSongFilter> filters_lansong = fliterCollectionManage.getInstance().getFiltersForLansong();
        ArrayList<LanSongFilter>list_choose=new ArrayList<>();
        list_choose.add(filters_lansong.get(position));
        filters_lansong.remove(0);
        BitmapGetFilters getFilter = new BitmapGetFilters(BaseApplication.getInstance(), bitmap, list_choose);
        getFilter.setDrawpadOutFrameListener((v, obj) -> {
            listForFilter.add((Bitmap)obj);
        });
        getFilter.setRorate(0);
        getFilter.setGetFiltersCompletedListener(bitmapGetFilters -> {
            this.callback.getFilterBitmap(listForFilter);
        });
        getFilter.start();// 开始线程.
    }


    public interface  filterBitmapCallback{
       void  getFilterBitmap(ArrayList<Bitmap> list);
    }



}
