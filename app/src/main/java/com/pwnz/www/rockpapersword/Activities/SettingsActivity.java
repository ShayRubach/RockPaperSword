package com.pwnz.www.rockpapersword.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.pwnz.www.rockpapersword.R;

public class SettingsActivity extends AppCompatActivity {

    private static final float MAX_VOLUME = 100f;
    private SeekBar mBgMusic, mSfx;
    private ImageView bgVolumeLogo, sfxVolumeLogo;
    public static float sfxGeneralVolume = 50;


    private final int DEFAULT_VOLUME_VALUE = 50;
    private final int MUTE = 0;
    private static final String MUTE_MSG = "Muted";
    private final int mMuteLogo = android.R.drawable.ic_lock_silent_mode;
    private final int mUnmuteLogo = android.R.drawable.ic_lock_silent_mode_off;
    private final int sfxVol = R.raw.blop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //TODO: turn this into function and use it in all activities
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        mBgMusic = findViewById(R.id.sb_bg_music);
        mSfx = findViewById(R.id.sb_sfx);
        bgVolumeLogo = findViewById(R.id.img_sound_on_or_off);
        sfxVolumeLogo = findViewById(R.id.img_sfx_on_or_off);

        mBgMusic.setProgress(DEFAULT_VOLUME_VALUE);
        mSfx.setProgress(DEFAULT_VOLUME_VALUE);

        mBgMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float output = progress / MAX_VOLUME;
                MainMenuActivity.mediaPlayer.setVolume(output, output);

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
                sfxGeneralVolume = progress / MAX_VOLUME;

                MainMenuActivity.getSoundEffects().play(sfxVol, sfxGeneralVolume, sfxGeneralVolume);

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

