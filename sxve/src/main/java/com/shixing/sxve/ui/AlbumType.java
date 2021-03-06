package com.shixing.sxve.ui;

public class AlbumType {
    public static boolean isVideo(String mimeType) {
        if (mimeType == null) {
            return false;
        }
        return mimeType.startsWith("video");
    }

    public static boolean isImage(String mimeType) {
        if (mimeType == null) {
            return false;
        }
        return mimeType.startsWith("image");
    }

}
