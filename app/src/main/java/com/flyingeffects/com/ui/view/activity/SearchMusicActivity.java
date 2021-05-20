package com.flyingeffects.com.ui.view.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.music_recent_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.commonlyModel.DoubleClick;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.databinding.ActSearchMusicBinding;
import com.flyingeffects.com.entity.ChooseMusic;
import com.flyingeffects.com.entity.CutSuccess;
import com.flyingeffects.com.entity.DownVideoPath;
import com.flyingeffects.com.entity.SearchKeyWord;
import com.flyingeffects.com.entity.SelectMusicCollet;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.ColorCorrectionManager;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;


/**
 * description ：搜索页面，背景音乐搜索
 * creation date: 2020/9/2
 * @author zhangtongju
 */
public class SearchMusicActivity extends BaseActivity {

    private int nowClickPosition;

    private music_recent_adapter adapter;

    private final List<ChooseMusic> listData = new ArrayList<>();

    private long needDuration;

    private String searchText;

    private final ArrayList<SearchKeyWord> listSearchKey = new ArrayList<>();
    private int isFrom;

    private boolean isFromShoot;
    private boolean isRefresh = true;
    private int selectPage = 1;
    private int perPageCount = 10;
    private ActSearchMusicBinding mBinding;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {
        mBinding = ActSearchMusicBinding.inflate(getLayoutInflater());
        View rootView = mBinding.getRoot();
        setContentView(rootView);

        EventBus.getDefault().register(this);

        initSmartRefreshLayout();
        initRecycler();
        mBinding.ivBack.setOnClickListener(view -> finish());

        //键盘的搜索按钮
        mBinding.edSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) { //键盘的搜索按钮
                String nowShowText = mBinding.edSearch.getText().toString().trim();
                if (!"".equals(nowShowText)) {
                    searchText = nowShowText;
                    requestFagData();
                }
                return true;
            }
            return false;
        });
        requestKeywordList();
    }

    /**
     * 请求友友推荐
     */
    private void requestKeywordList() {
        listSearchKey.clear();
        HashMap<String, String> params = new HashMap<>();
        // 启动时间
        Observable ob = Api.getDefault().musicKeyword(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(SearchMusicActivity.this) {
            @Override
            protected void onSubError(String message) {
            }

            @Override
            protected void onSubNext(Object data) {
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("OOM", "requestKeywordList=" + str);
                try {
                    JSONArray array = new JSONArray(str);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject ob = array.getJSONObject(i);
                        SearchKeyWord key = new SearchKeyWord();
                        key.setName(ob.getString("name"));
                        key.setID(ob.getString("id"));
                        key.setWeigh(ob.getString("weigh"));
                        key.setCreate_time(ob.getString("create_time"));
                        listSearchKey.add(key);
                    }
                    setKeyWordList(listSearchKey);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LogUtil.d("OOM", str);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    private void setKeyWordList(ArrayList<SearchKeyWord> listSearchKey) {
        mBinding.autoNewLineLayout.removeAllViews();
        for (int i = 0; i < listSearchKey.size(); i++) {
            String nowChooseColor = ColorCorrectionManager.getInstance().getChooseColor(i);
            TextView tv = (TextView) LayoutInflater.from(SearchMusicActivity.this).inflate(R.layout.textview_recommend, null);
            tv.setText(listSearchKey.get(i).getName());
            tv.setTextColor(Color.parseColor("#FFFFFF"));
            int finalI = i;
            tv.setOnClickListener(view -> {
                if (!com.flyingeffects.com.manager.DoubleClick.getInstance().isFastDoubleClick()) {
                    if (listSearchKey.size() >= finalI + 1) {
                        searchText = listSearchKey.get(finalI).getName();
                        mBinding.edSearch.setText(searchText);
                        isRefresh = true;
                        selectPage = 1;
                        mBinding.smartRefreshLayout.setEnableLoadMore(true);
                        requestFagData();
                        cancelFocus();
                    }
                }
            });
            //获取控件的背
            GradientDrawable viewGround = (GradientDrawable) tv.getBackground();
            viewGround.setStroke(2, Color.parseColor(nowChooseColor));
            mBinding.autoNewLineLayout.addView(tv);
        }

    }


    private void cancelFocus() {
        mBinding.edSearch.setFocusable(true);
        mBinding.edSearch.setFocusableInTouchMode(true);
        mBinding.edSearch.requestFocus();
        mBinding.edSearch.clearFocus();//失去焦点
    }

    @Override
    protected void initAction() {
        needDuration = getIntent().getLongExtra("needDuration", 10000);
        isFrom = getIntent().getIntExtra(ChooseMusicActivity.IS_FROM, ChooseMusicActivity.IS_FROM_OTHERS);
        isFromShoot = getIntent().getBooleanExtra("isFromShoot", false);
    }


    public void initSmartRefreshLayout() {
        mBinding.smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            if (!TextUtils.isEmpty(searchText)) {
                isRefresh = true;
                refreshLayout.setEnableLoadMore(true);
                selectPage = 1;
                requestFagData();
            } else {
                new Handler().postDelayed(() -> finishData(), 500);
            }
        });
        mBinding.smartRefreshLayout.setOnLoadMoreListener(refresh -> {
            if (!TextUtils.isEmpty(searchText)) {
                isRefresh = false;
                selectPage++;
                requestFagData();
            } else {
                new Handler().postDelayed(() -> finishData(), 500);
            }

        });
    }


    private void initRecycler() {
        adapter = new music_recent_adapter(R.layout.list_music_recent_item, listData, this, 3);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.rvSearchMusic.setLayoutManager(linearLayoutManager);
        mBinding.rvSearchMusic.setHasFixedSize(true);
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (!DoubleClick.getInstance().isFastDoubleClick()) {
                nowClickPosition = position;
                switch (view.getId()) {
                    case R.id.tv_make:
                        Intent intent = new Intent(this, LocalMusicTailorActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("videoPath", listData.get(position).getAudio_url());
                        intent.putExtra("needDuration", needDuration);
                        intent.putExtra("isFromShoot", isFromShoot);
                        intent.putExtra("title", listData.get(position).getTitle());
                        intent.putExtra("isAudio", true);
                        startActivity(intent);
                        break;
                    case R.id.iv_collect:
                        //收藏
                        clickCollect(listData.get(position).getId(), listData.get(position).getIs_collection());
                        break;
                    case R.id.iv_play_music:
                        //播放音乐
                        playMusic(listData.get(position).getAudio_url(), position);
                        break;
                    case R.id.tv_user:
                        Intent intentUserHome = new Intent(this, UserHomepageActivity.class);
                        intentUserHome.putExtra("toUserId", listData.get(position).getUser_id());
                        startActivity(intentUserHome);
                        break;
                    default:
                        break;
                }
            }
        });
        mBinding.rvSearchMusic.setAdapter(adapter);
    }

    private int lastPosition;
    private MediaPlayer mediaPlayer;

    private void playMusic(String path, int position) {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
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
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mediaPlayer1 -> {
                ChooseMusic chooseMusic2 = listData.get(lastPosition);
                chooseMusic2.setPlaying(false);
                listData.set(lastPosition, chooseMusic2);
                adapter.notifyItemChanged(lastPosition);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clickCollect(String musicId, int isCollect) {
        HashMap<String, String> params = new HashMap<>();
        params.put("music_id", musicId);
        // 启动时间
        Observable ob = Api.getDefault().collectMusic(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(this) {
            @Override
            protected void onSubError(String message) {
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(Object data) {
                String str = StringUtil.beanToJSONString(data);
                LogUtil.d("OOM", "收藏音乐返回的值为" + str);
                updateCollect(isCollect, musicId);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    private void updateCollect(int oldIsCollect, String musicId) {
        if (oldIsCollect == 0) {
            oldIsCollect = 1;
        } else {
            oldIsCollect = 0;
        }
        ChooseMusic chooseMusic = listData.get(nowClickPosition);
        chooseMusic.setIs_collection(oldIsCollect);
        listData.set(nowClickPosition, chooseMusic);
        adapter.notifyItemChanged(nowClickPosition);
        EventBus.getDefault().post(new SelectMusicCollet(musicId));
    }


    private void requestFagData() {
        lastPosition = 0;
        pauseMusic();
        if (isFrom == ChooseMusicActivity.IS_FROM_SHOOT) {
            StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "12_shoot_music_search", searchText);
        } else {
            StatisticsEventAffair.getInstance().setFlag(BaseApplication.getInstance(), "12_mb_shoot_music_search", searchText);
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("page", selectPage + "");
        params.put("pageSize", perPageCount + "");
        params.put("keyword", searchText);
        LogUtil.d("OOM", "searchText=" + params);
        Observable ob = Api.getDefault().musicList(BaseConstans.getRequestHead(params));
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<List<ChooseMusic>>(this) {
            @Override
            protected void onSubError(String message) {
                finishData();
                ToastUtil.showToast(message);
            }

            @Override
            protected void onSubNext(List<ChooseMusic> data) {
                finishData();
                String str = StringUtil.beanToJSONString(data);

                LogUtil.d("OOM2", str);

                if (isRefresh) {
                    listData.clear();
                }

                if (isRefresh && data.size() == 0) {
                    ToastUtil.showToast("没有找到相关内容");
                }

                if (!isRefresh && data.size() < perPageCount) {  //因为可能默认只请求8条数据
                    ToastUtil.showToast(getResources().getString(R.string.no_more_data));
                }

                if (data.size() < perPageCount) {
                    mBinding.smartRefreshLayout.setEnableLoadMore(false);
                }

                listData.addAll(data);
                adapter.notifyDataSetChanged();
            }
        }, "fagBjItem", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                ChooseMusic chooseMusic2 = listData.get(lastPosition);
                chooseMusic2.setPlaying(false);
                adapter.notifyItemChanged(lastPosition);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        EventBus.getDefault().unregister(this);
    }


    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void finishData() {
        mBinding.smartRefreshLayout.finishRefresh();
        mBinding.smartRefreshLayout.finishLoadMore();
    }

    @Subscribe
    public void onEventMainThread(DownVideoPath event) {
        LogUtil.d("OOM2", "销毁了onEventMainThread");
        this.finish();
    }

    @Subscribe
    public void onEventMainThread(CutSuccess cutSuccess) {
        this.finish();
    }

}
