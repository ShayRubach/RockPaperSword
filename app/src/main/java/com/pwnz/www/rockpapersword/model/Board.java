package com.pwnz.www.rockpapersword.model;

import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Pair;

import com.pwnz.www.rockpapersword.R;
import com.pwnz.www.rockpapersword.controller.GameManager;

import java.util.ArrayList;

public class Board {

    Tile[][] tiles;
    ArrayList<Soldier> soldierTeamA = new ArrayList<>();
    ArrayList<Soldier> soldierTeamB = new ArrayList<>();
    ArrayList<Soldier> matchSoldierTeamA = new ArrayList<>();
    ArrayList<Soldier> matchSoldierTeamB = new ArrayList<>();

    ArrayList<Pair<Tile, SoldierMovement>> pathArrows = new ArrayList<>();

    int cols, rows;
    int canvasW, canvasH;
    int tileW, tileH;
    int brightColor, darkColor;
    public static final int TEAM_A = 0;
    public static final int TEAM_B = 1;
    public static final int SOLDIERS_TYPES_COUNT = 7;
    public static final int MAX_PATH_ARROWS = 4;
    private GameManager manager;

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

    /*
    Allocate a team of soldiers that will be displayed on the match scenes. According to which team is asked to be allocated,
    the sprites will be chosen.
     */
    private void initSoldierMatchTeam(int team, ArrayList<Soldier> matchSoldierTeam, int soldiersTypesCount) {

        if(matchSoldierTeam == null)
            return;

        float tileWidthOffset = (float) (tileW * 2.5);
        float tileHeightOffset = (float) (tileH * 1.5);

        Tile tile = new Tile();
        Rect rect = new Rect();
        if(team == TEAM_A){
            rect.left   = (int)((canvasW/2) - tileWidthOffset);
            rect.top    = (int)((canvasH/2) - tileHeightOffset);
            rect.right  = canvasW/2;
            rect.bottom = (int)((canvasH/2) + tileHeightOffset);
        }
        else{
            rect.left   =  canvasW/2;
            rect.top    = (int)((canvasH/2) - tileHeightOffset);
            rect.right  = (int)((canvasW/2) + tileWidthOffset);
            rect.bottom = (int)((canvasH/2) + tileHeightOffset);
        }

        tile.setRect(rect);

        for (int i = 0; i < soldiersTypesCount ; i++) {
            Soldier soldier = new Soldier();
            soldier.setTile(tile);
            soldier.setVisible(true);
            soldier.setTeam(team);
            soldier.setSoldierType(Soldier.pickUniqueSoldierType(i));

            //soldier.setAnimationSprite(getMatchSpriteAnimation(soldier.getSoldierType(), soldier.getTeam()));
            soldier.spriteId = getMatchSpriteAnimation(soldier.getSoldierType(), soldier.getTeam());

            soldier.spriteSheet = BitmapFactory.decodeResource(manager.getAppResources(), soldier.spriteId);
            soldier.spriteSheetH = soldier.spriteSheet.getHeight();
            soldier.spriteSheetW = soldier.spriteSheet.getWidth();
            soldier.numberOfSpriteFrames = 4;
            soldier.spriteFrameSrcH = soldier.spriteSheetH / 2;   //2 rows
            soldier.spriteFrameSrcW = soldier.spriteSheetW / 5;   //5 columns

            soldier.sourceRect = new Rect();
            soldier.destRect = soldier.getTile().getRect();
            soldier.resetToFirstFrame();

            matchSoldierTeam.add(soldier);
        }
    }

