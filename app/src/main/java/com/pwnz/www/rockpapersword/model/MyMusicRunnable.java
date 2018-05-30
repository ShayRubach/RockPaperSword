package com.pwnz.www.rockpapersword.model;

import android.content.Context;
import android.media.MediaPlayer;


import com.pwnz.www.rockpapersword.R;

import java.io.IOException;

public class MyMusicRunnable implements Runnable, MediaPlayer.OnCompletionListener {
    public Context appContext;
    public MediaPlayer mPlayer;
    public boolean musicIsPlaying = false;

    public MyMusicRunnable(Context c) {
        // be careful not to leak the activity context.
        // can keep the app context instead.
        appContext = c.getApplicationContext();
    }

    public boolean isMusicIsPlaying() {
        return musicIsPlaying;
    }

    /**
     * MediaPlayer.OnCompletionListener callback
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        // loop back - play again
        if (musicIsPlaying && mPlayer != null) {
            mPlayer.start();
        }
    }

    /**
     * toggles the music player state
     * called asynchronously every time the play/pause button is pressed
     */
    @Override
    public void run() {

        if (musicIsPlaying) {
            mPlayer.stop();
            musicIsPlaying = false;
        } else {
            if (mPlayer == null) {
                mPlayer = MediaPlayer.create(appContext, R.raw.handmade_moments_wanderin_eyes_edited);
//                mPlayer.setLooping(true);
                mPlayer.seekTo(0);
                mPlayer.setVolume(0.4f, 0.4f);
                mPlayer.start();
                mPlayer.setOnCompletionListener(this); // MediaPlayer.OnCompletionListener
            } else {
                try {
                    mPlayer.prepare();
                    mPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            musicIsPlaying = true;
        }

    }

    public MediaPlayer getMediaPlayer() {
        return mPlayer;
    }
}