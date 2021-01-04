package com.flyingeffects.com.ui.view.fragment;

import android.graphics.Bitmap;
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
import com.flyingeffects.com.manager.FileManager;
import com.flyingeffects.com.manager.statisticsEventAffair;
import com.flyingeffects.com.utils.FileUtil;
import com.flyingeffects.com.utils.LogUtil;
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
                downSticker(listForSticker.get(position).getImage(), listForSticker.get(position).getId(), position,listForSticker.get(position).getTitle());
            }
        });
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
        Observable ob = Api.getDefault().getStickerslist(BaseConstans.getRequestHead(params));
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
    private void downSticker(String path, String imageId, int position,String title) {
        WaitingDialog.openPragressDialog(getContext());
        if (path.endsWith(".gif")) {
            String format = path.substring(path.length() - 4);
            String fileName = mGifFolder + File.separator + imageId + format;
            File file = new File(fileName);
            if (file.exists()) {
                //如果已经下载了，就用已经下载的，但是如果已经展示了，就不能复用，需要类似于复制功能，只针对gif
                String copyName = mGifFolder + File.separator + System.currentTimeMillis() + format;
                if (mStickerListener != null) {
                    mStickerListener.copyGif(fileName, copyName,title);
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
                            mStickerListener.addSticker(fileName,title);
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
                                mStickerListener.addSticker(copyName,title);
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

    public interface StickerListener {
        void addSticker(String stickerPath,String name);

        void copyGif(String fileName, String copyName,String title);

        void clickItemSelected(int position);
    }
}
