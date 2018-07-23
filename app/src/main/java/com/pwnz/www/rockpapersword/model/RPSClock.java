package com.pwnz.www.rockpapersword.model;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.pwnz.www.rockpapersword.R;

/**
 * Handles the timing of the Game Clock in game.
 */
public class RPSClock extends AnimationHandler {

    public Context context;

    public RPSClock(Context context) {
        this.context = context;

        initAnimationDetails(context, R.drawable.clock_sprite, 2 ,5 );
        resetToFirstFrame();

        clockPadding = (spriteFrameSrcW / 2);
        destRect = new Rect(canvasW - spriteFrameSrcW - clockPadding, 0, canvasW, spriteFrameSrcH + clockPadding);
    }

    public boolean updateTime(long timePassed) {
        boolean swapTurns = false;

        if (timePassed >= 1) {
            swapTurns = chooseNextFrame();
        }
        //if swapTurns == true, we need to notify the GameManager so it forces a turn swap on clock reset
        return swapTurns;
    }
}
