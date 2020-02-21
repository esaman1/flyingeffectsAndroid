package com.shixing.sxve.ui;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.VideoView;

import com.shixing.sxve.R;
import com.shixing.sxvideoengine.SXPlayerSurfaceView;

public class VideoPlayActivity extends AppCompatActivity {

    public static void start(Context context, String path) {
        Intent starter = new Intent(context, VideoPlayActivity.class);
        starter.putExtra("path", path);
        context.startActivity(starter);
    }





    private void informAlbum(String outputFile){
        // 扫描本地mp4文件并添加到本地视频库
        MediaScannerConnection mMediaScanner = new MediaScannerConnection(this, null);
        mMediaScanner.connect();
        if ( mMediaScanner.isConnected()) {
            mMediaScanner.scanFile(outputFile, getVideoMimeType(outputFile));
        }
    }


    // 获取video的mine_type,暂时只支持mp4,3gp
    private static String getVideoMimeType(String path) {
        String lowerPath = path.toLowerCase();
        if (lowerPath.endsWith("mp4") || lowerPath.endsWith("mpeg4")) {
            return "video/mp4";
        } else if (lowerPath.endsWith("3gp")) {
            return "video/3gp";
        }
        return "video/mp4";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SXVE);
        setContentView(R.layout.activity_preview);

        String path = getIntent().getStringExtra("path");
        informAlbum(path);
        VideoView videoView = findViewById(R.id.video_view);

        final MediaController controller = new MediaController(this);
        videoView.setMediaController(controller);

        videoView.setVideoPath(path);
        videoView.start();
        videoView.post(new Runnable() {
            @Override
            public void run() {
                controller.show();
            }
        });
    }

    public void back(View view) {
        finish();
    }

}
