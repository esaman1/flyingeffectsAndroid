package com.flyingeffects.com.manager;

import com.bumptech.glide.load.ImageHeaderParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ffmpegManage {


    /**
     * 视频抽帧转成图片
     *
     * @param inputFile  输入文件
     * @param startTime  开始时间
     * @param duration   持续时间
     * @param frameRate  帧率
     * @param targetFile 输出文件
     * @return 视频抽帧的命令行
     */
    public static String[] videoToImage(String inputFile, int startTime, int duration, int frameRate, String targetFile) {
        //-ss：开始时间，单位为秒
        //-t：持续时间，单位为秒
        //-r：帧率，每秒抽多少帧
        String toImage = "ffmpeg -i %s -ss %s -t %s -r %s %s";
        toImage = String.format(Locale.CHINESE, toImage, inputFile, startTime, duration, frameRate, targetFile);
        toImage = toImage + "%3d.jpg";
        return toImage.split(" ");
    }


    public static String[] pictureToVideo(String sourceFile, ImageHeaderParser.ImageType type, int frames, String targetFile) {
//        //-f image2：代表使用image2格式，需要放在输入文件前面
//        String combineVideo = "ffmpeg -f image2 -r 20 -i %s -vcodec mpeg4 -y %s";//"ffmpeg -f image2 -r 1 -i %simg#d.jpg -vcodec mpeg4 %s";
//        combineVideo = String.format(combineVideo, sourceFile, targetFile);
//        combineVideo = combineVideo.replace("#", "%");
//        return combineVideo.split(" ");//以空格分割为字符串数组
        List<String> command = new ArrayList<>();
        //线程数
        command.add("-threads");
        command.add("2");
        command.add("-y");
        command.add("-r");
        //帧率
        command.add(String.valueOf(frames));
        command.add("-i");
        //图片原目录
        command.add(String.valueOf(sourceFile));
        switch (type) {
            case PNG:
                command.add("-vcodec");
                command.add("png");
                break;
            case JPEG:
                break;
//            case OTHER:
//                break;
        }

        command.add("-y");
        //合成保存目录
        command.add(String.valueOf(targetFile));
        return command.toArray(new String[command.size()]);
    }


}
