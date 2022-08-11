package com.example.fullscreenvideocreator.WaveSong;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.Random;

public class SeekTest {
    private static final String PREF_SEEK_TEST_RESULT = "seek_test_result";
    private static final String PREF_SEEK_TEST_DATE = "seek_test_date";

    private static long before;
    private static long after;

    public static boolean CanSeekAccurately(SharedPreferences prefs) {
        Log.i("Ringdroid", "Running CanSeekAccurately");
        boolean result = false;

        result = prefs.getBoolean(PREF_SEEK_TEST_RESULT, false);
        long testDate = prefs.getLong(PREF_SEEK_TEST_DATE, 0);
        long now = (new Date()).getTime();
        long oneWeekMS = 1000 * 60 * 60 * 24 * 7;

        if (now - testDate < oneWeekMS) {
            Log.i("Ringdroid", "Fast MP3 seek result cached: " + result);
            return result;
        }

        String filename = "/sdcard/silence" + new Random().nextLong() + ".mp3";
        File file = new File(filename);
        boolean ok = false;
        try {
            RandomAccessFile f = new RandomAccessFile(file, "r");
        } catch (Exception e) {
            ok = true;
        }

        if (!ok) {
            Log.i("Ringdroid", "Couldn't find temporary filename");
            return false;
        }

        Log.i("Ringdroid", "Writing " + filename);

        try {
            file.createNewFile();
        } catch (Exception e) {
            Log.i("Ringdroid", "Couldn't output for writing");
            return false;
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            for (int i = 0; i < 80; i++) {
                out.write(SILENCE_MP3_FRAME, 0, SILENCE_MP3_FRAME.length);
            }
        } catch (Exception e) {
            Log.i("Ringdroid", "Couldn't write temp silence file");
            try {
                file.delete();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return false;
        }

        try {
            Log.i("Ringdroid", "File written, starting to play");
            MediaPlayer player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            FileInputStream subsetInputStream = new FileInputStream(filename);
            long start = 70 * SILENCE_MP3_FRAME.length;
            long len = 10 * SILENCE_MP3_FRAME.length;
            player.setDataSource(subsetInputStream.getFD(),
                    start,
                    len);
            Log.i("Ringdroid", "Preparing");
            player.prepare();
            before = 0;
            after = 0;
            player.setOnCompletionListener(new OnCompletionListener() {
                public synchronized void onCompletion(MediaPlayer arg0) {
                    Log.i("Ringdroid", "Got callback");
                    after = System.currentTimeMillis();
                }
            });

            Log.i("Ringdroid", "Starting");
            player.start();

            for (int i = 0; i < 200 && before == 0; i++) {
                if (player.getCurrentPosition() > 0) {
                    Log.i("Ringdroid", "Started playing after " + (i * 10) +
                            " ms");
                    before = System.currentTimeMillis();
                }
                Thread.sleep(10);
            }
            if (before == 0) {
                Log.i("Ringdroid", "Never started playing.");
                Log.i("Ringdroid", "Fast MP3 seek disabled by default");
                try {
                    file.delete();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

                SharedPreferences.Editor prefsEditor = prefs.edit();
                prefsEditor.putLong(PREF_SEEK_TEST_DATE, now);
                prefsEditor.putBoolean(PREF_SEEK_TEST_RESULT, result);
                prefsEditor.commit();

                return false;
            }

            Log.i("Ringdroid", "Sleeping");
            for (int i = 0; i < 300 && after == 0; i++) {
                Log.i("Ringdroid", "Pos: " + player.getCurrentPosition());
                Thread.sleep(10);
            }

            Log.i("Ringdroid", "Result: " + before + ", " + after);

            if (after > before && after < before + 2000) {
                long delta = after > before ? after - before : -1;
                Log.i("Ringdroid", "Fast MP3 seek enabled: " + delta);
                result = true;
            } else {
                Log.i("Ringdroid", "Fast MP3 seek disabled");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Ringdroid", "Couldn't play: " + e.toString());
            Log.i("Ringdroid", "Fast MP3 seek disabled by default");
            try {
                file.delete();
            } catch (Exception e2) {
                e2.printStackTrace();
            }

            SharedPreferences.Editor prefsEditor = prefs.edit();
            prefsEditor.putLong(PREF_SEEK_TEST_DATE, now);
            prefsEditor.putBoolean(PREF_SEEK_TEST_RESULT, result);
            prefsEditor.commit();

            return false;
        }

        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putLong(PREF_SEEK_TEST_DATE, now);
        prefsEditor.putBoolean(PREF_SEEK_TEST_RESULT, result);
        prefsEditor.commit();

        try {
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    static private byte[] SILENCE_MP3_FRAME = {
            (byte) 0xff, (byte) 0xfb, (byte) 0x10, (byte) 0xc4, (byte) 0x00,
            (byte) 0x03, (byte) 0x81, (byte) 0xf4, (byte) 0x01, (byte) 0x26,
            (byte) 0x60, (byte) 0x00, (byte) 0x40, (byte) 0x20, (byte) 0x59,
            (byte) 0x80, (byte) 0x23, (byte) 0x48, (byte) 0x00, (byte) 0x09,
            (byte) 0x74, (byte) 0x00, (byte) 0x01, (byte) 0x12, (byte) 0x03,
            (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xfe,
            (byte) 0x9f, (byte) 0x63, (byte) 0xbf, (byte) 0xd1, (byte) 0x7a,
            (byte) 0x3f, (byte) 0x5d, (byte) 0x01, (byte) 0xff, (byte) 0xff,
            (byte) 0xff, (byte) 0xff, (byte) 0xfe, (byte) 0x8d, (byte) 0xad,
            (byte) 0x6c, (byte) 0x31, (byte) 0x42, (byte) 0xc3, (byte) 0x02,
            (byte) 0xc7, (byte) 0x0c, (byte) 0x09, (byte) 0x86, (byte) 0x83,
            (byte) 0xa8, (byte) 0x7a, (byte) 0x3a, (byte) 0x68, (byte) 0x4c,
            (byte) 0x41, (byte) 0x4d, (byte) 0x45, (byte) 0x33, (byte) 0x2e,
            (byte) 0x39, (byte) 0x38, (byte) 0x2e, (byte) 0x32, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
}