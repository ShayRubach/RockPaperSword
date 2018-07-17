package com.pwnz.www.rockpapersword.Activities;

import android.app.ActionBar;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.pwnz.www.rockpapersword.R;
import com.pwnz.www.rockpapersword.model.AsyncHandler;
import com.pwnz.www.rockpapersword.model.MyMusicRunnable;
import com.pwnz.www.rockpapersword.model.MySFxRunnable;
import io.fabric.sdk.android.Fabric;

public class MainMenuActivity extends AppCompatActivity {

    public static MediaPlayer mediaPlayer = null;
    private static MySFxRunnable mSoundEffects = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_main_menu);

        mediaPlayer = MediaPlayer.create(this, R.raw.handmade_moments_wanderin_eyes_edited);
        mediaPlayer.setVolume(SettingsActivity.sfxGeneralVolume, SettingsActivity.sfxGeneralVolume);
        mediaPlayer.start();

        if (mSoundEffects == null) {
            mSoundEffects = new MySFxRunnable(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(),"onDestroy", Toast.LENGTH_SHORT ).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(getApplicationContext(),"onPause", Toast.LENGTH_SHORT ).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(),"onResume", Toast.LENGTH_SHORT ).show();

    }

    void onClickSettings(View v){
        startActivity(new Intent(MainMenuActivity.this, SettingsActivity.class));
    }

    void onClickStart(View v){
        mediaPlayer.pause();
        startActivity(new Intent(MainMenuActivity.this, GameActivity.class));
    }

    public static MySFxRunnable getSoundEffects() {
        return mSoundEffects;
    }
}
