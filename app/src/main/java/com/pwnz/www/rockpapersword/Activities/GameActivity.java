package com.pwnz.www.rockpapersword.Activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import com.pwnz.www.rockpapersword.GamePanel;
import com.pwnz.www.rockpapersword.controller.GameManager;
import com.pwnz.www.rockpapersword.model.Board;

public class GameActivity extends AppCompatActivity {

    private final int COLUMNS = 7;
    private final int ROWS = 6;
    private int brightColor = Color.rgb(189, 135, 50);
    private int darkColor = Color.rgb(143, 102, 38);

    private Integer canvasW, canvasH;

    private Board mBoard;
    private GamePanel mGamePanel;
    private GameManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //hide the top status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // this will give us the canvas dimensions info before even drawing
        // and enable us to prepare the ground and objects for drawing on game loop with no
        // need for calculations inside our game loop - better performance.
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        canvasH = displayMetrics.heightPixels;
        canvasW  = displayMetrics.widthPixels;

        mBoard = new Board(COLUMNS, ROWS, canvasW, canvasH, brightColor, darkColor);
        mGamePanel = new GamePanel(this);
        mManager = new GameManager(mBoard, mGamePanel);
        setContentView(mGamePanel);

        //force no-black-screen before staring a game
        mGamePanel.setInMenuScreen(false);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //ignore double actions (UP & DOWN)
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                handleClickEvent(event);
                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    private void handleClickEvent(MotionEvent event) {
        if(mGamePanel.isInMenuScreen() ){
            mGamePanel.setInMenuScreen(false);
        }
        //more code goes here later
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGamePanel.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGamePanel.resume();
    }
}
