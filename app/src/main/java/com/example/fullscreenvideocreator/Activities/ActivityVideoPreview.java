package com.example.fullscreenvideocreator.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.fullscreenvideocreator.R;
import com.example.fullscreenvideocreator.Utills.Constance;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;

public class ActivityVideoPreview extends AppCompatActivity {

    Context context;
    String Video_Path;
    VideoView vv_playvideo;
    MediaController mediacontroller;
    ProgressDialog pDialog;
    LinearLayout facbook_ad_banner;
    AdView mAdView;
    ImageView iv_vp_play;
    ImageView iv_video_pic;
    boolean df = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);

        context = ActivityVideoPreview.this;
        Video_Path = getIntent().getExtras().getString("Video_Path");
        // Toast.makeText(context, "path:-" + Video_Path, Toast.LENGTH_LONG).show();
        vv_playvideo = findViewById(R.id.vv_playvideo);
        facbook_ad_banner = findViewById(R.id.facbook_ad_banner);
        mAdView = findViewById(R.id.adView);
        iv_vp_play = findViewById(R.id.iv_vp_play);
        iv_video_pic = findViewById(R.id.iv_video_pic);

       /* Glide.with(context).load(selectedimages.get(0).getSingleimagepath())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(iv_video_pic);*/

        if (Constance.adType.equals("Ad Mob")) {
            displayAdMob();
            Log.d("ADssss", "Ad Mob");
        } else {
            interstitialFacbookAd();
            Log.d("ADssss", "Facebook");
        }

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Buffering...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
     /*   iv_video_pic.setVisibility(View.GONE);
        iv_vp_play.setVisibility(View.GONE);
*/
        iv_vp_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.dismiss();
                Log.d("wseqwqwqwqwqwqwqw", "sdss");
                vv_playvideo.start();
                iv_video_pic.setVisibility(View.GONE);
                iv_vp_play.setVisibility(View.GONE);
                df = true;

            }
        });
        try {
            mediacontroller = new MediaController(this);
            mediacontroller.setAnchorView(vv_playvideo);
            vv_playvideo.setMediaController(mediacontroller);
            vv_playvideo.setVideoPath(Video_Path);

        } catch (Exception e) {
            e.printStackTrace();
        }

        vv_playvideo.requestFocus();

        if (Constance.adType.equals("Ad Mob")) {
            foradvertise();

        } else {
            facebookAd();
        }

        vv_playvideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                //vv_playvideo.start();


            }
        });
        vv_playvideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                    mp.stop();
                }
                //mp.stop();
                vv_playvideo.pause();
                //iv_video_pic.setVisibility(View.VISIBLE);
                iv_vp_play.setVisibility(View.VISIBLE);
            }
        });

    }


    public void foradvertise() {
        facbook_ad_banner.setVisibility(View.GONE);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice("C04B1BFFB0774708339BC273F8A43708")
                .build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                // Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                // Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mAdView.loadAd(adRequest);
    }


    public void facebookAd() {

        mAdView.setVisibility(View.GONE);
        facbook_ad_banner.setVisibility(View.VISIBLE);
        com.facebook.ads.AdView adFaceView = new com.facebook.ads.AdView(context, getResources().getString(R.string.facebook_banner_id), AdSize.BANNER_HEIGHT_50);

        AdSettings.setDebugBuild(true);
        //AdSettings.addTestDevice("HASHED ID");
        // Find the Ad Container


        // Add the ad view to your activity layout
        facbook_ad_banner.addView(adFaceView);

        // Request an ad
        adFaceView.loadAd();

    }

    public void displayAdMob() {
        if (ActivityHome.getInstance().mInterstitialAd != null) {
            if (ActivityHome.getInstance().mInterstitialAd.isLoaded()) {
                Log.d("shsjks","sdhsjkhd");
                ActivityHome.getInstance().mInterstitialAd.show();
            } else {
            }
        } else {

        }
    }

    public void interstitialFacbookAd() {

        if (ActivityHome.getInstance().interstitialFacbookAd != null) {
            if (!ActivityHome.getInstance().interstitialFacbookAd.isAdLoaded()) {

                AdSettings.setDebugBuild(true);
                ActivityHome.getInstance().interstitialFacbookAd.loadAd();
            } else {

            }
        } else {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
      /*  vv_playvideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                //vv_playvideo.start();


            }
        });
        vv_playvideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                    mp.stop();
                }
                //mp.stop();
                vv_playvideo.pause();
                //iv_video_pic.setVisibility(View.VISIBLE);
                 iv_vp_play.setVisibility(View.VISIBLE);
            }
        });*/
    }

    public void onclickItems(View view) {
        switch (view.getId()) {
            case R.id.iv_backarrow:
                onBackPressed();
                //  startActivity(new Intent(context,ActivityHome.class));
                break;
            case R.id.iv_MyVideo:
                startActivity(new Intent(context, ActivityMyVideo.class));
                break;
            case R.id.btn_share:
                shareVideo(context, Video_Path);
                break;
            case R.id.btn_delete:
                showDeleteDailog();
                break;
        }
    }

    public static void shareVideo(Context context, String filePath) {
        Uri mainUri = Uri.parse(filePath);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("video/mp4");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(Intent.createChooser(sharingIntent, "Share Video using"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Application not found to open this file", Toast.LENGTH_LONG).show();
        }
    }

    public void showDeleteDailog() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        final AlertDialog alertDialog = builder.create();
        builder.setMessage("Are you sure to delete this video")
                .setCancelable(false)
                .setTitle("Delete ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        File file = new File(Video_Path);
                        file.delete();
                        dialog.dismiss();
                        onBackPressed();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

}
