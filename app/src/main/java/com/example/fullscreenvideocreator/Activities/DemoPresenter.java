package com.example.fullscreenvideocreator.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.fullscreenvideocreator.Models.Model_images;
import com.example.fullscreenvideocreator.R;
import com.example.fullscreenvideocreator.Utills.Constance;
import com.example.fullscreenvideocreator.UtillsNew.IDemoView;
import com.example.fullscreenvideocreator.UtillsNew.UriUtil;
import com.example.fullscreenvideocreator.widget.FilterItem;
import com.example.fullscreenvideocreator.widget.FilterType;
import com.example.fullscreenvideocreator.widget.MovieFilterView;
import com.example.fullscreenvideocreator.widget.MovieTransferView;
import com.example.fullscreenvideocreator.widget.TransferItem;
import com.hw.photomovie.PhotoMovie;
import com.hw.photomovie.PhotoMovieFactory;
import com.hw.photomovie.PhotoMoviePlayer;
import com.hw.photomovie.model.PhotoData;
import com.hw.photomovie.model.PhotoSource;
import com.hw.photomovie.model.SimplePhotoData;
import com.hw.photomovie.record.GLMovieRecorder;
import com.hw.photomovie.render.GLSurfaceMovieRenderer;
import com.hw.photomovie.render.GLTextureMovieRender;
import com.hw.photomovie.render.GLTextureView;
import com.hw.photomovie.timer.IMovieTimer;
import com.hw.photomovie.timer.MovieTimer;
import com.hw.photomovie.util.MLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.fullscreenvideocreator.Activities.DemoActivity.seekbar;
import static com.example.fullscreenvideocreator.Activities.DemoActivity.tvEndTime;
import static com.example.fullscreenvideocreator.Activities.DemoActivity.tvTime;

/**
 * Created by huangwei on 2018/9/9.
 */
public class DemoPresenter implements IMovieTimer.MovieListener, MovieFilterView.FilterCallback, MovieTransferView.TransferCallback {

    static IDemoView mDemoView;
    private static PhotoMovie mPhotoMovie;
    public PhotoMoviePlayer mPhotoMoviePlayer;
    public static GLSurfaceMovieRenderer mMovieRenderer;
    private Uri mMusicUri;
    Handler handler = new Handler();
    Runnable runnable;
    int time = 0;
    boolean abc = false;
    AlertDialog alertDialog;

    public void attachView(IDemoView demoView) {
        mDemoView = demoView;
        initFilters();
        initTransfers();
        initMoviePlayer();
    }

    private void initTransfers() {
        List<TransferItem> items = new LinkedList<TransferItem>();
        items.add(new TransferItem(R.drawable.ic_movie_transfer, "LeftRight", PhotoMovieFactory.PhotoMovieType.HORIZONTAL_TRANS));
        items.add(new TransferItem(R.drawable.ic_movie_transfer, "UpDown", PhotoMovieFactory.PhotoMovieType.VERTICAL_TRANS));
        items.add(new TransferItem(R.drawable.ic_movie_transfer, "Window", PhotoMovieFactory.PhotoMovieType.WINDOW));
        items.add(new TransferItem(R.drawable.ic_movie_transfer, "Gradient", PhotoMovieFactory.PhotoMovieType.GRADIENT));
        //  items.add(new TransferItem(R.drawable.ic_movie_transfer, "Tranlation", PhotoMovieFactory.PhotoMovieType.SCALE_TRANS));
        items.add(new TransferItem(R.drawable.ic_movie_transfer, "Thaw", PhotoMovieFactory.PhotoMovieType.THAW));
        items.add(new TransferItem(R.drawable.ic_movie_transfer, "Scale", PhotoMovieFactory.PhotoMovieType.SCALE));
        mDemoView.setTransfers(items);
    }

