package com.flyingeffects.com.ui.view.fragment;

import android.annotation.SuppressLint;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.music_recent_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.base.BaseFragment;
import com.flyingeffects.com.commonlyModel.DoubleClick;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.entity.ChooseMusic;
import com.flyingeffects.com.entity.FragmentHasSlide;
import com.flyingeffects.com.entity.RefeshCollectState;
import com.flyingeffects.com.entity.SelectMusicCollet;
import com.flyingeffects.com.entity.VideoInfo;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.ui.model.VideoManage;
import com.flyingeffects.com.ui.view.activity.ChooseMusicActivity;
import com.flyingeffects.com.ui.view.activity.LocalMusicTailorActivity;
import com.flyingeffects.com.ui.view.activity.UserHomepageActivity;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.TimeUtils;
import com.flyingeffects.com.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.flyingeffects.com.utils.BlogFileResource.FileManager.isLansongVESuppport;


/**
 * description ???????????????
 * creation date: 2020/8/26
 * user : zhangtongju
 */
public class RecentUpdateMusicFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.smart_refresh_layout_bj)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.lin_show_nodata_bj)
    LinearLayout lin_show_nodata_bj;

    private boolean isRefresh = true;
    private int selectPage = 1;
    private int perPageCount = 10;

    //0 ?????????????????? 1 ?????????????????? 2??????????????????
    private int id;

    private music_recent_adapter adapter;

    private List<ChooseMusic> listData = new ArrayList<>();

    private long needDuration;

    private int nowClickPosition;

    private boolean isFromShoot = false;
    private int isFrom = ChooseMusicActivity.IS_FROM_SHOOT;
    private String mCollectionTag;


    @Override
    protected int getContentLayout() {
        return R.layout.fag_recent_updates;
    }


    @Override
    protected void initView() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            id = bundle.getInt("id", 0);
            LogUtil.d("oom2", "id=" + id + "bundle != null");
            needDuration = bundle.getLong("needDuration");
            isFromShoot = bundle.getBoolean("isFromShoot");
            isFrom = bundle.getInt(ChooseMusicActivity.IS_FROM,0);
            bundle.getInt(ChooseMusicActivity.IS_FROM);
            LogUtil.d("oom2", "needDuration=" + needDuration);
        }
        if (isFrom == 0) {
            mCollectionTag = "12_shoot_music_use";
        } else {
            mCollectionTag = "12_mb_shoot_music_use";
        }

        initSmartRefreshLayout();
        initRecycler();
    }

    @Override
    protected void initAction() {
        if (id == 1) {
            startQuery();
        } else {
            LogUtil.d("OOM2", "????????????????????????");
            requestFagData();
        }
    }


    @Override
    protected void initData() {
    }


    private void requestFagData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", selectPage + "");
        params.put("pageSize", perPageCount + "");
        Observable ob;
        if (id == 0) {
            ob = Api.getDefault().musicList(BaseConstans.getRequestHead(params));
        } else {
            //2 ????????????
            ob = Api.getDefault().musicCollectionList(BaseConstans.getRequestHead(params));
        }
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<ChooseMusic>>(getActivity()) {
            @Override
            protected void onSubError(String message) {
                finishData();
                //   ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(List<ChooseMusic> data) {
                finishData();
                if (isRefresh) {
                    listData.clear();
                }
                if (isRefresh && data.size() == 0) {
                    showNoData(true);
                } else {
                    showNoData(false);
                }
                if (!isRefresh && data.size() < perPageCount) {
                    ToastUtil.showToast(getResources().getString(R.string.no_more_data));
                }
                if (data.size() < perPageCount) {
                    smartRefreshLayout.setEnableLoadMore(false);
                }
                listData.addAll(data);
                adapter.notifyDataSetChanged();

            }
        }, "fagBjItem", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, true);
    }


    private void finishData() {
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
    }


    public void initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            if (id != 1) {
                isRefresh = true;
                refreshLayout.setEnableLoadMore(true);
                selectPage = 1;
                requestFagData();
            } else {
                startQuery();
            }
        });
        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
            isRefresh = false;
            selectPage++;
            requestFagData();
        });
    }




    private void initRecycler() {
        adapter = new music_recent_adapter(R.layout.list_music_recent_item, listData, getActivity(), id);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (!DoubleClick.getInstance().isFastDoubleClick()) {
                nowClickPosition = position;
                switch (view.getId()) {
                    case R.id.tv_make:
                        if (!DoubleClick.getInstance().isFastDoubleLongClick(2000)) {
                            if (id == 1) {
                                StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), mCollectionTag, "????????????");
                            } else {
                                StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), mCollectionTag, "????????????-" + listData.get(position).getTitle());
                            }
                            Intent intent = new Intent(getActivity(), LocalMusicTailorActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("isFromShoot", isFromShoot);
                            intent.putExtra("title", listData.get(position).getTitle());
                            intent.putExtra("videoPath", listData.get(position).getAudio_url());
                            intent.putExtra("needDuration", needDuration);
                            intent.putExtra("isAudio", true);
                            startActivity(intent);
                        }
                        break;
                    case R.id.iv_collect:
                        //??????
                        clickCollect(listData.get(position).getId(), listData.get(position).getIs_collection(), listData.get(position).getTitle());
                        break;
                    case R.id.tv_user:
                        //?????????????????????
                        if (id != 1) {
                            Intent intentUserHome = new Intent(getActivity(), UserHomepageActivity.class);
                            intentUserHome.putExtra("toUserId", listData.get(position).getUser_id());
                            startActivity(intentUserHome);
                        }
                        break;
                    case R.id.iv_play_music:
                        //????????????
                        StatisticsEventAffair.getInstance().setFlag(getActivity(), "16_paly", listData.get(position).getTitle());
                        playMusic(listData.get(position).getAudio_url(), position);
                        break;
                    default:
                        break;
                }
            }
        });
        recyclerView.setAdapter(adapter);
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
                endTimer();
                if (lastPosition == position) {
                    ChooseMusic chooseMusic2 = listData.get(lastPosition);
                    chooseMusic2.setPlaying(false);
                    adapter.notifyItemChanged(lastPosition);
                    return;
                }
            }
            ChooseMusic chooseMusic = listData.get(position);
            chooseMusic.setPlaying(true);
            listData.set(position, chooseMusic);
            if (lastPosition != position) {
                ChooseMusic chooseMusic2 = listData.get(lastPosition);
                chooseMusic2.setPlaying(false);
                listData.set(lastPosition, chooseMusic2);
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
                    ChooseMusic chooseMusic2 = listData.get(lastPosition);
                    chooseMusic2.setPlaying(false);
                    listData.set(lastPosition, chooseMusic2);
                    adapter.notifyItemChanged(lastPosition);
                    endTimer();
                });
                startTimer(integer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private Timer mTimer;
    TimerTask mTimerTask;

    private void startTimer(long allDuration) {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (getActivity() != null && adapter != null) {
                    if (allDuration != 0) {
                        float position = mediaPlayer.getCurrentPosition() / (float) allDuration;
                        ChooseMusic chooseMusic2 = listData.get(lastPosition);
                        chooseMusic2.setPlayingTime(TimeUtils.timeParse(mediaPlayer.getCurrentPosition()));
                        chooseMusic2.setProgress((int) (position * 100));
                        Observable.just(lastPosition).observeOn(AndroidSchedulers.mainThread()).subscribe(integer -> adapter.notifyItemChanged(lastPosition));
                    }
                }
            }
        };
        mTimer.schedule(mTimerTask, 0, 10);
    }

    private void endTimer() {
        if (mTimer != null) {
            mTimer.purge();
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        if (listData != null && listData.size() != 0 && listData.size() >= lastPosition) {
            ChooseMusic chooseMusic2 = listData.get(lastPosition);
            chooseMusic2.setPlaying(false);
            listData.set(lastPosition, chooseMusic2);
            adapter.notifyItemChanged(lastPosition);
        }
        endTimer();
    }

    public void showNoData(boolean isShowNoData) {
        if (isShowNoData) {
            lin_show_nodata_bj.setVisibility(View.VISIBLE);
        } else {
            lin_show_nodata_bj.setVisibility(View.GONE);
        }
    }


    QueryHandler handler;

    private void startQuery() {
        if (getActivity() != null) {
            smartRefreshLayout.setEnableLoadMore(false);
            handler = new QueryHandler(getActivity().getContentResolver());
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.TITLE, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST,};
            handler.startQuery(0, null, uri, projection, null, null, null);
        }
    }

    // ????????????????????????
    @SuppressLint("HandlerLeak")
    public final class QueryHandler extends AsyncQueryHandler {
        public QueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor c) {
            super.onQueryComplete(token, cookie, c);
            listData.clear();
            if (c != null && c.getColumnCount() > 0) {
                while (c.moveToNext()) {
                    String path = c.getString(0);// ??????
                    if (!new File(path).exists()) {
                        continue;
                    }
                    if (!isLansongVESuppport(path)) {
                        continue;
                    }
                    try {
                        String name = c.getString(1);
                        String album = c.getString(2);
                        String artist = c.getString(3);
                        File file = new File(path);
                        if (file.exists()) {
                            ChooseMusic chooseMusic = new ChooseMusic();
                            chooseMusic.setAudio_url(path);
                            chooseMusic.setImage(album);
                            if (!TextUtils.isEmpty(artist)) {
                                chooseMusic.setNickname(artist);
                            } else {
                                chooseMusic.setNickname("?????????");
                            }
                            chooseMusic.setTitle(name);
                            listData.add(chooseMusic);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //??????
                Collections.reverse(listData);
                if (listData.size() == 0) {
                    showNoData(true);
                } else {
                    showNoData(false);
                }

                adapter.notifyDataSetChanged();
                finishData();
            }
        }
    }


    public void clickCollect(String music_id, int isCollect, String title) {
        HashMap<String, String> params = new HashMap<>();
        params.put("music_id", music_id);
        // ????????????
        Observable ob = Api.getDefault().collectMusic(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(getActivity()) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(Object data) {
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("OOM", "???????????????????????????" + str);
                updateCollect(isCollect, music_id, title);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    private void updateCollect(int oldIsCollect, String music_id, String title) {
        if (id == 2) {
            //????????????item
            listData.remove(nowClickPosition);
            adapter.notifyDataSetChanged();
            pauseMusic();
        } else {
            if (oldIsCollect == 0) {
                //??????
                if (!TextUtils.isEmpty(title)) {
                    StatisticsEventAffair.getInstance().setFlag(getActivity(), "16_pick music_keep", title);
                }
                oldIsCollect = 1;
            } else {
                //???????????????
                if (!TextUtils.isEmpty(title)) {
                    StatisticsEventAffair.getInstance().setFlag(getActivity(), "16_pick music_keep_cancel", title);
                }
                oldIsCollect = 0;
            }
            ChooseMusic chooseMusic = listData.get(nowClickPosition);
            chooseMusic.setIs_collection(oldIsCollect);
            listData.set(nowClickPosition, chooseMusic);
            adapter.notifyItemChanged(nowClickPosition);
        }

        if (!TextUtils.isEmpty(music_id) && id == 2) {
            EventBus.getDefault().post(new SelectMusicCollet(music_id));
        } else {
            EventBus.getDefault().post(new RefeshCollectState());
        }
    }


    @Subscribe
    public void onEventMainThread(FragmentHasSlide fragmentHasSlide) {
        pauseMusic();
        ChooseMusic chooseMusic2 = listData.get(lastPosition);
        chooseMusic2.setPlaying(false);
        listData.set(lastPosition, chooseMusic2);
        adapter.notifyItemChanged(lastPosition);
        endTimer();
    }


    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            endTimer();
        }
    }


    /**
     * description ?????????????????????
     * creation date: 2020/9/10
     * user : zhangtongju
     */
    @Subscribe
    public void onEventMainThread(SelectMusicCollet selectMusicCollet) {
        String musicId = selectMusicCollet.getMusic_id();
        if (id == 0) {
            for (int i = 0; i < listData.size(); i++) {
                String needId = listData.get(i).getId();
                if (needId.equals(musicId)) {
                    nowClickPosition = i;
                    updateCollect(listData.get(i).getIs_collection(), "", "");
                    return;
                }
            }
        } else if (id == 2) {
            refreshData();

        }

    }


    @Subscribe
    public void onEventMainThread(RefeshCollectState selectMusicCollet) {
        if (id == 2) {
            refreshData();
        }
    }


    private void refreshData() {
        isRefresh = true;
        selectPage = 1;
        requestFagData();
    }

}
