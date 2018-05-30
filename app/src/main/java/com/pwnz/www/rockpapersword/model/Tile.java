package com.pwnz.www.rockpapersword.model;

import android.graphics.Rect;

public class Tile {

    private int topLeftX;
    private int topLeftY;
    private int btmRightX;
    private int btmRightY;


    public Tile(){}

    public Tile(int topLeftX, int topLeftY, int btmRightX, int btmRightY) {
        this.topLeftX = topLeftX;
        this.topLeftY = topLeftY;
        this.btmRightX = btmRightX;
        this.btmRightY = btmRightY;

    }


    public int getTopLeftX() {
        return topLeftX;
    }

    public int getTopLeftY() {
        return topLeftY;
    }

    public int getBtmRightX() {
        return btmRightX;
    }

    public int getBtmRightY() {
        return btmRightY;
    }
}
