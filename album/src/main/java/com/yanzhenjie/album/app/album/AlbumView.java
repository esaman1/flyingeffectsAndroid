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

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.yanzhenjie.PhotoChooseIndex;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.AlbumFolder;
import com.yanzhenjie.album.R;
import com.yanzhenjie.album.api.widget.Widget;
import com.yanzhenjie.album.app.Contract;
import com.yanzhenjie.album.impl.DoubleClickWrapper;
import com.yanzhenjie.album.impl.DragSelectTouchListener;
import com.yanzhenjie.album.impl.OnCheckedClickListener;
import com.yanzhenjie.album.impl.OnItemClickListener;
import com.yanzhenjie.album.util.AlbumUtils;
import com.yanzhenjie.album.util.SystemBar;
import com.yanzhenjie.album.widget.ColorProgressBar;
import com.yanzhenjie.album.widget.divider.Api21ItemDivider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YanZhenjie on 2018/4/7.
 */
class AlbumView extends Contract.AlbumView implements View.OnClickListener {

    private Activity mActivity;

    private Toolbar mToolbar;
    private MenuItem mCompleteMenu;
    private LinearLayout mFlAdContainer;

    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private AlbumAdapter mAdapter;
    private TextView tv_show_alert;

    private Button mBtnPreview;
    private AppCompatTextView mTvSwitchFolder;
    private AppCompatTextView mTvCapture;

    private LinearLayout mLayoutLoading;
    private ColorProgressBar mProgressBar;
    private DragSelectTouchListener touchListener;

    private TabLayout mTabLayout;
    private AppCompatTextView mTvCount;
    private AppCompatImageView mIvBack;
    private AppCompatTextView mTvNext;

    public AlbumView(Activity activity, Contract.AlbumPresenter presenter, String material_info) {
        super(activity, presenter);
        this.mActivity = activity;
        this.mToolbar = activity.findViewById(R.id.toolbar);
        this.mRecyclerView = activity.findViewById(R.id.recycler_view);
        this.tv_show_alert = activity.findViewById(R.id.tv_show_alert);
        this.tv_show_alert.setText(material_info);
        this.mTvSwitchFolder = activity.findViewById(R.id.tv_switch_dir);
        this.mBtnPreview = activity.findViewById(R.id.btn_preview);
        this.mTvCapture = activity.findViewById(R.id.tv_capture);
        this.mFlAdContainer = activity.findViewById(R.id.fl_ad_container);

        this.mLayoutLoading = activity.findViewById(R.id.layout_loading);
        this.mProgressBar = activity.findViewById(R.id.progress_bar);
        this.mTabLayout = activity.findViewById(R.id.tl_index);
        this.mTvCount = activity.findViewById(R.id.tv_count);
        if(!TextUtils.isEmpty(material_info)&&material_info.equals("pictureAlbum")){
            this.mTvCount.setVisibility(View.INVISIBLE);
        }else{
            this.mTvCount.setVisibility(View.VISIBLE);
        }
        this.mIvBack = activity.findViewById(R.id.iv_back);
        this.mTvNext = activity.findViewById(R.id.tv_next);

        this.mToolbar.setOnClickListener(new DoubleClickWrapper(this));
        this.mTvSwitchFolder.setOnClickListener(this);
        this.mBtnPreview.setOnClickListener(this);
        this.mTvCapture.setOnClickListener(this);
        this.mIvBack.setOnClickListener(this);
        this.mTvNext.setOnClickListener(this);
        this.mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getPresenter().reLoadAlbumData(tab);
                ClearAllCountData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        getPresenter().returnAdContainer(mFlAdContainer);
    }

