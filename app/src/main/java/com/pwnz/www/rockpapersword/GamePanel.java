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
    private int mCanvasH, mCanvasW;


    public GamePanel(Context context) {
        super(context);
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
                mCanvas.drawBitmap(soldier.getSoldierBitmap(), null, soldier.getTile().getRect(), null);
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
