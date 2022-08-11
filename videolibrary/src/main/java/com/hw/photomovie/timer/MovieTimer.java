package com.hw.photomovie.timer;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import com.hw.photomovie.PhotoMovie;

/**
 * Created by yellowcat on 2015/6/12.
 */
public class MovieTimer implements IMovieTimer, ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

    private  static ValueAnimator mAnimator;

    private static MovieListener mMovieListener;

    private long mPausedPlayTime;
    private boolean mPaused;

    private PhotoMovie mPhotoMovie;
    private boolean mLoop;
    int abc;
    static boolean status=false;
    static long statusTime;

    public MovieTimer(PhotoMovie photoMovie) {
        mPhotoMovie = photoMovie;

        mAnimator = ValueAnimator.ofInt(0, 1);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(this);
        mAnimator.addListener(this);
        mAnimator.setDuration(Long.MAX_VALUE);
    }


    public void start() {
        if (!mPaused) {
            mAnimator.start();
        } else {
            mAnimator.start();
        }
    }

    public void pause() {
        if (mPaused) {
            return;
        }
        mPaused = true;
        mPausedPlayTime = mAnimator.getCurrentPlayTime();
        Log.d("mPausedPlayTime","1 :"+mPausedPlayTime);
        mAnimator.cancel();

    }

    @Override
    public void setMovieListener(IMovieTimer.MovieListener movieListener) {
        this.mMovieListener = movieListener;
    }

    @Override
    public int getCurrentPlayTime() {
        Log.d("mPausedPlayTime","2 :"+(int)mPausedPlayTime);
        return (int) mPausedPlayTime;

    }

    @Override
    public void setLoop(boolean loop) {
        mLoop = loop;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (mPaused || !animation.isRunning()) {
            return;
        }
        long curTime = animation.getCurrentPlayTime();

        if (curTime >= mPhotoMovie.getDuration()) {
            mAnimator.removeUpdateListener(this);
            mAnimator.removeListener(this);
            mAnimator.end();
            if (mMovieListener != null) {
                mMovieListener.onMovieEnd();
            }
            mAnimator.addUpdateListener(this);
            mAnimator.addListener(this);
            if(mLoop){
                mAnimator.start();
            }
        }else{
            if (mMovieListener != null) {
               // mMovieListener.onMovieUpdate((int) (curTime+1000));
                mMovieListener.onMovieUpdate((int) curTime);
                Log.d("MovieTimerTime","time"+(int) curTime);
            }
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {
        if (mMovieListener != null) {
            if (mPaused) {
                mMovieListener.onMovieResumed();
                mAnimator.setCurrentPlayTime(mPausedPlayTime);
            } else {
              // mAnimator.setCurrentPlayTime(2000);
              /*  if (status){
                    Log.d("sdhaduduaduasu",""+statusTime);
                    mAnimator.setCurrentPlayTime(statusTime);
                    mMovieListener.onMovieStarted();
                }
                else
                {*/
                    mMovieListener.onMovieStarted();

                //}
            }
        }
        if (mPaused) {
            mAnimator.setCurrentPlayTime(mPausedPlayTime);
        }
        mPaused = false;
        mPausedPlayTime = 0;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (mMovieListener != null) {
            if (mPaused) {
                mMovieListener.onMoviedPaused();
            } else {
                mMovieListener.onMovieEnd();
            }
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        mPausedPlayTime = mAnimator.getCurrentPlayTime();
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }


    static  public void getStatus(boolean xyz,long time)
    {

        Log.d("sdhhhhhhhhhhh","dhssssssssss");
        status=xyz;
        statusTime=time;
        mAnimator.setCurrentPlayTime(statusTime);
        mMovieListener.onMovieStarted();
    }
}
