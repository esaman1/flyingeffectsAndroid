package com.flyingeffects.com.ui.view.fragment;

import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.TemplateGridViewAdapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.constans.UiStep;
import com.flyingeffects.com.enity.StickerList;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.DownloadZipManager;
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.ZipFileHelperManager;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shixing.sxve.ui.view.WaitingDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.flyingeffects.com.manager.FileManager.saveBitmapToPath;

/**
 * @author ZhouGang
 * @date 2020/12/7
 * 贴纸fragment
 */
public class StickerFragment extends BaseFragment {
    @BindView(R.id.smart_refresh_layout)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.gridView)
    GridView mGridView;

    List<StickerList> listForSticker = new ArrayList<>();
    TemplateGridViewAdapter mGridViewAdapter;
    int stickerType;


    /**
     * 0表示普通贴纸页面 1表示拍摄的贴纸页面
     */
    private int formToType;
    StickerListener mStickerListener;
    private int selectPage = 1;
    private int perPageCount = 20;
    private boolean isRefresh = true;
    private String mGifFolder;

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_sticker;
    }

    @Override
    protected void initView() {
        stickerType = getArguments().getInt("stickerType");
        formToType = getArguments().getInt("type");
        FileManager fileManager = new FileManager();
        mGifFolder = fileManager.getFileCachePath(getContext(), "gifFolder");
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected void initData() {
        mGridViewAdapter = new TemplateGridViewAdapter(listForSticker, getContext());
        mGridView.setAdapter(mGridViewAdapter);

        mSmartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            isRefresh = true;
            refreshLayout.setEnableLoadMore(true);
            selectPage = 1;
            requestStickersList(true);
        });
        mSmartRefreshLayout.setOnLoadMoreListener(refresh -> {
            isRefresh = false;
            selectPage++;
            requestStickersList(false);
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                modificationSingleItemIsChecked(position);
                if (mStickerListener != null) {
                    mStickerListener.clickItemSelected(position);
                }
                if (UiStep.isFromDownBj) {
                    statisticsEventAffair.getInstance().setFlag(getContext(), " 5_mb_bj_Sticker", listForSticker.get(position).getTitle());
                } else {
                    statisticsEventAffair.getInstance().setFlag(getContext(), " 6_customize_bj_Sticker", listForSticker.get(position).getTitle());
                }

                if (formToType == 1) {
                    //下载拍摄的bundle 数据,zip 文件
                    toDownZip(listForSticker.get(position).getFile_name(), listForSticker.get(position).getSourcefile());
                } else {
                    downSticker(listForSticker.get(position).getImage(), listForSticker.get(position).getId(), position, listForSticker.get(position).getTitle());
                }
            }
        });
    }


    /**
     * description ：
     * creation date: 2021/2/4
     * user : zhangtongju
     */

    private File mFolder;
    private int mProgress;
    private boolean isDownZipUrl = false;

    private void toDownZip(String name, String path) {
        LogUtil.d("OOM3", "开始下载-name=" + name + "path=" + path);
        File mFolder = getActivity().getExternalFilesDir("FUSticker/" + name);
        if (mFolder != null) {
            LogUtil.d("OOM3", "mFolder != null");
            String folderPath = mFolder.getParent();
            if (!isDownZipUrl) {
                if (mFolder == null || mFolder.list().length == 0) {
                    LogUtil.d("OOM3", "开始下载2");
                    downZip(path, folderPath,name);
                    mProgress = 0;
                    showMakeProgress();
                } else {
                    downZipCallback.zipPath(mFolder.getPath(),name);
//                    intoTemplateActivity(mFolder.getPath());
                }
            } else {
                ToastUtil.showToast("下载中，请稍后再试");
            }
        } else {
            ToastUtil.showToast("没找到sd卡");
        }

    }

    private void showMakeProgress() {
        downZipCallback.showDownProgress(mProgress);
    }


    private void downZip(String loadUrl, String path,String name) {
        mProgress = 0;
        if (!TextUtils.isEmpty(loadUrl)) {
            new Thread() {
                @Override
                public void run() {
                    isDownZipUrl = true;
                    try {
                        DownloadZipManager.getInstance().getFileFromServer(loadUrl, path, (progress, isSucceed, zipPath) -> {
                            if (!isSucceed) {
                                LogUtil.d("onVideoAdError", "progress=" + progress);
                                mProgress = progress;
                                showMakeProgress();
                            } else {
                                showMakeProgress();
                                LogUtil.d("onVideoAdError", "下载完成");
                                isDownZipUrl = false;
                                //可以制作了，先解压
                                File file = new File(zipPath);
                                try {
                                    ZipFileHelperManager.upZipFile(file, path, path1 -> {
                                        if (file.exists()) { //删除压缩包
                                            file.delete();
                                        }
//                                        videoPause();
                                        mProgress = 100;
                                        showMakeProgress();
                                        downZipCallback.zipPath(path1,name);
                                    });
                                } catch (IOException e) {
                                    LogUtil.d("onVideoAdError", "Exception=" + e.getMessage());
                                    e.printStackTrace();

                                }
                            }
                        });

                    } catch (Exception e) {
                        isDownZipUrl = false;
                        Observable.just(e).subscribeOn(AndroidSchedulers.mainThread()).subscribe(e1 -> new Handler().post(() -> ToastUtil.showToast("下载异常，请重试")));
                        LogUtil.d("OOM3", "Exception=" + e.getMessage());
                        downZipCallback.showDownProgress(100);
                    }
                    super.run();
                }
            }.start();
        } else {
            ToastUtil.showToast("没有zip地址");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        listForSticker.clear();
        isRefresh = true;
        selectPage = 1;
        requestStickersList(true);
    }

    public void requestStickersList(boolean isShowDialog) {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", selectPage + "");
        params.put("pageSize", perPageCount + "");
        params.put("category_id", String.valueOf(stickerType));
        // 启动时间
        Observable ob;
        if (formToType == 1) {
            ob = Api.getDefault().camerstickerList(BaseConstans.getRequestHead(params));
        } else {
            ob = Api.getDefault().getStickerslist(BaseConstans.getRequestHead(params));
        }
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<ArrayList<StickerList>>(getContext()) {
            @Override
            protected void _onError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void _onNext(ArrayList<StickerList> list) {
                finishData();
                if (!isRefresh && list.size() < perPageCount) {  //因为可能默认只请求8条数据
                    ToastUtil.showToast(getContext().getResources().getString(R.string.no_more_data));
                }
                if (list.size() < perPageCount) {
                    mSmartRefreshLayout.setEnableLoadMore(false);
                }

                listForSticker.addAll(list);
                modificationAllData(list);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, isShowDialog);
    }

    private void finishData() {
        mSmartRefreshLayout.finishRefresh();
        mSmartRefreshLayout.finishLoadMore();
    }

    private void modificationAllData(ArrayList<StickerList> list) {
        for (int i = 0; i < list.size(); i++) {
            String fileName = mGifFolder + File.separator + list.get(i).getId() + ".gif";
            File file = new File(fileName);
            if (file.exists()) {
                StickerList item1 = list.get(i);
                item1.setIsDownload(1);
                list.set(i, item1);
            }
        }
        mGridViewAdapter.notifyDataSetChanged();
    }

    /**
     * 下载帖子功能
     *
     * @param path     下载地址
     * @param imageId  gif 保存的图片id
     * @param position 当前点击的那个item ，主要用来更新数据
     */
    private void downSticker(String path, String imageId, int position, String title) {
        WaitingDialog.openPragressDialog(getContext());
        if (path.endsWith(".gif")) {
            String format = path.substring(path.length() - 4);
            String fileName = mGifFolder + File.separator + imageId + format;
            File file = new File(fileName);
            if (file.exists()) {
                //如果已经下载了，就用已经下载的，但是如果已经展示了，就不能复用，需要类似于复制功能，只针对gif
                String copyName = mGifFolder + File.separator + System.currentTimeMillis() + format;
                if (mStickerListener != null) {
                    mStickerListener.copyGif(fileName, copyName, title);
                }
                WaitingDialog.closePragressDialog();
                return;
            }
            Observable.just(path).map(s -> {
                File file1 = null;
                try {
                    file1 = Glide.with(getContext())
                            .load(path)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return file1;
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(path1 -> {
                try {
                    if (path1 != null) {
                        FileUtil.copyFile(path1, fileName);
                        if (mStickerListener != null) {
                            mStickerListener.addSticker(fileName, title);
                        }
                        WaitingDialog.closePragressDialog();
                        modificationSingleItem(position);
                    } else {
                        WaitingDialog.closePragressDialog();
                        ToastUtil.showToast("请重试");
                    }

                } catch (IOException e) {
                    WaitingDialog.closePragressDialog();
                    e.printStackTrace();
                }
            });

        } else {
            new Thread(() -> {
                Bitmap originalBitmap = null;
                FutureTarget<Bitmap> futureTarget =
                        Glide.with(BaseApplication.getInstance())
                                .asBitmap()
                                .load(path)
                                .submit();
                try {
                    originalBitmap = futureTarget.get();
                    Bitmap finalOriginalBitmap = originalBitmap;
                    Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
                        WaitingDialog.closePragressDialog();
                        String aa = path.substring(path.length() - 4);
                        String copyName = mGifFolder + File.separator + System.currentTimeMillis() + aa;
                        saveBitmapToPath(finalOriginalBitmap, copyName, isSucceed -> {
                            modificationSingleItem(position);
                            if (mStickerListener != null) {
                                mStickerListener.addSticker(copyName, title);
                            }
                        });
                    });
                } catch (Exception e) {
                    LogUtil.d("oom", e.getMessage());
                }
                Glide.with(BaseApplication.getInstance()).clear(futureTarget);
            }).start();
        }
    }

    private void modificationSingleItem(int position) {
        StickerList item1 = listForSticker.get(position);
        item1.setIsDownload(1);
        //修改对应的元素
        listForSticker.set(position, item1);
        mGridViewAdapter.notifyDataSetChanged();
    }

    private void modificationSingleItemIsChecked(int position) {
        for (StickerList item : listForSticker) {
            item.setChecked(false);
        }
        StickerList item1 = listForSticker.get(position);
        item1.setChecked(true);
        //修改对应的元素
        listForSticker.set(position, item1);
        mGridViewAdapter.notifyDataSetChanged();
    }

    public void setStickerListener(StickerListener stickerListener) {
        mStickerListener = stickerListener;
    }


    public void setFUStickerListener(DownZipCallback downZipCallback) {
        this.downZipCallback = downZipCallback;
    }


    DownZipCallback downZipCallback;

    public interface DownZipCallback {

        void showDownProgress(int progress);

        void zipPath(String path,String title);
    }


    public interface StickerListener {
        void addSticker(String stickerPath, String name);

        void copyGif(String fileName, String copyName, String title);

        void clickItemSelected(int position);
    }
}
