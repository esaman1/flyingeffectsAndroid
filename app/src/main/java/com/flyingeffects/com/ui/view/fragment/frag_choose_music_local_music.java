package com.flyingeffects.com.ui.view.fragment;

import android.annotation.SuppressLint;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.music_local_adapter;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.commonlyModel.DoubleClick;
import com.flyingeffects.com.enity.BlogFile.Video;
import com.flyingeffects.com.enity.FragmentHasSlide;
import com.flyingeffects.com.enity.VideoInfo;
import com.flyingeffects.com.ui.model.VideoManage;
import com.flyingeffects.com.ui.view.activity.LocalMusicTailorActivity;
import com.flyingeffects.com.utils.LogUtil;
import com.lansosdk.videoeditor.MediaInfo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
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

    private List<Video> listVideoFiltrateMp4 = new ArrayList<>();


    private music_local_adapter adapter;

    private long needDuration;


    @Override
    protected int getContentLayout() {
        return R.layout.fag_local_music;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            needDuration = bundle.getLong("needDuration", 10000);
        }
        initRecycler();
    }

    @Override
    protected void initAction() {
    }

    @Override
    protected void initData() {
        startQuery();
    }


    private void finishData() {
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }


    public void initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            startQuery();
        });
        smartRefreshLayout.setEnableLoadMore(true);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        if(listVideoFiltrateMp4!=null&&listVideoFiltrateMp4.size()!=0&&listVideoFiltrateMp4.size()>=lastPosition){
            Video video3 = listVideoFiltrateMp4.get(lastPosition);
            video3.setPlaying(false);
            listVideoFiltrateMp4.set(lastPosition, video3);
            adapter.notifyItemChanged(lastPosition);
        }
    }

    private void initRecycler() {
        adapter = new music_local_adapter(R.layout.list_music_local_item, listVideoFiltrateMp4, getActivity());
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.
                        VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (!DoubleClick.getInstance().isFastDoubleClick()) {
                switch (view.getId()) {
                    case R.id.tv_make:
                        pauseMusic();
                        Intent intent = new Intent(getActivity(), LocalMusicTailorActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("videoPath", listVideoFiltrateMp4.get(position).getPath());
                        intent.putExtra("needDuration", needDuration);
                        intent.putExtra("isAudio", false);
                        startActivity(intent);
                        break;


                    case R.id.iv_play:
                        playMusic(listVideoFiltrateMp4.get(position).getPath(), position);
                        break;

                    default:
                        break;
                }
            }
        });


        recyclerView.setAdapter(adapter);
    }


    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Video video = listVideoFiltrateMp4.get(lastPosition);
            video.setPlaying(false);
            adapter.notifyItemChanged(lastPosition);
        }
    }


    private int lastPosition;
    private MediaPlayer mediaPlayer;

    private void playMusic(String path, int position) {
        Observable.just(path).map(s -> {
            VideoInfo videoInfo = VideoManage.getInstance().getVideoInfo(getActivity(), s);
            return videoInfo.getDuration();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                if (lastPosition == position) {
                    Video video = listVideoFiltrateMp4.get(lastPosition);
                    video.setPlaying(false);
                    adapter.notifyItemChanged(lastPosition);
                    return;
                }
            }
            Video video = listVideoFiltrateMp4.get(position);
            video.setPlaying(true);
            listVideoFiltrateMp4.set(position, video);
            if (lastPosition != position) {
                Video video2 = listVideoFiltrateMp4.get(lastPosition);
                video2.setPlaying(false);
                listVideoFiltrateMp4.set(lastPosition, video2);
            }
            adapter.notifyItemChanged(lastPosition);
            adapter.notifyItemChanged(position);
            lastPosition = position;
            try {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }
                mediaPlayer.reset();
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mediaPlayer1 -> {
                    Video video3 = listVideoFiltrateMp4.get(lastPosition);
                    video3.setPlaying(false);
                    listVideoFiltrateMp4.set(lastPosition, video3);
                    adapter.notifyItemChanged(lastPosition);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    QueryHandler handler;

    private void startQuery() {
        if (getActivity() != null) {
            handler = new QueryHandler(getActivity().getContentResolver());
            handler.startQuery(0, null, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.TITLE,
                    MediaStore.Video.Media.DURATION,
                    MediaStore.Video.Media.SIZE,
                    MediaStore.Video.Media.MINI_THUMB_MAGIC,
            }, null, null, null);
        }
    }

    // 写一个异步查询类
    @SuppressLint("HandlerLeak")
    public final class QueryHandler extends AsyncQueryHandler {
        public QueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor c) {
            super.onQueryComplete(token, cookie, c);
            listVideoFiltrateMp4.clear();
            Observable.just(c).map(c1 -> {
                if(c1 !=null&& c1.getColumnCount()>0) {
                    while (c1.moveToNext()) {
                        String path = c1.getString(c1.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));// 路径
                        LogUtil.d("getVideos", "系统筛选出来的视频：" + path);
                        if (!new File(path).exists()) {
                            continue;
                        }
                        if (!isLansongVESuppport(path)) {
                            continue;
                        }
                        int id = c1.getInt(c1.getColumnIndexOrThrow(MediaStore.Video.Media._ID));// 视频的id
                        try {
                            String name = c1.getString(c1.getColumnIndexOrThrow("title")); // 视频名称
                            long size = c1.getLong(c1.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));// 大小
                            File file = new File(path);
                            if (file.exists()) {
                                MediaInfo mediaInfo = new MediaInfo(path);
                                if (mediaInfo.prepare() && mediaInfo.isSupport()) {
                                    long duration = Math.round(mediaInfo.vDuration * 1000);
                                    long date = c1.getLong(c1.getColumnIndexOrThrow("_data"));//修改时间
                                    if (duration >= 1000) {
                                        Video video = new Video(id, path, name, "", size, date, duration);
                                        listVideoFiltrateMp4.add(video);
                                    }
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                Collections.reverse(listVideoFiltrateMp4);
                return listVideoFiltrateMp4;
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<Video>>() {
                @Override
                public void call(List<Video> videos) {
                    adapter.notifyDataSetChanged();
                    finishData();
                }
            });


//            if(c!=null&&c.getColumnCount()>0){
//                while (c.moveToNext()) {
//                    String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));// 路径
//                    LogUtil.d("getVideos", "系统筛选出来的视频：" + path);
//                    if (!new File(path).exists()) {
//                        continue;
//                    }
//                    if (!isLansongVESuppport(path)) {
//                        continue;
//                    }
//                    int id = c.getInt(c.getColumnIndexOrThrow(MediaStore.Video.Media._ID));// 视频的id
//                    try {
//                        String name = c.getString(c.getColumnIndexOrThrow("title")); // 视频名称
//                        long size = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));// 大小
//                        File file = new File(path);
//                        if (file.exists()) {
//                            MediaInfo mediaInfo = new MediaInfo(path);
//                            if (mediaInfo.prepare() && mediaInfo.isSupport()) {
//                                long duration = Math.round(mediaInfo.vDuration * 1000);
//                                long date = c.getLong(c.getColumnIndexOrThrow("_data"));//修改时间
//                                if (duration >= 1000) {
//                                    Video video = new Video(id, path, name, "", size, date, duration);
//                                    listVideoFiltrateMp4.add(video);
//                                }
//                            }
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                //倒序
//                Collections.reverse(listVideoFiltrateMp4);
//                adapter.notifyDataSetChanged();
//                finishData();
//            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    @Subscribe
    public void onEventMainThread(FragmentHasSlide fragmentHasSlide) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        Video video = listVideoFiltrateMp4.get(lastPosition);
        video.setPlaying(false);
        adapter.notifyItemChanged(lastPosition);
    }
}
