package com.pwnz.www.rockpapersword;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GamePanel extends SurfaceView implements Runnable {
    //private static ArrayList<NyanCat> cats = new ArrayList<>();
    private boolean canPlay = false;
    private Thread mPlayThread = null;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;
    private Bitmap mTtpBitmap, mTtsBitmap, mLogoCatBitmap, bg;
    public boolean isInMenuScreen = true;

    public GamePanel(Context context) {
        super(context);
        initPositions();
        surfaceHolder = getHolder();
        //bg = BitmapFactory.decodeResource(getResources(), R.drawable.bright_tile);

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

            if(!surfaceHolder.getSurface().isValid()){
                continue;
            }

            canvas = surfaceHolder.lockCanvas();
            //canvas.drawBitmap(bg,0,0,null);

            if(isInMenuScreen){
                //todo: convert dpToPxl for x-platform responsive look
                drawPlayButton(canvas);
                drawGameTitle(canvas);
            }
            else {
                //todo: play logic goes here

                drawInstructions(canvas);
                drawTiles();
                displayAllSoldiers();
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawTiles() {
        final int HORIZONTAL_TILES_COUNT = 7;
        final int VERTICAL_TILES_COUNT = 6;

        //Bitmap[][] tilesMatrix = new Bitmap[HORIZONTAL_TILES_COUNT][VERTICAL_TILES_COUNT];
        Bitmap[] tilesMatrix = new Bitmap[HORIZONTAL_TILES_COUNT];
        Bitmap[] tilesMatrix2 = new Bitmap[HORIZONTAL_TILES_COUNT];


        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.dark_tile, option);

        int tileHeight = option.outHeight;
        int tileWidth = option.outWidth;

        for (int i = 0; i+1 < HORIZONTAL_TILES_COUNT ; i+=2) {
                tilesMatrix[i] = BitmapFactory.decodeResource(getResources(), R.drawable.dark_tile);
                tilesMatrix[i+1] = BitmapFactory.decodeResource(getResources(), R.drawable.bright_tile);

                tilesMatrix2[i] = BitmapFactory.decodeResource(getResources(), R.drawable.bright_tile);
                tilesMatrix2[i+1] = BitmapFactory.decodeResource(getResources(), R.drawable.dark_tile);
        }
        tilesMatrix[HORIZONTAL_TILES_COUNT-1] = BitmapFactory.decodeResource(getResources(), R.drawable.dark_tile);
        tilesMatrix2[HORIZONTAL_TILES_COUNT-1] = BitmapFactory.decodeResource(getResources(), R.drawable.bright_tile);

        int j = 0;
        while(j != VERTICAL_TILES_COUNT){
            for (int i = 0; i < tilesMatrix.length ; i++) {
                canvas.drawBitmap(tilesMatrix[i], i*tileWidth, j*tileHeight, null);
            }

            j++;
            for (int i = 0; i < tilesMatrix.length ; i++) {
                canvas.drawBitmap(tilesMatrix2[i], i*tileWidth, j*tileHeight, null);
            }
            j++;
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
//        canvas.drawBitmap(mTtpBitmap, canvas.getWidth() / 4,  ttpYPos, null);
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
//        canvas.drawBitmap(gameTitle, canvas.getWidth() / 4,  GAME_TITLE_Y, null);
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

}