    private int getMatchSpriteAnimation(SoldierType soldierType, int team) {

        if(soldierType == null){
            System.out.println("SOLDIER TYPE IS NULL");
            return -1;
        }

        switch (soldierType){
            case ASHES:
                if(team == TEAM_A)
                    return R.drawable.samurai_hit_sprite;
                else
                    return R.drawable.shieldon;
            case SWORDMASTER:
                if(team == TEAM_A)
                    return R.drawable.samurai_hit_sprite;
                else
                    return R.drawable.shieldon;
            case SHIELDON:
                if(team == TEAM_A)
                    return R.drawable.samurai_hit_sprite;
                else
                    return R.drawable.shieldon;
            case PEPPER:
                if(team == TEAM_A)
                    return R.drawable.samurai_hit_sprite;
                else
                    return R.drawable.shieldon;
            case STONE:
                if(team == TEAM_A)
                    return R.drawable.samurai_hit_sprite;
                else
                    return R.drawable.shieldon;
            case KING:
                if(team == TEAM_A)
                    return R.drawable.samurai_hit_sprite;
                else
                    return R.drawable.shieldon;
            case LASSO:
                if(team == TEAM_A)
                    return R.drawable.samurai_hit_sprite;
                else
                    return R.drawable.shieldon;
        }


        return R.drawable.shieldon;
    }

