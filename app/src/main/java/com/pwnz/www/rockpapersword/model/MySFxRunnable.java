package com.pwnz.www.rockpapersword.model;

import android.content.Context;
import android.media.SoundPool;
import android.util.SparseIntArray;

import com.pwnz.www.rockpapersword.R;
import com.pwnz.www.rockpapersword.utils.Compat;

public class MySFxRunnable implements Runnable {
    Context appContext;
    SoundPool soundPool;
    /**
     * like a hash map, but more efficient
     */
    SparseIntArray soundsMap = new SparseIntArray();
    private boolean prepared = false;

    public MySFxRunnable(Context c) {
        // be careful not to leak the activity context.
        // can keep the app context instead.
        appContext = c.getApplicationContext();

        // init this object on a user thread.
        // The rest of the use of this class can be on the UI thread
        AsyncHandler.post(this);
    }

    /**
     * load and init the sound effects, so later I'll be able to play them instantly from the
     * UI thread.
     */
    @Override
    public void run() {

        soundPool = Compat.createSoundPool();


        /**
         * a callback when prepared -- we need to prevent playing before prepared
         */
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                prepared = true;
            }
        });

        /**
         * the load() returns a stream id that can be used to play the sound.
         * I use the "R.raw.xyz" integer as key, because it's useless to invent new keys for
         * them
         */
           soundsMap.put(R.raw.blop, soundPool.load(appContext, R.raw.blop, 1));
           soundsMap.put(R.raw.move_self, soundPool.load(appContext, R.raw.move_self, 1));
           soundsMap.put(R.raw.move_enemy, soundPool.load(appContext, R.raw.move_enemy, 1));

    }

    public void play(int soundKey, float leftVol, float rightVol ) {
        if (soundPool == null || !prepared) {
            return;
        }
        soundPool.play(soundsMap.get(soundKey), leftVol, rightVol, 1, 0, 1.0f);
    }

    public SoundPool getSoundPool() {
        return soundPool;
    }

    public SparseIntArray getSoundsMap() {
        return soundsMap;
    }
}
