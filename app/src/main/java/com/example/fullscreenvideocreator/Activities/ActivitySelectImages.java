package com.example.fullscreenvideocreator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fullscreenvideocreator.Adepters.Adapter_PhotoFolder;
import com.example.fullscreenvideocreator.Adepters.Adapter_Selected_images;
import com.example.fullscreenvideocreator.Adepters.Adapter_SinglePhotoFolder;
import com.example.fullscreenvideocreator.Models.Model_images;
import com.example.fullscreenvideocreator.R;
import com.example.fullscreenvideocreator.Utills.Constance;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class ActivitySelectImages extends AppCompatActivity {

    static Context context;
    RecyclerView rv_selectimage;
    static RecyclerView rv_singlefolderimages;
    static RecyclerView rv_single_selectedImage;
    AdView adView_leavepage;
    static TextView tv_countselectedimage;
    ArrayList<String> imageData;
    public static ArrayList<Model_images> al_images;
    public static ArrayList<Model_images> single_selectedimages = new ArrayList<>();
    boolean boolean_folder;
    public static  Adapter_PhotoFolder adapter_photoFolder;
    public static Adapter_SinglePhotoFolder adapter_singlePhotoFolder;
    static Adapter_Selected_images adapter_selected_images;
    LinearLayout ll_nextbtn,facbook_ad_banner;
    ImageView iv_nextbtn, iv_edit_selectedimage, iv_delete_selectedimage;
    private static final int REQUEST_PERMISSIONS = 100;
    boolean checkForResult;
    AdView mAdView;
    LinearLayout facbook_ad_banner1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_images);
        context = ActivitySelectImages.this;
        bindview();
        if(Constance.adType.equals("Ad Mob"))
        {
            foradvertise();

        }
        else {
            facebookAd1();
        }

        checkForResult=getIntent().getBooleanExtra("checkForResult",false);

 al_images = new ArrayList<>();

        rv_selectimage.setHasFixedSize(true);
        rv_single_selectedImage.setHasFixedSize(true);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_selectimage.setLayoutManager(layoutManager);

        LinearLayoutManager layoutManager_selectedimage
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_single_selectedImage.setLayoutManager(layoutManager_selectedimage);

        rv_singlefolderimages.setLayoutManager(new GridLayoutManager(context, 3));

        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(ActivitySelectImages.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(ActivitySelectImages.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(ActivitySelectImages.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        }else {
            Log.e("Else","Else");
            fn_imagespath();

        }

        ll_nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (single_selectedimages.size() < 3)
                {
                    Toast.makeText(context, "select atleast 3 image to create video", Toast.LENGTH_LONG).show();
                }
                else
                    {
                        Constance.selectedimages = single_selectedimages;
                    if(checkForResult)
                    {
                       // Constance.selectedimages = single_selectedimages;
                        setResult(RESULT_OK);
                        finish();
                       // startActivity(new Intent(context, DemoActivity.class));

                    }else
                    {
                        startActivity(new Intent(context, ActivityListSelectedPhotos.class));
                    }
                }
              /*  Constance.selectedimages = single_selectedimages;
                if(checkForResult)
                {
                    // Constance.selectedimages = single_selectedimages;
                    setResult(RESULT_OK);
                    finish();
                    // startActivity(new Intent(context, DemoActivity.class));

                }else
                {
                    startActivity(new Intent(context, ActivityListSelectedPhotos.class));
                }*/
            }
        });
        iv_edit_selectedimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });
        iv_delete_selectedimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDailog();
            }
        });
    }

    public void showDeleteDailog() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dailog_delete);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);
        TextView tv_yes, tv_no;
        tv_yes = dialog.findViewById(R.id.tv_yes);
        tv_no = dialog.findViewById(R.id.tv_no);

        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                single_selectedimages.clear();
                tv_countselectedimage.setText("0");
                adapter_selected_images.notifyDataSetChanged();
                dialog.dismiss();
                //finishAffinity();
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

    public static void setSingleFolderData() {
        adapter_singlePhotoFolder = new Adapter_SinglePhotoFolder(context, al_images, Constance.Folderposition);
        adapter_singlePhotoFolder.notifyDataSetChanged();
        rv_singlefolderimages.setAdapter(adapter_singlePhotoFolder);

    }

    public static void getSingleSelectedImage(String selectedimagelist) {

        single_selectedimages.add(new Model_images(selectedimagelist));

        //Toast.makeText(context,""+single_selectedimages.size(),Toast.LENGTH_LONG).show();
        tv_countselectedimage.setText(String.valueOf(single_selectedimages.size()));
        adapter_selected_images.notifyDataSetChanged();

    }

    public static void removeItemOnrecyclerview(int position) {

        single_selectedimages.remove(position);
        tv_countselectedimage.setText(String.valueOf(single_selectedimages.size()));

        // Toast.makeText(context,""+modelAddremiberAddmedications.size(),Toast.LENGTH_LONG).show();
        adapter_selected_images.notifyDataSetChanged();

    }


    public  ArrayList<Model_images> fn_imagespath() {
        al_images.clear();

        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        uri =MediaStore.Images.Media.EXTERNAL_CONTENT_URI;


        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = context.getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
          //  Log.e("Column", absolutePathOfImage);
            //Log.d("Folder", cursor.getString(column_index_folder_name));
           Log.d("Folder", "assasas"+al_images.size());
           Log.d("Folder", "assasas"+boolean_folder);

            //Toast.makeText(context, "" + al_images.size(), Toast.LENGTH_LONG).show();
            for (int i = 0; i < al_images.size(); i++) {
                Log.d("Folder", "forloop");
                if (al_images.get(i).getStr_folder().equals(cursor.getString(column_index_folder_name))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }
            if (boolean_folder) {
                Log.d("Folder", "if");
                ArrayList<String> al_path = new ArrayList<>();
                al_path.addAll(al_images.get(int_position).getAl_imagepath());
                Log.d("qqq", "if : " +al_images.get(int_position).getAl_imagepath().size());
                Log.d("qqq", "if : " +absolutePathOfImage);

                al_path.add(absolutePathOfImage);
                al_images.get(int_position).setAl_imagepath(al_path);

            } else {
                Log.d("Folder", "else");
                ArrayList<String> al_path = new ArrayList<>();
                al_path.add(absolutePathOfImage);
                Model_images obj_model = new Model_images();
                obj_model.setStr_folder(cursor.getString(column_index_folder_name));
                obj_model.setAl_imagepath(al_path);
                al_images.add(obj_model);
            }

        }
        for (int i = 0; i < al_images.size(); i++) {
            Log.d("www","FOLDER"+ al_images.get(i).getStr_folder());
            for (int j = 0; j < al_images.get(i).getAl_imagepath().size(); j++) {
               Log.d("www","FILE"+ al_images.get(i).getAl_imagepath().get(j));
            }
        }
        adapter_photoFolder = new Adapter_PhotoFolder(context, al_images);
        rv_selectimage.setAdapter(adapter_photoFolder);
        return al_images;
    }
    public void foradvertise() {
        facbook_ad_banner1.setVisibility(View.GONE);
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

    @Override
    protected void onResume() {
        super.onResume();
        tv_countselectedimage.setText(String.valueOf(single_selectedimages.size()));
        adapter_selected_images = new Adapter_Selected_images(context, single_selectedimages);
        adapter_selected_images.notifyDataSetChanged();
        rv_single_selectedImage.setAdapter(adapter_selected_images);


        adapter_singlePhotoFolder = new Adapter_SinglePhotoFolder(context, al_images, Constance.Folderposition);
        adapter_singlePhotoFolder.notifyDataSetChanged();
        rv_singlefolderimages.setAdapter(adapter_singlePhotoFolder);


    }
    public void bindview(){
        rv_selectimage = findViewById(R.id.rv_selectimage);
        rv_singlefolderimages = findViewById(R.id.rv_singlefolderimages);
        rv_single_selectedImage = findViewById(R.id.rv_single_selectedImage);
        tv_countselectedimage = findViewById(R.id.tv_countselectedimage);
        iv_nextbtn = findViewById(R.id.iv_nextbtn);
        iv_edit_selectedimage = findViewById(R.id.iv_edit_selectedimage);
        iv_delete_selectedimage = findViewById(R.id.iv_delete_selectedimage);
        ll_nextbtn = findViewById(R.id.ll_nextbtn);
        facbook_ad_banner1 = findViewById(R.id.facbook_ad_banner1);
         mAdView = findViewById(R.id.adView);
    }

    public void showleavethispagedailog()
    {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dailog_leavepage);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);
        TextView tv_yes, tv_no;

        tv_yes = dialog.findViewById(R.id.tv_yes);
        tv_no = dialog.findViewById(R.id.tv_no);
        adView_leavepage = dialog.findViewById(R.id.adView_leavepage);
        facbook_ad_banner=dialog.findViewById(R.id.facbook_ad_banner);

        if (Constance.adType.equals("Ad Mob")) {
            foradvertiseleavepage();

        } else {
            facebookAd();
        }


        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                single_selectedimages.clear();
                tv_countselectedimage.setText("0");
                adapter_selected_images.notifyDataSetChanged();
                dialog.dismiss();
                startActivity(new Intent(context,ActivityHome.class));
                //finishAffinity();
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


    public void facebookAd() {

        adView_leavepage.setVisibility(View.GONE);
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
    public void facebookAd1() {

        mAdView.setVisibility(View.GONE);
        facbook_ad_banner1.setVisibility(View.VISIBLE);
        com.facebook.ads.AdView adFaceView = new com.facebook.ads.AdView(context, getResources().getString(R.string.facebook_banner_id), AdSize.BANNER_HEIGHT_50);

        AdSettings.setDebugBuild(true);
        //AdSettings.addTestDevice("HASHED ID");
        // Find the Ad Container


        // Add the ad view to your activity layout
        facbook_ad_banner1.addView(adFaceView);

        // Request an ad
        adFaceView.loadAd();

    }

    public void foradvertiseleavepage() {
        facbook_ad_banner.setVisibility(View.GONE);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice("C04B1BFFB0774708339BC273F8A43708")
                .build();

        adView_leavepage.setAdListener(new AdListener() {
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

        adView_leavepage.loadAd(adRequest);
    }
    @Override
    public void onBackPressed() {
        showleavethispagedailog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        fn_imagespath();

                    } else {
                        Toast.makeText(ActivitySelectImages.this, "The app was not allowed to read or write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
}
