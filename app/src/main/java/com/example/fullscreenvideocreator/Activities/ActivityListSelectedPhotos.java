package com.example.fullscreenvideocreator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.fullscreenvideocreator.Adepters.Adapter_ListSelectedPhotos;
import com.example.fullscreenvideocreator.Models.Model_images;
import com.example.fullscreenvideocreator.R;
import com.example.fullscreenvideocreator.Utills.Constance;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class ActivityListSelectedPhotos extends AppCompatActivity {

    static Context context;
    static int newuriposition;
    static Adapter_ListSelectedPhotos adapter_listSelectedPhotos;
    public static ArrayList<Model_images> list_selectedimages = new ArrayList<>();
    RecyclerView rv_listselectedimage;
    ImageView iv_backarrow_listselectedimage, iv_nextarrow_listselectedarrow;
    boolean checkForEditResult;
    AdView mAdView;
LinearLayout facbook_ad_banner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_selected_photos);

        context = ActivityListSelectedPhotos.this;
        bindView();


        if(Constance.adType.equals("Ad Mob"))
        {
            foradvertise();

        }
        else {
            facebookAd();
        }


        rv_listselectedimage.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager1 = new GridLayoutManager(this, 2);
        rv_listselectedimage.setLayoutManager(layoutManager1);

        list_selectedimages = Constance.selectedimages;

        checkForEditResult = getIntent().getBooleanExtra("checkForEditResult", false);


        iv_backarrow_listselectedimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        iv_nextarrow_listselectedarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constance.selectedimages=list_selectedimages;
                if (checkForEditResult) {
                    // onBackPressed();
                    setResult(RESULT_OK);
                    finish();

                } else {
                    startActivity(new Intent(context, DemoActivity.class));
                }
            }
        });
    }

    public static void removeItemOnrecyclerview(int position) {

        list_selectedimages.remove(position);
        adapter_listSelectedPhotos.notifyDataSetChanged();

    }

    public static void EditItemOnrecyclerview(int position, String editimagepath) {
        newuriposition = position;
        Intent i = new Intent(context, ActivityImageEditor.class);
        i.putExtra("editimagepath", editimagepath);
        context.startActivity(i);
    }

    public void bindView() {
        rv_listselectedimage = findViewById(R.id.rv_listselectedimage);
        iv_nextarrow_listselectedarrow = findViewById(R.id.iv_nextarrow_listselectedarrow);
        iv_backarrow_listselectedimage = findViewById(R.id.iv_backarrow_listselectedimage);
         mAdView = findViewById(R.id.adView);
        facbook_ad_banner = findViewById(R.id.facbook_ad_banner);

    }

    public void foradvertise() {
        mAdView.setVisibility(View.VISIBLE);
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


    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        list_selectedimages.get(newuriposition).setSingleimagepath(cursor.getString(column_index));
        adapter_listSelectedPhotos.notifyItemChanged(newuriposition);
        return cursor.getString(column_index);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (Constance.new_uri_path != null) {
            //Glide.with(context).load(Constance.new_uri_path).into(imagdemo);
            Log.d("kkkk", "Constance.new_uri_path :-" + Constance.new_uri_path);
            Log.d("kkkk", "Before list_selectedimages :-" + list_selectedimages.get(newuriposition).getSingleimagepath());
            getPath(Constance.new_uri_path);
            // Toast.makeText(context,"hello resume"+list_selectedimages.get(newuriposition).getSingleimagepath(),Toast.LENGTH_LONG).show();
            Log.d("kkkk", "list_selectedimages :-" + list_selectedimages.get(newuriposition).getSingleimagepath());
        }
        adapter_listSelectedPhotos = new Adapter_ListSelectedPhotos(context, list_selectedimages);
        rv_listselectedimage.setAdapter(adapter_listSelectedPhotos);

    }

}
