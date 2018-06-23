package com.pwnz.www.rockpapersword.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import com.pwnz.www.rockpapersword.GamePanel;
import com.pwnz.www.rockpapersword.controller.GameManager;
import com.pwnz.www.rockpapersword.model.Board;

public class GameActivity extends AppCompatActivity {

    private final int COLUMNS = 7;
    private final int ROWS = 6;

    private Board mBoard;
    private GamePanel mGamePanel;
    private GameManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBoard = new Board(COLUMNS, ROWS);
        mGamePanel = new GamePanel(this);
        mManager = new GameManager(mBoard);
        setContentView(mGamePanel);

        //force no splash screen before staring a game
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
