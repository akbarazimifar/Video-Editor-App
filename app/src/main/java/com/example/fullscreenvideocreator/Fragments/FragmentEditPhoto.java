package com.example.fullscreenvideocreator.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fullscreenvideocreator.Activities.ActivityFont;
import com.example.fullscreenvideocreator.Activities.ActivityImageEditor;
import com.example.fullscreenvideocreator.Adepters.AdapterBottomNavigation;
import com.example.fullscreenvideocreator.Adepters.StickerColorAdapter;
import com.example.fullscreenvideocreator.Models.ModelBottomNavigation;
import com.example.fullscreenvideocreator.Models.StickerModel;
import com.example.fullscreenvideocreator.R;
import com.example.fullscreenvideocreator.StickerClasses.DrawableSticker;
import com.example.fullscreenvideocreator.StickerClasses.StickerView;
import com.example.fullscreenvideocreator.Utills.Constance;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.hoanganhtuan95ptit.brightness.BrightnessFragment;
import com.hoanganhtuan95ptit.brightness.OnBrightnessListener;
import com.hoanganhtuan95ptit.contrast.ContrastFragment;
import com.hoanganhtuan95ptit.contrast.OnContrastListener;
import com.hoanganhtuan95ptit.crop.CropFragment;
import com.hoanganhtuan95ptit.crop.OnCropListener;
import com.hoanganhtuan95ptit.fillter.FilterFragment;
import com.hoanganhtuan95ptit.fillter.OnFilterListener;
import com.hoanganhtuan95ptit.rotate.OnRotateListener;
import com.hoanganhtuan95ptit.rotate.RotateFragment;
import com.hoanganhtuan95ptit.saturation.OnSaturationListener;
import com.hoanganhtuan95ptit.saturation.SaturationFragment;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

public class FragmentEditPhoto extends Fragment implements OnFilterListener, OnBrightnessListener, OnRotateListener, OnSaturationListener, OnContrastListener, OnCropListener {
    static Context context;
    static PhotoView photo_view;
    AdView mAdView;
    public static LinearLayout ll_stickerlist;
    RecyclerView rv_bottomnavigation;
    static AVLoadingIndicatorView ivLoading;

