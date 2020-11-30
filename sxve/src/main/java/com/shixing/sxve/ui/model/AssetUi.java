package com.shixing.sxve.ui.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.text.TextUtils;

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
    protected final Bitmap f;
    protected  Bitmap b;
    public boolean IsAnim=false;

    public AssetUi(String folder, JSONObject ui, AssetDelegate delegate, Size size) throws JSONException {
        group = ui.getInt("group");
        index = ui.getInt("index");
        mDelegate = delegate;
        this.size = size;

        String file = ui.optString("f");
        if (!TextUtils.isEmpty(file)) {
            file = folder + "/ui/" + file;
            f = BitmapFactory.decodeFile(file);
        } else {
            f = null;
        }

        String background = ui.optString("b");
        if (!TextUtils.isEmpty(background)) {
            background = folder + "/ui/" + background;
            b = BitmapFactory.decodeFile(background);
        } else {
            b = null;
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

    public abstract void draw(Canvas canvas, int activeLayer);

    public void scroll(float distanceX, float distanceY) {
    }

    public void scale(float sx, float sy, float px, float py) {
    }

    public void rotate(float degrees, float px, float py) {
    }

    public abstract boolean isPointInside(PointF point);

    public abstract void singleTap(GroupModel groupModel);

    public abstract String getOriginPath(String folder);

    public abstract String getPathOrigin();


    public abstract void hasChooseBg(String path,boolean isVideo);

    public abstract String getSnapPath(String folder);

    public Bitmap saveBitmapToPath(Bitmap bitmap, String path) {
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
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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

    public void  setIsAnim(boolean IsAnim){
        this.IsAnim=IsAnim;
    }

    public boolean  getIsAnim(){
        return  IsAnim;
    }





}
