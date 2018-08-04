package com.pwnz.www.rockpapersword.model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;
import android.util.Pair;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.pwnz.www.rockpapersword.R;
import com.pwnz.www.rockpapersword.controller.GameManager;

/**
 * Acts as the data (model) component.
 * Responsible for allocating and initializing all game components before they reach the UI Thread.
 * Manipulating and updating values and performing computations on the game object. Using the GameManager
 * as controller to communicate with the Gui.
 */

public class Board {

    Tile[][] tiles;
    ArrayList<Soldier> soldierTeamA = new ArrayList<>();
    ArrayList<Soldier> soldierTeamB = new ArrayList<>();

    ArrayList<Pair<Tile, SoldierMovement>> pathArrows = new ArrayList<>();
    AnimationHandler winAnnouncementAnimation, loseAnnouncementAnimation, judge;
    AnimationHandler openMenuIngameBtn, resumeGameBtn, backToMenuBtn;
    AnimationHandler newWeaponSword, newWeaponRock, newWeaponPaper;
    HashMap<String, AnimationHandler> matchAnimationsMap = new HashMap<>();

    int cols, rows;
    int canvasW, canvasH;
    int tileW, tileH;
    int brightColor, darkColor;
    public static final int TILE_OFFSET_PERCENTAGE = 6 ;
    public static final int TEAM_A = 0;
    public static final int TEAM_B = 1;
    private static final int DEFAULT_SHUFFLE_TIMES = 2;

    private GameManager manager;
    private AnimationHandler gameBg;
    private Bitmap revealMark;
    private Rect matchAnimationPosition;
    private GameStorage storage;

    public Board(int cols, int rows, int canvasW, int canvasH, int brightColor, int darkColor) {
        this.cols = cols;
        this.rows = rows;
        this.canvasW = canvasW;
        this.canvasH = canvasH;
        allocateTiles();
        allocateSoldierTeam(soldierTeamA, cols*2);
        allocateSoldierTeam(soldierTeamB, cols*2);
        setBrightColor(brightColor);
        setDarkColor(darkColor);
    }

    /**
     * initiation of the list holding all of the match animations.
     */
    private void initMatchAnimationsMap(){
        initAshesMatchAnimation();
        initStoneMatchAnimation();
        initKingMatchAnimation();
        initPepperMatchAnimation();
        initShieldonMatchAnimation();
        initSwordmanMatchAnimation();
    }


