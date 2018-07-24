package com.pwnz.www.rockpapersword.Activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.pwnz.www.rockpapersword.R;
import com.pwnz.www.rockpapersword.model.MySFxRunnable;
import io.fabric.sdk.android.Fabric;

public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener {

    public static MediaPlayer mediaPlayer = null;
    private static MySFxRunnable mSoundEffects = null;
    private static boolean isMusicPlaying = false;

    private Button mStartBtn, mSettingsBtn, mInstructionsBtn;
    private ImageView mAboutImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_main_menu);

        mStartBtn = findViewById(R.id.btn_start);
        mStartBtn.setOnClickListener(this);

        mSettingsBtn = findViewById(R.id.btn_settings);
        mSettingsBtn.setOnClickListener(this);

        mInstructionsBtn = findViewById(R.id.btn_instructions);
        mInstructionsBtn.setOnClickListener(this);

        mAboutImage = findViewById(R.id.img_about);
        mAboutImage.setOnClickListener(this);

        if(!isMusicPlaying){
            mediaPlayer = MediaPlayer.create(this, R.raw.handmade_moments_wanderin_eyes_edited);
            mediaPlayer.setVolume(SettingsActivity.sfxGeneralVolume, SettingsActivity.sfxGeneralVolume);
            mediaPlayer.start();
            isMusicPlaying = true;
        }

        if (mSoundEffects == null) {
            mSoundEffects = new MySFxRunnable(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.pause();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_start:
                mediaPlayer.pause();
                isMusicPlaying = false;
                startActivity(new Intent(MainMenuActivity.this, GameActivity.class));
                finish();
                break;
            case R.id.btn_settings:
                startActivity(new Intent(MainMenuActivity.this, SettingsActivity.class));
                finish();
                break;
            case R.id.btn_instructions:
                startActivity(new Intent(MainMenuActivity.this, InstructionsActivity.class));
                finish();
                break;
            case R.id.img_about:
                popUpAboutWindow();
        }
    }

    private void popUpAboutWindow() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainMenuActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.about_the_app, null);

        final Button mBtnGotIt = mView.findViewById(R.id.btnGotIt);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        mBtnGotIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });
    }

    public static MySFxRunnable getSoundEffects() {
        return mSoundEffects;
    }

    public static void hideTopStatusBar(View decorView) {
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
