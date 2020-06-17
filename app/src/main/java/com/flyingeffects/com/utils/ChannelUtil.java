package com.flyingeffects.com.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by LV on 4月11日.
 */
public class ChannelUtil {

    private static String channel = null;

    public static String getChannel(Context context) {
        if (channel != null) {
            return channel;
        }

        final String start_flag = "META-INF/channel_";
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.contains(start_flag)) {
                    channel = entryName.replace(start_flag, "");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {

                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



        if (channel == null || channel.length() <= 0) {
            channel = "douyin-AD-A-3";
        }
        return channel;
//        return  "360";
    }
}
