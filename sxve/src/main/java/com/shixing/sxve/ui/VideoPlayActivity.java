package com.shixing.sxve.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.shixing.sxve.R;

public class VideoPlayActivity extends AppCompatActivity {

    public static void start(Context context, String path) {
        Intent starter = new Intent(context, VideoPlayActivity.class);
        starter.putExtra("path", path);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SXVE);
        setContentView(R.layout.activity_preview);

        String path = getIntent().getStringExtra("path");
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
