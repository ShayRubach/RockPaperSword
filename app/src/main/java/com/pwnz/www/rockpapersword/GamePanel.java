package com.pwnz.www.rockpapersword;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.pwnz.www.rockpapersword.model.Tile;

public class GamePanel extends SurfaceView implements Runnable {

    private boolean canPlay = false;
    private boolean isInMenuScreen = true;

    private Thread mPlayThread = null;
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Bitmap mTtpBitmap, bg;
    private final int COLUMNS = 7;
    private final int ROWS = 6;
    private static Tile[][] tilesMatrix = null;
    private int mCanvasH, mCanvasW;


    public GamePanel(Context context) {
        super(context);
        bg = BitmapFactory.decodeResource(getResources(), R.drawable.chartest);
        initPositions();
        mSurfaceHolder = getHolder();


    }

    private void initPositions() {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void run() {

        while(canPlay) {

            if(!mSurfaceHolder.getSurface().isValid()){
                continue;
            }

            mCanvas = mSurfaceHolder.lockCanvas();

            //mCanvas.drawBitmap(bg, null, new Rect(0,0,mCanvasW,mCanvasH),null);

            mCanvasH = mCanvas.getHeight();
            mCanvasW = mCanvas.getWidth();

            if(isInMenuScreen){
                //todo: convert dpToPxl for x-platform responsive look
                drawPlayButton(mCanvas);
                drawGameTitle(mCanvas);
            }
            else {
                //todo: play logic goes here

                drawInstructions(mCanvas);
                drawTiles();
                displayAllSoldiers();
            }
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }


    private void drawTiles() {

        final int gapTop = 2;
        final int gapBtm = 2;
        final int hDivisor = 10;
        final int wDivisor = 7;

        Bitmap soldier = BitmapFactory.decodeResource(getResources(), R.drawable.attack_1);

        Paint brushBlack = new Paint();
        brushBlack.setColor(Color.rgb(249, 184, 72));
        brushBlack.setStyle(Paint.Style.FILL);

        Paint brushGreen = new Paint();
        brushGreen.setColor(Color.rgb(242, 227, 201));
        brushGreen.setStyle(Paint.Style.FILL);

        Paint brushes[] = new Paint[2];
        brushes[0] = brushBlack;
        brushes[1] = brushGreen;

        int brushRand = 0 ;
        int rectW = mCanvasW / wDivisor;
        int rectH = mCanvasH / hDivisor;

        for (int i = 0; i < hDivisor ; i++) {
            if(i < gapTop || i > hDivisor - gapBtm - 1)
                continue;
            for (int k = 0; k < wDivisor; k++) {
                Rect rect = new Rect(k * rectW, i * rectH, (k+1) * rectW, (i+1) * rectH);
                brushRand = brushRand == 1 ? 0 : 1;
                mCanvas.drawRect(rect, brushes[brushRand]);

                //dont draw the 2 separating middle rows:
                if(i != (hDivisor / 2) && i != (hDivisor / 2) - 1){
                    mCanvas.drawBitmap(soldier, null, rect,null);
                }


            }
        }

    }

    private void drawInstructions(Canvas canvas) {

    }

    private void displayAllSoldiers() {

    }

    public void pause(){
        canPlay = false;
        //cats.clear();

        while (true) {
            try {
                mPlayThread.join();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //reset the thread
        mPlayThread = null;
    }

    public void resume() {
        canPlay = true;
        mPlayThread = new Thread(this);
        mPlayThread.start();

    }


    public void addSoldierAt(int x, int y) {
//        if(cats.size() < MAX_CATS_ON_SCREEN){
//            //draw a cat
//            cats.add(new NyanCat(BitmapFactory.decodeResource(getResources(), R.drawable.nyan_cat_left), x, y));
//        }
//        else {
//            cats.clear();
//        }
    }

    private void drawPlayButton(Canvas canvas) {

//        mTtpBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tap_to_play);
//        mCanvas.drawBitmap(mTtpBitmap, mCanvas.getWidth() / 4,  ttpYPos, null);
//
//        BitmapFactory.Options option = new BitmapFactory.Options();
//        option.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(getResources(), R.drawable.tap_to_play, option);
//        ttpHeight = toPxs(option.outHeight);
//        ttpWidth = toPxs(option.outWidth);
//
//        if(ttpYPos <= TTP_Y - MAX_MOVEMENT_EFFECT){
//            goingUp = false;
//        }
//        if(ttpYPos >= TTP_Y + MAX_MOVEMENT_EFFECT){
//            goingUp = true;
//        }
//        if(goingUp){
//            ttpYPos -= ttpYDir;
//        }
//        if(!goingUp){
//            ttpYPos += ttpYDir;
//        }
    }

    private void drawGameTitle(Canvas canvas){
//        gameTitle = BitmapFactory.decodeResource(getResources(), R.drawable.arc_title);
//        mCanvas.drawBitmap(gameTitle, mCanvas.getWidth() / 4,  GAME_TITLE_Y, null);
    }

    public boolean isValidPosition(int x, int y){

        boolean isValid = true;
//        if(x > bigNyanPosX && x < bigNyanPosX + bigNyanWidth){
//            //x is invalid
//            isValid = false;
//        }
//        if(y > bigNyanPosY && y < bigNyanPosY + bigNyanHeight){
//            //y is invalid
//            isValid = false;
//        }
        return isValid;
    }


    private int toPxs(int dps){
        return (int)(dps * getResources().getDisplayMetrics().density + 0.5f);
    }

    public boolean isInMenuScreen() {
        return isInMenuScreen;
    }

    public void setInMenuScreen(boolean inMenuScreen) {
        isInMenuScreen = inMenuScreen;
    }
}
