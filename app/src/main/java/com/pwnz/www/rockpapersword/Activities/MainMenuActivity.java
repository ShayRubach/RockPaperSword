package com.pwnz.www.rockpapersword.Activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.pwnz.www.rockpapersword.R;
import com.pwnz.www.rockpapersword.model.MyMusicRunnable;
import com.pwnz.www.rockpapersword.model.MySFxRunnable;
import io.fabric.sdk.android.Fabric;

public class MainMenuActivity extends AppCompatActivity {

    private static MyMusicRunnable mMusicPlayer;
    private static MySFxRunnable mSoundEffects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_main_menu);


        if (mMusicPlayer == null) {
            mMusicPlayer = new MyMusicRunnable(this);
        }

        //TODO: UNCOMMENT THIS, its just a workaround for instantly mute when app comes up, its annoying during development
        //AsyncHandler.post(mMusicPlayer);

        if (mSoundEffects == null) {
            mSoundEffects = new MySFxRunnable(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    void onClickSettings(View v){
        startActivity(new Intent(MainMenuActivity.this, SettingsActivity.class));
    }

    void onClickStart(View v){
        startActivity(new Intent(MainMenuActivity.this, GameActivity.class));
    }

    public static MyMusicRunnable getMusicPlayer() {
        return mMusicPlayer;
    }

    public static MySFxRunnable getSoundEffects() {
        return mSoundEffects;
    }
}
