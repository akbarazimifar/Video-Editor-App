package com.example.fullscreenvideocreator.WaveSong;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

public class FileUtils {
    private static File mSdCard = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
    public static File APP_DIRECTORY = new File(mSdCard, File.separator+"Full Screen Video Creator");
    public static final File TEMP_DIRECTORY = new File(APP_DIRECTORY, ".temp");
    public static final File TEMP_DIRECTORY_AUDIO = new File(APP_DIRECTORY, ".temp_audio");
    private static final File TEMP_VID_DIRECTORY = new File(TEMP_DIRECTORY, ".temp_vid");
    public static final File frameFile = new File(APP_DIRECTORY, ".frame.png");
    private static long mDeleteFileCount = 0;

    static {
        if (!TEMP_DIRECTORY.exists()) {
            TEMP_DIRECTORY.mkdirs();
        }
        if (!TEMP_VID_DIRECTORY.exists()) {
            TEMP_VID_DIRECTORY.mkdirs();
        }
    }

    private static final String FFMPEG_FILE_NAME = "ffmpeg";

    public static File getFFmpeg(Context context) {

        File folder = context.getFilesDir();
        return new File(folder, FFMPEG_FILE_NAME);
    }

    static boolean inputStreamToFile(InputStream stream, File file) {
        try {
            InputStream input = new BufferedInputStream(stream);
            OutputStream output = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                output.write(buffer, 0, bytesRead);
            }
            output.flush();
            output.close();
            input.close();
            return true;
        } catch (IOException e) {
            Log.e("error while writing", e.toString());
        }
        return false;
    }

    private static File getImageDirectory(String theme) {
        File imageDir = new File(TEMP_DIRECTORY, theme);
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
        return imageDir;
    }

    public static File getImageDirectory(String theme, int iNo) {
        File imageDir = new File(getImageDirectory(theme), String.format(Locale.getDefault(), "IMG_%03d", iNo));
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
        return imageDir;
    }

    public static boolean deleteThemeDir(String theme) {
        return deleteFile(getImageDirectory(theme));
    }

    public FileUtils() {
        mDeleteFileCount = 0;
    }


    public static void deleteTempDir() {
        for (final File child : TEMP_DIRECTORY.listFiles()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FileUtils.deleteFile(child);
                }
            }).start();
        }
    }

    public static boolean deleteFile(File mFile) {
        boolean idDelete = false;
        if (mFile == null) {
            return true;
        }
        if (mFile.exists()) {
            if (mFile.isDirectory()) {
                File[] children = mFile.listFiles();
                if (children != null && children.length > 0) {
                    for (File child : children) {
                        mDeleteFileCount += child.length();
                        idDelete = deleteFile(child);
                    }
                }
                mDeleteFileCount += mFile.length();
                idDelete = mFile.delete();
            } else {
                mDeleteFileCount += mFile.length();
                idDelete = mFile.delete();
            }
        }
        return idDelete;
    }

    @SuppressLint({"DefaultLocale"})
    public static String getDuration(long duration) {
        if (duration < 1000) {
            return String.format("%02d:%02d", 0, 0);
        }
        int hours = (int) (duration / (1000 * 60 * 60));
        int minutes = (int) (duration % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((duration % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hours == 0) {
            return String.format("%02d:%02d", minutes, seconds);
        }
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
