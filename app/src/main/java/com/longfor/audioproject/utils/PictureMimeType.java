package com.longfor.audioproject.utils;

import android.content.Context;
import android.text.TextUtils;

import com.longfor.audioproject.R;

import java.io.File;

public final class PictureMimeType {
    private static final String MIME_TYPE_PNG = "image/png";
    public static final String MIME_TYPE_JPEG = "image/jpeg";
    private static final String MIME_TYPE_JPG = "image/jpg";
    private static final String MIME_TYPE_BMP = "image/bmp";
    private static final String MIME_TYPE_GIF = "image/gif";
    private static final String MIME_TYPE_WEBP = "image/webp";
    private static final String MIME_TYPE_3GP = "video/3gp";
    private static final String MIME_TYPE_MP4 = "video/mp4";
    private static final String MIME_TYPE_MPEG = "video/mpeg";
    private static final String MIME_TYPE_AVI = "video/avi";
    public static final String JPEG = ".jpeg";
    public static final String PNG = ".png";
    public static final String MP4 = ".mp4";
    public static final String JPEG_Q = "image/jpeg";
    public static final String PNG_Q = "image/png";
    public static final String MP4_Q = "video/mp4";
    public static final String AVI_Q = "video/avi";
    public static final String DCIM = "DCIM/Camera";
    public static final String CAMERA = "Camera";
    public static final String MIME_TYPE_IMAGE = "image/jpeg";
    public static final String MIME_TYPE_VIDEO = "video/mp4";
    public static final String MIME_TYPE_AUDIO = "audio/mpeg";
    private static final String MIME_TYPE_PREFIX_IMAGE = "image";
    private static final String MIME_TYPE_PREFIX_VIDEO = "video";
    private static final String MIME_TYPE_PREFIX_AUDIO = "audio";

    public PictureMimeType() {
    }

    public static int ofAll() {
        return 0;
    }

    public static int ofImage() {
        return 1;
    }

    public static int ofVideo() {
        return 2;
    }

    /** @deprecated */
    @Deprecated
    public static int ofAudio() {
        return 3;
    }

    public static String ofPNG() {
        return "image/png";
    }

    public static String ofJPEG() {
        return "image/jpeg";
    }

    public static String ofBMP() {
        return "image/bmp";
    }

    public static String ofGIF() {
        return "image/gif";
    }

    public static String ofWEBP() {
        return "image/webp";
    }

    public static String of3GP() {
        return "video/3gp";
    }

    public static String ofMP4() {
        return "video/mp4";
    }

    public static String ofMPEG() {
        return "video/mpeg";
    }

    public static String ofAVI() {
        return "video/avi";
    }

    public static boolean isGif(String mimeType) {
        return mimeType != null && (mimeType.equals("image/gif") || mimeType.equals("image/GIF"));
    }

    public static boolean isHasVideo(String mimeType) {
        return mimeType != null && mimeType.startsWith("video");
    }

    public static boolean isUrlHasVideo(String url) {
        return url.endsWith(".mp4");
    }

    public static boolean isHasAudio(String mimeType) {
        return mimeType != null && mimeType.startsWith("audio");
    }

    public static boolean isHasImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isJPEG(String mimeType) {
        if (TextUtils.isEmpty(mimeType)) {
            return false;
        } else {
            return mimeType.startsWith("image/jpeg") || mimeType.startsWith("image/jpg");
        }
    }

    public static boolean isJPG(String mimeType) {
        return TextUtils.isEmpty(mimeType) ? false : mimeType.startsWith("image/jpg");
    }

    public static boolean isHasHttp(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        } else {
            return path.startsWith("http") || path.startsWith("https") || path.startsWith("/http") || path.startsWith("/https");
        }
    }

    public static String getMimeType(int cameraMimeType) {
        switch(cameraMimeType) {
            case 2:
                return "video/mp4";
            case 3:
                return "audio/mpeg";
            default:
                return "image/jpeg";
        }
    }

    public static boolean isSuffixOfImage(String name) {
        return !TextUtils.isEmpty(name) && name.endsWith(".PNG") || name.endsWith(".png") || name.endsWith(".jpeg") || name.endsWith(".gif") || name.endsWith(".GIF") || name.endsWith(".jpg") || name.endsWith(".webp") || name.endsWith(".WEBP") || name.endsWith(".JPEG") || name.endsWith(".bmp");
    }

    public static boolean isMimeTypeSame(String oldMimeType, String newMimeType) {
        return getMimeType(oldMimeType) == getMimeType(newMimeType);
    }

    public static String getImageMimeType(String path) {
        try {
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                String fileName = file.getName();
                int beginIndex = fileName.lastIndexOf(".");
                String temp = beginIndex == -1 ? "jpeg" : fileName.substring(beginIndex + 1);
                return "image/" + temp;
            } else {
                return "image/jpeg";
            }
        } catch (Exception var5) {
            var5.printStackTrace();
            return "image/jpeg";
        }
    }

    public static int getMimeType(String mimeType) {
        if (TextUtils.isEmpty(mimeType)) {
            return 1;
        } else if (mimeType.startsWith("video")) {
            return 2;
        } else {
            return mimeType.startsWith("audio") ? 3 : 1;
        }
    }

    public static String getLastImgSuffix(String mineType) {
        String defaultSuffix = ".png";

        try {
            int index = mineType.lastIndexOf("/") + 1;
            return index > 0 ? "." + mineType.substring(index) : defaultSuffix;
        } catch (Exception var3) {
            var3.printStackTrace();
            return defaultSuffix;
        }
    }

    public static boolean isContent(String url) {
        return TextUtils.isEmpty(url) ? false : url.startsWith("content://");
    }

    public static String s(Context context, String mimeType) {
        Context ctx = context.getApplicationContext();
        if (isHasVideo(mimeType)) {
            return ctx.getString(R.string.picture_video_error);
        } else {
            return isHasAudio(mimeType) ? ctx.getString(R.string.picture_audio_error) : ctx.getString(R.string.picture_audio_error);
        }
    }
}