    @Override
    protected void onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.album_menu_album, menu);
        mCompleteMenu = menu.findItem(R.id.album_menu_finish);
    }

    private boolean singleCompletion = false;

    @Override
    protected void onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.album_menu_finish) {
            if (!isFastDoubleClick() && !singleCompletion) {
                singleCompletion = true;
//                long xx=item.get
                getPresenter().complete();
            }
        }
    }


    private long lastClickTime;

    public boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        Log.i("timeD", "timeD=" + timeD);
        if (0 < timeD && timeD < 3000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }


    @Override
    public void setupViews(Widget widget, int column, boolean hasCamera, int choiceMode) {
        SystemBar.setNavigationBarColor(mActivity, widget.getNavigationBarColor());

        int statusBarColor = widget.getStatusBarColor();
        if (widget.getUiStyle() == Widget.STYLE_LIGHT) {
            if (SystemBar.setStatusBarDarkFont(mActivity, true)) {
                SystemBar.setStatusBarColor(mActivity, statusBarColor);
            } else {
                SystemBar.setStatusBarColor(mActivity, getColor(R.color.albumColorPrimaryBlack));
            }

            mProgressBar.setColorFilter(getColor(R.color.albumLoadingDark));

            Drawable navigationIcon = getDrawable(R.drawable.album_ic_back_white);
            AlbumUtils.setDrawableTint(navigationIcon, getColor(R.color.albumIconDark));
            setHomeAsUpIndicator(navigationIcon);

            Drawable completeIcon = mCompleteMenu.getIcon();
            AlbumUtils.setDrawableTint(completeIcon, getColor(R.color.albumIconDark));
            mCompleteMenu.setIcon(completeIcon);
        } else {
            mProgressBar.setColorFilter(widget.getToolBarColor());
            SystemBar.setStatusBarColor(mActivity, statusBarColor);
            setHomeAsUpIndicator(R.drawable.album_ic_back_white);
        }
        mToolbar.setBackgroundColor(widget.getToolBarColor());

        Configuration config = mActivity.getResources().getConfiguration();
        mLayoutManager = new GridLayoutManager(getContext(), column, getOrientation(config), false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        int dividerSize = getResources().getDimensionPixelSize(R.dimen.album_dp_4);
        mRecyclerView.addItemDecoration(new Api21ItemDivider(Color.TRANSPARENT, dividerSize, dividerSize));
        mAdapter = new AlbumAdapter(getContext(), hasCamera, choiceMode, widget.getMediaItemCheckSelector());

        mAdapter.setLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("album", "长点击");
                int position = mRecyclerView.getChildAdapterPosition(v);
//                mAdapter.setSelected(position, true);
                View view = mLayoutManager.findViewByPosition(position);    //2为recyclerView中item位置，
                AppCompatCheckBox box = view.findViewById(R.id.check_box);
                if (!box.isChecked()) {
                    box.setChecked(true);
                } else {
                    box.setChecked(false);
                }
                getPresenter().tryCheckItem(box, position);
                touchListener.setStartSelectPosition(position);
                return true;
            }
        });


        mAdapter.setAddClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                getPresenter().clickCamera(view);
            }
        });
        mAdapter.setCheckedClickListener(new OnCheckedClickListener() {
            @Override
            public void onCheckedClick(CompoundButton button, int position) {
                getPresenter().tryCheckItem(button, position);
            }
        });
        mAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                getPresenter().tryPreviewItem(position);
            }
        });

        //ztj start
        touchListener = new DragSelectTouchListener();

        //监听滑动选择
        mRecyclerView.addOnItemTouchListener(touchListener);
        touchListener.setSelectListener(new DragSelectTouchListener.onSelectListener() {
            @Override
            public void onSelectChange(int start, int end, boolean isSelected) {
                if (isSelected) {
                    dataSelect(start, end);
                } else {
                    dataUnSelect(start, end);
                }
            }
        });

        //ztj end

        mRecyclerView.setAdapter(mAdapter);
    }


    private void dataSelect(int start, int end) {
        for (int i = start; i <= end; i++) {
            View view = mLayoutManager.findViewByPosition(i);
            if (view != null) {
                AppCompatCheckBox box = view.findViewById(R.id.check_box);
                if (box.isChecked()) {
                    box.setChecked(false);
                } else {
                    box.setChecked(true);
                }
                getPresenter().tryCheckItem(box, i);
            }
        }
    }

    private void dataUnSelect(int start, int end) {
        for (int i = start; i <= end; i++) {
            View view = mLayoutManager.findViewByPosition(i);
            if (view != null) {
                AppCompatCheckBox box = view.findViewById(R.id.check_box);
                if (!box.isChecked()) {
                    box.setChecked(true);
                } else {
                    box.setChecked(false);
                }
                getPresenter().tryCheckItem(box, i);
            }
        }
    }


    @Override
    public void setLoadingDisplay(boolean display) {
        mLayoutLoading.setVisibility(display ? View.VISIBLE : View.GONE);
    }


    @Override
    public void setShowCapture(boolean showCapture) {
        if (showCapture) {
            this.mTvCapture.setVisibility(View.VISIBLE);
        } else {
            this.mTvCapture.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setTab(String[] tabStr) {
        if (tabStr.length > 0) {
            for (String s : tabStr) {
                mTabLayout.addTab(mTabLayout.newTab().setText(s));
            }
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        int position = mLayoutManager.findFirstVisibleItemPosition();
        mLayoutManager.setOrientation(getOrientation(newConfig));
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager.scrollToPosition(position);
    }

    @RecyclerView.Orientation
    private int getOrientation(Configuration config) {
        switch (config.orientation) {
            case Configuration.ORIENTATION_PORTRAIT: {
                return LinearLayoutManager.VERTICAL;
            }
            case Configuration.ORIENTATION_LANDSCAPE: {
                return LinearLayoutManager.HORIZONTAL;
            }
            default: {
                throw new AssertionError("This should not be the case.");
            }
        }
    }

    @Override
    public void setCompleteDisplay(boolean display) {
        mCompleteMenu.setVisible(display);
        if (display) {
            mTvNext.setVisibility(View.VISIBLE);
        } else {
            mTvNext.setVisibility(View.INVISIBLE);
        }

    }



    /**
     * description ：相册扫描后的回调
     */
    @Override
    public void bindAlbumFolder(AlbumFolder albumFolder) {
        mTvSwitchFolder.setText(albumFolder.getName());
        ArrayList<AlbumFile> list = albumFolder.getAlbumFiles();
        if (list != null && list.size() > 0) {
            mAdapter.setAlbumFiles(list);
            mAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(0);
        }
    }

    @Override
    public void notifyInsertItem(int position) {
        mAdapter.notifyItemInserted(position);
    }

    @Override
    public void notifyItem(int position) {
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void setCheckedCountAndTotal(int count, int total) {
        mBtnPreview.setText(" (" + count + ")");
        mTvCount.setText(String.format("已选择 %d/%d", count, total));
        ArrayList<Integer>needShowNubList = PhotoChooseIndex.getInstance().GetPhotoIndexList();
        int lastPosition=PhotoChooseIndex.getInstance().GetLastIndex();
        List<AlbumFile>albumFiles= mAdapter.getAlbumFiles();
        if(lastPosition!=-1){
            //还原之前的
            AlbumFile albumFile=albumFiles.get(lastPosition);
            albumFile.setNowChooseIndex(-1);
            PhotoChooseIndex.getInstance().SetLastIndex();
        }
        if(albumFiles!=null&&albumFiles.size()>0){
            if(needShowNubList!=null&&needShowNubList.size()>0){
                for(int i=1;i<=needShowNubList.size();i++){
                    int needChoose=needShowNubList.get(i-1);
                    AlbumFile albumFile=albumFiles.get(needChoose);
                    albumFile.setNowChooseIndex(i);
                }
            }
        }

        mAdapter.notifyDataSetChanged();
    }



    /**
     * description ：清除全部标记
     * creation date: 2020/11/3
     * user : zhangtongju
     */
    public void ClearAllCountData(){
        ArrayList<Integer>needShowNubList = PhotoChooseIndex.getInstance().GetPhotoIndexList();
        List<AlbumFile>albumFiles= mAdapter.getAlbumFiles();
        if(albumFiles!=null&&albumFiles.size()>0){
            if(needShowNubList!=null&&needShowNubList.size()>0){
                for(int i=1;i<=needShowNubList.size();i++){
                    int needChoose=needShowNubList.get(i-1);
                    AlbumFile albumFile=albumFiles.get(needChoose);
                    albumFile.setNowChooseIndex(0);

                }
            }
        }
        mAdapter.notifyDataSetChanged();
        PhotoChooseIndex.getInstance().ClearAllData();
    }



    @Override
    public boolean isSingleCompletion() {
        return singleCompletion;
    }

    @Override
    public void setSingleCompletion(boolean singleCompletion) {
        this.singleCompletion = singleCompletion;
    }

    @Override
    public void onClick(View v) {
        if (v == mToolbar) {
            mRecyclerView.smoothScrollToPosition(0);
        } else if (v == mTvSwitchFolder) {
            getPresenter().clickFolderSwitch();
        } else if (v == mBtnPreview) {
            getPresenter().tryPreviewChecked();
        } else if (v == mTvCapture) {



            getPresenter().toCapturePage();
        } else if (v == mIvBack) {
            getPresenter().finishActivity();
        } else if (v == mTvNext) {
            getPresenter().complete();
        }
    }
}