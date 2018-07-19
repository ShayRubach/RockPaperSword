package com.pwnz.www.rockpapersword.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/*
    AnimationHandler will handle the 'slicing' of the next frame every time the object is drawn.
    The handler will notify the Game Manager when the animation has started in order to stop the
    clock and when it has ended so it can move on to the next step of the game and restart
    the clock.
 */
public class AnimationHandler {
    protected int spriteFrameCnt;
    protected double localClock;

    //Source variables:
    protected Bitmap spriteSheet;
    protected Bitmap clockSpriteFrame;
    protected int spriteId;
    protected int spriteSheetH, spriteSheetW;
    protected int spriteFrameSrcW,spriteFrameSrcH;
    protected int spriteFrameSrcPosX,spriteFrameSrcPosY;
    protected int numberOfSpriteFrames;
    protected Rect sourceRect;

    //Destination variables:
    protected int canvasW, canvasH;
    protected int spriteFrameDstW,spriteFrameDstH;
    protected int spriteFrameDstPosX,spriteFrameDstPosY;
    protected Rect destRect;

    protected int clockPadding;
    protected int spriteCol = 0;
    protected int spriteRow = 0;




    public void drawAnimation(Canvas canvas){
        canvas.drawBitmap(spriteSheet, sourceRect, destRect,null);
    }

    public void resetToFirstFrame(){
        setFrameIndex(0,0);
        spriteCol = 0;
        spriteRow = 0;
    }

    public boolean chooseNextFrame() {
        //if we reached the last frame on our row
        if(spriteCol == spriteSheetW / spriteFrameSrcW - 1){
            if(spriteRow != spriteSheetH / spriteFrameSrcH - 1){
                spriteCol = 0;  // back to row start
                spriteRow++;    // jump to next next row
            }
            else {
                resetToFirstFrame();
                return true;
            }
        }
        else
            spriteCol++;

        System.out.println("chooseNextFrame: spriteCol="+spriteCol+"  spriteRow="+spriteRow);
        setFrameIndex(spriteCol, spriteRow);
        return false;
    }

    public void setFrameIndex(int i, int j) {
        sourceRect.left = i * spriteFrameSrcW;
        sourceRect.top = j * spriteFrameSrcH;
        sourceRect.right = (i+1) * spriteFrameSrcW;
        sourceRect.bottom = (j+1) * spriteFrameSrcH;

    }
}