    public static StickerView sticker_view;
    public static List<DrawableSticker> drawables_sticker = new ArrayList<>();
    static ArrayList<StickerModel> list;
    AssetManager assetManager;
    RecyclerView rv_stickerlist;
    static ViewTreeObserver vto;
    static RelativeLayout rl_maincontent;
    static int imgFinalHeight = 0;
    static int imgFinalWidth = 0;
    StickerColorAdapter stickerAdapter;
    ImageView iv_nextactivity, iv_backtohome;
    Bitmap bitmapsave;
    ProgressDialog progressDialog;
LinearLayout facbook_ad_banner;
    @SuppressLint("ValidFragment")
    public FragmentEditPhoto() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_photo, container, false);
        context = getContext();
        bindview(view);

        if(Constance.adType.equals("Ad Mob"))
        {
            foradvertise();

        }
        else {
            facebookAd();
        }
        initView();
        getBitmap(Constance.editimagepath);

        hideLoading();
        list = new ArrayList<>();
        touchListener(photo_view);
        iv_nextactivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sticker_view.hideIcons(true);
                bitmapsave = viewToBitmap(rl_maincontent);
                getImageUri(context, bitmapsave);
                getActivity().finish();

            }
        });

        iv_backtohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });


        Constance.isStickerAvail = false;
        if (!Constance.isStickerAvail) {
            Constance.isStickerTouch = false;
            sticker_view.setLocked(true);
        }
        if (list != null && list.equals("null")) {
            list.clear();
        }
        assetManager = getActivity().getAssets();

        return view;
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + Calendar.getInstance().getTime(), null);
        Constance.new_uri_path = Uri.parse(path);
        return Uri.parse(path);
    }

    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public Bitmap getBitmap(String path) {
        Bitmap bitmap = null;
        try {
            File f = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            Constance.DemoBitmapImage = bitmap;
            editedPhoto(Constance.DemoBitmapImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static void editedPhoto(Bitmap bitmap) {
        Constance.DemoBitmapImage = bitmap;
        photo_view.setImageBitmap(Constance.DemoBitmapImage);


        final int height = (int) Math.ceil(Constance.screenWidth * (float) photo_view.getDrawable().getIntrinsicHeight() / photo_view.getDrawable().getIntrinsicWidth());
        photo_view.getLayoutParams().height = height;

        vto = photo_view.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {

                photo_view.getViewTreeObserver().removeOnPreDrawListener(this);

                imgFinalHeight = photo_view.getMeasuredHeight();
                imgFinalWidth = photo_view.getMeasuredWidth();

                // Manage image width based on height
                if (height > imgFinalWidth) {
//                        Log.e("Image", "Taller");
                    imgFinalWidth = (int) Math.ceil(imgFinalHeight * (float) photo_view.getDrawable().getIntrinsicWidth() / photo_view.getDrawable().getIntrinsicHeight());
                }

                photo_view.getLayoutParams().width = imgFinalWidth;
                FrameLayout.LayoutParams _rootLayoutParams = new FrameLayout.LayoutParams(imgFinalWidth, imgFinalHeight);
                //_rootLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                rl_maincontent.setLayoutParams(_rootLayoutParams);
                _rootLayoutParams.gravity = Gravity.CENTER;


                return true;
            }
        });

    }


    public void bindview(View view) {
        mAdView = view.findViewById(R.id.adView);
        photo_view = view.findViewById(R.id.photo_view);
        rv_bottomnavigation = view.findViewById(R.id.rv_bottomnavigation);
        sticker_view = view.findViewById(R.id.sticker_view);
        rv_stickerlist = view.findViewById(R.id.rv_stickerlist);
        ll_stickerlist = view.findViewById(R.id.ll_stickerlist);
        rl_maincontent = view.findViewById(R.id.rl_maincontent);
        iv_nextactivity = view.findViewById(R.id.iv_nextactivity);
        iv_backtohome = view.findViewById(R.id.iv_backtohome);
        ivLoading = view.findViewById(R.id.ivLoading);
        facbook_ad_banner = view.findViewById(R.id.facbook_ad_banner);

        rv_stickerlist.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public static void hideLoading() {
        if (ivLoading != null)
            ivLoading.smoothToHide();
    }

    private void showLoading() {
        if (ivLoading != null)
            ivLoading.smoothToShow();
    }

    private void initView() {
        AdapterBottomNavigation editAdapter = new AdapterBottomNavigation(context, navigationlist());
        rv_bottomnavigation.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rv_bottomnavigation.setAdapter(editAdapter);
        editAdapter.setOnItemEditPhotoClickedListener(new AdapterBottomNavigation.OnItemEditPhotoClickedListener() {
            @Override
            public void onItemEditPhotoClicked(int position) {
                switch (position) {
                    case 0:
                        showLoading();
                        ( (ActivityImageEditor) ( getActivity() ) ).addFragmentToStack(CropFragment.create(Constance.DemoBitmapImage, FragmentEditPhoto.this));

                        break;
                    case 1:
                        showLoading();

                        ( (ActivityImageEditor) ( getActivity() ) ).addFragmentToStack(FilterFragment.create(Constance.DemoBitmapImage, FragmentEditPhoto.this));

                        break;
                    case 2:
                        showLoading();

                        ( (ActivityImageEditor) ( getActivity() ) ).addFragmentToStack(RotateFragment.create(Constance.DemoBitmapImage, FragmentEditPhoto.this));
                        break;
                    case 3:
                        showLoading();
                        ( (ActivityImageEditor) ( getActivity() ) ).addFragmentToStack(SaturationFragment.create(Constance.DemoBitmapImage, FragmentEditPhoto.this));
                        break;
                    case 4:
                        showLoading();
                        ( (ActivityImageEditor) ( getActivity() ) ).addFragmentToStack(BrightnessFragment.create(Constance.DemoBitmapImage, FragmentEditPhoto.this));
                        break;
                    case 5:
                        showLoading();
                        ( (ActivityImageEditor) ( getActivity() ) ).addFragmentToStack(ContrastFragment.create(Constance.DemoBitmapImage, FragmentEditPhoto.this));
                        break;
                    case 6:
                        Intent intent = new Intent(getActivity(), ActivityFont.class);
                        startActivity(intent);
                        break;
                    case 7:

                        setStickerThumb();
                        break;
                    default:
                        break;
                }
            }
        });

    }

    public static ArrayList<ModelBottomNavigation> navigationlist() {
        ArrayList<ModelBottomNavigation> data = new ArrayList<>();

        data.add(new ModelBottomNavigation(R.drawable.ic_cropnew, "Crop"));
        data.add(new ModelBottomNavigation(R.drawable.ic_filter_photo, "Fillter"));
        data.add(new ModelBottomNavigation(R.drawable.ic_rotation, "Rotate"));
        data.add(new ModelBottomNavigation(R.drawable.ic_saturation, "Saturation"));
        data.add(new ModelBottomNavigation(R.drawable.ic_brightness, "Brightness"));
        data.add(new ModelBottomNavigation(R.drawable.ic_contrast, "Contrast"));
        data.add(new ModelBottomNavigation(R.drawable.ic_text, "Text"));
        data.add(new ModelBottomNavigation(R.drawable.ic_sticker, "Sticker"));

        return data;

    }

    @Override
    public void onCropPhotoCompleted(String url) {

        File imgFile = new File(url);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            editedPhoto(myBitmap);
            hideLoading();
        }


    }

    @Override
    public void onBrightnessPhotoCompleted(Bitmap bitmap) {
        editedPhoto(bitmap);
        hideLoading();
    }

    @Override
    public void onFilterPhotoCompleted(Bitmap bitmap) {
        editedPhoto(bitmap);
        hideLoading();
    }

    @Override
    public void onRotatePhotoCompleted(Bitmap bitmap) {
        editedPhoto(bitmap);
        hideLoading();
    }

    @Override
    public void onSaturationPhotoCompleted(Bitmap bitmap) {
        editedPhoto(bitmap);
        hideLoading();
    }

    @Override
    public void onContrastPhotoCompleted(Bitmap bitmap) {
        editedPhoto(bitmap);
        hideLoading();

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

    private void sortArray(String[] arrayList, final String frontStr) {

        Arrays.sort(arrayList, new Comparator<String>() {
            @Override
            public int compare(String entry1, String entry2) {
                Integer file1 = Integer.parseInt(( entry1.split(frontStr)[1] ).split("\\.")[0]);
                Integer file2 = Integer.parseInt(( entry2.split(frontStr)[1] ).split("\\.")[0]);
                return file1.compareTo(file2);
            }
        });
    }

    private void setStickerThumb() {
        new AsynTask().execute();

    }

    public static void setemoji(int position) {
        Constance.FONT_FLAG = false;
        DrawableSticker drawableSticker = new DrawableSticker(list.get(position).getDrawable());

        sticker_view.addSticker(drawableSticker);

        drawables_sticker.add(drawableSticker);
        Constance.isStickerAvail = true;
        Constance.isStickerTouch = true;
        sticker_view.setLocked(false);

        ll_stickerlist.setVisibility(View.GONE);


    }

    private class AsynTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                final String[] imgPath = assetManager.list("stickers");
                sortArray(imgPath, "sticker_");

                for (int j = 0; j < imgPath.length; j++) {

                    InputStream is = assetManager.open("stickers/" + imgPath[j]);
                    Drawable drawable = Drawable.createFromStream(is, null);

                    StickerModel stickerModel = new StickerModel();
                    stickerModel.setDrawable(drawable);
                    list.add(stickerModel);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressDialog.dismiss();

            showStickerRow();
            stickerAdapter = new StickerColorAdapter(context, list);
            rv_stickerlist.setAdapter(stickerAdapter);

        }
    }

    private void showStickerRow() {

        ll_stickerlist.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        sticker_view.hideIcons(false);

        if (Constance.FONT_FLAG) {
            Constance.FONT_FLAG = false;

            DrawableSticker drawableSticker = Constance.TEXT_DRAWABLE;
            // drawableSticker.setTag("text");
            sticker_view.addSticker(drawableSticker);
            //  drawables_sticker.add(drawableSticker);
            //drawables_sticker_text.add(drawableSticker);
            Constance.isStickerAvail = true;
            Constance.isStickerTouch = true;
            sticker_view.setLocked(false);


        }

    }

    private void touchListener(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    if (Constance.isStickerAvail) {
                        // Log.e("ACTION_DOWN Share.isStickerTouch", Share.isStickerTouch + "");

                        if (Constance.isStickerTouch || !Constance.isStickerTouch) {
                            Constance.isStickerTouch = false;
                            sticker_view.setLocked(true);
                        }
                    }
                    // Toast.makeText(getContext(), "you just touch the screen :-)", Toast.LENGTH_SHORT).show();
                } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                    if (Constance.isStickerAvail) {

                        // Log.e("ACTION_UP Share.isStickerTouch", Share.isStickerTouch + "");
                        if (!Constance.isStickerTouch) {
                            Constance.isStickerTouch = true;
                            sticker_view.setLocked(false);
                        }
                    }
                } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                    if (Constance.isStickerAvail) {
                        if (!Constance.isStickerTouch || Constance.isStickerTouch) {
                            Log.e("image move", "sticker lock");
                            Constance.isStickerTouch = false;
                            sticker_view.setLocked(true);
                        }
                    }
                }
                return true;
            }
        });
    }

}
