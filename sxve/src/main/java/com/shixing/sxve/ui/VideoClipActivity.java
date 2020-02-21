package com.shixing.sxve.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.shixing.sxve.R;
import com.shixing.sxve.ui.adapter.TimelineAdapter;
import com.shixing.sxve.ui.view.SXProgressDialog;
import com.shixing.sxve.ui.view.SXVideoView;
import com.shixing.sxve.ui.view.VideoClipLayout;
import com.shixing.sxvideoengine.SXCompositor;
import com.shixing.sxvideoengine.SXRenderListener;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;


/**
 * description ：视频剪切界面
 * date: ：2019/5/13 9:41
 * author: 张同举 @邮箱 jutongzhang@sina.com
 */
public class VideoClipActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener, SXVideoView.OnGetSizeListener {
    private static final String TAG = "VideoClipActivity";
    private static final String KEY_TEMPLATE_WIDTH = "KEY_TEMPLATE_WIDTH";
    public static final String KEY_TEMPLATE_HEIGHT = "KEY_TEMPLATE_HEIGHT";
    public static final String KEY_TEMPLATE_DURATION = "KEY_TEMPLATE_DURATION";
    public static final String KEY_VIDEO_PATH = "KEY_VIDEO_PATH";
    public static final String CLICP_PATH = "path";

    private int mTemplateWidth;
    private int mTemplateHeight;
    private float mTemplateDuration;
    private CheckBox mCbMute;
    private SXVideoView mVideoView;
    private String mVideoPath;
    private TimelineAdapter mTimelineAdapter;
    private RecyclerView mThumbList;
    //private MediaMetadataRetriever mRetriever;
    private HashMap<Integer, Bitmap> mData;
    private int[] mTimeUs;
    private SXProgressDialog mDialog;

    private int mScrollX;
    private int mTotalWidth;
    private int mVideoDuration;
    private int mStartTime;

    public static void start(Activity activity, int templateWidth, int templateHeight, float duration, String videoPath, int requestCode) {
        Intent starter = new Intent(activity, VideoClipActivity.class);
        starter.putExtra(KEY_TEMPLATE_WIDTH, templateWidth);
        starter.putExtra(KEY_TEMPLATE_HEIGHT, templateHeight);
        starter.putExtra(KEY_TEMPLATE_DURATION, duration);
        starter.putExtra(KEY_VIDEO_PATH, videoPath);
        activity.startActivityForResult(starter, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_clip);

        parseIntent();
        //Environment.getExternalStorageDirectory() + "/abc.mp4";

        TextView duration = findViewById(R.id.duration);
        duration.setText(String.format(Locale.US, "%.1fs", mTemplateDuration));

        VideoClipLayout videoClipLayout = findViewById(R.id.sx_video_view);
        videoClipLayout.setTemplateWidthAndHeight(mTemplateWidth, mTemplateHeight);
        mVideoView = videoClipLayout.getVideoView();
        mVideoView.setVideoPath(mVideoPath);
        mVideoView.onGetViewSize(this);

        mCbMute = findViewById(R.id.cb_mute);
        mCbMute.setOnCheckedChangeListener(this);
        this.<RadioGroup>findViewById(R.id.radio_group).setOnCheckedChangeListener(this);

        initThumb();
        mDialog = new SXProgressDialog();
    }

    private void initThumb() {
        mThumbList = findViewById(R.id.list_thumb);
        mThumbList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mTimelineAdapter = new TimelineAdapter();
        mThumbList.setAdapter(mTimelineAdapter);
        mThumbList.setHasFixedSize(true);

        mThumbList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    float percent = (float) mScrollX / mTotalWidth;
                    mStartTime = (int) (mVideoDuration * percent);
                    mVideoView.seekTo(mStartTime);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mScrollX += dx;
            }
        });
    }

    private void parseIntent() {
        Intent intent = getIntent();
        mTemplateWidth = intent.getIntExtra(KEY_TEMPLATE_WIDTH, 0);
        mTemplateHeight = intent.getIntExtra(KEY_TEMPLATE_HEIGHT, 0);
        mTemplateDuration = intent.getFloatExtra(KEY_TEMPLATE_DURATION, 0);
        mVideoPath = intent.getStringExtra(KEY_VIDEO_PATH);
    }

    public void back(View view) {
        Toast.makeText(this, "back", Toast.LENGTH_SHORT).show();
    }


    /**
     * description ：下一步
     * date: ：2019/5/13 9:43
     * author: 张同举 @邮箱 jutongzhang@sina.com
     */
    public void next(View view) {
        mDialog.show(getSupportFragmentManager(), mDialog.getClass().getSimpleName());
        Matrix matrix = mVideoView.getMatrix();
        final String outputPath = getOutputPath();
        SXCompositor formatter = new SXCompositor(mVideoPath, outputPath, matrix, mVideoView.isMute());  //对视频进行截取 ztj
        formatter.setWidth(mTemplateWidth);
        formatter.setHeight(mTemplateHeight);
        formatter.setStartTime(mStartTime / 1000f);
        formatter.setDuration(mTemplateDuration);
        formatter.setRenderListener(new SXRenderListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onUpdate(int progress) {
                mDialog.setProgress(progress);
            }

            @Override
            public void onFinish(boolean success, String msg) {
                mDialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra(CLICP_PATH, outputPath);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onCancel() {

            }
        });
        formatter.start();
    }

    private String getOutputPath() {
        return getExternalCacheDir() + File.separator + UUID.randomUUID() + ".mp4";
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        mVideoView.setFitMod(checkedId == R.id.cb_fit_width ? SXVideoView.FIT_WIDTH : SXVideoView.FIT_HEIGHT);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mVideoView.setMute(isChecked);
    }

    @Override
    public void onGetVideoInfo(int width, int height, int duration) {
        mVideoDuration = duration;
        int listWidth = mThumbList.getWidth() - mThumbList.getPaddingLeft() - mThumbList.getPaddingRight();
        int listHeight = mThumbList.getHeight();

        float scale = (float) listHeight / height;
        int thumbWidth = (int) (scale * width);

        mTimelineAdapter.setBitmapSize(thumbWidth, listHeight);

        final int thumbCount = (int) (listWidth * ((float) duration / mTemplateDuration / 1000) / thumbWidth);
        final int interval = duration / thumbCount * 1000;

        mTimeUs = new int[thumbCount];
        for (int i = 0; i < thumbCount; i++) {
            mTimeUs[i] = i * interval;
        }
        mData = new HashMap<>();

        mTimelineAdapter.setVideoUri(Uri.fromFile(new File(mVideoPath)));
        mTimelineAdapter.setData(mTimeUs, mData);

        mTotalWidth = thumbWidth * thumbCount;
    }
}