    private void initFilters() {
        List<FilterItem> items = new LinkedList<FilterItem>();
        items.add(new FilterItem(R.drawable.filter_default, "None", FilterType.NONE));
        items.add(new FilterItem(R.drawable.gray, "BlackWhite", FilterType.GRAY));
        items.add(new FilterItem(R.drawable.kuwahara, "Watercolour", FilterType.KUWAHARA));
        items.add(new FilterItem(R.drawable.snow, "Snow", FilterType.SNOW));
        items.add(new FilterItem(R.drawable.l1, "Lut_1", FilterType.LUT1));
        items.add(new FilterItem(R.drawable.cameo, "Cameo", FilterType.CAMEO));
        items.add(new FilterItem(R.drawable.l2, "Lut_2", FilterType.LUT2));
        items.add(new FilterItem(R.drawable.l3, "Lut_3", FilterType.LUT3));
        items.add(new FilterItem(R.drawable.l4, "Lut_4", FilterType.LUT4));
        items.add(new FilterItem(R.drawable.l5, "Lut_5", FilterType.LUT5));
        mDemoView.setFilters(items);
    }

    private void initMoviePlayer() {
        final GLTextureView glTextureView = mDemoView.getGLView();
        mMovieRenderer = new GLTextureMovieRender(glTextureView);

        mPhotoMoviePlayer = new PhotoMoviePlayer(mDemoView.getActivity().getApplicationContext());
        mPhotoMoviePlayer.setMovieRenderer(mMovieRenderer);
        mPhotoMoviePlayer.setMovieListener(this);
        mPhotoMoviePlayer.setLoop(false);

        runnable = new Runnable() {
            @Override
            public void run() {
                if (mPhotoMoviePlayer != null) {
                    int mCurrentPosition = Constance.elapsedTime;
                    seekbar.setProgress(mCurrentPosition);
                }
                handler.postDelayed(this, 500);
            }
        };

        mPhotoMoviePlayer.setOnPreparedListener(new PhotoMoviePlayer.OnPreparedListener() {
            @Override
            public void onPreparing(PhotoMoviePlayer moviePlayer, float progress) {
            }

            @Override
            public void onPrepared(PhotoMoviePlayer moviePlayer, int prepared, int total) {
                mDemoView.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPhotoMoviePlayer.start();
                    }
                });

            }