    public Soldier getFightingSoldier(SoldierType soldierType, int team) {

        if(soldierType == null){
            System.out.println("SOLDIER TYPE IS NULL");
            return null;
        }

        ArrayList<Soldier> matchSoldierTeam = (team == TEAM_A) ? matchSoldierTeamA : matchSoldierTeamB;

        Soldier soldierInMatch = null;

        for(Soldier soldier : matchSoldierTeam){
            System.out.println("++++++++++++++++++++++++++++++++");
            System.out.println("soldier.getSoldierType() = " + soldier.getSoldierType());
            System.out.println("soldierType = " + soldierType);
            if(soldier.getSoldierType() == soldierType){
                System.out.println("SOLDIER_TYPE_IS = " + soldier.getSoldierType()) ;
                soldierInMatch = soldier;
                System.out.println("SOLDIER_IN_MATCH = " + soldierInMatch) ;
                break;
            }
        }

        return soldierInMatch;
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

    public ArrayList<Soldier> getSoldierTeamA() {
        return soldierTeamA;
    }

    public ArrayList<Soldier> getSoldierTeamB() {
        return soldierTeamB;
    }

    public int getBrightColor() {
        return brightColor;
    }

    public void setBrightColor(int brightColor) {
        this.brightColor = brightColor;
    }

    public int getDarkColor() {
        return darkColor;
    }

    public void setDarkColor(int darkColor) {
        this.darkColor = darkColor;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
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


    //boardPadding is the gap of top and bottom screen where the board shouldn't be drawn.
    //H and W Divisors are actually rows and cols count. we treat our screen as a grid.
    public void initBoard(int boardPaddingFactor, int hDivisor, int wDivisor){
        tileW = canvasW / wDivisor;
        tileH = canvasH / hDivisor;


        //this will give us a clean pad from the top of the screen which the board will not be drawn at
        int boardPadding = boardPaddingFactor * tileH;

        initTiles(boardPadding, tileW, tileH);
        initSoldiers(soldierTeamA, TEAM_A, 0);
        initSoldiers(soldierTeamB, TEAM_B, 4);
        initSoldierMatchTeam(TEAM_A, matchSoldierTeamA, SOLDIERS_TYPES_COUNT);
        initSoldierMatchTeam(TEAM_B, matchSoldierTeamB, SOLDIERS_TYPES_COUNT);
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

    private void initSoldiers(ArrayList<Soldier> soldiersTeam, int team, int SOLDIERS_START_ROW) {

        for (int i = 0, j = SOLDIERS_START_ROW, k = 0; i < soldiersTeam.size() ; i++, k++) {

            //todo: set the types of the soldiers here by game rules.
            //todo: 3 Stones, 3 Swordmasters, 3 Peppers, 1 random(between 3 regulars), 1 Shieldon, 1 Sir Lasso, 1 Ashes, 1 King

            soldiersTeam.get(i).setTeam(team);
            soldiersTeam.get(i).setSoldierType(pickAvailableSoldierType());
            soldiersTeam.get(i).setSoldierAnimationSpriteByType();
            soldiersTeam.get(i).setSoldierBitmap(BitmapFactory.decodeResource(manager.getAppResources(), soldiersTeam.get(i).getAnimationSprite()));
            soldiersTeam.get(i).setVisible(true);
            tiles[k % cols][j].setOccupied(true);
            tiles[k % cols][j].setCurrSoldier(soldiersTeam.get(i));
            soldiersTeam.get(i).setTile(tiles[k % cols][j]);

            //stop over the the next tile row
            if(i == (soldiersTeam.size()-1) / 2)
                ++j;
        }
    }

    private SoldierType pickAvailableSoldierType() {
        return Soldier.pickAvailableSoldierType();
    }


    public Soldier getClickedSoldier(float x, float y) {
        //find clicked soldier on self team
        for (Soldier soldier : soldierTeamB){
            if(isInside(soldier.getTile().getRect(), x,y) == true)
                return soldier;
        }

        return null;
    }

    private boolean isInside(Rect rectPosition, float x, float y) {
        if(rectPosition.left <= x && rectPosition.right >= x){
            if(rectPosition.top <= y && rectPosition.bottom >= y){
                return true;
            }
        }
        return false;
    }

    public void displaySoldierPath(Soldier focusedSoldier) {

        highlightPathArrows(focusedSoldier);
        focusedSoldier.highlight();
    }


    private void highlightPathArrows(Soldier focusedSoldier) {
        Tile tile = null;
        Integer[] xyPos = new Integer[2];
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

    public ArrayList<Pair<Tile, SoldierMovement>> getPathArrows() {
        return pathArrows;
    }

    public ArrayList<Soldier> getMatchSoldierTeamA() {
        return matchSoldierTeamA;
    }

    public ArrayList<Soldier> getMatchSoldierTeamB() {
        return matchSoldierTeamB;
    }

    public static int getSoldiersTypesCount() {
        return SOLDIERS_TYPES_COUNT;
    }

    public int getMaxPathArrows() {
        return MAX_PATH_ARROWS;
    }


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

    public Soldier getFirstSurroundingOpponent(Soldier initiator) {
        Integer[] xyPos = new Integer[2];
        getTileIndex(initiator.getTile(), xyPos);
        int newX, newY;


        //potential opponent to the left
        if(xyPos[0]-1 > -1){
            newX = xyPos[0]-1;
            newY = xyPos[1];
            if(isValidOpponent(newX, newY, initiator)) {
                return getTiles()[newX][newY].getCurrSoldier();
            }
        }
        //potential opponent to the right
        if(xyPos[0]+1 < cols ) {
            newX = xyPos[0]+1;
            newY = xyPos[1];
            if (isValidOpponent(newX, newY, initiator)){
                return getTiles()[newX][newY].getCurrSoldier();
            }

        }
        //potential opponent to the top
        if(xyPos[1]-1 > -1 ) {
            newX = xyPos[0];
            newY = xyPos[1]-1;
            if (isValidOpponent(newX, newY, initiator)) {
                return getTiles()[newX][newY].getCurrSoldier();
            }
        }
        //potential opponent to the bottom
        if(xyPos[1]+1 < rows ) {
            newX = xyPos[0];
            newY = xyPos[1]+1;
            if (isValidOpponent(newX, newY, initiator)) {
                return getTiles()[newX][newY].getCurrSoldier();
            }
        }

        return null;
    }

    private boolean isValidOpponent(int newX, int newY, Soldier initiator) {


        return getTiles()[newX][newY].isOccupied() &&
               getTiles()[newX][newY].getCurrSoldier().getTeam() != initiator.getTeam();



    }

    public void setManager(GameManager manager) {
        this.manager = manager;
    }
}
