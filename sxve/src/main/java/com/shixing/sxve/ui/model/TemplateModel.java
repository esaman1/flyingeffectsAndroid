package com.shixing.sxve.ui.model;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.glidebitmappool.GlideBitmapPool;
import com.shixing.sxve.ui.AssetDelegate;
import com.shixing.sxve.ui.SourceVideoWH;
import com.shixing.sxve.ui.allVideoCutAchieveListenner;
import com.shixing.sxve.ui.util.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * description ：模板类
 * date: ：2019/5/7 20:06
 * author: 张同举 @邮箱 jutongzhang@sina.com
 */
public class TemplateModel {
    private static final String TAG = "TemplateModel";
    private static final String CONFIG_FILE_NAME = "config.json";
    public Boolean HasBj = false;

    private List<AssetModel> mAssets = new ArrayList<>();

    private List<AssetModel> mReplaceableAssets = new ArrayList<>();
    public SparseArray<GroupModel> groups = new SparseArray<>();//这个值表示可替换模板的数量，如果有背景，那么这个值应该减一
    public int fps = 0;
    private int placeholderNum = 0;
    public int groupSize;  //这个值表示可替换模板的数量，如果有背景，那么这个值应该减一
    private Context context;

    public int[] getCompSize() {
        return compSize;
    }

    public void setCompSize(int[] compSize) {
        this.compSize = compSize;
    }

    private int[] compSize;

    public AssetModel getBgModel() {
        return bgModel;
    }

    public void setBgModel(AssetModel bgModel) {
        this.bgModel = bgModel;
    }

    private AssetModel bgModel;

    @WorkerThread
    public TemplateModel(String templateFolder, AssetDelegate delegate, final Context context) throws IOException, JSONException {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        placeholderNum = 0;
        GlideBitmapPool.initialize(cacheSize); // 20mb max memory size
        File folder = new File(templateFolder);
        this.context = context;
        File configFile = new File(folder, CONFIG_FILE_NAME);//得到配置文件，获得fps,图片占位置信息，图片需要显示的顺序，视频等
        if (configFile.exists()) {
            String configJson = FileUtils.readJsonFromFile(configFile);
            Log.d("OOM", "configJson=" + configJson);
            JSONObject config = new JSONObject(configJson);

            fps = config.getInt("fps");
            JSONArray assets = config.getJSONArray("assets");
            JSONArray comps = config.getJSONArray("comps");
            JSONObject object = new JSONObject(comps.get(0).toString());
            compSize = getIntArray(object.getJSONArray("size"));
            for (int i = 0; i < assets.length(); i++) {
                JSONObject asset = assets.getJSONObject(i);
                if (asset.has("ui")) {
                    AssetModel assetModel;
                    if (!asset.get("ui_extra").equals("BG")) { // 只要不是背景图片
                        if (!TextUtils.isEmpty(asset.get("ui_extra").toString())) {
                            String showSlow = asset.get("ui_extra").toString();
                            String str2 = showSlow.replace(" ", "");//去掉所用空格
                            List<String> list = Arrays.asList(str2.split(","));
                            String type = list.get(0);
                            if (type.equals("slow")) {
                                //变慢标志
                                String startFrame = list.get(1);
                                String endFrame = list.get(2);
                                //变慢级别
                                String slowLevel = list.get(3);
                                assetModel = new AssetModel(folder.getPath(), asset, delegate, context, startFrame, endFrame, slowLevel);
                            } else {
                                assetModel = new AssetModel(folder.getPath(), asset, delegate, context);
                            }
                        } else {
                            assetModel = new AssetModel(folder.getPath(), asset, delegate, context);
                        }
                        mAssets.add(assetModel); //里面保存了所有的图片,文字集合
                        int group = assetModel.ui.group;
                        if (groupSize < group)
                            groupSize = group; //得到最大的group 的值，group 就是位置，但这里最大值是没包括背景的
                        GroupModel groupModel = groups.get(group);
                        if (groupModel == null) {
                            groupModel = new GroupModel();
                            groups.put(group, groupModel);
                        }
                        groupModel.add(assetModel); //这里也加入了assetModel，groups 里面的,一个groupModel对应一个AssetModel
                    } else {
                        HasBj = true;
                        bgModel = new AssetModel(folder.getPath(), asset, delegate, context);
                        int group = bgModel.ui.group;
                        if (groupSize < group)
                            groupSize = group; //得到最大的group 的值，group 就是位置，但这里最大值是没包括背景的
                    }
                }
            }
            placeholderNum = HasBj ? 1 : 0; //如果有背景，那么groups 会少一个
            for (int i = 1; i <= groups.size() + placeholderNum; i++) { //可替代的图片集合mReplaceableAssets
                GroupModel groupModel = groups.get(i);
                if (groupModel != null) { //因为有了背景图，所有这里第一个可能会为null,如果为null ，那么就表示为背景图
                    SparseArray<AssetModel> groupAssets = groupModel.getAssets();
                    ArrayList<TextUiModel> tempModels = new ArrayList<>();
                    for (int j = 0; j < groupAssets.size(); j++) {
                        if (groupAssets.get(j).type == AssetModel.TYPE_MEDIA) {
                            mReplaceableAssets.add(groupAssets.get(j));
                        } else if (groupAssets.get(j).type == AssetModel.TYPE_TEXT) {
                            tempModels.add((TextUiModel) groupAssets.get(j).ui);
                            mReplaceableAssets.add(groupAssets.get(j));
                        }
                    }
                    // TODO: 2019/9/26 模板解析出来的高度不对，临时调整Model，后期肯定要改对了
                    for (TextUiModel x : tempModels) {
                        x.adjustTextArea((compSize[1] - groupModel.getSize().getHeight()) / 2);
                    }
                }
            }
        }

    }


