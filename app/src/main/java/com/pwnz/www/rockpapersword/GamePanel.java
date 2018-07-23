package com.pwnz.www.rockpapersword;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.pwnz.www.rockpapersword.controller.GameManager;
import com.pwnz.www.rockpapersword.model.Board;
import com.pwnz.www.rockpapersword.model.RPSClock;
import com.pwnz.www.rockpapersword.model.Soldier;
import com.pwnz.www.rockpapersword.model.SoldierMovement;
import com.pwnz.www.rockpapersword.model.SoldierType;
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

    private double fps, fts, ftm, ftn; //frames per seconds, frame time second/ms/ns
    private double framePerSecond, frameTimeSeconds ,frameTimeMs, frameTimeNs;
    private double lastFrameTime, endOfRenderTime, deltaTime;
    private RPSClock gameClock;
    public static final int GAME_IN_PROGRESS = -1;

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

            if(!mSurfaceHolder.getSurface().isValid()) {
                continue;
            }

            mCanvas = mSurfaceHolder.lockCanvas();
            mCanvasH = mCanvas.getHeight();
            mCanvasW = mCanvas.getWidth();

            if(isInMenuScreen){
                //todo: pre-game screen place_holder
            }
            else if(isGameFinished() != GAME_IN_PROGRESS){
                drawWinnerAnnouncement(manager.getWinningTeam());
            }
            else {
                drawTiles();
                drawSoldiers();
                drawPathArrows();
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

            //sleep for the missing delta time of the interval frame time between each frame.
            try {
                if(deltaTime > 0 )
                    mPlayThread.sleep((long) deltaTime/1000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            lastFrameTime = System.nanoTime();

            timeMillis = System.currentTimeMillis();
            endTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis);

            //todo: @Idan @shay - impl this
            //if updateTime == true, clock resets and we need to tell GameManager so force a turn swap:
            if(gameClock.updateTime(endTimeSeconds - startTimeSeconds)){
                //forceTurnSwap()
            }
        }
    }

    /**
     * an animation screen that is displayed after a win / lose
     * @param winningTeam {0 = TEAM_A},{1 = TEAM_B}, {-1 = NONE}
     * @return none
     */
    private void drawWinnerAnnouncement(int winningTeam) {

        if(winningTeam == manager.getBoard().TEAM_A){
            manager.getBoard().getLoseAnnouncementAnimation().drawAnimation(mCanvas);
            manager.getBoard().getLoseAnnouncementAnimation().chooseNextFrame();
        }
        else{
            manager.getBoard().getWinAnnouncementAnimation().drawAnimation(mCanvas);
            manager.getBoard().getWinAnnouncementAnimation().chooseNextFrame();
        }

        //sleep a bit between each frame to slow animation down:
        try {
            if(deltaTime > 0 )
                mPlayThread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private int isGameFinished() {
        return manager.getWinningTeam();
    }

    private void drawMatch() {
        //todo: possible logic simplification: use 1 animation that holds both players in match with all permutations @shay

        //todo: @shay - replace the values with constants
        Soldier soldierA = manager.getBoard().getMatchSoldierTeamA().get(0);
        Soldier soldierB = manager.getBoard().getMatchSoldierTeamB().get(0);

        if(soldierA == null || soldierB == null)
            System.out.println("drawMatch: one of the fighting soldiers is null.");

        soldierA.drawAnimation(mCanvas);
        soldierB.drawAnimation(mCanvas);

        boolean aAnimationEnded = soldierA.chooseNextFrame();
        boolean bAnimationEnded = soldierB.chooseNextFrame();

        //if animation ended, set match off & remove match animation from screen:
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

    /**
     * updated delta time for the gameloop drawings
     * @return none
     */
    private void update() {
        if (deltaTime < 0 )
            deltaTime = frameTimeSeconds - deltaTime;

    }

    /**
     * after a Player soldier has been clicked (focused), display path arrows to indicate
     * valid tiles he can move to.
     * @return none
     */
    private void drawPathArrows() {
        Bitmap bm = null;

        if(manager.getBoard().getPathArrows().isEmpty())
            return;

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

    private void drawSoldiers() {
        drawSoldiersTeam(manager.getBoard().getSoldierTeamA());
        drawSoldiersTeam(manager.getBoard().getSoldierTeamB());
    }

    private void drawSoldiersTeam(ArrayList<Soldier> soldierTeam) {
        synchronized (soldierTeam){
            for(Soldier soldier: soldierTeam){
                if(soldier != null){
                    mCanvas.drawBitmap(soldier.getSoldierBitmap(), null, soldier.getTile().getRect(), null);
                }
            }
        }
    }

    public void pause(){
        canPlay = false;

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


    public void setInMenuScreen(boolean inMenuScreen) {
        isInMenuScreen = inMenuScreen;
    }

    public void setManager(GameManager manager) {
        this.manager = manager;
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
