package com.flyingeffects.com.manager;


//import org.apache.tools.zip.ZipEntry;
//import org.apache.tools.zip.ZipFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * zip文件解压工具类
 * Created by mufeng on 2017/3/13.
 */
public class ZipFileHelperManager {

    private static final int BUFFER_SIZE = 1024 * 1024;//1M Byte

    /**
     * 解压缩一个文件
     *
     * @param zipFile    压缩文件
     * @param folderPath 解压缩的目标目录
     * @return
     * @throws IOException 当解压缩过程出错时抛出
     */
    public static ArrayList<File> upZipFile(File zipFile, String folderPath, zipDoneListener listner) throws IOException {

        String name;
        ArrayList<File> fileList = new ArrayList<File>();
        File desDir = new File(folderPath);
        if (!desDir.exists()) {
            desDir.mkdirs();
        }
        ZipFile zf = new ZipFile(zipFile);
        name=zf.entries().nextElement().getName();
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            if (entry.isDirectory()) {
                continue;
            }
            InputStream is = zf.getInputStream(entry);
            String str = folderPath + File.separator + entry.getName();
            str = new String(str.getBytes("8859_1"), "UTF-8");
            File desFile = new File(str);
            if (!desFile.exists()) {
                File fileParentDir = desFile.getParentFile();
                if (!fileParentDir.exists()) {
                    fileParentDir.mkdirs();
                }
                desFile.createNewFile();
            }
            OutputStream os = new FileOutputStream(desFile);
            byte buffer[] = new byte[BUFFER_SIZE];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
            os.close();
            is.close();
            fileList.add(desFile);

        }
        listner.isFinish(desDir.getPath()+"/"+name);
        return fileList;
    }
//

    public interface zipDoneListener{

      void  isFinish(String path);

    }


//    /**
//     * 使用 org.apache.tools.zip.ZipFile 解压文件，它与 java 类库中的 java.util.zip.ZipFile
//     * 使用方式是一新的，只不过多了设置编码方式的 接口。
//     *
//     * 注，apache 没有提供 ZipInputStream 类，所以只能使用它提供的ZipFile 来读取压缩文件。
//     *
//     * @param archive
//     *            压缩包路径
//     * @param decompressDir
//     *            解压路径
//     */
//    public static void upZipFile(File archive, String decompressDir,zipDoneListener listner)
//            throws IOException, FileNotFoundException, ZipException {
//        BufferedInputStream bi;
//        ZipFile zf = new ZipFile(archive.getPath(), "GBK");// 支持中文
//        Enumeration e = zf.getEntries();
//        while (e.hasMoreElements()) {
//            ZipEntry ze2 = (ZipEntry) e.nextElement();
//            String entryName = ze2.getName();
//            String path = decompressDir + "/" + entryName;
//            if (ze2.isDirectory()) {
//                File decompressDirFile = new File(path);
//                if (!decompressDirFile.exists()) {
//                    decompressDirFile.mkdirs();
//                }
//            } else {
//                String fileDir = path.substring(0, path.lastIndexOf("/"));
//                File fileDirFile = new File(fileDir);
//                if (!fileDirFile.exists()) {
//                    fileDirFile.mkdirs();
//                }
//                BufferedOutputStream bos = new BufferedOutputStream(
//                        new FileOutputStream(decompressDir + "/" + entryName));
//                bi = new BufferedInputStream(zf.getInputStream(ze2));
//                byte[] readContent = new byte[1024];
//                int readCount = bi.read(readContent);
//                while (readCount != -1) {
//                    bos.write(readContent, 0, readCount);
//                    readCount = bi.read(readContent);
//                }
//                bos.close();
//            }
//        }
//        zf.close();
//        listner.isFinish(decompressDir);
//    }


}