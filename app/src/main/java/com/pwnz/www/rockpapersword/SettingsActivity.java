package com.pwnz.www.rockpapersword;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private SeekBar mBgMusic, mSfx;
    private ImageView bgVolumeLogo, sfxVolumeLogo;
    private final int DEFAULT_VOLUME_VALUE = 50;
    private final int MUTE = 0;
    private static final String MUTE_MSG = "Muted";
    private final int mMuteLogo = android.R.drawable.ic_lock_silent_mode;
    private final int mUnmuteLogo = android.R.drawable.ic_lock_silent_mode_off;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mBgMusic = findViewById(R.id.sb_bg_music);
        mSfx = findViewById(R.id.sb_sfx);
        bgVolumeLogo = findViewById(R.id.img_sound_on_or_off);
        sfxVolumeLogo = findViewById(R.id.img_sfx_on_or_off);

        mBgMusic.setProgress(DEFAULT_VOLUME_VALUE);
        mSfx.setProgress(DEFAULT_VOLUME_VALUE);

        mBgMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float output = progress / 100f;
                MainMenuActivity.getmMusicPlayer().getMediaPlayer().setVolume(output, output);

                if(progress == MUTE){
                    mute(bgVolumeLogo);
                }
                else {
                    unMute(bgVolumeLogo);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        mSfx.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float output = progress / 100f;
                //MainMenuActivity.getmSoundEffects().getSoundPool().setVolume(output, output);
                MainMenuActivity.getmSoundEffects().play(R.raw.blop);

                if(progress == MUTE){
                    mute(sfxVolumeLogo);
                }
                else {
                    unMute(sfxVolumeLogo);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


    }

    void mute(ImageView logo){
        Toast.makeText(getApplicationContext(),MUTE_MSG, Toast.LENGTH_SHORT ).show();
        logo.setImageResource(mMuteLogo);
    }

    void unMute(ImageView logo){
        logo.setImageResource(mUnmuteLogo);
    }
}

