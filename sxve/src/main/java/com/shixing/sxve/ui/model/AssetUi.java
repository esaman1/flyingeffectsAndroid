package com.shixing.sxve.ui.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.text.TextUtils;
import android.util.Log;

import com.shixing.sxve.ui.AssetDelegate;
import com.shixing.sxve.ui.util.Size;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class AssetUi {
    public final int group;
    public final int index;
    protected final AssetDelegate mDelegate;
    public final Size size;
    protected final Bitmap drawBitmapBj;
    protected  Bitmap BjForFilter;
    private Context context;


    public AssetUi(String folder, JSONObject ui, AssetDelegate delegate, Size size) throws JSONException {
        group = ui.getInt("group");
        index = ui.getInt("index");
        mDelegate = delegate;
        this.size = size;

        String file = ui.optString("f");
        if (!TextUtils.isEmpty(file)) {
            file = folder + "/ui/" + file;
            drawBitmapBj = BitmapFactory.decodeFile(file);
        } else {
            drawBitmapBj = null;
        }
    }

    int[] getIntArray(JSONArray array) throws JSONException {
        int[] ints = new int[array.length()];
        for (int i = 0; i < array.length(); i++) {
            ints[i] = array.getInt(i);
        }
        return ints;
    }

    float[] getFloatArray(JSONArray array) throws JSONException {
        float[] floats = new float[array.length()];
        for (int i = 0; i < array.length(); i++) {
            floats[i] = (float) array.getDouble(i);
        }
        return floats;
    }

    public void scroll(float distanceX, float distanceY) {
    }

    public abstract void draw(Canvas canvas, int activeLayer);

//    public abstract void drawThumbnail(Canvas canvas, int activeLayer);


    public abstract void isShow(boolean show);


    public void scale(float sx, float sy, float px, float py) {
    }

    public void rotate(float degrees, float px, float py) {
    }

    public abstract boolean isPointInside(PointF point);

    public abstract void singleTap(GroupModel groupModel);

    public abstract String getSnapPath(String folder);

    public abstract String getSnapPathForKeep(String folder);

    public abstract String getOriginPath(String folder);

    public abstract boolean hasPlaceholder();


    public abstract boolean hasChooseFilter(int filterPosition);


    public abstract boolean hasChooseBg(String path);



    public Bitmap saveBitmapToPath(Bitmap bitmap, String path, saveBitmapState state) {
        if (!path.endsWith(".png") && !path.endsWith(".PNG")) {
            throw new IllegalArgumentException();
        }

        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            boolean isSsuccees = bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            state.succeed(isSsuccees);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            state.succeed(false);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            state.succeed(false);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return bitmap;
    }


    interface saveBitmapState {
        void succeed(boolean isSucceed);
    }









}
