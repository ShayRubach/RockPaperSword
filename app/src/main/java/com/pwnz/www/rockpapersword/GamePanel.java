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

import com.pwnz.www.rockpapersword.controller.GameManager;
import com.pwnz.www.rockpapersword.model.Soldier;
import com.pwnz.www.rockpapersword.model.SoldierMovement;
import com.pwnz.www.rockpapersword.model.Tile;

import java.util.ArrayList;

public class GamePanel extends SurfaceView implements Runnable {

    private boolean canPlay = false;
    private boolean isInMenuScreen = true;
    private GameManager manager;
    private Thread mPlayThread = null;
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private Bitmap mTtpBitmap, bg;
    private static Tile[][] tilesMatrix = null;
    private int mCanvasH, mCanvasW;


    public GamePanel(Context context) {
        super(context);
        bg = BitmapFactory.decodeResource(getResources(), R.drawable.chartest);
        mSurfaceHolder = getHolder();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void run() {

        while(canPlay) {

            if(!mSurfaceHolder.getSurface().isValid()){
                continue;
            }

            mCanvas = mSurfaceHolder.lockCanvas();
            mCanvasH = mCanvas.getHeight();
            mCanvasW = mCanvas.getWidth();

            if(isInMenuScreen){

            }
            else {
                drawTiles();
                drawSoldiers();
                drawPathArrows();
            }
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    private void drawPathArrows() {
        Bitmap bm = null;

        for (int i = 0; i < manager.getBoard().getPathArrows().size() ; i++) {
            if(manager.getBoard().getPathArrows().get(i) != null) {
                if (manager.getBoard().getPathArrows().get(i).second == SoldierMovement.MOVE_LEFT)
                    bm = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_left);
                else if (manager.getBoard().getPathArrows().get(i).second == SoldierMovement.MOVE_RIGHT)
                    bm = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_right);
                else if (manager.getBoard().getPathArrows().get(i).second == SoldierMovement.MOVE_UP)
                    bm = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_up);
                else if (manager.getBoard().getPathArrows().get(i).second == SoldierMovement.MOVE_DOWN)
                    bm = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_down);

                mCanvas.drawBitmap(bm, null, manager.getBoard().getPathArrows().get(i).first.getRect() ,null);
            }
        }
    }

    private void drawTiles() {

        Paint brush = new Paint();
        brush.setStyle(Paint.Style.FILL);

        for(Tile[] row : manager.getBoard().getTiles()){
            for(Tile tile : row){
                brush.setColor(tile.getColor());
                if(tile.getRect() != null)
                    mCanvas.drawRect(tile.getRect(), brush);
            }
        }
    }

    //TODO: remove this
    private void drawTiles2() {

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

    private void drawSoldiers() {
        drawSoldiersTeam(manager.getBoard().getSoldierTeamA());
        drawSoldiersTeam(manager.getBoard().getSoldierTeamB());
    }

    private void drawSoldiersTeam(ArrayList<Soldier> soldierTeam) {

        for(Soldier soldier: soldierTeam){
            if(soldier != null){
                //todo: move this to an earlier stage. (taking the advantage we got a context here and setting the bitmap)
                soldier.setSoldierBitmap(BitmapFactory.decodeResource(getResources(), soldier.getAnimationSprite()));
                mCanvas.drawBitmap(soldier.getSoldierBitmap(), null, soldier.getRectPosition(), null);
            }
        }
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

    private int toPxs(int dps){
        return (int)(dps * getResources().getDisplayMetrics().density + 0.5f);
    }

    public boolean isInMenuScreen() {
        return isInMenuScreen;
    }

    public void setInMenuScreen(boolean inMenuScreen) {
        isInMenuScreen = inMenuScreen;
    }

    public void setManager(GameManager manager) {
        this.manager = manager;
    }
}
