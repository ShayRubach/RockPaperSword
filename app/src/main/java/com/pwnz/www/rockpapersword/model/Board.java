package com.pwnz.www.rockpapersword.model;

import android.graphics.Rect;
import android.util.Pair;

import java.util.ArrayList;

public class Board {

    Tile[][] tiles;
    ArrayList<Soldier> soldierTeamA = new ArrayList<>();
    ArrayList<Soldier> soldierTeamB = new ArrayList<>();
    ArrayList<Pair<Tile, SoldierMovement>> pathArrows = new ArrayList<>();

    int cols, rows;
    int canvasW, canvasH;
    int tileW, tileH;
    int brightColor, darkColor;

    public static final int MAX_PATH_ARROWS = 4;

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
        initSoldiers(soldierTeamA, 0);
        initSoldiers(soldierTeamB, 4);
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
            }
        }
    }

    private void initSoldiers(ArrayList<Soldier> soldiersTeam, int SOLDIERS_START_ROW) {

        for (int i = 0, j = SOLDIERS_START_ROW, k = 0; i < soldiersTeam.size() ; i++, k++) {

            //todo: set the types of the soldiers here by game rules.
            //todo: 3 Stones, 3 Swordmasters, 3 Peppers, 1 random(between 3 regulars), 1 Shieldon, 1 Sir Lasso, 1 Ashes, 1 King

            soldiersTeam.get(i).setSoldierType(pickAvailableSoldierType());
            soldiersTeam.get(i).setSoldierAnimationSpriteByType();
            soldiersTeam.get(i).setVisible(true);
            soldiersTeam.get(i).setRectPosition(tiles[k % cols][j].getRect());
            tiles[k % cols][j].setOccupied(true);

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
            if(isInInside(soldier.getRectPosition(), x,y) == true)
                return soldier;
        }

        return null;
    }

    private boolean isInInside(Rect rectPosition, float x, float y) {
        if(rectPosition.left <= x && rectPosition.right >= x){
            if(rectPosition.top <= y && rectPosition.bottom >= y){
                return true;
            }
        }
        return false;
    }

    public void displaySoldierPath(Soldier focusedSoldier) {
        ArrayList<Pair<Tile, SoldierMovement>> surroundingTiles = new ArrayList<>();
        highlightPathArrows(surroundingTiles, focusedSoldier);
        focusedSoldier.highlight();
        highlightPath(surroundingTiles);

    }

    private void highlightPath(ArrayList<Pair<Tile, SoldierMovement>> surroundingTiles) {
        pathArrows = surroundingTiles;
    }



    private void highlightPathArrows(ArrayList<Pair<Tile, SoldierMovement>> surroundingTiles, Soldier focusedSoldier) {
        Tile tile = null;
        Integer[] xyPos = new Integer[2];
        rectPositionToTileIndex(focusedSoldier.getRectPosition(), xyPos);

        //is left neighbor exist and unoccupied?
        if(xyPos[0]-1 > -1 )
            addNeighbor(surroundingTiles, tile, xyPos[0]-1, xyPos[1], SoldierMovement.MOVE_LEFT );
        //is right neighbor exist and unoccupied?
        if(xyPos[0]+1 < cols )
            addNeighbor(surroundingTiles, tile, xyPos[0]+1, xyPos[1], SoldierMovement.MOVE_RIGHT );
        //is top neighbor exist and unoccupied?
        if(xyPos[1]-1 > -1 )
            addNeighbor(surroundingTiles, tile, xyPos[0], xyPos[1]-1, SoldierMovement.MOVE_UP );
        //is bottom neighbor exist and unoccupied?
        if(xyPos[1]+1 < rows )
            addNeighbor(surroundingTiles, tile, xyPos[0], xyPos[1]+1, SoldierMovement.MOVE_DOWN );
    }

    private void addNeighbor(ArrayList<Pair<Tile, SoldierMovement>> surroundingTiles, Tile tile, Integer x, Integer y, SoldierMovement move){
        tile = getTiles()[x][y];
        if(tile.isOccupied() == false){
            surroundingTiles.add(new Pair<>(tile, move));
        }
    }

    private void rectPositionToTileIndex(Rect rect, Integer[] pos) {
        for (int i = 0; i < cols ; i++) {
            for (int j = 0; j < rows  ; j++) {
                if(getTiles()[i][j].getRect() == rect){
                    pos[0] = i;
                    pos[1] = j;
                    return;
                }
            }
        }
    }


    public void getMoveDirection(Soldier focusedSoldier, float x, float y) {

    }

    public ArrayList<Pair<Tile, SoldierMovement>> getPathArrows() {
        return pathArrows;
    }

    public int getMaxPathArrows() {
        return MAX_PATH_ARROWS;
    }
}
