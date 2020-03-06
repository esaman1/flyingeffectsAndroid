package com.shixing.sxve.ui.model;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.util.Log;
import android.util.SparseArray;
import android.webkit.MimeTypeMap;

import com.shixing.sxve.ui.AssetDelegate;
import com.shixing.sxve.ui.albumType;
import com.shixing.sxve.ui.util.FileUtils;
import com.shixing.sxve.ui.util.Size;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TemplateModel {
    private static final String TAG = "TemplateModel";
    private static final String CONFIG_FILE_NAME = "config.json";
    private  int mDuration;
    // 所有可替换的asset，用于编辑完，渲染开始前获取替换的素材路径
    private List<AssetModel> mAssets = new ArrayList<>();

    // 可替换图片和视频的asset，用于批量替换时
    private List<AssetModel> mReplaceableAssets = new ArrayList<>();

    public SparseArray<GroupModel> groups = new SparseArray<>();
    public final float fps;
    public int groupSize;

    @WorkerThread
    public TemplateModel(String templateFolder, AssetDelegate delegate, Context context) throws IOException, JSONException {
        File folder = new File(templateFolder);
        File configFile = new File(folder, CONFIG_FILE_NAME);
        if (!configFile.exists()) {
            throw new IllegalArgumentException("config file not found");
        }

        String configJson = FileUtils.readJsonFromFile(configFile);
        JSONObject config = new JSONObject(configJson);

        int uiVersionMajor = 1;
        if (config.has("ui_version")) { //ui 2.0 新增version字段
            String versionStr = config.getString("ui_version");
            String majorStr = versionStr.substring(0, versionStr.indexOf('.'));
            uiVersionMajor = Integer.parseInt(majorStr);
        }

        fps = (float) config.getDouble("fps");

        JSONArray assets = config.getJSONArray("assets");
        for (int i = 0; i < assets.length(); i++) {
            JSONObject asset = assets.getJSONObject(i);
            if (asset.has("ui")) {
                AssetModel assetModel = new AssetModel(folder.getPath(), asset, delegate, uiVersionMajor);
                mAssets.add(assetModel);

                int group = assetModel.ui.group;
                if (groupSize < group) groupSize = group;

                GroupModel groupModel = groups.get(group);
                if (groupModel == null) {
                    groupModel = new GroupModel();
                    groups.put(group, groupModel);
                }

                groupModel.add(assetModel);
            }
        }

        try{
            JSONArray assetsComps= config.getJSONArray("comps");
            JSONObject comoOb= (JSONObject) assetsComps.get(0);
            mDuration=comoOb.getInt("duration");
        }catch (Exception e){
            Log.d("Exception",e.getMessage());
        }


        //一个GroupModel 里面可能包含多个同组不同index
        for (int i = 1; i <= groups.size(); i++) { //group index从1开始
            GroupModel groupModel = groups.get(i);
            SparseArray<AssetModel> groupAssets = groupModel.getAssets();
            for (int j = 0; j < groupAssets.size(); j++) {
                if (groupAssets.get(j).type == AssetModel.TYPE_MEDIA) {
                    mReplaceableAssets.add(groupAssets.get(j));
                }
            }
        }

        if (uiVersionMajor > 1) { //ui 2.0 新增ui_group字段
            JSONArray group_size = config.getJSONArray("ui_group");
            for (int i = 0; i < group_size.length(); i++) {
                JSONObject obj = group_size.getJSONObject(i);
                JSONArray size = obj.getJSONArray("size");
                int width = size.getInt(0);
                int height = size.getInt(1);

                groups.get(i + 1).setSize(new Size(width, height));
            }
        }
    }

    @WorkerThread
    public String[] getReplaceableFilePaths(String folder) {
        String[] paths = new String[mAssets.size()];
        for (int i = 0; i < mAssets.size(); i++) {
            paths[i] = mAssets.get(i).ui.getSnapPath(folder);
        }
        return paths;
    }

    public int getAssetsSize() {
        return mReplaceableAssets.size();
    }

    public void setReplaceFiles(List<String> paths) {
        for (int i = 0; i < paths.size(); i++) {
            AssetModel assetModel = mReplaceableAssets.get(i);
            ((MediaUiModel) assetModel.ui).setImageAsset(paths.get(i));
        }
    }


    public int getDuration() {
    return mDuration;
    }



    /**
     * description ：统一裁剪，只需要知道裁剪大小
     * 替换用户选择的图片和视频,默认裁剪为传过来的动画时间。
     * 比较耗性能
     * date: ：2019/5/14 14:37
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    private int allVideoNumber = 0;
    private int isReadyCutVideo;

    private List<AssetModel> mediaUIModelList;
    private boolean isReplaceMaterial = false;
    private List<AssetModel> textUIModelList;

    public void setReplaceAllFiles(List<String> paths, Context context, isFirstReplaceComplete firstReplaceComplete, final String tag) {  //批量选择图片,视频的替换方法
        allVideoNumber = 0;
        isReadyCutVideo = 0;
        isReplaceMaterial = false;
        mediaUIModelList = new ArrayList<>();
        textUIModelList = new ArrayList<>();
        if (mReplaceableAssets != null && mReplaceableAssets.size() > 0) {
            for (int i = 0; i < mReplaceableAssets.size(); i++) {
                if (mReplaceableAssets.get(i) != null) {
                    if (mReplaceableAssets.get(i).ui instanceof MediaUiModel) {
                        mediaUIModelList.add(mReplaceableAssets.get(i));
                        textUIModelList.add(null); //todo 考虑到文字在中间的情况，补位，解决数组越界
                    } else if (mReplaceableAssets.get(i).ui instanceof TextUiModel) {
                        textUIModelList.add(mReplaceableAssets.get(i));
                    }
                }

            }
        }
        for (int i = 0; i < paths.size(); i++) {
            if (paths.get(i) != null && !paths.get(i).equals("")) {
                String mimeType;
                String extension = MimeTypeMap.getFileExtensionFromUrl(paths.get(i)); //获得格式
                if (extension != null && !extension.equals("")) {
                    mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                    if (mimeType == null) {
                        mimeType = getPathType(paths.get(i));
                    }
                } else {  //有些手机获取不到，比如vivo 是中文目录
                    mimeType = getPathType(paths.get(i));
                }
                if (!mediaUIModelList.isEmpty() && mediaUIModelList.size() > i && mediaUIModelList.get(i) != null) {
                    AssetModel assetModel = mediaUIModelList.get(i);
                    if (i == 0) {
                       // (assetModel.ui).isShow(true);
                    }
                    if (albumType.isImage(mimeType)) {
                        ((MediaUiModel) assetModel.ui).setImageAsset(paths.get(i));//, context
                    } else if (albumType.isVideo(mimeType)) {
//                        String VideoPathOrigin = paths.get(i);
//                        allVideoNumber++;
//                        final MediaUiModel1 mModel = (MediaUiModel1) assetModel.ui;
//                        mModel.setVideoPathOrigin(VideoPathOrigin);//设置视频的源文件地址
//                        mModel.isVideoSlide = true;
//                        mModel.setVideoPath(VideoPathOrigin);
                    }
                }
            }
            if (i == paths.size() - 1) {
                firstReplaceComplete.isComplete(true);
            }
        }
    }



    public void setReplaceAllMaterial(List<String>list){
        if (mReplaceableAssets != null && mReplaceableAssets.size() > 0) {
            for (int i = 0; i < mReplaceableAssets.size(); i++) {
                if (mReplaceableAssets.get(i) != null) {
                    if (mReplaceableAssets.get(i).ui instanceof MediaUiModel) {
                        MediaUiModel2 media= (MediaUiModel2) mReplaceableAssets.get(i).ui;
                        media.setImageAsset(list.get(i));
                        textUIModelList.add(null); //todo 考虑到文字在中间的情况，补位，解决数组越界
                    } else if (mReplaceableAssets.get(i).ui instanceof TextUiModel) {
                        textUIModelList.add(mReplaceableAssets.get(i));
                    }
                }

            }
        }



    }






    private String getPathType(String path) {
        String mimeType;
        String suffix = path.substring(path.lastIndexOf(".") + 1).toLowerCase();
        if (suffix.equalsIgnoreCase("mp4") || suffix.equalsIgnoreCase("M4V") || suffix.equalsIgnoreCase("3gp") || suffix.equals("3G2") || suffix.equalsIgnoreCase("WMV") || suffix.equalsIgnoreCase("ASF") || suffix.equalsIgnoreCase("AVI") || suffix.equalsIgnoreCase("FLV") || suffix.equalsIgnoreCase("MKV") || suffix.equalsIgnoreCase("WEBM")) {
            mimeType = "video/*";
        } else {
            mimeType = "image/*";
        }
        return mimeType;
    }


    public interface isFirstReplaceComplete {
        void isComplete(boolean complete);
    }


}
