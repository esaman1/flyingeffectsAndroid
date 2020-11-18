package com.yanzhenjie;

import android.util.Log;

import java.util.ArrayList;


/**
 * description ：记录图片选中文案，每次添加/删除都刷新item
 * creation date: 2020/11/3
 * user : zhangtongju
 */
public class PhotoChooseIndex {


    private int lastIntPosition = -1;

    private ArrayList<Integer> listForKeepPhoto = new ArrayList<>();

    private static PhotoChooseIndex thisModel;

    public static PhotoChooseIndex getInstance() {
        if (thisModel == null) {
            thisModel = new PhotoChooseIndex();
        }
        return thisModel;
    }


    /**
     * description ：存入相片位置
     * index 当前保存的次数   photoIndex 图片的位置
     * creation date: 2020/11/3
     * user : zhangtongju
     */
    public void PutPhotoIndex(int photoIndex) {
//        Log.d("OOM5","存入的位置为"+photoIndex);
        for (int j = 0; j < listForKeepPhoto.size(); j++) {
            int nowIndex = listForKeepPhoto.get(j);
            if (photoIndex == nowIndex) {
                lastIntPosition = nowIndex;
                listForKeepPhoto.remove(j);
                return;
            }
        }
        listForKeepPhoto.add(photoIndex);
    }


    /**
     * description ：返回保存的图片item
     * creation date: 2020/11/3
     * user : zhangtongju
     */
    public ArrayList<Integer> GetPhotoIndexList() {
        return listForKeepPhoto;
    }


    public int GetLastIndex() {
        return lastIntPosition;
    }


    public int SetLastIndex() {
        return -1;
    }


    /**
     * description ：清除全部数据
     * creation date: 2020/11/3
     * user : zhangtongju
     */
    public void ClearAllData() {


        listForKeepPhoto.clear();
        lastIntPosition = -1;
    }


}
