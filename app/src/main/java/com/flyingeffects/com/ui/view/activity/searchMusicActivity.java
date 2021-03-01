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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flyingeffects.com.R;
import com.flyingeffects.com.adapter.music_recent_adapter;
import com.flyingeffects.com.base.ActivityLifeCycleEvent;
import com.flyingeffects.com.base.BaseActivity;
import com.flyingeffects.com.base.BaseApplication;
import com.flyingeffects.com.commonlyModel.DoubleClick;
import com.flyingeffects.com.constans.BaseConstans;
import com.flyingeffects.com.enity.ChooseMusic;
import com.flyingeffects.com.enity.CutSuccess;
import com.flyingeffects.com.enity.DownVideoPath;
import com.flyingeffects.com.enity.SearchKeyWord;
import com.flyingeffects.com.enity.SelectMusicCollet;
import com.flyingeffects.com.http.Api;
import com.flyingeffects.com.http.HttpUtil;
import com.flyingeffects.com.http.ProgressSubscriber;
import com.flyingeffects.com.manager.ColorCorrectionManager;
import com.flyingeffects.com.manager.StatisticsEventAffair;
import com.flyingeffects.com.utils.LogUtil;
import com.flyingeffects.com.utils.StringUtil;
import com.flyingeffects.com.utils.ToastUtil;
import com.flyingeffects.com.view.WarpLinearLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import rx.Observable;


/**
 * description ：搜索页面，背景音乐搜索
 * creation date: 2020/9/2
 * user : zhangtongju
 */
public class searchMusicActivity extends BaseActivity {

    @BindView(R.id.smart_refresh_layout)
    SmartRefreshLayout smartRefreshLayout;

    private boolean isRefresh = true;
    private int selectPage = 1;
    private int perPageCount = 10;


    @BindView(R.id.RecyclerView)
    RecyclerView recyclerView;

    private int nowClickPosition;

    private music_recent_adapter adapter;

    private List<ChooseMusic> listData = new ArrayList<>();

    @BindView(R.id.ed_search)
    EditText ed_search;

    private long needDuration;

    private String searchText;

    @BindView(R.id.iv_back)
    ImageView iv_back;

    @BindView(R.id.AutoNewLineLayout)
    WarpLinearLayout autoNewLineLayout;

    private ArrayList<SearchKeyWord> listSearchKey = new ArrayList<>();
    private int isFrom;

    @Override
    protected int getLayoutId() {
        return R.layout.act_search_music;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        initSmartRefreshLayout();
        initRecycler();
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //键盘的搜索按钮
        ed_search.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) { //键盘的搜索按钮
                String nowShowText = ed_search.getText().toString().trim();
                if (!"".equals(nowShowText)) {
                    searchText = nowShowText;
                    requestFagData();
                }
                return true;
            }
            return false;
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
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
        HttpUtil.getInstance().toSubscribe(ob, new ProgressSubscriber<Object>(searchMusicActivity.this) {
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
//                        key.setColor(ob.getString("color"));
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
        autoNewLineLayout.removeAllViews();
        for (int i = 0; i < listSearchKey.size(); i++) {
            String nowChooseColor = ColorCorrectionManager.getInstance().getChooseColor(i);
            TextView tv = (TextView) LayoutInflater.from(searchMusicActivity.this).inflate(R.layout.textview_recommend, null);
            tv.setText(listSearchKey.get(i).getName());
            tv.setTextColor(Color.parseColor("#FFFFFF"));
            int finalI = i;
            tv.setOnClickListener(view -> {
                if (!com.flyingeffects.com.manager.DoubleClick.getInstance().isFastDoubleClick()) {
                    if (listSearchKey.size() >= finalI + 1) {
                        searchText = listSearchKey.get(finalI).getName();
                        ed_search.setText(searchText);
                        isRefresh = true;
                        selectPage = 1;
                        smartRefreshLayout.setEnableLoadMore(true);
                        requestFagData();
                        cancelFocus();
                    }
                }
            });
            GradientDrawable view_ground = (GradientDrawable) tv.getBackground(); //获取控件的背
            view_ground.setStroke(2, Color.parseColor(nowChooseColor));
            autoNewLineLayout.addView(tv);
        }

    }


    private void cancelFocus() {
        ed_search.setFocusable(true);
        ed_search.setFocusableInTouchMode(true);
        ed_search.requestFocus();
        ed_search.clearFocus();//失去焦点
    }

    @Override
    protected void initAction() {
        needDuration = getIntent().getLongExtra("needDuration", 10000);
        isFrom = getIntent().getIntExtra(ChooseMusicActivity.IS_FROM, ChooseMusicActivity.IS_FROM_OTHERS);
    }


    public void initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            if (!TextUtils.isEmpty(searchText)) {
                isRefresh = true;
                refreshLayout.setEnableLoadMore(true);
                selectPage = 1;
                requestFagData();
            } else {
                new Handler().postDelayed(() -> finishData(), 500);
            }
        });
        smartRefreshLayout.setOnLoadMoreListener(refresh -> {
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
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (!DoubleClick.getInstance().isFastDoubleClick()) {
                nowClickPosition = position;
                switch (view.getId()) {
                    case R.id.tv_make:
                        Intent intent = new Intent(this, LocalMusicTailorActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("videoPath", listData.get(position).getAudio_url());
                        intent.putExtra("needDuration", needDuration);
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
        recyclerView.setAdapter(adapter);
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

    public void clickCollect(String music_id, int isCollect) {
        HashMap<String, String> params = new HashMap<>();
        params.put("music_id", music_id);
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
                updateCollect(isCollect, music_id);
            }
        }, "cacheKey", ActivityLifeCycleEvent.DESTROY, lifecycleSubject, false, true, false);
    }


    private void updateCollect(int oldIsCollect, String music_id) {
        if (oldIsCollect == 0) {
            oldIsCollect = 1;
        } else {
            oldIsCollect = 0;
        }
        ChooseMusic chooseMusic = listData.get(nowClickPosition);
        chooseMusic.setIs_collection(oldIsCollect);
        listData.set(nowClickPosition, chooseMusic);
        adapter.notifyItemChanged(nowClickPosition);
        EventBus.getDefault().post(new SelectMusicCollet(music_id));
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
                    smartRefreshLayout.setEnableLoadMore(false);
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
        smartRefreshLayout.finishRefresh();
        smartRefreshLayout.finishLoadMore();
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
