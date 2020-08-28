package com.flyingeffects.com.ui.view.fragment;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.music_local_adapter;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.commonlyModel.DoubleClick;
import com.flyingeffects.com.enity.BlogFile.Video;
import com.flyingeffects.com.utils.BlogFileResource.FileManager;
import com.flyingeffects.com.utils.LogUtil;
import com.lansosdk.videoeditor.MediaInfo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.flyingeffects.com.utils.BlogFileResource.FileManager.isLansongVESuppport;


/**
 * description ：选择本地音乐
 * creation date: 2020/8/26
 * user : zhangtongju
 */
public class frag_choose_music_local_music extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.smart_refresh_layout_bj)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.lin_show_nodata_bj)
    LinearLayout lin_show_nodata_bj;

    private boolean isRefresh = true;
    private int selectPage = 1;
    private int perPageCount = 10;

    private List<Video> listVideoFiltrateMp4 = new ArrayList<>();


    private music_local_adapter adapter;


    @Override
    protected int getContentLayout() {
        return R.layout.fag_local_music;
    }

    @Override
    protected void initView() {
//        initSmartRefreshLayout();
        initRecycler();
    }

    @Override
    protected void initAction() {
//        initVideo();
    }

    @Override
    protected void initData() {
        startQuery();
    }


    private void finishData() {
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }


//    public void initSmartRefreshLayout() {
//        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
//            isRefresh = true;
//            refreshLayout.setEnableLoadMore(true);
//            selectPage = 1;
//            requestFagData();
//        });
//        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
//            isRefresh = false;
//            selectPage++;
//            requestFagData();
//        });
//    }



    private void initRecycler() {
        adapter = new music_local_adapter(R.layout.list_music_local_item, listVideoFiltrateMp4, getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (!DoubleClick.getInstance().isFastDoubleClick()) {
                switch (view.getId()) {
                    case R.id.tv_make:
//                        Intent intent = new Intent(getActivity(), LocalMusicTailorActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.putExtra("videoPath", listData.get(position).getAudio_url());
//                        intent.putExtra("needDuration", needDuration);
//                        intent.putExtra("isAudio", true);
//                        startActivity(intent);
                        break;

                    default:
                        break;
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }


    private void initVideo(){
        rx.Observable.create((Observable.OnSubscribe<List<Video>>) subscriber -> {
            FileManager mInstance = FileManager.getInstance();
            List<Video> data = mInstance.getVideos();
            if (data != null && data.size() > 0) {
                Collections.sort(data, new Video());
                data = filtrateMp4(data);
                listVideoFiltrateMp4.addAll(data);
                subscriber.onNext(data);
            }else{
//                WatingDilog.closePragressDialog();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(videos ->
                        notifyVideoData()
                );

    }

    private void notifyVideoData() {
//        WatingDilog.closePragressDialog();
        adapter.notifyDataSetChanged();
    }



    private List<Video> filtrateMp4(List<Video> listVideo) {
        listVideoFiltrateMp4.clear();
        if (listVideo != null && listVideo.size() > 0) {
            for (int i = 0; i < listVideo.size(); i++) {
                String path=listVideo.get(i).getPath();
                File file =new File(path);
                if(file.exists()&&file.length()>0){
                    long longtime = listVideo.get(i).getDuration();
                    if (longtime < 5000000) {
                        listVideoFiltrateMp4.add(listVideo.get(i));
                    }
                }
            }
        }
        return listVideoFiltrateMp4;
    }



    private QueryHandler handler;
    private void startQuery() {
        handler.startQuery(0,null, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.MINI_THUMB_MAGIC,
        }, null, null, null);
    }

    // 写一个异步查询类
    private final class QueryHandler extends AsyncQueryHandler {
        public QueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor c) {
            super.onQueryComplete(token, cookie, c);
            // 更新mAdapter的Cursor
//            mAdapter.changeCursor(cursor);

            while (c.moveToNext()) {
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));// 路径
                LogUtil.d("getVideos", "系统筛选出来的视频：" + path);
                if (!new File(path).exists()) {
                    continue;
                }

                if (!isLansongVESuppport(path)) {
                    continue;
                }
                int id = c.getInt(c.getColumnIndexOrThrow(MediaStore.Video.Media._ID));// 视频的id
                String name = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)); // 视频名称
                String resolution = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION)); //分辨率
                long size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));// 大小
//                long duration = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));// 时长
                try {
                    File file = new File(path);
                    if (file.exists()) {
                        MediaInfo mediaInfo = new MediaInfo(path);
                        if (mediaInfo.prepare() && mediaInfo.isSupport()) {
                            long duration = Math.round(mediaInfo.vDuration * 1000);
                            long date = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED));//修改时间
                            if (duration >= 1000) {
                                Video video = new Video(id, path, name, resolution, size, date, duration);
                                listVideoFiltrateMp4.add(video);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            adapter.notifyDataSetChanged();
        }
    }


}