    //导出之前之后动态修改groups集合，修改文字位置，临时Debug的手段
    public void restoreTemplate(boolean isDown, String path) {
        if (isDown) {
            if (HasBj && groups.get(groupSize) != null) {
                groups.remove(bgModel.groupId);
                mAssets.remove(mAssets.size() - 1);
            }
        } else {
            if (HasBj && groups.get(groupSize) == null) {
                GroupModel defaultModel = new GroupModel();
                defaultModel.add(this.getBgModel());
                groups.put(bgModel.groupId, defaultModel);
                mAssets.add(bgModel);


                SparseArray<AssetModel> groupAssets = groups.get(groupSize).getAssets();
                for (int j = 0; j < groupAssets.size(); j++) {
                    if (groupAssets.get(j).type == AssetModel.TYPE_MEDIA) {
                        MediaUiModel model = (MediaUiModel) groupAssets.get(j).ui;
                        model.setImageAsset(path, context);
                    }
                }
            }
        }
        for (int i = 1; i <= groups.size(); i++) {
            if (groups.get(i) != null) {
                GroupModel groupModel = groups.get(i);
                SparseArray<AssetModel> groupAssets = groupModel.getAssets();
                for (int j = 0; j < groupAssets.size(); j++) {
                    if (groupAssets.get(j).type == AssetModel.TYPE_TEXT) {
                        TextUiModel model = (TextUiModel) groupAssets.get(j).ui;
                        model.setTextChanged(false);
                        // TODO: 2019/9/26 模板解析出来的高度不对，临时调整Model，后期肯定要改对了
                        if (isDown) {
                            model.adjustTextArea((compSize[1] - groupModel.getSize().getHeight()) / 2);
                        } else {
                            model.adjustTextArea(-(compSize[1] - groupModel.getSize().getHeight()) / 2);
                        }
                    }
                }
            }
        }
    }

    int[] getIntArray(JSONArray array) throws JSONException {
        int[] ints = new int[array.length()];
        for (int i = 0; i < array.length(); i++) {
            ints[i] = array.getInt(i);
        }
        return ints;
    }

    @WorkerThread
    public String[] getReplaceableFilePaths(String folder) { //未设置占位图
        String[] paths = new String[mAssets.size()];
        synchronized (new Object()) {
            for (int i = 0; i < mAssets.size(); i++) {
//                Log.d("OOM", "第" + i + "张地址为" + mAssets.get(i).ui.getSnapPath(folder));
                paths[i] = mAssets.get(i).ui.getSnapPath(folder);
            }
        }
        return paths;
    }


    @WorkerThread
    public String[] getReplaceableFilePathsForKeep(String folder, boolean hasPlaceholder) { //未设置占位图
        String[] paths = new String[mAssets.size()];
        synchronized (new Object()) {
            for (int i = 0; i < mAssets.size(); i++) {
//                Log.d("OOM", "第" + i + "张地址为" + mAssets.get(i).ui.getSnapPath(folder));
                paths[i] = mAssets.get(i).ui.getSnapPathForKeep(folder);
            }
        }
        return paths;
    }


