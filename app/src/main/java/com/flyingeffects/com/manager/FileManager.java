package com.flyingeffects.com.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import com.flyingeffects.com.utils.LogUtil;
import com.lansosdk.videoeditor.LanSongFileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileManager {


    public String getCachePath(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            //外部存储不可用
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    public String getFileCachePath(Context context, String fileName) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            if (context.getExternalFilesDir(fileName) != null) {
                cachePath = context.getExternalFilesDir(fileName).getPath();
            }
        } else {
            cachePath = context.getFilesDir().getPath();
        }
        return cachePath;
    }


    private static final String[] unsupportedSuffix = new String[]{"wmv", "3gp", "mov", "move", "flac", "mpg"};

    public static boolean isLansongVESuppport(String path) {
        String suffix = LanSongFileUtil.getFileSuffix(path.toLowerCase());
        for (String s : unsupportedSuffix) {
            if (s.equalsIgnoreCase(suffix)) {
                return false;
            }
        }
        return true;
    }


    public static List<String> getFilesAllName(String path) {
        //传入指定文件夹的路径
        File file = new File(path);
        File[] files = file.listFiles();
        List<String> imagePaths = new ArrayList<>();
        if (files != null) {
            for (File value : files) {
                if (checkIsImageFile(value.getPath())) {
                    imagePaths.add(value.getPath());
                }
            }
        }
        return imagePaths;
    }


    public static List<File> listFileSortByModifyTime(String path) {
        List<File> list = getFiles(path, new ArrayList<File>());
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newFile) {

                    String file1 = file.getName();
                    int dot = file1.lastIndexOf('.');
                    String name1 = file1.substring(0, dot);
                    int fileId = Integer.parseInt(name1);
                    String file2 = newFile.getName();
                    int dot2 = file2.lastIndexOf('.');
                    String name2 = file2.substring(0, dot2);
                    int fileId2 = Integer.parseInt(name2);

                    if (fileId < fileId2) {
                        return -1;
                    } else if (fileId == fileId2) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            });
        }
        return list;
    }

    /**
     * 获取目录下所有文件
     *
     * @param realpath
     * @param files
     * @return
     */
    public static List<File> getFiles(String realpath, List<File> files) {
        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    getFiles(file.getAbsolutePath(), files);
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }


    /**
     * 15  * 判断是否是照片
     * 16
     */
    public static boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        //获取拓展名
        String fileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (fileEnd.equals("jpg") || fileEnd.equals("png") || fileEnd.equals("gif")
                || fileEnd.equals("jpeg") || fileEnd.equals("bmp")) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }

    saveBitmapState callback;

    public static Bitmap saveBitmapToPath(Bitmap bitmap, String path, saveBitmapState callback) {
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
            if (callback != null) {
                callback.succeed(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            callback.succeed(false);

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


    public interface saveBitmapState {
        void succeed(boolean isSucceed);
    }

    /**
     * 复制图片
     *
     * @param fromFile 文件位置
     * @param toFile   保存的位置
     */
    public void mCopyFile(File fromFile, File toFile) {
        try {
            FileInputStream fosfrom = new FileInputStream(fromFile);
            FileOutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024 * 1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
        } catch (Exception e) {
            LogUtil.i("复制文件异常", e.toString());
        }
    }

    /**
     * 获取文件名及后缀
     */
    public String getFileNameWithSuffix(String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }
        int start = path.lastIndexOf("/");
        if (start != -1) {
            return path.substring(start + 1);
        } else {
            return "";
        }
    }


}