            @Override
            public void onError(PhotoMoviePlayer moviePlayer) {
                MLog.i("onPrepare", "onPrepare error");
            }
        });

        DemoActivity.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvTime.setText(convertFormat(progress));

                Constance.elapsedTime = progress;
                //mPhotoMovie.getSegmentPicker().getSegmentProgress(mPhotoMovie.getSegmentPicker().getCurrentSegment(progress), mPhotoMovie.getDuration());
                //  mPhotoMoviePlayer.seekTo(Constance.elapsedTime);
                //  mMovieRenderer.drawFrame(Constance.elapsedTime);
                //  onMovieUpdate(Constance.elapsedTime);
                Log.d("xxx", "Progress " + Constance.elapsedTime);
               /* mAnimator.setCurrentPlayTime(progress);
                Constance.elapsedTime=(int)mAnimator.getCurrentPlayTime();
                Log.d("xxx", "1: " +(int)mAnimator.getCurrentPlayTime());
                Log.d("xxx", "2: " + seekBar.getProgress());
                Log.d("xxx", "Constance.elapsedTime: " + Constance.elapsedTime);
                if (mPhotoMoviePlayer != null) {
                    //  mPhotoMoviePlayer.onMovieUpdate(Constance.elapsedTime);
                    seekBar.setProgress(Constance.elapsedTime);
                    mPhotoMoviePlayer.seekTo(Constance.elapsedTime);
                    mAnimator.setCurrentPlayTime(Constance.elapsedTime);
                    // Log.d("xxx", "progress: " + progress);
                }*/


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mPhotoMoviePlayer.pause();

              /*  mPhotoMoviePlayer.pause();
                Log.d("mPausedPlayTime","2 :");
                DemoActivity.playpausebtn.setImageResource(R.drawable.ic_play);
               */

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                Log.d("sdaaaaaaaa", "2 :" + seekBar.getProgress());
                time = seekBar.getProgress();
                DemoPresenter.this.onMovieUpdate(time);
                Constance.elapsedTime = time;
                DemoActivity.seekbar.setProgress(Constance.elapsedTime);
                abc = true;
                mPhotoMoviePlayer.start();
                DemoActivity.playpausebtn.setImageResource(R.drawable.ic_pause);

                MovieTimer.getStatus(true, seekBar.getProgress());

               /* mPhotoMoviePlayer.start();
                DemoActivity.playpausebtn.setImageResource(R.drawable.ic_pause);
              */
            }
        });

    }

    @SuppressLint("DefaultLocale")
    private String convertFormat(int duration) {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    public static void addWaterMark(int position) {
        Bitmap waterMark = BitmapFactory.decodeResource(mDemoView.getActivity().getResources(), position);
       // mMovieRenderer.setWaterMark(waterMark, new RectF(0, 0, 1042, 750), 0.8f);
       // mMovieRenderer.setWaterMark(waterMark, new RectF(0, 0, 1042, 960), 0.8f);
       // mMovieRenderer.setWaterMark(waterMark, new RectF(0, 0, 1042, 1360), 0.8f);
        mMovieRenderer.setWaterMark(waterMark, new RectF(0, 0, Constance.widthOfVideo, Constance.heightOfVideo), 0.8f);
        // mMovieRenderer.setWaterMark("Watermark",40, Color.argb(100,255,0,0),100,100);
    }


    public void playbtn() {
        if (mPhotoMoviePlayer == null) {
            Log.d("yyyyy", "playbtn_if");
            Log.d("nnnnnnnn", "playbtn_if");
            startPlay(Constance.finallistphotosource);
        } else {
            Log.d("yyyyy", "playbtn_else");
            Log.d("nnnnnnnn", "playbtn_else");
            mPhotoMoviePlayer.stop();
            // mPhotoMovie = PhotoMovieFactory.generatePhotoMovie(Constance.finallistphotosource, PhotoMovieFactory.PhotoMovieType.HORIZONTAL_TRANS);
            mPhotoMovie = PhotoMovieFactory.generatePhotoMovie(Constance.finallistphotosource, Constance.mMovieType);
            mPhotoMoviePlayer.setDataSource(mPhotoMovie);
            if (mMusicUri != null) {
                mPhotoMoviePlayer.setMusic(mDemoView.getActivity(), mMusicUri);
            } else {
                defaultMusic();
            }
            mPhotoMoviePlayer.setOnPreparedListener(new PhotoMoviePlayer.OnPreparedListener() {
                @Override
                public void onPreparing(PhotoMoviePlayer moviePlayer, float progress) {
                }

                @Override
                public void onPrepared(PhotoMoviePlayer moviePlayer, int prepared, int total) {
                    mDemoView.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPhotoMoviePlayer.start();

                        }
                    });
                }

                @Override
                public void onError(PhotoMoviePlayer moviePlayer) {
                    MLog.i("onPrepare", "onPrepare error");
                }
            });
            mPhotoMoviePlayer.prepare();
        }
    }

    public void defaultMusic() {
        if (Constance.CACHE_MUSIC_DIRECTORY.exists()) {
            Log.d("aaa", "Music dir not Created");
        } else {
            Constance.CACHE_MUSIC_DIRECTORY.mkdirs();
        }
        InputStream in = mDemoView.getActivity().getResources().openRawResource(R.raw.happy_music);

        File musicFile = new File(Constance.CACHE_MUSIC_DIRECTORY, "defaultmusic.mp3");
        try {
            try (OutputStream output = new FileOutputStream(musicFile)) {
                byte[] buffer = new byte[4 * 1024]; // or other buffer size
                int read;

                while (( read = in.read(buffer) ) != -1) {
                    output.write(buffer, 0, read);
                }

                output.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mPhotoMoviePlayer.setMusic(musicFile.getAbsolutePath());


    }



    private void startPlay(PhotoSource photoSource) {
        // mPhotoMovie = PhotoMovieFactory.generatePhotoMovie(photoSource, mMovieType);
        mPhotoMovie = PhotoMovieFactory.generatePhotoMovie(photoSource, Constance.mMovieType);
        mPhotoMoviePlayer.setDataSource(mPhotoMovie);
        if (mMusicUri != null) {
            mPhotoMoviePlayer.setMusic(mDemoView.getActivity(), mMusicUri);
        } else {
            defaultMusic();
        }
        mPhotoMoviePlayer.prepare();
    }

    public void detachView() {
        mDemoView = null;
    }

    @Override
    public void onFilterSelect(FilterItem item) {
        mMovieRenderer.setMovieFilter(item.initFilter());
    }


    @Override
    public void onMovieUpdate(int elapsedTime) {
        Log.d("sdgahdgsagda", "dhajdhgajghd" + elapsedTime);

        if (abc) {
            Constance.elapsedTime = time;
            // mPhotoMoviePlayer.seekTo(Constance.elapsedTime);
            //Log.d("sdgahdgsagda", "dhajdhgajghd");
            DemoActivity.seekbar.setProgress(time);
            abc = false;
            //onMovieUpdate(Constance.elapsedTime);
            // mPhotoMoviePlayer.mMovieTimer.mAnimator.setCurrentPlayTime(Constance.elapsedTime);
        } else {
            Constance.elapsedTime = elapsedTime;

            DemoActivity.seekbar.setProgress(elapsedTime);
        }
    }

    @Override
    public void onMovieStarted() {

        if (abc) {
            DemoActivity.seekbar.setProgress(time);
        } else {
            Log.d("yykkkyyy", "onMovieStarted");
            DemoActivity.seekbar.setMax(mPhotoMovie.getDuration());
            Log.d("bbbb", "startduration :-" + mPhotoMovie.getDuration());
            int duration = mPhotoMovie.getDuration();
            String sDuration = convertFormat(duration);
            tvEndTime.setText(sDuration);
            handler.postDelayed(runnable, 0);
        }


    }

    @Override
    public void onMoviedPaused() {

        Log.d("yyyyy", "onMoviedPaused");

    }

    @Override
    public void onMovieResumed() {
        Log.d("yyyyy", "onMovieResumed");
    }

    @Override
    public void onMovieEnd() {
        Log.d("yyyyy", "onMovieEnd");

        DemoActivity.playpausebtn.setImageResource(R.drawable.ic_play);
        mPhotoMoviePlayer.seekTo(0);
    }

    @Override
    public void onTransferSelect(TransferItem item) {
        // mMovieType = item.type;
        Constance.mMovieType = item.type;
        mPhotoMoviePlayer.stop();
        // mPhotoMovie = PhotoMovieFactory.generatePhotoMovie(mPhotoMovie.getPhotoSource(), mMovieType);
        mPhotoMovie = PhotoMovieFactory.generatePhotoMovie(mPhotoMovie.getPhotoSource(), Constance.mMovieType);
        mPhotoMoviePlayer.setDataSource(mPhotoMovie);
        if (mMusicUri != null) {
            mPhotoMoviePlayer.setMusic(mDemoView.getActivity(), mMusicUri);
        } else {
            defaultMusic();
        }
        mPhotoMoviePlayer.setOnPreparedListener(new PhotoMoviePlayer.OnPreparedListener() {
            @Override
            public void onPreparing(PhotoMoviePlayer moviePlayer, float progress) {
            }

            @Override
            public void onPrepared(PhotoMoviePlayer moviePlayer, int prepared, int total) {
                mDemoView.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPhotoMoviePlayer.start();
                        DemoActivity.playpausebtn.setImageResource(R.drawable.ic_pause);
                    }
                });
            }

            @Override
            public void onError(PhotoMoviePlayer moviePlayer) {
                MLog.i("onPrepare", "onPrepare error");
            }
        });
        mPhotoMoviePlayer.prepare();
    }


    public void setMusic(Uri uri) {
        mMusicUri = uri;
        mPhotoMoviePlayer.setMusic(mDemoView.getActivity(), uri);
    }
    public void setMenullyMusic(String path) {
       // mMusicUri = uri;
        mPhotoMoviePlayer.setMusic(path);
    }

    public void saveVideo() {
        mPhotoMoviePlayer.pause();


      /*  final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        View dialogView = LayoutInflater.from(context).inflate(R.layout.alertbox_design, null);

        //TextView tv_alert_rate = (TextView) dialogView.findViewById(R.id.tv_alert_rate);
        dialogBuilder.setView(dialogView);
        ProgressBar pb_alert = dialogView.findViewById(R.id.pb_alert);
        TextView tv_alert = dialogView.findViewById(R.id.tv_alert);
        AdView mAdView = dialogView.findViewById(R.id.adView);
        //Button alert_btn_ok = dialogView.findViewById(R.id.alert_btn_ok);
        LinearLayout facbook_ad_banner = dialogView.findViewById(R.id.facbook_ad_banner);

        if (Constance.adType.equals("Ad Mob")) {
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


        } else {
            mAdView.setVisibility(View.GONE);
            facbook_ad_banner.setVisibility(View.VISIBLE);
            com.facebook.ads.AdView adFaceView = new com.facebook.ads.AdView(context, context.getResources().getString(R.string.facebook_banner_id), AdSize.BANNER_HEIGHT_50);

            AdSettings.setDebugBuild(true);
            //AdSettings.addTestDevice("HASHED ID");
            // Find the Ad Container


            // Add the ad view to your activity layout
            facbook_ad_banner.addView(adFaceView);

            // Request an ad
            adFaceView.loadAd();

        }
        pb_alert.setMax(100);
        AlertDialog alertDialog;
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();*/
        final ProgressDialog dialog = new ProgressDialog(mDemoView.getActivity());
        dialog.setMessage("Please Wait Video Is Creating...");

        dialog.setMax(100);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.show();
        final long startRecodTime = System.currentTimeMillis();
        final GLMovieRecorder recorder = new GLMovieRecorder(mDemoView.getActivity());
        final File file = initVideoFile();
        Log.d("jjjjj", "file : " + file);

        GLTextureView glTextureView = mDemoView.getGLView();
        int bitrate = glTextureView.getWidth() * glTextureView.getHeight() > 1000 * 1500 ? 8000000 : 4000000;
        recorder.configOutput(glTextureView.getWidth(), glTextureView.getHeight(), bitrate, 30, 1, file.getAbsolutePath());
        // recorder.configOutput(glTextureView.setForeground(R.drawable.frame3), file.getAbsolutePath());
        //生成一个全新的MovieRender，不然与现有的GL环境不一致，相互干扰容易出问题
        PhotoMovie newPhotoMovie = PhotoMovieFactory.generatePhotoMovie(mPhotoMovie.getPhotoSource(), Constance.mMovieType);
        GLSurfaceMovieRenderer newMovieRenderer = new GLSurfaceMovieRenderer(mMovieRenderer);
        newMovieRenderer.setPhotoMovie(newPhotoMovie);
        String audioPath = null;
        Log.d("oooooo", "mMusicUri : " + mMusicUri);
        if (mMusicUri != null) {
            audioPath = UriUtil.getPath(mDemoView.getActivity(), mMusicUri);
            Log.d("oooooo", "mMusicUri_audioPath : " + audioPath);
            Log.d("oooooo", "mMusicUri : " + mMusicUri);

        } else {
            //File dir = new File(Environment.getExternalStorageDirectory(), Constance.CACHE_Music_DIRECTORY);
            File dir = Constance.CACHE_MUSIC_File;
            Log.d("musicdir", "aaaaaaaaaaaa: "+dir);

            if (dir.exists()) {
                audioPath = UriUtil.getPath(mDemoView.getActivity(), Uri.fromFile(dir));
                Log.d("oooooo", "filemusic_audioPath : " + audioPath);
                Log.d("oooooo", "filemusic : " + Uri.fromFile(dir));
            } else {
                Log.d("oooooo", "dir not found : ");
                //Toast.makeText(mDemoView.getActivity(),"dir not found ",Toast.LENGTH_LONG).show();
            }

        }
        if (!TextUtils.isEmpty(audioPath)) {
            if (Build.VERSION.SDK_INT < 18) {
                Toast.makeText(mDemoView.getActivity().getApplicationContext(), "Mix audio needs api18!", Toast.LENGTH_LONG).show();
            } else {
                recorder.setMusic(audioPath);
            }
        }
        recorder.setDataSource(newMovieRenderer);

        recorder.startRecord(new GLMovieRecorder.OnRecordListener() {
            @Override
            public void onRecordFinish(boolean success) {
                Log.d("check_alert", "startRecord");

                File outputFile = file;
                long recordEndTime = System.currentTimeMillis();
                MLog.i("Record", "record:" + ( recordEndTime - startRecodTime ));
              //  alertDialog.dismiss();
                dialog.dismiss();
                if (success) {
                    scanFile(mDemoView.getActivity(), Uri.fromFile(outputFile));

                    //Toast.makeText(mDemoView.getActivity().getApplicationContext(), "Video save to path:" + outputFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    Log.d("jjjjj", "outputFile.getAbsolutePath() : " + outputFile.getAbsolutePath());


                    Log.d("check_alert", "success completed");

                    Intent intent = new Intent(mDemoView.getActivity(), ActivityVideoPreview.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("Video_Path", outputFile.getAbsolutePath());
                    mDemoView.getActivity().startActivity(intent);
                    /*
                    mDemoView.getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

                    
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Intent.ACTION_VIEW);
                    String type = "video/*";
                    intent.setDataAndType(Uri.fromFile(outputFile), type);
                    mDemoView.getActivity().startActivity(intent);*/
                } else {
                    Toast.makeText(mDemoView.getActivity().getApplicationContext(), "com.hw.photomovie.record error!", Toast.LENGTH_LONG).show();
                }
                if (recorder.getAudioRecordException() != null) {
                    Toast.makeText(mDemoView.getActivity().getApplicationContext(), "record audio failed:" + recorder.getAudioRecordException().toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onRecordProgress(int recordedDuration, int totalDuration) {
                Log.d("check_alert", "onRecordProgress");
                dialog.setProgress((int) ( recordedDuration / (float) totalDuration * 100 ));
               // tv_alert.setText((int) ( recordedDuration / (float) totalDuration * 100 ) + "/100");
            }
        });

    }


    private File initVideoFile() {

        if (!Constance.FileDirectory.exists()) {
            Constance.FileDirectory.mkdirs();
        }
        if (!Constance.FileDirectory.exists()) {
            Constance.FileDirectory = mDemoView.getActivity().getCacheDir();
        }

        return new File(Constance.FileDirectory, String.format("Full_Screen_video_creator_%s.mp4",
                new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(System.currentTimeMillis())));
    }

    public void onPause() {
        Log.d("yyyyy", "onpause");
        mPhotoMoviePlayer.pause();

    }

    public void onResume() {
        Log.d("yyyyy", "onresume");
        DemoActivity.playVideoFromImages();
        playbtn();
        //  playpausebtn.setImageResource(R.drawable.ic_pause);
        DemoActivity.playpausebtn.setImageResource(R.drawable.ic_pause);
    }

    public void onPhotoPick(ArrayList<Model_images> photos) {
        List<PhotoData> photoDataList = new ArrayList<PhotoData>(photos.size());

        for (Model_images path : photos) {
            PhotoData photoData = new SimplePhotoData(mDemoView.getActivity(), path.getSingleimagepath(), PhotoData.STATE_LOCAL);
            photoDataList.add(photoData);
        }
        PhotoSource photoSource = new PhotoSource(photoDataList);
        Log.d("ccccccccccc", "" + photoDataList);
        Constance.finallistphotosource = photoSource;
    }
    private static void scanFile(Context context, Uri imageUri){
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(imageUri);
        context.sendBroadcast(scanIntent);

    }

}