    @WorkerThread
    public String[] getReplaceableOriginFilePaths(String folder) { //未设置占位图
        String[] paths = new String[mAssets.size()];
        for (int i = 0; i < mAssets.size(); i++) {
            paths[i] = mAssets.get(i).ui.getOriginPath(folder);
        }
        return paths;
    }


    public int getAssetsSize() {
        return mReplaceableAssets.size();
    }

    public void setReplaceFiles(List<String> paths) {  //批量选择图片后的替换方法
        int index = 0;
        for (int i = 0; i < paths.size(); i++) {
            AssetModel assetModel = mReplaceableAssets.get(i);
            for (int j = 0; j < mediaUIModelList.size(); j++) {
                if (mediaUIModelList.get(j) == assetModel) {
                    index = j;
                }
            }
            ((MediaUiModel) assetModel.ui).setImageAsset(paths.get(i), context);
            ((MediaUiModel) mediaUIModelList.get(index).ui).setImageAsset(paths.get(i), context);
        }
    }


    /**
     * description ：更新uiModel,提示需要重新渲染bitmap
     * date: ：2019/11/4 10:31
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public void setHasFilter(final int position) {
        for (int i = 0; i < mAssets.size(); i++) {
            AssetModel model = mAssets.get(i);
            if (model.type == AssetModel.TYPE_MEDIA) { //如果类型是meidiaUiModel
                model.ui.hasChooseFilter(position);
            }
        }
    }


    /**
     * description ：通知单个刷新页面，节约内存
     * date: ：2019/11/19 20:26
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public void setSingleHasFilter(final int position, int selectedPosition) {
        AssetModel model = mAssets.get(selectedPosition);
        if (model.type == AssetModel.TYPE_MEDIA) {
            model.ui.hasChooseFilter(position);
        }
    }


    /**
     * description ：更新uiModel,提示需要重新渲染bitmap
     * date: ：2019/11/4 10:31
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public void setHasBg(String path) {
        for (int i = 0; i < mAssets.size(); i++) {
            AssetModel model = mAssets.get(i);
            if (model.type == AssetModel.TYPE_MEDIA) { //如果类型是meidiaUiModel
                model.ui.hasChooseBg(path);
            }
        }
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
                        (assetModel.ui).isShow(true);
                    }
                    if (albumType.isImage(mimeType)) {
                        ((MediaUiModel) assetModel.ui).setImageAsset(paths.get(i), context);
                    } else if (albumType.isVideo(mimeType)) {
                        String VideoPathOrigin = paths.get(i);
                        allVideoNumber++;
                        final MediaUiModel mModel = (MediaUiModel) assetModel.ui;
                        mModel.setVideoPathOrigin(VideoPathOrigin);//设置视频的源文件地址
                        mModel.isVideoSlide = true;
                        mModel.setVideoPath(VideoPathOrigin);
                    }
                }
            }
            if (i == paths.size() - 1) {
                firstReplaceComplete.isComplete(true);
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


    public SourceVideoWH getSourceVideoWH(String path) {
        SourceVideoWH sourceVideoWH = new SourceVideoWH();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();//实例化MediaMetadataRetriever对象
        File file = new File(path);//实例化File对象，文件路径为/storage/sdcard/Movies/music1.mp4
        if (file.exists() && file.length() > 0) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(new File(path).getAbsolutePath());
                mmr.setDataSource(inputStream.getFD());//设置数据源为该文件对象指定的绝对路径
                int videoRotation = 0;
                try {
                    videoRotation = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
                    String width = (videoRotation == 90 || videoRotation == 270) ? mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT) : mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                    String height = (videoRotation == 90 || videoRotation == 270) ? mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH) : mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                    if (width != null && !width.equals("")) {
                        int sourceVideoWidth = Integer.parseInt(width);
                        sourceVideoWH.setWidth(sourceVideoWidth);
                    }
                    if (height != null && !height.equals("")) {
                        int sourceVideoHeight = Integer.parseInt(height);
                        sourceVideoWH.setHeight(sourceVideoHeight);
                    }
                } catch (Exception e) {
                    Log.d("Exception", e.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        return sourceVideoWH;
    }


    /**
     * true 表示没旋转，else 表示旋转了
     */
    public boolean getSourceVideoDirection(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();//实例化MediaMetadataRetriever对象
        File file = new File(path);//实例化File对象，文件路径为/storage/sdcard/Movies/music1.mp4
        if (file.exists() && file.length() > 0) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(new File(path).getAbsolutePath());
                mmr.setDataSource(inputStream.getFD());//设置数据源为该文件对象指定的绝对路径
                int videoRotation = 0;
                try {
                    videoRotation = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
                    return videoRotation != 90 && videoRotation != 270;

                } catch (Exception e) {
                    Log.d("Exception", e.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return true;
    }


    /**
     * description ：单一裁剪，需要Matrix等
     * 替换用户修改了视频的地址
     * 比较耗性能
     * date: ：2019/5/14 14:37
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    private int changeVideoNumber;
    private int isReadyChangeVideo;
    //是否重新截取了视频，如果截取了视频且有滤镜的情况，那么滤镜需要重新生成
    public boolean hasReplaceVideo;
    private boolean isReplaceMaterial = false;
    private float needSlowTime;

    public void setReplaceUpdateVideoFiles(final Context context, final allVideoCutAchieveListenner videoCutAchieveListenner, final String tag, boolean isPreview) {  //批量选择图片,视频的替换方法
        changeVideoNumber = 0;
        isReadyChangeVideo = 0;
        hasReplaceVideo = false;
        isReplaceMaterial = false;
        ExecutorService executorService = Executors.newFixedThreadPool(getMaxSupportedInstances()); //5个线程池
        for (int i = 0; i < mReplaceableAssets.size(); i++) {
            if (mReplaceableAssets.get(i).ui instanceof MediaUiModel) {
                final MediaUiModel mUiModel = (MediaUiModel) mReplaceableAssets.get(i).ui;
                if (mUiModel.isVideoType()) {
                    if (mUiModel.isVideoSlide || mUiModel.isReplaceMaterial) {  //是否滑动，缩放等,有了替换，也需要裁剪到需要的尺寸，因为之前是用的源尺寸
                        hasReplaceVideo = true;
                        final videoCut_outModel cut_outModel = new videoCut_outModel(context);
                        changeVideoNumber++;
                        Log.d("OOM", "准备截取个数为" + i);
                        final int finalI = i;
                        Runnable syncRunnable = new Runnable() {
                            @Override
                            public void run() {
                                    cut_outModel.Cut_outVideo(mReplaceableAssets.get(finalI).ui.getSnapPath(context.getExternalCacheDir().getPath()), mUiModel.size.getWidth(), mUiModel.size.getHeight(), (float) mUiModel.getDuration() / fps, mUiModel.getmMatrix(), 0, fps, new videoCut_outModel.ShowCut_outPath() {
                                        @Override
                                        public void cutOutPath(String path, boolean success) {
                                            singleCropComplete(finalI, mUiModel, path, videoCutAchieveListenner, tag);
                                        }
                                    });
                            }
                        };
                        executorService.execute(syncRunnable);
                    }
                }

                if (mUiModel.isReplaceMaterial) { //完成有替换
                    isReplaceMaterial = true;
                    if (isPreview) {//保存的时候不修改替换状态,
                        mUiModel.isReplaceMaterial = false;
                    }
                }
            } else if (mReplaceableAssets.get(i).ui instanceof TextUiModel) {
                final TextUiModel model = (TextUiModel) mReplaceableAssets.get(i).ui;
                if (model.isTextChanged()) {
                    isReplaceMaterial = true;
                }
            }
            if (bgModel != null && ((MediaUiModel) bgModel.ui).isReplaceMaterial) {
                isReplaceMaterial = true;
            }
            if (!hasReplaceVideo && i == mReplaceableAssets.size() - 1) { //没有视频或者视频替换完成的时候，并且是最后一张的时候
                videoCutAchieveListenner.cutVideoProgress(100, true, false, tag, isReplaceMaterial, changeVideoNumber);
            }
        }
    }


    private void singleCropComplete(int finalI, MediaUiModel mUiModel, String path, allVideoCutAchieveListenner videoCutAchieveListenner, String tag) {
        Log.d("OOM", "截取完成" + finalI + "changeVideoNumber=" + changeVideoNumber + "isReadyChangeVideo=" + isReadyChangeVideo);
        isReadyChangeVideo++;
        mUiModel.isVideoSlide = false;
        mUiModel.setVideoPath(path);
        if (changeVideoNumber == isReadyChangeVideo) {
            videoCutAchieveListenner.cutVideoProgress(100, true, true, tag, isReplaceMaterial, changeVideoNumber);
        } else {
            videoCutAchieveListenner.cutVideoProgress(0, false, true, tag, isReplaceMaterial, changeVideoNumber);
        }
    }


    public void changeTemplate(Context context, int position, int lastPosition, String path) {
        if (lastPosition - position > 0) {
            for (int i = position; i < lastPosition; i++) {
                synchronized (this) {
                    changeData(i, i + 1, path, context);
                }
            }
        } else {
            for (int i = position; i > lastPosition; i--) { //递减
                synchronized (this) {
                    changeData(i, i - 1, path, context);
                }
            }
        }
    }


    private void changeData(int pos, int pos2, String path, Context context) {
        //交换图片/视频层
        MediaUiModel mUiModel = (MediaUiModel) mediaUIModelList.get(pos).ui;
        MediaUiModel mUiModel_2 = (MediaUiModel) mediaUIModelList.get(pos2).ui;
        for (int i = 0; i < mReplaceableAssets.size(); i++) {
            if (mReplaceableAssets.get(i).ui == mUiModel) {
                mUiModel = (MediaUiModel) mReplaceableAssets.get(i).ui;
            } else if (mReplaceableAssets.get(i).ui == mUiModel_2) {
                mUiModel_2 = (MediaUiModel) mReplaceableAssets.get(i).ui;
            }
        }
        mUiModel.isReplaceMaterial = true;
        mUiModel_2.isReplaceMaterial = true;
        String resourcePath = mUiModel.getOriginPathForThumb(path);
        String resourcePath2 = mUiModel_2.getOriginPathForThumb(path);
        String OriginPath = mUiModel.getOriginPath(path);
        String OriginPath2 = mUiModel_2.getOriginPath(path);
        boolean isVideoType2 = mUiModel_2.isVideoType();
        boolean isVideoType1 = mUiModel.isVideoType();
        if (resourcePath != null && !resourcePath.equals("")) {
            if (isVideoType1) {//是视频
                mUiModel_2.setVideoPath(resourcePath);
                mUiModel_2.setVideoPathOrigin(OriginPath);
            } else {
                mUiModel_2.setImageAsset(resourcePath, context);
            }
        }
        if (resourcePath2 != null && !resourcePath2.equals("")) {
            if (isVideoType2) {//是视频
                mUiModel.setVideoPath(resourcePath2);
                mUiModel.setVideoPathOrigin(OriginPath2);
            } else {
                mUiModel.setImageAsset(resourcePath2, context);
            }
        }

        if (textUIModelList != null && textUIModelList.size() > 0 && textUIModelList.get(pos) != null && textUIModelList.get(pos2) != null && textUIModelList.get(pos).groupId != textUIModelList.get(pos2).groupId) {  //如果交换中都有文字且来自不同层，那就交换，否则不交换
            TextUiModel text1 = (TextUiModel) textUIModelList.get(pos).ui;
            TextUiModel text2 = (TextUiModel) textUIModelList.get(pos2).ui;
            String textUiModel1 = text1.getText();
            String textUiModel2 = text2.getText();
            text1.setText(textUiModel2);
            text2.setText(textUiModel1);
        }


    }


    /**
     * description ：能同時裁剪的最大值
     * date: ：2019/7/5 17:37
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public int getMaxSupportedInstances() {
        MediaCodec codec = null;
        try {
            codec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            MediaCodecInfo.CodecCapabilities capabilities = codec.getCodecInfo().getCapabilitiesForType(MediaFormat.MIMETYPE_VIDEO_AVC);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                int maxSupportedInstances = capabilities.getMaxSupportedInstances();
                return maxSupportedInstances < 5 ? maxSupportedInstances : 5;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 5;
    }


    /**
     * description ：遍历数据确定是否有文字替换
     * date: ：2019/9/24 18:38
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */


    ArrayList<Integer> listForPosition = new ArrayList<>();

    public ArrayList<Integer> getHasTextPosition() {
        listForPosition.clear();
        for (int i = 0; i < mAssets.size(); i++) {
            if (mAssets.get(i).type == 2) {//是文字类
                listForPosition.add(mAssets.get(i).groupId);
            }
        }
        return listForPosition;


    }


    /**
     * description ：是否还包含占位图
     * date: ：2019/10/30 11:03
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public boolean hasPlaceholder() {
        for (int i = 0; i < mAssets.size(); i++) {
            AssetModel model = mAssets.get(i);
            if (model.type == AssetModel.TYPE_MEDIA) {
                if (model.ui.hasPlaceholder()) {
                    return true;
                }
            }
        }
        return false;
    }


}
