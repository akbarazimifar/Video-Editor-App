package com.example.fullscreenvideocreator.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fullscreenvideocreator.Adepters.AdapterFrameVideo;
import com.example.fullscreenvideocreator.Models.Model_images;
import com.example.fullscreenvideocreator.R;
import com.example.fullscreenvideocreator.Utills.Constance;
import com.example.fullscreenvideocreator.UtillsNew.IDemoView;
import com.example.fullscreenvideocreator.WaveSong.MusicListActivity;
import com.example.fullscreenvideocreator.widget.FilterItem;
import com.example.fullscreenvideocreator.widget.MovieFilterView;
import com.example.fullscreenvideocreator.widget.MovieTransferView;
import com.example.fullscreenvideocreator.widget.TransferItem;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.internal.Constants;
import com.hw.photomovie.PhotoMovieFactory;
import com.hw.photomovie.render.GLTextureView;
import com.hw.photomovie.util.AppResources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class DemoActivity extends AppCompatActivity implements IDemoView {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    static Context context;
    private static final int REQUEST_MUSIC = 234;
    private final int REQUEST_PICK_IMAGES = 100;
    private final int REQUEST_Edit_IMAGES = 101;
    private final int REQUEST_Music = 102;

    private static DemoPresenter mDemoPresenter = new DemoPresenter();
    private GLTextureView mGLTextureView;
    private MovieFilterView mFilterView;
    private MovieTransferView mTransferView;
    private List<FilterItem> mFilters;
    private List<TransferItem> mTransfers;
    public static ImageView playpausebtn;
    LinearLayout ll_animationtransefer, ll_menual_music,ll_previewvideo;
    ImageView iv_backarrow_editorimage, iv_previewvideo;
    static ImageView iv_Frame;
    ViewStub transfer_stub, filter_stub;
    RecyclerView rv_frame;
    RelativeLayout rl_frameview;

    public static SeekBar seekbar, seekbar_with_interval;
    static TextView tvEndTime, tvTime;
    TextView tv_duration1, tv_duration_1_5, tv_duration_2, tv_duration_2_5, tv_duration_3, tv_duration_3_5, tv_duration_4;
    RelativeLayout rl_Duration;
    public static AdapterFrameVideo adapterFrameVideo;
    AdView mAdView;
    LinearLayout facbook_ad_banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppResources.getInstance().init(getResources());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        context = DemoActivity.this;

        bindview();
        mAdView = findViewById(R.id.adView);
        facbook_ad_banner = findViewById(R.id.facbook_ad_banner);
        ll_menual_music = findViewById(R.id.ll_menual_music);
        //foradvertise();

        if (Constance.adType.equals("Ad Mob")) {
            foradvertise();

        } else {
            facebookAd();
        }
        calculationForVideoHeight();
        ll_menual_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] mSongs = new int[]{R.raw.tunejonakaha, R.raw.kuch_is_tarah_atif_aslam_sad_love_song, R.raw.happy_music, R.raw.meherbani, R.raw.party_on_my_mind_ft_kk};
                final String[] mSongsName = new String[]{"tune jo na kaha", "kuch_is_tarah_atif_aslam_sad_love_song", "happy_music", "meherbani", "party_on_my_mind_ft_kk"};
                for (int i = 0; i < mSongs.length; i++) {
                    try {
                      //  String path = Environment.getExternalStorageDirectory() + "/RawMusic";
                        String path = Constance.rawMusicDirectory;
                        Log.d("pathaaaaaaaa", "" + path);
                        File dir = new File(path);
                        if (dir.mkdirs() || dir.isDirectory()) {
                            // String str_song_name = i + ".mp3";
                            String str_song_name = mSongsName[i] + ".mp3";
                            CopyRAWtoSDCard(mSongs[i], path + File.separator + str_song_name);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Intent intentMusic1 = new Intent(context, ActivityRawMusic.class);
                //intentMusic.putExtra("checkForMusic", true);
                startActivityForResult(intentMusic1, REQUEST_Music);

            }
        });

        mDemoPresenter.attachView(this);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_frame.setLayoutManager(layoutManager);

        ll_previewvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDemoPresenter.saveVideo();
            }
        });

        playpausebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDemoPresenter.mPhotoMoviePlayer.isPlaying()) {
                    mDemoPresenter.mPhotoMoviePlayer.pause();
                    Log.d("vvvvvv", "pause");

                    playpausebtn.setImageResource(R.drawable.ic_play);

                } else {
                    Log.d("vvvvvv", "play");

                    mDemoPresenter.mPhotoMoviePlayer.isPlaying();
                    mDemoPresenter.mPhotoMoviePlayer.start();
                    playpausebtn.setImageResource(R.drawable.ic_pause);
                }

                //Toast.makeText(getApplicationContext(),"helllooo",Toast.LENGTH_LONG).show();
            }
        });
        mTransferViewDailogShow();
        mTransferView.show();


    }

    private void CopyRAWtoSDCard(int id, String path) throws IOException {
        InputStream in = getResources().openRawResource(id);
        FileOutputStream out = new FileOutputStream(path);
        byte[] buff = new byte[1024];
        int read = 0;
        try {
            while (( read = in.read(buff) ) > 0) {
                out.write(buff, 0, read);
            }
        } finally {
            in.close();
            out.close();
        }
    }

    public void mTransferViewDailogShow() {
        if (mTransferView == null) {

            mTransferView = (MovieTransferView) transfer_stub.inflate();
            mTransferView.setVisibility(View.GONE);
            mTransferView.setItemList(mTransfers);
            mTransferView.setTransferCallback(mDemoPresenter);
        }
        // mBottomView.setVisibility(View.GONE);
        mTransferView.show();
    }

    public void mFiltersDailogShow() {
        if (mFilterView == null) {
            mFilterView = (MovieFilterView) filter_stub.inflate();
            mFilterView.setVisibility(View.GONE);
            mFilterView.setItemList(mFilters);
            mFilterView.setFilterCallback(mDemoPresenter);
        }
        //.setVisibility(View.GONE);
        mFilterView.show();
    }


    @Override
    public GLTextureView getGLView() {
        return mGLTextureView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDemoPresenter.detachView();
    }


    @Override
    public void setFilters(List<FilterItem> filters) {
        mFilters = filters;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void setTransfers(List<TransferItem> items) {
        mTransfers = items;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDemoPresenter.onPause();
        playpausebtn.setImageResource(R.drawable.ic_play);
        mGLTextureView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDemoPresenter.onResume();
        mGLTextureView.onResume();
    }

    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.iv_backarrow_editorimage:
                onBackPressed();

                break;
            case R.id.ll_addpic:
                Intent intent = new Intent(this, ActivitySelectImages.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                intent.putExtra("checkForResult", true);
                startActivityForResult(intent, REQUEST_PICK_IMAGES);

                break;
            case R.id.ll_editpic:
                Intent intentedit = new Intent(this, ActivityListSelectedPhotos.class);
                intentedit.putExtra("checkForEditResult", true);
                startActivityForResult(intentedit, REQUEST_Edit_IMAGES);

                break;
            case R.id.ll_animationtransefer:
                rl_frameview.setVisibility(View.GONE);
                rl_Duration.setVisibility(View.GONE);

                if (mFilterView != null && mFilterView.getVisibility() == View.VISIBLE) {
                    mFilterView.hide();
                }

                mTransferViewDailogShow();

                break;
            case R.id.ll_filter:
                rl_frameview.setVisibility(View.GONE);
                rl_Duration.setVisibility(View.GONE);

                if (mTransferView != null && mTransferView.getVisibility() == View.VISIBLE) {
                    mTransferView.hide();
                }
                mFiltersDailogShow();
                break;
            case R.id.ll_music:
                Intent intentMusic = new Intent(this, MusicListActivity.class);
                //intentMusic.putExtra("checkForMusic", true);
                startActivityForResult(intentMusic, REQUEST_Music);
                break;
            case R.id.ll_Frame:
                transfer_stub.setVisibility(View.GONE);
                filter_stub.setVisibility(View.GONE);
                rl_frameview.setVisibility(View.VISIBLE);
                rl_Duration.setVisibility(View.GONE);
                rl_frameview.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_bottom));

                adapterFrameVideo = new AdapterFrameVideo(context);
                rv_frame.setAdapter(adapterFrameVideo);
                break;
            case R.id.ll_duration:
                showDuration();
                break;
        }
    }

    public void showDuration()
    {
        rl_Duration.setVisibility(View.VISIBLE);
        rl_Duration.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_bottom));

        transfer_stub.setVisibility(View.GONE);
        filter_stub.setVisibility(View.GONE);
        rl_frameview.setVisibility(View.GONE);

        tv_duration_2.setTextColor(context.getResources().getColor(R.color.colorPink));
        tv_duration1.setTextColor(context.getResources().getColor(R.color.colorWhite));
        tv_duration_1_5.setTextColor(context.getResources().getColor(R.color.colorWhite));
        tv_duration_2_5.setTextColor(context.getResources().getColor(R.color.colorWhite));
        tv_duration_3.setTextColor(context.getResources().getColor(R.color.colorWhite));
        tv_duration_3_5.setTextColor(context.getResources().getColor(R.color.colorWhite));
        tv_duration_4.setTextColor(context.getResources().getColor(R.color.colorWhite));

        seekbar_with_interval.setProgress(Constance.durationseek_progress);
        seekbar_with_interval.setMax(6);
        seekbar_with_interval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            //   int seek_progress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Constance.durationseek_progress = progress;
                //Toast.makeText(context, "Value: "+progress, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (Constance.durationseek_progress == 0) {
                    tv_duration1.setTextColor(context.getResources().getColor(R.color.colorPink));
                    tv_duration_2.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_1_5.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_2_5.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_3.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_3_5.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_4.setTextColor(context.getResources().getColor(R.color.colorWhite));

                    Constance.changeDuration = 500;
                    Constance.changeDuration_Windowsegment = -100;
                    Constance.ThawSegmentduration = 1000;
                    Constance.HorizontalTrans = 100;
                    Constance.END_GAUSSIANBLUR_DURATION = 400;
                    // Toast.makeText(context, "Value: "+seek_progress, Toast.LENGTH_SHORT).show();
                } else if (Constance.durationseek_progress == 1) {
                    tv_duration_1_5.setTextColor(context.getResources().getColor(R.color.colorPink));
                    tv_duration1.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_2.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_2_5.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_3.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_3_5.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_4.setTextColor(context.getResources().getColor(R.color.colorWhite));

                    Constance.changeDuration = 800;
                    Constance.changeDuration_Windowsegment = 200;
                    Constance.ThawSegmentduration = 1400;
                    Constance.HorizontalTrans = 500;
                    Constance.END_GAUSSIANBLUR_DURATION = 1000;
                    // Toast.makeText(context, "Value: "+seek_progress, Toast.LENGTH_SHORT).show();
                } else if (Constance.durationseek_progress == 2) {
                    tv_duration_2.setTextColor(context.getResources().getColor(R.color.colorPink));
                    tv_duration1.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_1_5.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_2_5.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_3.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_3_5.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_4.setTextColor(context.getResources().getColor(R.color.colorWhite));

                    Constance.changeDuration = 1000;
                    Constance.changeDuration_Windowsegment = 500;
                    Constance.ThawSegmentduration = 1800;
                    Constance.HorizontalTrans = 800;
                    Constance.END_GAUSSIANBLUR_DURATION = 1500;
                    //  Toast.makeText(context, "Value: "+seek_progress, Toast.LENGTH_SHORT).show();
                } else if (Constance.durationseek_progress == 3) {
                    tv_duration_2_5.setTextColor(context.getResources().getColor(R.color.colorPink));
                    tv_duration1.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_2.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_1_5.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_3.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_3_5.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_4.setTextColor(context.getResources().getColor(R.color.colorWhite));


                    Constance.changeDuration = 1600;
                    Constance.changeDuration_Windowsegment = 1200;
                    Constance.ThawSegmentduration = 2200;
                    Constance.HorizontalTrans = 1200;

                    Constance.END_GAUSSIANBLUR_DURATION = 2100;
                    //Toast.makeText(context, "Value: "+seek_progress, Toast.LENGTH_SHORT).show();
                } else if (Constance.durationseek_progress == 4) {
                    tv_duration_3.setTextColor(context.getResources().getColor(R.color.colorPink));
                    tv_duration1.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_2.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_2_5.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_1_5.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_3_5.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_4.setTextColor(context.getResources().getColor(R.color.colorWhite));

                    Constance.changeDuration = 1800;
                    Constance.changeDuration_Windowsegment = 1700;
                    Constance.ThawSegmentduration = 2600;
                    Constance.HorizontalTrans = 1600;
                    Constance.END_GAUSSIANBLUR_DURATION = 2700;

                    //  Toast.makeText(context, "Value: "+seek_progress, Toast.LENGTH_SHORT).show();
                } else if (Constance.durationseek_progress == 5) {
                    tv_duration_3_5.setTextColor(context.getResources().getColor(R.color.colorPink));
                    tv_duration1.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_2.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_2_5.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_3.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_1_5.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_4.setTextColor(context.getResources().getColor(R.color.colorWhite));

                    Constance.changeDuration = 2000;
                    Constance.changeDuration_Windowsegment = 2200;
                    Constance.ThawSegmentduration = 3000;
                    Constance.HorizontalTrans = 2000;
                    Constance.END_GAUSSIANBLUR_DURATION = 3000;


                    //  Toast.makeText(context, "Value: "+seek_progress, Toast.LENGTH_SHORT).show();
                } else if (Constance.durationseek_progress == 6) {
                    tv_duration_4.setTextColor(context.getResources().getColor(R.color.colorPink));
                    tv_duration1.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_2.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_2_5.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_3.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_3_5.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    tv_duration_1_5.setTextColor(context.getResources().getColor(R.color.colorWhite));

                    Constance.changeDuration = 2100;
                    Constance.changeDuration_Windowsegment = 2700;
                    Constance.ThawSegmentduration = 3400;
                    Constance.HorizontalTrans = 2400;
                    Constance.END_GAUSSIANBLUR_DURATION = 3500;

                    //   Toast.makeText(context, "Value: "+seek_progress, Toast.LENGTH_SHORT).show();
                }
                PhotoMovieFactory.duration = Constance.HorizontalTrans;
                PhotoMovieFactory.ThawSegmentduration = Constance.ThawSegmentduration;
                PhotoMovieFactory.HorizontalTrans = Constance.HorizontalTrans;
                PhotoMovieFactory.END_GAUSSIANBLUR_DURATION = Constance.END_GAUSSIANBLUR_DURATION;
                PhotoMovieFactory.Windowsegmentduration = Constance.changeDuration_Windowsegment;
                mDemoPresenter.playbtn();
                playpausebtn.setImageResource(R.drawable.ic_pause);
            }
        });
    }

    public void bindview() {
        mGLTextureView = findViewById(R.id.gl_texture);
        ll_animationtransefer = findViewById(R.id.ll_animationtransefer);
        iv_backarrow_editorimage = findViewById(R.id.iv_backarrow_editorimage);
        playpausebtn = findViewById(R.id.playpausebtn);
        iv_previewvideo = findViewById(R.id.iv_previewvideo);
        ll_previewvideo = findViewById(R.id.ll_previewvideo);
        transfer_stub = findViewById(R.id.movie_menu_transfer_stub);
        filter_stub = findViewById(R.id.movie_menu_filter_stub);
        rl_Duration = findViewById(R.id.rl_Duration);

        seekbar = findViewById(R.id.seekbar);
        seekbar_with_interval = findViewById(R.id.seekbar_with_interval);
        tvEndTime = findViewById(R.id.tvEndTime);
        tvTime = findViewById(R.id.tvTime);
        tv_duration1 = findViewById(R.id.tv_duration1);
        tv_duration_1_5 = findViewById(R.id.tv_duration_1_5);
        tv_duration_2 = findViewById(R.id.tv_duration_2);
        tv_duration_2_5 = findViewById(R.id.tv_duration_2_5);
        tv_duration_3 = findViewById(R.id.tv_duration_3);
        tv_duration_3_5 = findViewById(R.id.tv_duration_3_5);
        tv_duration_4 = findViewById(R.id.tv_duration_4);

        iv_Frame = findViewById(R.id.iv_Frame);
        rv_frame = findViewById(R.id.rv_frame);
        rl_frameview = findViewById(R.id.rl_frameview);
        // btn_getsagment = findViewById(R.id.btn_getsagment);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_MUSIC) {
            Uri uri = data.getData();
            Log.d("asssssssssssaa", "ddddd" + uri);
            Uri myUri = Uri.parse("/storage/emulated/0/hepi/.temp_audio/temp.mp3");
            Log.d("asssssssssssaa", "ddddd" + myUri);
            mDemoPresenter.setMusic(uri);
            // mDemoPresenter.setMusic(myUri);
            Log.d("asssssssssssaa", "" + uri);
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_PICK_IMAGES) {

            playVideoFromImages();
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_Edit_IMAGES) {

            playVideoFromImages();
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_Music) {

            Environment.getExternalStoragePublicDirectory("Full Screen Video Creator");
            // imageUri = Uri.fromFile(new File(filepath));
            Uri myUri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory("Full Screen Video Creator" + "/" + ".temp_audio" + "/" + "temp.mp3").getPath()));
            //  Uri myUri=Uri.parse("android.resource://com.example.videocreator/" + R.raw.fluteonmyway);
            mDemoPresenter.setMusic(myUri);

            Log.d("sdssdfsjds", "sdhjdhasjdh" + myUri);


        }
    }

    public static void playVideoFromImages() {
        Log.d("rrrrrr", "demo " + Constance.selectedimages.size());
        ArrayList<Model_images> photos = Constance.selectedimages;
        mDemoPresenter.onPhotoPick(photos);
      //  mDemoPresenter.playbtn();
      //  playpausebtn.setImageResource(R.drawable.ic_pause);
    }


    @Override
    public void onBackPressed() {
        leavethispagedailog();

    }

    public void leavethispagedailog() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dailog_leavepage_videocreate);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // lp.windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setAttributes(lp);
        TextView tv_yes, tv_no;
        AdView adView_leavepage;
        tv_yes = dialog.findViewById(R.id.tv_yes);
        tv_no = dialog.findViewById(R.id.tv_no);
        // adView_leavepage = dialog.findViewById(R.id.adView_leavepage);
        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ActivityHome.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                dialog.dismiss();


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

    public void foradvertise() {
        //facbook_ad_banner.setVisibility(View.GONE);
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
    public void calculationForVideoHeight() {

        //linearlayout view
        ViewTreeObserver vto = mGLTextureView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mGLTextureView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mGLTextureView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                Constance.widthOfVideo = mGLTextureView.getMeasuredWidth();//1080 horizontalview
                Constance.heightOfVideo = mGLTextureView.getMeasuredHeight();//236
                // Log.d("widthOfvideo","widthOfvideo:"+Constance.widthOfvideo );
                //Log.d("widthOfvideo","heightOfvideo:"+Constance.heightOfvideo );

            }
        });

    }


}
