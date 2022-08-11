package com.example.fullscreenvideocreator.WaveSong;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fullscreenvideocreator.R;
import com.example.fullscreenvideocreator.WaveSong.audiocutter.soundfile.CheapSoundFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MusicListActivity extends AppCompatActivity implements MarkerView.MarkerListener, WaveformView.WaveformListener {


    private boolean isFromItemClick = false;
    boolean isPlaying = false;
    MusicAdapter mAdapter;
    String mArtist;
    private boolean mCanSeekAccurately;
    private float mDensity;
    private MarkerView mEndMarker;
    private int mEndPos;
    private TextView mEndText;
    private boolean mEndVisible;
    private String mExtension;
    ImageButton mFfwdButton;
    ImageView iv_wave_done;

    private View.OnClickListener mFfwdListener = new View.OnClickListener() {
        @Override
        public void onClick(View sender) {
            if (mIsPlaying) {
                int newPos = mPlayer.getCurrentPosition() + 5000;
                if (newPos > mPlayEndMsec) {
                    newPos = mPlayEndMsec;
                }
                mPlayer.seekTo(newPos);
            } else {
                mEndMarker.requestFocus();
                markerFocus(mEndMarker);
            }
        }
    };
    private File mFile;
    private String mFilename = "record";
    private int mFlingVelocity;
    private Handler mHandler;
    private boolean mIsPlaying;
    private boolean mKeyDown;
    private int mLastDisplayedEndPos;
    private int mLastDisplayedStartPos;
    private boolean mLoadingKeepGoing;
    private long mLoadingLastUpdateTime;
    int mMarkerBottomOffset;
    private int mMarkerLeftInset;
    private int mMarkerRightInset;
    private int mMarkerTopOffset;
    private int mMaxPos;
    private ArrayList<MusicData> mMusicDatas;
    private RecyclerView mMusicList;
    private int mOffset;
    private int mOffsetGoal;
    private ImageButton mPlayButton;
    private int mPlayEndMsec;
    private View.OnClickListener mPlayListener = new View.OnClickListener() {
        @Override
        public void onClick(View sender) {
            onPlay(mStartPos);
        }
    };
    private int mPlayStartMsec;
    private int mPlayStartOffset;
    private MediaPlayer mPlayer;
    private ProgressDialog mProgressDialog;
    private String mRecordingFilename;
    private Uri mRecordingUri;
    ImageButton mRewindButton;
    private View.OnClickListener mRewindListener = new View.OnClickListener() {
        @Override
        public void onClick(View sender) {
            if (mIsPlaying) {
                int newPos = mPlayer.getCurrentPosition() - 5000;
                if (newPos < mPlayStartMsec) {
                    newPos = mPlayStartMsec;
                }
                mPlayer.seekTo(newPos);
            } else {
                mStartMarker.requestFocus();
                markerFocus(mStartMarker);
            }
        }
    };
    private CheapSoundFile mSoundFile;
    private MarkerView mStartMarker;
    private int mStartPos;
    private TextView mStartText;
    private boolean mStartVisible;
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mStartText.hasFocus()) {
                try {
                    mStartPos = mWaveformView.secondsToPixels(Double.parseDouble(mStartText.getText().toString()));
                    updateDisplay();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            if (mEndText.hasFocus()) {
                try {
                    mEndPos = mWaveformView.secondsToPixels(Double.parseDouble(mEndText.getText().toString()));
                    updateDisplay();
                } catch (NumberFormatException e2) {
                    e2.printStackTrace();
                }
            }
        }
    };
    private Runnable mTimerRunnable = new Runnable() {
        public void run() {
            if (!(mStartPos == mLastDisplayedStartPos || mStartText.hasFocus())) {
                mStartText.setText(formatTime(mStartPos));
                mLastDisplayedStartPos = mStartPos;
            }
            if (!(mEndPos == mLastDisplayedEndPos || mEndText.hasFocus())) {
                mEndText.setText(formatTime(mEndPos));
                mLastDisplayedEndPos = mEndPos;
            }
            mHandler.postDelayed(mTimerRunnable, 100);
        }
    };
    String mTitle;
    private boolean mTouchDragging;
    private int mTouchInitialEndPos;
    private int mTouchInitialOffset;
    private int mTouchInitialStartPos;
    private float mTouchStart;
    private long mWaveformTouchStartMsec;
    private WaveformView mWaveformView;
    private int mWidth;
    private MusicData selectedMusicData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecordingFilename = null;
        mRecordingUri = null;
        mPlayer = null;
        mIsPlaying = false;
        mSoundFile = null;
        mKeyDown = false;
        mHandler = new Handler();
        loadGui();
        init();

        iv_wave_done=findViewById(R.id.iv_wave_done);
        iv_wave_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSave();
            }
        });
        mHandler.postDelayed(mTimerRunnable, 100);

    }

    private void loadGui() {
        setContentView(R.layout.activity_music_list);
        bindView();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mDensity = metrics.density;

        mMarkerLeftInset = (int) (46.0f * mDensity);
        mMarkerRightInset = (int) (48.0f * mDensity);
        mMarkerTopOffset = (int) (mDensity * 10.0f);
        mMarkerBottomOffset = (int) (mDensity * 10.0f);

        mStartText = findViewById(R.id.starttext);
        mStartText.addTextChangedListener(mTextWatcher);

        mEndText = findViewById(R.id.endtext);
        mEndText.addTextChangedListener(mTextWatcher);

        mPlayButton = findViewById(R.id.play);
        mPlayButton.setOnClickListener(mPlayListener);

        mRewindButton = findViewById(R.id.rew);
        mRewindButton.setOnClickListener(mRewindListener);

        mFfwdButton = findViewById(R.id.ffwd);
        mFfwdButton.setOnClickListener(mFfwdListener);

        enableDisableButtons();
        mWaveformView = findViewById(R.id.waveform);
        mWaveformView.setListener(this);

        mMaxPos = 0;
        mLastDisplayedStartPos = -1;
        mLastDisplayedEndPos = -1;

        if (mSoundFile != null) {
            mWaveformView.setSoundFile(mSoundFile);
            mWaveformView.recomputeHeights(mDensity);
            mMaxPos = mWaveformView.maxPos();
        }

        mStartMarker = findViewById(R.id.startmarker);
        mStartMarker.setListener(this);
        mStartMarker.setAlpha(1f);
        mStartMarker.setFocusable(true);
        mStartMarker.setFocusableInTouchMode(true);
        mStartVisible = true;

        mEndMarker = findViewById(R.id.endmarker);
        mEndMarker.setListener(this);
        mEndMarker.setAlpha(1f);
        mEndMarker.setFocusable(true);
        mEndMarker.setFocusableInTouchMode(true);
        mEndVisible = true;

        updateDisplay();
    }

    private void bindView() {
        mMusicList = findViewById(R.id.rvMusicList);
    }

    private void setUpRecyclerView() {
        mAdapter = new MusicAdapter(mMusicDatas);
        mMusicList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        mMusicList.setItemAnimator(new DefaultItemAnimator());
        mMusicList.setAdapter(mAdapter);
    }

    private boolean isAudioFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        return path.endsWith(".mp3");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (TextUtils.isEmpty(mFilename) || !mFilename.equals("record")) {
            getMenuInflater().inflate(R.menu.menu_selection, menu);
            menu.removeItem(R.id.menu_clear);
        } else {
            menu.clear();
        }
        return super.onCreateOptionsMenu(menu);
    }




    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {

        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mPlayer = null;
        if (mRecordingFilename != null) {
            try {
                if (!new File(mRecordingFilename).delete()) {
                    showFinalAlert(new Exception(), R.string.delete_tmp_error);
                }
                getContentResolver().delete(mRecordingUri, null, null);
            } catch (SecurityException e) {
                showFinalAlert(e, R.string.delete_tmp_error);
            }
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_done:
                onSave();
                //MyApplication.getInstance().setMusicData(selectedMusicData);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            onPlay(mStartPos);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void init() {
        new LoadMusics().execute();
     /*   setSupportActionBar(toolbar);
        new LoadMusics().execute();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }*/
    }



    @SuppressLint("StaticFieldLeak")
    public class LoadMusics extends AsyncTask<Void, Void, Void> {
        ProgressDialog pDialog;

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MusicListActivity.this);
            pDialog.setTitle("Please wait");
            pDialog.setMessage("Loading music...");
            pDialog.show();
        }

        @Override
        public Void doInBackground(Void... paramVarArgs) {
           mMusicDatas = getMusicFiles();

            Log.d("checkmusic","getmusicfile");

            if (mMusicDatas.size() > 0) {
                selectedMusicData = mMusicDatas.get(0);
                mFilename = selectedMusicData.getTrack_data();
                Log.d("checkmusic","if");

            } else {
                mFilename = "record";
                Log.d("checkmusic","else");
            }
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            if (!mFilename.equals("record")) {
                setUpRecyclerView();
                loadFromFile();
                supportInvalidateOptionsMenu();
            } else if (mMusicDatas.size() > 0) {
                Toast.makeText(getApplicationContext(), "No Music found in device\nPlease add music in sdCard", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.Holder> {
        SparseBooleanArray booleanArray = new SparseBooleanArray();
        int mSelectedChoice = 0;
        private ArrayList<MusicData> musicDatas;

        class Holder extends RecyclerView.ViewHolder {
            CheckBox radioMusicName;
            TextView tv_song_name;

            Holder(View v) {
                super(v);
                radioMusicName = v.findViewById(R.id.radioMusicName);
                tv_song_name = v.findViewById(R.id.tv_song_name);
            }
        }

        MusicAdapter(ArrayList<MusicData> mMusicDatas) {
            musicDatas = mMusicDatas;
            booleanArray.put(0, true);
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int paramInt) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.items_music, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final Holder holder, @SuppressLint("RecyclerView") final int pos) {
            holder.tv_song_name.setText(musicDatas.get(pos).track_displayName);
            holder.radioMusicName.setChecked(booleanArray.get(pos, false));
            holder.radioMusicName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    booleanArray.clear();
                    booleanArray.put(pos, true);
                    onPlay(-1);
                    playMusic(pos);
                    isFromItemClick = true;
                    notifyDataSetChanged();

                }
            });
            holder.radioMusicName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        holder.tv_song_name.setSelected(true);

                    }
                    else {
                        holder.tv_song_name.setSelected(false);

                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return musicDatas.size();
        }

        void playMusic(int pos) {
            if (mSelectedChoice != pos) {
                selectedMusicData = mMusicDatas.get(pos);
                mFilename = selectedMusicData.getTrack_data();
                loadFromFile();
            }
            mSelectedChoice = pos;
        }
    }

    @Override
    public void markerDraw() {

    }

    @Override
    public void markerEnter(MarkerView markerView) {

    }

    @Override
    public void markerFocus(MarkerView marker) {
        mKeyDown = false;
        if (marker == mStartMarker) {
            setOffsetGoalStartNoUpdate();
        } else {
            setOffsetGoalEndNoUpdate();
        }
        mHandler.postDelayed(new Runnable() {
            public void run() {
                updateDisplay();
            }
        }, 100);

    }

    @Override
    public void markerKeyUp() {
        mKeyDown = false;
        updateDisplay();
    }

    @Override
    public void markerLeft(MarkerView marker, int velocity) {
        mKeyDown = true;
        if (marker == mStartMarker) {
            int saveStart = mStartPos;
            mStartPos = trap(mStartPos - velocity);
            mEndPos = trap(mEndPos - (saveStart - mStartPos));
            setOffsetGoalStart();
        }
        if (marker == mEndMarker) {
            if (mEndPos == mStartPos) {
                mStartPos = trap(mStartPos - velocity);
                mEndPos = mStartPos;
            } else {
                mEndPos = trap(mEndPos - velocity);
            }
            setOffsetGoalEnd();
        }
        updateDisplay();

    }

    @Override
    public void markerRight(MarkerView marker, int velocity) {
        mKeyDown = true;

        if (marker == mStartMarker) {
            int saveStart = mStartPos;
            mStartPos += velocity;
            if (mStartPos > mMaxPos) {
                mStartPos = mMaxPos;
            }
            mEndPos += mStartPos - saveStart;
            if (mEndPos > mMaxPos) {
                mEndPos = mMaxPos;
            }
            setOffsetGoalStart();
        }
        if (marker == mEndMarker) {
            mEndPos += velocity;
            if (mEndPos > mMaxPos) {
                mEndPos = mMaxPos;
            }
            setOffsetGoalEnd();
        }
        updateDisplay();

    }

    @Override
    public void markerTouchEnd(MarkerView marker) {
        mTouchDragging = false;

        if (marker == mStartMarker) {
            setOffsetGoalStart();
        } else {
            setOffsetGoalEnd();
        }
    }

    @Override
    public void markerTouchMove(MarkerView marker, float f) {
        float delta = f - mTouchStart;

        if (marker == mStartMarker) {
            mStartPos = trap((int) (mTouchInitialStartPos + delta));
            mEndPos = trap((int) (mTouchInitialEndPos + delta));
        } else {
            mEndPos = trap((int) (mTouchInitialEndPos + delta));
            if (mEndPos < mStartPos) {
                mEndPos = mStartPos;
            }
        }
        updateDisplay();
    }

    @Override
    public void markerTouchStart(MarkerView markerView, float f) {
        mTouchDragging = true;
        mTouchStart = f;
        mTouchInitialStartPos = mStartPos;
        mTouchInitialEndPos = mEndPos;
    }

    @Override
    public void waveformTouchStart(float x) {
        mTouchDragging = true;
        mTouchStart = x;
        mTouchInitialOffset = mOffset;
        mFlingVelocity = 0;
        mWaveformTouchStartMsec = System.currentTimeMillis();
    }

    @Override
    public void waveformTouchMove(float x) {
        mOffset = trap((int) (mTouchInitialOffset + (mTouchStart - x)));
        updateDisplay();
    }

    @Override
    public void waveformTouchEnd() {
        mTouchDragging = false;
        mOffsetGoal = mOffset;

        if ((System.currentTimeMillis() - mWaveformTouchStartMsec) < 300) {
            if (mIsPlaying) {
                int seekMsec = mWaveformView.pixelsToMillisecs(
                        (int) (mTouchStart + mOffset));
                if (seekMsec >= mPlayStartMsec &&
                        seekMsec < mPlayEndMsec) {
                    mPlayer.seekTo(seekMsec);
                } else {
                    handlePause();
                }
            } else {
                onPlay((int) (mTouchStart + mOffset));
            }
        }
    }

    @Override
    public void waveformFling(float x) {
        mTouchDragging = false;
        mOffsetGoal = mOffset;
        mFlingVelocity = (int) (-x);
        updateDisplay();
    }

    @Override
    public void waveformDraw() {
        mWidth = mWaveformView.getMeasuredWidth();
        if (mOffsetGoal != mOffset && !mKeyDown) {
            updateDisplay();
        } else if (mIsPlaying) {
            updateDisplay();
        } else if (mFlingVelocity != 0) {
            updateDisplay();
        }

    }

    private ArrayList<MusicData> getMusicFiles() {

        ArrayList<MusicData> mMusicDatas = new ArrayList<>();
        //https://riptutorial.com/android/example/23916/fetch-audio-mp3-files-from-specific-folder-of-device-or-fetch-all-files
        String[] selectionArgs=new String[]{Environment.getExternalStorageDirectory()+"/PhotoVideoMaker/"};
        @SuppressLint("Recycle")
     Cursor mCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "title", "_data", "_display_name", "duration"}, "is_music != 0", null, "title ASC");
      // Cursor mCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "title", "_data", "_display_name", "duration"}, null, null, "title ASC");
       // Cursor mCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "title", "_data", "_display_name", "duration"}, MediaStore.Audio.Media.DATA + " like ? ", new String[]{"%Video Creator%"}, "title ASC");
        assert mCursor != null;
        int trackId = mCursor.getColumnIndex("_id");
        Log.d("fjshdfffffffffj","musicData.track_Title"+ trackId);

        int trackTitle = mCursor.getColumnIndex("title");
        int trackDisplayName = mCursor.getColumnIndex("_display_name");
        int trackData = mCursor.getColumnIndex("_data");
        int trackDuration = mCursor.getColumnIndex("duration");
        while (mCursor.moveToNext()) {
            String path = mCursor.getString(trackData);
            if (isAudioFile(path)) {
                MusicData musicData = new MusicData();
                musicData.track_Id = mCursor.getLong(trackId);
                musicData.track_Title = mCursor.getString(trackTitle);
                Log.d("fjshj","musicData.track_Title"+ musicData.track_Title);
                musicData.track_data = path;
                //musicData.track_data = "android.resource://com.example.videocreator/" + R.raw.fluteonmyway;;
                Log.d("fjshj","musicData.track_duration"+ musicData.track_data);

                musicData.track_duration = mCursor.getLong(trackDuration);
                Log.d("fjshj","musicData.track_duration"+ musicData.track_duration);
                musicData.track_displayName = mCursor.getString(trackDisplayName);
                Log.d("fjshj","musicData.track_displayName"+ musicData.track_displayName);
                //mMusicDatas.add(musicData);
                if(!musicData.track_Title.equals("temp"))
                {
                    mMusicDatas.add(musicData);
                }

            }
        }
        return mMusicDatas;
    }
    private void loadFromFile() {
        mFile = new File(mFilename);

        mExtension = getExtensionFromFilename(mFilename);
        SongMetadataReader metadataReader = new SongMetadataReader(this, mFilename);
        mTitle = metadataReader.mTitle;
        mArtist = metadataReader.mArtist;

        String titleLabel = mTitle;
        if (mArtist != null && mArtist.length() > 0) {
            titleLabel = titleLabel + " - " + mArtist;
        }
        setTitle(titleLabel);

        mLoadingLastUpdateTime = System.currentTimeMillis();
        mLoadingKeepGoing = true;
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle(R.string.progress_dialog_loading);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mLoadingKeepGoing = false;
            }
        });
        mProgressDialog.show();

        final CheapSoundFile.ProgressListener listener = new CheapSoundFile.ProgressListener() {
            @Override
            public boolean reportProgress(double fractionComplete) {
                long now = System.currentTimeMillis();
                if (now - mLoadingLastUpdateTime > 100) {
                    mProgressDialog.setProgress((int) (((double) mProgressDialog.getMax()) * fractionComplete));
                    mLoadingLastUpdateTime = now;
                }
                return mLoadingKeepGoing;
            }
        };

        mCanSeekAccurately = false;
        new Thread() {
            @Override
            public void run() {
                mCanSeekAccurately = SeekTest.CanSeekAccurately(getPreferences(0));
                System.out.println("Seek test done, creating media player.");
                try {
                    MediaPlayer player = new MediaPlayer();
                    player.setDataSource(mFile.getAbsolutePath());
                    player.setAudioStreamType(3);
                    player.prepare();
                    mPlayer = player;
                } catch (final IOException e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("ReadError", "Error Reading File");
                        }
                    });
                }
            }
        }.start();
        new Thread() {
            public void run() {
                try {
                    mSoundFile = CheapSoundFile.create(mFile.getAbsolutePath(), listener);
                    if (mSoundFile == null) {
                        final String err;
                        mProgressDialog.dismiss();
                        String[] components = mFile.getName().toLowerCase().split("\\.");
                        if (components.length < 2) {
                            err = getResources().getString(R.string.no_extension_error);
                        } else {
                            err = getResources().getString(R.string.bad_extension_error) + " " + components[components.length - 1];
                        }
                        mHandler.post(new Runnable() {
                            public void run() {
                                Log.i("Ringdroid", err);
                            }
                        });
                        return;
                    }
                    mProgressDialog.dismiss();
                    if (mLoadingKeepGoing) {
                        mHandler.post(new Runnable() {
                            public void run() {
                                finishOpeningSoundFile();
                            }
                        });
                        return;
                    }
                    finish();
                } catch (final Exception e) {
                    mProgressDialog.dismiss();
                    e.printStackTrace();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("Ringdroid", "Error Reading File");
                        }
                    });
                }
            }
        }.start();
    }

    private void finishOpeningSoundFile() {
        mWaveformView.setSoundFile(mSoundFile);
        mWaveformView.recomputeHeights(mDensity);

        mMaxPos = mWaveformView.maxPos();
        mLastDisplayedStartPos = -1;
        mLastDisplayedEndPos = -1;

        mTouchDragging = false;

        mOffset = 0;
        mOffsetGoal = 0;
        mFlingVelocity = 0;
        resetPositions();
        if (mEndPos > mMaxPos) {
            mEndPos = mMaxPos;
        }

        updateDisplay();

        if (isFromItemClick) {
            onPlay(mStartPos);
        }
    }

    private synchronized void updateDisplay() {
        if (mIsPlaying) {
            int now = mPlayer.getCurrentPosition() + mPlayStartOffset;
            int frames = mWaveformView.millisecsToPixels(now);
            mWaveformView.setPlayback(frames);
            setOffsetGoalNoUpdate(frames - (mWidth / 2));
            if (now >= mPlayEndMsec) {
                handlePause();
            }
        }
        if (!mTouchDragging) {
            int offsetDelta;

            if (mFlingVelocity != 0) {
                offsetDelta = mFlingVelocity / 30;
                if (mFlingVelocity > 80) {
                    mFlingVelocity -= 80;
                } else if (mFlingVelocity < -80) {
                    mFlingVelocity += 80;
                } else {
                    mFlingVelocity = 0;
                }

                mOffset += offsetDelta;

                if (mOffset + mWidth / 2 > mMaxPos) {
                    mOffset = mMaxPos - mWidth / 2;
                    mFlingVelocity = 0;
                }
                if (mOffset < 0) {
                    mOffset = 0;
                    mFlingVelocity = 0;
                }
                mOffsetGoal = mOffset;
            } else {
                offsetDelta = mOffsetGoal - mOffset;

                if (offsetDelta > 10)
                    offsetDelta = offsetDelta / 10;
                else if (offsetDelta > 0)
                    offsetDelta = 1;
                else if (offsetDelta < -10)
                    offsetDelta = offsetDelta / 10;
                else if (offsetDelta < 0)
                    offsetDelta = -1;
                else
                    offsetDelta = 0;

                mOffset += offsetDelta;
            }
        }

        mWaveformView.setParameters(mStartPos, mEndPos, mOffset);
        mWaveformView.invalidate();

        mStartMarker.setContentDescription(
                getResources().getText(R.string.start_marker) + " " +
                        formatTime(mStartPos));
        mEndMarker.setContentDescription(
                getResources().getText(R.string.end_marker) + " " +
                        formatTime(mEndPos));

        int startX = mStartPos - mOffset - mMarkerLeftInset;
        if (startX + mStartMarker.getWidth() >= 0) {
            if (!mStartVisible) {
                // Delay this to avoid flicker
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        mStartVisible = true;
                        mStartMarker.setAlpha(1f);
                    }
                }, 0);
            }
        } else {
            if (mStartVisible) {
                mStartMarker.setAlpha(0f);
                mStartVisible = false;
            }
            startX = 0;
        }

        int endX = mEndPos - mOffset - mEndMarker.getWidth() + mMarkerRightInset;
        if (endX + mEndMarker.getWidth() >= 0) {
            if (!mEndVisible) {
                // Delay this to avoid flicker
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        mEndVisible = true;
                        mEndMarker.setAlpha(1f);
                    }
                }, 0);
            }
        } else {
            if (mEndVisible) {
                mEndMarker.setAlpha(0f);
                mEndVisible = false;
            }
            endX = 0;
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(
                startX,
                mMarkerTopOffset,
                0,
                0);
        mStartMarker.setLayoutParams(params);
        params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(endX,
                mWaveformView.getMeasuredHeight() - mEndMarker.getHeight(), 0,
                0);

        mEndMarker.setLayoutParams(params);
    }

    private void enableDisableButtons() {
        if (mIsPlaying) {
            mPlayButton.setImageResource(android.R.drawable.ic_media_pause);
            mPlayButton.setContentDescription(getResources().getText(R.string.stop));
        } else {
            mPlayButton.setImageResource(android.R.drawable.ic_media_play);
            mPlayButton.setContentDescription(getResources().getText(R.string.play));
        }
    }

    private void resetPositions() {
        mStartPos = mWaveformView.secondsToPixels(0.0);
        mEndPos = mWaveformView.secondsToPixels((double) mMaxPos);
    }

    private int trap(int pos) {
        if (pos < 0) {
            return 0;
        }
        if (pos > mMaxPos) {
            return mMaxPos;
        }
        return pos;
    }

    private void setOffsetGoalStart() {
        setOffsetGoal(mStartPos - (mWidth / 2));
    }

    private void setOffsetGoalStartNoUpdate() {
        setOffsetGoalNoUpdate(mStartPos - (mWidth / 2));
    }

    private void setOffsetGoalEnd() {
        setOffsetGoal(mEndPos - (mWidth / 2));
    }

    private void setOffsetGoalEndNoUpdate() {
        setOffsetGoalNoUpdate(mEndPos - (mWidth / 2));
    }

    private void setOffsetGoal(int offset) {
        setOffsetGoalNoUpdate(offset);
        updateDisplay();
    }

    private void setOffsetGoalNoUpdate(int offset) {
        if (mTouchDragging) {
            return;
        }

        mOffsetGoal = offset;
        if (mOffsetGoal + mWidth / 2 > mMaxPos)
            mOffsetGoal = mMaxPos - mWidth / 2;
        if (mOffsetGoal < 0)
            mOffsetGoal = 0;
    }

    private String formatTime(int pixels) {
        if (mWaveformView != null && mWaveformView.isInitialized()) {
            return formatDecimal(mWaveformView.pixelsToSeconds(pixels));
        } else {
            return "";
        }
    }

    private String formatDecimal(double x) {
        int xWhole = (int) x;
        int xFrac = (int) (100 * (x - xWhole) + 0.5);

        if (xFrac >= 100) {
            xWhole++; //Round up
            xFrac -= 100; //Now we need the remainder after the round up
            if (xFrac < 10) {
                xFrac *= 10; //we need a fraction that is 2 digits long
            }
        }

        if (xFrac < 10)
            return xWhole + ".0" + xFrac;
        else
            return xWhole + "." + xFrac;
    }

    private synchronized void handlePause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        mWaveformView.setPlayback(-1);
        mIsPlaying = false;
        enableDisableButtons();
    }

    private synchronized void onPlay(int startPosition) {
        if (mIsPlaying) {
            handlePause();
            return;
        }

        if (mPlayer == null) {
            // Not initialized yet
            return;
        }

        try {
            mPlayStartMsec = mWaveformView.pixelsToMillisecs(startPosition);
            if (startPosition < mStartPos) {
                mPlayEndMsec = mWaveformView.pixelsToMillisecs(mStartPos);
            } else if (startPosition > mEndPos) {
                mPlayEndMsec = mWaveformView.pixelsToMillisecs(mMaxPos);
            } else {
                mPlayEndMsec = mWaveformView.pixelsToMillisecs(mEndPos);
            }

            mPlayStartOffset = 0;

            int startFrame = mWaveformView.secondsToFrames(
                    mPlayStartMsec * 0.001);
            int endFrame = mWaveformView.secondsToFrames(
                    mPlayEndMsec * 0.001);
            int startByte = mSoundFile.getSeekableFrameOffset(startFrame);
            int endByte = mSoundFile.getSeekableFrameOffset(endFrame);
            if (mCanSeekAccurately && startByte >= 0 && endByte >= 0) {
                try {
                    mPlayer.reset();
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    FileInputStream subsetInputStream = new FileInputStream(
                            mFile.getAbsolutePath());
                    mPlayer.setDataSource(subsetInputStream.getFD(),
                            startByte, endByte - startByte);
                    mPlayer.prepare();
                    mPlayStartOffset = mPlayStartMsec;
                } catch (Exception e) {
                    System.out.println("Exception trying to play file subset");
                    mPlayer.reset();
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mPlayer.setDataSource(mFile.getAbsolutePath());
                    mPlayer.prepare();
                    mPlayStartOffset = 0;
                }
            }

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public synchronized void onCompletion(MediaPlayer arg0) {
                    handlePause();
                }
            });
            mIsPlaying = true;

            if (mPlayStartOffset == 0) {
                mPlayer.seekTo(mPlayStartMsec);
            }
            mPlayer.start();
            updateDisplay();
            enableDisableButtons();
        } catch (Exception e) {
            showFinalAlert(e, R.string.play_error);
        }
    }

    private void showFinalAlert(Exception e, CharSequence message) {
        CharSequence title;
        if (e != null) {
            Log.e("", "Error: " + message);
            Log.e("", getStackTrace(e));
            title = getResources().getText(R.string.alert_title_failure);
            setResult(RESULT_CANCELED, new Intent());
        } else {
            Log.i("Ringdroid", "Success: " + message);
            title = getResources().getText(R.string.alert_title_success);
        }
        new AlertDialog.Builder(this, R.style.MovieMakerAlertDialog)
                .setTitle(title).setMessage(message)
                .setPositiveButton(R.string.alert_ok_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    private void showFinalAlert(Exception e, int messageResourceId) {
        showFinalAlert(e, getResources().getText(messageResourceId));
    }

    private String makeRingtoneFilename(CharSequence title, String extension) {
        FileUtils.TEMP_DIRECTORY_AUDIO.mkdirs();
        File tempFile = new File(FileUtils.TEMP_DIRECTORY_AUDIO, title + extension);
        if (tempFile.exists()) {
            FileUtils.deleteFile(tempFile);
        }
        return tempFile.getAbsolutePath();
    }

    private void saveRingtone(final CharSequence title) {
        final String outPath = makeRingtoneFilename(title, mExtension);
        if (outPath == null) {
            showFinalAlert(new Exception(), R.string.no_unique_filename);
            return;
        }
        double startTime = mWaveformView.pixelsToSeconds(mStartPos);
        double endTime = mWaveformView.pixelsToSeconds(mEndPos);
        final int startFrame = mWaveformView.secondsToFrames(startTime);
        final int endFrame = mWaveformView.secondsToFrames(endTime);
        final int duration = (int) ((endTime - startTime) + 0.5d);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(0);
        mProgressDialog.setTitle(R.string.progress_dialog_saving);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        new Thread() {
            public void run() {
                final File outFile = new File(outPath);
                try {
                    mSoundFile.WriteFile(outFile, startFrame, endFrame - startFrame);
                    CheapSoundFile.create(outPath, new CheapSoundFile.ProgressListener() {
                        @Override
                        public boolean reportProgress(double frac) {
                            return true;
                        }
                    });
                    Log.d("sdjsdj","dhsjh"+outPath);
                    mProgressDialog.dismiss();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            setResult(RESULT_OK);
                            finish();
                            //afterSavingRingtone(title, outPath, outFile, duration);
                            //finish();
                            //onBackPressed();

                        }
                    });
                } catch (Exception e) {

                    final String errorMessage;
                    mProgressDialog.dismiss();
                    if (e.getMessage().equals("No space left on device")) {
                        errorMessage = getResources().getString(R.string.no_space_error);
                        e = null;
                    } else {
                        errorMessage = getResources().getString(R.string.write_error);
                    }
                    mHandler.post(new Runnable() {
                        public void run() {
                            Log.e("WriteError", errorMessage);
                        }
                    });
                }
            }
        }.start();
    }

    private void afterSavingRingtone(CharSequence title, String outPath, File outFile, int duration) {
        if (outFile.length() <= 512) {
            outFile.delete();
            new AlertDialog.Builder(this).setTitle(R.string.alert_title_failure)
                    .setMessage(R.string.too_small_error)
                    .setPositiveButton(R.string.alert_ok_button, null).setCancelable(false).show();
            return;
        }
        Log.d("outPath","aaa"+outPath);
        long fileSize = outFile.length();
        String artist = getResources().getString(R.string.artist_name);
        ContentValues values = new ContentValues();
        values.put("_data", outPath);
        values.put("title", title.toString());
        values.put("_size", fileSize);
        values.put("mime_type", "audio/mpeg");
        values.put("artist", artist);
        values.put("duration", duration);
        values.put("is_music", true);
        Log.e("audio", "duaration is " + duration);
       /* File file=Environment.getExternalStoragePublicDirectory("Video Creator" + "/"+".temp_audio"+"/"+"temp.mp3");
        Uri myUri = Uri.parse(file.toURI().getPath());
        new Intent().setData(myUri);
        setResult(RESULT_OK,new Intent().setData(myUri));*/

        setResult(RESULT_OK, new Intent().setData(getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(outPath), values)));
      /*  selectedMusicData.track_data = outPath;
        selectedMusicData.track_duration = (long) (duration * 1000);*/
       // MyApplication.getInstance().setMusicData( selectedMusicData);
        finish();
    }

    private void onSave() {
        if (mIsPlaying) {
            handlePause();
        }
        saveRingtone("temp");
    }

    private String getStackTrace(Exception e) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        e.printStackTrace(new PrintWriter(stream, true));
        return stream.toString();
    }

    private String getExtensionFromFilename(String filename) {
        return filename.substring(filename.lastIndexOf(46), filename.length());
    }

    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
        if (isPlaying) {
            mPlayer.release();
        }
    }

}
