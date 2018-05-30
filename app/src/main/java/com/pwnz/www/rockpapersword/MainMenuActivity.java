package com.pwnz.www.rockpapersword;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.pwnz.www.rockpapersword.model.SettingsActivity;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    void onClickSettings(View v){
        startActivity(new Intent(MainMenuActivity.this, SettingsActivity.class));
    }

    void onClickStart(View v){
        startActivity(new Intent(MainMenuActivity.this, GameActivity.class));
    }

}
