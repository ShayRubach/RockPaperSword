package com.pwnz.www.rockpapersword;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private GamePanel mGamePanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGamePanel = new GamePanel(this);
        setContentView(mGamePanel);

        //todo: moving from side to side
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
        if(mGamePanel.isInMenuScreen){
            //Toast.makeText(this,"Let's Go!",Toast.LENGTH_LONG).show();
            mGamePanel.isInMenuScreen = false;
        }
        else {
            //listen to game clicks.

        }
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
