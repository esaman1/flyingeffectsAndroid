package com.flyingeffects.com.manager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.flyingeffects.com.R;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.ui.interfaces.AlbumChooseCallback;
import com.flyingeffects.com.utils.LogUtil;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.Filter;
import com.yanzhenjie.album.api.widget.Widget;

import java.util.ArrayList;
import java.util.List;

public class AlbumManager {
    private static final String TAG = "AlbumManager";

    /**
     * description ：选择视频和图片
     * date: ：2019/5/29 10:41
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public static void chooseAlbum(Context context, int selectNum, int tag, AlbumChooseCallback callback, String materialInfo) {
        int num = BaseConstans.getOpenPhotoAlbumNum();
        if (num > Integer.MAX_VALUE - 1) {
            num = -1;
        }
        num++;
        BaseConstans.setOpenPhotoAlbumNum(num);
        StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "14_choose_picture");
        Album.album(context)
                .multipleChoice()
                .columnCount(3)
                .selectCount(selectNum)
                .camera(false)
                .materialInfo(materialInfo)
                .filterSize(new Filter<Long>() {
                    @Override
                    public boolean filter(Long attributes) {
                        return attributes < 20000;
                    }
                })
                .filterMimeType(new Filter<String>() {
                    @Override
                    public boolean filter(String attributes) {
                        LogUtil.d("filter2222222", "attributes=" + attributes);
//                        return attributes.equals("image/gif")||attributes.equals("image/svg+xml")||attributes.equals("image/x-icon");
                        return filterAlbum(attributes);
                    }
                })
                .afterFilterVisibility(false)
                .cameraVideoQuality(1)
                .cameraVideoLimitDuration(Integer.MAX_VALUE)
                .cameraVideoLimitBytes(Integer.MAX_VALUE)
                .widget(
                        Widget.newDarkBuilder(context)
                                .title(R.string.better_to_choose_all)
                                .statusBarColor(ContextCompat.getColor(context, com.yanzhenjie.album.R.color.albumColorTabBlack))
                                .toolBarColor(ContextCompat.getColor(context, com.yanzhenjie.album.R.color.albumColorTabBlack))
                                .mediaItemCheckSelector(Color.WHITE, Color.parseColor("#FEE131"))
                                .bucketItemCheckSelector(Color.WHITE, Color.parseColor("#FEE131"))
                                .buttonStyle(
                                        Widget.ButtonStyle.newDarkBuilder(context)
                                                .setButtonSelector(Color.WHITE, Color.WHITE)
                                                .build()
                                )
                                .build()
                )
                .onReturnView(
                        (result, isFromCamera) -> {
                            if (BaseConstans.getOpenPhotoAlbumNum() % BaseConstans.getIntervalNumShowAD() == 0) {
                                requestAlbumAd(context, result);
                            }

                        }

//

                )
                .onResult((result, isFromCamera) -> {
                    List<String> paths = new ArrayList<>();
                    for (AlbumFile albumFile : result) {
                        paths.add(albumFile.getPath());
                    }
                    callback.resultFilePath(tag, paths, false, isFromCamera,result);
                    if (BaseConstans.getOpenPhotoAlbumNum() % BaseConstans.getIntervalNumShowAD() == 0) {
                        AdManager.getInstance().releaseBannerManager();
                    }
                })
                .onCancel((result, isFromCamera) -> {
                    callback.resultFilePath(tag, new ArrayList<>(), true, isFromCamera,new ArrayList<>());
                    if (BaseConstans.getOpenPhotoAlbumNum() % BaseConstans.getIntervalNumShowAD() == 0) {
                        AdManager.getInstance().releaseBannerManager();
                    }
                })
                .start();
    }


    /**
     * description ：选择视频和图片,过滤视频
     * date: ：2019/5/29 10:41
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public static void chooseAlbum(Context context, int selectNum, int tag, AlbumChooseCallback callback, String materialInfo, long duration) {
        int num = BaseConstans.getOpenPhotoAlbumNum();
         if (num > Integer.MAX_VALUE - 1) {
            num = -1;
        }
        num++;
        BaseConstans.setOpenPhotoAlbumNum(num);
        StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "14_choose_picture");
        Album.album(context)
                .multipleChoice()
                .columnCount(3)
                .selectCount(selectNum)
                .camera(false)
                .materialInfo(materialInfo)
                .setMineVideoTime(duration)
                .filterSize(new Filter<Long>() {
                    @Override
                    public boolean filter(Long attributes) {
                        return attributes < 20000;
                    }
                })
                .filterMimeType(new Filter<String>() {
                    @Override
                    public boolean filter(String attributes) {
//                        return attributes.equals("image/gif")||attributes.equals("image/svg+xml")||attributes.equals("image/x-icon")||attributes.equals("video/x-ms-wmv");
                        return filterAlbum(attributes);
                    }
                })
                .afterFilterVisibility(false)
                .cameraVideoQuality(1)
                .cameraVideoLimitDuration(Integer.MAX_VALUE)
                .cameraVideoLimitBytes(Integer.MAX_VALUE)
                .widget(
                        Widget.newDarkBuilder(context)
                                .title(R.string.better_to_choose_all)
                                .statusBarColor(ContextCompat.getColor(context, com.yanzhenjie.album.R.color.albumColorTabBlack))
                                .toolBarColor(ContextCompat.getColor(context, com.yanzhenjie.album.R.color.albumColorTabBlack))
                                .mediaItemCheckSelector(Color.WHITE, Color.parseColor("#FEE131"))
                                .bucketItemCheckSelector(Color.WHITE, Color.parseColor("#FEE131"))
                                .buttonStyle(
                                        Widget.ButtonStyle.newDarkBuilder(context)
                                                .setButtonSelector(Color.WHITE, Color.WHITE)
                                                .build()
                                )
                                .build()
                )
                .onReturnView((result, isFromCamera) -> {
                    if (BaseConstans.getOpenPhotoAlbumNum() % BaseConstans.getIntervalNumShowAD() == 0) {
                        requestAlbumAd(context, result);
                    }
                })
                .onResult((result, isFromCamera) -> {
                    List<String> paths = new ArrayList<>();
                    for (AlbumFile albumFile : result) {
                        paths.add(albumFile.getPath());
                    }
                    if (BaseConstans.getOpenPhotoAlbumNum() % BaseConstans.getIntervalNumShowAD() == 0) {
                        AdManager.getInstance().releaseBannerManager();
                    }
                    callback.resultFilePath(tag, paths, false,isFromCamera, result);
                })
                .onCancel((result, isFromCamera) -> {
                    callback.resultFilePath(tag, new ArrayList<>(), true,isFromCamera, new ArrayList<>());
                    if (BaseConstans.getOpenPhotoAlbumNum() % BaseConstans.getIntervalNumShowAD() == 0) {
                        AdManager.getInstance().releaseBannerManager();
                    }
                })
                .start();
    }


    /**
     * description ：选择视频和图片,过滤视频 拍摄专用，为了避免和其他页面冲突
     * date: ：2019/5/29 10:41
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public static void chooseAlbum(Context context, int selectNum, int tag, AlbumChooseCallback callback, String materialInfo, long duration, String title, String musicFolder) {
        int num = BaseConstans.getOpenPhotoAlbumNum();
         if (num > Integer.MAX_VALUE - 1) {
            num = -1;
        }
        num++;
        BaseConstans.setOpenPhotoAlbumNum(num);
        StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "14_choose_picture");
        Album.album(context)
                .multipleChoice()
                .columnCount(3)
                .selectCount(selectNum)
                .camera(false)
                .materialInfo(materialInfo)
                .setMineVideoTime(duration)
                .setModelTitle(title)
                .setMusicPath(musicFolder)
                .filterSize(new Filter<Long>() {
                    @Override
                    public boolean filter(Long attributes) {
                        return attributes < 20000;
                    }
                })
                .filterMimeType(new Filter<String>() {
                    @Override
                    public boolean filter(String attributes) {
//                        return attributes.equals("image/gif")||attributes.equals("image/svg+xml")||attributes.equals("image/x-icon")||attributes.equals("video/x-ms-wmv");
                        return filterAlbum(attributes);
                    }
                })
                .afterFilterVisibility(false)

                .cameraVideoQuality(1)
                .cameraVideoLimitDuration(Integer.MAX_VALUE)
                .cameraVideoLimitBytes(Integer.MAX_VALUE)
                .widget(
                        Widget.newDarkBuilder(context)
                                .title(R.string.better_to_choose_all)
                                .statusBarColor(ContextCompat.getColor(context, com.yanzhenjie.album.R.color.albumColorTabBlack))
                                .toolBarColor(ContextCompat.getColor(context, com.yanzhenjie.album.R.color.albumColorTabBlack))
                                .mediaItemCheckSelector(Color.WHITE, Color.parseColor("#FEE131"))
                                .bucketItemCheckSelector(Color.WHITE, Color.parseColor("#FEE131"))
                                .buttonStyle(
                                        Widget.ButtonStyle.newDarkBuilder(context)
                                                .setButtonSelector(Color.WHITE, Color.WHITE)
                                                .build()
                                )
                                .build()
                )
                .onReturnView((result, isFromCamera) -> {
                    if (BaseConstans.getOpenPhotoAlbumNum() % BaseConstans.getIntervalNumShowAD() == 0) {
                        requestAlbumAd(context, result);
                    }
                })
                .onResult((result, isFromCamera) -> {
                    List<String> paths = new ArrayList<>();
                    for (AlbumFile albumFile : result) {
                        paths.add(albumFile.getPath());
                    }
                    if (BaseConstans.getOpenPhotoAlbumNum() % BaseConstans.getIntervalNumShowAD() == 0) {
                        AdManager.getInstance().releaseBannerManager();
                    }
                    callback.resultFilePath(tag, paths, false,isFromCamera, result);
                })
                .onCancel((result, isFromCamera) -> {
                    callback.resultFilePath(tag, new ArrayList<>(), true, isFromCamera,new ArrayList<>());
                    if (BaseConstans.getOpenPhotoAlbumNum() % BaseConstans.getIntervalNumShowAD() == 0) {
                        AdManager.getInstance().releaseBannerManager();
                    }
                })
                .start();
    }


    /**
     * description ：只选择图片
     * date: ：2019/5/29 10:41
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public static void chooseImageAlbum(Context context, int selectNum, int tag, AlbumChooseCallback callback, String materialInfo) {
        int num = BaseConstans.getOpenPhotoAlbumNum();
         if (num > Integer.MAX_VALUE - 1) {
            num = -1;
        }
        num++;
        BaseConstans.setOpenPhotoAlbumNum(num);
        StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "14_choose_picture");
        Album.image(context) // Image selection.
                .multipleChoice()
                .camera(false)
                .materialInfo(materialInfo)
                .columnCount(3)
                .filterSize(new Filter<Long>() {
                    @Override
                    public boolean filter(Long attributes) {
                        return attributes < 20000;
                    }
                })
                .filterMimeType(new Filter<String>() {
                    @Override
                    public boolean filter(String attributes) {
                        return "image/gif".equals(attributes) || "image/svg+xml".equals(attributes) || "image/x-icon".equals(attributes);
                    }
                })
                .afterFilterVisibility(false)
                .selectCount(selectNum)
                .widget(
                        Widget.newDarkBuilder(context)
                                .title(R.string.better_to_choose_all)
                                .statusBarColor(ContextCompat.getColor(context, com.yanzhenjie.album.R.color.albumColorTabBlack))
                                .toolBarColor(ContextCompat.getColor(context, com.yanzhenjie.album.R.color.albumColorTabBlack))
                                .mediaItemCheckSelector(Color.WHITE, Color.parseColor("#FEE131"))
                                .bucketItemCheckSelector(Color.WHITE, Color.parseColor("#FEE131"))
                                .buttonStyle(
                                        Widget.ButtonStyle.newDarkBuilder(context)
                                                .setButtonSelector(Color.WHITE, Color.WHITE)
                                                .build()
                                )
                                .build())
                .onReturnView((result, isFromCamera) -> {
                    if (BaseConstans.getOpenPhotoAlbumNum() % BaseConstans.getIntervalNumShowAD() == 0) {
                        requestAlbumAd(context, result);
                    }
                })
                .onResult((result, isFromCamera) -> {
                    List<String> paths = new ArrayList<>();
                    for (AlbumFile albumFile : result) {
                        paths.add(albumFile.getPath());
                    }
                    if (BaseConstans.getOpenPhotoAlbumNum() % BaseConstans.getIntervalNumShowAD() == 0) {
                        AdManager.getInstance().releaseBannerManager();
                    }
                    callback.resultFilePath(tag, paths, false,isFromCamera, result);
                })
                .onCancel((result, isFromCamera) -> {
                    callback.resultFilePath(tag, new ArrayList<>(), true,isFromCamera, new ArrayList<>());
                    if (BaseConstans.getOpenPhotoAlbumNum() % BaseConstans.getIntervalNumShowAD() == 0) {
                        AdManager.getInstance().releaseBannerManager();
                    }
                })
                .start();
    }


    /**
     * description ：选择视频
     * date: ：2019/5/29 10:41
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public static void chooseVideo(Activity act, int selectNum, int tag, AlbumChooseCallback callback, String materialInfo) {
        int num = BaseConstans.getOpenPhotoAlbumNum();
         if (num > Integer.MAX_VALUE - 1) {
            num = -1;
        }
        num++;
        BaseConstans.setOpenPhotoAlbumNum(num);
        StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "14_choose_picture");
        Album.video(act) // Video selection.
                .multipleChoice()
                .camera(false)
                .columnCount(3)
                .materialInfo(materialInfo)
                .selectCount(selectNum)
                .filterSize(new Filter<Long>() {
                    @Override
                    public boolean filter(Long attributes) {
                        return attributes < 20000;
                    }
                })
                .filterMimeType(new Filter<String>() {
                    @Override
                    public boolean filter(String attributes) {
                        return filterAlbum(attributes);
                    }
                })
                .afterFilterVisibility(false)
                .widget(
                        Widget.newDarkBuilder(act)
                                .title(R.string.better_to_choose_all)
                                .statusBarColor(ContextCompat.getColor(act, com.yanzhenjie.album.R.color.albumColorTabBlack))
                                .toolBarColor(ContextCompat.getColor(act, com.yanzhenjie.album.R.color.albumColorTabBlack))
                                .mediaItemCheckSelector(Color.WHITE, Color.parseColor("#FEE131"))
                                .bucketItemCheckSelector(Color.WHITE, Color.parseColor("#FEE131"))
                                .buttonStyle(
                                        Widget.ButtonStyle.newDarkBuilder(act)
                                                .setButtonSelector(Color.WHITE, Color.WHITE)
                                                .build()
                                )
                                .build())
                .onReturnView((result, isFromCamera) -> {
                    if (BaseConstans.getOpenPhotoAlbumNum() % BaseConstans.getIntervalNumShowAD() == 0) {
                        requestAlbumAd(act, result);
                    }
                })
                .onResult((result, isFromCamera) -> {
                    List<String> paths = new ArrayList<>();
                    for (AlbumFile albumFile : result) {
                        paths.add(albumFile.getPath());
                    }
                    callback.resultFilePath(tag, paths, false, isFromCamera,result);
                    if (BaseConstans.getOpenPhotoAlbumNum() % BaseConstans.getIntervalNumShowAD() == 0) {
                        AdManager.getInstance().releaseBannerManager();
                    }
                })
                .onCancel((result, isFromCamera) -> {
                    callback.resultFilePath(tag, new ArrayList<>(), true,isFromCamera, new ArrayList<>());
                    if (BaseConstans.getOpenPhotoAlbumNum() % BaseConstans.getIntervalNumShowAD() == 0) {
                        AdManager.getInstance().releaseBannerManager();
                    }
                })
                .start();
    }


    /**
     * description ： chooseType  0 均可以选择  1：选择图片 2：选择视频
     * date: ：2019/6/14 10:25
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public static void chooseWhichAlbum(Context context, int selectNum, int tag, AlbumChooseCallback callback, int chooseType, String materialInfo) {
        switch (chooseType) {
            case 0:
                AlbumManager.chooseAlbum(context, selectNum, tag, callback, materialInfo);
                break;
            case 1:
                AlbumManager.chooseImageAlbum(context, selectNum, tag, callback, materialInfo);
                break;
            case 2:
                AlbumManager.chooseAlbum((Activity) context, selectNum, tag, callback, materialInfo);
                break;
            case 3:
                AlbumManager.chooseVideo((Activity) context, selectNum, tag, callback, materialInfo);
                break;
            default:
                break;
        }
    }


    private static boolean filterAlbum(String attributes) {

        LogUtil.d("xxxx", "attributes=" + attributes);
        String[] strList = {"image/gif", "image/svg+xml", "image/x-icon", "video/x-ms-wmv", "avi", "wmv", "WMV", "mov", "MOV", "mpg", "MPG", "3gp", "3GP", "lansongBox", "avi", "AVI", "gif", "mpeg", "svg+xml", "quicktime"};
        for (String str : strList) {
            if (!TextUtils.isEmpty(attributes)&&attributes.contains(str)) {
                LogUtil.d("xxxx", "过滤的值为=" + attributes);
                return true;
            }

        }
        return false;
    }

    private static void requestAlbumAd(Context activity, LinearLayout llAdContainer) {
        if (BaseConstans.getHasAdvertising() == 1 && !BaseConstans.getIsNewUser()) {
            Log.d(TAG, "showAd :" + llAdContainer);
            AdManager.getInstance().showBannerAd((Activity) activity, AdConfigs.AD_ALBUM, llAdContainer);
        }
    }

}
