package com.pwnz.www.rockpapersword;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.pwnz.www.rockpapersword.Activities.GameActivity;
import com.pwnz.www.rockpapersword.Activities.MainMenuActivity;
import com.pwnz.www.rockpapersword.controller.GameManager;
import com.pwnz.www.rockpapersword.model.AnimationHandler;
import com.pwnz.www.rockpapersword.model.Board;
import com.pwnz.www.rockpapersword.model.RPSClock;
import com.pwnz.www.rockpapersword.model.Soldier;
import com.pwnz.www.rockpapersword.model.SoldierMovement;
import com.pwnz.www.rockpapersword.model.Tile;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * acts as the View (UI Thread). Only responsible of drawing.
 */
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
    private int COUNTDOWN_VALUE = 80;
    private int judgeAnimationCountdown = COUNTDOWN_VALUE ;

    private double fps, fts, ftm, ftn; //frames per seconds, frame time second/ms/ns
    private double framePerSecond, frameTimeSeconds ,frameTimeMs, frameTimeNs;
    private double lastFrameTime, endOfRenderTime, deltaTime;
    private RPSClock gameClock;
    private Bitmap bg;

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
            else if(isGameFinished() != GameActivity.GAME_IN_PROGRESS){
                drawWinnerAnnouncement(manager.getWinningTeam());
                MainMenuActivity.pauseMusic();
            }
            else {
                drawBg();
                drawTiles();
                drawSoldiers();
                drawPathArrows();
                drawClock();
                drawMenuButtonIngame();

                if(inTie()) {
                    //reset timer and let the user choose a new weapon
                    gameClock.resetToFirstFrame();
                    drawTieOptions();
                }
                else
                    drawJudges();

                if(isMenuOpen())
                   drawMenu();
                else if(isMatchOn())
                    drawMatch();

            }
            mSurfaceHolder.unlockCanvasAndPost(mCanvas);
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

            //if updateTime == true, clock resets and we need to tell GameManager so force a turn swap:
            if(!isMenuOpen() && gameClock.updateTime(endTimeSeconds - startTimeSeconds)){
                manager.swapTurns();
            }
        }
    }

    private void drawTieOptions() {
        manager.getBoard().getNewWeaponPaper().drawAnimation(mCanvas);
        manager.getBoard().getNewWeaponRock().drawAnimation(mCanvas);
        manager.getBoard().getNewWeaponSword().drawAnimation(mCanvas);

        manager.getBoard().getNewWeaponPaper().chooseNextFrame();
        manager.getBoard().getNewWeaponRock().chooseNextFrame();
        manager.getBoard().getNewWeaponSword().chooseNextFrame();


    }

    private boolean inTie() {
        return manager.isInTie();
    }

    private void drawMenu() {
        manager.getBoard().getResumeGameBtn().drawAnimation(mCanvas);
        manager.getBoard().getBackToMenuBtn().drawAnimation(mCanvas);

    }

    private void drawMenuButtonIngame() {
        manager.getBoard().getMenuIngameBtn().drawAnimation(mCanvas);
        manager.getBoard().getMenuIngameBtn().chooseNextFrame();
    }

    /**
     * an animation screen that is displayed after a win / lose
     * @param winningTeam {0 = TEAM_A},{1 = TEAM_B}, {-1 = NONE}
     * @return none
     */
    private void drawWinnerAnnouncement(int winningTeam) {
        boolean endOfFrame = false;

        if(winningTeam == manager.getBoard().TEAM_A){
            manager.getBoard().getLoseAnnouncementAnimation().drawAnimation(mCanvas);
            endOfFrame = manager.getBoard().getLoseAnnouncementAnimation().chooseNextFrame();
        }
        else{
            manager.getBoard().getWinAnnouncementAnimation().drawAnimation(mCanvas);
            endOfFrame = manager.getBoard().getWinAnnouncementAnimation().chooseNextFrame();
        }
        //sleep a bit between each frame to slow animation down:
        if(endOfFrame){
            try {
                if(deltaTime > 0 )
                    mPlayThread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private int isGameFinished() {
        return manager.getWinningTeam();
    }

    private void drawMatch() {

        AnimationHandler matchAnimation = manager.getBoard().getMatchAnimationsMap().get(manager.getMatchFighters());

        if(matchAnimation== null)
            System.out.println("drawMatch: match animation handler is null.");

        matchAnimation.drawAnimation(mCanvas);

        boolean endOfAnimation = matchAnimation.chooseNextFrame();

        //if animation ended, set match off & remove match animation from screen:
        if(endOfAnimation){
            manager.setMatchOn(false);
            gameClock.resetToFirstFrame();
        }

    }

    private void drawBg() {
        mCanvas.drawBitmap(manager.getBoard().getGameBg().getSpriteSheet(), null, manager.getBoard().getGameBg().getDestRect(), null);
    }

    private boolean isMatchOn() {
        return manager.getIsMatchOn();
    }

    private void drawJudges() {
        manager.getBoard().getJudge().drawAnimation(mCanvas);

        if(judgeAnimationCountdown > 0) {
            --judgeAnimationCountdown;
        }
        else {
            if(manager.getBoard().getJudge().chooseNextFrame()){
                judgeAnimationCountdown = COUNTDOWN_VALUE;
            }


        }
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
                if(soldier != null && soldier.isVisible()){
                    changeOffset(soldier.getTile().getRect(), soldier.getTileOffset()*(-1));
                    mCanvas.drawBitmap(soldier.getSoldierBitmap(), null, soldier.getTile().getRect(), null);
                    changeOffset(soldier.getTile().getRect(), soldier.getTileOffset());

                    //if this soldier is from team B and has been revealed, mark it with a reveal mark:
                    if(soldier.hasBeenRevealed() && soldier.getTeam() != Board.TEAM_A){
                        mCanvas.drawBitmap(manager.getBoard().getRevealMark(), null, soldier.getTile().getRect(), null);
                    }
                }
            }
        }
    }

    private void changeOffset(Rect rect, int offset) {
        rect.top += offset;
        rect.bottom += offset;
    }

    public void pause(){
        canPlay = false;

        while (true) {
            try {
                if(mPlayThread != null)
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

    public boolean isMenuOpen() {
        return manager.menuOpen();
    }
}
