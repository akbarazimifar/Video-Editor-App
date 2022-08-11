package com.example.fullscreenvideocreator.Utills;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;

import com.example.fullscreenvideocreator.Models.Model_images;
import com.example.fullscreenvideocreator.StickerClasses.DrawableSticker;
import com.hw.photomovie.PhotoMovieFactory;
import com.hw.photomovie.model.PhotoSource;

import java.io.File;
import java.util.ArrayList;

public class Constance {

    public static int Folderposition = 0;
    public static int elapsedTime=0;
    public static int changeDuration;
    public static int END_GAUSSIANBLUR_DURATION;
    public static int changeDuration_Windowsegment;
    public static int ThawSegmentduration;
    public static int HorizontalTrans;
    public static int widthOfVideo;
    public static int heightOfVideo;
    public static PhotoSource finallistphotosource;
    public static int durationseek_progress=2;

    public static PhotoMovieFactory.PhotoMovieType mMovieType = PhotoMovieFactory.PhotoMovieType.HORIZONTAL_TRANS;

    public static ArrayList<Model_images> selectedimages = new ArrayList<>();
   // public static String adType = "facebook";
    public static String adType = "Ad Mob";

    public static File FileDirectory = Environment.getExternalStoragePublicDirectory("Full Screen Video Creator" + "/");
    public static final File CACHE_MUSIC_DIRECTORY = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "FullScreen_Video_Creator_Music");
    public static final File CACHE_MUSIC_File = new File(CACHE_MUSIC_DIRECTORY, "defaultmusic.mp3");
    public static final String rawMusicDirectory=Environment.getExternalStorageDirectory() + "/FullScreenRawMusic";


    public static String shareapp_url = "https://play.google.com/store/apps/details?id";
    public static String Rateapp = "http://play.google.com/store/apps/details?id";
    public static String Moreapp = "https://play.google.com/store/apps/details?id";
    public static String aboutUs = "Your content should be the voice of your brand. And, at Yasza Media, we make sure that we surpass your expectations while meeting your audience’s needs. We believe content which is engaging, relevant, and informative is the core to establishing your brand’s reputation online.\n" +
            "This is why we invest time in researching your target audience. We take into consideration several factors such as individual preferences, demographics, platform usage, and trends to build a content plan that yields results.\n";
    public static boolean AllowToOpenAdvertise = false;


    //photo editor

    public static Bitmap DemoBitmapImage;
    public static String FONT_STYLE = "";
    public static String FontStyle = "ABeeZee.otf";
    public static String editimagepath;
    public static Uri new_uri_path;
    public static String FONT_EFFECT = "6";

    public static String FONT_TEXT = "";
    public static boolean FONT_FLAG = false;
    public static Integer COLOR = Color.parseColor("#00BFFF");

    public static boolean isStickerTouch = false;
    public static boolean isStickerAvail = false;
    public static DrawableSticker TEXT_DRAWABLE;

    public static int screenWidth = 0;
    public static int screenHeight = 0;


}