    private void initSwordmanMatchAnimation() {
        matchAnimationsMap.put(GameStorage.SW_KI, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_sword_king_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.SW_ST, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_sword_stone_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.SW_PE, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_sword_paper_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.SW_SH, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_sword_shield_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.SW_AS, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_sword_ashes_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.SW_SW, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_sword_sword_min, 6, 5, matchAnimationPosition));
    }

    private void initShieldonMatchAnimation() {
        matchAnimationsMap.put(GameStorage.SH_KI, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_shield_king_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.SH_ST, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_shield_stone_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.SH_PE, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_shield_paper_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.SH_SH, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_shield_shield_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.SH_AS, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_shield_ashes_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.SH_SW, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_shield_sword_min, 6, 5, matchAnimationPosition));
    }

    private void initPepperMatchAnimation() {
        matchAnimationsMap.put(GameStorage.PE_KI, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_paper_king_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.PE_ST, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_paper_stone_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.PE_PE, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_paper_paper_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.PE_SH, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_paper_shield_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.PE_AS, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_paper_ashes_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.PE_SW, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_paper_sword_min, 6, 5, matchAnimationPosition));
    }

    private void initKingMatchAnimation() {
        matchAnimationsMap.put(GameStorage.KI_KI, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_king_king_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.KI_ST, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_king_stone_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.KI_PE, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_king_paper_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.KI_SH, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_king_shield_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.KI_AS, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_king_ashes_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.KI_SW, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_king_sword_min, 6, 5, matchAnimationPosition));
    }

    private void initStoneMatchAnimation() {
        matchAnimationsMap.put(GameStorage.ST_KI, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_stone_king_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.ST_ST, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_stone_stone_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.ST_PE, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_stone_paper_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.ST_SH, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_stone_shield_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.ST_AS, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_stone_ashes_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.ST_SW, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_stone_sword_min, 6, 5, matchAnimationPosition));
    }

    private void initAshesMatchAnimation() {
        matchAnimationsMap.put(GameStorage.AS_KI, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_ashes_king_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.AS_ST, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_ashes_stone_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.AS_PE, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_ashes_paper_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.AS_SH, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_ashes_shield_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.AS_AS, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_ashes_ashes_min, 6, 5, matchAnimationPosition));
        matchAnimationsMap.put(GameStorage.AS_SW, new AnimationHandler(manager.getPanelContext(), R.drawable.match_sprite_ashes_sword_min, 6, 5, matchAnimationPosition));
    }




    /**
     * This Rect holds the position in which the match animation should appear in.
     */
    private void initMatchAnimationPosition() {
        matchAnimationPosition = new Rect();
        matchAnimationPosition.left   = canvasW/4;
        matchAnimationPosition.top    = canvasH/3;
        matchAnimationPosition.right  = matchAnimationPosition.left * 3;
        matchAnimationPosition.bottom = matchAnimationPosition.top  * 2;
    }

    private void allocateSoldierTeam(ArrayList<Soldier> soldierTeam, int size) {

        for (int j = 0; j < size; j++) {
            soldierTeam.add(new Soldier());
        }
    }

    private void allocateTiles() {
        tiles = new Tile[cols][rows];

        for (int i = 0; i < cols ; i++) {
            for (int j = 0; j < rows  ; j++) {
                tiles[i][j] = new Tile();
            }
        }
    }

    /**
     * Initiation of the board. boardPadding is the gap of top and bottom screen where the
     * board shouldn't be drawn. H and W Divisors are actually rows and cols count.
     * We treat our screen as a grid.
     * @param boardPaddingFactor how much to pad our top and bottom
     * @param hDivisor how many divisions for height
     * @param wDivisor how many divisions for width
     */
    public void initBoard(int boardPaddingFactor, int hDivisor, int wDivisor){
        tileW = canvasW / wDivisor;
        tileH = canvasH / hDivisor;

        //this will give us a clean pad from the top of the screen which the board will not be drawn at
        int boardPadding = boardPaddingFactor * tileH;
        storage = new GameStorage(manager.getAppResources());

        initBg(canvasH, canvasW, R.drawable.game_bg);
        initTiles(boardPadding, tileW, tileH);
        initSoldiers(soldierTeamA, TEAM_A, 0);
        initSoldiers(soldierTeamB, TEAM_B, 4);
        initJudge(R.drawable.judge_nutral_sprite);
        initWinningTeamAnnouncementAnimation();
        initInGameMenu();
        initNewWeaponsChoiceOnTie();
        initMatchAnimationPosition();
        initMatchAnimationsMap();
        setRevealMark(BitmapFactory.decodeResource(manager.getAppResources(), R.drawable.reveal_mark));
    }

    private void initNewWeaponsChoiceOnTie() {
        initNewWeaponSword();
        initNewWeaponRock();
        initNewWeaponPaper();

    }

    private void initInGameMenu() {
        initMenuIngameButton();
        initResumeIngameButton();
        initBackToMenuButton();
    }

    private void initNewWeaponPaper() {
        newWeaponPaper = new AnimationHandler();
        newWeaponPaper.initAnimationDetails(manager.getPanelContext(), R.drawable.paper_solo_sprite, 3, 5);
        int left = (canvasW/2) + (newWeaponPaper.spriteFrameSrcW/2)*3;
        int right = (canvasW/2) + (newWeaponPaper.spriteFrameSrcW/2)*6;
        int top = 0;
        int bot = (int) ((canvasH/10) * 1.5);

        newWeaponPaper.destRect = new Rect (left, top, right, bot);
        newWeaponPaper.resetToFirstFrame();
    }

    private void initNewWeaponRock() {
        newWeaponRock = new AnimationHandler();
        newWeaponRock.initAnimationDetails(manager.getPanelContext(), R.drawable.stone_solo_sprite, 3, 5);
        int left = (canvasW/2) - (newWeaponSword.spriteFrameSrcW/2)*6;
        int right = (canvasW/2) - (newWeaponSword.spriteFrameSrcW/2)*3;
        int top = 0;
        int bot = (int) ((canvasH/10) * 1.5);

        newWeaponRock.destRect = new Rect (left, top, right, bot);
        newWeaponRock.resetToFirstFrame();
    }

    private void initNewWeaponSword() {
        newWeaponSword = new AnimationHandler();
        newWeaponSword.initAnimationDetails(manager.getPanelContext(), R.drawable.sword_solo_sprite, 3, 5);
        int left = (canvasW/2) - tileW; // - (newWeaponSword.spriteFrameSrcW/2)*6;
        int right = (canvasW/2) +tileW; // - (newWeaponSword.spriteFrameSrcW/2)*3;
        int top = 0;
        int bot = (int) ((canvasH/10) * 1.5);

        newWeaponSword.destRect = new Rect (left, top, right, bot);
        newWeaponSword.resetToFirstFrame();
    }

    private void initBackToMenuButton() {
        backToMenuBtn = new AnimationHandler();
        backToMenuBtn.initAnimationDetails(manager.getPanelContext(), R.drawable.back_to_main_menu, 1, 1);
        int left = (canvasW/2) - (backToMenuBtn.spriteFrameSrcW/2);
        int right = (canvasW/2) + (backToMenuBtn.spriteFrameSrcW/2);
        int top = (canvasH/2) - (backToMenuBtn.spriteFrameSrcH);
        int bot = (canvasH/2);

        backToMenuBtn.destRect = new Rect (left, top, right, bot);
        backToMenuBtn.resetToFirstFrame();

    }

    private void initResumeIngameButton() {
        resumeGameBtn = new AnimationHandler();
        resumeGameBtn.initAnimationDetails(manager.getPanelContext(), R.drawable.resume_game, 1, 1);

        int left = (canvasW/2) - (resumeGameBtn.spriteFrameSrcW/2);
        int right = (canvasW/2) + (resumeGameBtn.spriteFrameSrcW/2);
        int top = (canvasH/2);
        int bot = (canvasH/2) + (resumeGameBtn.spriteFrameSrcH);

        resumeGameBtn.destRect = new Rect (left, top, right, bot);
        resumeGameBtn.resetToFirstFrame();
    }

    private void initMenuIngameButton() {
        openMenuIngameBtn = new AnimationHandler();
        openMenuIngameBtn.initAnimationDetails(manager.getPanelContext(), R.drawable.open_menu_ingame, 1, 1);
        int left = (openMenuIngameBtn.canvasW/2) - (openMenuIngameBtn.spriteFrameSrcW/2);
        int top = canvasH - openMenuIngameBtn.spriteSheetH;
        int right = (openMenuIngameBtn.canvasW/2) + (openMenuIngameBtn.spriteFrameSrcW/2);
        int bot = canvasH;

        openMenuIngameBtn.destRect = new Rect(left, top, right, bot);
        openMenuIngameBtn.resetToFirstFrame();
    }

    private void initJudge(int spriteId) {
        judge = new AnimationHandler();
        judge.initAnimationDetails(manager.getPanelContext(), spriteId, 3, 5);

        int left = (canvasW/2) - tileW;
        int top = 0;
        int right = (judge.canvasW/2) + tileW;
        int bot = tileH * 2;

        judge.destRect = new Rect(left, top, right, bot);
        judge.resetToFirstFrame();
    }

    private void initBg(int canvasH, int canvasW, int bg) {
        gameBg = new AnimationHandler();
        gameBg.spriteSheet = BitmapFactory.decodeResource(manager.getAppResources(), bg);
        gameBg.sourceRect = new Rect(0, 0, gameBg.spriteSheet.getWidth(), gameBg.spriteSheet.getHeight());
        gameBg.destRect = new Rect(0, 0, canvasW, canvasH);

    }

    /**
     * Initiation of the win/lose announcement animation.
     */
    private void initWinningTeamAnnouncementAnimation() {

        winAnnouncementAnimation = new AnimationHandler();
        loseAnnouncementAnimation = new AnimationHandler();

        winAnnouncementAnimation.initAnimationDetails(manager.getPanelContext(), R.drawable.win_anim, 2, 5);
        loseAnnouncementAnimation.initAnimationDetails(manager.getPanelContext(), R.drawable.lose_anim, 2, 5);

        loseAnnouncementAnimation.destRect = new Rect(0, 0, canvasW, canvasH);
        loseAnnouncementAnimation.resetToFirstFrame();

        winAnnouncementAnimation.destRect = new Rect(0, 0, canvasW, canvasH);
        winAnnouncementAnimation.resetToFirstFrame();
    }

    private void initTiles(int boardPadding, int tileW, int tileH) {
        int colorPicks[] = {brightColor, darkColor};
        int pickCycle = 0;

        for (int i = 0; i < cols ; i++, pickCycle = (pickCycle == 0) ? 1 : 0) {
            for (int k = 0; k < rows ; k++) {
                // get matching rect positions according to the iterated tile
                Rect rect = new Rect(i * tileW, k * tileH + boardPadding, (i+1) * tileW, (k+1) * tileH + boardPadding);

                // cyclic choice between the 2 colors
                pickCycle = (pickCycle == 0) ? 1 : 0;

                tiles[i][k].setColor(colorPicks[pickCycle]);
                tiles[i][k].setOccupied(false);
                tiles[i][k].setRect(rect);
                tiles[i][k].setCurrSoldier(null);

            }
        }
    }

    public void eliminateSoldier(Soldier soldier){
        Log.d("MEGA_DBG","\n eliminating: \n" + soldier);
        ArrayList<Soldier> removeFrom = soldier.getTeam() == Board.TEAM_A ? soldierTeamA : soldierTeamB;
        synchronized (removeFrom){
            soldier.setVisible(false);
            soldier.getTile().setOccupied(false);
            soldier.getTile().setCurrSoldier(null);
            removeFrom.remove(soldier);
        }
    }

    public void eliminateBoth(Soldier potentialInitiator, Soldier opponent){
        eliminateSoldier(potentialInitiator);
        eliminateSoldier(opponent);
    }

    private void initSoldiers(ArrayList<Soldier> soldiersTeam, int team, int SOLDIERS_START_ROW) {

        Soldier soldier;
        for (int i = 0, j = SOLDIERS_START_ROW, k = 0; i < soldiersTeam.size() ; i++, k++) {
            soldier = soldiersTeam.get(i);
            soldier.setTeam(team);
            soldier.setSoldierType(pickAvailableSoldierType());
            soldier.getBitmapsByType();

            soldier.setRevealed(false);
            soldier.setVisible(true);
            tiles[k % cols][j].setOccupied(true);
            tiles[k % cols][j].setCurrSoldier(soldier);
            soldier.setTile(tiles[k % cols][j]);
            soldier.setTileOffset(tileW / TILE_OFFSET_PERCENTAGE);

            //stop over the the next tile row
            if(i == (soldiersTeam.size()-1) / 2)
                ++j;
        }
    }

    /**
     * @return an available type from our soldierTypeList in Soldier class.
     */
    private SoldierType pickAvailableSoldierType() {
        return Soldier.pickAvailableSoldierType();
    }

    public Soldier getClickedSoldier(float x, float y) {
        //find clicked soldier on self team
        for (Soldier soldier : soldierTeamB){
            if(isInside(soldier.getTile().getRect(), x,y) == true)
                return soldier.isVisible() ? soldier : null;
        }
        return null;
    }

    /**
     * Determines whether a a rectangle has been pressed on screen according to x,y
     * @param rectPosition position of the soldier's occupied tile (rectangle based)
     * @param x pos
     * @param y pos
     * @return true = pressed, false = not pressed
     */
    private boolean isInside(Rect rectPosition, float x, float y) {
        if(rectPosition.left <= x && rectPosition.right >= x){
            if(rectPosition.top <= y && rectPosition.bottom >= y){
                return true;
            }
        }
        return false;
    }

    /**
     * Displays arrows indicating the eligible tiles to move to
     * @param focusedSoldier the clicked soldier
     */
    public void displaySoldierPath(Soldier focusedSoldier) {
        highlightPathArrows(focusedSoldier);
        focusedSoldier.highlight();
    }

    /**
     * @see #displaySoldierPath(Soldier)
     * @param focusedSoldier Soldier
     */
    private void highlightPathArrows(Soldier focusedSoldier) {
        Tile tile = null;
        Integer[] xyPos = new Integer[2];

        if(focusedSoldier != null)
            getTileIndex(focusedSoldier.getTile(), xyPos);

        //is left neighbor exist and unoccupied?
        if(xyPos[0]-1 > -1 )
            addNeighbor(pathArrows, tile, xyPos[0]-1, xyPos[1], SoldierMovement.MOVE_LEFT );
        //is right neighbor exist and unoccupied?
        if(xyPos[0]+1 < cols )
            addNeighbor(pathArrows, tile, xyPos[0]+1, xyPos[1], SoldierMovement.MOVE_RIGHT );
        //is top neighbor exist and unoccupied?
        if(xyPos[1]-1 > -1 )
            addNeighbor(pathArrows, tile, xyPos[0], xyPos[1]-1, SoldierMovement.MOVE_UP );
        //is bottom neighbor exist and unoccupied?
        if(xyPos[1]+1 < rows )
            addNeighbor(pathArrows, tile, xyPos[0], xyPos[1]+1, SoldierMovement.MOVE_DOWN );
    }

    /**
     * add a valid neighbor tile to be marked with an arrow indicator
     * @see #pathArrows
     * @param surroundingTiles all surrounding tiles around the soldier (4)
     * @param tile soldier's current tile
     * @param x soldier's x tile pos
     * @param y soldier's y tile pos
     * @param move LEFT / RIGHT / UP / DOWN
     */
    private void addNeighbor(ArrayList<Pair<Tile, SoldierMovement>> surroundingTiles, Tile tile, Integer x, Integer y, SoldierMovement move){
        tile = getTiles()[x][y];
        if(tile.isOccupied() == false){
            surroundingTiles.add(new Pair<>(tile, move));
        }
    }

    public void getTileIndex(Tile tile , Integer[] pos) {
        for (int i = 0; i < cols ; i++) {
            for (int j = 0; j < rows  ; j++) {
                if(getTiles()[i][j] == tile){
                    pos[0] = i;
                    pos[1] = j;
                    return;
                }
            }
        }
    }

    public Tile getTileAt(float x, float y) {
        for(Pair<Tile, SoldierMovement> pair : pathArrows){
            if(isInside(pair.first.getRect(), x, y) == true){
                return pair.first;
            }
        }
        return null;
    }

    /**
     * picks up a random soldier for the AI to play with on its turn.
     * will only retrurn a moveable soldir
     * @return valid soldier to play with
     */
    public Soldier getRandomSoldier() {
        Soldier AISoldier = null;
        int randIdx;
        do {
            randIdx = (int )(Math.random() * soldierTeamA.size() - 1 + 0);
            AISoldier = soldierTeamA.get(randIdx);
            highlightPathArrows(AISoldier);
            if(pathArrows.isEmpty())
                AISoldier = null;

        }while(AISoldier == null);

        return AISoldier;
    }

    public Tile getTraversalTile() {
        int randIdx = (int )(Math.random() * pathArrows.size()-1 + 0);
        return pathArrows.get(randIdx).first;
    }

    /**
     * Seeks for the first surrounding soldier next to the initiator
     * @param initiator the soldier that seeks for a match
     * @return the first surrounding opponent
     */
    public Soldier getFirstSurroundingOpponent(Soldier initiator) {
        Integer[] xyPos = new Integer[2];
        getTileIndex(initiator.getTile(), xyPos);
        int newX, newY;

        //potential opponent to the left
        if(xyPos[0]-1 > -1){
            newX = xyPos[0]-1;
            newY = xyPos[1];
            if(isValidOpponent(newX, newY, initiator)) {
                Log.d("MEGA_DBG", "getFirstSurroundingOpponent: FOUND LEFT.\n");
                Log.d("MEGA_DBG", getTiles()[newX][newY].getCurrSoldier().toString());
                return getTiles()[newX][newY].getCurrSoldier();
            }
        }
        //potential opponent to the right
        if(xyPos[0]+1 < cols ) {
            newX = xyPos[0]+1;
            newY = xyPos[1];
            if (isValidOpponent(newX, newY, initiator)){
                Log.d("MEGA_DBG", "getFirstSurroundingOpponent: FOUND RIGHT.\n");
                Log.d("MEGA_DBG", getTiles()[newX][newY].getCurrSoldier().toString());
                return getTiles()[newX][newY].getCurrSoldier();
            }

        }
        //potential opponent to the top
        if(xyPos[1]-1 > -1 ) {
            newX = xyPos[0];
            newY = xyPos[1]-1;
            if (isValidOpponent(newX, newY, initiator)) {
                Log.d("MEGA_DBG", "getFirstSurroundingOpponent: FOUND TOP.\n");
                Log.d("MEGA_DBG", getTiles()[newX][newY].getCurrSoldier().toString());
                return getTiles()[newX][newY].getCurrSoldier();
            }
        }
        //potential opponent to the bottom
        if(xyPos[1]+1 < rows ) {
            newX = xyPos[0];
            newY = xyPos[1]+1;
            if (isValidOpponent(newX, newY, initiator)) {
                Log.d("MEGA_DBG", "getFirstSurroundingOpponent: FOUND BOTTOM.\n");
                Log.d("MEGA_DBG", getTiles()[newX][newY].getCurrSoldier().toString());
                return getTiles()[newX][newY].getCurrSoldier();
            }
        }
        Log.d("MEGA_DBG", "getFirstSurroundingOpponent: FOUND NO ONE.\n");
        return null;
    }

    private boolean isValidOpponent(int newX, int newY, Soldier initiator) {
        return getTiles()[newX][newY].isOccupied() &&
               getTiles()[newX][newY].getCurrSoldier().getTeam() != initiator.getTeam();
    }

    public void setManager(GameManager manager) {
        this.manager = manager;
    }

    public AnimationHandler getWinAnnouncementAnimation() {
        return winAnnouncementAnimation;
    }

    public AnimationHandler getLoseAnnouncementAnimation() {
        return loseAnnouncementAnimation;
    }

    public ArrayList<Soldier> getSoldierTeamA() {
        return soldierTeamA;
    }

    public ArrayList<Soldier> getSoldierTeamB() {
        return soldierTeamB;
    }

    public void setBrightColor(int brightColor) {
        this.brightColor = brightColor;
    }

    public void setDarkColor(int darkColor) {
        this.darkColor = darkColor;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public int getCanvasW() {
        return canvasW;
    }

    public int getCanvasH() {
        return canvasH;
    }

    public ArrayList<Pair<Tile, SoldierMovement>> getPathArrows() {
        return pathArrows;
    }
    public AnimationHandler getGameBg() {
        return gameBg;
    }

    public AnimationHandler getJudge() {
        return judge;
    }

    public Bitmap getRevealMark() {
        return revealMark;
    }

    public void setRevealMark(Bitmap revealMark) {
        this.revealMark = revealMark;
    }

    public void shuffleTeams(ArrayList<Soldier> soldierTeam) {
        ArrayList<Tile> tempTileList = new ArrayList<>();

        //copy all types into new list
        for (int i = 0; i < soldierTeam.size() ; i++) {
            tempTileList.add(soldierTeam.get(i).getTile());
        }

        //now shuffle it
        for (int i = 0; i < DEFAULT_SHUFFLE_TIMES ; i++) {
            Collections.shuffle(tempTileList);
        }

        //reassign new shuffled tiles to all soldiers and vice versa
        for (int i = 0; i < soldierTeam.size() ; i++) {
            soldierTeam.get(i).setTile(tempTileList.get(i));
            soldierTeam.get(i).getTile().setCurrSoldier(soldierTeam.get(i));
        }

    }

    public AnimationHandler getMenuIngameBtn() {
        return openMenuIngameBtn;
    }

    public boolean menuButtonWasPressed(float x, float y) {
        return isInside(getMenuIngameBtn().getDestRect() ,x ,y);
    }

    public AnimationHandler getResumeGameBtn() {
        return resumeGameBtn;
    }

    public AnimationHandler getBackToMenuBtn() {
        return backToMenuBtn;
    }

    public boolean resumeWasPressed(float x, float y) {
        return isInside(getResumeGameBtn().getDestRect(), x, y);
    }

    public boolean backToMenuWasPressed(float x, float y) {
        Log.d("MENU_DBG", "menuButtonWasPressed called with {"+x+","+y+"}");
        return isInside(getBackToMenuBtn().getDestRect(), x, y);
    }

    public SoldierType getNewPickedWeapon(float x, float y) {
        SoldierType newWeapon = null;
        if(isInside(newWeaponSword.getDestRect(), x, y)){
            newWeapon = SoldierType.SWORDMASTER;
        }
        else if(isInside(newWeaponRock.getDestRect(), x, y)){
            newWeapon = SoldierType.STONE;
        }
        else if(isInside(newWeaponPaper.getDestRect(), x, y)){
            newWeapon = SoldierType.PEPPER;
        }
        return newWeapon;
    }

    public AnimationHandler getNewWeaponSword() {
        return newWeaponSword;
    }

    public AnimationHandler getNewWeaponRock() {
        return newWeaponRock;
    }

    public AnimationHandler getNewWeaponPaper() {
        return newWeaponPaper;
    }

    public HashMap<String, AnimationHandler> getMatchAnimationsMap() {
        return matchAnimationsMap;
    }

    /**
     * EasterEgg time.
     */
    public void hax() {
        for(Soldier s : soldierTeamA){
            s.setSoldierBitmap(s.getSoldierBitmap() == s.getSoldierRevealedBitmap() ?
                  s.getSoldierNonHighlightedBitmap() : s.getSoldierRevealedBitmap());
        }
    }
}
