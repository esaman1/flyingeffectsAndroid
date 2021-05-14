/*
 * Copyright 2018 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.album.app.album;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;

import com.google.android.material.tabs.TabLayout;
import com.yanzhenjie.PhotoChooseIndex;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.AlbumFolder;
import com.yanzhenjie.album.Filter;
import com.yanzhenjie.album.R;
import com.yanzhenjie.album.api.widget.Widget;
import com.yanzhenjie.album.app.Contract;
import com.yanzhenjie.album.app.album.data.MediaReadTask;
import com.yanzhenjie.album.app.album.data.MediaReader;
import com.yanzhenjie.album.app.album.data.PathConversion;
import com.yanzhenjie.album.app.album.data.PathConvertTask;
import com.yanzhenjie.album.app.album.data.ThumbnailBuildTask;
import com.yanzhenjie.album.app.camera.CaptureActivity;
import com.yanzhenjie.album.impl.OnItemClickListener;
import com.yanzhenjie.album.mvp.BaseActivity;
import com.yanzhenjie.album.util.AlbumUtils;
import com.yanzhenjie.album.widget.LoadingDialog;
import com.yanzhenjie.mediascanner.MediaScanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <p>Responsible for controlling the album data and the overall logic.</p>
 * Created by Yan Zhenjie on 2016/10/17.
 */
