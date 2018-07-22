package com.pwnz.www.rockpapersword.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import com.pwnz.www.rockpapersword.GamePanel;
import com.pwnz.www.rockpapersword.controller.GameManager;
import com.pwnz.www.rockpapersword.model.AsyncHandler;
import com.pwnz.www.rockpapersword.model.Board;

/**
 * Holds the activity and the view (GamePanel) of the game.
 */
public class GameActivity extends AppCompatActivity {

    private final int COLUMNS = 7;
    private final int ROWS = 6;
    public static final int GAME_IN_PROGRESS = -1;

    //private int brightColor = Color.rgb(189, 135, 50);
    private int brightColor = Color.rgb(191, 168, 168);

    //private int darkColor = Color.rgb(143, 102, 38);
    private int darkColor = Color.rgb(117, 83, 83);

    private Integer canvasW, canvasH;

    private Board mBoard;
    private GamePanel mGamePanel;
    private GameManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainMenuActivity.hideTopStatusBar(getWindow().getDecorView());
        getExactCanvasDims();

        mBoard = new Board(COLUMNS, ROWS, canvasW, canvasH, brightColor, darkColor);
        mGamePanel = new GamePanel(this);
        mManager = new GameManager(mBoard, mGamePanel);
        setContentView(mGamePanel);

        //in case we need a pre-game screen after pressing start, this is our placeholder (set to true)
        mGamePanel.setInMenuScreen(false);
    }

    private void getExactCanvasDims() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        canvasH = displayMetrics.heightPixels;
        canvasW  = displayMetrics.widthPixels;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //ignore double actions (UP & DOWN)
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                //if game has ended
                if(mManager.getWinningTeam() > GAME_IN_PROGRESS){
                    startActivity(new Intent(GameActivity.this, MainMenuActivity.class));
                    finish();
                    mManager.setWinningTeam(GAME_IN_PROGRESS);
                }
                mManager.onTouchEvent(event);
                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
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
