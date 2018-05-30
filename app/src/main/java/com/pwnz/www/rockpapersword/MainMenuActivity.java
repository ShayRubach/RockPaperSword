package com.pwnz.www.rockpapersword;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.pwnz.www.rockpapersword.model.AsyncHandler;
import com.pwnz.www.rockpapersword.model.MyMusicRunnable;
import com.pwnz.www.rockpapersword.model.MySFxRunnable;

public class MainMenuActivity extends AppCompatActivity {

    private static MyMusicRunnable mMusicPlayer;
    private static MySFxRunnable mSoundEffects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        if (mMusicPlayer == null) {
            mMusicPlayer = new MyMusicRunnable(this);
        }

        AsyncHandler.post(mMusicPlayer);

//        if (mSoundEffects == null) {
//            mSoundEffects = new MySFxRunnable(this);
//        }
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

    public static MyMusicRunnable getmMusicPlayer() {
        return mMusicPlayer;
    }

    public static MySFxRunnable getmSoundEffects() {
        return mSoundEffects;
    }
}
