package com.flyingeffects.com.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.VideoView;

import java.util.HashMap;

public class VideoUtils {
    /**
     * 播放背景视频
     */
    public static void startBgVideo(final VideoView customVideoView, Context context, String videoPath) {
        Bitmap bitmap = null;
        try {
            Uri uri = Uri.parse(videoPath);
            customVideoView.setVideoURI(uri);
            customVideoView.setVisibility(View.VISIBLE);
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            try {
                if (videoPath.startsWith("http://") || videoPath.startsWith("https://")) {
                    media.setDataSource(videoPath, new HashMap<String, String>());
                } else {
                    media.setDataSource(context, uri);
                }
                bitmap = media.getFrameAtTime(); //retriever.getFrameAtTime(-1);
                //bitmap = media.getFrameAtTime(0, MediaMetadataRetriever.OPTION_NEXT_SYNC);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != media) {
                        media.release();
                    }
                } catch (Exception ex) {
                    // Ignore failures while cleaning up.
                    ex.printStackTrace();
                }

            }
            Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
            customVideoView.setBackground(drawable);
            //播放
            customVideoView.start();
            //循环播放
            customVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    customVideoView.start();
                    mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                        @Override
                        public boolean onInfo(MediaPlayer mp, int what, int extra) {
                            return false;
                        }
                    });
                }
            });

            customVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //mp.setVolume(0f, 0f);
                    mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                        @Override
                        public boolean onInfo(MediaPlayer mp, int what, int extra) {
                            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                                customVideoView.setBackgroundColor(Color.TRANSPARENT);
                            }
                            return true;
                        }
                    });
                }
            });
            customVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != bitmap && !bitmap.isRecycled()) {
                    bitmap = null;
                }
            } catch (Exception e) {
                //L.w(Constant.EXCEPTIONTAG, "startBgVideo--Exception_e=" + e.toString());
                e.printStackTrace();
            }

        }

    }
}