public class AlbumActivity extends BaseActivity implements
        Contract.AlbumPresenter,
        MediaReadTask.Callback,
        GalleryActivity.Callback,
        PathConvertTask.Callback,
        ThumbnailBuildTask.Callback {
    private static final String TAG = "AlbumActivity";

    private static final int CODE_ACTIVITY_NULL = 1;
    private static final int CODE_PERMISSION_STORAGE = 1;
    private static final int CODE_TO_CAPTURE = 2;


    public static Filter<Long> sSizeFilter;
    public static Filter<String> sMimeFilter;
    public static Filter<Long> sDurationFilter;

    public static Action<ArrayList<AlbumFile>> sResult;
    public static Action<String> sCancel;
    public static Action<LinearLayout> sActionView;

    private List<AlbumFolder> mAlbumFolders;
    private int mCurrentFolder;

    private Widget mWidget;
    private int mFunction;
    private String material_info;
    private int mChoiceMode;
    private int mColumnCount;
    private long mMineVideoTime;
    private boolean mHasCamera;
    private int mLimitCount;

    private int mQuality;
    private long mLimitDuration;
    private long mLimitBytes;
    private long videoTimeFlite;

    private boolean mFilterVisibility;

    private ArrayList<AlbumFile> mCheckedList;
    private MediaScanner mMediaScanner;

    private Contract.AlbumView mView;
    private FolderDialog mFolderDialog;
    private PopupMenu mCameraPopupMenu;
    private LoadingDialog mLoadingDialog;

    private MediaReadTask mMediaReadTask;
    private String mTitle;
    private String mMusicPath;
    private boolean showCapture = false;
    private String[] mTabStr = new String[]{"全部", "图片", "视频"};
    private String[] mTabStr2 = new String[]{"图片"};
    private String[] mTabStr3 = new String[]{"视频"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeArgument();
        setContentView(createView());
        mView = new AlbumView(this, this, material_info);
        mView.setupViews(mWidget, mColumnCount, mHasCamera, mChoiceMode);
        mView.setTitle("");//相册UI更改，title不需要了
        mView.setCompleteDisplay(false);
        mView.setLoadingDisplay(true);
        mView.setShowCapture(showCapture);

        switch (mFunction) {
            case Album.FUNCTION_CHOICE_IMAGE:
                mView.setTab(mTabStr2);
                break;
            case Album.FUNCTION_CHOICE_VIDEO:
                mView.setTab(mTabStr3);
                break;
            case Album.FUNCTION_CHOICE_ALBUM:
                mView.setTab(mTabStr);
                break;
            default:
                break;
        }

//        mView.setTab(mTabStr);
        requestPermission(PERMISSION_STORAGE, CODE_PERMISSION_STORAGE);
    }

    private void initializeArgument() {
        Bundle argument = getIntent().getExtras();
        assert argument != null;
        mWidget = argument.getParcelable(Album.KEY_INPUT_WIDGET);
        mFunction = argument.getInt(Album.KEY_INPUT_FUNCTION);
        mChoiceMode = argument.getInt(Album.KEY_INPUT_CHOICE_MODE);
        mColumnCount = argument.getInt(Album.KEY_INPUT_COLUMN_COUNT);
        mMineVideoTime = argument.getLong(Album.VIDEOTIME);
        mHasCamera = argument.getBoolean(Album.KEY_INPUT_ALLOW_CAMERA);
        mLimitCount = argument.getInt(Album.KEY_INPUT_LIMIT_COUNT);
        mQuality = argument.getInt(Album.KEY_INPUT_CAMERA_QUALITY);
        videoTimeFlite = argument.getInt(Album.VIDEOTIME);
        mLimitDuration = argument.getLong(Album.KEY_INPUT_CAMERA_DURATION);
        mLimitBytes = argument.getLong(Album.KEY_INPUT_CAMERA_BYTES);
        material_info = argument.getString(Album.KEY_INPUT_MATERIALINFO);
        mFilterVisibility = argument.getBoolean(Album.KEY_INPUT_FILTER_VISIBILITY);
        mTitle = argument.getString(Album.MODEL_TITLE);
        //如果传进来的标题为空，就认为不是从模板页面过来的
        showCapture = !TextUtils.isEmpty(mTitle);
        mMusicPath = argument.getString(Album.MUSIC_PATH);
    }

    /**
     * Use different layouts depending on the style.
     *
     * @return layout id.
     */
    private int createView() {

        if (mWidget != null) {
            switch (mWidget.getUiStyle()) {
                case Widget.STYLE_DARK: {
                    return R.layout.album_activity_album_dark;
                }
                case Widget.STYLE_LIGHT: {
                    return R.layout.album_activity_album_light;
                }
                default: {
                    throw new AssertionError("This should not be the case.");
                }
            }
        } else {
            return R.layout.album_activity_album_light;
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mView.onConfigurationChanged(newConfig);
        if (mFolderDialog != null && !mFolderDialog.isShowing()) {
            mFolderDialog = null;
        }
    }

    @Override
    protected void onPermissionGranted(int code) {
        ArrayList<AlbumFile> checkedList = getIntent().getParcelableArrayListExtra(Album.KEY_INPUT_CHECKED_LIST);
        MediaReader mediaReader = new MediaReader(this, sSizeFilter, sMimeFilter, sDurationFilter, mFilterVisibility);
        mMediaReadTask = new MediaReadTask(mFunction, checkedList, mediaReader, this);
        mMediaReadTask.execute();
    }

    @Override
    protected void onPermissionDenied(int code) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.album_title_permission_failed)
                .setMessage(R.string.album_permission_storage_failed_hint)
                .setPositiveButton(R.string.album_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callbackCancel();
                    }
                })
                .show();
    }

    @Override
    public void onScanCallback(ArrayList<AlbumFolder> albumFolders, ArrayList<AlbumFile> checkedFiles) {
        mMediaReadTask = null;
        switch (mChoiceMode) {
            case Album.MODE_MULTIPLE: {
                mView.setCompleteDisplay(true);
                break;
            }
            case Album.MODE_SINGLE: {
                mView.setCompleteDisplay(false);
                break;
            }
            default: {
                throw new AssertionError("This should not be the case.");
            }
        }

        mView.setLoadingDisplay(false);
        mAlbumFolders = albumFolders;
        mCheckedList = checkedFiles;

        if (mAlbumFolders.get(0).getAlbumFiles().isEmpty()) {
            Intent intent = new Intent(this, NullActivity.class);
            intent.putExtras(getIntent());
            startActivityForResult(intent, CODE_ACTIVITY_NULL);
        } else {
            showFolderAlbumFiles(0);
            int count = mCheckedList.size();
            mView.setCheckedCountAndTotal(count, mLimitCount);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE_ACTIVITY_NULL: {
                if (resultCode == RESULT_OK) {
                    String imagePath = NullActivity.parsePath(data);
                    String mimeType = AlbumUtils.getMimeType(imagePath);
                    if (!TextUtils.isEmpty(mimeType)) {
                        mCameraAction.onAction(imagePath, false);
                    }
                } else {
                    callbackCancel();
                }
                break;
            }
            case CODE_TO_CAPTURE:
                if (resultCode == RESULT_OK) {
                    //从拍摄页面返回的视频地址
                    String captureUrl = data.getStringExtra(CaptureActivity.RESULT_FILE_PATH);
                    AlbumFile albumFile = new AlbumFile();
                    albumFile.setPath(captureUrl);
                    ArrayList<AlbumFile> albumFileList = new ArrayList<>();
                    albumFileList.add(albumFile);
                    Log.d(TAG, "onActivityResult: " + captureUrl);
                    if (sResult != null) {
                        sResult.onAction(albumFileList, true);
                    }
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void clickFolderSwitch() {
        if (mFolderDialog == null) {
            mFolderDialog = new FolderDialog(this, mWidget, mAlbumFolders, new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    mCurrentFolder = position;
                    showFolderAlbumFiles(mCurrentFolder);
                }
            });
        }
        if (!mFolderDialog.isShowing()) {
            mFolderDialog.show();
        }
    }

    /**
     * Update data source.
     */
    private void showFolderAlbumFiles(int position) {
        this.mCurrentFolder = position;
        AlbumFolder albumFolder = mAlbumFolders.get(position);
        mView.bindAlbumFolder(albumFolder);
    }

    @Override
    public void clickCamera(View v) {
        int hasCheckSize = mCheckedList.size();
        if (hasCheckSize >= mLimitCount) {
            int messageRes;
            switch (mFunction) {
                case Album.FUNCTION_CHOICE_IMAGE: {
                    messageRes = R.plurals.album_check_image_limit_camera;
                    break;
                }
                case Album.FUNCTION_CHOICE_VIDEO: {
                    messageRes = R.plurals.album_check_video_limit_camera;
                    break;
                }
                case Album.FUNCTION_CHOICE_ALBUM: {
                    messageRes = R.plurals.album_check_album_limit_camera;
                    break;
                }
                default: {
                    throw new AssertionError("This should not be the case.");
                }
            }
            mView.toast(getResources().getQuantityString(messageRes, mLimitCount, mLimitCount));
        } else {


            switch (mFunction) {
                case Album.FUNCTION_CHOICE_IMAGE: {
                    takePicture();
                    break;
                }
                case Album.FUNCTION_CHOICE_VIDEO: {
                    takeVideo();
                    break;
                }
                case Album.FUNCTION_CHOICE_ALBUM: {
                    if (mCameraPopupMenu == null) {
                        mCameraPopupMenu = new PopupMenu(this, v);
                        mCameraPopupMenu.getMenuInflater().inflate(R.menu.album_menu_item_camera, mCameraPopupMenu.getMenu());
                        mCameraPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                int id = item.getItemId();
                                if (id == R.id.album_menu_camera_image) {
                                    takePicture();
                                } else if (id == R.id.album_menu_camera_video) {
                                    takeVideo();
                                }
                                return true;
                            }
                        });
                    }
                    mCameraPopupMenu.show();
                    break;
                }
                default: {
                    throw new AssertionError("This should not be the case.");
                }
            }
        }
    }

    private void takePicture() {
        String filePath;
        if (mCurrentFolder == 0) {
            filePath = AlbumUtils.randomJPGPath();
        } else {
            File file = new File(mAlbumFolders.get(mCurrentFolder).getAlbumFiles().get(0).getPath());
            filePath = AlbumUtils.randomJPGPath(file.getParentFile());
        }
        Album.camera(this)
                .image()
                .filePath(filePath)
                .onResult(mCameraAction)
                .start();
    }

    private void takeVideo() {
        String filePath;
        if (mCurrentFolder == 0) {
            filePath = AlbumUtils.randomMP4Path();
        } else {
            File file = new File(mAlbumFolders.get(mCurrentFolder).getAlbumFiles().get(0).getPath());
            filePath = AlbumUtils.randomMP4Path(file.getParentFile());
        }
        Album.camera(this)
                .video()
                .filePath(filePath)
                .quality(mQuality)
                .limitDuration(mLimitDuration)
                .limitBytes(mLimitBytes)
                .onResult(mCameraAction)
                .start();
    }


    private Action<String> mCameraAction = new Action<String>() {
        @Override
        public void onAction(@NonNull String result, boolean isFromCamera) {
            if (mMediaScanner == null) {
                mMediaScanner = new MediaScanner(AlbumActivity.this);
            }
            mMediaScanner.scan(result);

            PathConversion conversion = new PathConversion(sSizeFilter, sMimeFilter, sDurationFilter);
            PathConvertTask task = new PathConvertTask(conversion, AlbumActivity.this);
            task.execute(result);
        }
    };

    @Override
    public void onConvertStart() {
        showLoadingDialog();
        mLoadingDialog.setMessage(R.string.album_converting);
    }

    @Override
    public void onConvertCallback(AlbumFile albumFile) {
        albumFile.setChecked(!albumFile.isDisable());
        if (albumFile.isDisable()) {
            if (mFilterVisibility) {
                addFileToList(albumFile);
            } else {
                mView.toast(getString(R.string.album_take_file_unavailable));
            }
        } else {
            addFileToList(albumFile);
        }

        dismissLoadingDialog();
    }

    private void addFileToList(AlbumFile albumFile) {
        if (mCurrentFolder != 0) {
            List<AlbumFile> albumFiles = mAlbumFolders.get(0).getAlbumFiles();
            if (albumFiles.size() > 0) {
                albumFiles.add(0, albumFile);
            } else {
                albumFiles.add(albumFile);
            }
        }

        AlbumFolder albumFolder = mAlbumFolders.get(mCurrentFolder);
        List<AlbumFile> albumFiles = albumFolder.getAlbumFiles();
        if (albumFiles.isEmpty()) {
            albumFiles.add(albumFile);
            mView.bindAlbumFolder(albumFolder);
        } else {
            albumFiles.add(0, albumFile);
            mView.notifyInsertItem(mHasCamera ? 1 : 0);
        }

        mCheckedList.add(albumFile);
        int count = mCheckedList.size();
        mView.setCheckedCountAndTotal(count, mLimitCount);

        switch (mChoiceMode) {
            case Album.MODE_SINGLE: {
                callbackResult();
                break;
            }
            case Album.MODE_MULTIPLE: {
                // Nothing.
                break;
            }
            default: {
                throw new AssertionError("This should not be the case.");
            }
        }
    }

    @Override
    public void tryCheckItem(CompoundButton button, int position) {

        AlbumFile albumFile = mAlbumFolders.get(mCurrentFolder).getAlbumFiles().get(position);
        if (button.isChecked()) {
            if (mCheckedList.size() >= mLimitCount) {
                int messageRes;
                switch (mFunction) {
                    case Album.FUNCTION_CHOICE_IMAGE: {
                        messageRes = R.plurals.album_check_image_limit;
                        break;
                    }
                    case Album.FUNCTION_CHOICE_VIDEO: {
                        messageRes = R.plurals.album_check_video_limit;
                        break;
                    }
                    case Album.FUNCTION_CHOICE_ALBUM: {
                        messageRes = R.plurals.album_check_album_limit;
                        break;
                    }
                    default: {
                        throw new AssertionError("This should not be the case.");
                    }
                }
                mView.toast(getResources().getQuantityString(messageRes, mLimitCount, mLimitCount));
                button.setChecked(false);
            } else {
                PhotoChooseIndex.getInstance().PutPhotoIndex(position);
                albumFile.setChecked(true);
                mCheckedList.add(albumFile);
                setCheckedCount();
            }
        } else {
            PhotoChooseIndex.getInstance().PutPhotoIndex(position);
            albumFile.setChecked(false);
            mCheckedList.remove(albumFile);
            setCheckedCount();
        }
    }

    private void setCheckedCount() {
        int count = mCheckedList.size();
        mView.setCheckedCountAndTotal(count, mLimitCount);
    }

    @Override
    public void tryPreviewItem(int position) {
        switch (mChoiceMode) {
            case Album.MODE_SINGLE: {
                AlbumFile albumFile = mAlbumFolders.get(mCurrentFolder).getAlbumFiles().get(position);
//                albumFile.setChecked(true);
//                mView.notifyItem(position);
                mCheckedList.add(albumFile);
                setCheckedCount();

                callbackResult();
                break;
            }
            case Album.MODE_MULTIPLE: {
                GalleryActivity.sAlbumFiles = mAlbumFolders.get(mCurrentFolder).getAlbumFiles();
                GalleryActivity.sCheckedCount = mCheckedList.size();
                GalleryActivity.sCurrentPosition = position;
                GalleryActivity.sCallback = this;
                Intent intent = new Intent(this, GalleryActivity.class);
                intent.putExtras(getIntent());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            }
            default: {
                throw new AssertionError("This should not be the case.");
            }
        }
    }

    @Override
    public void tryPreviewChecked() {
        if (mCheckedList != null && mCheckedList.size() > 0) {
            GalleryActivity.sAlbumFiles = new ArrayList<>(mCheckedList);
            GalleryActivity.sCheckedCount = mCheckedList.size();
            GalleryActivity.sCurrentPosition = 0;
            GalleryActivity.sCallback = this;
            Intent intent = new Intent(this, GalleryActivity.class);
            intent.putExtras(getIntent());
            startActivity(intent);
        }
    }


    @Override
    public void toCapturePage() {
        //点击拍摄按钮
//        Bundle captureBundle = new Bundle();
//        captureBundle.putLong(Album.VIDEOTIME, mMineVideoTime);
//        captureBundle.putString(Album.MODEL_TITLE, mTitle);
//        captureBundle.putString(Album.MUSIC_PATH, mMusicPath);
//        Intent intent = new Intent(this, CaptureActivity.class);
//        intent.putExtras(captureBundle);
//        startActivityForResult(intent, CODE_TO_CAPTURE);

        AlbumFile albumFile = new AlbumFile();
        albumFile.setClickToCamera(true);
        mCheckedList.clear();
        mCheckedList.add(albumFile);
        ThumbnailBuildTask task = new ThumbnailBuildTask(this, mCheckedList, this);
        task.execute();
    }

    @Override
    public void onPreviewComplete() {
//        if (!TextUtils.isEmpty(material_info) && material_info.equals("pictureAlbum")) {
//
//        } else {
//            callbackResult();
//        }
        if (mCheckedList.size() < mLimitCount) {
            mCheckedList = toBespreadMaterial();
            ThumbnailBuildTask task = new ThumbnailBuildTask(this, mCheckedList, this);
            task.execute();
        } else {
            callbackResult();
        }
    }

    @Override
    public void onPreviewChanged(AlbumFile albumFile) {
        ArrayList<AlbumFile> albumFiles = mAlbumFolders.get(mCurrentFolder).getAlbumFiles();
        int position = albumFiles.indexOf(albumFile);
        int notifyPosition = mHasCamera ? position + 1 : position;
        mView.notifyItem(notifyPosition);

        if (albumFile.isChecked()) {
            if (!mCheckedList.contains(albumFile)) {
                mCheckedList.add(albumFile);
            }
        } else {
            mCheckedList.remove(albumFile);
        }
        setCheckedCount();
    }

    @Override
    public void complete() {
        if (mCheckedList.isEmpty()) {
            int messageRes;
            switch (mFunction) {
                case Album.FUNCTION_CHOICE_IMAGE: {
                    messageRes = R.string.album_check_image_little;
                    mView.setSingleCompletion(false);
                    break;
                }
                case Album.FUNCTION_CHOICE_VIDEO: {
                    messageRes = R.string.album_check_video_little;
                    mView.setSingleCompletion(false);
                    break;
                }
                case Album.FUNCTION_CHOICE_ALBUM: {
                    messageRes = R.string.album_check_album_little;
                    mView.setSingleCompletion(false);
                    break;
                }
                default: {
                    throw new AssertionError("This should not be the case.");
                }
            }
            mView.toast(messageRes);
        } else {
            if (mCheckedList.size() < mLimitCount) {
//                    showMaterialCountDialog();
                mCheckedList = toBespreadMaterial();
                ThumbnailBuildTask task = new ThumbnailBuildTask(this, mCheckedList, this);
                task.execute();
            } else {
                callbackResult();
            }
//            if (!TextUtils.isEmpty(material_info) && material_info.equals("pictureAlbum")) {
//
//            } else {
//                callbackResult();
//            }
        }
    }

    @Override
    public void reLoadAlbumData(TabLayout.Tab tab) {
        Log.d(TAG, "reLoadAlbumData: " + tab.getPosition());
        //todo 这个方法还有优化空间
        String tabStr = tab.getText().toString();
        Log.d(TAG, "reLoadAlbumData: " + tabStr);
        switch (tabStr) {
            case "全部":
                mFunction = Album.FUNCTION_CHOICE_ALBUM;
                break;
            case "图片":
                mFunction = Album.FUNCTION_CHOICE_IMAGE;
                break;
            case "视频":
                mFunction = Album.FUNCTION_CHOICE_VIDEO;
                break;
            default:
                break;
        }
        requestPermission(PERMISSION_STORAGE, CODE_PERMISSION_STORAGE);
    }

    @Override
    public void onBackPressed() {
        if (mMediaReadTask != null) {
            mMediaReadTask.cancel(true);
        }
        callbackCancel();
    }

    /**
     * Callback result action.
     * 如果传了最低要求视频时长且只选了一个视频的时候，当选择视频时长小于规定时长时需要给出提示
     */
    private void callbackResult() {
        long chooseDuration = mCheckedList.get(0).getDuration();
        if (mMineVideoTime != 0 && mCheckedList.get(0).getMediaType() == AlbumFile.TYPE_VIDEO && mCheckedList.size() == 1) {
            if (chooseDuration < mMineVideoTime) {
                showDialog();
            } else {
                ThumbnailBuildTask task = new ThumbnailBuildTask(this, mCheckedList, this);
                task.execute();
            }
        } else {
            ThumbnailBuildTask task = new ThumbnailBuildTask(this, mCheckedList, this);
            task.execute();
        }
    }


    public void showDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AlbumActivity.this);
        builder.setTitle("提示");
        int needTime = (int) (mMineVideoTime / (float) 1000);
        builder.setMessage("此模板上传" + needTime + "秒视频最佳\n" +
                "不然画面会少一些哟");
        builder.setNegativeButton("取消", (dialog, which) -> {
            mView.setSingleCompletion(false);
            dialog.dismiss();
        });
        builder.setPositiveButton("确定", (dialog, which) -> {
            ThumbnailBuildTask task = new ThumbnailBuildTask(this, mCheckedList, this);
            task.execute();
            dialog.dismiss();
        });
        builder.setCancelable(true);
        Dialog mDialog = builder.show();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    public void showMaterialCountDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AlbumActivity.this);
        builder.setTitle("提示");
        int needTime = (int) (mMineVideoTime / (float) 1000);
        builder.setMessage("少于20张素材最佳\n" +
                "会随机铺满哟");
        builder.setNegativeButton("取消", (dialog, which) -> {
            mView.setSingleCompletion(false);
            dialog.dismiss();
        });
        builder.setPositiveButton("确定", (dialog, which) -> {
            mCheckedList = toBespreadMaterial();
            ThumbnailBuildTask task = new ThumbnailBuildTask(this, mCheckedList, this);
            task.execute();
            dialog.dismiss();
        });
        builder.setCancelable(true);
        Dialog mDialog = builder.show();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }


    /**
     * description ：铺满素材
     * creation date: 2020/11/16
     * user : zhangtongju
     */
    private ArrayList<AlbumFile> toBespreadMaterial() {
        if (mCheckedList != null && mCheckedList.size() > 0) {
            int CheckListSize = mCheckedList.size();
            if (CheckListSize < mLimitCount) {
                Random r = new Random(1);
                int needAddSize = mLimitCount - mCheckedList.size();
                for (int i = 0; i < needAddSize; i++) {
                    mCheckedList.add(mCheckedList.get(r.nextInt(CheckListSize)));
                }
            }
        }

        return mCheckedList;


    }


    @Override
    public void onThumbnailStart() {
        showLoadingDialog();
        mLoadingDialog.setMessage(R.string.album_thumbnail);
    }

    @Override
    public void onThumbnailCallback(ArrayList<AlbumFile> albumFiles) {
        Log.d(TAG, "paths.size = " + albumFiles.size());
        if (sResult != null) {
            sResult.onAction(albumFiles, false);
        }
        dismissLoadingDialog();
        finish();
    }

    /**
     * Callback cancel action.
     */
    private void callbackCancel() {
        if (sCancel != null) {
            sCancel.onAction("User canceled.", false);
        }
        finish();
    }

    /**
     * Display loading dialog.
     */
    private void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this);
            mLoadingDialog.setupViews(mWidget);
        }
        if (!mLoadingDialog.isShowing() && !isOnDestroy) {
            mLoadingDialog.show();
        }
    }

    /**
     * Dismiss loading dialog.
     */
    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void returnAdContainer(LinearLayout flAdContainer) {
        Log.d(TAG, "returnAdContainer");
        if (sActionView != null) {
            sActionView.onAction(flAdContainer, false);
        }

    }

    private boolean isOnDestroy = false;

    @Override
    protected void onDestroy() {
        isOnDestroy = true;
        super.onDestroy();
    }

    @Override
    public void finish() {
        sSizeFilter = null;
        sMimeFilter = null;
        sDurationFilter = null;
        sResult = null;
        sActionView = null;
        sCancel = null;
        super.finish();
    }
}