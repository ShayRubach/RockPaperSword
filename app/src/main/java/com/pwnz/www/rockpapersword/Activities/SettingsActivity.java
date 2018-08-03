package com.pwnz.www.rockpapersword.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.pwnz.www.rockpapersword.R;

/**
 * Handles all configurable settings.
 */

public class SettingsActivity extends AppCompatActivity {

    private static final float MAX_VOLUME = 100f;
    private SeekBar mBgMusic, mSfx;
    private ImageView bgVolumeLogo, sfxVolumeLogo;
    private Button mBtnBackToMainMenu;

    private static final float DEFAULT_VOLUME_VALUE = 30;
    public static float sfxGeneralVolume = DEFAULT_VOLUME_VALUE;
    public static float bgGeneralVolume = DEFAULT_VOLUME_VALUE;

    private final int MUTE = 0;
    private static final String MUTE_MSG = "Muted";
    private final int mMuteLogo = android.R.drawable.ic_lock_silent_mode;
    private final int mUnmuteLogo = android.R.drawable.ic_lock_silent_mode_off;
    private final int sfxVol = R.raw.blop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MainMenuActivity.hideTopStatusBar(getWindow().getDecorView());

        mBtnBackToMainMenu = findViewById(R.id.btn_back_from_settings);
        mBgMusic = findViewById(R.id.sb_bg_music);
        mSfx = findViewById(R.id.sb_sfx);
        bgVolumeLogo = findViewById(R.id.img_sound_on_or_off);
        sfxVolumeLogo = findViewById(R.id.img_sfx_on_or_off);

        mBgMusic.setProgress((int) DEFAULT_VOLUME_VALUE);
        mSfx.setProgress((int) DEFAULT_VOLUME_VALUE);

        mBgMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bgGeneralVolume  = progress / MAX_VOLUME;
                MainMenuActivity.mediaPlayer.setVolume(bgGeneralVolume , bgGeneralVolume);

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
                sfxGeneralVolume  = progress / MAX_VOLUME;
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

        mBtnBackToMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, MainMenuActivity.class));
                finish();
            }
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

