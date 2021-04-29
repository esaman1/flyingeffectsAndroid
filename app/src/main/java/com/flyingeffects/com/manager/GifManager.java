package com.flyingeffects.com.manager;

import android.content.Context;
import android.os.AsyncTask;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.flyingeffects.com.ui.model.DressUpSpecialModel;
import com.flyingeffects.com.utils.LogUtil;

import java.io.File;

public class GifManager {


    Context mcontext;
    private  String gifFolder;
    private downGifCallback callback;
    public GifManager(Context mcontext ,downGifCallback callback){
        this.mcontext=mcontext;
        this.callback=callback;
        FileManager fileManager = new FileManager();
        gifFolder = fileManager.getFileCachePath(mcontext, "gifPath");
    }


    public void toDownGif(String path){
        SaveImageTask saveImageTask = new SaveImageTask(mcontext);
        saveImageTask.execute(path);
    }


    /**
     * Created by csonezp on 16-1-12.
     */
    public class SaveImageTask extends AsyncTask<String, Void, File> {
        private
        final Context context;

        public SaveImageTask(Context context) {
            this.context = context;
        }

        @Override
        protected File doInBackground(String... params) {
            String url = params[0]; // should be easy to extend to share multiple images at once
            try {
                return Glide
                        .with(context)
                        .load(url)
                        .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get() // needs to be called on background thread
                        ;
            } catch (Exception ex) {
                LogUtil.d("OOM3", "ex" + ex.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(File result) {
            if (result == null) {
                callback.downSuccess("");
                return;
            }
            callback.downSuccess(result.getPath());
        }
    }


   public interface downGifCallback{
        void downSuccess(String path);
    }



}
