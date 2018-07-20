package com.pwnz.www.rockpapersword;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.pwnz.www.rockpapersword.controller.GameManager;
import com.pwnz.www.rockpapersword.model.Board;
import com.pwnz.www.rockpapersword.model.RPSClock;
import com.pwnz.www.rockpapersword.model.Soldier;
import com.pwnz.www.rockpapersword.model.SoldierMovement;
import com.pwnz.www.rockpapersword.model.Tile;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class GamePanel extends SurfaceView implements Runnable {

    public static final int MAX_FPS = 60;
    private boolean canPlay = false;
    private boolean isInMenuScreen = true;
    private boolean shouldDrawClock = true;
    private GameManager manager;
    private Thread mPlayThread = null;
    private Canvas mCanvas;
    private SurfaceHolder mSurfaceHolder;
    private int mCanvasH, mCanvasW;
    private boolean redraw = false;
    private boolean matchRectsInitialized = false;

    private double fps, fts, ftm, ftn; //frames per seconds, frame time second/ms/ns
    private double framePerSecond, frameTimeSeconds ,frameTimeMs, frameTimeNs;
    private double lastFrameTime, endOfRenderTime, deltaTime;
    private RPSClock gameClock;


    public GamePanel(Context context) {
        super(context);
        gameClock = new RPSClock(this.getContext());
        mSurfaceHolder = getHolder();
        initFrameTimes();
    }

    private void initFrameTimes() {
        deltaTime = 0;
        framePerSecond = MAX_FPS;
        frameTimeSeconds = 1 / framePerSecond;
        frameTimeMs = framePerSecond * 1000;
        frameTimeNs = framePerSecond * 1000000;

        //converters:
        //1s = 1,000 ms
        //1s = 1,000,000,000 ns
        //1ms = 1,000,000 ns

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void run() {

        long timeMillis;
        long startTimeSeconds, endTimeSeconds;


        lastFrameTime = System.nanoTime();
        deltaTime = 0;

        while(canPlay) {

            timeMillis = System.currentTimeMillis();
            startTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis);

            update();

            if(!mSurfaceHolder.getSurface().isValid()) { //todo: implement this  || !shouldRedraw()){
                continue;
            }

            mCanvas = mSurfaceHolder.lockCanvas();
            mCanvasH = mCanvas.getHeight();
            mCanvasW = mCanvas.getWidth();

            if(isInMenuScreen){
                //todo: implement a menu screen later
            }
            else {
                    drawTiles();
                    drawSoldiers();
                    //todo: fix and enable this function (it procs ArrayOutOfBounds Exception) @idan
                    //drawPathArrows();
                    drawClock();
                    drawJudges();

                if(isMatchOn()){
                    drawMatch();
                }
            }
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            setRedraw(false);

            endOfRenderTime = System.nanoTime();
            deltaTime = frameTimeNs - (endOfRenderTime - lastFrameTime);

            try {
                if(deltaTime > 0 )
                    mPlayThread.sleep((long) deltaTime/1000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            lastFrameTime = System.nanoTime();

            timeMillis = System.currentTimeMillis();
            endTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis);
            System.out.println("endTimeSeconds - startTimeSeconds= " + (endTimeSeconds - startTimeSeconds));

            //todo: impl this
            //if updateTime == true, clock resets and we need to tell GameManager so force a turn swap:
            if(gameClock.updateTime(endTimeSeconds - startTimeSeconds)){

            }

        }
    }

    private void drawMatch() {
        //todo: possible logic simplification: use 1 animation that holds both players in match with all permutations @shay

        Soldier soldierA = manager.getFightingSoldier(Board.TEAM_A);
        Soldier soldierB = manager.getFightingSoldier(Board.TEAM_B);

        if(soldierA == null || soldierB == null)
            System.out.println("drawMatch: one of the fighting soldiers is null.");

        soldierA.drawAnimation(mCanvas);
        soldierB.drawAnimation(mCanvas);

        boolean aAnimationEnded = soldierA.chooseNextFrame();
        boolean bAnimationEnded = soldierB.chooseNextFrame();

        //todo: @shay
        //if animation ended, set match off
        if(aAnimationEnded && bAnimationEnded){
            manager.setMatchOn(false);
        }

    }

    private boolean isMatchOn() {
        return manager.getIsMatchOn();
    }

    private void drawJudges() {

    }

    private void drawClock() {
        if(shouldDrawClock)
            gameClock.drawAnimation(mCanvas);
    }

    private void update() {
        if (deltaTime < 0 )
            deltaTime = frameTimeSeconds - deltaTime;

    }

    private void drawPathArrows() {
        Bitmap bm = null;


        System.out.println("drawPathArrows called.");
        System.out.println("manager.getBoard().getPathArrows().size() = " + manager.getBoard().getPathArrows().size());
        for (int i = 0; i < manager.getBoard().getPathArrows().size() ; i++) {
            if(manager.getBoard().getPathArrows().get(i) != null) {
                System.out.println("i = " + i);
                System.out.println("manager.getBoard().getPathArrows().get(i).second = " + manager.getBoard().getPathArrows().get(i).second);
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

    public boolean shouldRedraw() {
        return redraw;
    }

    public void setRedraw(boolean redraw) {
        this.redraw = redraw;
    }

    public void stopClock(){
        shouldDrawClock = false;
    }

    public void resetClock() {
        gameClock.resetToFirstFrame();
        shouldDrawClock = true;
    }
}
