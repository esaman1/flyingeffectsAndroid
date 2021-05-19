package com.imaginstudio.imagetools.pixellab;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import androidx.annotation.NonNull;

public class commonFuncs {
    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final int FORMAT_JPG = 2;
    public static final int FORMAT_PNG = 1;
    private static final int abLength = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".length();
    private static SecureRandom rnd = new SecureRandom();

    public static boolean equalBundles(Bundle one, Bundle two) {
        return one != null && two != null && one.size() == two.size() && one.toString().equals(two.toString());
    }

    public static int strToInt(String str, int defaultVal) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return defaultVal;
        }
    }

    public static void copyDirectory(File sourceLocation, File targetLocation) throws IOException {
        if (!sourceLocation.getAbsolutePath().equals(targetLocation.getAbsolutePath())) {
            if (!sourceLocation.isDirectory()) {
                File directory = targetLocation.getParentFile();
                if (directory == null || directory.exists() || directory.mkdirs()) {
                    InputStream in = new FileInputStream(sourceLocation);
                    OutputStream out = new FileOutputStream(targetLocation);
                    byte[] buf = new byte[1024];
                    while (true) {
                        int len = in.read(buf);
                        if (len > 0) {
                            out.write(buf, 0, len);
                        } else {
                            in.close();
                            out.close();
                            return;
                        }
                    }
                } else {
                    throw new IOException("Cannot create dir " + directory.getAbsolutePath());
                }
            } else if (targetLocation.exists() || targetLocation.mkdirs()) {
                String[] children = sourceLocation.list();
                for (String aChildren : children) {
                    copyDirectory(new File(sourceLocation, aChildren), new File(targetLocation, aChildren));
                }
            } else {
                throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
            }
        }
    }

    public static Rect fitTextInRect(String text, Paint pnt, Rect area) {
        Rect bounds = new Rect();
        pnt.setTextSize((float) area.height());
        pnt.getTextBounds(text, 0, text.length(), bounds);
        while (true) {
            if (bounds.width() <= area.width() && bounds.height() <= area.height()) {
                return bounds;
            }
            pnt.setTextSize(pnt.getTextSize() - 1.0f);
            pnt.getTextBounds(text, 0, text.length(), bounds);
        }
    }

    public static int ceilConvert(float val) {
        return (int) Math.ceil((double) val);
    }

    public static int floorConvert(float val) {
        return (int) Math.floor((double) val);
    }

    public static boolean checkEquality(float... args) {
        if (args.length == 0) {
            return false;
        }
        float first = args[0];
        for (int i = 1; i < args.length; i++) {
            if (args[i] != first) {
                return false;
            }
        }
        return true;
    }

    public static int stripAlpha(int color) {
        return Color.argb(255, Color.red(color), Color.green(color), Color.blue(color));
    }

    @NonNull
    public static String getFileNameNoExtension(String fileName) {
        StringBuilder name = new StringBuilder();
        int i = 0;
        while (i < fileName.length() && fileName.charAt(i) != '.') {
            try {
                name.append(fileName.charAt(i));
                i++;
            } catch (Exception e) {
                return "";
            }
        }
        return name.toString();
    }

    @NonNull
    public static String getFileExtension(String name) {
        try {
            int indexOfPoint = name.lastIndexOf(".");
            if (indexOfPoint <= 0 || indexOfPoint + 1 >= name.length()) {
                return "";
            }
            return name.substring(indexOfPoint + 1).toLowerCase();
        } catch (Exception e) {
            return "";
        }
    }

    @NonNull
    public static String getFileNameNoExtension(File file) {
        return getFileNameNoExtension(file.getName());
    }

    @NonNull
    public static String getFileExtension(File file) {
        return getFileExtension(file.getName());
    }

    public static void drawRoundedRectCustom(RectF rect, float rad, boolean left, boolean right, Paint pnt, Canvas canvas) {
        if (left || right) {
            canvas.drawRoundRect(rect, rad, rad, pnt);
            float dp = dpToPx(1);
            if (!left) {
                canvas.drawRect(rect.left - dp, rect.top, rect.left + rad, rect.bottom, pnt);
            }
            if (!right) {
                canvas.drawRect(rect.right - rad, rect.top, rect.right + dp, rect.bottom, pnt);
                return;
            }
            return;
        }
        canvas.drawRect(rect, pnt);
    }

    public static void saveFile(Bitmap b, File where, int format) {
        try {
            FileOutputStream fos = new FileOutputStream(where);
            if (format == 1) {
                b.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } else if (format == 2) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public static Bitmap loadBitmapFromAsset(Context context, String path, int inSample) throws IOException {
        InputStream fis = context.getAssets().open(path);
        BitmapFactory.Options opt = new BitmapFactory.Options();
        if (inSample >= 2) {
            opt.inSampleSize = inSample;
        }
        Bitmap b = BitmapFactory.decodeStream(fis, null, opt);
        fis.close();
        return b;
    }

    public static Bitmap loadBitmap(String path, int inSample) throws IOException {
        FileInputStream fis = new FileInputStream(new File(path));
        BitmapFactory.Options options = new BitmapFactory.Options();
        if (inSample >= 2) {
            options.inSampleSize = inSample;
        }
        Bitmap b = BitmapFactory.decodeStream(fis, null, options);
        fis.close();
        return b;
    }

    public static float dpToPx(int dp) {
        return ((float) dp) * Resources.getSystem().getDisplayMetrics().density;
    }

    public static float pxToDp(int px) {
        return ((float) px) / Resources.getSystem().getDisplayMetrics().density;
    }

    public static PointF getSelected(float fingerX, float fingerY, float rad, PointF ifNone, PointF... points) {
        for (PointF point : points) {
            if (dist(point, fingerX, fingerY) < rad) {
                return point;
            }
        }
        return ifNone;
    }

    public static float spToPx(int sp) {
        return TypedValue.applyDimension(2, (float) sp, Resources.getSystem().getDisplayMetrics());
    }

    public static int dpToPxInt(int dp) {
        return (int) dpToPx(dp);
    }

    static int[] getDimensionsFromFile(String file) {
        int width = 0;
        int height = 0;
        try {
            FileInputStream fis = new FileInputStream(new File(file));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(fis, null, options);
            width = options.outWidth;
            height = options.outHeight;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new int[]{width, height};
    }

    public static int[] getDimensionsFromAssets(String path, Context context) throws Exception {
        InputStream image_stream = context.getAssets().open(path);
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(image_stream, null, opt);
        return new int[]{opt.outWidth, opt.outHeight};
    }

    public static int[] getDimensionsFromPath(String path) throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        return new int[]{options.outWidth, options.outHeight};
    }

    public static int getInSampleFromPath(String path, Context context, int containerW, int containerH) throws Exception {
        int[] dims = getDimensionsFromPath(path);
        return getInSampleSize(dims[0], dims[1], containerW, containerH);
    }

    public static Bitmap getBitmapFromPath(String path, int inSample) throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSample;
        return BitmapFactory.decodeFile(path, options);
    }

    public static int getInSampleFromFile(String picName, int containerW, int containerH) throws IOException {
        FileInputStream fis = new FileInputStream(new File(picName));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(fis, null, options);
        int inSample = getInSampleSize(options.outWidth, options.outHeight, containerW, containerH);
        fis.close();
        return inSample;
    }

    public static String getRandomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".charAt(rnd.nextInt(abLength)));
        }
        return sb.toString();
    }

    public static int fromHex(String color) {
        return Color.parseColor(color);
    }

    public static String generateName(String prefix) {
        return prefix + getRandomString(4);
    }

    public static void growRectBounded(Rect in, Rect out, int padding) {
        in.left = Math.max(in.left - padding, out.left);
        in.top = Math.max(in.top - padding, out.top);
        in.right = Math.min(in.right + padding, out.right);
        in.bottom = Math.min(in.bottom + padding, out.bottom);
    }

    public static int NearestPowerOf2LessThan32(float val) {
        return (int) Math.min(Math.pow(2.0d, Math.floor(Math.log((double) val) / Math.log(2.0d))), 32.0d);
    }

    public static float imgContainerScaleFactor(int imgW, int imgH, int containerW, int containerH) {
        return Math.max(1.0f, Math.min(((float) imgW) / ((float) containerW), ((float) imgH) / ((float) containerH)));
    }

    public static int getInSampleSize(int imgW, int imgH, int containerW, int containerH) {
        return NearestPowerOf2LessThan32(imgContainerScaleFactor(imgW, imgH, containerW, containerH));
    }

    public static File getCacheFile(String fileName) {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/pixelLab/.cache");
        dir.mkdirs();
        try {
            new File(dir, ".nomedia").createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File returned = new File(dir, fileName);
        try {
            returned.createNewFile();
        } catch (IOException e2) {
        }
        return returned;
    }

    public static File getFileOnStorage(String fileName) {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        dir.mkdirs();
        File returned = new File(dir, fileName);
        try {
            returned.createNewFile();
        } catch (IOException e) {
        }
        return returned;
    }

    public static boolean isValidRect(Rect rect) {
        return rect != null && rect.left < rect.right && rect.top < rect.bottom;
    }

    public static boolean isValidRect(RectF rect) {
        return rect != null && rect.left < rect.right && rect.top < rect.bottom;
    }

    public static int getInSampleAssetFromFile(Context context, String imgPath, int containerW, int containerH) throws IOException {
        InputStream fis = context.getAssets().open(imgPath);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(fis, null, options);
        int inSample = getInSampleSize(options.outWidth, options.outHeight, containerW, containerH);
        fis.close();
        return inSample;
    }

    public static void putEmptyFileHere(File dir, String name) {
        if (dir != null && dir.exists() && dir.isDirectory()) {
            try {
                new File(dir, name).createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void putNoMediaHere(File dir) {
        if (dir != null && dir.isDirectory() && dir.exists()) {
            try {
                new File(dir, ".nomedia").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static JSONArray shuffleJsonArray(JSONArray array) throws JSONException {
        Random rnd2 = new Random();
        rnd2.setSeed(System.currentTimeMillis());
        for (int i = array.length() - 1; i >= 0; i--) {
            int j = rnd2.nextInt(i + 1);
            Object object = array.get(j);
            array.put(j, array.get(i));
            array.put(i, object);
        }
        return array;
    }

    public static int orientation(PointF p, PointF q, PointF r) {
        double val = (double) (((q.y - p.y) * (r.x - q.x)) - ((q.x - p.x) * (r.y - q.y)));
        if (val == 0.0d) {
            return 0;
        }
        return val > 0.0d ? 1 : 2;
    }

    public static boolean intersect(PointF p1, PointF q1, PointF p2, PointF q2) {
        return (orientation(p1, q1, p2) == orientation(p1, q1, q2) || orientation(p2, q2, p1) == orientation(p2, q2, q1)) ? false : true;
    }

    public static boolean compareStrings(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }
        return str1.equals(str2);
    }

    public static PointF pointAlongSegment(PointF startPoint, PointF endPoint, float p) {
        return pointAlongSegment(startPoint, endPoint, p, 0.0f);
    }

    public static boolean checkIfSidesClipped(Bitmap bmp) {
        if (!checkImage(bmp)) {
            return false;
        }
        for (int i = 0; i < bmp.getHeight(); i++) {
            if (Color.alpha(bmp.getPixel(0, i)) != 0) {
                return true;
            }
            if (Color.alpha(bmp.getPixel(bmp.getWidth() - 1, i)) != 0) {
                return true;
            }
        }
        return false;
    }

    public static PointF pointAlongSegment(PointF startPoint, PointF endPoint, float p, float addDist) {
        float dy;
        float addDist2 = Math.abs(addDist);
        PointF vector = new PointF(endPoint.x - startPoint.x, endPoint.y - startPoint.y);
        PointF result = new PointF(startPoint.x + (vector.x * p), startPoint.y + (vector.y * p));
        float dx = 0.0f;
        if (vector.x != 0.0f) {
            float alpha = vector.y / vector.x;
            dx = (float) (((double) addDist2) / Math.sqrt((double) (1.0f + (alpha * alpha))));
            dy = Math.abs(dx * alpha);
        } else {
            dy = addDist2;
        }
        result.offset(Math.signum(vector.x) * dx, Math.signum(vector.y) * dy);
        return result;
    }

    public static void translatePointAlongVector(PointF translatedPoint, PointF start, PointF end, float dist) {
        float dy;
        PointF vector = new PointF(end.x - start.x, end.y - start.y);
        float dist2 = Math.abs(dist);
        float dx = 0.0f;
        if (vector.x != 0.0f) {
            float alpha = vector.y / vector.x;
            dx = (float) (((double) dist2) / Math.sqrt((double) (1.0f + (alpha * alpha))));
            dy = Math.abs(dx * alpha);
        } else {
            dy = dist2;
        }
        translatedPoint.offset(Math.signum(vector.x) * dx, Math.signum(vector.y) * dy);
    }

    public static PointF rotateAroundDegrees(PointF pointToRotate, PointF centerPoint, float angleDeg) {
        return rotateAroundRadians(pointToRotate, centerPoint, Math.toRadians((double) angleDeg));
    }

    public static PointF rotateAroundRadians(PointF pointToRotate, PointF centerPoint, double angleRad) {
        PointF result = new PointF(pointToRotate.x, pointToRotate.y);
        result.offset(-centerPoint.x, -centerPoint.y);
        double cosTheta = Math.cos(angleRad);
        double sinTheta = Math.sin(angleRad);
        result.set((float) ((((double) result.x) * cosTheta) - (((double) result.y) * sinTheta)), (float) ((((double) result.x) * sinTheta) + (((double) result.y) * cosTheta)));
        result.offset(centerPoint.x, centerPoint.y);
        return result;
    }

    public static float dist(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((double) (((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2))));
    }

    public static float dist(PointF p1, PointF p2) {
        return (float) Math.sqrt((double) (((p1.x - p2.x) * (p1.x - p2.x)) + ((p1.y - p2.y) * (p1.y - p2.y))));
    }

    public static float dist(PointF p1, float x2, float y2) {
        return (float) Math.sqrt((double) (((p1.x - x2) * (p1.x - x2)) + ((p1.y - y2) * (p1.y - y2))));
    }

    public static void drawSquare(float xC, float yC, float rad, Paint pnt, Canvas canvas) {
        canvas.drawRect(xC - rad, yC - rad, xC + rad, yC + rad, pnt);
    }

    public static void saveDebugBitmap(Bitmap bitmap_cache, String chosenName) {
        FileNotFoundException e;
        try {
            bitmap_cache.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + chosenName)));
        } catch (FileNotFoundException e2) {
            e = e2;
            e.printStackTrace();
        }
    }

    public static RectF getRectFromCircle(float xC, float yC, float dist) {
        return new RectF(xC - (dist * 0.5f), yC - (dist * 0.5f), (dist * 0.5f) + xC, (0.5f * dist) + yC);
    }

    public static boolean isValidImage(Bitmap bmp) {
        return bmp != null && bmp.getWidth() > 0 && bmp.getHeight() > 0;
    }

    public static int min3Id(float a, float b, float c) {
        if (a >= b || a >= c) {
            return b < c ? 2 : 3;
        }
        return 1;
    }

    public static boolean checkImage(Bitmap image) {
        return image != null && !image.isRecycled() && image.getWidth() > 1 && image.getHeight() > 1;
    }

    public static int touchedRectSide(PointF finger, RectF rect, int rad) {
        RectF out = new RectF(rect);
        RectF in = new RectF(rect);
        out.inset((float) (rad * -2), (float) (rad * -2));
        in.inset((float) (rad * 2), (float) (rad * 2));
        if (in.contains(finger.x, finger.y)) {
            return -1;
        }
        if (finger.x >= out.left && finger.x <= in.left) {
            return 0;
        }
        if (finger.y >= out.top && finger.y <= in.top) {
            return 1;
        }
        if (finger.x < in.right || finger.x > out.right) {
            return (finger.y < in.bottom || finger.y > out.bottom) ? -2 : 3;
        }
        return 2;
    }

    public static boolean equalsRectF(RectF rect1, RectF rect2) {
        return rect1.left == rect2.left && rect1.top == rect2.top && rect1.right == rect2.right && rect1.bottom == rect2.bottom;
    }

    public static boolean zipFolder(String sourcePath, String toLocation) {
        try {
            File sourceFile = new File(sourcePath);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(toLocation)));
            File[] files = sourceFile.listFiles();
            for (File f : files) {
                if (!f.isDirectory()) {
                    byte[] data = new byte[2048];
                    BufferedInputStream origin = new BufferedInputStream(new FileInputStream(f.getAbsolutePath()), 2048);
                    out.putNextEntry(new ZipEntry(f.getName()));
                    while (true) {
                        int count = origin.read(data, 0, 2048);
                        if (count == -1) {
                            break;
                        }
                        out.write(data, 0, count);
                    }
                } else {
                    zipSubFolder(out, f, f.getParent().length());
                }
            }
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void zipSubFolder(ZipOutputStream out, File folder, int basePathLength) throws Exception {
        File[] fileList = folder.listFiles();
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte[] data = new byte[2048];
                String unmodifiedFilePath = file.getAbsolutePath();
                String relativePath = unmodifiedFilePath.substring(basePathLength);
                BufferedInputStream origin = new BufferedInputStream(new FileInputStream(unmodifiedFilePath), 2048);
                out.putNextEntry(new ZipEntry(relativePath));
                while (true) {
                    int count = origin.read(data, 0, 2048);
                    if (count == -1) {
                        break;
                    }
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }
    }

    public static boolean unzipFile(File zipFile, File targetDirectory) {
        try {
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
            byte[] buffer = new byte[2048];
            while (true) {
                ZipEntry ze = zis.getNextEntry();
                if (ze == null) {
                    return true;
                }
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if ((dir.isDirectory() || dir.mkdirs()) && !ze.isDirectory()) {
                    FileOutputStream fout = new FileOutputStream(file);
                    while (true) {
                        int count = zis.read(buffer);
                        if (count == -1) {
                            break;
                        }
                        fout.write(buffer, 0, count);
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static String suffixifyFileName(String name, String suffix) {
        String extension = getFileExtension(name);
        StringBuilder append = new StringBuilder().append(getFileNameNoExtension(name)).append(suffix);
        if (!extension.equals("")) {
            extension = "." + extension;
        }
        return append.append(extension).toString();
    }

    public static File returnAvailableFile(File dir, String name) {
        String suffix = "";
        int i = 0;
        while (new File(dir, suffixifyFileName(name, suffix)).exists()) {
            i++;
            suffix = String.valueOf(i);
        }
        return new File(dir, suffixifyFileName(name, suffix));
    }

    public static boolean isDarkColor(int colorIn) {
        return ((((float) Color.red(colorIn)) * 0.299f) + (((float) Color.green(colorIn)) * 0.587f)) + (((float) Color.blue(colorIn)) * 0.114f) < 186.0f;
    }

    public static int getContrastColor(int colorIn) {
        if (Color.alpha(colorIn) == 0) {
            colorIn = Color.rgb(0, 0, 0);
        }
        return ((((float) Color.red(colorIn)) * 0.299f) + (((float) Color.green(colorIn)) * 0.587f)) + (((float) Color.blue(colorIn)) * 0.114f) > 186.0f ? -12303292 : -1;
    }

    public static Bundle copyBundle(Bundle in) {
        if (in == null) {
            return null;
        }
        return new Bundle(in);
    }

    public static void unionRects(RectF r1, RectF r2) {
        if (r1 != null && r2 != null) {
            if (r2.width() != 0.0f || r2.height() != 0.0f) {
                if (r1.width() == 0.0f && r1.height() == 0.0f) {
                    r1.set(r2);
                } else {
                    r1.set(Math.min(r1.left, r2.left), Math.min(r1.top, r2.top), Math.max(r1.right, r2.right), Math.max(r1.bottom, r2.bottom));
                }
            }
        }
    }

    public static void computePathsBounds(RectF bounds, Path... paths) {
        RectF tmp = new RectF();
        for (Path p : paths) {
            p.computeBounds(tmp, false);
            unionRects(bounds, tmp);
        }
    }

    public static RectF arrToRectF(ArrayList<String> arr) {
        if (arr == null || arr.size() < 4) {
            return new RectF();
        }
        return new RectF(Float.parseFloat(arr.get(0)), Float.parseFloat(arr.get(1)), Float.parseFloat(arr.get(2)), Float.parseFloat(arr.get(3)));
    }

    public static ArrayList<String> rectFToArr(RectF area) {
        if (area == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(String.valueOf(area.left), String.valueOf(area.top), String.valueOf(area.right), String.valueOf(area.bottom)));
    }

    public static boolean stringToBool(String str) {
        return !str.isEmpty();
    }

    public static String boolToString(boolean bool) {
        return bool ? "1" : "";
    }

    public static Path.Direction stringToDirection(String str) {
        return str.isEmpty() ? Path.Direction.CW : Path.Direction.CCW;
    }

    public static String directionToString(Path.Direction dir) {
        return dir == Path.Direction.CW ? "" : "1";
    }

    public static Path invertPath(Path path, float left, float top, float right, float bottom) {
        return invertPath(new ArrayList(Collections.singletonList(path)), left, top, right, bottom);
    }

    public static Path invertPath(ArrayList<Path> paths, float left, float top, float right, float bottom) {
        Path result = new Path();
        result.setFillType(Path.FillType.EVEN_ODD);
        Iterator<Path> it = paths.iterator();
        while (it.hasNext()) {
            result.addPath(it.next());
        }
        result.addRect(left - ((float) 1), top - ((float) 1), ((float) 1) + right, ((float) 1) + bottom, Path.Direction.CCW);
        return result;
    }

    public static Rect absolutePortion(RectF portion, int width, int height) {
        return new Rect((int) ((portion.left / 100.0f) * ((float) width)), (int) ((portion.top / 100.0f) * ((float) height)), (int) ((portion.right / 100.0f) * ((float) width)), (int) ((portion.bottom / 100.0f) * ((float) height)));
    }

    public static void flipPortion(RectF portion, boolean h, boolean v) {
        portion.set(h ? 100.0f - portion.right : portion.left, v ? 100.0f - portion.bottom : portion.top, h ? 100.0f - portion.left : portion.right, v ? 100.0f - portion.top : portion.bottom);
    }

    public static Bitmap cropRotBitmap(Bitmap bitmap, RectF portion, int angle, boolean flipH, boolean flipV) {
        if (portion.left == 0.0f && portion.top == 0.0f && portion.right == 100.0f && portion.bottom == 100.0f && angle == 0 && !flipH && !flipV) {
            return bitmap;
        }
        boolean shouldFlip = (angle / 90) % 2 != 0;
        Rect absolutePortion = absolutePortion(portion, bitmap.getWidth(), bitmap.getHeight());
        Bitmap result = Bitmap.createBitmap(!shouldFlip ? absolutePortion.width() : absolutePortion.height(), shouldFlip ? absolutePortion.width() : absolutePortion.height(), bitmap.getConfig());
        RectF drawBounds = new RectF(0.0f, 0.0f, (float) result.getWidth(), (float) result.getHeight());
        Canvas canvas = new Canvas(result);
        Paint pnt = new Paint(1);
        pnt.setFilterBitmap(true);
        canvas.scale(flipH ? -1.0f : 1.0f, flipV ? -1.0f : 1.0f, drawBounds.centerX(), drawBounds.centerY());
        canvas.rotate((float) angle, drawBounds.centerX(), drawBounds.centerY());
        canvas.drawBitmap(bitmap, absolutePortion, rotateRect(drawBounds, angle), pnt);
        return result;
    }

    public static RectF rotateRect(RectF rect, int angle) {
        RectF rotatedRect = new RectF(rect);
        Matrix mat = new Matrix();
        mat.setRotate((float) angle, rect.centerX(), rect.centerY());
        mat.mapRect(rotatedRect);
        return rotatedRect;
    }

    public static int maxId(float... list) {
        if (list.length == 0) {
            return 0;
        }
        int id = 0;
        float max = list[0];
        for (int i = 0; i < list.length; i++) {
            if (list[i] > max) {
                max = list[i];
                id = i;
            }
        }
        return id;
    }

    public static boolean arrayContains(int[] arr, int val) {
        for (int i : arr) {
            if (i == val) {
                return true;
            }
        }
        return false;
    }

    public static PointF[] getPointOnCurve(PointF p1, PointF p2, PointF p3, PointF p4, float t) {
        PointF p12 = pointAlongSegment(p1, p2, t);
        PointF p23 = pointAlongSegment(p2, p3, t);
        PointF p34 = pointAlongSegment(p3, p4, t);
        PointF p123 = pointAlongSegment(p12, p23, t);
        PointF p234 = pointAlongSegment(p23, p34, t);
        return new PointF[]{p12, p34, p123, p234, pointAlongSegment(p123, p234, t)};
    }

    public static ArrayList<PointF> findExtremaCubicCurve(PointF p1, PointF p2, PointF p3, PointF p4) {
        ArrayList<PointF> result = new ArrayList<>();
        float[] derivativeRoots = solveQuadratic(((((-p1.x) + (p2.x * 3.0f)) - (p3.x * 3.0f)) + p4.x) * 3.0f, 6.0f * ((p1.x - (p2.x * 2.0f)) + p3.x), (p2.x - p1.x) * 3.0f);
        for (float derivativeRoot : derivativeRoots) {
            if (inRange(derivativeRoot, 0.0f, 1.0f, false)) {
                result.add(getPointOnCurve(p1, p2, p3, p4, derivativeRoot)[4]);
            }
        }
        float[] derivativeRoots2 = solveQuadratic(((((-p1.y) + (p2.y * 3.0f)) - (p3.y * 3.0f)) + p4.y) * 3.0f, 6.0f * ((p1.y - (p2.y * 2.0f)) + p3.y), (p2.y - p1.y) * 3.0f);
        for (float derivativeRoot2 : derivativeRoots2) {
            if (inRange(derivativeRoot2, 0.0f, 1.0f, false)) {
                result.add(getPointOnCurve(p1, p2, p3, p4, derivativeRoot2)[4]);
            }
        }
        return result;
    }

    public static float[] solveQuadratic(float a, float b, float c) {
        float d = (b * b) - ((4.0f * a) * c);
        if (a == 0.0f) {
            if (b == 0.0f) {
                return new float[0];
            }
            return new float[]{(-c) / b};
        } else if (d < 0.0f) {
            return new float[0];
        } else {
            if (d == 0.0f) {
                return new float[]{(-b) / (2.0f * a)};
            }
            return new float[]{(float) ((((double) (-b)) - Math.sqrt((double) d)) / ((double) (2.0f * a))), (float) ((((double) (-b)) + Math.sqrt((double) d)) / ((double) (2.0f * a)))};
        }
    }

    public static boolean inRange(float val, float lower, float upper, boolean inclusive) {
        return inclusive ? val >= lower && val <= upper : val > lower && val < upper;
    }

    public static int invertColor(int color) {
        return Color.argb(Color.alpha(color), 255 - Color.red(color), 255 - Color.green(color), 255 - Color.blue(color));
    }
}
