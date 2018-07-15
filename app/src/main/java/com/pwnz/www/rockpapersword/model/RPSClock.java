package com.pwnz.www.rockpapersword.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.pwnz.www.rockpapersword.R;

public class RPSClock {

    public Context context;
    private static final int INITIAL_TURN_TIME = 10;
    private int turnTimeLeft = INITIAL_TURN_TIME;

    private int spriteFrameCnt;
    private double localClock;

    //Source variables:
    private Bitmap clockSpriteSheet;
    private Bitmap clockSpriteFrame;
    private int clockSpriteId = R.drawable.clock_sprite;
    private int spriteSheetH, spriteSheetW;
    private int spriteFrameSrcW,spriteFrameSrcH;
    private int spriteFrameSrcPosX,spriteFrameSrcPosY;
    private int numberOfSpriteFrames;
    private Rect sourceRect;

    //Destination variables:

    private int canvasW, canvasH;
    private int spriteFrameDstW,spriteFrameDstH;
    private int spriteFrameDstPosX,spriteFrameDstPosY;
    private Rect destRect;

    private int clockPadding;
    private int spriteCol = 0;
    private int spriteRow = 0;

    public RPSClock(Context context) {
        this.context = context;
        localClock = 0;

        clockSpriteSheet = BitmapFactory.decodeResource(context.getResources(), clockSpriteId);
        spriteSheetH = clockSpriteSheet.getHeight();
        spriteSheetW = clockSpriteSheet.getWidth();
        numberOfSpriteFrames = 4;
        spriteFrameSrcH = spriteSheetH/2;   //2 rows
        spriteFrameSrcW = spriteSheetW/5;   //5 columns

        canvasH = context.getResources().getDisplayMetrics().heightPixels;
        canvasW = context.getResources().getDisplayMetrics().widthPixels;

        clockPadding = (spriteFrameSrcW/2);

        //todo: change this to spawn clock at top right corner
        spriteFrameDstPosX = 0;
        spriteFrameDstPosY = 0;

        //todo: apply the above here??
        sourceRect = new Rect();
        destRect = new Rect(canvasW-spriteFrameSrcW - clockPadding,0, canvasW,spriteFrameSrcH + clockPadding );
        resetClock();

    }

    public void drawClock(Canvas canvas){
        System.out.println("drawClock: called");
        System.out.println("sourceRect = \n" + sourceRect);
        canvas.drawBitmap(clockSpriteSheet, sourceRect, destRect,null);
    }

    public void resetClock(){
        setFrameIndex(0,0);
        spriteCol = 0;
        spriteRow = 0;
    }

    public boolean updateTime(long timePassed){
        System.out.println("updateTime: timePassed = " + timePassed);
        boolean swapTurns = false;

        if(timePassed >= 1) {
            swapTurns = chooseNextFrame();
        }
        //if swapTurns == true, we need to notify the GameManager so it forces a turn swap on clock reset
        return swapTurns ;
    }

    private boolean chooseNextFrame() {
        //if we reached the last frame on our row
        if(spriteCol == spriteSheetW / spriteFrameSrcW - 1){
            if(spriteRow != spriteSheetH / spriteFrameSrcH - 1){
                spriteCol = 0;  // back to row start
                spriteRow++;    // jump to next next row
            }
            else {
                resetClock();
                return true;
            }
        }
        else
            spriteCol++;

        System.out.println("chooseNextFrame: spriteCol="+spriteCol+"  spriteRow="+spriteRow);
        setFrameIndex(spriteCol, spriteRow);
        return false;
    }

    private void setFrameIndex(int i, int j) {
        sourceRect.left = i * spriteFrameSrcW;
        sourceRect.top = j * spriteFrameSrcH;
        sourceRect.right = (i+1) * spriteFrameSrcW;
        sourceRect.bottom = (j+1) * spriteFrameSrcH;

    }
}
