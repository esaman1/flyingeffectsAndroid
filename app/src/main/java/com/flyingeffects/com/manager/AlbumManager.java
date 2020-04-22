package com.flyingeffects.com.manager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.flyingeffects.com.R;
import com.flyingeffects.com.ui.interfaces.AlbumChooseCallback;
import com.flyingeffects.com.utils.LogUtil;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.Filter;
import com.yanzhenjie.album.api.widget.Widget;

import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

public class AlbumManager {


    /**
     * description ：选择视频和图片
     * date: ：2019/5/29 10:41
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public static void chooseAlbum(Context context, int selectNum, int tag, AlbumChooseCallback callback, String material_info) {
        Album.album(context)
                .multipleChoice()
                .columnCount(3)
                .selectCount(selectNum)
                .camera(false)
                .material_info(material_info)
                .cameraVideoQuality(1)
                .cameraVideoLimitDuration(Integer.MAX_VALUE)
                .cameraVideoLimitBytes(Integer.MAX_VALUE)
                .widget(
                        Widget.newLightBuilder(context)
                                .title(R.string.better_to_choose_all)
                                .statusBarColor(Color.WHITE)
                                .toolBarColor(Color.WHITE)
                                .mediaItemCheckSelector(Color.WHITE, Color.parseColor("#FEE131"))
                                .bucketItemCheckSelector(Color.WHITE, Color.parseColor("#FEE131"))
                                .buttonStyle(
                                        Widget.ButtonStyle.newLightBuilder(context)
                                                .setButtonSelector(Color.WHITE, Color.WHITE)
                                                .build()
                                )
                                .build()
                )
                .onResult(result -> {
                    List<String> paths = new ArrayList<>();
                    for (AlbumFile albumFile : result
                    ) {
                        paths.add(albumFile.getPath());
                    }
                    callback.resultFilePath(tag, paths, false, result);
                })
                .onCancel(result ->
                        callback.resultFilePath(tag, new ArrayList<>(), true, new ArrayList<>()))
                .start();
    }




    /**
     * description ：选择视频和图片,过滤视频
     * date: ：2019/5/29 10:41
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public static void chooseAlbum(Context context, int selectNum, int tag, AlbumChooseCallback callback, String material_info,long duration) {
        Album.album(context)
                .multipleChoice()
                .columnCount(3)
                .selectCount(selectNum)
                .camera(false)
                .material_info(material_info)
                .setMineVideoTime(duration)
                .filterSize(new Filter<Long>() {
                    @Override
                    public boolean filter(Long attributes) {
                        return attributes<20000;
                    }
                })
                .filterMimeType(new Filter<String>() {
                    @Override
                    public boolean filter(String attributes) {
                        return attributes.equals("image/gif")||attributes.equals("image/svg+xml")||attributes.equals("image/x-icon");
                    }
                })

                .cameraVideoQuality(1)
                .cameraVideoLimitDuration(Integer.MAX_VALUE)
                .cameraVideoLimitBytes(Integer.MAX_VALUE)
                .widget(
                        Widget.newLightBuilder(context)
                                .title(R.string.better_to_choose_all)
                                .statusBarColor(Color.WHITE)
                                .toolBarColor(Color.WHITE)
                                .mediaItemCheckSelector(Color.WHITE, Color.parseColor("#FEE131"))
                                .bucketItemCheckSelector(Color.WHITE, Color.parseColor("#FEE131"))
                                .buttonStyle(
                                        Widget.ButtonStyle.newLightBuilder(context)
                                                .setButtonSelector(Color.WHITE, Color.WHITE)
                                                .build()
                                )
                                .build()
                )
                .onResult(result -> {
                    List<String> paths = new ArrayList<>();
                    for (AlbumFile albumFile : result
                    ) {
                        paths.add(albumFile.getPath());
                    }
                    callback.resultFilePath(tag, paths, false, result);
                })
                .onCancel(result ->
                        callback.resultFilePath(tag, new ArrayList<>(), true, new ArrayList<>()))
                .start();
    }
    /**
     * description ：只选择图片
     * date: ：2019/5/29 10:41
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public static void chooseImageAlbum(Context context, int selectNum, int tag, AlbumChooseCallback callback,String material_info) {

        Album.image(context) // Image selection.
                .multipleChoice()
                .camera(false)
                .material_info(material_info)
                .columnCount(3)
                .filterSize(new Filter<Long>() {
                    @Override
                    public boolean filter(Long attributes) {
                        return attributes<20000;
                    }
                })
                .filterMimeType(new Filter<String>() {
                    @Override
                    public boolean filter(String attributes) {
                        return attributes.equals("image/gif")||attributes.equals("image/svg+xml")||attributes.equals("image/x-icon");
                    }
                })
                .afterFilterVisibility(false)
                .selectCount(selectNum)
                .widget(
                        Widget.newLightBuilder(context)
                                .title(R.string.better_to_choose_all)
                                .statusBarColor(Color.WHITE)
                                .toolBarColor(Color.WHITE)
                                .mediaItemCheckSelector(Color.WHITE, Color.parseColor("#FEE131"))
                                .bucketItemCheckSelector(Color.WHITE, Color.parseColor("#FEE131"))
                                .buttonStyle(
                                        Widget.ButtonStyle.newLightBuilder(context)
                                                .setButtonSelector(Color.WHITE, Color.WHITE)
                                                .build()
                                )
                                .build())
                .onResult(result -> {
                    List<String> paths = new ArrayList<>();
                    for (AlbumFile albumFile : result
                    ) {
                        paths.add(albumFile.getPath());
                    }
                    callback.resultFilePath(tag, paths, false, result);
                })
                .onCancel(result -> callback.resultFilePath(tag, new ArrayList<>(), true, new ArrayList<>()))
                .start();

    }


    /**
     * description ：选择视频
     * date: ：2019/5/29 10:41
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public static void chooseVideo(Activity act, int selectNum, int tag, AlbumChooseCallback callback,String material_info) {


        Album.video(act) // Video selection.
                .multipleChoice()
                .camera(false)
                .columnCount(3)
                .material_info(material_info)
                .selectCount(selectNum)
//                .checkedList(mAlbumFiles)
                .widget(
                        Widget.newLightBuilder(act)
                                .title(R.string.better_to_choose_all)
                                .statusBarColor(Color.WHITE)
                                .toolBarColor(Color.WHITE)
                                .mediaItemCheckSelector(Color.WHITE, Color.parseColor("#FEE131"))
                                .bucketItemCheckSelector(Color.WHITE, Color.parseColor("#FEE131"))
                                .buttonStyle(
                                        Widget.ButtonStyle.newLightBuilder(act)
                                                .setButtonSelector(Color.WHITE, Color.WHITE)
                                                .build()
                                )
                                .build())
                .onResult(result -> {
                    List<String> paths = new ArrayList<>();
                    for (AlbumFile albumFile : result
                    ) {
                        paths.add(albumFile.getPath());
                    }
                    callback.resultFilePath(tag, paths, false, result);
                })
                .onCancel(result -> callback.resultFilePath(tag, new ArrayList<>(), true, new ArrayList<>()))
                .start();
    }







    /**
     * description ： ChooseType  0 均可以选择  1：选择图片 2：选择视频
     * date: ：2019/6/14 10:25
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public static void chooseWhichAlbum(Context context, int selectNum, int tag, AlbumChooseCallback callback,int ChooseType,String material_info){



        switch (ChooseType) {
            case 0:
                AlbumManager.chooseAlbum(context, selectNum, tag, callback,material_info);
                break;
            case 1:
                AlbumManager.chooseImageAlbum(context, selectNum, tag, callback,material_info);
                break;
            case 2:
                AlbumManager.chooseAlbum((Activity) context, selectNum, tag, callback,material_info);
                break;
            case 3:
                AlbumManager.chooseVideo((Activity) context, selectNum, tag, callback,material_info);
                break;
        }
    }
}
