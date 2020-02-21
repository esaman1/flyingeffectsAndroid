package com.mobile.flyingeffects.ui.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.mobile.flyingeffects.R;
import com.mobile.flyingeffects.adapter.TemplateThumbAdapter;
import com.mobile.flyingeffects.base.BaseActivity;
import com.mobile.flyingeffects.enity.TemplateThumbItem;
import com.mobile.flyingeffects.ui.interfaces.view.TemplateMvpView;
import com.mobile.flyingeffects.ui.presenter.TemplatePresenter;
import com.mobile.flyingeffects.utils.LogUtil;
import com.mobile.flyingeffects.utils.WatingDilog;
import com.shixing.sxve.ui.AssetDelegate;
import com.shixing.sxve.ui.SxveConstans;
import com.shixing.sxve.ui.model.GroupModel;
import com.shixing.sxve.ui.model.MediaUiModel;
import com.shixing.sxve.ui.model.TemplateModel;
import com.shixing.sxve.ui.model.TextUiModel;
import com.shixing.sxve.ui.view.TemplateView;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 模板页面
 */
public class TemplateActivity extends BaseActivity implements TemplateMvpView, AssetDelegate {

    private TemplatePresenter presenter;
    private List<String> imgPath = new ArrayList<>();
    private AsyncTask asyncTask;
    private TemplateModel mTemplateModel;
    private File mFolder;
    private TemplateThumbAdapter templateThumbAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<TemplateThumbItem> listItem = new ArrayList<>();
    private ArrayList<TemplateView> mTemplateViews;
    private int maxChooseNum=10;
    private int nowChooseIndex = 0;
    @BindView(R.id.edit_view_container)
    private FrameLayout mContainer;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;


    @Override
    protected int getLayoutId() {
        return R.layout.act_template_edit;
    }

    @Override
    protected void initView() {
        presenter = new TemplatePresenter(this, this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            imgPath = bundle.getStringArrayList("paths");
        }
        ((TextView) findViewById(R.id.tv_top_title)).setText("拖动素材位置");
        mFolder = getExternalFilesDir("dynamic/" + "test");
        File dir = getExternalFilesDir("");
        SxveConstans.default_bg_path = new File(dir, "default_bj.png").getPath();
        mTemplateViews = new ArrayList<>();

    }

    @Override
    protected void initAction() {
        asyncTask = new LoadTemplateTask(TemplateActivity.this).execute(mFolder.getPath());
    }

    @Override
    public void pickMedia(MediaUiModel model) {

    }

    @Override
    public void editText(TextUiModel model) {

    }


     class LoadTemplateTask extends AsyncTask<String, Void, TemplateModel> {
        private WeakReference<TemplateActivity> activityReference;

        // only retain a weak reference to the activity
        LoadTemplateTask(TemplateActivity context) {
            activityReference = new WeakReference<>(context);
        }


        @Override
        protected TemplateModel doInBackground(String... strings) {
            TemplateModel templateModel = null;
            try {
                templateModel = new TemplateModel(strings[0], activityReference.get(), activityReference.get()); //通过路径地址
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return templateModel;
        }


        @Override
        protected void onPostExecute(TemplateModel templateModel) {
            if (templateModel != null) {
                activityReference.get().mTemplateModel = templateModel;
                initTemplateThumb();

            }
        }
    }


    /**
     * description ：如果有背景，这里需要忽略最后一个值，因为最后一个模板是背景模板，用户是不能够操作的。
     * date: ：2019/11/28 14:10
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    private void initTemplateViews(TemplateModel templateModel) {
        templateThumbAdapter.setTemplateModel(templateModel);
        for (int i = 1; i <= templateModel.groupSize; i++) {
            if ( i == templateModel.groupSize) {
                continue;
            }
            TemplateView templateView = new TemplateView(TemplateActivity.this);
            templateView.setBackgroundColor(Color.BLACK);
            templateView.setVisibility(i == 1 ? View.VISIBLE : View.GONE);
            GroupModel groupModel = templateModel.groups.get(i);
            templateView.setAssetGroup(groupModel);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            params.gravity = Gravity.CENTER;
            mTemplateViews.add(templateView);
            mContainer.addView(templateView, params);
        }

        isFirstReplace(imgPath);
    }


    private void isFirstReplace(List<String> paths) {
        if ( mTemplateViews != null && mTemplateViews.size() > 0) {
            LogUtil.d(TAG, "isFirstReplace");
            List<String> list_all = new ArrayList<>();
            for (int i = 0; i < maxChooseNum; i++) {  //填满数据，为了缩略图
                if (paths.size() > i && !TextUtils.isEmpty(paths.get(i))) {
                    list_all.add(paths.get(i)); //前面的时path ，后面的为默认的path
                } else {
                    list_all.add(SxveConstans.default_bg_path);
                }
            }
            for (int i = 0; i < list_all.size(); i++) {  //合成底部缩略图
                TemplateThumbItem templateThumbItem = new TemplateThumbItem();
                templateThumbItem.setPathUrl(list_all.get(i));
                if (i == 0) {
                    templateThumbItem.setIsCheck(0);
                } else {
                    templateThumbItem.setIsCheck(1);
                }
                listItem.set(i, templateThumbItem);
            }
            templateThumbAdapter.notifyDataSetChanged();
                WatingDilog.openPragressDialog(this);
                new Thread(() -> {
                    mTemplateModel.setReplaceAllFiles(list_all, TemplateActivity.this, complete -> TemplateActivity.this.runOnUiThread(() -> {
                        WatingDilog.closePragressDialog();
                        selectGroup(0);
                        nowChooseIndex = 0;
                        templateThumbAdapter.notifyDataSetChanged();
                        if (mTemplateViews != null && mTemplateViews.size() > 0) {
                            mTemplateViews.get(nowChooseIndex).invalidate(); //提示重新绘制预览图
                        }
                    }), "FIRST_MEDIA");  //批量替换图片
                }).start();
        }
    }


    /**
     * description ：选择当前的点，里面有个mModel ，其中mModel 一定要保证是当前可见mediaModel 的mModel,否则会出现灰屏的情况
     * date: ：2019/11/28 13:58
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public void selectGroup(final int index) {
            if (mTemplateViews != null && mTemplateViews.size() > 0) {
                TemplateView nowChooseTemplateView = mTemplateViews.get(index);
                nowChooseTemplateView.setVisibility(View.VISIBLE);
//                    nowChooseTemplateView.isViewVisible(true);
                    nowChooseTemplateView.invalidate();
                rx.Observable.from(mTemplateViews).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(templateView -> {
                    LogUtil.d("OOM", "selectGroup");
                    if (templateView != nowChooseTemplateView && templateView.getVisibility() != View.GONE) {
                        templateView.setVisibility(View.GONE);
//                        templateView.isViewVisible(false);
                    }
                });
            }
    }


    public void initTemplateThumb() {
        //创建布局管理
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        //创建适配器
        templateThumbAdapter = new TemplateThumbAdapter(R.layout.item_group_thumb, listItem, TemplateActivity.this);
        //条目点击事件
        templateThumbAdapter.setOnItemClickListener((adapter, view, position) -> {
        });
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(templateThumbAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(templateThumbAdapter);
        if (recyclerView.getItemAnimator() != null) {
            recyclerView.getItemAnimator().setChangeDuration(0);
            recyclerView.getItemAnimator().setMoveDuration(0);
        }
        initTemplateViews(mTemplateModel);  //初始化templateView 等数据
    }


}
