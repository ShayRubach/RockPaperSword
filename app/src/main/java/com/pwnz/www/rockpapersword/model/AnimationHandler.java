package com.pwnz.www.rockpapersword.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import com.pwnz.www.rockpapersword.R;

/**
    AnimationHandler will handle the 'slicing' of the next frame every time the object is drawn.
    The handler will notify the Game Manager when the animation has started in order to stop the
    clock and when it has ended so it can move on to the next step of the game and restart
    the clock.
 */
public class AnimationHandler {

    //Source variables:
    protected Bitmap spriteSheet;
    protected int spriteId;
    protected int spriteSheetH, spriteSheetW;
    protected int spriteFrameSrcW,spriteFrameSrcH;
    protected int numberOfSpriteFrames;
    protected Rect sourceRect;

    //Destination variables:
    protected int canvasW, canvasH;
    protected Rect destRect;

    protected int clockPadding;
    protected int spriteCol = 0;
    protected int spriteRow = 0;

    public AnimationHandler() {}

    //todo: refactor - use this ctor instad of new + initDetails
    public AnimationHandler(Context context, int spriteId, int rowsCnt, int colsCnt, Rect rect){
        initAnimationDetails(context, spriteId, rowsCnt, colsCnt);
        setDestRect(rect);
    }

    public void initAnimationDetails(Context context, int spriteId, int rowsCnt, int colsCnt){

        this.spriteId = spriteId;

        spriteSheet = BitmapFactory.decodeResource(context.getResources(), spriteId);
        spriteSheetH = spriteSheet.getHeight();
        spriteSheetW = spriteSheet.getWidth();
        numberOfSpriteFrames = 4;
        spriteFrameSrcH = spriteSheetH / rowsCnt;     // how many rows
        spriteFrameSrcW = spriteSheetW / colsCnt ;    // how many columns

        canvasH = context.getResources().getDisplayMetrics().heightPixels;
        canvasW = context.getResources().getDisplayMetrics().widthPixels;

        sourceRect = new Rect();
    }

    public void drawAnimation(Canvas canvas){
        if(this == null)
            Log.d("ANIM_DBG", "AnimationHandler is null\n");
        else
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

        setFrameIndex(spriteCol, spriteRow);
        return false;
    }

    public void setFrameIndex(int i, int j) {
        sourceRect.left = i * spriteFrameSrcW;
        sourceRect.top = j * spriteFrameSrcH;
        sourceRect.right = (i+1) * spriteFrameSrcW;
        sourceRect.bottom = (j+1) * spriteFrameSrcH;
    }

    public Rect getDestRect() {
        return destRect;
    }

    public Bitmap getSpriteSheet() {
        return spriteSheet;
    }

    public void setDestRect(Rect destRect) {
        this.destRect = destRect;
    }
}

