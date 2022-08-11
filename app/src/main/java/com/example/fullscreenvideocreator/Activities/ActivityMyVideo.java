package com.example.fullscreenvideocreator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.fullscreenvideocreator.Adepters.AdapterMyVideoFileList;
import com.example.fullscreenvideocreator.R;
import com.example.fullscreenvideocreator.Utills.Constance;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.util.ArrayList;

public class ActivityMyVideo extends AppCompatActivity {

    Context context;
    ImageView iv_backarrow;
    RecyclerView rv_myvideolist;
    private AdapterMyVideoFileList adapterMyVideoFileList;
    private ArrayList<File> fileArrayList;
    public static RelativeLayout rl_videonotfound;
    public static InterstitialAd mInterstitialAd;
    AdView mAdView;
    LinearLayout facbook_ad_banner;
    static public  com.facebook.ads.InterstitialAd interstitialFacbookAd;
    private final String TAG = ActivityMyVideo.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_video);

        context = ActivityMyVideo.this;
        iv_backarrow = findViewById(R.id.iv_backarrow);
        rv_myvideolist = findViewById(R.id.rv_myvideolist);
        rl_videonotfound = findViewById(R.id.rl_videonotfound);
        facbook_ad_banner = findViewById(R.id.facbook_ad_banner);
        mAdView= findViewById(R.id.adView);
        rv_myvideolist.setLayoutManager(new LinearLayoutManager(this));
      //  LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        iv_backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if(Constance.adType.equals("Ad Mob"))
        {
            foradvertise();

        }
        else {
            facebookAd();
        }
        showInterstitialAds();
        facebookInterstitialAd();
    }

    public void foradvertise() {

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


    public void facebookAd(){

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

    private void getAllFiles() {
        fileArrayList = new ArrayList<>();
        // Toast.makeText(context,""+Constance.FileDirectory,Toast.LENGTH_LONG).show();

        if (!Constance.FileDirectory.exists()) {
            Constance.FileDirectory.mkdir();

        } else {
            Log.d("jjjjj", "Constance.FileDirectory : " + Constance.FileDirectory);
            File[] files = Constance.FileDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".mp4"))
                    {
                        fileArrayList.add(file);
                    }
                }

                for(int i=0;i<fileArrayList.size();i++){
                    if(fileArrayList.get(i).isDirectory()){
                        fileArrayList.remove(i);
                    }
                }
                adapterMyVideoFileList = new AdapterMyVideoFileList(context, fileArrayList);
                adapterMyVideoFileList.notifyDataSetChanged();
                rv_myvideolist.setAdapter(adapterMyVideoFileList);
            }
            else {
                Toast.makeText(context, "Data Not Found", Toast.LENGTH_LONG).show();
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllFiles();
        if (fileArrayList.size() == 0) {
            rl_videonotfound.setVisibility(View.VISIBLE);
        } else {
            rl_videonotfound.setVisibility(View.GONE);
        }
    }

    public static void showInterstitial() {

        if(Constance.adType.equals("Ad Mob"))
        {
            if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }

        }
        else {
            if (interstitialFacbookAd.isAdLoaded()) {

                AdSettings.setDebugBuild(true);
                interstitialFacbookAd.loadAd();
            }
        }

    }

    public void showInterstitialAds() {

        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_full_screen));
        mInterstitialAd.loadAd(new AdRequest.Builder()
                .addTestDevice("6A2D2B68A7166B0DE00868C6F74E8DB9")
                .addTestDevice("88045C0A4BBC3C24FABBF3D543FC7C8C")
                .addTestDevice("3BCC9944F0D7A19C3D3BEFCD7D8B3EDE")
                .addTestDevice("D3662558A58B055494404223B20E0CA8")
                .build());

        if (mInterstitialAd != null) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

            }
            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.

            }
            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.

            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.

            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.

                mInterstitialAd.loadAd(new AdRequest.Builder()
                        .addTestDevice("6A2D2B68A7166B0DE00868C6F74E8DB9")
                        .addTestDevice("88045C0A4BBC3C24FABBF3D543FC7C8C")
                        .addTestDevice("3BCC9944F0D7A19C3D3BEFCD7D8B3EDE")
                        .addTestDevice("D3662558A58B055494404223B20E0CA8")
                        .build());
            }
        });

    }

    void facebookInterstitialAd(){

        AudienceNetworkAds.initialize(this);


        //AdSettings.setIntegrationErrorMode(INTEGRATION_ERROR_CRASH_DEBUG_MODE);
        // Toast.makeText(ActivityNewsPapersCategory.this,"id"+getResources().getString(R.string.facebook_interstitial_Ad),Toast.LENGTH_LONG).show();
        interstitialFacbookAd = new com.facebook.ads.InterstitialAd(this, getResources().getString(R.string.facebook_interstitial_Ad));
        AdSettings.setDebugBuild(true);
        // AdSettings.addTestDevice("HASHED ID");
        interstitialFacbookAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                interstitialFacbookAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        });

    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent intent = new Intent(context, ActivityHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
