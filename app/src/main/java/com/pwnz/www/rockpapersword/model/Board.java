package com.pwnz.www.rockpapersword.model;

import android.graphics.Rect;

import java.util.ArrayList;

public class Board {

    Tile[][] tiles;
    ArrayList<Soldier> soldierTeamA;
    ArrayList<Soldier> soldierTeamB;
    int cols, rows;
    int canvasW, canvasH;
    int brightColor, darkColor;

    public Board(int cols, int rows, int canvasW, int canvasH, int brightColor, int darkColor) {
        this.cols = cols;
        this.rows = rows;
        this.canvasW = canvasW;
        this.canvasH = canvasH;
        allocateTiles();
        soldierTeamA = new ArrayList<>(cols*2);
        soldierTeamB = new ArrayList<>(cols*2);
        setBrightColor(brightColor);
        setDarkColor(darkColor);
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
        int tileW = canvasW / wDivisor;
        int tileH = canvasH / hDivisor;


        //this will give us a clean pad from the top of the screen which the board will not be drawn at
        int boardPadding = boardPaddingFactor * tileH;

        initTiles(boardPadding, tileW, tileH);

        //initSoldiers(soldierTeamA, 0);
        //initSoldiers(soldierTeamB, 4);
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
        for (int i = 0, k = 0, j = SOLDIERS_START_ROW; i < soldiersTeam.size() ; i++, k++) {
            soldiersTeam.get(i).setVisible(true);
            soldiersTeam.get(i).setRectPosition(tiles[j][k % cols].getRect());
            if(i == (soldiersTeam.size()-1) / 2){
                ++j;
            }
        }
    }
}
