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
        localClock = 0;
        spriteId = R.drawable.clock_sprite;

        spriteSheet = BitmapFactory.decodeResource(context.getResources(), spriteId);
        spriteSheetH = spriteSheet.getHeight();
        spriteSheetW = spriteSheet.getWidth();
        numberOfSpriteFrames = 4;
        spriteFrameSrcH = spriteSheetH / 2;   //2 rows
        spriteFrameSrcW = spriteSheetW / 5;   //5 columns

        canvasH = context.getResources().getDisplayMetrics().heightPixels;
        canvasW = context.getResources().getDisplayMetrics().widthPixels;

        clockPadding = (spriteFrameSrcW / 2);

        sourceRect = new Rect();
        destRect = new Rect(canvasW - spriteFrameSrcW - clockPadding, 0, canvasW, spriteFrameSrcH + clockPadding);
        resetToFirstFrame();

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
