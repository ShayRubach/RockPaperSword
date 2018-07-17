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

    private static MyMusicRunnable mMusicPlayer = null;
    private static MySFxRunnable mSoundEffects = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_main_menu);

        initAndPlayMusicPlauer();

        if (mSoundEffects == null) {
            mSoundEffects = new MySFxRunnable(this);
        }
    }

    private void initAndPlayMusicPlauer() {
        if (mMusicPlayer == null) {
            mMusicPlayer = new MyMusicRunnable(this);
        }

        AsyncHandler.post(mMusicPlayer);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMusicPlayer = null;
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
        mMusicPlayer = null;
        startActivity(new Intent(MainMenuActivity.this, GameActivity.class));
    }

    public static MyMusicRunnable getMusicPlayer() {
        return mMusicPlayer;
    }

    public static MySFxRunnable getSoundEffects() {
        return mSoundEffects;
    }
}
