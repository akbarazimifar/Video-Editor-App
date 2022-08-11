package com.example.fullscreenvideocreator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.hw.photomovie.PhotoMovieFactory;

public class ActivityHome extends AppCompatActivity {

    Context context;
    LinearLayout ll_createvideo;
    private static final int REQUEST_PERMISSIONS = 100;
    LinearLayout ll_myvideo;
    LinearLayout facbook_ad_banner;
    AdView mAdView;
    public static ActivityHome instance = null;

    private final String TAG = ActivityHome.class.getSimpleName();

    public ActivityHome() {
        instance = ActivityHome.this;
    }

    public static synchronized ActivityHome getInstance() {
        if (instance == null) {
            instance = new ActivityHome();
        }
        return instance;
    }
     public InterstitialAd mInterstitialAd;

    public com.facebook.ads.InterstitialAd interstitialFacbookAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorBlack));

        context = ActivityHome.this;


        ll_createvideo = findViewById(R.id.ll_createvideo);
        ll_myvideo = findViewById(R.id.ll_myvideo);
        facbook_ad_banner = findViewById(R.id.facbook_ad_banner);
        mAdView = findViewById(R.id.adView);


        if (Constance.adType.equals("Ad Mob")) {
            foradvertise();

        } else {
            facebookAd();
        }

        Constance.selectedimages.clear();
        Constance.new_uri_path = null;
        Constance.elapsedTime = 0;
        Constance.changeDuration = 0;
        Constance.Folderposition=0;
        Constance.durationseek_progress=2;
        Constance.mMovieType = PhotoMovieFactory.PhotoMovieType.HORIZONTAL_TRANS;


        ll_createvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getpermission();
            }
        });
        ll_myvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, ActivityMyVideo.class));
            }
        });

        //// AdMob Ad-------------------------


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
                // Code to be executed when an ad finishes loading.
                /*if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }*/
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.

                Constance.AllowToOpenAdvertise = false;

            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
                Constance.AllowToOpenAdvertise = false;
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Constance.AllowToOpenAdvertise = false;

            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
                Constance.AllowToOpenAdvertise = false;


                mInterstitialAd.loadAd(new AdRequest.Builder()
                        .addTestDevice("6A2D2B68A7166B0DE00868C6F74E8DB9")
                        .addTestDevice("88045C0A4BBC3C24FABBF3D543FC7C8C")
                        .addTestDevice("3BCC9944F0D7A19C3D3BEFCD7D8B3EDE")
                        .addTestDevice("D3662558A58B055494404223B20E0CA8")
                        .build());
            }
        });



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


    public void getpermission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(ActivityHome.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(ActivityHome.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(ActivityHome.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        } else {
            Log.e("Else", "Else");
            startActivity(new Intent(context, ActivitySelectImages.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                        startActivity(new Intent(context, ActivitySelectImages.class));
                    } else {
                        Toast.makeText(ActivityHome.this, "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dailog_exit);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);


        TextView tv_yes, tv_no;
        RelativeLayout rl_gotoback;
        LinearLayout ll_exitapp;
        tv_yes = dialog.findViewById(R.id.tv_yes);
        tv_no = dialog.findViewById(R.id.tv_no);
        rl_gotoback = dialog.findViewById(R.id.rl_gotoback);
        ll_exitapp = dialog.findViewById(R.id.ll_exitapp);
        rl_gotoback.setVisibility(View.GONE);
        ll_exitapp.setVisibility(View.VISIBLE);

        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });
        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void onClickHome(View view) {
        switch (view.getId()) {
            case R.id.ll_shareapp:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, Constance.shareapp_url + getPackageName());
                intent.setType("text/plain");
                this.startActivity(intent);
                break;
            case R.id.ll_RateUs:
                try {
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent intentrate = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intentrate);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(Constance.Rateapp)));
                }
                break;
            case R.id.ll_Moreapp:
                try {
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent intentmoreapp = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intentmoreapp);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(Constance.Moreapp)));
                }
                break;
            case R.id.ll_AboutUs:
                startActivity(new Intent(context, ActivityAboutUs.class));

                break;
        }
    }
}
